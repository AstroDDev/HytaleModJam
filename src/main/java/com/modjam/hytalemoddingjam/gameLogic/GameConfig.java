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
			.append(new KeyedCodec<>("SpawnPoints", EnnemySpawnPoint.ARRAY_CODEC), (config, o) -> config.points = o, config -> config.points)
			.documentation("The Spawnpoints of ennemies").add()
			.append(new KeyedCodec<>("ReturnPortal", VECTORI), (config, o) -> config.returnPortal = o, config -> config.returnPortal)
			.documentation("The required amount of scrap to respawn").add()
			.build();

	private int portalScrap = 100;
	private int respawnScrap = 10;
	private Vector3i returnPortal;
	private EnnemySpawnPoint[] points = new EnnemySpawnPoint[0];

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

	public EnnemySpawnPoint[] getPoints() {
		return points;
	}

	public void setPoints(EnnemySpawnPoint[] points) {
		this.points = points;
	}
}
