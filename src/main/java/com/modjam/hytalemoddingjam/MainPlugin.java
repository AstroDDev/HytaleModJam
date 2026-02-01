package com.modjam.hytalemoddingjam;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.Matchmaking.Commands.MatchmakingCommands;
import com.modjam.hytalemoddingjam.Matchmaking.Commands.lobby.LobbyCommands;
import com.modjam.hytalemoddingjam.Matchmaking.MatchmakingSystem;
import com.modjam.hytalemoddingjam.commands.GameCommand;
import com.modjam.hytalemoddingjam.gameLogic.GameConfig;
import com.modjam.hytalemoddingjam.gameLogic.GameInstances;
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

        // weak points
        weakPointComponentType = getEntityStoreRegistry().registerComponent(WeakPointComponent.class, "WeakPointComponent", WeakPointComponent.CODEC);
        getCodecRegistry(Interaction.CODEC).register("WeakProjectileCondition", WeakProjectileInteraction.class, WeakProjectileInteraction.CODEC);
        getCommandRegistry().registerCommand(new SpawnEntityWithWeakPointCommand("WeakPointTest", "Test for spawning an enemy that has a weak point on it"));

        // matchmaking + lobbies
        getCommandRegistry().registerCommand(new MatchmakingCommands());
        getCommandRegistry().registerCommand(new LobbyCommands());
        getEntityStoreRegistry().registerSystem(new MatchmakingSystem());

        getCodecRegistry(GameplayConfig.PLUGIN_CODEC).register(GameConfig.class, "MannCo", GameConfig.CODEC);


        GameInstances.init(this.getEventRegistry());
		this.getCommandRegistry().registerCommand(new GameCommand());
    }
}
