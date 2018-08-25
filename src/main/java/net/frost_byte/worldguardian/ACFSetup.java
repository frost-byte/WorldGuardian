package net.frost_byte.worldguardian;

import co.aikar.commands.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.utility.EnumUtil;
import net.frost_byte.worldguardian.utility.GuardianTargetCategory;
import net.frost_byte.worldguardian.utility.GuardianTargetType;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

public class ACFSetup
{
	private final BukkitCommandManager commandManager;
	private final Logger logger;

	@Inject
	public ACFSetup(BukkitCommandManager commandManager,
			@Named("Logger") Logger logger)
	{
		this.commandManager = commandManager;
		this.logger = logger;
	}

	/**
	 * Defines Completions which can be referenced by any Command registered with ACF,
	 * which allows the user to use the tab key to provide suggestions or complete
	 * partially typed input
	 */
	public void registerCommandCompletions()
	{
		logger.info("ACF Setup: Registering command completions");

		CommandCompletions<BukkitCommandCompletionContext> comp = commandManager.getCommandCompletions();


//		comp.registerCompletion("worlds", c -> WorldManager.getWorldNames());
		comp.registerCompletion("stack_values", (c) -> IntStream.rangeClosed(1, 64)
				.boxed()
				.map(Object::toString)
				.collect(Collectors.toList()));

		comp.registerCompletion("targets", (c) -> getValidTargets());
		comp.registerCompletion("target_names", (c) -> getValidTargets());
		comp.registerCompletion("target_categories", (c) ->
			Stream.of(GuardianTargetCategory.values())
				.map(GuardianTargetCategory::name)
				.collect(Collectors.toList())
		);
			//EnumUtil.getAll(GuardianTargetCategory.class));
	}

	/**
	 * Provides ACF with contexts for converting string input to commands into
	 * object instances, based upon various annotations, Flags, for example.
	 * Compares the parameters defined for a command and converts the input strings
	 * provided by the command issuer into a relevant object instance.
	 * <p>
	 * Specifying an IssuerAware context allows the CommandSender or initiator of the
	 * command to be treated as an instance of a specific object.
	 */
	public void registerCommandContexts()
	{
		logger.info("ACF Setup: Registering command contexts");

		CommandContexts<BukkitCommandExecutionContext> con = commandManager.getCommandContexts();

		con.registerContext(
			GuardianTargetType.class,
			c -> {
				String targetTypeName = c.popFirstArg();
				logger.info("ACFSetup.registerContext.GuardianTargetType: name: " + targetTypeName);
				GuardianTargetType targetType;

				try
				{
					targetType = GuardianTargetType.valueOf(c.popFirstArg());
				}
				catch (IllegalArgumentException ex)
				{
					throw new InvalidCommandArgument("Invalid target type!");
				}

				return targetType;
			}
		);

		con.registerContext(
				GuardianTargetCategory.class,
				c -> {
					String name = c.popFirstArg();
					logger.info("ACFSetup.registerContext.GuardianTargetCategory: name: " + name);
					GuardianTargetCategory targetCategory;

					try
					{
						targetCategory = GuardianTargetCategory.valueOf(c.popFirstArg());
					}
					catch (IllegalArgumentException ex)
					{
						throw new InvalidCommandArgument("Invalid target type!");
					}

					return targetCategory;
				}
		);

		con.registerContext(
				GuardianTarget.class,
				c -> {
					String targetName = c.popFirstArg();
					logger.info("ACFSetup.registerContext.GuardianTarget: name: " + targetName);
					GuardianTarget target;

					try
					{
						target = forName(targetName);
					}
					catch (IllegalArgumentException ex)
					{
						throw new InvalidCommandArgument("Invalid target type!");
					}

					return target;
				}
		);

	}

	/**
	 * Register Replacements with ACF
	 * Defines an alias for a widely used permission or string, that can be referenced when
	 * defining the permissions for a command/subcommands registered with ACF.
	 * The obvious benefit of using this, instead of just referring to them literally in all places,
	 * is to use the "%player" replacement instead of having to change "ve.player" everywhere. This way you change it
	 * in one spot.
	 */
	public void registerCommandReplacements()
	{
		// logger.info("ACF Setup: Registering command replacements");

		// CommandReplacements rep = commandManager.getCommandReplacements();

		// Permissions Replacements
		//		rep.addReplacement("%player","ve.player");
		//		rep.addReplacement("%moderator","ve.mod");
		//		rep.addReplacement("%admin","ve.admin");
	}

}
