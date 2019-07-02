package net.frost_byte.worldguardian.targeting;

import net.frost_byte.worldguardian.WorldGuardianPlugin;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
public class GuardianTarget
{
	/**
	 * All NPCs are targets.
	 */
	public static GuardianTarget NPCS = new GuardianTarget(new EntityType[]{}, "NPC");

	/**
	 * The NPC's owner is a target.
	 */
	public static GuardianTarget OWNER = new GuardianTarget(new EntityType[]{}, "OWNER");

	/**
	 * Player entity is a target.
	 */
	public static GuardianTarget PLAYERS = new GuardianTarget(new EntityType[]{EntityType.PLAYER}, "PLAYER");

	/**
	 * Basic single-entity target types (that were added in Minecraft 1.8 or earlier).
	 */
	public static GuardianTarget PIGS = new GuardianTarget(new EntityType[]{EntityType.PIG}, "PIG"),
		OCELOTS = new GuardianTarget(new EntityType[]{EntityType.OCELOT}, "OCELOT", "CAT"),
		COWS = new GuardianTarget(new EntityType[]{EntityType.COW}, "COW"),
		RABBITS = new GuardianTarget(new EntityType[]{EntityType.RABBIT}, "RABBIT", "BUNNY", "BUNNIE"),
		SHEEP = new GuardianTarget(new EntityType[]{EntityType.SHEEP}, "SHEEP"),
		CHICKENS = new GuardianTarget(new EntityType[]{EntityType.CHICKEN}, "CHICKEN", "DUCK"),
		HORSES = new GuardianTarget(new EntityType[]{EntityType.HORSE}, "HORSE"),
		MUSHROOM_COW = new GuardianTarget(new EntityType[]{EntityType.MUSHROOM_COW}, "MUSHROOM_COW", "MUSHROOMCOW", "MOOSHROOM"),
		IRON_GOLEMS = new GuardianTarget(new EntityType[]{EntityType.IRON_GOLEM}, "IRON_GOLEM", "IRONGOLEM"),
		SQUIDS = new GuardianTarget(new EntityType[]{EntityType.SQUID}, "SQUID"),
		VILLAGER = new GuardianTarget(new EntityType[]{EntityType.VILLAGER}, "VILLAGER"),
		WOLF = new GuardianTarget(new EntityType[]{EntityType.WOLF}, "WOLF"),
		SNOWMEN = new GuardianTarget(new EntityType[]{EntityType.SNOWMAN}, "SNOWMAN", "SNOWMEN"),
		WITCH = new GuardianTarget(new EntityType[]{EntityType.WITCH}, "WITCH"),
		GUARDIANS = new GuardianTarget(new EntityType[]{EntityType.GUARDIAN}, "GUARDIAN"),
		CREEPERS = new GuardianTarget(new EntityType[]{EntityType.CREEPER}, "CREEPER"),
		SKELETONS = new GuardianTarget(new EntityType[]{EntityType.SKELETON}, "SKELETON"),
		ZOMBIES = new GuardianTarget(new EntityType[]{EntityType.ZOMBIE}, "ZOMBIE"),
		MAGMA_CUBES = new GuardianTarget(new EntityType[]{EntityType.MAGMA_CUBE}, "MAGMA_CUBE", "MAGMACUBE"),
		ZOMBIE_PIGMEN = new GuardianTarget(new EntityType[]{EntityType.PIG_ZOMBIE}, "PIG_ZOMBIE", "PIGZOMBIE", "ZOMBIEPIGMAN", "ZOMBIEPIGMEN", "ZOMBIE_PIGMAN", "ZOMBIE_PIGMEN", "ZOMBIE_PIGMAN"),
		SILVERFISH = new GuardianTarget(new EntityType[]{EntityType.SILVERFISH}, "SILVERFISH", "SILVER_FISH", "SILVERFISHE", "SILVER_FISHE"),
		BATS = new GuardianTarget(new EntityType[]{EntityType.BAT}, "BAT"),
		BLAZES = new GuardianTarget(new EntityType[]{EntityType.BLAZE}, "BLAZE"),
		GHASTS = new GuardianTarget(new EntityType[]{EntityType.GHAST}, "GHAST"),
		GIANTS = new GuardianTarget(new EntityType[]{EntityType.GIANT}, "GIANT"),
		SLIME = new GuardianTarget(new EntityType[]{EntityType.SLIME}, "SLIME"),
		SPIDER = new GuardianTarget(new EntityType[]{EntityType.SPIDER}, "SPIDER"),
		CAVE_SPIDERS = new GuardianTarget(new EntityType[]{EntityType.CAVE_SPIDER}, "CAVE_SPIDER", "CAVESPIDER"),
		ENDERMEN = new GuardianTarget(new EntityType[]{EntityType.ENDERMAN}, "ENDERMAN", "ENDER_MAN", "ENDERMEN", "ENDER_MEN"),
		ENDERMITES = new GuardianTarget(new EntityType[]{EntityType.ENDERMITE}, "ENDERMITE", "ENDER_MITE"),
		WITHER = new GuardianTarget(new EntityType[]{EntityType.WITHER}, "WITHER"),
		ENDERDRAGON = new GuardianTarget(new EntityType[]{EntityType.ENDER_DRAGON}, "ENDERDRAGON", "ENDER_DRAGON");

