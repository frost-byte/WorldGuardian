package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import org.bukkit.Bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import static net.frost_byte.worldguardian.WorldGuardianPlugin.ColorBasic;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.debugMe;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.prefixGood;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("guardian|guard|wgd")
public class GuardianCommand extends BaseCommand
{
	@Inject
		private WorldGuardianPlugin plugin;



	@HelpCommand
	public void doHelp(CommandSender sender, CommandHelp help)
	{
		help.showHelp();
		getValidTargetsMessage();
	}

	@Description("Toggle debugging for the selected guardian")
	@Subcommand("debug")
	@CommandPermission("guardian.debug")
	public void toggleGuardianDebugging(Player sender)
	{
		net.citizensnpcs.api.npc.NPCSelector selector = CitizensAPI.getDefaultNPCSelector();

		if (selector != null && sender != null)
		{
			NPC selected = selector.getSelected(sender);

			if (selected != null && selected.hasTrait(GuardianTrait.class))
			{
				selected.getTrait(GuardianTrait.class).toggleDebugging();
			}
			plugin.sendChannelMessage(sender, prefixGood + "Toggled debugging for the selected guardian!");
		}
	}

	@Description("Toggle debugging for the plugin")
	@Subcommand("enable_debug")
	@CommandPermission("guardian.debug")
	public void enableDebugging(Player sender)
	{
		net.citizensnpcs.api.npc.NPCSelector selector = CitizensAPI.getDefaultNPCSelector();

		if (sender != null)
		{
			plugin.sendChannelMessage(sender, prefixGood + "Toggled: " + debugMe + "!");
			debugMe = !debugMe;
		}
	}

