package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.MainPlugin;
import com.modjam.hytalemoddingjam.hud.MannCoHudSystem;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GameInstances {

	private static final ConcurrentHashMap<String, GameLogic> logics = new ConcurrentHashMap<>();

	public static void onRemoveWorldEvent(RemoveWorldEvent ev) {
		var removedWorld = logics.remove(ev.getWorld().getName());
		if (removedWorld != null) {
			removedWorld.cleanup();
		}
	}

	public static void onStartWorldEvent(StartWorldEvent ev) {
        createGameInWorld(ev.getWorld());
	}

	public static void createInstance(@Nonnull Ref<EntityStore> ref, @Nonnull World world) {
		var trans= ref.getStore().getComponent(ref, TransformComponent.getComponentType());

		// TODO probably change to spawn location of world
		Transform returnLocation = trans.getTransform();

		CompletableFuture<World> instanceWorldFuture = InstancesPlugin.get().spawnInstance("MannCoWorld", "MannCoWorld", world, returnLocation);
		InstancesPlugin.teleportPlayerToLoadingInstance(ref, ref.getStore(), instanceWorldFuture, null);
	}

	public static void createGameInWorld(World world) {
		if (world.getName().equals("default")) return;

		if (logics.containsKey(world.getName())) {
			MainPlugin.getInstance().getLogger().atSevere().log("GameLogic already exists for world " + world.getName());
			return;
		}

		world.execute(() -> {
			GameConfig config = world.getGameplayConfig().getPluginConfig().get(GameConfig.class);
            if (config == null) {
                MainPlugin.getInstance().getLogger().atSevere().log("No GameConfig found for world " + world.getName());
				return;
            }

            GameLogic logic = new GameLogic(world, config);
            logics.put(world.getName(), logic);
            logic.start();
        });
	}
	public static GameLogic getAny() {
		var k=logics.keySet().stream().findFirst();
		return k.map(logics::get).orElse(null);
	}

	public static GameLogic get(World world) {
		return logics.get(world.getName());
	}

	public static boolean has(World world) { return logics.containsKey(world.getName()); }
}
