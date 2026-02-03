package com.modjam.hytalemoddingjam.gameLogic;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.MainPlugin;

import javax.annotation.Nonnull;

public class DifficultyResource implements Resource<EntityStore> {
   @Nonnull
   public static final BuilderCodec<DifficultyResource> CODEC = BuilderCodec.builder(DifficultyResource.class, DifficultyResource::new)
      .append(new KeyedCodec<>("LocalDifficulty", Codec.DOUBLE), (o, dif) -> o.difficulty = dif, o -> o.difficulty)
      .documentation("The MannCo difficulty for this server")
      .add()
      .build();
   private double difficulty;

   public DifficultyResource() {
      this(0.5);
   }

   public DifficultyResource(double diff) {
      this.difficulty = diff;
   }

   public double getLocalDifficulty() {
      return this.difficulty;
   }

   public void setLocalDifficulty(double diff) {
      this.difficulty = diff;
   }
	public void addDifficulty(double added) {
		this.difficulty = Math.max(0.1,difficulty+added);
	}
   @Nonnull
   @Override
   public Resource<EntityStore> clone() {
      return new DifficultyResource(this.difficulty);
   }

   @Nonnull
   @Override
   public String toString() {
      return "DifficultyResource{difficulty=" + this.difficulty + "}";
   }
}
