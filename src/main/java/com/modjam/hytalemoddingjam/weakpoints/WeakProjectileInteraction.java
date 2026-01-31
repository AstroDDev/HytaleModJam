//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.modjam.hytalemoddingjam.weakpoints;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticData;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticDataProvider;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeakProjectileInteraction extends SimpleInteraction {
    public static final BuilderCodec<WeakProjectileInteraction> CODEC;
    private static final StringTag TAG_NEXT;
    private static final StringTag TAG_FAILED;
    private static final int FAILED_LABEL_INDEX = 0;
    @Nullable
    protected String next;
    @Nullable
    protected String failed;

    protected WeakProjectileInteraction(){
    }

    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    public boolean needsRemoteSync() {
        return true;
    }

    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        World world = commandBuffer.getExternalData().getWorld();

        Ref<EntityStore> entityRef = context.getTargetEntity();
        Ref<EntityStore> projectileRef = context.getEntity();
        Store<EntityStore> store = commandBuffer.getExternalData().getStore();

        WeakPointComponent weakPoint = store.getComponent(entityRef, WeakPointComponent.getComponentType());

        if (weakPoint != null) {
            if (weakPoint.isHit(entityRef, projectileRef, store)) {
                context.getState().state = InteractionState.Finished;
            } else {
                context.getState().state = InteractionState.Failed;
                context.jump(context.getLabel(0));
            }
        } else {
            context.getState().state = InteractionState.Finished;
        }


        //super.tick0(firstRun, time, type, context, cooldownHandler);
    }

    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        World world = commandBuffer.getExternalData().getWorld();

            Ref<EntityStore> entityRef = context.getTargetEntity();
            Ref<EntityStore> projectileRef = context.getEntity();
            Store<EntityStore> store = commandBuffer.getExternalData().getStore();

            WeakPointComponent weakPoint = store.getComponent(entityRef, WeakPointComponent.getComponentType());

            if (weakPoint != null) {
                if (weakPoint.isHit(entityRef, projectileRef, store)) {
                    context.getState().state = InteractionState.Finished;
                } else {
                    context.getState().state = InteractionState.Failed;
                    context.jump(context.getLabel(0));
                }
            } else {
                context.getState().state = InteractionState.Finished;
            }

        //super.simulateTick0(firstRun, time, type, context, cooldownHandler);
    }

    public void compile(@Nonnull OperationsBuilder builder) {
        if (this.next == null && this.failed == null) {
            builder.addOperation(this);
        } else {
            Label failedLabel = builder.createUnresolvedLabel();
            Label endLabel = builder.createUnresolvedLabel();
            builder.addOperation(this, new Label[]{failedLabel});
            if (this.next != null) {
                Interaction nextInteraction = Interaction.getInteractionOrUnknown(this.next);
                nextInteraction.compile(builder);
            }

            if (this.failed != null) {
                builder.jump(endLabel);
            }

            builder.resolveLabel(failedLabel);
            if (this.failed != null) {
                Interaction failedInteraction = Interaction.getInteractionOrUnknown(this.failed);
                failedInteraction.compile(builder);
            }

            builder.resolveLabel(endLabel);
        }
    }

    public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
        if (this.next != null && InteractionManager.walkInteraction(collector, context, TAG_NEXT, this.next)) {
            return true;
        } else {
            return this.failed != null && InteractionManager.walkInteraction(collector, context, TAG_FAILED, this.failed);
        }
    }

    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.SimpleInteraction();
    }

    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.SimpleInteraction p = (com.hypixel.hytale.protocol.SimpleInteraction)packet;
        p.next = Interaction.getInteractionIdOrUnknown(this.next);
        p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
    }

    public String toString() {
        String var10000 = this.next;
        return "SimpleInteraction{next='" + var10000 + "'failed='" + this.failed + "'} " + super.toString();
    }

    static {
        CODEC = ((BuilderCodec.Builder<WeakProjectileInteraction>)((BuilderCodec.Builder<WeakProjectileInteraction>)((BuilderCodec.Builder<WeakProjectileInteraction>)BuilderCodec.builder(WeakProjectileInteraction.class, WeakProjectileInteraction::new, Interaction.ABSTRACT_CODEC).documentation("A interaction that does nothing other than base interaction features. Can be used for simple delays or triggering animations in between other interactions.")).appendInherited(new KeyedCodec("Next", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.next = s, (interaction) -> interaction.next, (interaction, parent) -> interaction.next = parent.next).documentation("The interactions to run when this interaction succeeds.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Failed", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.failed = s, (interaction) -> interaction.failed, (interaction, parent) -> interaction.failed = parent.failed).documentation("The interactions to run when this interaction fails.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).build();
        TAG_NEXT = StringTag.of("Next");
        TAG_FAILED = StringTag.of("Failed");
    }
}
