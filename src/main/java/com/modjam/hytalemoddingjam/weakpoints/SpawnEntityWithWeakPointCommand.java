package com.modjam.hytalemoddingjam.weakpoints;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import it.unimi.dsi.fastutil.Pair;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.swing.text.html.parser.Entity;

public class SpawnEntityWithWeakPointCommand extends AbstractPlayerCommand {
    public SpawnEntityWithWeakPointCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());

        world.execute(() -> {
            Pair<Ref<EntityStore>, INonPlayerCharacter> result = NPCPlugin.get().spawnNPC(store, "Armadillo", "None", transform.getPosition(), transform.getRotation());

            WeakPointComponent weakPoint = store.ensureAndGetComponent(result.first(), WeakPointComponent.getComponentType());

            weakPoint.weak = true;
            weakPoint.minX = -5;
            weakPoint.minY = 0;
            weakPoint.minZ = 0;
            weakPoint.maxX = 10;
            weakPoint.maxY = 10;
            weakPoint.maxZ = 10;
        });
    }
}
