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
import com.modjam.hytalemoddingjam.Matchmaking.MatchmakingSystem;
import com.hypixel.hytale.server.core.Message;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class JoinLobbyCommand extends AbstractPlayerCommand {
    private final RequiredArg<String> lobbyId;
    public JoinLobbyCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);
        this.lobbyId = this.withRequiredArg("lobbyId", "The ID of the lobby to join.", ArgTypes.STRING);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());

        if(player == null) {
            return;
        }

        if(MatchmakingSystem.getInstance().joinLobby(player, lobbyId.get(commandContext))) {
            playerRef.sendMessage(Message.raw("[Matchmaking] You have joined the lobby: " + lobbyId.get(commandContext)));
        } else {
            playerRef.sendMessage(Message.raw("[Matchmaking] Failed to join the lobby: " + lobbyId.get(commandContext)));
        }
    }
}
