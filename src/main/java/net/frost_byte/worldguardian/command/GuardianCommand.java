package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.SentryImport;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.*;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.ColorBasic;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.debugMe;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.prefixGood;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("guardian|guard|wgd")
public class GuardianCommand extends BaseCommand
{
	@Inject
	@Named("WorldGuardian")
	private WorldGuardianPlugin plugin;

	@Inject private SentryImport sentryImport;

	@HelpCommand
	public void doHelp(CommandSender sender, CommandHelp help)
	{
		help.showHelp();
		sender.sendMessage(getValidTargetsMessage());
	}

	@Description("Import NPCs from the Sentry plugin.")
	@Subcommand("import")
	@CommandPermission("guardian.sentryimport")
	public void importCommand(Player sender)
	{
		if (sentryImport == null || Bukkit.getServer().getPluginManager().getPlugin("Sentry") == null)
		{
			sender.sendMessage(prefixBad + "Sentry plugin must be installed to perform import!");
		}
		else
		{
			sender.sendMessage(prefixGood + "Converting all NPCs from Sentry to Guardian...");

			int imported = sentryImport.PerformImport();
			sender.sendMessage(prefixGood + "Imported " + imported
				+ " Sentry NPCs. You may now restart and remove the Sentry plugin.");
		}
	}

	@Description("Toggle debugging for the plugin")
	@Subcommand("debug")
	@CommandPermission("guardian.debug")
	public void setDebug(Player sender)
	{
		debugMe = !debugMe;
		sender.sendMessage(prefixGood + "Toggled: " + debugMe + "!");
	}

	@Description("Get information about the guardian's targets.")
	@Subcommand("target_info|targetinfo")
	@CommandPermission("guardian.info")
	public void getTargetsInfo(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		sender.sendMessage(
			prefixGood + ChatColor.RESET + guardian.getNPC().getFullName() + ColorBasic + ": owned by " + ChatColor.RESET
				+ getOwner(guardian.getNPC()));

		sender.sendMessage(prefixGood + "Targets: " + ChatColor.AQUA + getTargetString(guardian.targets));

		sender.sendMessage(
			prefixGood + "Player Name Targets: " + ChatColor.AQUA + getNameTargetString(guardian.playerNameTargets));

		sender.sendMessage(
			prefixGood + "NPC Name Targets: " + ChatColor.AQUA + getNameTargetString(guardian.npcNameTargets));

		sender.sendMessage(
			prefixGood + "Entity Name Targets: " + ChatColor.AQUA + getNameTargetString(guardian.entityNameTargets));

		sender.sendMessage(
			prefixGood + "Held Item Targets: " + ChatColor.AQUA + getNameTargetString(guardian.heldItemTargets));

		sender
			.sendMessage(prefixGood + "Group Targets: " + ChatColor.AQUA + getNameTargetString(guardian.groupTargets));

		sender
			.sendMessage(prefixGood + "Event Targets: " + ChatColor.AQUA + getNameTargetString(guardian.eventTargets));

		sender
			.sendMessage(prefixGood + "Other Targets: " + ChatColor.AQUA + getNameTargetString(guardian.otherTargets));

		sender.sendMessage(prefixGood + "Ignored Targets: " + ChatColor.AQUA + getTargetString(guardian.ignores));

		sender.sendMessage(prefixGood + "Ignored Player Name Targets: " + ChatColor.AQUA + getNameTargetString(guardian.playerNameIgnores));

		sender.sendMessage(
			prefixGood + "Ignored NPC Name Targets: " + ChatColor.AQUA + getNameTargetString(guardian.npcNameIgnores));

		sender.sendMessage(prefixGood + "Ignored Entity Name Targets: " + ChatColor.AQUA + getNameTargetString(guardian.entityNameIgnores));

		sender.sendMessage(prefixGood + "Ignored Held Item Targets: " + ChatColor.AQUA + getNameTargetString(guardian.heldItemIgnores));

		sender.sendMessage(
			prefixGood + "Ignored Group Targets: " + ChatColor.AQUA + getNameTargetString(guardian.groupIgnores));

		sender.sendMessage(
			prefixGood + "Ignored Other Targets: " + ChatColor.AQUA + getNameTargetString(guardian.otherIgnores));
	}

