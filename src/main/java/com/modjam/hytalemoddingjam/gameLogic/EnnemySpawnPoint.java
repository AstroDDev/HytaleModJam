package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;

public class EnnemySpawnPoint {
	public static final BuilderCodec<EnnemySpawnPoint> CODEC = BuilderCodec.builder(EnnemySpawnPoint.class, EnnemySpawnPoint::new)
			.append(new KeyedCodec<>("Coordinates", ProtocolCodecs.VECTOR3F), (config, o) -> config.pos = new Vector3d(o.x, o.y, o.z), config -> new Vector3f((float) config.pos.x, (float) config.pos.y, (float) config.pos.z))
			.documentation("The required amount of scrap to respawn").add()

			.build();
	public static ArrayCodec<EnnemySpawnPoint> ARRAY_CODEC = new ArrayCodec<>(CODEC, EnnemySpawnPoint[]::new);

	private Vector3d pos = Vector3d.ZERO.clone();

	public Vector3d getPos() {
		return pos;
	}
}
