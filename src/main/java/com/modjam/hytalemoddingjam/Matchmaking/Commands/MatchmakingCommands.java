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

public class MatchmakingCommands extends AbstractPlayerCommand {

    public MatchmakingCommands() {
        super("matchmaking", "Base command for matchmaking system.");
        this.addSubCommand(new QueueCommand("join", "Add yourself to the matchmaking queue."));
        this.addSubCommand(new DequeueCommand("leave", "Remove yourself from the matchmaking queue."));
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world) {
        playerRef.sendMessage(Message.raw("[Matchmaking] Use /matchmaking <join|leave> to interact with the matchmaking system."));
    }
}
