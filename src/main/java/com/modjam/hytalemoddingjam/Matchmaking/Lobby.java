package com.modjam.hytalemoddingjam.Matchmaking;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final String ID;
    private final int MAX_PLAYERS = 2;
    private final List<Player> players;
    private boolean isStarting = false;
    private long countdownTimer = 200; // in ticks (20 ticks = 1 second)
    private boolean isStarted = false;
    private final long creationTime;
    
    public Lobby(String id) {
        this.ID = id;
        players = new ArrayList<>();
        this.creationTime = System.currentTimeMillis() / 1000L;
    }
    
    public String getInfo() {
        String info = "Lobby ID: " + ID + "\n";
        info += "Players: " + players.size() + "/" + MAX_PLAYERS + "\n";
        info += "Player List: \n";
        for(Player player : players) {
            info += "- " + player.getDisplayName() + "\n";
        }
        info += "Status: " + (isStarted ? "Started" : (isStarting ? "Starting" : "Waiting")) + "\n";
        return info;
    }
    
    public void tick() {
        if(isStarting && !isStarted) {
            countdownTimer--;
            if(countdownTimer % 20 == 0 && countdownTimer > 0) {
                broadcastMessage("[Matchmaking] Game starting in " + (countdownTimer / 20) + " seconds...");
            }
            if(countdownTimer <= 0) {
                startGame();
                isStarted = true;
                isStarting = false;
                broadcastMessage("[Matchmaking] The game has started!");
            }
        }
    }
    
    public String getID() {
        return ID;
    }
    
    public boolean hasPlayers() {
        return !players.isEmpty();
    }
    
    public boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
  public int getMaxPlayers() {
        return MAX_PLAYERS;
    }
    
    public boolean isStarted() {
        return isStarted;
    }
    
    public long getCreationTime() {
        return creationTime;
    }
    
    public void addPlayer(Player player) {
        players.add(player);
        broadcastMessage("[Matchmaking] A new player has joined the lobby. Current players: " + players.size() + "/" + MAX_PLAYERS);
    }
    
    public void removePlayer(Player player) {
        players.remove(player);
        broadcastMessage("[Matchmaking] A player has left the lobby. Current players: " + players.size() + "/" + MAX_PLAYERS);
    }
    
    public void removeAllPlayers() {
        players.clear();
    }
    
    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }
    
    public boolean startGame() {
        isStarting = true;
        return true;
    }
    
    private void broadcastMessage(String message) {
        for(Player player : players) {
            var ref = player.getReference();
            PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
            if(playerRef != null) {
                playerRef.sendMessage(com.hypixel.hytale.server.core.Message.raw(message));
            }
        }
    }
}
