package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent;
import com.hypixel.hytale.server.core.event.events.entity.EntityRemoveEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemSystem;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerItemEntityPickupSystem;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.modjam.hytalemoddingjam.MainPlugin;
import jdk.jfr.consumer.EventStream;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Collection;

import static com.hypixel.hytale.server.core.entity.EntityUtils.getEntity;

public class GameEvents {
    public static void readyPlayer(AddPlayerToWorldEvent event){
        World world = event.getWorld();
        Player player = event.getHolder().getComponent(Player.getComponentType());
        Inventory inventory = new Inventory();
        if (world.getName().equals("MannCoWorld")) {
            inventory.getHotbar().setItemStackForSlot((short) 0, new ItemStack("MannCoRifle"));
        }
        player.setInventory(inventory);
        player.sendInventory();
    }

    public static void entityRemoved(EntityRemoveEvent event){
        Ref<EntityStore> ref = event.getEntity().getReference();
        Store<EntityStore> store = ref.getStore();
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        //Doesn't work
        ParticleUtil.spawnParticleEffect("Explosion_Big", transform.getPosition(), store);
    }
}