	/**
	 * Valid target types for 1.9 or higher.
	 */
	public static GuardianTarget SHULKERS;

	/**
	 * Valid target types for 1.10 or higher.
	 */
	public static GuardianTarget POLAR_BEARS;

	/**
	 * Valid target types for 1.11 or higher.
	 */
	public static GuardianTarget VEXES, DONKEYS, LLAMAS, MULES, HUSKS, ELDER_GUARDIANS,
		EVOKERS, SKELETON_HORSES, STRAYS, ZOMBIE_VILLAGERS, ZOMBIE_HORSES, WITHER_SKELETONS, VINDICATORS;

	/**
	 * Valid target types for 1.12 or higher.
	 */
	public static GuardianTarget PARROTS, ILLUSIONERS;

	/**
	 * Valid target types for 1.13 or higher.
	 */
	public static GuardianTarget DOLPHIN, DROWNED, COD, SALMON, PUFFERFISH, TROPICAL_FISH, PHANTOM, TURTLE, FISH;

	/**
	 * Valid target types for 1.14 or higher.
	 */
	public static GuardianTarget RAVAGERS, PILLAGERS, CATS, PANDAS, TRADER_LLAMAS, WANDERING_TRADERS, FOXES;

	/**
	 * Multiple-entity-type targets.
	 */
	public static GuardianTarget PASSIVE_MOBS, MONSTERS, MOBS;

	/**
	 * HashSets of Materials within a category (for multi-version support).
	 */
	public static final HashSet<Material> SWORD_MATERIALS = new HashSet<>(),
		PICKAXE_MATERIALS = new HashSet<>(),
		AXE_MATERIALS = new HashSet<>(),
		HELMET_MATERIALS = new HashSet<>(),
		CHESTPLATE_MATERIALS = new HashSet<>(),
		LEGGINGS_MATERIALS = new HashSet<>(),
		BOOTS_MATERIALS = new HashSet<>(),
		BOW_MATERIALS = new HashSet<>(),
		POTION_MATERIALS = new HashSet<>(),
		SKULL_MATERIALS = new HashSet<>();

	/**
	 * A map of weapon materials to their damage multipliers.
	 */
	public static final Map<Material, Double> WEAPON_DAMAGE_MULTIPLIERS = new HashMap<>();

	/**
	 * A map of armor materials to their protection multipliers.
	 */
	public static final Map<Material, Double> ARMOR_PROTECTION_MULTIPLIERS = new HashMap<>();

	/**
	 * A specific material (for multi-version support).
	 */
	public static final Material MATERIAL_SNOW_BALL, MATERIAL_NETHER_STAR, MATERIAL_BLAZE_ROD;

	/**
	 * Boolean indicating if the server version is >= the named version.
	 */
	public static final boolean v1_8, v1_9, v1_10, v1_11, v1_12, v1_13, v1_14;

