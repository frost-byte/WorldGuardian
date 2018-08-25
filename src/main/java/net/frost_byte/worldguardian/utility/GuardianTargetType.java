package net.frost_byte.worldguardian.utility;


import org.bukkit.entity.EntityType;

import java.util.Arrays;

@SuppressWarnings("unused")
public enum GuardianTargetType
{
	NPCS(new EntityType[]{}, "NPC"),
	OWNER(new EntityType[]{}, "OWNER"),
	PLAYERS(new EntityType[]{EntityType.PLAYER}, "PLAYER"),
	PIGS(new EntityType[]{EntityType.PIG}, "PIG"),
	OCELOTS(new EntityType[]{EntityType.OCELOT}, "OCELOT", "CAT"),
	COWS(new EntityType[]{EntityType.COW}, "COW"),
	RABBITS(new EntityType[]{EntityType.RABBIT}, "RABBIT", "BUNNY", "BUNNIE"),
	SHEEP(new EntityType[]{EntityType.SHEEP}, "SHEEP"),
	CHICKENS(new EntityType[]{EntityType.CHICKEN}, "CHICKEN", "DUCK"),
	HORSES(new EntityType[]{EntityType.HORSE}, "HORSE"),
	MUSHROOM_COW(new EntityType[]{EntityType.MUSHROOM_COW}, ""),
	IRON_GOLEMS(new EntityType[]{EntityType.IRON_GOLEM}, "IRON_GOLEM", "IRONGOLEM"),
	SQUIDS(new EntityType[]{EntityType.SQUID}, "SQUID"),
	VILLAGER(new EntityType[]{EntityType.VILLAGER}, "VILLAGER"),
	WOLF(new EntityType[]{EntityType.WOLF}, "WOLF"),
	SNOWMEN(new EntityType[]{EntityType.SNOWMAN}, "SNOWMAN"),
	WITCH(new EntityType[]{EntityType.WITCH}, "WITCH"),
	GUARDIANS(new EntityType[]{EntityType.GUARDIAN}, "GUARDIAN"),
	CREEPERS(new EntityType[]{EntityType.CREEPER}, "CREEPER"),
	SKELETONS(new EntityType[]{EntityType.SKELETON}, "SKELETON"),
	ZOMBIES(new EntityType[]{EntityType.ZOMBIE}, "ZOMBIE"),
	MAGMA_CUBES(new EntityType[]{EntityType.MAGMA_CUBE}, "MAGMA_CUBE", "MAGMACUBE"),
	ZOMBIE_PIGMEN(new EntityType[]{EntityType.PIG_ZOMBIE}, "PIG_ZOMBIE", "PIGZOMBIE", "ZOMBIEPIGMAN", "ZOMBIEPIGMEN", "ZOMBIE_PIGMAN", "ZOMBIE_PIGMEN", "ZOMBIE_PIGMAN"),
	SILVERFISH(new EntityType[]{EntityType.SILVERFISH}, "SILVERFISH", "SILVER_FISH", "SILVERFISHE", "SILVER_FISHE"),
	BATS(new EntityType[]{EntityType.BAT}, "BAT"),
	BLAZES(new EntityType[]{EntityType.BLAZE}, "BLAZE"),
	GHASTS(new EntityType[]{EntityType.GHAST}, "GHAST"),
	GIANTS(new EntityType[]{EntityType.GIANT}, "GIANT"),
	SLIME(new EntityType[]{EntityType.SLIME}, "SLIME"),
	SPIDER(new EntityType[]{EntityType.SPIDER}, "SPIDER"),
	CAVE_SPIDERS(new EntityType[]{EntityType.CAVE_SPIDER}, "CAVE_SPIDER", "CAVESPIDER"),
	ENDERMEN(new EntityType[]{EntityType.ENDERMAN}, "ENDERMAN", "ENDER_MAN", "ENDERMEN", "ENDER_MEN"),
	ENDERMITES(new EntityType[]{EntityType.ENDERMITE}, "ENDERMITE", "ENDER_MITE"),
	WITHER(new EntityType[]{EntityType.WITHER}, "WITHER"),
	ENDERDRAGON(new EntityType[]{EntityType.ENDER_DRAGON}, "ENDERDRAGON", "ENDER_DRAGON"),

