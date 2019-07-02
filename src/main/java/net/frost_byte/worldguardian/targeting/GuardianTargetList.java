package net.frost_byte.worldguardian.targeting;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.util.DataKey;
import net.frost_byte.worldguardian.GuardianIntegration;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import net.frost_byte.worldguardian.command.GuardianCommand;
import net.frost_byte.worldguardian.utility.GuardianTargetType;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import net.frost_byte.worldguardian.utility.GuardianUtilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.ColorBasic;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.debugMe;
import static net.frost_byte.worldguardian.utility.GuardianUtilities.*;

@SuppressWarnings("WeakerAccess")
public class GuardianTargetList
{
	/**
	 * Returns a duplicate of the target list, with all inner arrays duplicated.
	 */
	public GuardianTargetList duplicate() {
		GuardianTargetList result = new GuardianTargetList();
		result.targets.addAll(targets);
		result.byPlayerName.addAll(byPlayerName);
		result.byNpcName.addAll(byNpcName);
		result.byEntityName.addAll(byEntityName);
		result.byHeldItem.addAll(byHeldItem);
		result.byGroup.addAll(byGroup);
		result.byEvent.addAll(byEvent);
		result.byOther.addAll(byOther);
		result.byMultiple.addAll(byMultiple);
		result.byAllInOne.addAll(byAllInOne);
		return result;
	}

	/**
	 * Returns whether an entity is targeted by this target list on a specific Sentinel NPC.
	 * Does not include target-list-specific handling, such as current temporary targets.
	 */
	public boolean isTarget(LivingEntity entity, GuardianTrait guardian) {
		checkRecalculateTargetsCache();
		if (
			targetsProcessed.contains(GuardianTarget.OWNER) &&
			entity.getUniqueId().equals(guardian.getNPC().getTrait(Owner.class).getOwnerId())
		) {
			return true;
		}
		return isTarget(entity);
	}

	/**
	 * Returns whether an entity is targeted by this target list.
	 * Does not account for NPC-specific target handlers, like 'owner' (which requires knowledge of who that owner is, based on which NPC is checking).
	 * To include that, use isTarget(LivingEntity, SentinelTrait)
	 */
	public boolean isTarget(LivingEntity entity) {
		checkRecalculateTargetsCache();
		return isTargetNoCache(entity);
	}

	public static GuardianTarget forName(String name) {
		return WorldGuardianPlugin.targetOptions.get(name.toUpperCase());
	}

