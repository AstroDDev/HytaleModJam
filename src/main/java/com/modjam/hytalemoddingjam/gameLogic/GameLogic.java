package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.builtin.npceditor.NPCRoleAssetTypeHandler;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionEffects;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.simple.ApplyEffectInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.corecomponents.world.ActionStorePosition;
import com.modjam.hytalemoddingjam.MainPlugin;
import com.modjam.hytalemoddingjam.gameLogic.spawing.WaveHelper;

import java.util.ArrayList;
import java.util.Collection;
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
	private List<Ref<EntityStore>> deadPlayers=new ArrayList<>();

	public GameLogic(World world, GameConfig config) {
		this.config = config;
		this.world = world;
		this.store = world.getEntityStore().getStore();
	}

	public void start() {

		this.executor = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> world.execute(this::tick), 500, 500, TimeUnit.MILLISECONDS);
		this.started = true;
		var savedDifficluty = Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType()).getLocalDifficulty();
		this.waveHelper = new WaveHelper(config, savedDifficluty);
		waveHelper.start(store);
		waveHelper.setGameOverFunction(this::onGameEnd);
		this.world.sendMessage(Message.raw("Hazard Level is " + Math.floor(savedDifficluty * 100)));
	}
	public void onGameEnd(EndedGameData data)
	{

		this.stop();
		Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType()).addDifficulty(data.isWon()?1:-1);
		world.sendMessage(Message.raw("Game "+(data.isWon()?"Won":"Over")+"!"));
		world.sendMessage(Message.raw("You survived "+data.getLastWave()+" waves.\nYou collected "+data.getTotalScrap()+" scraps."));
		world.sendMessage(Message.raw("Instance will close in 10 secondes"));
		HytaleServer.SCHEDULED_EXECUTOR.schedule(()->{
			world.stopIndividualWorld();

		},10,TimeUnit.SECONDS);

	}
	/**
	 * Temporary method, allow for easy collect scrap from inventory for now
	 */
	private void autoScoreScraps()
	{
				getPlayers().forEach(pl -> {

					var trans = pl.getInventory().getCombinedEverything().removeItemStack(new ItemStack("RustyGear", 10), false, true);
					var eaten = 10;
					if(trans.getRemainder() != null)
						eaten -= trans.getRemainder().getQuantity();
					if(eaten > 0) {
						waveHelper.scrapCollected(eaten,world);
					}


				});
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
		for(Player player : getPlayers()) {
			applyEffect("HealthRegen_Buff_T1",player.getReference());
			if(!deadPlayers.contains(player.getReference()))
			{
				atLeast1PlayerAlive=true;
				break;
			}
		}
		if(!atLeast1PlayerAlive)
			waveHelper.forceEnd();
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