	/**
	 * Returns whether an item material is a valid weapon type.
	 */
	public static boolean isWeapon(Material mat) {
		return GuardianTarget.WEAPON_DAMAGE_MULTIPLIERS.containsKey(mat)
			   || GuardianTarget.POTION_MATERIALS.contains(mat)
			   || GuardianTarget.BOW_MATERIALS.contains(mat)
			   || GuardianTarget.SKULL_MATERIALS.contains(mat)
			   || mat == GuardianTarget.MATERIAL_SNOW_BALL
			   || mat == GuardianTarget.MATERIAL_BLAZE_ROD
			   || mat == GuardianTarget.MATERIAL_NETHER_STAR;
	}

	/**
	 * Combines two arrays of EntityType values.
	 */
	public static EntityType[] combine(EntityType[] a, EntityType... b) {
		EntityType[] types = new EntityType[a.length + b.length];
		for (int i = 0; i < a.length; i++) {
			types[i] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			types[i + a.length] = b[i];
		}
		return types;
	}

	/**
	 * Name array for passive mob types.
	 */
	static String[] passiveNames() {
		return new String[]{"PASSIVE_MOB", "PASSIVEMOB", "GOODMOB", "GOOD_MOB", "FRIENDLYMOB", "FRIENDLY_MOB"};
	}

	static EntityType[] v1_8_passive() {
		return new EntityType[]{
			EntityType.PIG, EntityType.OCELOT, EntityType.COW, EntityType.RABBIT, EntityType.SHEEP, EntityType.CHICKEN, EntityType.MUSHROOM_COW,
			EntityType.HORSE, EntityType.IRON_GOLEM, EntityType.SQUID, EntityType.VILLAGER, EntityType.WOLF, EntityType.SNOWMAN
		};
	}

	static EntityType[] v1_10_passive() {
		return combine(v1_8_passive(), EntityType.POLAR_BEAR);
	}

	static EntityType[] v1_11_passive() {
		return combine(v1_10_passive(), EntityType.DONKEY, EntityType.LLAMA, EntityType.MULE, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE);
	}

	static EntityType[] v1_12_passive() {
		return combine(v1_11_passive(), EntityType.PARROT);
	}

	static EntityType[] v1_13_passive() {
		return combine(v1_12_passive(), EntityType.DOLPHIN, EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH, EntityType.TURTLE);
	}

	static EntityType[] v1_14_passive() {
		return combine(v1_13_passive(), EntityType.CAT, EntityType.PANDA, EntityType.TRADER_LLAMA, EntityType.WANDERING_TRADER, EntityType.FOX);
	}


	static EntityType[] v1_8_monsters() {
		return new EntityType[]{EntityType.GUARDIAN, EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE,
			EntityType.MAGMA_CUBE, EntityType.PIG_ZOMBIE, EntityType.SILVERFISH, EntityType.BAT, EntityType.BLAZE,
			EntityType.GHAST, EntityType.GIANT, EntityType.SLIME, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ENDERMAN,
			EntityType.ENDERMITE, EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.WITCH};
	}

	static EntityType[] v1_9_monsters() {
		return combine(v1_8_monsters(), EntityType.SHULKER);
	}

	static EntityType[] v1_11_monsters() {
		return combine(v1_9_monsters(), EntityType.VEX, EntityType.HUSK, EntityType.ELDER_GUARDIAN,
					   EntityType.EVOKER, EntityType.STRAY, EntityType.ZOMBIE_VILLAGER,
					   EntityType.WITHER_SKELETON, EntityType.VINDICATOR);
	}

	static EntityType[] v1_12_monsters() {
		return combine(v1_11_monsters(), EntityType.ILLUSIONER);
	}

	static EntityType[] v1_13_monsters() {
		return combine(v1_12_monsters(), EntityType.DROWNED, EntityType.PHANTOM);
	}

	static EntityType[] v1_14_monsters() {
		return combine(v1_13_monsters(), EntityType.RAVAGER, EntityType.PILLAGER);
	}

