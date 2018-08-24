package net.frost_byte.worldguardian.utility;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import org.bukkit.Material;

import java.util.*;

@SuppressWarnings({ "WeakerAccess", "unused" })
@Singleton
public class MaterialUtil
{
	private final WorldGuardianPlugin plugin;
	private static Set<Material> SWORD_MATERIALS = new HashSet<>();
	private static Set<Material> PICKAXE_MATERIALS = new HashSet<>();
	private static Set<Material> AXE_MATERIALS = new HashSet<>();
	private static Set<Material> HELMET_MATERIALS = new HashSet<>();
	private static Set<Material> CHESTPLATE_MATERIALS = new HashSet<>();
	private static Set<Material> LEGGINGS_MATERIALS = new HashSet<>();
	private static Set<Material> BOOTS_MATERIALS = new HashSet<>();
	private static Set<Material> BOW_MATERIALS = new HashSet<>();
	private static Set<Material> POTION_MATERIALS = new HashSet<>();
	private static Set<Material> SKULL_MATERIALS = new HashSet<>();

	private static Map<Material, Double> WEAPON_DAMAGE_MULTIPLIERS = new HashMap<>();
	private static Map<Material, Double> ARMOR_PROTECTION_MULTIPLIERS = new HashMap<>();

	private static Material MATERIAL_SNOW_BALL, MATERIAL_NETHER_STAR, MATERIAL_BLAZE_ROD;

	@Inject MaterialUtil(WorldGuardianPlugin plugin)
	{
		this.plugin = plugin;
		initWeapons();
		initArmor();
	}

	public Material getMaterial(String name)
	{
		try
		{
			return Material.valueOf(name);
		}
		catch (IllegalArgumentException ex)
		{
			plugin.getLogger().warning("Guardian loader failed to handle material name '" + name
					+ "', that material will not function (REPORT THIS ERROR!)");
			return Material.valueOf("STICK");
		}
	}

	private void addAllMaterials(Set<Material> set, String... matNames)
	{
		Arrays.stream(matNames).forEach(mat -> set.add(getMaterial(mat)));
	}

	private void allMaterialsTo(Map<Material, Double> map, Set<Material> set, Double val)
	{
		set.forEach(mat -> map.put(mat, val));
	}

	public Set<Material> getMaterials(String category)
	{
		category = Preconditions.checkNotNull(category, "The category cannot be null");

		switch (category.toLowerCase())
		{
			case "sword":
				return SWORD_MATERIALS;
			case "pickaxe":
				return  PICKAXE_MATERIALS;
			case "axe":
				return AXE_MATERIALS;
			case "helmet":
				return HELMET_MATERIALS;
			case "chestplate":
				return CHESTPLATE_MATERIALS;
			case "leggings":
				return LEGGINGS_MATERIALS;
			case "boots":
				return BOOTS_MATERIALS;
			case "bow":
				return BOW_MATERIALS;
			case "potions":
				return POTION_MATERIALS;
			case "skulls":
				return SKULL_MATERIALS;
		}
		return null;
	}

	public static Material getSnowBall() { return MATERIAL_SNOW_BALL; }
	public static Material getBlazeRod() { return MATERIAL_BLAZE_ROD; }
	public static Material getNetherStar() { return MATERIAL_NETHER_STAR; }

	public double getWeaponDamageModifier(Material material)
	{
		material = Preconditions.checkNotNull(material, "The material cannot be null");
		if (WEAPON_DAMAGE_MULTIPLIERS.containsKey(material))
		{
			return WEAPON_DAMAGE_MULTIPLIERS.get(material);
		}
		return 1.0;
	}

	public boolean shouldTakeDura(Material material)
	{
		return BOW_MATERIALS.contains(material) || SWORD_MATERIALS.contains(material) ||
			PICKAXE_MATERIALS.contains(material) || AXE_MATERIALS.contains(material);
	}

	public double getArmorProtectionModifier(Material material)
	{
		material = Preconditions.checkNotNull(material, "The material cannot be null");
		if (ARMOR_PROTECTION_MULTIPLIERS.containsKey(material))
		{
			return ARMOR_PROTECTION_MULTIPLIERS.get(material);
		}
		return 0.4;
	}

	public boolean isWeapon(Material material)
	{
		return WEAPON_DAMAGE_MULTIPLIERS.containsKey(material);
	}

	public boolean isPotion(Material material)
	{
		return POTION_MATERIALS.contains(material);
	}

	public boolean isBow(Material material)
	{
		return BOW_MATERIALS.contains(material);
	}

