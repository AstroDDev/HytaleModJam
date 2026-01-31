package com.modjam.hytalemoddingjam;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.weakpoints.SpawnEntityWithWeakPointCommand;
import com.modjam.hytalemoddingjam.weakpoints.WeakPointComponent;
import com.modjam.hytalemoddingjam.weakpoints.WeakProjectileInteraction;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class MainPlugin extends JavaPlugin {
    public MainPlugin(@NonNullDecl JavaPluginInit init) { super(init); }

    public ComponentType<EntityStore, WeakPointComponent> weakPointComponentType;

    private static MainPlugin _instance;
    public static MainPlugin getInstance(){ return _instance; }

    @Override
    protected void setup(){
        _instance = this;

        weakPointComponentType = getEntityStoreRegistry().registerComponent(WeakPointComponent.class, "WeakPointComponent", WeakPointComponent.CODEC);

        getCommandRegistry().registerCommand(new SpawnEntityWithWeakPointCommand("WeakPointTest", "Test for spawning an enemy that has a weak point on it"));

        getCodecRegistry(Interaction.CODEC).register("WeakProjectileCondition", WeakProjectileInteraction.class, WeakProjectileInteraction.CODEC);
    }
}
