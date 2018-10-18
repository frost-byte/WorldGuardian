package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.GuardianTarget;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import net.frost_byte.worldguardian.utility.GuardianTargetCategory;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.*;
import static net.frost_byte.worldguardian.utility.GuardianTargetCategory.*;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("guardian|guard|wgd")
public class GuardianTargetCommand extends BaseCommand
{
	@Inject
		private WorldGuardianPlugin plugin;

	@Description("Add a target for a guardian")
	@Syntax("<target>")
	@Subcommand("add")
	@CommandAlias("gat")
	@CommandCompletion("@targets")
	@CommandPermission("guardian.addtarget")
	public void addByTarget(Player sender, GuardianTarget target)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian != null && guardian.targets.add(target.getName()))
		{
			plugin.sendChannelMessage(sender, prefixGood + "Target added!");
		}
		else
		{
			plugin.sendChannelMessage(sender, prefixBad + "Target already added!");
		}
	}

	@SuppressWarnings("UnusedAssignment")
	@Description("Add a target for a guardian using a category and a search string")
	@Syntax("<targetCategory> <search>")
	@Subcommand("add_category")
	@CommandAlias("gatc")
	@CommandCompletion("@target_categories @nothing")
	@CommandPermission("guardian.addtarget")
	public void addByCategorySearch(Player sender, GuardianTargetCategory category, String searchString)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (category != null && searchString != null && !searchString.isEmpty())
			addTarget(sender, category, guardian, searchString);
	}

	@Description("Remove a target for a guardian")
	@Syntax("<target>")
	@Subcommand("remove|rem|del")
	@CommandAlias("grt")
	@CommandCompletion("@targets")
	@CommandPermission("guardian.removetarget")
	public void removeByTarget(Player sender, GuardianTarget target)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (!guardian.targets.remove(target.getName()))
		{
			plugin.sendChannelMessage(sender, 
				prefixBad +
				"Not tracking that target!"
			);
		}
		else
		{
			plugin.sendChannelMessage(sender, 
				prefixGood +
				"No longer tracking that target!"
			);
		}
	}

	@SuppressWarnings("UnusedAssignment")
	@Description("Remove a target for a guardian using a category and a search string")
	@Syntax("<targetCategory> <search>")
	@Subcommand("rem_category")
	@CommandAlias("grtc")
	@CommandCompletion("@targets @target_categories @nothing")
	@CommandPermission("guardian.removetarget")
	public void removeByCategorySearch(Player sender, GuardianTargetCategory category, String searchString)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (category != null && searchString != null && !searchString.isEmpty())
		{
			removeTarget(
				sender,
				category,
				guardian,
				searchString
			);

		}
	}

	@Description("Add a target to the ignore list for a guardian")
	@Syntax("<target>")
	@Subcommand("iadd|ignoreadd|addignore|addi")
	@CommandCompletion("@targets")
	@CommandPermission("guardian.addignore")
	public void addIgnoreByTarget(Player sender, GuardianTarget target)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (guardian.ignores.add(target.getName()))
		{
			plugin.sendChannelMessage(sender, 
				prefixGood +
				"Target added!"
			);
		}
		else
		{
			plugin.sendChannelMessage(sender, 
				prefixBad +
				"Target already added!"
			);
		}
	}

	@SuppressWarnings("UnusedAssignment")
	@Description("Add a target for to the ignore list for a guardian using a category and search string")
	@Syntax("<targetCategory> <search>")
	@Subcommand("iadd_category|addi_category")
	@CommandCompletion("@targets @target_categories @nothing")
	@CommandPermission("guardian.addignore")
	public void addIgnoreByCategorySearch(Player sender, GuardianTargetCategory category, String searchString)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (category != null && searchString != null && !searchString.isEmpty())
		{
			addIgnoreTarget(
				sender,
				category,
				guardian,
				searchString
			);
		}
	}

	@Description("Remove a target from the ignore list for a guardian")
	@Syntax("<target>")
	@Subcommand("iremove|irem|idel")
	@CommandCompletion("@targets")
	@CommandPermission("guardian.removeignore")
	public void removeIgnoreByTarget(Player sender, GuardianTarget target)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (!guardian.ignores.remove(target.getName()))
		{
			plugin.sendChannelMessage(sender, 
				prefixBad +
				"Not tracking that target!"
			);
		}
		else
		{
			plugin.sendChannelMessage(sender, 
				prefixGood +
				"No longer tracking that target!"
			);
		}
	}

	@SuppressWarnings("UnusedAssignment")
	@Description("Remove a target from the ignore list for a guardian using a category and a search string")
	@Syntax("<targetCategory> <search>")
	@Subcommand("ignore_rem_category")
	@CommandCompletion("@targets @target_categories @nothing")
	@CommandPermission("guardian.removeignore")
	public void removeIgnoreByCategorySearch(Player sender, GuardianTargetCategory category, String searchString)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (category != null && searchString != null && !searchString.isEmpty())
		{
			removeIgnoreTarget(
					sender,
					category,
					guardian,
					searchString
			);

		}
	}


}
