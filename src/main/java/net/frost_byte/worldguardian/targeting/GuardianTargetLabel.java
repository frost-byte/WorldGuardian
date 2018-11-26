package net.frost_byte.worldguardian.targeting;

import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@SuppressWarnings( { "unused", "WeakerAccess" }) public class GuardianTargetLabel
{
	/**
	 * The label prefix.
	 */
	public String prefix;

	/**
	 * The target value.
	 */
	public String value;

	/**
	 * Whether this label is regex based.
	 */
	public boolean isRegex;

	/**
	 * Prefixes that need regex validation.
	 */
	public static HashSet<String> regexPrefixes = new HashSet<>(
		Arrays.asList("player", "npc", "entityname", "helditem"));

	/**
	 * Helper to ensure code won't be optimized away (Java isn't likely to do this anyway, but just in case).
	 */
	public static long ignoreMe = 0;

	/**
	 * Returns whether the RegEx is valid (or the target is not a RegEx target).
	 */
	public boolean isValidRegex() {
		try {
			if (isRegex && "Guardian".matches(value)) {
				ignoreMe++;
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns whether the target is valid.
	 * True for all prefixed targets.
	 */
	public boolean isValidTarget()
	{
		return prefix != null || GuardianTargetUtil.forName(value) != null;
	}

	/**
	 * Gets the target collection from a list set for this label.
	 */
	public Collection<String> getTargetsList(GuardianTargetList listSet) {
		if (prefix == null) {
			return listSet.targets;
		}
		else if (prefix.equals("player")) {
			return listSet.byPlayerName;
		}
		else if (prefix.equals("npc")) {
			return listSet.byNpcName;
		}
		else if (prefix.equals("entityname")) {
			return listSet.byEntityName;
		}
		else if (prefix.equals("helditem")) {
			return listSet.byHeldItem;
		}
		else if (prefix.equals("group")) {
			return listSet.byGroup;
		}
		else if (prefix.equals("event")) {
			return listSet.byEvent;
		}
		else {
			return listSet.byOther;
		}
	}

	private static GuardianTargetList helperList = new GuardianTargetList();

	/**
	 * Gets the addable string for this label.
	 */
	public String addable() {
		if (getTargetsList(helperList) == helperList.byOther) {
			return prefix + ":" + value;
		}
		return value;
	}

	/**
	 * Adds this target label to a list set.
	 */
	public boolean addToList(GuardianTargetList listSet) {
		Collection<String> list = getTargetsList(listSet);
		String addable = addable();
		if (list.contains(addable)) {
			return false;
		}
		getTargetsList(listSet).add(addable());
		if (list == listSet.targets) {
			listSet.recalculateTargetsCache();
		}
		return true;
	}

	/**
	 * Removes this target label from a list set.
	 */
	public boolean removeFromList(GuardianTargetList listSet) {
		Collection<String> list = getTargetsList(listSet);
		String addable = addable();
		if (!list.contains(addable)) {
			return false;
		}
		getTargetsList(listSet).remove(addable());
		if (list == listSet.targets) {
			listSet.recalculateTargetsCache();
		}
		return true;
	}

	/**
	 * Constructs the target label instance.
	 */
	public GuardianTargetLabel(String label) {
		int index = label.indexOf(':');
		if (index >= 0) {
			prefix = label.substring(0, index).toLowerCase();
			value = label.substring(index + 1);
			isRegex = regexPrefixes.contains(prefix);
			value = ChatColor.translateAlternateColorCodes('&', value);
		}
		else {
			value = label.toUpperCase();
		}
	}	
}
