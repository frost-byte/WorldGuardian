package net.frost_byte.worldguardian.utility;

import com.google.common.base.Preconditions;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.frost_byte.worldguardian.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import java.util.*;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.*;

@SuppressWarnings("WeakerAccess")
public final class GuardianTargetUtil
{
	private static Map<GuardianTargetType, GuardianTarget> targetMap = new HashMap<>();
	private static HashMap<EntityType, HashSet<GuardianTarget>> entityToTargets = new HashMap<>();
	private static HashMap<String, GuardianTarget> targetOptions = new HashMap<>();
	private static List<String> targetNames = new ArrayList<>();
	private static WorldGuardianPlugin plugin;
	public static final boolean v1_8, v1_9, v1_10, v1_11, v1_12, v1_13;

	static {
		String vers = Bukkit.getBukkitVersion(); // Returns in format like: 1.12.2-R0.1-SNAPSHOT
		v1_13 = vers.startsWith("1.13");
		v1_12 = vers.startsWith("1.12") || v1_13;
		v1_11 = vers.startsWith("1.11") || v1_12;
		v1_10 = vers.startsWith("1.10") || v1_11;
		v1_9 = vers.startsWith("1.9") || v1_10;
		v1_8 = vers.startsWith("1.8") || v1_9;
	}

