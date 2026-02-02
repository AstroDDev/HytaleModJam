package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.builtin.npceditor.NPCRoleAssetTypeHandler;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.corecomponents.world.ActionStorePosition;
import com.modjam.hytalemoddingjam.gameLogic.spawing.WaveHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameLogic {
	public final GameConfig config;
	public final World world;
	public final Store<EntityStore> store;
	private ScheduledFuture<?> executor;
	private boolean started = false;
	private WaveHelper waveHelper;

	public GameLogic(World world, GameConfig config) {
		this.config = config;
		this.world = world;
		this.store = world.getEntityStore().getStore();
	}

	public void start() {

		this.executor = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> world.execute(this::tick), 500, 500, TimeUnit.MILLISECONDS);
		this.started = true;

		this.waveHelper = new WaveHelper(config);
		waveHelper.start(store);
	}

	public void tick() {
        if (!started) {
            return;
        }

		waveHelper.update(store);
    }

	public List<Player> getPlayers() {
		return world.getPlayerRefs().stream().map((ref) -> ref.getReference().getStore().getComponent(ref.getReference(), Player.getComponentType())).toList();

	}

	public void collectGear(){
		collectedgears++;
	}

	public boolean revivePlayer(String username)
	{
		if(waveHelper.getScrapCollectedWave()>=config.getRespawnScrap())
		{
			waveHelper.addScrap(-config.getRespawnScrap());
			world.sendMessage(Message.raw("Reviving "+username+": "+config.getRespawnScrap()+" Scraps lost."));
			return true;
		}
		world.sendMessage(Message.raw(username+" is out of the game!"));
		return false;

	}
	public void cleanup() {
		executor.cancel(true);
	}

	public void stop() {
		this.started = false;
	}

	public WaveHelper getWaveHelper() {
		return waveHelper;
	}
}