	PASSIVE_MOB(
		new EntityType[]{
			EntityType.PIG, EntityType.OCELOT, EntityType.COW, EntityType.RABBIT, EntityType.SHEEP, EntityType.CHICKEN,
			EntityType.MUSHROOM_COW, EntityType.HORSE, EntityType.IRON_GOLEM, EntityType.SQUID, EntityType.VILLAGER,
			EntityType.WOLF, EntityType.SNOWMAN, EntityType.POLAR_BEAR,  EntityType.DONKEY, EntityType.LLAMA, EntityType.MULE,
			EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE, EntityType.PARROT, EntityType.DOLPHIN, EntityType.COD,
			EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH, EntityType.TURTLE
		},
		"PASSIVE_MOB", "PASSIVEMOB", "GOODMOB", "GOOD_MOB", "FRIENDLYMOB", "FRIENDLY_MOB"
	),
	MOBS(
		new EntityType[]{
			EntityType.GUARDIAN, EntityType.SHULKER, EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE,
			EntityType.MAGMA_CUBE, EntityType.PIG_ZOMBIE, EntityType.SILVERFISH, EntityType.BAT, EntityType.BLAZE,
			EntityType.GHAST, EntityType.GIANT, EntityType.SLIME, EntityType.SPIDER, EntityType.CAVE_SPIDER,
			EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.WITCH,
			EntityType.PIG, EntityType.OCELOT, EntityType.COW, EntityType.RABBIT, EntityType.SHEEP, EntityType.CHICKEN,
			EntityType.MUSHROOM_COW, EntityType.HORSE, EntityType.IRON_GOLEM, EntityType.SQUID, EntityType.VILLAGER,
			EntityType.WOLF, EntityType.SNOWMAN, EntityType.POLAR_BEAR, EntityType.DONKEY, EntityType.LLAMA,
			EntityType.MULE, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE, EntityType.VEX, EntityType.HUSK,
			EntityType.ELDER_GUARDIAN, EntityType.EVOKER, EntityType.STRAY, EntityType.ZOMBIE_VILLAGER,
			EntityType.WITHER_SKELETON, EntityType.VINDICATOR, EntityType.PARROT, EntityType.ILLUSIONER,
			EntityType.DROWNED, EntityType.PHANTOM, EntityType.DOLPHIN, EntityType.COD, EntityType.SALMON,
			EntityType.PUFFERFISH, EntityType.TROPICAL_FISH, EntityType.TURTLE
		},
		"MOB"
	),
	MONSTERS(
		new EntityType[]{
			EntityType.GUARDIAN, EntityType.SHULKER, EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE,
			EntityType.MAGMA_CUBE, EntityType.PIG_ZOMBIE, EntityType.SILVERFISH, EntityType.BAT, EntityType.BLAZE,
			EntityType.GHAST, EntityType.GIANT, EntityType.SLIME, EntityType.SPIDER, EntityType.CAVE_SPIDER,
			EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.WITCH,
			EntityType.VEX, EntityType.HUSK, EntityType.ELDER_GUARDIAN, EntityType.EVOKER, EntityType.STRAY,
			EntityType.ZOMBIE_VILLAGER, EntityType.WITHER_SKELETON, EntityType.VINDICATOR, EntityType.ILLUSIONER,
			EntityType.DROWNED, EntityType.PHANTOM
		},
		"MONSTER"
	),

	// 1.9
	SHULKERS(new EntityType[]{}, "SHULKER"),

	// 1.10
	POLAR_BEARS(new EntityType[]{EntityType.POLAR_BEAR}, "POLARBEAR", "POLAR_BEAR"),

	// 1.11
	VEXES(new EntityType[]{EntityType.VEX}, "VEX", "VEXE"),
	DONKEYS(new EntityType[]{EntityType.DONKEY}, "DONKEY"),
	LLAMAS(new EntityType[]{EntityType.LLAMA}, "LLAMA"),
	MULES(new EntityType[]{EntityType.MULE}, "MULE"),
	HUSKS(new EntityType[]{EntityType.HUSK}, "HUSK"),
	ELDER_GUARDIANS(new EntityType[]{EntityType.ELDER_GUARDIAN}, "ELDER_GUARDIAN", "ELDERGUARDIAN"),
	EVOKERS(new EntityType[]{EntityType.EVOKER}, "EVOKER"),
	SKELETON_HORSES(new EntityType[]{EntityType.SKELETON_HORSE}, "SKELETON_HORSE", "SKELETONHORSE"),
	STRAYS(new EntityType[]{EntityType.STRAY}, "STRAY"),
	ZOMBIE_VILLAGERS(new EntityType[]{EntityType.ZOMBIE_VILLAGER}, "ZOMBIE_VILLAGER", "ZOMBIEVILLAGER"),
	ZOMBIE_HORSES(new EntityType[]{EntityType.ZOMBIE_HORSE}, "ZOMBIE_HORSE", "ZOMBIEHORSE"),
	WITHER_SKELETONS(new EntityType[]{EntityType.WITHER_SKELETON}, "WITHER_SKELETON", "WITHERSKELETON"),
	VINDICATORS(new EntityType[]{EntityType.VINDICATOR}, "VINDICATOR"),

	// 1.12
	PARROTS(new EntityType[]{EntityType.PARROT}, "PARROT"),
	ILLUSIONERS(new EntityType[]{EntityType.ILLUSIONER}, "ILLUSIONER"),

	// 1.13
	DOLPHIN(new EntityType[]{EntityType.DOLPHIN}, "DOLPHIN"),
	DROWNED(new EntityType[]{EntityType.DROWNED}, "DROWNED"),
	COD(new EntityType[]{EntityType.COD}, "COD"),
	SALMON(new EntityType[]{EntityType.SALMON}, "SALMON"),
	PUFFERFISH(new EntityType[]{EntityType.PUFFERFISH}, "PUFFERFISH"),
	TROPICAL_FISH(new EntityType[]{ EntityType.TROPICAL_FISH}, "TROPICAL_FISH", "TROPICALFISH"),
	PHANTOM(new EntityType[]{EntityType.PHANTOM}, "PHANTOM"),
	TURTLE(new EntityType[]{EntityType.TURTLE}, "TURTLE");
	
	private String[] names;
	private EntityType[] entityTypes;

	GuardianTargetType(EntityType[] entityTypes, String... names)
	{
		this.entityTypes = entityTypes;
		this.names = names;
	}

	public static GuardianTargetType findByName(String typeName)
	{
		return Arrays.stream(GuardianTargetType.values())
				.filter(gtt -> gtt.name().equalsIgnoreCase(typeName))
				.findFirst()
				.orElse(null);
	}

	public String getPrimaryName() { return names[0]; }
	public EntityType[] getEntityTypes() { return entityTypes; }
	public String[] getNames() { return names; }
}