	public boolean isSkull(Material material)
	{
		return SKULL_MATERIALS.contains(material);
	}

	private void initWeapons()
	{
		plugin.getLogger().info("MaterialUtil: Initializing Weapons");
		WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("DIAMOND_SWORD"), 7.0);
		WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("IRON_SWORD"), 6.0);
		WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("STONE_SWORD"), 5.0);
		WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("GOLDEN_SWORD"), 4.0);
		WEAPON_DAMAGE_MULTIPLIERS.put(getMaterial("WOODEN_SWORD"), 4.0);

		// Sword
		addAllMaterials(
			SWORD_MATERIALS,
			"DIAMOND_SWORD",
			"IRON_SWORD",
			"STONE_SWORD",
			"GOLDEN_SWORD",
			"WOODEN_SWORD"
		);

		// Pickaxe
		addAllMaterials(
			PICKAXE_MATERIALS,
			"DIAMOND_PICKAXE",
			"IRON_PICKAXE",
			"STONE_PICKAXE",
			"GOLDEN_PICKAXE",
			"WOODEN_PICKAXE"
		);
		allMaterialsTo(WEAPON_DAMAGE_MULTIPLIERS, PICKAXE_MATERIALS, 2.0);

		// Axe
		addAllMaterials(
			AXE_MATERIALS,
			"DIAMOND_AXE",
			"IRON_AXE",
			"STONE_AXE",
			"GOLDEN_AXE",
			"WOODEN_AXE"
		);

		allMaterialsTo(
			WEAPON_DAMAGE_MULTIPLIERS,
			AXE_MATERIALS,
			3.0
		);

		// Bow
		BOW_MATERIALS.add(getMaterial("BOW"));

	}

	private void initArmor()
	{
		plugin.getLogger().info("MaterialUtil: Initializing Armor");

		addAllMaterials(
			HELMET_MATERIALS,
			"DIAMOND_HELMET",
			"GOLDEN_HELMET",
			"IRON_HELMET",
			"LEATHER_HELMET",
			"CHAINMAIL_HELMET"
		);

		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_HELMET"), 0.12);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_HELMET"), 0.08);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_HELMET"), 0.08);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_HELMET"), 0.04);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_HELMET"), 0.08);

		// Chestplate
		addAllMaterials(
			CHESTPLATE_MATERIALS,
			"DIAMOND_CHESTPLATE",
			"GOLDEN_CHESTPLATE",
			"IRON_CHESTPLATE",
			"LEATHER_CHESTPLATE",
			"CHAINMAIL_CHESTPLATE"
		);

		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_CHESTPLATE"), 0.32);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_CHESTPLATE"), 0.20);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_CHESTPLATE"), 0.24);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_CHESTPLATE"), 0.12);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_CHESTPLATE"), 0.20);

		// Leggings
		addAllMaterials(
			LEGGINGS_MATERIALS,
			"DIAMOND_LEGGINGS",
			"GOLDEN_LEGGINGS",
			"IRON_LEGGINGS",
			"LEATHER_LEGGINGS",
			"CHAINMAIL_LEGGINGS"
		);

		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_LEGGINGS"), 0.24);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_LEGGINGS"), 0.12);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_LEGGINGS"), 0.20);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_LEGGINGS"), 0.08);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_LEGGINGS"), 0.16);

		// Boots
		addAllMaterials(
			BOOTS_MATERIALS,
			"DIAMOND_BOOTS",
			"GOLDEN_BOOTS",
			"IRON_BOOTS",
			"LEATHER_BOOTS",
			"CHAINMAIL_BOOTS"
		);

		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("DIAMOND_BOOTS"), 0.12);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("GOLDEN_BOOTS"), 0.04);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("IRON_BOOTS"), 0.08);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("LEATHER_BOOTS"), 0.04);
		ARMOR_PROTECTION_MULTIPLIERS.put(getMaterial("CHAINMAIL_BOOTS"), 0.04);

		// Potions
		addAllMaterials(POTION_MATERIALS,
			"POTION",
			"LINGERING_POTION",
			"SPLASH_POTION"
		);

		// Skulls
		addAllMaterials(SKULL_MATERIALS,
			"WITHER_SKELETON_SKULL",
			"WITHER_SKELETON_WALL_SKULL"
		);

		// Weapons
		MATERIAL_SNOW_BALL = getMaterial("SNOWBALL");
		MATERIAL_NETHER_STAR = getMaterial("NETHER_STAR");
		MATERIAL_BLAZE_ROD = getMaterial("BLAZE_ROD");
	}
}
