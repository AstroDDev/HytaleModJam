package com.modjam.hytalemoddingjam.hud;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.MainPlugin;
import com.modjam.hytalemoddingjam.gameLogic.GameInstances;
import com.modjam.hytalemoddingjam.gameLogic.GameLogic;

import javax.annotation.Nonnull;

public class MannCoHudSystem extends EntityTickingSystem<EntityStore> {

    private static final long ANNOUNCEMENT_LENGTH_MILLIS = 2000;

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        // TODO this would be best if it used a component specific to players in the game
        return Player.getComponentType();
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());

        CustomUIHud customHud = player.getHudManager().getCustomHud();
        GameLogic gameLogic = GameInstances.get(player.getWorld());
        if (gameLogic == null) {
			if(customHud instanceof MannCoHud)
				player.getHudManager().setCustomHud(playerRef,null);
            return;
        }

        if (customHud == null) {
            MannCoHud mannCoHud = new MannCoHud(playerRef);
            player.getHudManager().setCustomHud(playerRef, mannCoHud);
            customHud = mannCoHud;
        }

        UICommandBuilder builder = new UICommandBuilder();
        builder.append("Hud.ui");
		var diff=Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType()).getLocalDifficulty();
		builder.set("#DifficultyLabel.Text","Difficulty: "+diff);
		if(gameLogic.getWaveHelper().isIntermission()) {
			//builder.set("#GearLabel.Text", "0/0");
			builder.set("#WaveLabel.Text", "Prep Time");
		}
		else {

			builder.set("#WaveLabel.Text", "Wave "+(gameLogic.getWaveHelper().getWaveIndex()+1));
		}
		builder.set("#GearLabel.Text", gameLogic.getWaveHelper().getScrapCollectedWave()+"/"+gameLogic.getWaveHelper().getQuota());
		var time=0;
		if(gameLogic.getWaveHelper().isIntermission())
			time=(int)Math.abs(System.currentTimeMillis() -  gameLogic.getWaveHelper().getWaveStartTime());
		else
			time= (int) ((gameLogic.getWaveHelper().getWaveStartTime()+ gameLogic.config.getWaveLength())-System.currentTimeMillis());
		time=time/1000;
		builder.set("#TimerLabel.Text",""+time);

//        showWaveText(gameLogic, builder);

        customHud.update(true, builder);
    }

    private static void showWaveText(GameLogic gameLogic, UICommandBuilder builder) {
        if (System.currentTimeMillis() - gameLogic.getWaveHelper().getWaveStartTime() < ANNOUNCEMENT_LENGTH_MILLIS) {
            if (gameLogic.getWaveHelper().isIntermission()) {
                // NOTE: this is + 1 for 0-indexed then - 1 because wave over happens after index increment
				if(gameLogic.getWaveHelper().getWaveIndex()>0)
                	builder.set("#BannerTitle.Text", "Wave " + (gameLogic.getWaveHelper().getWaveIndex()) + " Over!");
				else
					builder.set("#BannerTitle.Text", "");
            } else {
                builder.set("#BannerTitle.Text", "Wave " + (gameLogic.getWaveHelper().getWaveIndex() + 1) + " Incoming!");
            }
        } else {
            builder.set("#BannerTitle.Text", "");
        }
    }
}
