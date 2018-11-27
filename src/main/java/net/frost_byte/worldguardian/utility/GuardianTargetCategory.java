package net.frost_byte.worldguardian.utility;

import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.*;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
public enum GuardianTargetCategory
{
	PLAYER("player", "REGEX"),
	NPC("npc", "REGEX"),
	ENTITY_NAME("entityname", "REGEX"),
	HELD_ITEM("helditem", "REGEX"),
	GROUP_NAME("groupname", "REGEX"),
	EVENT("event", "EVENT"),
	OTHER("other", "REGEX");

	private String categoryName;
	private String searchType;

	GuardianTargetCategory(String categoryName, String searchType)
	{
		this.categoryName = categoryName;
		this.searchType = searchType;
	}

	public String getCategoryName() { return categoryName; }
	public String getSearchType() { return searchType; }
	public static GuardianTargetCategory fromInteger(int e)
	{
		return Arrays.stream(GuardianTargetCategory.values())
				.filter(category -> category.ordinal() == e)
				.findAny()
				.orElse(null);
	}

	@SuppressWarnings("UnusedAssignment")
	public static void addTarget(
			Player sender,
			GuardianTargetCategory category,
			GuardianTrait guardian,
			String searchString
	){
		long ignoreMe = 0;
		List<String> names = null;
		boolean doRegex = true;
		searchString = ChatColor.translateAlternateColorCodes('&', searchString);

		if (debugMe)
		{
			String formatString = "(category, guardian, search) : (%s, %s, %s)";
			String debugInfo = String.format(
				formatString,
				category.name(),
				guardian.getName(),
				searchString
			);
			getPlugin().getLogger().info("GuardianTargetCategory.addTarget: " + debugInfo);
		}
		WorldGuardianPlugin plugin = getPlugin();

		switch (category)
		{
			case PLAYER:
				names = guardian.allTargets.byPlayerName;
				break;
			case NPC:
				names = guardian.allTargets.byNpcName;
				break;
			case ENTITY_NAME:
				names = guardian.allTargets.byEntityName;
				break;
			case HELD_ITEM:
				names = guardian.allTargets.byHeldItem;
				break;
			case GROUP_NAME:
				if (guardian.allTargets.byGroup.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
						prefixBad +
						"Already tracking that name target!"
					);
				}
				else
				{
					guardian.allTargets.byGroup.add(searchString);
					plugin.sendChannelMessage(sender,
						prefixGood +
						"Tracking new target!"
					);
				}
				return;
			case EVENT:
				names = guardian.allTargets.byEvent;
				break;
			case OTHER:
				doRegex = false;
				names = guardian.allTargets.byOther;
				searchString = category.categoryName + ":" + searchString;
				break;
		}

		try {
			if (doRegex && "Guardian".matches(searchString)) {
				ignoreMe++;
			}
		}
		catch (Exception e) {
			names = null;
			plugin.sendChannelMessage(sender,
				prefixBad +
				"Bad regular expression!"
			);
		}

