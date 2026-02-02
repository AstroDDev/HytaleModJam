package com.modjam.hytalemoddingjam.gameLogic.entities;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.weakpoints.WeakPointComponent;
import it.unimi.dsi.fastutil.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class EnemyAdditions {
	private static ConcurrentHashMap<String, BiConsumer<Ref<EntityStore>, INonPlayerCharacter>> additions = new ConcurrentHashMap<>();

	public static void add(String npc, BiConsumer<Ref<EntityStore>, INonPlayerCharacter> afterSpawn) {
		additions.put(npc, afterSpawn);
	}

	/**
	 * Run a consumer method linked to a specific npcrole (if present). Allow to add extra components and stuff
	 */
	public static void onNPCSpawned(Pair<Ref<EntityStore>, INonPlayerCharacter> npcInfos) {
		var fn = additions.get(npcInfos.second().getNPCTypeId());
		if(fn != null)
			fn.accept(npcInfos.left(), npcInfos.right());
	}

	/**
	 * Methods get called for targeted npc just after he is spawned
	 */
	static {
		add("ArmorBot", (ref, npc) -> {
			//TODO I let you handle the specifics of WeakPoint definitions. Diams.
			ref.getStore().addComponent(ref, WeakPointComponent.getComponentType(), new WeakPointComponent());
		});
	}
}
