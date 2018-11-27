package net.frost_byte.worldguardian.targeting;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.util.DataKey;
import net.frost_byte.worldguardian.GuardianIntegration;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import net.frost_byte.worldguardian.utility.GuardianTargetType;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;

import static net.frost_byte.worldguardian.utility.GuardianUtilities.*;

@SuppressWarnings("WeakerAccess")
public class GuardianTargetList
{
	/**
	 * Returns whether an entity is targeted by this target list.
	 */
	@SuppressWarnings("unused")
	public boolean isTarget(LivingEntity entity) {
		checkRecalculateTargetsCache();
		if (entity.getEquipment() != null && entity.getEquipment().getItemInMainHand() != null
			&& isRegexTargeted(entity.getEquipment().getItemInMainHand().getType().name(), byHeldItem)) {
			return true;
		}

		for (GuardianIntegration integration : WorldGuardianPlugin.integrations) {
			for (String text : byOther) {
				if (integration.isTarget(entity, text)) {
					return true;
				}
			}
		}
		if (entity.hasMetadata("NPC")) {
			return targetsProcessed.contains(GuardianTargetUtil.getTarget(GuardianTargetType.NPCS)) ||
				isRegexTargeted(CitizensAPI.getNPCRegistry().getNPC(entity).getName(), byNpcName);
		}

		if (entity instanceof Player)
		{
			WorldGuardianPlugin plugin = GuardianTargetUtil.getPlugin();

			if (isRegexTargeted(entity.getName(), byPlayerName)) {
				return true;
			}

			Player player = (Player) entity;

			for (String group : byGroup )
			{
				if (player.hasPermission("group." + group))
					return true;
			}
		}
		else if (isRegexTargeted(entity.getCustomName() == null ? entity.getType().name() : entity.getCustomName(), byEntityName)) {
			return true;
		}
		HashSet<GuardianTarget> possible = GuardianTargetUtil.findTargetByEntityType(entity.getType());
		if (possible == null || possible.isEmpty())
			return false;

		for (GuardianTarget poss : possible) {
			if (targetsProcessed.contains(poss)) {
				return true;
			}
		}
		return false;
	}

	public void fillListFromKey(ArrayList<String> list, DataKey key) {
		for (DataKey listEntry : key.getSubKeys()) {
			list.add(listEntry.getRaw("").toString());
		}
	}

	@SuppressWarnings("unused")
	public void updateOld(DataKey key, String name) {
		switch (name)
		{
			case "playerName":
				fillListFromKey(
					byPlayerName,
					key
				);
				break;
			case "npcName":
				fillListFromKey(
					byNpcName,
					key
				);
				break;
			case "entityName":
				fillListFromKey(
					byEntityName,
					key
				);
				break;
			case "heldItem":
				fillListFromKey(
					byHeldItem,
					key
				);
				break;
			case "group":
				fillListFromKey(
					byGroup,
					key
				);
				break;
			case "event":
				fillListFromKey(
					byEvent,
					key
				);
				break;
			case "other":
				fillListFromKey(
					byOther,
					key
				);
				break;
		}
	}

	/**
	 * Cache of target objects.
	 */
	public HashSet<GuardianTarget> targetsProcessed = new HashSet<>();

	/**
	 * Checks if the targets cache ('targetsProcessed') needs to be reprocessed, and refills it if so.
	 */
	public void checkRecalculateTargetsCache() {
		if (targets.size() != targetsProcessed.size()) {
			recalculateTargetsCache();
		}
	}

	/**
	 * Fills the cache 'targetsProcessed' set then uses that set to deduplicate the source 'targets' set.
	 */
	public void recalculateTargetsCache() {
		targetsProcessed.clear();
		for (String target : targets) {
			targetsProcessed.add(GuardianTargetUtil.forName(target));
		}
		targets.clear();
		for (GuardianTarget target : targetsProcessed) {
			targets.add(target.getName());
		}
	}

	/**
	 * List of target-type-based targets.
	 */
	@Persist("targets")
	public HashSet<String> targets = new HashSet<>();

	/**
	 * List of player-name-based targets.
	 */
	@Persist("byPlayerName")
	public ArrayList<String> byPlayerName = new ArrayList<>();

	/**
	 * List of NPC-name-based targets.
	 */
	@Persist("byNpcName")
	public ArrayList<String> byNpcName = new ArrayList<>();

	/**
	 * List of entity-name-based targets.
	 */
	@Persist("byEntityName")
	public ArrayList<String> byEntityName = new ArrayList<>();

	/**
	 * List of held-item-based targets.
	 */
	@Persist("byHeldItem")
	public ArrayList<String> byHeldItem = new ArrayList<>();

	/**
	 * List of scoreboard-group-based targets.
	 */
	@Persist("byGroup")
	public ArrayList<String> byGroup = new ArrayList<>();

	/**
	 * List of event-based targets.
	 */
	@Persist("byEvent")
	public ArrayList<String> byEvent = new ArrayList<>();

	/**
	 * List of targets not handled by any other target type list.
	 */
	@Persist("byOther")
	public ArrayList<String> byOther = new ArrayList<>();
}
