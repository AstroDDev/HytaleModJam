package com.modjam.hytalemoddingjam.hud;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.MainPlugin;
import com.modjam.hytalemoddingjam.gameLogic.DifficultyResource;
import com.modjam.hytalemoddingjam.gameLogic.GameInstances;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.Collection;

public class GameMenuPage extends InteractiveCustomUIPage<GameMenuPage.GameMenuInputData> {
    public GameMenuPage(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, GameMenuInputData.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/GameMenu.ui");
        double difficulty = Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType()).getLocalDifficulty();
        uiCommandBuilder.set("#HazardLevel.Text", "Current Hazard Level: " + ((int) Math.floor(difficulty) * 100) + "%");
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SummonButton", EventData.of("Action", "SummonActivated"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseEntered, "#SummonButton", EventData.of("Action", "SummonMouseEntered"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#SummonButton", EventData.of("Action", "SummonMouseExited"), false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @NonNullDecl GameMenuInputData data){
        super.handleDataEvent(ref, store, data);

        if ("SummonMouseEntered".equals(data.action)) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#Vignette.Visible", true);
            this.sendUpdate(commandBuilder, (UIEventBuilder)null, false);
        } else if ("SummonMouseExited".equals(data.action)) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#Vignette.Visible", false);
            this.sendUpdate(commandBuilder, (UIEventBuilder)null, false);
        } else {
            store.getComponent(ref, Player.getComponentType()).getPageManager().setPage(ref, store, Page.None);

            var inst = GameInstances.getAny();

            World world = store.getExternalData().getWorld();

            if (inst == null){
                GameInstances.createInstance(ref, world);
                inst = GameInstances.getAny();
            }

            Collection<PlayerRef> playerRefs = world.getPlayerRefs();

            for (PlayerRef pref : playerRefs){
                InstancesPlugin.teleportPlayerToInstance(ref, ref.getStore(), inst.world, null);
            }

            sendUpdate();
        }
    }

    public static class GameMenuInputData{
        public String action;

        public static final BuilderCodec<GameMenuInputData> CODEC = BuilderCodec.builder(GameMenuInputData.class, GameMenuInputData::new).append(new KeyedCodec<>("Action", Codec.STRING), (e, s) -> e.action = s, (e) -> e.action).add().build();
    }
}
