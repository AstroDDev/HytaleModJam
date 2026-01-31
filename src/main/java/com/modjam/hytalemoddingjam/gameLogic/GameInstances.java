package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;

import java.util.concurrent.ConcurrentHashMap;

public class GameInstances {

	private static ConcurrentHashMap<World, GameLogic> logics = new ConcurrentHashMap<>();

	public static void init(EventRegistry registry) {
		registry.registerGlobal(StartWorldEvent.class, (ev) -> {
			ev.getWorld().execute(() -> {
				GameConfig config = ev.getWorld().getGameplayConfig().getPluginConfig().get(GameConfig.class);
				if(config != null) {
					create(ev.getWorld(), config).start();
				}
			});

		});
		registry.registerGlobal(RemoveWorldEvent.class, (ev) -> {
			var rem = logics.remove(ev.getWorld());
			if(rem != null)
				rem.cleanup();

		});
	}

	public static GameLogic get(World world) {
		return logics.get(world);
	}

	public static GameLogic create(World world, GameConfig config) {
		GameLogic logic;
		logics.put(world, logic = new GameLogic(world, config));
		return logic;
	}
}
