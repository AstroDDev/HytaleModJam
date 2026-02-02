package com.modjam.hytalemoddingjam.commands;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.MainPlugin;
import com.modjam.hytalemoddingjam.gameLogic.GameInstances;
import com.modjam.hytalemoddingjam.gameLogic.GameLogic;

import javax.annotation.Nonnull;
import java.util.Locale;

public class GameCommand extends AbstractCommandCollection {

	public GameCommand() {
		super("scrappers", "server.commands.modjam.game.desc");
		this.addSubCommand(new CreateNewGameCommand());
		//this.addSubCommand(new ForceGameStateCommand()); Not really relevant anymore
		this.addSubCommand(new JoinGameCommand());
		this.addSubCommand(new GetDifficultyCommand());
		this.addSubCommand(new SetDifficultyCommand());
	}
	public static class SetDifficultyCommand extends AbstractPlayerCommand
	{
			RequiredArg<Double> param=this.withRequiredArg("level",null,ArgTypes.DOUBLE);
		public SetDifficultyCommand() {
			super("setDifficulty","server.commands.modjam.game.setDifficulty.desc");
		}

		@Override
		protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
			var ress=Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType());
			ress.setLocalDifficulty(param.get(context));
			context.sendMessage(Message.raw("Difficulty set to "+ress.getLocalDifficulty()));
		}
	}
	public static class GetDifficultyCommand extends AbstractPlayerCommand
	{

		public GetDifficultyCommand() {
			super("getDifficulty","server.commands.modjam.game.getDifficulty.desc");
		}

		@Override
		protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
			var ress=Universe.get().getDefaultWorld().getEntityStore().getStore().getResource(MainPlugin.getDifficultyResourceType());
			context.sendMessage(Message.raw("Difficulty is currently "+ress.getLocalDifficulty()));
		}
	}
	public static class CreateNewGameCommand extends AbstractPlayerCommand {
		public CreateNewGameCommand() {
			super("create", "server.commands.modjam.game.create.desc");
			this.addAliases("c");
		}

		@Override
		protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
			GameInstances.createInstance(ref, world);
			context.sendMessage(Message.raw("Creating instance..."));
		}
	}

	public static class JoinGameCommand extends AbstractPlayerCommand {
		public JoinGameCommand() {
			super("join", "server.commands.modjam.game.join.desc");
			this.addAliases("j");
		}

		@Override
		protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
			var inst = GameInstances.getAny();

			if(inst != null) {
				InstancesPlugin.teleportPlayerToInstance(ref, ref.getStore(), inst.world, null);
			} else {
				GameInstances.createInstance(ref, world);
				context.sendMessage(Message.raw("Creating instance..."));
			}

		}
	}

	public static class ForceGameStateCommand extends AbstractPlayerCommand {
		@Nonnull
		private final RequiredArg<String> gameNameArg = this.withRequiredArg("state", "server.commands.modjam.game.force.state.arg", ArgTypes.STRING)
				.addValidator(new Validator<>() {
					@Override
					public void accept(String arg, ValidationResults valResults) {
						arg = arg.toLowerCase(Locale.ROOT);
						if (!(arg.equals("start") || arg.equals("s") || arg.equals("stop") || arg.equals("st"))) {
                            valResults.fail("Allowed States: (S)tart, (St)op");
                        }
					}

					@Override
					public void updateSchema(SchemaContext var1, Schema var2) {}
				});


		public ForceGameStateCommand() {
			super("force", "server.commands.modjam.game.force.desc");
			this.addAliases("f");
		}

		@Override
		protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
			String state = this.gameNameArg.get(context).toLowerCase(Locale.ROOT);
            GameLogic inst = GameInstances.get(world);
			if (inst == null) {
				context.sendMessage(Message.raw("There is no game instance."));
				return;
			}
            switch (state) {
                case "start", "s" -> inst.start();
                case "stop", "st" -> inst.stop();
            }
        }
	}

}

