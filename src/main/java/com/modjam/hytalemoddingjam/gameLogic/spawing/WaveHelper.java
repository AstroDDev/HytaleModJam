package com.modjam.hytalemoddingjam.gameLogic.spawing;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.gameLogic.GameConfig;

import java.util.Collection;

public class WaveHelper {
    private GameConfig config;
    private WaveSpawner spawner;

    private long waveStartTime;
    private int waveIndex;
    private boolean intermission;
	private int quota=0;
	private int scrapCollectedWave=0;
	private int scrapCollectedTotal=0;
    public WaveHelper(GameConfig config){
        this.config = config;
        this.spawner = new WaveSpawner(1, config);
        spawner.Disable();
    }

    public void start(Store<EntityStore> store){
        waveStartTime = System.currentTimeMillis() + config.getWaveIntermissionLength();
        waveIndex = 0;
		quota=config.getScrapQuotaForWave(waveIndex);
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
				if(this.scrapCollectedWave>=quota)
				{
					//proceed to next wave
					nextWave(store,currentTime);
				}
				else
				{

					spawner.Disable();
					//TODO not enough scraps, game over.
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
		quota=config.getScrapQuotaForWave(waveIndex);
		//Is all bonus scrap loss at the end of a wave? If so we should reset this to 0 instead.
		scrapCollectedWave-=quota;

		spawner.Disable();
		spawner.Despawn(store);
		spawner.setWave(waveIndex + 1);

		intermission = true;
		waveStartTime = currentTime + config.getWaveIntermissionLength();

		if (waveIndex >= config.getWaveCount()){
			//Crude end the game for now
			Collection<PlayerRef> playerRefs = store.getExternalData().getWorld().getPlayerRefs();

			for (PlayerRef playerRef : playerRefs){
				InstancesPlugin.exitInstance(playerRef.getReference(), store);
			}
		}
		else{
			store.getExternalData().getWorld().sendMessage(Message.raw("Wave " + waveIndex + " has ended"));
		}
	}
	public void scrapCollected(int collected,World world){
		scrapCollectedWave+=collected;
		scrapCollectedTotal+=collected;
		world.sendMessage(Message.raw("Scrap collected: "+scrapCollectedWave+"/"+quota));
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
}