	/**
	 * Returns whether an entity is targeted by this target list.
	 * Does not account for NPC-specific target handlers, like 'owner' (which requires knowledge of who that owner is, based on which NPC is checking).
	 * To include that, use isTarget(LivingEntity, GuardianTrait)
	 *
	 * Explicitly does not reprocess the cache.
	 */
	public boolean isTargetNoCache(LivingEntity entity) {
		if (entity.getEquipment() != null && GuardianUtilities.getHeldItem(entity) != null
			&& isRegexTargeted(GuardianUtilities.getHeldItem(entity).getType().name(), byHeldItem)) {
			return true;
		}
		for (ArrayList<CachedOtherTarget> targets : otherTargetCache.values()) {
			for (CachedOtherTarget target : targets) {
				if (target.integration.isTarget(entity, target.prefix, target.value)) {
					return true;
				}
			}
		}
		WorldGuardianPlugin plugin = GuardianTargetUtil.getPlugin();
		Logger logger = (plugin != null) ? plugin.getLogger() : null;

		for (GuardianTargetList allInOne : byAllInOne) {
			GuardianTargetList subList = allInOne.duplicate();
			subList.recalculateCacheNoClear();
			if (debugMe && plugin != null) {
				plugin.getLogger().info("All-In-One Debug: " + subList.totalTargetsCount() + " at start: " + subList.toMultiTargetString());
			}
			while (subList.ifIsTargetDeleteTarget(entity)) {
			}
			if (subList.totalTargetsCount() == 0) {
				return true;
			}
			if (debugMe && logger != null) {
				logger.info(
					"All-In-One Debug: " + subList.totalTargetsCount() + " left: " + subList.toMultiTargetString()
				);
			}
		}
		// Any NPCs cause instant return - things below should be non-NPC only target types
		if (entity.hasMetadata("NPC")) {
			return targetsProcessed.contains(GuardianTarget.NPCS) ||
				   isRegexTargeted(CitizensAPI.getNPCRegistry().getNPC(entity).getName(), byNpcName);
		}
		if (entity instanceof Player) {
			if (isRegexTargeted(entity.getName(), byPlayerName)) {
				return true;
			}

			if (checkPlayerByGroup((Player) entity, byGroup))
				return true;

//			if (plugin != null && plugin.vaultPerms != null) {
//
//				for (String group : byGroup) {
//
//					if (plugin.vaultPerms.playerInGroup((Player) entity, group)) {
//						return true;
//					}
//				}
//			}
		}
		else if (isRegexTargeted(entity.getCustomName() == null ? entity.getType().name() : entity.getCustomName(), byEntityName)) {
			return true;
		}
		HashSet<GuardianTarget> possible = WorldGuardianPlugin.entityToTargets.get(entity.getType());
		for (GuardianTarget poss : possible) {
			if (targetsProcessed.contains(poss)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkPlayerByGroup(Player player, ArrayList<String> byGroup) {
		try {
			LuckPermsApi luckPermsApi = LuckPerms.getApi();
			for (String group : byGroup) {
				if (player.hasPermission("group." + group)) {
					return true;
				}
			}

		}
		catch (IllegalStateException ex){
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * This is a special target method, that will remove the target from the targets list if it's matched.
	 * Primarily for the multi-targets system.
	 */
	public boolean ifIsTargetDeleteTarget(LivingEntity entity) {
		if (entity.getEquipment() != null && GuardianUtilities.getHeldItem(entity) != null) {
			String match = GuardianUtilities.getRegexTarget(GuardianUtilities.getHeldItem(entity).getType().name(), byHeldItem);
			if (match != null) {
				byHeldItem.remove(match);
				return true;
			}
		}
		for (Map.Entry<String, ArrayList<CachedOtherTarget>> targets : otherTargetCache.entrySet()) {
			for (CachedOtherTarget target : targets.getValue()) {
				if (target.integration.isTarget(entity, target.prefix, target.value)) {
					byOther.remove(target.prefix + ":" + target.value);
					recalculateCacheNoClear();
					return true;
				}
			}
		}
		for (GuardianTargetList allInOne : byAllInOne) {
			GuardianTargetList subList = allInOne.duplicate();
			subList.recalculateCacheNoClear();
			while (subList.ifIsTargetDeleteTarget(entity)) {
			}
			if (subList.totalTargetsCount() == 0) {
				byAllInOne.remove(allInOne);
				return true;
			}
		}
		// Any NPCs cause instant return - things below should be non-NPC only target types
		if (entity.hasMetadata("NPC")) {
			if (targetsProcessed.contains(GuardianTarget.NPCS)) {
				for (String target : targets) {
					if (GuardianTargetUtil.forName(target) == GuardianTarget.NPCS) {
						targets.remove(target);
						recalculateCacheNoClear();
						return true;
					}
				}
				targetsProcessed.remove(GuardianTarget.NPCS);
				return true;
			}
			String match = GuardianUtilities.getRegexTarget(CitizensAPI.getNPCRegistry().getNPC(entity).getName(), byNpcName);
			if (match != null) {
				byNpcName.remove(match);
				return true;
			}
			return false;
		}
		if (entity instanceof Player) {
			String match = GuardianUtilities.getRegexTarget(((Player) entity).getName(), byPlayerName);
			if (match != null) {
				byPlayerName.remove(match);
				return true;
			}

			if (checkPlayerByGroup((Player) entity, byGroup))
				return true;

//			if (GuardianTargetUtil.getPlugin().vaultPerms != null) {
//				for (String group : byGroup) {
//					if (.vaultPerms.playerInGroup((Player) entity, group)) {
//						byGroup.remove(group);
//						return true;
//					}
//				}
//			}
		}
		else {
			String match = GuardianUtilities.getRegexTarget(entity.getCustomName() == null ? entity.getType().name() : entity.getCustomName(), byEntityName);
			if (match != null) {
				byEntityName.remove(match);
				return true;
			}
		}
		HashSet<GuardianTarget> possible = WorldGuardianPlugin.entityToTargets.get(entity.getType());
		for (GuardianTarget poss : possible) {
			if (targetsProcessed.contains(poss)) {
				for (String target : targets) {
					if (GuardianTargetUtil.forName(target) == poss) {
						targets.remove(target);
						recalculateCacheNoClear();
						return true;
					}
				}
				targetsProcessed.remove(poss);
				return true;
			}
		}
		return false;
	}


	/**
	 * Fills the target list from a Citizens data key, during load time.
	 */
	public void fillListFromKey(ArrayList<String> list, DataKey key) {
		for (DataKey listEntry : key.getSubKeys()) {
			list.add(listEntry.getRaw("").toString());
		}
	}

	/**
	 * Updates old (Sentinel 1.6 or lower) saves to new (Sentinel 1.7 or higher) saves.
	 */
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
	 * Returns whether a chat event is targeted by this list.
	 */
	@SuppressWarnings("unused")
	public boolean isEventTarget(GuardianTrait guardian, AsyncPlayerChatEvent event) {
		if (!guardian.targetingHelper.canSee(event.getPlayer())) {
			return false;
		}
		for (String str : byEvent) {
			if (str.startsWith("message,")) {
				String messageCheck = str.substring("message,".length());
				if (event.getMessage().toLowerCase().contains(messageCheck.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether a damage event is targeted by this list.
	 */
	public boolean isEventTarget(EntityDamageByEntityEvent event) {
		if (CitizensAPI.getNPCRegistry().isNPC(event.getDamager())) {
			return false;
		}
		if (byEvent.contains("pvp")
			&& event.getEntity() instanceof Player
			&& !CitizensAPI.getNPCRegistry().isNPC(event.getEntity())) {
			return true;
		}
		else if (byEvent.contains("pve")
				 && !(event.getEntity() instanceof Player)
				 && event.getEntity() instanceof LivingEntity) {
			return true;
		}
		else if (byEvent.contains("pvnpc")
				 && event.getEntity() instanceof LivingEntity
				 && CitizensAPI.getNPCRegistry().isNPC(event.getEntity())) {
			return true;
		}
		else if (byEvent.contains("pvguardian")
				 && event.getEntity() instanceof LivingEntity
				 && CitizensAPI.getNPCRegistry().isNPC(event.getEntity())
				 && CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).hasTrait(GuardianTrait.class)) {
			return true;
		}
		return false;
	}

	/**
	 * Represents an "other target" for use with caching.
	 */
	public static class CachedOtherTarget {

		/**
		 * The integration object.
		 */
		public GuardianIntegration integration;

		/**
		 * The "other" target prefix.
		 */
		public String prefix;

		/**
		 * The "other" target value.
		 */
		public String value;
	}

	/**
	 * Cache of "other" targets.
	 */
	public HashMap<String, ArrayList<CachedOtherTarget>> otherTargetCache = new HashMap<>();

	private int otherTargetSize = 0;
	/**
	 * Fills the cache 'targetsProcessed' set and does not deduplicate the source list.
	 * Also fills the 'otherTargetCache'.
	 */
	public void recalculateCacheNoClear() {
		targetsProcessed.clear();
		for (String target : targets) {
			targetsProcessed.add(GuardianTarget.forName(target));
		}
		otherTargetCache.clear();
		for (String otherTarget : byOther) {
			int colon = otherTarget.indexOf(':');
			String before = otherTarget.substring(0, colon);
			String after = otherTarget.substring(colon + 1);
			GuardianIntegration integration = GuardianTargetUtil.getPlugin().integrationPrefixMap.get(before);
			if (integration != null) {
				ArrayList<CachedOtherTarget> subList = otherTargetCache.get(before);
				if (subList == null) {
					subList = new ArrayList<>();
					otherTargetCache.put(before, subList);
				}
				CachedOtherTarget targ = new CachedOtherTarget();
				targ.integration = integration;
				targ.prefix = before;
				targ.value = after;
				subList.add(targ);
			}
		}
		otherTargetSize = byOther.size();
	}

	/**
	 * Fills the cache 'targetsProcessed' set then uses that set to deduplicate the source 'targets' set.
	 */
	public void recalculateTargetsCache() {
		debug("Clearing Targets Processed...");
		targetsProcessed.clear();

		debug("Recalculating Targets Cache...");
		for (String target : targets) {
			GuardianTarget newTarget = GuardianTargetUtil.forName(target);
			debug("Determining target for " + target + "; GuardianTarget: " + ((newTarget != null) ? "valid" : "invalid"));
			targetsProcessed.add(newTarget);
		}
		debug("Clearing Targets...");
		targets.clear();

		debug("Adding Targets Processed to Targets...");
		for (GuardianTarget target : targetsProcessed) {
			if (target != null)
			{
				debug("Added " + target.name());
				targets.add(target.name());
			}
			else
				debug("Tried to add Invalid target!");
		}
	}
	/**
	 * Returns the total count of targets (other than multi-targets).
	 */
	public int totalTargetsCount() {
		return targets.size() + byPlayerName.size() + byNpcName.size() + byEntityName.size()
			   + byHeldItem.size() + byGroup.size() + byEvent.size() + byOther.size() + byAllInOne.size();
	}

	public static void debug(String message) {
		if (debugMe)
			GuardianTargetUtil.getPlugin().getLogger().info(message);
	}

	private static void addList(StringBuilder builder, ArrayList<String> strs, String prefix) {
		if (!strs.isEmpty()) {
			for (String str : strs) {
				if (prefix != null) {
					builder.append(prefix).append(":");
				}
				builder
					.append(str)
					.append(ColorBasic)
					.append(" ")
					.append((char) 0x01)
					.append(" ")
					.append(ChatColor.AQUA);
			}
		}
	}
	/**
	 * Forms a \0x01-separated list for all-in-one-target output.
	 */
	public String toComboString() {
		StringBuilder sb = new StringBuilder();
		addList(sb, targets, null);
		addList(sb, byPlayerName, "player");
		addList(sb, byNpcName, "npc");
		addList(sb, byEntityName, "entityname");
		addList(sb, byHeldItem, "helditem");
		addList(sb, byGroup, "group");
		addList(sb, byEvent, "event");
		addList(sb, byOther, null);
		if (!byAllInOne.isEmpty()) {
			for (GuardianTargetList list : byAllInOne) {
				sb.append("allinone:").append(list.toAllInOneString()).append(ColorBasic)
					.append(" ").append((char) 0x01).append(" ").append(ChatColor.AQUA);
			}
		}
		if (sb.length() == 0) {
			return "";
		}
		return sb.substring(0, sb.length() - (ColorBasic + " . " + ChatColor.AQUA.toString()).length());
	}

	/**
	 * Forms a comma-separated list for multi-target output.
	 */
	public String toMultiTargetString() {
		return toComboString().replace((char) 0x01, ',');
	}

	/**
	 * Forms a pipe-separated list for all-in-one-target output.
	 */
	public String toAllInOneString() {
		return toComboString().replace((char) 0x01, '|');
	}

	/**
	 * Helper list, general ignorable.
	 */
	public ArrayList<LivingEntity> tempTargeted = new ArrayList<>();

	/**
	 * List of target-type-based targets.
	 */
	@Persist("targets")
	public ArrayList<String> targets = new ArrayList<>();

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

	/**
	 * List of target lists that need to be matched in full on exactly one entity to qualify as a match.
	 */
	@Persist("byAllInOne")
	public ArrayList<GuardianTargetList> byAllInOne = new ArrayList<>();

	/**
	 * List of target lists that need to be matched in full to qualify as a match.
	 */
	@Persist("byMultiple")
	public ArrayList<GuardianTargetList> byMultiple = new ArrayList<>();
}
