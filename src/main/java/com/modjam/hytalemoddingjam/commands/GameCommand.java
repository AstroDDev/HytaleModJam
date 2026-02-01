package com.modjam.hytalemoddingjam.commands;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.gameLogic.GameInstances;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GameCommand extends AbstractPlayerCommand {
	public GameCommand() {
		super("game", "server.commands.modjam.game.desc");
		this.addSubCommand(new CreateNewGameCommand());
		this.addSubCommand(new ForceGameStateCommand());
		this.addSubCommand(new JoinGameCommand());
	}

	@Override
	protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
	) {

	}

	public static class CreateNewGameCommand extends AbstractPlayerCommand {

		public CreateNewGameCommand() {
			super("create", "server.commands.modjam.game.create.desc");
			this.addAliases("c");
		}

		@Override
		protected void execute(
				@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
		) {
			tryCreateInstance(ref, world);
			context.sendMessage(Message.raw("Creating instance..."));

		}
	}

	public static class JoinGameCommand extends AbstractPlayerCommand {
		
		public JoinGameCommand() {
			super("join", "server.commands.modjam.game.join.desc");
			this.addAliases("j");
		}

		@Override
		protected void execute(
				@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
		) {
			var inst = GameInstances.getAny();

			if(inst != null) {
				InstancesPlugin.teleportPlayerToInstance(ref, ref.getStore(), inst.world, null);
			} else {
				//TODO need to settle on gamemodeId or settings
				tryCreateInstance(ref, world);
				context.sendMessage(Message.raw("Creating instance..."));
			}

		}
	}

	public static class ForceGameStateCommand extends AbstractPlayerCommand {
		@Nonnull
		private final RequiredArg<String> gameNameArg = this.withRequiredArg("state", "server.commands.modjam.game.force.state.arg", ArgTypes.STRING)
				.addValidator(new Validator<>() {
					@Override
					public void accept(String var1, ValidationResults var2) {
						var1 = var1.toLowerCase(Locale.ROOT);
						if(!(var1.equals("start") || var1.equals("s") || var1.equals("stop") || var1.equals("st")))
							var2.fail("Allowed States: (S)tart, (St)op");
					}

					@Override
					public void updateSchema(SchemaContext var1, Schema var2) {}
				});


		public ForceGameStateCommand() {
			super("force", "server.commands.modjam.game.force.desc");
			this.addAliases("f");
		}

		@Override
		protected void execute(
				@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
		) {
			String state = this.gameNameArg.get(context).toLowerCase(Locale.ROOT);
			var inst=GameInstances.get(world);
			if(inst !=null)
			{
				switch(state) {
					case "start", "s" -> inst.start();
					case "stop", "st" -> inst.stop();
				}
			}
		}
	}
	public static CompletableFuture<World> tryCreateInstance(@Nonnull Ref<EntityStore> ref, @Nonnull World world) {
				var trans=ref.getStore().getComponent(ref, TransformComponent.getComponentType());
				Transform returnLocation = new Transform(trans.getPosition().clone(),trans.getRotation().clone());
				CompletableFuture<World> instanceWorld = InstancesPlugin.get().spawnInstance("MannCoWorld", world, returnLocation);
				InstancesPlugin.teleportPlayerToLoadingInstance(ref, ref.getStore(), instanceWorld, null);
				return instanceWorld;

	}
}

