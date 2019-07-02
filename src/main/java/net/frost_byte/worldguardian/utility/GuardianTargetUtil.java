package net.frost_byte.worldguardian.utility;

import com.google.common.base.Preconditions;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.frost_byte.worldguardian.*;
import net.frost_byte.worldguardian.targeting.GuardianTarget;
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
	public static final boolean v1_8, v1_9, v1_10, v1_11, v1_12, v1_13, v1_14;

	static {
		String vers = Bukkit.getBukkitVersion(); // Returns in format like: 1.12.2-R0.1-SNAPSHOT
		v1_14 = vers.startsWith("1.14");
		v1_13 = vers.startsWith("1.13") || v1_14;
		v1_12 = vers.startsWith("1.12") || v1_13;
		v1_11 = vers.startsWith("1.11") || v1_12;
		v1_10 = vers.startsWith("1.10") || v1_11;
		v1_9 = vers.startsWith("1.9") || v1_10;
		v1_8 = vers.startsWith("1.8") || v1_9;
	}

	static
	{
		targetMap.put(GuardianTargetType.NPCS, GuardianTarget.NPCS);
		targetMap.put(GuardianTargetType.OWNER, GuardianTarget.OWNER);
		targetMap.put(GuardianTargetType.PLAYERS, GuardianTarget.PLAYERS);
		targetMap.put(GuardianTargetType.PIGS, GuardianTarget.PIGS);
		targetMap.put(GuardianTargetType.OCELOTS, GuardianTarget.OCELOTS);
		targetMap.put(GuardianTargetType.COWS, GuardianTarget.COWS);
		targetMap.put(GuardianTargetType.RABBITS, GuardianTarget.RABBITS);
		targetMap.put(GuardianTargetType.SHEEP, GuardianTarget.SHEEP);
		targetMap.put(GuardianTargetType.CHICKENS, GuardianTarget.CHICKENS);
		targetMap.put(GuardianTargetType.HORSES, GuardianTarget.HORSES);
		targetMap.put(GuardianTargetType.MUSHROOM_COW, GuardianTarget.MUSHROOM_COW);
		targetMap.put(GuardianTargetType.IRON_GOLEMS, GuardianTarget.IRON_GOLEMS);
		targetMap.put(GuardianTargetType.SQUIDS, GuardianTarget.SQUIDS);
		targetMap.put(GuardianTargetType.VILLAGER, GuardianTarget.VILLAGER);
		targetMap.put(GuardianTargetType.WOLF, GuardianTarget.WOLF);
		targetMap.put(GuardianTargetType.SNOWMEN, GuardianTarget.SNOWMEN);
		targetMap.put(GuardianTargetType.WITCH, GuardianTarget.WITCH);
		targetMap.put(GuardianTargetType.GUARDIANS, GuardianTarget.GUARDIANS);
		targetMap.put(GuardianTargetType.CREEPERS, GuardianTarget.CREEPERS);
		targetMap.put(GuardianTargetType.SKELETONS, GuardianTarget.SKELETONS);
		targetMap.put(GuardianTargetType.ZOMBIES, GuardianTarget.ZOMBIES);
		targetMap.put(GuardianTargetType.MAGMA_CUBES, GuardianTarget.MAGMA_CUBES);
		targetMap.put(GuardianTargetType.ZOMBIE_PIGMEN, GuardianTarget.ZOMBIE_PIGMEN);
		targetMap.put(GuardianTargetType.SILVERFISH, GuardianTarget.SILVERFISH);
		targetMap.put(GuardianTargetType.BATS, GuardianTarget.BATS);
		targetMap.put(GuardianTargetType.BLAZES, GuardianTarget.BLAZES);
		targetMap.put(GuardianTargetType.GHASTS, GuardianTarget.GHASTS);
		targetMap.put(GuardianTargetType.GIANTS, GuardianTarget.GIANTS);
		targetMap.put(GuardianTargetType.SLIME, GuardianTarget.SLIME);
		targetMap.put(GuardianTargetType.SPIDER, GuardianTarget.SPIDER);
		targetMap.put(GuardianTargetType.CAVE_SPIDERS, GuardianTarget.CAVE_SPIDERS);
		targetMap.put(GuardianTargetType.ENDERMEN, GuardianTarget.ENDERMEN);
		targetMap.put(GuardianTargetType.ENDERMITES, GuardianTarget.ENDERMITES);
		targetMap.put(GuardianTargetType.WITHER, GuardianTarget.WITHER);
		targetMap.put(GuardianTargetType.ENDERDRAGON, GuardianTarget.ENDERDRAGON);

		targetMap.put(GuardianTargetType.PASSIVE_MOB, GuardianTarget.PASSIVE_MOBS);
		targetMap.put(GuardianTargetType.MOBS, GuardianTarget.MOBS);
		targetMap.put(GuardianTargetType.MONSTERS, GuardianTarget.MONSTERS);
		targetMap.put(GuardianTargetType.SHULKERS, GuardianTarget.SHULKERS);

		// 1.13
		targetMap.put(GuardianTargetType.DOLPHIN, GuardianTarget.DOLPHIN);
		targetMap.put(GuardianTargetType.DROWNED, GuardianTarget.DROWNED);
		targetMap.put(GuardianTargetType.COD, GuardianTarget.COD);
		targetMap.put(GuardianTargetType.SALMON, GuardianTarget.SALMON);
		targetMap.put(GuardianTargetType.PUFFERFISH, GuardianTarget.PUFFERFISH);
		targetMap.put(GuardianTargetType.TROPICAL_FISH, GuardianTarget.TROPICAL_FISH);
		targetMap.put(GuardianTargetType.PHANTOM, GuardianTarget.PHANTOM);
		targetMap.put(GuardianTargetType.TURTLE, GuardianTarget.TURTLE);

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
		return GuardianTarget.forName(name.toUpperCase());
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
		return targetMap.containsKey(targetType) ? targetMap.get(targetType).name() : "";
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	public static String getTargetString(ArrayList<String> guardian) {
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

//		if (player == null) {
//			return "Server/Unknown";
//		}

		return player.getName();
	}

	@SuppressWarnings("unused")
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
