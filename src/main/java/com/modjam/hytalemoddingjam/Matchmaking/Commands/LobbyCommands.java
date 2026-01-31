package com.modjam.hytalemoddingjam.Matchmaking.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class LobbyCommands extends AbstractPlayerCommand {

    public LobbyCommands() {
        super("lobby", "Base command for lobbies.");
        this.addSubCommand(new CreateLobbyCommand("create", "Create a new lobby."));
        this.addSubCommand(new JoinLobbyCommand("join", "Add yourself to the matchmaking queue."));
        this.addSubCommand(new LeaveLobbyCommand("leave", "Leave the current lobby you are in."));
        this.addSubCommand(new StartLobbyCommand("start", "Start the lobby if you are the host."));
        this.addSubCommand(new ViewLobbyCommand("view", "View the current lobby you are in."));
        this.addSubCommand(new ListLobbiesCommand("list", "List all available lobbies."));  
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world) {
        playerRef.sendMessage(Message.raw("[Lobby] Use /lobby <join|leave> to interact with the lobbies.."));
    }
}