	static
	{
		targetMap.put(GuardianTargetType.NPCS, new GuardianTarget(GuardianTargetType.NPCS));
		targetMap.put(GuardianTargetType.OWNER, new GuardianTarget(GuardianTargetType.OWNER));
		targetMap.put(GuardianTargetType.PLAYERS, new GuardianTarget(GuardianTargetType.PLAYERS));
		targetMap.put(GuardianTargetType.PIGS, new GuardianTarget(GuardianTargetType.PIGS));
		targetMap.put(GuardianTargetType.OCELOTS, new GuardianTarget(GuardianTargetType.OCELOTS));
		targetMap.put(GuardianTargetType.COWS, new GuardianTarget(GuardianTargetType.COWS));
		targetMap.put(GuardianTargetType.RABBITS, new GuardianTarget(GuardianTargetType.RABBITS));
		targetMap.put(GuardianTargetType.SHEEP, new GuardianTarget(GuardianTargetType.SHEEP));
		targetMap.put(GuardianTargetType.CHICKENS, new GuardianTarget(GuardianTargetType.CHICKENS));
		targetMap.put(GuardianTargetType.HORSES, new GuardianTarget(GuardianTargetType.HORSES));
		targetMap.put(GuardianTargetType.MUSHROOM_COW, new GuardianTarget(GuardianTargetType.MUSHROOM_COW));
		targetMap.put(GuardianTargetType.IRON_GOLEMS, new GuardianTarget(GuardianTargetType.IRON_GOLEMS));
		targetMap.put(GuardianTargetType.SQUIDS, new GuardianTarget(GuardianTargetType.SQUIDS));
		targetMap.put(GuardianTargetType.VILLAGER, new GuardianTarget(GuardianTargetType.VILLAGER));
		targetMap.put(GuardianTargetType.WOLF, new GuardianTarget(GuardianTargetType.WOLF));
		targetMap.put(GuardianTargetType.SNOWMEN, new GuardianTarget(GuardianTargetType.SNOWMEN));
		targetMap.put(GuardianTargetType.WITCH, new GuardianTarget(GuardianTargetType.WITCH));
		targetMap.put(GuardianTargetType.GUARDIANS, new GuardianTarget(GuardianTargetType.GUARDIANS));
		targetMap.put(GuardianTargetType.CREEPERS, new GuardianTarget(GuardianTargetType.CREEPERS));
		targetMap.put(GuardianTargetType.SKELETONS, new GuardianTarget(GuardianTargetType.SKELETONS));
		targetMap.put(GuardianTargetType.ZOMBIES, new GuardianTarget(GuardianTargetType.ZOMBIES));
		targetMap.put(GuardianTargetType.MAGMA_CUBES, new GuardianTarget(GuardianTargetType.MAGMA_CUBES));
		targetMap.put(GuardianTargetType.ZOMBIE_PIGMEN, new GuardianTarget(GuardianTargetType.ZOMBIE_PIGMEN));
		targetMap.put(GuardianTargetType.SILVERFISH, new GuardianTarget(GuardianTargetType.SILVERFISH));
		targetMap.put(GuardianTargetType.BATS, new GuardianTarget(GuardianTargetType.BATS));
		targetMap.put(GuardianTargetType.BLAZES, new GuardianTarget(GuardianTargetType.BLAZES));
		targetMap.put(GuardianTargetType.GHASTS, new GuardianTarget(GuardianTargetType.GHASTS));
		targetMap.put(GuardianTargetType.GIANTS, new GuardianTarget(GuardianTargetType.GIANTS));
		targetMap.put(GuardianTargetType.SLIME, new GuardianTarget(GuardianTargetType.SLIME));
		targetMap.put(GuardianTargetType.SPIDER, new GuardianTarget(GuardianTargetType.SPIDER));
		targetMap.put(GuardianTargetType.CAVE_SPIDERS, new GuardianTarget(GuardianTargetType.CAVE_SPIDERS));
		targetMap.put(GuardianTargetType.ENDERMEN, new GuardianTarget(GuardianTargetType.ENDERMEN));
		targetMap.put(GuardianTargetType.ENDERMITES, new GuardianTarget(GuardianTargetType.ENDERMITES));
		targetMap.put(GuardianTargetType.WITHER, new GuardianTarget(GuardianTargetType.WITHER));
		targetMap.put(GuardianTargetType.ENDERDRAGON, new GuardianTarget(GuardianTargetType.ENDERDRAGON));

		targetMap.put(GuardianTargetType.PASSIVE_MOB, new GuardianTarget(GuardianTargetType.PASSIVE_MOB));
		targetMap.put(GuardianTargetType.MOBS, new GuardianTarget(GuardianTargetType.MOBS));
		targetMap.put(GuardianTargetType.MONSTERS, new GuardianTarget(GuardianTargetType.MONSTERS));
		targetMap.put(GuardianTargetType.SHULKERS, new GuardianTarget(GuardianTargetType.SHULKERS));

		// 1.13
		targetMap.put(GuardianTargetType.DOLPHIN, new GuardianTarget(GuardianTargetType.DOLPHIN));
		targetMap.put(GuardianTargetType.DROWNED, new GuardianTarget(GuardianTargetType.DROWNED));
		targetMap.put(GuardianTargetType.COD, new GuardianTarget(GuardianTargetType.COD));
		targetMap.put(GuardianTargetType.SALMON, new GuardianTarget(GuardianTargetType.SALMON));
		targetMap.put(GuardianTargetType.PUFFERFISH, new GuardianTarget(GuardianTargetType.PUFFERFISH));
		targetMap.put(GuardianTargetType.TROPICAL_FISH, new GuardianTarget(GuardianTargetType.TROPICAL_FISH));
		targetMap.put(GuardianTargetType.PHANTOM, new GuardianTarget(GuardianTargetType.PHANTOM));
		targetMap.put(GuardianTargetType.TURTLE, new GuardianTarget(GuardianTargetType.TURTLE));

		for (EntityType entityType : EntityType.values())
			entityToTargets.put(entityType, new HashSet<>());
	}

	public static WorldGuardianPlugin getPlugin() { return plugin;}
	public static void setPlugin(WorldGuardianPlugin thePlugin) { plugin = thePlugin; }

	public static List<String> getValidTargets() {
		return targetNames;
	}

