package com.modjam.hytalemoddingjam.Matchmaking.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.Matchmaking.Lobby;
import com.modjam.hytalemoddingjam.Matchmaking.MatchmakingSystem;
import com.hypixel.hytale.server.core.Message;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Objects;

public class StartLobbyCommand extends AbstractPlayerCommand {
    public StartLobbyCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());

        if(player == null) {
            return;
        }

        Lobby currentLobby = MatchmakingSystem.getInstance().getPlayerLobby(player);
        if(!Objects.equals(currentLobby.getID(), player.getDisplayName())) {
            playerRef.sendMessage(Message.raw("[Matchmaking] Only the lobby creator can start the game."));
            return;
        }
        
        if(currentLobby != null) {
            if(currentLobby.startGame()) {
                playerRef.sendMessage(Message.raw("[Matchmaking] Game Manually Started!"));
            } else {
                playerRef.sendMessage(Message.raw("[Matchmaking] Failed to start the game. Make sure all conditions are met."));
            }
        } else {
            playerRef.sendMessage(Message.raw("[Matchmaking] You are not in a lobby."));
        }
    }
}
