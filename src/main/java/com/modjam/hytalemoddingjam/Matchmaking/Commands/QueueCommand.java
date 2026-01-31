package com.modjam.hytalemoddingjam.Matchmaking.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.Matchmaking.MatchmakingSystem;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import com.hypixel.hytale.server.core.Message;

public class QueueCommand extends AbstractPlayerCommand {
    public QueueCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        
        if(player == null) {
            return;
        }

        if(MatchmakingSystem.getInstance().queuePlayer(player)) {
            playerRef.sendMessage(Message.raw("[Matchmaking] You have been added to the matchmaking queue."));
        } else {
            playerRef.sendMessage(Message.raw("[Matchmaking] Failed to add you to the matchmaking queue."));
        }
    }
}