	@Description("Get information about the guardian's targets.")
	@Subcommand("target_info|targetinfo")
	@CommandPermission("guardian.info")
	public void getTargetsInfo(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, RED + "Could not find guardian!");
			return;
		}

		plugin.sendChannelMessage
		(
			sender, 
			prefixGood + RESET + guardian.getNPC().getFullName() + ColorBasic + ": owned by " + RESET
				+ getOwner(guardian.getNPC()),
			prefixGood + "Targets: " + AQUA + getTargetString(guardian.allTargets.targets),
			prefixGood + "Player Name Targets: " + AQUA + getNameTargetString(guardian.allTargets.byPlayerName),
			prefixGood + "NPC Name Targets: " + AQUA + getNameTargetString(guardian.allTargets.byNpcName),
			prefixGood + "Entity Name Targets: " + AQUA + getNameTargetString(guardian.allTargets.byEntityName),
			prefixGood + "Held Item Targets: " + AQUA + getNameTargetString(guardian.allTargets.byHeldItem),
			prefixGood + "Group Targets: " + AQUA + getNameTargetString(guardian.allTargets.byGroup),
			prefixGood + "Event Targets: " + AQUA + getNameTargetString(guardian.allTargets.byEvent),
			prefixGood + "Other Targets: " + AQUA + getNameTargetString(guardian.allTargets.byOther),
			prefixGood + "Ignored Targets: " + AQUA + getTargetString(guardian.allIgnores.targets),
			prefixGood + "Ignored Player Name Targets: " + AQUA + getNameTargetString(guardian.allIgnores.byPlayerName),
			prefixGood + "Ignored NPC Name Targets: " + AQUA + getNameTargetString(guardian.allIgnores.byNpcName),
			prefixGood + "Ignored Entity Name Targets: " + AQUA + getNameTargetString(guardian.allIgnores.byEntityName),
			prefixGood + "Ignored Held Item Targets: " + AQUA + getNameTargetString(guardian.allIgnores.byHeldItem),
			prefixGood + "Ignored Group Targets: " + AQUA + getNameTargetString(guardian.allIgnores.byGroup),
			prefixGood + "Ignored Other Targets: " + AQUA + getNameTargetString(guardian.allIgnores.byOther)
		);
	}

	@Description("Get information about the guardian.")
	@Subcommand("info")
	@CommandPermission("guardian.info")
	public void getInfo(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, RED + "Could not find guardian!");
			return;
		}
		plugin.sendChannelMessage
		(
			sender,
			prefixGood + RESET + guardian.getNPC().getFullName() + ColorBasic + ": owned by " + RESET +
				getOwner(guardian.getNPC()) + (guardian.getGuarding() == null ? "" : ColorBasic + ", guarding: " +
				RESET + Bukkit.getOfflinePlayer(guardian.getGuarding()).getName()),
			prefixGood + guardian.getInventoryInfo(),
			prefixGood + "Damage: " + AQUA + guardian.damage,
			prefixGood + "Armor: " + AQUA + guardian.armor,
			prefixGood + "Health: " + AQUA + (guardian.getNPC().isSpawned() ? guardian.getLivingEntity().getHealth() + "/" :
				"") + guardian.health,
			prefixGood + "Range: " + AQUA + guardian.range,
			prefixGood + "Attack Rate: " + AQUA + guardian.attackRate,
			prefixGood + "Ranged Attack Rate: " + AQUA + guardian.attackRateRanged,
			prefixGood + "Heal Rate: " + AQUA + guardian.healRate,
			prefixGood + "Respawn Time: " + AQUA + guardian.respawnTime,
			prefixGood + "Accuracy: " + AQUA + guardian.accuracy,
			prefixGood + "Reach: " + AQUA + guardian.reach,
			prefixGood + "Invincibility Enabled: " + AQUA + guardian.invincible,
			prefixGood + "Fightback Enabled: " + AQUA + guardian.fightback,
			prefixGood + "Ranged Chasing Enabled: " + AQUA + guardian.rangedChase,
			prefixGood + "Close-Quarters Chasing Enabled: " + AQUA + guardian.closeChase,
			prefixGood + "Maximum chase range: " + AQUA + guardian.chaseRange,
			prefixGood + "Safe-Shot Enabled: " + AQUA + guardian.safeShot,
			prefixGood + "Enemy-Drops Enabled: " + AQUA + guardian.enemyDrops,
			prefixGood + "Autoswitch Enabled: " + AQUA + guardian.autoswitch,
			prefixGood + "Realistic Targetting Enabled: " + AQUA + guardian.realistic,
			prefixGood + "Squad: " + AQUA + (guardian.squad == null ? "None" : guardian.squad)
		);
	}

	@Description("View stats for the guardian.")
	@Subcommand("stats")
	@CommandPermission("guardian.info")
	public void getStats(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender,RED + "Could not find guardian!");
			return;
		}

		plugin.sendChannelMessage
		(
			sender,
			prefixGood + RESET + guardian.getNPC().getFullName() + ColorBasic + ": owned by " + RESET
				+ getOwner(guardian.getNPC()),
			prefixGood + "Arrows fired: " + AQUA + guardian.stats_arrowsFired,
			prefixGood + "Potions thrown: " + AQUA + guardian.stats_potionsThrown,
			prefixGood + "Fireballs launched: " + AQUA + guardian.stats_fireballsFired,
			prefixGood + "Snowballs thrown: " + AQUA + guardian.stats_snowballsThrown,
			prefixGood + "Eggs thrown: " + AQUA + guardian.stats_eggsThrown,
			prefixGood + "Pearls used: " + AQUA + guardian.stats_pearlsUsed,
			prefixGood + "Skulls thrown: " + AQUA + guardian.stats_skullsThrown,
			prefixGood + "Punches: " + AQUA + guardian.stats_punches,
			prefixGood + "Times spawned: " + AQUA + guardian.stats_timesSpawned,
			prefixGood + "Damage Given: " + AQUA + guardian.stats_damageGiven,
			prefixGood + "Damage Taken: " + AQUA + guardian.stats_damageTaken,
			prefixGood + "Minutes spawned: " + AQUA + guardian.stats_ticksSpawned / (20.0 * 60.0)
		);
	}
}
