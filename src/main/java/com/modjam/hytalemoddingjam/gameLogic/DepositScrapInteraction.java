package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainerUtil;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;

public class DepositScrapInteraction extends SimpleInstantInteraction {
    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        World world = commandBuffer.getExternalData().getWorld();
        Store<EntityStore> store = commandBuffer.getStore();
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = store.getComponent(ref, Player.getComponentType());
        Inventory inventory = player.getInventory();
        ItemContainer hotbar = inventory.getHotbar();

        for (short i = 0; i < hotbar.getCapacity(); i++){
            ItemStack item = hotbar.getItemStack(i);
            if (item == null) continue;
            String itemID = item.getItemId();
            if (itemID.equals("RustyGear")){
                hotbar.removeItemStack(item);

                player.sendMessage(Message.raw("You deposited a Rusty Gear").color(Color.GREEN));

                if (GameInstances.has(world)) GameInstances.get(world).collectGear();

                return;
            }
        }

        player.sendMessage(Message.raw("You do not have any scrap").color(Color.RED));
    }

    public static final BuilderCodec<DepositScrapInteraction> CODEC = BuilderCodec.builder(DepositScrapInteraction.class, DepositScrapInteraction::new).build();
}
