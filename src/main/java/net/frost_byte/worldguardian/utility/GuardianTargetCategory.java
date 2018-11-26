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
				names = guardian.playerNameTargets;
				break;
			case NPC:
				names = guardian.npcNameTargets;
				break;
			case ENTITY_NAME:
				names = guardian.entityNameTargets;
				break;
			case HELD_ITEM:
				names = guardian.heldItemTargets;
				break;
			case GROUP_NAME:
				if (guardian.groupTargets.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
						prefixBad +
						"Already tracking that name target!"
					);
				}
				else
				{
					guardian.groupTargets.add(searchString);
					plugin.sendChannelMessage(sender,
						prefixGood +
						"Tracking new target!"
					);
				}
				return;
			case EVENT:
				names = guardian.eventTargets;
				break;
			case OTHER:
				doRegex = false;
				names = guardian.otherTargets;
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
				names = guardian.playerNameTargets;
				break;
			case NPC:
				names = guardian.npcNameTargets;
				break;
			case ENTITY_NAME:
				names = guardian.entityNameTargets;
				break;
			case HELD_ITEM:
				names = guardian.heldItemTargets;
				break;
			case GROUP_NAME:
				if (guardian.groupTargets.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
						prefixBad +
						"Already tracking that name target!"
					);
				}
				else
				{
					guardian.groupTargets.add(searchString);
					plugin.sendChannelMessage(sender,
						prefixGood +
						"Tracking new target!"
					);
				}
				return;
			case EVENT:
				names = guardian.eventTargets;
				break;
			case OTHER:
				doRegex = false;
				names = guardian.otherTargets;
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
				names = guardian.playerNameIgnores;
				break;
			case NPC:
				names = guardian.npcNameIgnores;
				break;
			case ENTITY_NAME:
				names = guardian.entityNameIgnores;
				break;
			case HELD_ITEM:
				names = guardian.heldItemIgnores;
				break;
			case GROUP_NAME:
				if (guardian.groupIgnores.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
						prefixBad +
						"Already ignoring that name target!"
					);
				}
				else
				{
					guardian.groupIgnores.add(searchString);
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
				names = guardian.otherIgnores;
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
				names = guardian.playerNameIgnores;
				break;
			case NPC:
				names = guardian.npcNameIgnores;
				break;
			case ENTITY_NAME:
				names = guardian.entityNameIgnores;
				break;
			case HELD_ITEM:
				names = guardian.heldItemIgnores;
				break;
			case GROUP_NAME:
				if (guardian.groupIgnores.contains(searchString))
				{
					plugin.sendChannelMessage(sender,
							prefixBad +
									"Already tracking that name target!"
					);
				}
				else
				{
					guardian.groupIgnores.add(searchString);
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
				names = guardian.otherIgnores;
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
				names = trait.playerNameTargets;
				break;
			case NPC:
				names = trait.npcNameTargets;
				break;
			case ENTITY_NAME:
				names = trait.entityNameTargets;
				break;
			case HELD_ITEM:
				names = trait.heldItemTargets;
				break;
			case GROUP_NAME:
				names = trait.groupTargets;
				break;
			case EVENT:
				names = trait.eventTargets;
				break;
			default:
				names = trait.otherTargets;
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
