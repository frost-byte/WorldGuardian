package net.frost_byte.worldguardian.utility;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.frost_byte.worldguardian.GuardianTarget;
import net.frost_byte.worldguardian.GuardianTargetFactory;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class GuardianTargetUtil
{
	private GuardianTargetFactory targetFactory;
	private WorldGuardianPlugin plugin;

	private static Map<GuardianTargetType, GuardianTarget> targetMap = new HashMap<>();
	private static HashMap<EntityType, HashSet<GuardianTarget>> entityToTargets = new HashMap<>();
	private static HashMap<String, GuardianTarget> targetOptions = new HashMap<>();

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

	private void initTargetMap()
	{
		targetMap.put(GuardianTargetType.NPCS, targetFactory.createTarget(GuardianTargetType.NPCS));
		targetMap.put(GuardianTargetType.OWNER, targetFactory.createTarget(GuardianTargetType.OWNER));
		targetMap.put(GuardianTargetType.PLAYERS, targetFactory.createTarget(GuardianTargetType.PLAYERS));
		targetMap.put(GuardianTargetType.PIGS, targetFactory.createTarget(GuardianTargetType.PIGS));
		targetMap.put(GuardianTargetType.OCELOTS, targetFactory.createTarget(GuardianTargetType.OCELOTS));
		targetMap.put(GuardianTargetType.COWS, targetFactory.createTarget(GuardianTargetType.COWS));
		targetMap.put(GuardianTargetType.RABBITS, targetFactory.createTarget(GuardianTargetType.RABBITS));
		targetMap.put(GuardianTargetType.SHEEP, targetFactory.createTarget(GuardianTargetType.SHEEP));
		targetMap.put(GuardianTargetType.CHICKENS, targetFactory.createTarget(GuardianTargetType.CHICKENS));
		targetMap.put(GuardianTargetType.HORSES, targetFactory.createTarget(GuardianTargetType.HORSES));
		targetMap.put(GuardianTargetType.MUSHROOM_COW, targetFactory.createTarget(GuardianTargetType.MUSHROOM_COW));
		targetMap.put(GuardianTargetType.IRON_GOLEMS, targetFactory.createTarget(GuardianTargetType.IRON_GOLEMS));
		targetMap.put(GuardianTargetType.SQUIDS, targetFactory.createTarget(GuardianTargetType.SQUIDS));
		targetMap.put(GuardianTargetType.VILLAGER, targetFactory.createTarget(GuardianTargetType.VILLAGER));
		targetMap.put(GuardianTargetType.WOLF, targetFactory.createTarget(GuardianTargetType.WOLF));
		targetMap.put(GuardianTargetType.SNOWMEN, targetFactory.createTarget(GuardianTargetType.SNOWMEN));
		targetMap.put(GuardianTargetType.WITCH, targetFactory.createTarget(GuardianTargetType.WITCH));
		targetMap.put(GuardianTargetType.GUARDIANS, targetFactory.createTarget(GuardianTargetType.GUARDIANS));
		targetMap.put(GuardianTargetType.CREEPERS, targetFactory.createTarget(GuardianTargetType.CREEPERS));
		targetMap.put(GuardianTargetType.SKELETONS, targetFactory.createTarget(GuardianTargetType.SKELETONS));
		targetMap.put(GuardianTargetType.ZOMBIES, targetFactory.createTarget(GuardianTargetType.ZOMBIES));
		targetMap.put(GuardianTargetType.MAGMA_CUBES, targetFactory.createTarget(GuardianTargetType.MAGMA_CUBES));
		targetMap.put(GuardianTargetType.ZOMBIE_PIGMEN, targetFactory.createTarget(GuardianTargetType.ZOMBIE_PIGMEN));
		targetMap.put(GuardianTargetType.SILVERFISH, targetFactory.createTarget(GuardianTargetType.SILVERFISH));
		targetMap.put(GuardianTargetType.BATS, targetFactory.createTarget(GuardianTargetType.BATS));
		targetMap.put(GuardianTargetType.BLAZES, targetFactory.createTarget(GuardianTargetType.BLAZES));
		targetMap.put(GuardianTargetType.GHASTS, targetFactory.createTarget(GuardianTargetType.GHASTS));
		targetMap.put(GuardianTargetType.GIANTS, targetFactory.createTarget(GuardianTargetType.GIANTS));
		targetMap.put(GuardianTargetType.SLIME, targetFactory.createTarget(GuardianTargetType.SLIME));
		targetMap.put(GuardianTargetType.SPIDER, targetFactory.createTarget(GuardianTargetType.SPIDER));
		targetMap.put(GuardianTargetType.CAVE_SPIDERS, targetFactory.createTarget(GuardianTargetType.CAVE_SPIDERS));
		targetMap.put(GuardianTargetType.ENDERMEN, targetFactory.createTarget(GuardianTargetType.ENDERMEN));
		targetMap.put(GuardianTargetType.ENDERMITES, targetFactory.createTarget(GuardianTargetType.ENDERMITES));
		targetMap.put(GuardianTargetType.WITHER, targetFactory.createTarget(GuardianTargetType.WITHER));
		targetMap.put(GuardianTargetType.ENDERDRAGON, targetFactory.createTarget(GuardianTargetType.ENDERDRAGON));

		targetMap.put(GuardianTargetType.PASSIVE_MOB, targetFactory.createTarget(GuardianTargetType.PASSIVE_MOB));
		targetMap.put(GuardianTargetType.MOBS, targetFactory.createTarget(GuardianTargetType.MOBS));
		targetMap.put(GuardianTargetType.MONSTERS, targetFactory.createTarget(GuardianTargetType.MONSTERS));
		targetMap.put(GuardianTargetType.SHULKERS, targetFactory.createTarget(GuardianTargetType.SHULKERS));

		// 1.13
		targetMap.put(GuardianTargetType.DOLPHIN, targetFactory.createTarget(GuardianTargetType.DOLPHIN));
		targetMap.put(GuardianTargetType.DROWNED, targetFactory.createTarget(GuardianTargetType.DROWNED));
		targetMap.put(GuardianTargetType.COD, targetFactory.createTarget(GuardianTargetType.COD));
		targetMap.put(GuardianTargetType.SALMON, targetFactory.createTarget(GuardianTargetType.SALMON));
		targetMap.put(GuardianTargetType.PUFFERFISH, targetFactory.createTarget(GuardianTargetType.PUFFERFISH));
		targetMap.put(GuardianTargetType.TROPICAL_FISH, targetFactory.createTarget(GuardianTargetType.TROPICAL_FISH));
		targetMap.put(GuardianTargetType.PHANTOM, targetFactory.createTarget(GuardianTargetType.PHANTOM));
		targetMap.put(GuardianTargetType.TURTLE, targetFactory.createTarget(GuardianTargetType.TURTLE));
	}

	public GuardianTarget forName(String name) {
		return targetOptions.get(name.toUpperCase());
	}

	public String getNameForType(GuardianTargetType targetType)
	{
		return targetMap.containsKey(targetType) ? targetMap.get(targetType).name() : "";
	}

	public void addTargetOption(String name, GuardianTarget guardianTarget)
	{
		name = Preconditions.checkNotNull(name, "The target name cannot be null");
		guardianTarget = Preconditions.checkNotNull(guardianTarget, "The guardian target cannot be null");

		targetOptions.putIfAbsent(name, guardianTarget);
	}

	public void addTypeMapping(EntityType entityType, GuardianTarget guardianTarget)
	{
		entityType = Preconditions.checkNotNull(entityType, "The entity type cannot be null");
		guardianTarget = Preconditions.checkNotNull(guardianTarget, "The guardian target cannot be null");

		if (entityToTargets != null && entityToTargets.containsKey(entityType))
			entityToTargets.get(entityType).add(guardianTarget);
	}

	private void initEntityMap()
	{
		for (EntityType entityType : EntityType.values())
			entityToTargets.put(entityType, new HashSet<>());
	}

	public HashSet<GuardianTarget> findTargetByEntityType(EntityType entityType)
	{
		entityType = Preconditions.checkNotNull(entityType, "The entity type cannot be null");

		if (entityToTargets == null || entityToTargets.isEmpty() || !(entityToTargets.containsKey(entityType)))
			return null;

		return entityToTargets.get(entityType);
	}

	public GuardianTarget getTarget(GuardianTargetType targetType){
		targetType = Preconditions.checkNotNull(targetType, "The target type cannot be null");

		if (targetMap != null && targetMap.containsKey(targetType))
		{
			return targetMap.get(targetType);
		}

		return null;
	}

	@Inject
	public GuardianTargetUtil(WorldGuardianPlugin plugin, GuardianTargetFactory targetFactory)
	{
		this.targetFactory = targetFactory;
		this.plugin = plugin;

		if (targetMap.isEmpty())
			initTargetMap();

		if (entityToTargets.isEmpty())
			initEntityMap();
	}
}
