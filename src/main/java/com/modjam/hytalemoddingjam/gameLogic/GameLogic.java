package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.builtin.npceditor.NPCRoleAssetTypeHandler;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemEntityConfig;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.EntityRemoveEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainerUtil;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerItemEntityPickupSystem;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionEffects;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.simple.ApplyEffectInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.corecomponents.world.ActionStorePosition;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import com.modjam.hytalemoddingjam.MainPlugin;
import com.modjam.hytalemoddingjam.gameLogic.spawing.WaveHelper;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameLogic {
	public final GameConfig config;
	public final World world;
	public final Store<EntityStore> store;
	private ScheduledFuture<?> executor;
	private boolean started = false;
	private WaveHelper waveHelper;
	private List<Ref<EntityStore>> deadPlayers = new ArrayList<>();

	public GameLogic(World world, GameConfig config) {
		this.config = config;
		this.world = world;
		this.store = world.getEntityStore().getStore();
	}

	public void start() {
		this.executor = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> world.execute(this::tick), 500, 500, TimeUnit.MILLISECONDS);
		this.started = true;
		var savedDifficluty = Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType()).getLocalDifficulty();
		this.waveHelper = new WaveHelper(this, config, savedDifficluty, this::onGameEnd);
		waveHelper.start(store);
		waveHelper.setNextWaveFunction((i)->respawnAllPlayers());
	}

	public void respawnAllPlayers() {
		deadPlayers.forEach((p)->{
			var tp=Teleport.createForPlayer(world.getWorldConfig().getSpawnProvider().getSpawnPoint(p,p.getStore()));
			p.getStore().addComponent(p,Teleport.getComponentType(),tp);
		});
		deadPlayers.clear();
	}

	public void onGameEnd(EndedGameData data) {

		this.stop();
		Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType()).addDifficulty(data.isWon()? 0.25 : -0.1);
		world.sendMessage(Message.raw("Game "+(data.isWon()?"Won":"Over")+"!"));
		world.sendMessage(Message.raw("You survived "+data.getLastWave()+" waves.\nYou collected "+data.getTotalScrap()+" scraps."));
		world.sendMessage(Message.raw("Instance will close in 10 secondes"));
		HytaleServer.SCHEDULED_EXECUTOR.schedule(()->{
			world.execute(()->{
				world.drainPlayersTo(Universe.get().getDefaultWorld());
			});


		},10,TimeUnit.SECONDS);
	}
	public void applyEffect(String effectId,Ref<EntityStore> player)
	{
		EntityEffect entityEffect = EntityEffect.getAssetMap().getAsset(effectId);
		if (entityEffect != null) {
			if (player.isValid()) {
				EffectControllerComponent effectControllerComponent = player.getStore().getComponent(player, EffectControllerComponent.getComponentType());
				if(effectControllerComponent != null && effectControllerComponent.getActiveEffects().get(EntityEffect.getAssetMap().getIndex(effectId))==null)
					effectControllerComponent.addEffect(player, entityEffect,player.getStore());

			}
		}
	}

	public void tick() {
        if (!started) {
            return;
        }
		waveHelper.update(store);
		boolean atLeast1PlayerAlive=false;

		Collection<PlayerRef> playerRefs = world.getPlayerRefs();
		for(PlayerRef playerref : playerRefs) {
			Ref<EntityStore> ref = playerref.getReference();
			Player player = store.getComponent(ref, Player.getComponentType());

			applyEffect("HealthRegen_Buff_T1",player.getReference());
			if(!deadPlayers.contains(player.getReference()))
			{
				atLeast1PlayerAlive=true;
			}
			//Forcing player inventory to be in one state
			Inventory inventory = player.getInventory();
			ItemContainer hotbar = inventory.getHotbar();
			ItemContainer storage = inventory.getStorage();

			boolean itemChange = false;

			for (short i = 0; i < storage.getCapacity(); i++){
				ItemStack item = storage.getItemStack(i);
				if (item == null || !item.getItemId().equals("BlankItem")) {
					storage.setItemStackForSlot(i, new ItemStack("BlankItem"));
					itemChange = true;
				}
			}

			ItemStack firstSlot = hotbar.getItemStack((short)0);
			if (firstSlot == null || !firstSlot.getItemId().equals("MannCoRifle")) {
				hotbar.setItemStackForSlot((short) 0, new ItemStack("MannCoRifle"));
				itemChange = true;
			}

			for (short i = 1; i < 8; i++) {
				ItemStack item = hotbar.getItemStack(i);
				if (item == null || !item.getItemId().equals("BlankItem")) {
					hotbar.setItemStackForSlot(i, new ItemStack("BlankItem"));
					itemChange = true;
				}
			}

			ItemStack lastSlot = hotbar.getItemStack((short) 8);
			if (!lastSlot.getItemId().equals("RustyGear") && !lastSlot.isEmpty()){
				hotbar.setItemStackForSlot((short) 8, ItemStack.EMPTY);
				itemChange = true;
			}

			if (itemChange) player.sendInventory();
		}
		if(!atLeast1PlayerAlive)
			waveHelper.forceEnd();
    }

	public Collection<PlayerRef> getPlayerRefs() {
		return world.getPlayerRefs();
	}

	public List<Player> getPlayers() {
		return world.getPlayerRefs().stream().map((ref) -> ref.getReference().getStore().getComponent(ref.getReference(), Player.getComponentType())).toList();

	}

	public void collectGear(){
		waveHelper.addScrap(1);
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
	public void addPlayerToDeadList(Ref<EntityStore> dead)
	{
		world.execute(()->deadPlayers.add(dead));
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
