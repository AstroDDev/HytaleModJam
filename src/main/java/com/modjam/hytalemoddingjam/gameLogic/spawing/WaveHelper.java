package com.modjam.hytalemoddingjam.gameLogic.spawing;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.modjam.hytalemoddingjam.gameLogic.EndedGameData;
import com.modjam.hytalemoddingjam.gameLogic.GameConfig;
import com.modjam.hytalemoddingjam.gameLogic.GameInstances;
import com.modjam.hytalemoddingjam.gameLogic.GameLogic;

import java.util.function.Consumer;

public class WaveHelper {
	private GameLogic gameLogic;
    private GameConfig config;
    private WaveSpawner spawner;

    private long waveStartTime;
    private int waveIndex;
    private boolean intermission;
	private int quota=0;
	private int scrapCollectedWave=0;
	private int scrapCollectedTotal=0;
	private final Consumer<EndedGameData> triggerGameOver;
	private Consumer<Integer> triggerNextWave;
    public WaveHelper(GameLogic gameLogic, GameConfig config, double serverDifficulty, Consumer<EndedGameData> triggerGameOver){
		this.gameLogic = gameLogic;
        this.config = config;
        this.spawner = new WaveSpawner(serverDifficulty, config);
		this.triggerGameOver = triggerGameOver;
        spawner.Disable();

    }
	public void setNextWaveFunction(Consumer<Integer> fn)
	{
		this.triggerNextWave=fn;
	}
    public void start(Store<EntityStore> store){
        waveStartTime = System.currentTimeMillis() + config.getWaveIntermissionLength();
        waveIndex = 0;
		quota = (int) Math.floor(config.getScrapQuotaRate() * spawner.getDifficulty());
        intermission = true;
    }

    public void update(Store<EntityStore> store){
        long currentTime = System.currentTimeMillis();

        if (intermission) {
            if (currentTime > waveStartTime){
				startNextWave(store);
            }
        }
        else {
            if (currentTime > (waveStartTime + config.getWaveLength())){

				//Quota checking
				if (this.scrapCollectedWave >= quota) {
					//proceed to next wave
					endWaveAndGoToIntermission(store,currentTime);
				} else {
					triggerGameOver.accept(new EndedGameData().setLastWave(waveIndex).setTotalScrap(scrapCollectedTotal).setWon(false));
					spawner.Disable();
					spawner.Despawn(gameLogic.store);
				}
            }
            else{
                spawner.Spawn(store);
            }
        }
    }

	private void startNextWave(Store<EntityStore> store) {
        scrapCollectedWave = 0;
		intermission = false;
		spawner.Enable();
		Message waveStartingMessage = Message.raw("Wave " + (waveIndex + 1) + " is starting!");
		Message quotaMessage = Message.raw("Quota: " + quota + " scrap");
		store.getExternalData().getWorld().sendMessage(waveStartingMessage);
		for (PlayerRef playerRef : gameLogic.getPlayerRefs()) {
			EventTitleUtil.showEventTitleToPlayer(playerRef, waveStartingMessage, quotaMessage, false, null, 2.0F, 0.5F, 0.5F);
		}
	}

	private void endWaveAndGoToIntermission(Store<EntityStore> store, long currentTime)
	{
		gameLogic.RestoreAllHealth();

		waveIndex++;
		spawner.Disable();
		spawner.Despawn(store);
		var localDiff=spawner.setWave(waveIndex);
		quota = (int) Math.floor(config.getScrapQuotaRate() * localDiff);

		intermission = true;
		waveStartTime = currentTime + config.getWaveIntermissionLength();

		if (waveIndex >= config.getWaveCount()){
			triggerGameOver.accept(new EndedGameData().setLastWave(waveIndex).setTotalScrap(scrapCollectedTotal).setWon(true));
			spawner.Disable();
			spawner.Despawn(gameLogic.store);
		}
		else{
			Message waveOverMessage = Message.raw("Wave " + waveIndex + " over!");
			store.getExternalData().getWorld().sendMessage(waveOverMessage);
			for (PlayerRef playerRef : gameLogic.getPlayerRefs()) {
				EventTitleUtil.showEventTitleToPlayer(playerRef, waveOverMessage, Message.raw("Intermission: " + ((config.getWaveIntermissionLength() / 1000)) + " seconds"), false, null, 2.0F, 0.5F, 0.5F);
			}
			if(triggerNextWave != null) {
                triggerNextWave.accept(waveIndex);
            }
		}

	}
	public void forceEnd()
	{
		if(triggerGameOver !=null)
			triggerGameOver.accept(new EndedGameData().setLastWave(waveIndex).setTotalScrap(scrapCollectedTotal).setWon(false));
		spawner.Disable();
		spawner.Despawn(gameLogic.store);
	}
    public long getWaveStartTime() {
        return waveStartTime;
    }

    public int getWaveIndex() {
        return waveIndex;
    }

    public boolean isIntermission() {
        return intermission;
    }

	public int getScrapCollectedWave() {
		return scrapCollectedWave;
	}

	public void addScrap(int amount) {
		this.scrapCollectedWave += amount;
		this.scrapCollectedTotal += amount;

		//To Do, update UI for all players
	}

	public int getScrapCollectedTotal() {
		return scrapCollectedTotal;
	}


	public int getQuota() {
		return quota;
	}

	public void scrapCollected(int collected, World world){
		scrapCollectedWave+=collected;
		scrapCollectedTotal+=collected;
		world.sendMessage(Message.raw("Scrap collected: "+scrapCollectedWave+"/"+quota));
	}
}
