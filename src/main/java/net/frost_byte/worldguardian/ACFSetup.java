package net.frost_byte.worldguardian;

import co.aikar.commands.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

		// Converts the input string to an integer
		con.registerContext(int.class, c -> {
			int number;

			try
			{
				number = Integer.parseInt(c.popFirstArg());
			}
			catch (NumberFormatException e)
			{
				throw new InvalidCommandArgument("Invalid number format.");
			}

			return number;
		});


//		con.registerContext(
//				World.class,
//				c -> {
//					String worldName = c.popFirstArg();
//					logger.info("ACFSetup.registerContext.World: name: " + worldName);
//					if (worldName == null || worldName.isEmpty())
//					{
//						worldName = WorldManager.Survival_World_Name;
//					}
//
//					return worldManager.getWorldByName(worldName);
//				}
//		);

//		// Converts a string representing the name of an player that is offline
//		// into an OfflinePlayer instance
//		con.registerContext(OfflinePlayer.class, c -> {
//			OfflinePlayer playerTarget;
//			CommandSender sender = c.getSender();
//
//			if (c.hasFlag("other"))
//			{
//				String rawUsername = c.popFirstArg();
//
//				if (playerManager.hasPlayedBefore(rawUsername))
//				{
//					playerTarget = playerManager.getOfflinePlayerFromRegisteredUsername(rawUsername);
//				}
//				else
//				{
//					throw new InvalidCommandArgument("That player hasn't joined the server.");
//				}
//			}
//			else
//			{
//				if (sender instanceof OfflinePlayer)
//				{
//					playerTarget = (OfflinePlayer) sender;
//				}
//				else
//				{
//					throw new InvalidCommandArgument("The command executor must be an offline player.");
//				}
//			}
//
//			return playerTarget;
//		});

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