	@Description("Get information about the guardian.")
	@Subcommand("info")
	@CommandPermission("guardian.info")
	public void getInfo(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		sender.sendMessage(
			prefixGood + ChatColor.RESET + guardian.getNPC().getFullName() + ColorBasic + ": owned by " + ChatColor.RESET
				+ getOwner(guardian.getNPC()) + (guardian.getGuarding() == null ?
				"" :
				ColorBasic + ", guarding: " + ChatColor.RESET + Bukkit.getOfflinePlayer(guardian.getGuarding()).getName()));
		sender.sendMessage(prefixGood + "Damage: " + ChatColor.AQUA + guardian.damage);
		sender.sendMessage(prefixGood + "Armor: " + ChatColor.AQUA + guardian.armor);
		sender.sendMessage(prefixGood + "Health: " + ChatColor.AQUA + (guardian.getNPC().isSpawned() ?
			guardian.getLivingEntity().getHealth() + "/" :
			"") + guardian.health);
		sender.sendMessage(prefixGood + "Range: " + ChatColor.AQUA + guardian.range);
		sender.sendMessage(prefixGood + "Attack Rate: " + ChatColor.AQUA + guardian.attackRate);
		sender.sendMessage(prefixGood + "Ranged Attack Rate: " + ChatColor.AQUA + guardian.attackRateRanged);
		sender.sendMessage(prefixGood + "Heal Rate: " + ChatColor.AQUA + guardian.healRate);
		sender.sendMessage(prefixGood + "Respawn Time: " + ChatColor.AQUA + guardian.respawnTime);
		sender.sendMessage(prefixGood + "Accuracy: " + ChatColor.AQUA + guardian.accuracy);
		sender.sendMessage(prefixGood + "Reach: " + ChatColor.AQUA + guardian.reach);
		sender.sendMessage(prefixGood + "Invincibility Enabled: " + ChatColor.AQUA + guardian.invincible);
		sender.sendMessage(prefixGood + "Fightback Enabled: " + ChatColor.AQUA + guardian.fightback);
		sender.sendMessage(prefixGood + "Ranged Chasing Enabled: " + ChatColor.AQUA + guardian.rangedChase);
		sender.sendMessage(prefixGood + "Close-Quarters Chasing Enabled: " + ChatColor.AQUA + guardian.closeChase);
		sender.sendMessage(prefixGood + "Maximum chase range: " + ChatColor.AQUA + guardian.chaseRange);
		sender.sendMessage(prefixGood + "Safe-Shot Enabled: " + ChatColor.AQUA + guardian.safeShot);
		sender.sendMessage(prefixGood + "Enemy-Drops Enabled: " + ChatColor.AQUA + guardian.enemyDrops);
		sender.sendMessage(prefixGood + "Autoswitch Enabled: " + ChatColor.AQUA + guardian.autoswitch);
		sender.sendMessage(prefixGood + "Realistic Targetting Enabled: " + ChatColor.AQUA + guardian.realistic);
		sender.sendMessage(prefixGood + "Squad: " + ChatColor.AQUA + (guardian.squad == null ? "None" : guardian.squad));
	}

	@Description("View stats for the guardian.")
	@Subcommand("stats")
	@CommandPermission("guardian.info")
	public void getStats(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		sender.sendMessage(
			prefixGood + ChatColor.RESET + guardian.getNPC().getFullName() + ColorBasic + ": owned by " + ChatColor.RESET
				+ getOwner(guardian.getNPC()));
		sender.sendMessage(prefixGood + "Arrows fired: " + ChatColor.AQUA + guardian.stats_arrowsFired);
		sender.sendMessage(prefixGood + "Potions thrown: " + ChatColor.AQUA + guardian.stats_potionsThrown);
		sender.sendMessage(prefixGood + "Fireballs launched: " + ChatColor.AQUA + guardian.stats_fireballsFired);
		sender.sendMessage(prefixGood + "Snowballs thrown: " + ChatColor.AQUA + guardian.stats_snowballsThrown);
		sender.sendMessage(prefixGood + "Eggs thrown: " + ChatColor.AQUA + guardian.stats_eggsThrown);
		sender.sendMessage(prefixGood + "Pearls used: " + ChatColor.AQUA + guardian.stats_pearlsUsed);
		sender.sendMessage(prefixGood + "Skulls thrown: " + ChatColor.AQUA + guardian.stats_skullsThrown);
		sender.sendMessage(prefixGood + "Punches: " + ChatColor.AQUA + guardian.stats_punches);
		sender.sendMessage(prefixGood + "Times spawned: " + ChatColor.AQUA + guardian.stats_timesSpawned);
		sender.sendMessage(prefixGood + "Damage Given: " + ChatColor.AQUA + guardian.stats_damageGiven);
		sender.sendMessage(prefixGood + "Damage Taken: " + ChatColor.AQUA + guardian.stats_damageTaken);
		sender.sendMessage(prefixGood + "Minutes spawned: " + ChatColor.AQUA + guardian.stats_ticksSpawned / (20.0 * 60.0));
	}
}
