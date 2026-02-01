package com.modjam.hytalemoddingjam.gameLogic.spawing;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.gameLogic.GameConfig;

import java.util.Collection;
import java.util.Date;

public class WaveHelper {
    private GameConfig config;
    private WaveSpawner spawner;

    private long waveStartTime;
    private int waveIndex;
    private boolean intermission;

    public WaveHelper(GameConfig config){
        this.config = config;
        this.spawner = new WaveSpawner(1, config);
        spawner.Disable();
    }

    public void Start(Store<EntityStore> store){
        waveStartTime = new Date().getTime() + config.getWaveIntermissionLength();
        waveIndex = 0;
        intermission = true;
    }

    public void Update(Store<EntityStore> store){
        long currentTime = new Date().getTime();

        if (intermission) {
            if (currentTime > waveStartTime){
                intermission = false;
                spawner.Enable();
                store.getExternalData().getWorld().sendMessage(Message.raw("Wave " + (waveIndex + 1) +" has started"));
                //To Do!!! Run UI Events here to say the next wave started and also update quota
            }
        }
        else{
            if (currentTime > (waveStartTime + config.getWaveLength())){
                waveIndex++;
                //To Do!!! Check if reached quota here
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
            else{
                spawner.Spawn(store);
            }
        }
    }
}