	public static String getValidTargetsMessage() {
		StringBuilder valid = new StringBuilder();
		targetOptions.keySet().forEach(name -> valid.append(name).append(", "));

		// Skip the last ', '
		String validNames = prefixGood + "Valid Targets: " + valid.substring(0, valid.length() - 2) + "\n";
		String validCategories = prefixGood +
			"Also allowed: player:NAME(REGEX), npc:NAME(REGEX), entityname:NAME(REGEX),\n" +
			"helditem:MATERIALNAME(REGEX), group:GROUPNAME(EXACT), event:pvp/pvnpc/pve\n";

		StringBuilder results = new StringBuilder("Also: \n");

		for (GuardianIntegration gi : integrations) {
			results.append("    ")
					.append(prefixGood)
					.append(gi.getTargetHelp())
					.append("\n");
		}

		return validNames + validCategories + results;
	}

	public static GuardianTarget forName(String name) {
		return targetOptions.get(name.toUpperCase());
	}

	public static GuardianTrait getGuardianFor(CommandSender sender) {
		NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);

		if (npc == null) {
			return null;
		}

		if (npc.hasTrait(GuardianTrait.class)) {
			return npc.getTrait(GuardianTrait.class);
		}

		return null;
	}

	@SuppressWarnings("unused")
	public static String getNameForType(GuardianTargetType targetType)
	{
		return targetMap.containsKey(targetType) ? targetMap.get(targetType).getName() : "";
	}

	public static void addTargetOption(String name, GuardianTarget guardianTarget)
	{
		name = Preconditions.checkNotNull(name, "The target name cannot be null");
		guardianTarget = Preconditions.checkNotNull(guardianTarget, "The guardian target cannot be null");

		targetOptions.putIfAbsent(name, guardianTarget);

		if (!targetNames.contains(name))
		{
			targetNames.add(name);
		}
	}

	public static void addTypeMapping(EntityType entityType, GuardianTarget guardianTarget)
	{
		entityType = Preconditions.checkNotNull(entityType, "The entity type cannot be null");
		guardianTarget = Preconditions.checkNotNull(guardianTarget, "The guardian target cannot be null");

		if (entityToTargets != null && entityToTargets.containsKey(entityType))
			entityToTargets.get(entityType).add(guardianTarget);
	}

	public static String getNameTargetString(List<String> strs) {
		StringBuilder targets = new StringBuilder();

		for (String str : strs) {
			targets.append(str).append(", ");
		}

		return targets.length() > 0 ?
				targets.substring(0, targets.length() - 2) : targets.toString();
	}

	public static String getTargetString(HashSet<String> guardian) {
		StringBuilder targets = new StringBuilder();

		for (String target : guardian) {
			targets.append(target).append(", ");
		}

		return targets.length() > 0 ?
				targets.substring(0, targets.length() - 2) : targets.toString();
	}

	public static String getOwner(NPC npc) {

		if (npc.getTrait(Owner.class).getOwnerId() == null) {
			return npc.getTrait(Owner.class).getOwner();
		}

		OfflinePlayer player = Bukkit.getOfflinePlayer(
				npc.getTrait(Owner.class).getOwnerId()
		);

		if (player == null) {
			return "Server/Unknown";
		}

		return player.getName();
	}

	public static HashSet<GuardianTarget> findTargetByEntityType(EntityType entityType)
	{
		entityType = Preconditions.checkNotNull(entityType, "The entity type cannot be null");

		if (entityToTargets == null || entityToTargets.isEmpty() || !(entityToTargets.containsKey(entityType)))
			return null;

		return entityToTargets.get(entityType);
	}

	@SuppressWarnings("unused")
	public static GuardianTarget getTarget(GuardianTargetType targetType){
		targetType = Preconditions.checkNotNull(targetType, "The target type cannot be null");

		if (targetMap != null && targetMap.containsKey(targetType))
		{
			return targetMap.get(targetType);
		}

		return null;
	}

	private GuardianTargetUtil()
	{
	}
}