		if (names != null) {

			if (names.contains(searchString)) {
				plugin.sendChannelMessage(sender,
					prefixBad +
					"Already tracking that target!"
				);
			}
			else
			{
				names.add(searchString);
				plugin.sendChannelMessage(sender,
					prefixGood +
					"Tracking new target!"
				);
			}
		}
		else
		{
			plugin.sendChannelMessage(sender,
				prefixBad +
				"Invalid target!"
			);

			plugin.sendChannelMessage(sender, getValidTargetsMessage());
		}
	}

	@SuppressWarnings("UnusedAssignment")
	public static void removeTarget(
		Player sender,
		GuardianTargetCategory category,
		GuardianTrait guardian,
		String searchString
	){
		long ignoreMe = 0;
		List<String> names = null;
		boolean doRegex = true;
		searchString = ChatColor.translateAlternateColorCodes('&', searchString);

		WorldGuardianPlugin plugin = getPlugin();
		switch (category)
		{
			case PLAYER:
				names = guardian.allTargets.byPlayerName;
				break;
			case NPC:
				names = guardian.allTargets.byNpcName;
				break;
			case ENTITY_NAME:
				names = guardian.allTargets.byEntityName;
				break;
			case HELD_ITEM:
				names = guardian.allTargets.byHeldItem;
				break;
			case GROUP_NAME:
				if (guardian.allTargets.byGroup.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
						prefixBad +
						"Already tracking that name target!"
					);
				}
				else
				{
					guardian.allTargets.byGroup.add(searchString);
					plugin.sendChannelMessage(sender,
						prefixGood +
						"Tracking new target!"
					);
				}
				return;
			case EVENT:
				names = guardian.allTargets.byEvent;
				break;
			case OTHER:
				doRegex = false;
				names = guardian.allTargets.byOther;
				searchString = category.categoryName + ":" + searchString;
				break;
		}

		try {
			if (doRegex && "Guardian".matches(searchString)) {
				ignoreMe++;
			}
		}
		catch (Exception e) {
			names = null;
			plugin.sendChannelMessage(sender,
				prefixBad +
				"Bad regular expression!"
			);
		}

		if (names != null) {

			if (!names.remove(searchString)) {
				plugin.sendChannelMessage(sender,
					prefixBad +
					"Not tracking that target!"
				);
			}
			else
			{
				names.add(searchString);
				plugin.sendChannelMessage(sender,
					prefixGood +
					"No longer tracking that target!"
				);
			}
		}
		else
		{
			plugin.sendChannelMessage(
				sender,
				prefixBad + "Invalid target!",
				prefixGood + "See /guardian to view valid targets!"
			);
		}
	}

	@SuppressWarnings("UnusedAssignment")
	public static void addIgnoreTarget(
			Player sender,
			GuardianTargetCategory category,
			GuardianTrait guardian,
			String searchString
	){
		long ignoreMe = 0;
		List<String> names = null;
		boolean doRegex = true;
		searchString = ChatColor.translateAlternateColorCodes('&', searchString);

		WorldGuardianPlugin plugin = getPlugin();
		switch (category)
		{
			case PLAYER:
				names = guardian.allIgnores.byPlayerName;
				break;
			case NPC:
				names = guardian.allIgnores.byNpcName;
				break;
			case ENTITY_NAME:
				names = guardian.allIgnores.byEntityName;
				break;
			case HELD_ITEM:
				names = guardian.allIgnores.byHeldItem;
				break;
			case GROUP_NAME:
				if (guardian.allIgnores.byGroup.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
						prefixBad +
						"Already ignoring that name target!"
					);
				}
				else
				{
					guardian.allIgnores.byGroup.add(searchString);
					plugin.sendChannelMessage(sender,
						prefixGood +
						"Ignoring new target!"
					);
				}
				return;
			case EVENT:
				return;
			case OTHER:
				doRegex = false;
				names = guardian.allIgnores.byOther;
				searchString = category.categoryName + ":" + searchString;
				break;
		}

		try {
			if (doRegex && "Guardian".matches(searchString)) {
				ignoreMe++;
			}
		}
		catch (Exception e) {
			names = null;
			plugin.sendChannelMessage(sender,
				prefixBad +
				"Bad regular expression!"
			);
		}

		if (names != null) {

			if (names.contains(searchString)) {
				plugin.sendChannelMessage(sender,
					prefixBad +
					"Already ignoring that target!"
				);
			}
			else
			{
				names.add(searchString);
				plugin.sendChannelMessage(sender,
					prefixGood +
					"Ignoring new target!"
				);
			}
		}
		else
		{
			plugin.sendChannelMessage(
				sender,
				prefixBad + "Invalid ignore target!",
				prefixGood + "See /guardian to view valid targets!"
			);
		}
	}

	@SuppressWarnings("UnusedAssignment")
	public static void removeIgnoreTarget(
			Player sender,
			GuardianTargetCategory category,
			GuardianTrait guardian,
			String searchString
	){
		long ignoreMe = 0;
		List<String> names = null;
		boolean doRegex = true;
		searchString = ChatColor.translateAlternateColorCodes('&', searchString);

		WorldGuardianPlugin plugin = getPlugin();

		switch (category)
		{
			case PLAYER:
				names = guardian.allIgnores.byPlayerName;
				break;
			case NPC:
				names = guardian.allIgnores.byNpcName;
				break;
			case ENTITY_NAME:
				names = guardian.allIgnores.byEntityName;
				break;
			case HELD_ITEM:
				names = guardian.allIgnores.byHeldItem;
				break;
			case GROUP_NAME:
				if (guardian.allIgnores.byGroup.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
							prefixBad +
									"Already tracking that name target!"
					);
				}
				else
				{
					guardian.allIgnores.byGroup.add(searchString);
					plugin.sendChannelMessage(sender,
							prefixGood +
									"Tracking new target!"
					);
				}
				return;
			case EVENT:
				return;
			case OTHER:
				doRegex = false;
				names = guardian.allIgnores.byOther;
				searchString = category.categoryName + ":" + searchString;
				break;
		}

		try {
			if (doRegex && "Guardian".matches(searchString)) {
				ignoreMe++;
			}
		}
		catch (Exception e) {
			names = null;
			plugin.sendChannelMessage(sender,
				prefixBad +
				"Bad regular expression!"
			);
		}

		if (names != null) {

			if (!names.remove(searchString)) {
				plugin.sendChannelMessage(sender,
					prefixBad +
					"Not ignoring that target!"
				);
			}
			else
			{
				names.add(searchString);
				plugin.sendChannelMessage(sender,
					prefixGood +
					"No longer ignoring that target!"
				);
			}
		}
		else
		{
			plugin.sendChannelMessage(
				sender,
				prefixBad + "Invalid ignore target!",
				prefixGood + "See /guardian to view valid targets!"
			);
		}
	}

	public static List<String> getNamesByCategory(GuardianTargetCategory category, GuardianTrait trait)
	{
		List<String> names;

		switch (category)
		{
			case PLAYER:
				names = trait.allTargets.byPlayerName;
				break;
			case NPC:
				names = trait.allTargets.byNpcName;
				break;
			case ENTITY_NAME:
				names = trait.allTargets.byEntityName;
				break;
			case HELD_ITEM:
				names = trait.allTargets.byHeldItem;
				break;
			case GROUP_NAME:
				names = trait.allTargets.byGroup;
				break;
			case EVENT:
				names = trait.allTargets.byEvent;
				break;
			default:
				names = trait.allTargets.byOther;
				break;
		}
		return names;
	}

	public static GuardianTargetCategory findByName(String name)
	{
		return Arrays.stream(GuardianTargetCategory.values())
				.filter(cat -> cat.name().equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}
}
