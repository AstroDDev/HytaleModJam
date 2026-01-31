package com.modjam.hytalemoddingjam.Matchmaking;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.Message;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nullable;
import java.util.*;

public class MatchmakingSystem extends EntityTickingSystem<EntityStore> {
    private static MatchmakingSystem INSTANCE;
    
    private final Queue<Player> playersInQueue = new LinkedList<>();
    private final List<Lobby> activeLobbies = new ArrayList<>();
    
    private final int LOBBY_START_TIMEOUT = 30; // seconds
    private long lastLateTick = System.currentTimeMillis();
    
    public MatchmakingSystem(){
        INSTANCE = this;
    }
    
    public static MatchmakingSystem getInstance(){
        return INSTANCE;
    }
    
    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk archetypeChunk, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        
        handleLobbies();
        
        if(System.currentTimeMillis() - lastLateTick >= 5000L) { // 5 second interval
            lateTick();
            lastLateTick = System.currentTimeMillis();
        }
    }
    
    private void lateTick(){
        handleLobbyFilling();
        handleUnstartedLobbies();
    }
    
    private void handleLobbies() {
        for (Lobby lobby : activeLobbies) {
            lobby.tick();
        }
    }

    private void handleLobbyFilling() {
        while (!playersInQueue.isEmpty()) {
            Player player = playersInQueue.poll();
            if (player == null) continue;

            boolean addedToLobby = false;

            for (Lobby lobby : activeLobbies) {
                if (!lobby.isStarted() && !lobby.isFull()) {
                    lobby.addPlayer(player);
                    addedToLobby = true;
                    break; // Stop after adding to one lobby
                }
            }

            if (!addedToLobby) {
                // No available lobby, create a new one
                createLobby(player);
            }
        }
    }

    private void handleUnstartedLobbies() {
        for (Lobby lobby : activeLobbies) {
            if (!lobby.isStarted()) {
                long currentTime = System.currentTimeMillis() / 1000L;
                if (lobby.getCreationTime() + LOBBY_START_TIMEOUT <= currentTime) {
                    if (!lobby.hasPlayers()) {
                        disbandLobby(lobby.getID());
                    } else {
                        lobby.startGame();
                    }
                }
            }
        }
    }
    
    public boolean createLobby(@NonNullDecl Player player) {
        try {
            if(isPlayerInAnyLobby(player)) {
                return false; // Player is already in a lobby
            }
            Lobby newLobby = new Lobby(player.getDisplayName());
            broadcastMessageToPlayer(player, "[Matchmaking] A new lobby has been created with ID: " + newLobby.getID());
            activeLobbies.add(newLobby);
            newLobby.addPlayer(player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean joinLobby(@NonNullDecl Player player, @NonNullDecl String lobbyIdStr) {
        try {
            UUID lobbyId = UUID.fromString(lobbyIdStr);
            for(Lobby lobby : activeLobbies) {
                if(Objects.equals(lobby.getID(), lobbyId.toString())) {
                    if(isPlayerInAnyLobby(player)) {
                        return false; // Player is already in a lobby
                    }
                    if(lobby.isFull() || lobby.isStarted()) {
                        return false; // Lobby is full or already started
                    }
                    lobby.addPlayer(player);
                    return true;
                }
            }
            return false; // Lobby not found
        } catch (IllegalArgumentException e) {
            return false; // Invalid UUID format
        }
    }
    
    public boolean leaveLobby(@NonNullDecl Player player) {
        for(Lobby lobby : activeLobbies) {
            if(lobby.containsPlayer(player)) {
                if(Objects.equals(lobby.getID(), player.getDisplayName())) {
                    disbandLobby(lobby.getID());
                } else {
                    lobby.removePlayer(player);
                }
                return true;
            }
        }
        return false;
    }

    private void disbandLobby(String lobbyID) {
        Iterator<Lobby> iterator = activeLobbies.iterator();
        while(iterator.hasNext()) {
            Lobby lobby = iterator.next();
            if(Objects.equals(lobby.getID(), lobbyID)) {
                broadcastMessageToLobby(lobby, "[Matchmaking] The lobby is being disbanded.");
                lobby.removeAllPlayers();
                iterator.remove();
            }
        }
    }

    public boolean queuePlayer(@NonNullDecl Player player) {
        try {
            if(playersInQueue.contains(player)) {
                return false; // Player is already in the queue
            }
            if(isPlayerInAnyLobby(player)) {
                return false; // Player is already in a lobby
            }
            playersInQueue.add(player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean dequeuePlayer(@NonNullDecl Player player) {
        try {
            if(!playersInQueue.contains(player)) {
                return false; // Player is not in the queue
            }
            playersInQueue.remove(player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void broadcastMessageToLobby(Lobby lobby, String message) {
        for(Player player : lobby.getPlayers()) {
            broadcastMessageToPlayer(player, message);
        }
    }
    
    private void broadcastMessageToPlayer(Player player, String message) {
        PlayerRef playerRef = player.getReference().getStore().getComponent(player.getReference(), PlayerRef.getComponentType());
        if(playerRef != null) {
            playerRef.sendMessage(Message.raw(message));
        }
    }
    
    private boolean isPlayerInAnyLobby(@NonNullDecl Player player) {
        for(Lobby lobby : activeLobbies) {
            if(lobby.containsPlayer(player)) {
                return true;
            }
        }
        return false;
    }
    
    public Lobby getPlayerLobby(@NonNullDecl Player player) {
        for(Lobby lobby : activeLobbies) {
            if(lobby.containsPlayer(player)) {
                return lobby;
            }
        }
        return null;
    }
    
    public List<Lobby> getActiveLobbies() {
        return activeLobbies;
    }

    @Nullable
    @Override
    public Query getQuery() {
        return Player.getComponentType();
    }
}
