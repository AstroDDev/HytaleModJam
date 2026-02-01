package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class GameRespawnController implements RespawnController {
   public static final GameRespawnController INSTANCE = new GameRespawnController();
   @Nonnull
   public static final BuilderCodec<GameRespawnController> CODEC = BuilderCodec.builder(GameRespawnController.class, () -> INSTANCE).build();

   public GameRespawnController() {
   }

   @Override
   public CompletableFuture<Void> respawnPlayer(
      @Nonnull World world, @Nonnull Ref<EntityStore> playerReference, @Nonnull ComponentAccessor<EntityStore> commandBuffer
   ) {
      ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();

      assert spawnProvider != null;
	   GameLogic logic= GameInstances.get(world);
	   assert logic !=null;
	   if(logic.revivePlayer(playerReference.getStore().getComponent(playerReference,Player.getComponentType()).getDisplayName()))
	   {
		   Transform spawnPoint = spawnProvider.getSpawnPoint(playerReference, commandBuffer);
		   Teleport teleportComponent = Teleport.createForPlayer(spawnPoint);
		   world.execute(()-> commandBuffer.addComponent(playerReference, Teleport.getComponentType(), teleportComponent));

	   }
	   else
		   world.execute(()->InstancesPlugin.exitInstance(playerReference, commandBuffer));

	   return CompletableFuture.completedFuture(null);
   }
}