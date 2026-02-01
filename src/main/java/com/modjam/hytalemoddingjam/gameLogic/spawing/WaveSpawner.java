package com.modjam.hytalemoddingjam.gameLogic.spawing;

import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.modjam.hytalemoddingjam.gameLogic.GameConfig;

import java.util.Date;

public class WaveSpawner {
    private GameConfig config;

    private final double difficulty;
    private int wave;
    private double lastWeakSpawn;
    private double lastStrongSpawn;
    private double weakGroupSize;
    private double weakSpawnRate;
    private double strongSpawnRate;

    private boolean disabled = false;

    public WaveSpawner(double difficulty, GameConfig config){
        this.difficulty = difficulty;
        this.config = config;

        wave = 0;
        lastWeakSpawn = 0;
        lastStrongSpawn = 0;
        weakGroupSize = MathUtil.lerpUnclamped(config.getWeakGroupingMin(), config.getWeakGroupingMax(), difficulty);
        weakSpawnRate = config.getWaveLength() / (config.getWeakSpawnAmount() * difficulty / weakGroupSize);
        strongSpawnRate = config.getWaveLength() / (config.getStrongSpawnAmount() * difficulty);
    }

    public void setWave(int wave){
        this.wave = wave;

        double localDifficulty = difficulty + (this.wave * config.getWaveDifficultyIncrease());

        lastWeakSpawn = -60;
        lastStrongSpawn = -60;
        weakGroupSize = MathUtil.lerpUnclamped(config.getWeakGroupingMin(), config.getWeakGroupingMax(), localDifficulty);
        weakSpawnRate = config.getWaveLength() / (config.getWeakSpawnAmount() * localDifficulty / weakGroupSize);
        strongSpawnRate = config.getWaveLength() / (config.getStrongSpawnAmount() * localDifficulty);
    }

    public void Disable(){ disabled = true; }

    public void Enable(){ disabled = false; }

    public void Spawn(Store<EntityStore> store){
        if (disabled) return;

        long currentTime = new Date().getTime();

        World world = store.getExternalData().getWorld();

        if (currentTime > lastWeakSpawn + weakSpawnRate){
            //Spawn a random group of weak enemies
            int randomNPC = (int) Math.floor(Math.random() * config.getWeakEnemies().length);
            int spawnCount = Math.random() > weakGroupSize % 1 ? (int) Math.floor(weakGroupSize) : (int) Math.floor(weakGroupSize) + 1;
            int spawnPoint = (int) Math.floor(Math.random() * config.getPoints().length);

            world.execute(() -> {
                for (int i = 0; i < spawnCount; i++){
                    NPCPlugin.get().spawnNPC(store, config.getWeakEnemies()[randomNPC], "Robot", config.getPoints()[spawnPoint].getPos().add(new Vector3d(Math.random() * 2 - 1, 0, Math.random() * 2 - 1)), new Vector3f(0, 0, 0));
                }
            });

            lastWeakSpawn = currentTime;
        }

        if (currentTime > lastStrongSpawn + strongSpawnRate){
            //Spawn a random strong enemy
            int randomNPC = (int) Math.floor(Math.random() * config.getStrongEnemies().length);
            int spawnPoint = (int) Math.floor(Math.random() * config.getPoints().length);

            world.execute(() -> {
                NPCPlugin.get().spawnNPC(store, config.getStrongEnemies()[randomNPC], "Robot", config.getPoints()[spawnPoint].getPos().add(new Vector3d(Math.random() * 2 - 1, 0, Math.random() * 2 - 1)), new Vector3f(0, 0, 0));
            });

            lastStrongSpawn = currentTime;
        }
    }

    public void Despawn(Store<EntityStore> store){
        World world = store.getExternalData().getWorld();

        store.forEachEntityParallel((index, archetypeChunk, commandBuffer) -> {
            if (archetypeChunk.getArchetype().contains(Player.getComponentType())) return;
            commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
        });
    }
}
