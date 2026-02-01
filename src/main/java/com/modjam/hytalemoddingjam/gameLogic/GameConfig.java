package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3i;

public class GameConfig {
	public static final BuilderCodec<Vector3i> VECTORI = BuilderCodec.builder(Vector3i.class, Vector3i::new)
			.addField(new KeyedCodec<>("X", Codec.INTEGER), (rangeVector3i, d) -> rangeVector3i.x = d, rangeVector3i -> rangeVector3i.x)
			.addField(new KeyedCodec<>("Y", Codec.INTEGER), (rangeVector3i, d) -> rangeVector3i.y = d, rangeVector3i -> rangeVector3i.y)
			.addField(new KeyedCodec<>("Z", Codec.INTEGER), (rangeVector3i, d) -> rangeVector3i.z = d, rangeVector3i -> rangeVector3i.z)
			.build();
	public static final BuilderCodec<GameConfig> CODEC = BuilderCodec.builder(GameConfig.class, GameConfig::new)
			.append(new KeyedCodec<>("PortalScrap", Codec.INTEGER), (config, o) -> config.portalScrap = o, config -> config.portalScrap)
			.documentation("The required amount of scrap to open the return portal")
			.add()
			.append(new KeyedCodec<>("RespawnScrap", Codec.INTEGER), (config, o) -> config.respawnScrap = o, config -> config.respawnScrap)
			.documentation("The required amount of scrap to respawn")
			.add()
			.append(new KeyedCodec<>("SpawnPoints", EnemySpawnPoint.ARRAY_CODEC), (config, o) -> config.points = o, config -> config.points)
			.documentation("The Spawnpoints of enemies").add()
			.append(new KeyedCodec<>("ReturnPortal", VECTORI), (config, o) -> config.returnPortal = o, config -> config.returnPortal)
			.documentation("The required amount of scrap to respawn").add()
			.append(new KeyedCodec<>("WeakEnemies", Codec.STRING_ARRAY), (config, o) -> config.weakEnemies = o, config -> config.weakEnemies)
			.documentation("Weak enemies are enemeies meant to be cannon fodder that will not drop Portal Scrap").add()
			.append(new KeyedCodec<>("StrongEnemies", Codec.STRING_ARRAY), (config, o) -> config.strongEnemies = o, config -> config.strongEnemies)
			.documentation("Strong enemies are enemies that do drop Portal Scrap and generally spawn less often").add()
			.append(new KeyedCodec<>("WeakSpawnAmount", Codec.DOUBLE), (config, o) -> config.weakSpawnAmount = o, config -> config.weakSpawnAmount)
			.documentation("The amount of weak enemies to spawn at difficulty 1.0").add()
			.append(new KeyedCodec<>("WeakGroupingMin", Codec.DOUBLE), (config, o) -> config.weakGroupingMin = o, config -> config.weakGroupingMin)
			.documentation("How big of groups weak enemies will spawn in at difficulty 0.0").add()
			.append(new KeyedCodec<>("WeakGroupingMax", Codec.DOUBLE), (config, o) -> config.weakGroupingMax = o, config -> config.weakGroupingMax)
			.documentation("How big of groups weak enemies will spawn in at difficulty 1.0").add()
			.append(new KeyedCodec<>("StrongSpawnAmount", Codec.DOUBLE), (config, o) -> config.strongSpawnAmount = o, config -> config.strongSpawnAmount)
			.documentation("The amount of strong enemies to spawn at difficulty 1.0").add()
			.append(new KeyedCodec<>("WaveDifficultyIncrease", Codec.DOUBLE), (config, o) -> config.waveDifficultyIncrease = o, config -> config.waveDifficultyIncrease)
			.documentation("The slight gain in difficulty each new wave").add()

			.append(new KeyedCodec<>("WaveCount", Codec.INTEGER), (config, o) -> config.waveCount = o, config -> config.waveCount)
			.documentation("The number of waves to clear").add()
			.append(new KeyedCodec<>("WaveLength", Codec.INTEGER), (config, o) -> config.waveLength = o, config -> config.waveLength)
			.documentation("The length of the wave in milliseconds").add()
			.append(new KeyedCodec<>("WaveIntermissionLength", Codec.INTEGER), (config, o) -> config.waveIntermissionLength = o, config -> config.waveIntermissionLength)
			.documentation("The length of rest in between waves").add()
			.build();

	private int portalScrap = 100;
	private int respawnScrap = 10;
	private Vector3i returnPortal;

	//Spawn Data
	private EnemySpawnPoint[] points = new EnemySpawnPoint[0];
	private String[] strongEnemies = new String[0];
	private String[] weakEnemies = new String[0];
	private double weakSpawnAmount = 10;
	//Weak grouping min and max define how many "weak" enemies should spawn at a time for a given difficulty
	private double weakGroupingMin = 1;
	private double weakGroupingMax = 2;
	private double strongSpawnAmount = 2;
	private double waveDifficultyIncrease = 0.025;

	private int waveCount = 3;
	private int waveLength = 60000; //In Milliseconds
	private int waveIntermissionLength = 10000;


	public GameConfig() {
	}

	public int getPortalScrap() {
		return portalScrap;
	}

	public void setPortalScrap(int portalScrap) {
		this.portalScrap = portalScrap;
	}

	public int getRespawnScrap() {
		return respawnScrap;
	}

	public void setRespawnScrap(int respawnScrap) {
		this.respawnScrap = respawnScrap;
	}

	public Vector3i getReturnPortal() {
		return returnPortal;
	}

	public void setReturnPortal(Vector3i returnPortal) {
		this.returnPortal = returnPortal;
	}

	public EnemySpawnPoint[] getPoints() {
		return points;
	}

	public void setPoints(EnemySpawnPoint[] points) {
		this.points = points;
	}

	public String[] getWeakEnemies() { return weakEnemies; }

	public String[] getStrongEnemies() { return strongEnemies; }

	public double getWeakSpawnAmount() { return weakSpawnAmount; }

	public double getWeakGroupingMin() { return weakGroupingMin; }

	public double getWeakGroupingMax() { return weakGroupingMax; }

	public double getStrongSpawnAmount() { return strongSpawnAmount; }

	public double getWaveDifficultyIncrease() { return waveDifficultyIncrease; }

	public int getWaveCount() { return waveCount; }

	public int getWaveLength() { return waveLength; }

	public int getWaveIntermissionLength() { return waveIntermissionLength; }
}