	static {
		String vers = Bukkit.getBukkitVersion(); // Returns in format like: 1.12.2-R0.1-SNAPSHOT
		boolean future = vers.startsWith("1.15") || vers.startsWith("1.16");
		v1_14 = vers.startsWith("1.14") || future;
		v1_13 = vers.startsWith("1.13") || v1_14;
		v1_12 = vers.startsWith("1.12") || v1_13;
		v1_11 = vers.startsWith("1.11") || v1_12;
		v1_10 = vers.startsWith("1.10") || v1_11;
		v1_9 = vers.startsWith("1.9") || v1_10;
		v1_8 = vers.startsWith("1.8") || v1_9;
		if (v1_8 && !v1_9) {
			PASSIVE_MOBS = new GuardianTarget(v1_8_passive(), passiveNames());
			MOBS = new GuardianTarget(combine(v1_8_passive(), v1_8_monsters()), "MOB");
			MONSTERS = new GuardianTarget(v1_8_monsters(), "MONSTER");
		}
		if (v1_9) {
			SHULKERS = new GuardianTarget(new EntityType[]{EntityType.SHULKER}, "SHULKER");
		}
		if (v1_9 && !v1_10) {
			PASSIVE_MOBS = new GuardianTarget(v1_8_passive(), passiveNames());
			MOBS = new GuardianTarget(combine(v1_8_passive(), v1_9_monsters()), "MOB");
			MONSTERS = new GuardianTarget(v1_9_monsters(), "MONSTER");
		}
		if (v1_10) {
			POLAR_BEARS = new GuardianTarget(new EntityType[]{EntityType.POLAR_BEAR}, "POLARBEAR", "POLAR_BEAR");
		}
		if (v1_10 && !v1_11) {
			PASSIVE_MOBS = new GuardianTarget(v1_10_passive(), passiveNames());
			MOBS = new GuardianTarget(combine(v1_10_passive(), v1_9_monsters()), "MOB");
			MONSTERS = new GuardianTarget(v1_9_monsters(), "MONSTER");
		}
		if (v1_11) {
			VEXES = new GuardianTarget(new EntityType[]{EntityType.VEX}, "VEX", "VEXE");
			DONKEYS = new GuardianTarget(new EntityType[]{EntityType.DONKEY}, "DONKEY");
			LLAMAS = new GuardianTarget(new EntityType[]{EntityType.LLAMA}, "LLAMA");
			MULES = new GuardianTarget(new EntityType[]{EntityType.MULE}, "MULE");
			HUSKS = new GuardianTarget(new EntityType[]{EntityType.HUSK}, "HUSK");
			ELDER_GUARDIANS = new GuardianTarget(new EntityType[]{EntityType.ELDER_GUARDIAN}, "ELDER_GUARDIAN", "ELDERGUARDIAN");
			EVOKERS = new GuardianTarget(new EntityType[]{EntityType.EVOKER}, "EVOKER");
			SKELETON_HORSES = new GuardianTarget(new EntityType[]{EntityType.SKELETON_HORSE}, "SKELETON_HORSE", "SKELETONHORSE");
			STRAYS = new GuardianTarget(new EntityType[]{EntityType.STRAY}, "STRAY");
			ZOMBIE_VILLAGERS = new GuardianTarget(new EntityType[]{EntityType.ZOMBIE_VILLAGER}, "ZOMBIE_VILLAGER", "ZOMBIEVILLAGER");
			ZOMBIE_HORSES = new GuardianTarget(new EntityType[]{EntityType.ZOMBIE_HORSE}, "ZOMBIE_HORSE", "ZOMBIEHORSE");
			WITHER_SKELETONS = new GuardianTarget(new EntityType[]{EntityType.WITHER_SKELETON}, "WITHER_SKELETON", "WITHERSKELETON");
			VINDICATORS = new GuardianTarget(new EntityType[]{EntityType.VINDICATOR}, "VINDICATOR");
		}
		if (v1_11 && !v1_12) {
			PASSIVE_MOBS = new GuardianTarget(v1_11_passive(), passiveNames());
			MOBS = new GuardianTarget(combine(v1_11_passive(), v1_11_monsters()), "MOB");
			MONSTERS = new GuardianTarget(v1_11_monsters(), "MONSTER");
		}
		if (v1_12) {
			PARROTS = new GuardianTarget(new EntityType[]{EntityType.PARROT}, "PARROT");
			ILLUSIONERS = new GuardianTarget(new EntityType[]{EntityType.ILLUSIONER}, "ILLUSIONER");
		}
		if (v1_12 && !v1_13) {
			PASSIVE_MOBS = new GuardianTarget(v1_12_passive(), passiveNames());
			MOBS = new GuardianTarget(combine(v1_12_passive(), v1_12_monsters()), "MOB");
			MONSTERS = new GuardianTarget(v1_12_monsters(), "MONSTER");
		}
		if (v1_13) {
			DOLPHIN = new GuardianTarget(new EntityType[]{EntityType.DOLPHIN}, "DOLPHIN");
			DROWNED = new GuardianTarget(new EntityType[]{EntityType.DROWNED}, "DROWNED");
			COD = new GuardianTarget(new EntityType[]{EntityType.COD}, "COD");
			SALMON = new GuardianTarget(new EntityType[]{EntityType.SALMON}, "SALMON");
			PUFFERFISH = new GuardianTarget(new EntityType[]{EntityType.PUFFERFISH}, "PUFFERFISH", "PUFFERFISHE");
			TROPICAL_FISH = new GuardianTarget(new EntityType[]{EntityType.TROPICAL_FISH}, "TROPICAL_FISH", "TROPICALFISH", "TROPICAL_FISHE", "TROPICALFISHE");
			TROPICAL_FISH = new GuardianTarget(new EntityType[]{EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD}, "FISH", "FISHE");
			PHANTOM = new GuardianTarget(new EntityType[]{EntityType.PHANTOM}, "PHANTOM");
			TURTLE = new GuardianTarget(new EntityType[]{EntityType.TURTLE}, "TURTLE");
		}
		if (v1_13 && !v1_14) {
			PASSIVE_MOBS = new GuardianTarget(v1_13_passive(), passiveNames());
			MOBS = new GuardianTarget(combine(v1_13_passive(), v1_13_monsters()), "MOB");
			MONSTERS = new GuardianTarget(v1_13_monsters(), "MONSTER");
		}
		if (v1_14) {
			// combine(v1_13_passive(), EntityType.CAT, EntityType.PANDA, EntityType.TRADER_LLAMA, EntityType.WANDERING_TRADER, EntityType.FOX);
			RAVAGERS = new GuardianTarget(new EntityType[]{EntityType.RAVAGER}, "RAVAGER");
			PILLAGERS = new GuardianTarget(new EntityType[]{EntityType.PILLAGER}, "PILLAGER");
			CATS = new GuardianTarget(new EntityType[]{EntityType.CAT}, "CAT");
			PANDAS = new GuardianTarget(new EntityType[]{EntityType.PANDA}, "PANDA");
			TRADER_LLAMAS = new GuardianTarget(new EntityType[]{EntityType.TRADER_LLAMA}, "TRADER_LLAMA", "TRADERLLAMA");
			WANDERING_TRADERS = new GuardianTarget(new EntityType[]{EntityType.WANDERING_TRADER}, "WANDERING_TRADER", "WANDERINGTRADER", "TRADER");
			FOXES = new GuardianTarget(new EntityType[]{EntityType.FOX}, "FOX", "FOXE");
		}
		if (v1_14) { // && !v1_15
			PASSIVE_MOBS = new GuardianTarget(v1_13_passive(), passiveNames());
			MOBS = new GuardianTarget(combine(v1_13_passive(), v1_14_monsters()), "MOB");
			MONSTERS = new GuardianTarget(v1_14_monsters(), "MONSTER");
		}
		// ========================== End Entities ==========================
		// ========================== Begin Materials ==========================
		if (v1_13) {
			// Sword
			addAllMaterials(SWORD_MATERIALS, "DIAMOND_SWORD", "IRON_SWORD", "STONE_SWORD", "GOLDEN_SWORD", "WOODEN_SWORD");
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("DIAMOND_SWORD"), 7.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("IRON_SWORD"), 6.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("STONE_SWORD"), 5.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("GOLDEN_SWORD"), 4.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("WOODEN_SWORD"), 4.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("TRIDENT"), 8.0);
			// Pickaxe
			addAllMaterials(PICKAXE_MATERIALS, "DIAMOND_PICKAXE", "IRON_PICKAXE", "STONE_PICKAXE", "GOLDEN_PICKAXE", "WOODEN_PICKAXE");
			allMaterialsTo(WEAPON_DAMAGE_MULTIPLIERS, PICKAXE_MATERIALS, 2.0);
			// Axe
			addAllMaterials(AXE_MATERIALS, "DIAMOND_AXE", "IRON_AXE", "STONE_AXE", "GOLDEN_AXE", "WOODEN_AXE");
			allMaterialsTo(WEAPON_DAMAGE_MULTIPLIERS, AXE_MATERIALS, 3.0);
			// Bow
			BOW_MATERIALS.add(getMaterial("BOW"));
			// Helmet
			addAllMaterials(HELMET_MATERIALS, "DIAMOND_HELMET", "GOLDEN_HELMET", "IRON_HELMET", "LEATHER_HELMET", "CHAINMAIL_HELMET");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_HELMET"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_HELMET"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_HELMET"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_HELMET"), 0.04);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_HELMET"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("TURTLE_HELMET"), 0.08);
			// Chestplate
			addAllMaterials(CHESTPLATE_MATERIALS, "DIAMOND_CHESTPLATE", "GOLDEN_CHESTPLATE", "IRON_CHESTPLATE", "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_CHESTPLATE"), 0.32);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_CHESTPLATE"), 0.20);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_CHESTPLATE"), 0.24);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_CHESTPLATE"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_CHESTPLATE"), 0.20);
			// Leggings
			addAllMaterials(LEGGINGS_MATERIALS, "DIAMOND_LEGGINGS", "GOLDEN_LEGGINGS", "IRON_LEGGINGS", "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_LEGGINGS"), 0.24);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_LEGGINGS"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_LEGGINGS"), 0.20);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_LEGGINGS"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_LEGGINGS"), 0.16);
			// Boots
			addAllMaterials(BOOTS_MATERIALS, "DIAMOND_BOOTS", "GOLDEN_BOOTS", "IRON_BOOTS", "LEATHER_BOOTS", "CHAINMAIL_BOOTS");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_BOOTS"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_BOOTS"), 0.04);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_BOOTS"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_BOOTS"), 0.04);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_BOOTS"), 0.04);
			// Potions
			addAllMaterials(POTION_MATERIALS, "POTION", "LINGERING_POTION", "SPLASH_POTION");
			// Skulls
			addAllMaterials(SKULL_MATERIALS, "WITHER_SKELETON_SKULL", "WITHER_SKELETON_WALL_SKULL");
			// Weapons
			MATERIAL_SNOW_BALL = getMaterial("SNOWBALL");
			MATERIAL_NETHER_STAR = getMaterial("NETHER_STAR");
			MATERIAL_BLAZE_ROD = getMaterial("BLAZE_ROD");
		}
		else { // v1_12 or lower
			// Sword
			addAllMaterials(SWORD_MATERIALS, "DIAMOND_SWORD", "IRON_SWORD", "STONE_SWORD", "GOLD_SWORD", "WOOD_SWORD");
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("DIAMOND_SWORD"), 7.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("IRON_SWORD"), 6.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("STONE_SWORD"), 5.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("GOLD_SWORD"), 4.0);
			WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("WOOD_SWORD"), 4.0);
			// Pickaxe
			addAllMaterials(PICKAXE_MATERIALS, "DIAMOND_PICKAXE", "IRON_PICKAXE", "STONE_PICKAXE", "GOLD_PICKAXE", "WOOD_PICKAXE");
			allMaterialsTo(WEAPON_DAMAGE_MULTIPLIERS, PICKAXE_MATERIALS, 2.0);
			// Axe
			addAllMaterials(AXE_MATERIALS, "DIAMOND_AXE", "IRON_AXE", "STONE_AXE", "GOLD_AXE", "WOOD_AXE");
			allMaterialsTo(WEAPON_DAMAGE_MULTIPLIERS, AXE_MATERIALS, 3.0);
			// Bow
			BOW_MATERIALS.add(getMaterial("BOW"));
			// Helmet
			addAllMaterials(HELMET_MATERIALS, "DIAMOND_HELMET", "GOLD_HELMET", "IRON_HELMET", "LEATHER_HELMET", "CHAINMAIL_HELMET");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_HELMET"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLD_HELMET"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_HELMET"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_HELMET"), 0.04);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_HELMET"), 0.08);
			// Chestplate
			addAllMaterials(CHESTPLATE_MATERIALS, "DIAMOND_CHESTPLATE", "GOLD_CHESTPLATE", "IRON_CHESTPLATE", "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_CHESTPLATE"), 0.32);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLD_CHESTPLATE"), 0.20);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_CHESTPLATE"), 0.24);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_CHESTPLATE"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_CHESTPLATE"), 0.20);
			// Leggings
			addAllMaterials(LEGGINGS_MATERIALS, "DIAMOND_LEGGINGS", "GOLD_LEGGINGS", "IRON_LEGGINGS", "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_LEGGINGS"), 0.24);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLD_LEGGINGS"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_LEGGINGS"), 0.20);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_LEGGINGS"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_LEGGINGS"), 0.16);
			// Boots
			addAllMaterials(BOOTS_MATERIALS, "DIAMOND_BOOTS", "GOLD_BOOTS", "IRON_BOOTS", "LEATHER_BOOTS", "CHAINMAIL_BOOTS");
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_BOOTS"), 0.12);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLD_BOOTS"), 0.04);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_BOOTS"), 0.08);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_BOOTS"), 0.04);
			ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_BOOTS"), 0.04);
			// Potions
			if (v1_9) {
				addAllMaterials(POTION_MATERIALS, "POTION", "LINGERING_POTION", "SPLASH_POTION");
			}
			else {
				addAllMaterials(POTION_MATERIALS, "POTION");
			}
			// Skulls
			addAllMaterials(SKULL_MATERIALS, "SKULL", "SKULL_ITEM");
			// Weapons
			MATERIAL_SNOW_BALL = getMaterial("SNOW_BALL");
			MATERIAL_NETHER_STAR = getMaterial("NETHER_STAR");
			MATERIAL_BLAZE_ROD = getMaterial("BLAZE_ROD");
		}
	}

	/**
	 * Gets the Material instance for a name (multi-version support).
	 */
	public static Material getMaterial(String name) {
		try {
			return Material.valueOf(name);
		}
		catch (IllegalArgumentException ex) {
			GuardianTargetUtil.getPlugin().getLogger().warning(
				"Sentinel loader failed to handle material name '" + name + "', that material will not " +
					 "function (REPORT THIS ERROR!)");
			return Material.valueOf("STICK");
		}
	}

	/**
	 * Adds a a list of named Materials to a set.
	 */
	public static void addAllMaterials(Set<Material> set, String... matNames) {
		for (String mat : matNames) {
			set.add(getMaterial(mat));
		}
	}

	/**
	 * Adds a set of materials to a map as keys with a specific value.
	 */
	public static void allMaterialsTo(Map<Material, Double> map, Set<Material> set, Double val) {
		for (Material mat : set) {
			map.put(mat, val);
		}
	}

	/**
	 * Gets the set of targets that include an entity type.
	 */
	public static HashSet<GuardianTarget> forEntityType(EntityType type) {
		return WorldGuardianPlugin.entityToTargets.get(type);
	}

	/**
	 * Gets the Sentinel target that matches a name.
	 */
	public static GuardianTarget forName(String name) {
		return WorldGuardianPlugin.targetOptions.get(name.toUpperCase());
	}

	/**
	 * The array of valid names for this target type.
	 */
	public String[] names;

	/**
	 * The name of this target type.
	 */
	public String name() {
		return names[0];
	}

	/**
	 * Constructs the target type instance.
	 */
	public GuardianTarget(EntityType[] types, String... names) {
		this.names = names;
		for (String name : names) {
			WorldGuardianPlugin.targetOptions.put(name, this);
			WorldGuardianPlugin.targetOptions.put(name + "S", this);
		}
		for (EntityType type : types) {
			WorldGuardianPlugin.entityToTargets.get(type).add(this);
		}
	}
}
