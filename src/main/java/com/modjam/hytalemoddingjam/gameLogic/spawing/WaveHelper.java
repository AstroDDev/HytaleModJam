package com.modjam.hytalemoddingjam.gameLogic.spawing;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.gameLogic.EndedGameData;
import com.modjam.hytalemoddingjam.gameLogic.GameConfig;

import java.util.Collection;
import java.util.function.Consumer;

public class WaveHelper {
    private GameConfig config;
    private WaveSpawner spawner;

    private long waveStartTime;
    private int waveIndex;
    private boolean intermission;
	private int quota=0;
	private int scrapCollectedWave=0;
	private int scrapCollectedTotal=0;
	private Consumer<EndedGameData> triggerGameOver;
    public WaveHelper(GameConfig config,double serverDifficulty ){
        this.config = config;
        this.spawner = new WaveSpawner(serverDifficulty, config);
        spawner.Disable();

    }
	public void setGameOverFunction(Consumer<EndedGameData> fn)
	{
		this.triggerGameOver=fn;
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
                intermission = false;
                spawner.Enable();
                store.getExternalData().getWorld().sendMessage(Message.raw("Wave " + (waveIndex + 1) + " has started"));
                //To Do!!! Run UI Events here to say the next wave started and also update quota
            }
        }
        else{
            if (currentTime > (waveStartTime + config.getWaveLength())){

				//Quota checking
				if(this.scrapCollectedWave >= quota)
				{
					//proceed to next wave
					nextWave(store,currentTime);
				}
				else
				{
					if(triggerGameOver !=null)
						triggerGameOver.accept(new EndedGameData().setLastWave(waveIndex).setTotalScrap(scrapCollectedTotal).setWon(false));
					spawner.Disable();
				}

            }
            else{
                spawner.Spawn(store);
            }
        }
    }

	private void nextWave(Store<EntityStore> store, long currentTime)
	{
		waveIndex++;
		quota = (int) Math.floor(config.getScrapQuotaRate() * spawner.getDifficulty());
		//Is all bonus scrap loss at the end of a wave? If so we should reset this to 0 instead.
		scrapCollectedWave = 0;

		spawner.Disable();
		spawner.Despawn(store);
		spawner.setWave(waveIndex);

		intermission = true;
		waveStartTime = currentTime + config.getWaveIntermissionLength();

		if (waveIndex >= config.getWaveCount()){
			if(triggerGameOver !=null)
				triggerGameOver.accept(new EndedGameData().setLastWave(waveIndex).setTotalScrap(scrapCollectedTotal).setWon(true));

		}
		else{
			store.getExternalData().getWorld().sendMessage(Message.raw("Wave " + waveIndex + " has ended"));
		}
	}
	public void forceEnd()
	{
		if(triggerGameOver !=null)
			triggerGameOver.accept(new EndedGameData().setLastWave(waveIndex).setTotalScrap(scrapCollectedTotal).setWon(false));
		spawner.Disable();
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
