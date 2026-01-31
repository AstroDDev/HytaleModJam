package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.npc.NPCPlugin;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameLogic {
	public final GameConfig config;
	public final World world;
	private final ScheduledFuture<?> executor;
	private boolean started = false;
	private int units = 50;
	private int collectedgears = 0;

	public GameLogic(World world, GameConfig config) {
		this.config = config;
		this.world = world;
		this.executor = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> world.execute(this::tick), 500, 500, TimeUnit.MILLISECONDS);
	}

	public void tick() {
		if(started) {
			if(units > 0) {
				for(EnnemySpawnPoint point : this.config.getPoints()) {
					NPCPlugin.get().spawnNPC(world.getEntityStore().getStore(), "Skeleton", null, point.getPos(), Vector3f.ZERO.clone());
					units--;
				}
			}
			if(config.getPortalScrap() - collectedgears > 0) {
				getPlayers().forEach(pl -> {
					var required = config.getPortalScrap() - collectedgears;
					var trans = pl.getInventory().getCombinedEverything().removeItemStack(new ItemStack("RustyGear", required), false, true);

					var eaten = required;
					if(trans.getRemainder() != null)
						eaten -= trans.getRemainder().getQuantity();

					if(eaten > 0) {
						collectedgears += eaten;
						world.sendMessage(Message.raw(collectedgears + "/" + config.getPortalScrap() + " gears collected!"));
					}


				});
			} else {
				started = false;
				//collectedgears = 0;
				world.sendMessage(Message.raw("WINNED!"));
			}
		}
	}

	public List<Player> getPlayers() {
		return world.getPlayerRefs().stream().map((ref) -> ref.getReference().getStore().getComponent(ref.getReference(), Player.getComponentType())).toList();

	}

	public void start() {
		this.units = 50;
		this.collectedgears = 0;
		this.started = true;
	}

	public void cleanup() {
		executor.cancel(true);
	}
}
