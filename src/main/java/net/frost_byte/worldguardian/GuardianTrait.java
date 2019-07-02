package net.frost_byte.worldguardian;

import com.google.inject.Inject;

import com.google.inject.throwingproviders.CheckedProvider;
import me.lucko.luckperms.api.LuckPermsApi;
import net.citizensnpcs.api.CitizensAPI;

import net.citizensnpcs.api.ai.TeleportStuckAction;
import net.citizensnpcs.api.ai.speech.SpeechContext;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.persistence.PersistenceLoader;
import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.trait.trait.Spawned;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.CurrentLocation;
import net.citizensnpcs.trait.waypoint.WanderWaypointProvider;
import net.citizensnpcs.trait.waypoint.Waypoint;
import net.citizensnpcs.trait.waypoint.WaypointProvider;
import net.citizensnpcs.trait.waypoint.Waypoints;
import net.citizensnpcs.util.NMS;
import net.citizensnpcs.util.PlayerAnimation;
import net.frost_byte.worldguardian.targeting.GuardianTargetList;
import net.frost_byte.worldguardian.targeting.GuardianTargetingHelper;
import net.frost_byte.worldguardian.utility.*;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.*;
import static net.frost_byte.worldguardian.utility.GuardianTargetType.*;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;
import static net.frost_byte.worldguardian.utility.GuardianUtilities.pickNear;
import static net.frost_byte.worldguardian.utility.MaterialUtil.*;

import static net.md_5.bungee.api.ChatColor.*;
import static org.bukkit.event.entity.EntityDamageEvent.*;
import static org.bukkit.event.player.PlayerTeleportEvent.*;

@SuppressWarnings({ "WeakerAccess", "deprecation", "unused" })
public class GuardianTrait extends Trait
{
	/**
	 * Constant: the smallest health value that can be given to an NPC.
	 */
	public static final double healthMin = 0.01;

	/**
	 * Constant: the maximum attack rate value (in ticks).
	 */
	public static final int attackRateMax = 2000;

	/**
	 * Constant: the maximum targeted rate value (in ticks).
	 */
	public static final int targetedRateMax = 2000;

	/**
	 * Constant: the maximum heal rate value (in ticks).
	 */
	public static final int healRateMax = 2000;

	/**
	 * Constant: The newline separator
	 */
	public static final String SEP = "\n";

	/**
	 * Helper for targeting logic.
	 */
	public GuardianTargetingHelper targetingHelper;

	/**
	 * Helper for items.
	 */
	public GuardianItemHelper itemHelper;

	/**
	 * Helper for weapons.
	 */
	public GuardianWeaponHelper weaponHelper;

	/**
	 * Helper for attacking.
	 */
	public GuardianAttackHelper attackHelper;

	/**
	 * Tick counter for use with giving up on targets that can't be seen
	 * (to avoid being overly 'clever' and chasing a target the NPC shouldn't be able to locate).
	 */
	int cleverTicks = 0;

	/**
	 * Tick counter for the {@code run} method.
	 */
	public int cTick = 0;

	/**
	 * Whether the NPC has chased a target during the most recent update.
	 */
	public boolean chased = false;

	/**
	 * Internally tracks whether the damage enforcement system can be used (protects against infinite loops).
	 */
	private boolean canEnforce = false;

	private boolean debugging = false;

	private static DamageModifier[] modifiersToZero = new DamageModifier[]{
		DamageModifier.HARD_HAT, DamageModifier.BLOCKING,
		DamageModifier.RESISTANCE, DamageModifier.MAGIC,
		DamageModifier.ABSORPTION
	};

	/**
	 * Time since the last attack.
	 */
	public long timeSinceAttack = 0;

	/**
	 * Time since the last heal.
	 */
	public long timeSinceHeal = 0;

	/**
	 * Time since the last targeted by an enemy.
	 */
	public long timeSinceTargeted = 0;

	/**
	 * The target entity this NPC is chasing (if any).
	 */
	public LivingEntity chasing = null;

	/**
	 * Runnable for respawning, if needed.
	 */
	public BukkitRunnable respawnMe;

	/**
	 * Last known entity UUID for this Guardian NPC.
	 */
	public UUID lastEntityUUID;

	/**
	 * Entities that will need their drops cleared if they die soon (because they were killed by this NPC).
	 */
	public HashSet<UUID> needsDropsClear = new HashSet<>();

	/**
	 * Players in range of the NPC that have already been greeted.
	 */
	private HashSet<UUID> greetedAlready = new HashSet<>();

	/**
	 * The maximum distance value (squared) for some distance calculations.
	 * Equal to ten-thousand (10000) blocks, squared (so: 100000000).
	 */
	private final static double MAX_DIST = 100000000;

	/**
	 * Tick counter for the NPC guarding a player (to avoid updating positions too quickly).
	 */
	public int ticksCountGuard = 0;

	/**
	 * Set true when waypoints are paused by Guardian, to indicate that an unpause is needed
	 * (to avoid over-doing the unpause call, which can disrupt unrelated unpauses).
	 */
	public boolean needsToUnpause = false;

	/**
	 * Indicates that the NPC needs to return to safety when next possible.
	 */
	public boolean needsSafeReturn = true;

	/**
	 * Special case for where the NPC has been forced to run to in certain situations.
	 */
	public Location pathingTo = null;

	/**
	 * Whether the waypoints helper (up-to-date Citizens) is available.
	 */
	private Boolean waypointHelperAvailable = null;

	public interface TraitProvider<T> extends CheckedProvider<T>
	{
		T get();
	}

	/**
	 * The Plugin Instance
	 */
	@Inject
	private WorldGuardianPlugin plugin;

	/**
	 * LuckPerms Api
	 */
	@SuppressWarnings("FieldCanBeLocal")
	@Inject
	private LuckPermsApi luckPermsApi;

	public GuardianTrait(){
		super("guardian");
		targetingHelper = new GuardianTargetingHelper();
		itemHelper = new GuardianItemHelper();
		weaponHelper = new GuardianWeaponHelper();
		attackHelper = new GuardianAttackHelper();
		targetingHelper.setTraitObject(this);
		itemHelper.setTraitObject(this);
		weaponHelper.setTraitObject(this);
		attackHelper.setTraitObject(this);
	}

	public void init(WorldGuardianPlugin plugin, LuckPermsApi luckPermsApi)
	{
		this.plugin = plugin;
		this.luckPermsApi = luckPermsApi;
	}

	@Persist("stats_ticksSpawned")
	public long stats_ticksSpawned = 0;

	@Persist("stats_timesSpawned")
	public long stats_timesSpawned = 0;

	@Persist("stats_arrowsFired")
	public long stats_arrowsFired = 0;

	@Persist("stats_potionsThrow")
	public long stats_potionsThrown = 0;

	@Persist("stats_fireballsFired")
	public long stats_fireballsFired = 0;

	@Persist("stats_snowballsThrown")
	public long stats_snowballsThrown = 0;

	@Persist("stats_eggsThrown")
	public long stats_eggsThrown = 0;

	@Persist("stats_skullsThrown")
	public long stats_skullsThrown = 0;

	@Persist("stats_pearlsUsed")
	public long stats_pearlsUsed = 0;

	@Persist("stats_punches")
	public long stats_punches = 0;

	@Persist("stats_attackAttempts")
	public long stats_attackAttempts = 0;

	@Persist("stats_damageTaken")
	public double stats_damageTaken = 0;

	@Persist("stats_damageGiven")
	public double stats_damageGiven = 0;

	@Persist("dialogue")
	public List<String> dialogue = new ArrayList<>();

	@Persist("farewell")
	public List<String> farewell = new ArrayList<>();

	/**
	 * How far to stay from avoid targets.
	 */
	@Persist("avoid_range")
	public double avoidRange = 10.0;

	/**
	 * Maximum range to trigger attacks from.
	 */
	@Persist("range")
	public double range = 20.0;

	/**
	 * The NPC's damage value (-1 means automatically calculated from weapon, anything else is equal to the HP lost by an unarmored target).
	 */
	@Persist("damage")
	public double damage = -1.0;

	/**
	 * The NPC's armor value (-1 means automatically calculated from equipment, 0 means no armor and 1 means invincible armor... decimals between 0 and 1 are normal).
	 */
	@Persist("armor")
	public double armor = -1.0;

	/**
	 * The NPC's maximum health (NOT its current health when the NPC is spawned and injured).
	 */
	@Persist("health")
	public double health = 20.0;

	/**
	 * Whether the NPC chases targets when using ranged weapons.
	 */
	@Persist("ranged_chase")
	public boolean rangedChase = false;

	/**
	 * Whether the NPC chases targets when using melee weapons.
	 */
	@Persist("close_chase")
	public boolean closeChase = true;

	/**
	 * Whether the NPC cannot be harmed (true = no harm, false = receives damage normally).
	 */
	@Persist("invincible")
	public boolean invincible = false;

	/**
	 * Whether the NPC "fights back" against attacks (targets anyone that damages it).
	 */
	@Persist("fightback")
	public boolean fightback = true;

	/**
	 * Whether the NPC runs away when attacked.
	 */
	@Persist("runaway")
	public boolean runaway = false;

	/**
	 * How long (in ticks) between using melee attacks.
	 */
	@Persist("attackRate")
	public int attackRate = 30;

	/**
	 * How long (in ticks) before the NPC can be targeted.
	 */
	@Persist("targetedRate")
	public int targetedRate = 30;

	/**
	 * How long (in ticks) between firing ranged shots.
	 */
	@Persist("attackRateRanged")
	public int attackRateRanged = 30;

	/**
	 * How long (in ticks) before the NPC heals by 1 HP (when damaged).
	 */
	@Persist("healRate")
	public int healRate = 30;

	/**
	 * Upper 64 bits of the guarded player's UUID.
	 */
	@Persist("guardingUpper")
	public long guardingUpper = 0;

	/**
	 * Lower 64 bits of the guarded player's UUID.
	 */
	@Persist("guardingLower")
	public long guardingLower = 0;

	/**
	 * Whether the NPC needs ammo to fire ranged weapons (otherwise, infinite ammo).
	 */
	@Persist("needsAmmo")
	public boolean needsAmmo = false;

	/**
	 * Whether to protect NPC arrow shots from damaging targets that weren't meant to be hit.
	 */
	@Persist("safeShot")
	public boolean safeShot = true;

	/**
	 * How long (in ticks) after death before the NPC respawns.
	 */
	@Persist("respawnTime")
	public long respawnTime = 100;

	/**
	 * The maximum distance from a guard point the NPC can run (when chasing a target).
	 */
	@Persist("chaseRange")
	public double chaseRange = 100;

	/**
	 * The NPC's respawn location (null = respawn where the NPC died at).
	 */
	@Persist("spawnPoint")
	public Location spawnPoint = null;

	/**
	 * The NPC's avoid return point (null = just run away).
	 */
	@Persist("avoidReturnPoint")
	public Location avoidReturnPoint = null;

	/**
	 * What the NPC drops when dead.
	 */
	@Persist("drops")
	public List<ItemStack> drops = new ArrayList<>();

	/**
	 * Whether mob targets killed by the NPC can drop items.
	 */
	@Persist("enemyDrops")
	public boolean enemyDrops = false;

	/**
	 * How long (in ticks) to retain an enemy target when out-of-view.
	 */
	@Persist("enemyTargetTime")
	public long enemyTargetTime = 0;

	/**
	 * How fast the NPC moves when chasing (1 = normal speed).
	 */
	@Persist("speed")
	public double speed = 1;

	/**
	 * The text to warn enemy players with (empty string = no greeting).
	 */
	@Persist("warning_text")
	public String warningText = "";

	/**
	 * The text to greet friendly players with (empty string = no greeting).
	 */
	@Persist("greeting_text")
	public String greetingText = "";

	/**
	 * The range this NPC gives greetings or warnings at.
	 */
	@Persist("greet_range")
	public double greetRange = 10;

	/**
	 * Whether this NPC automatically switches weapons.
	 */
	@Persist("autoswitch")
	public boolean autoswitch = false;

	/**
	 * The name of the squad this NPC is in (null for no squad).
	 */
	@Persist("squad")
	public String squad = null;

	/**
	 * The NPC's accuracy value (0 = perfectly accurate).
	 */
	@Persist("accuracy")
	public double accuracy = 0;

	/**
	 * Whether this NPC should have 'realistic' targeting.
	 */
	@Persist("realistic")
	public boolean realistic = false;

	/**
	 * How far this NPC's punches can reach.
	 */
	@Persist("reach")
	public double reach = 3;

	/**
	 * Minimum distance before choosing a new point (relative to guarded player).
	 */
	@Persist("guard_distance_minimum")
	public double guardDistanceMinimum = 7;

	/**
	 * Maximum possible distance of point to choose (relative to the guarded player).
	 */
	@Persist("guard_selection_range")
	public double guardSelectionRange = 4;

	/**
	 * The Source World for this NPC
	 */
	@Persist("sourceWorld")
	public String sourceWorld = null;

	/**
	 * The Source Location for this NPC
	 */
	@Persist("sourceLocation")
	public Location sourceLocation = null;

	/**
	 * The Destination world for this NPC,
	 * when players shift-click on it
	 */
	@Persist("destinationWorld")
	public String destinationWorld = null;

	/**
	 * The Destination Location for this NPC,
	 * when players shift-click on it
	 */
	@Persist("destinationLocation")
	public Location destinationLocation = null;

	@Persist("allTargets")
	public GuardianTargetList allTargets = new GuardianTargetList();

	@Persist("allIgnores")
	public GuardianTargetList allIgnores = new GuardianTargetList();

	@Persist("allAvoids")
	public GuardianTargetList allAvoids = new GuardianTargetList();

	public static class GuardianTargetListPersister implements Persister<GuardianTargetList>
	{
		@Override
		public GuardianTargetList create(DataKey dataKey) {
			return PersistenceLoader.load(new GuardianTargetList(), dataKey);
		}

		@Override
		public void save(GuardianTargetList o, DataKey dataKey) {
			PersistenceLoader.save(o, dataKey);
		}
	}

	static {
		PersistenceLoader.registerPersistDelegate(GuardianTargetList.class, GuardianTargetListPersister.class);
	}

	/**
	 * Updater for older WorldGuardian saves (up to 1.7.2)
	 */
	@Override
	public void load(final DataKey key) {
		new BukkitRunnable() {
			@Override
			public void run() {
				updateOld(key);
			}
		}.runTaskLater(plugin, 1);
	}

	@Override
	public void save(DataKey key) {
		for (DataKey subkey : key.getSubKeys()) {
			if (subkey.name().equals("targets")
				|| subkey.name().equals("ignores")
				|| subkey.name().endsWith("Targets")
				|| subkey.name().endsWith("Ignores")) {
				key.removeKey(subkey.name());
			}
		}
	}

	/**
	 * Updater for older WorldGuardian saves (up to 1.7.2)
	 */
	public void updateOld(DataKey key) {
		for (DataKey subkey : key.getSubKeys()) {
			if (subkey.name().equals("targets")) {
				for (DataKey listEntry : subkey.getSubKeys()) {
					allTargets.targets.add(listEntry.getRaw("").toString());
				}
				allTargets.recalculateTargetsCache();
			}
			else if (subkey.name().equals("ignores")) {
				for (DataKey listEntry : subkey.getSubKeys()) {
					allIgnores.targets.add(listEntry.getRaw("").toString());
				}
				allIgnores.recalculateTargetsCache();
			}
			if (subkey.name().endsWith("Targets")) {
				allTargets.updateOld(subkey, subkey.name().substring(0, subkey.name().length() - "Targets".length()));
			}
			else if (subkey.name().endsWith("Ignores")) {
				allIgnores.updateOld(subkey, subkey.name().substring(0, subkey.name().length() - "Ignores".length()));
			}
		}
	}

	public UUID getGuarding() {
		if (guardingLower == 0 && guardingUpper == 0) {
			return null;
		}
		return new UUID(guardingUpper, guardingLower);
	}

	public void setGuarding(UUID uuid) {
		if (uuid == null) {
			guardingUpper = 0;
			guardingLower = 0;
		}
		else {
			guardingUpper = uuid.getMostSignificantBits();
			guardingLower = uuid.getLeastSignificantBits();
		}
	}

	/**
	 * Retrieve the debugging State for the Guardian
	 * @return True if debugging is enabled for the guardian, otherwise false.
	 */
	public boolean getDebugging()
	{
		return debugging;
	}

	/**
	 * Set the debugging State for the Guardian
	 * @param debug The new debugging state for the guardian, true = enabled
	 */
	public void setDebugging(boolean debug)
	{
		debugging = debug;
	}

	public void toggleDebugging()
	{
		debugging = !debugging;

		if (debugging)
			debugMe = true;
	}

	/**
	 * Called when this Guardian gets attacked, to correct the armor handling.
	 */
	public void whenAttacksAreHappeningToMe(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!event.isApplicable(DamageModifier.ARMOR)) {
			event.setDamage(
				DamageModifier.BASE,
				(1.0 - getArmor(getLivingEntity())) * event.getDamage(DamageModifier.BASE)
			);
		}
		else {
			event.setDamage(
				DamageModifier.ARMOR,
				-getArmor(getLivingEntity()) * event.getDamage(DamageModifier.BASE)
			);
		}
		for (DamageModifier modifier : modifiersToZero) {
			if (event.isApplicable(modifier)) {
				event.setDamage(modifier, 0);
			}
		}
	}

	/**
	 * Called when this guardian attacks something, to correct damage handling.
	 */
	public void whenAttacksAreHappeningFromMe(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (GuardianTargetUtil.getPlugin().alternateDamage) {
			if (canEnforce) {
				canEnforce = false;
				whenAttacksHappened(event);
				if (!event.isCancelled()) {
					((LivingEntity) event.getEntity()).damage(event.getFinalDamage());
					if (event.getEntity() instanceof LivingEntity) {
						weaponHelper.knockback((LivingEntity) event.getEntity());
					}
				}
				debug("enforce damage value to " + event.getFinalDamage());
			}
			else {
				debug("refuse damage enforcement");
			}
			event.setDamage(0);
			event.setCancelled(true);
			return;
		}
		event.setDamage(DamageModifier.BASE, getDamage());
	}

	/**
	 * Called when this guardian attacks something with a projectile, to correct damage handling.
	 */
	public void whenAttacksAreHappeningFromMyArrow(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (GuardianTargetUtil.getPlugin().alternateDamage) {
			if (canEnforce) {
				canEnforce = false;
				whenAttacksHappened(event);
				if (!event.isCancelled()) {
					((LivingEntity) event.getEntity()).damage(getDamage());
					if (event.getEntity() instanceof LivingEntity) {
						weaponHelper.knockback((LivingEntity) event.getEntity());
					}
				}
				debug("enforce damage value to " + getDamage());
			}
			else {
				debug("refuse damage enforcement");
			}
			event.setDamage(0);
			event.setCancelled(true);
			return;
		}
		double dam = getDamage();
		double modder = event.getDamage(DamageModifier.BASE);
		double rel = modder == 0.0 ? 1.0 : dam / modder;
		event.setDamage(DamageModifier.BASE, dam);
		for (DamageModifier mod : DamageModifier.values()) {
			if (mod != DamageModifier.BASE && event.isApplicable(mod)) {
				event.setDamage(mod, event.getDamage(mod) * rel);
				debug("Set damage for " + mod + " to " + event.getDamage(mod));
			}
		}
	}

	/**
	 * Called when combat occurs in the world (and has not yet been processed by other plugins),
	 * to handle things like cancelling invalid damage to/from a Guardian NPC,
	 * changing damage values given to or received from an NPC,
	 * and if relevant handling config options that require overriding damage events.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void whenAttacksAreHappening(EntityDamageByEntityEvent event) {
		debug("GuardianTrait.whenAttacksAreHappening");

		if (!npc.isSpawned())
		{
			debug("Guardian: noNpcSpawned");

			return;
		}
		if (event.isCancelled())
		{
			debug("Guardian: eventCancelled");

			return;
		}

		double finalDamage = event.getFinalDamage();
		LivingEntity me = getLivingEntity();
		UUID myID = me.getUniqueId();

		Entity victim = event.getEntity();
		UUID victimID = victim.getUniqueId();

		Entity attacker = event.getDamager();
		UUID attackerID = attacker.getUniqueId();

		boolean imBeingAttacked = victimID.equals(myID);
		boolean imAttacking = attackerID.equals(myID);

		if (debugMe)
		{
			String debugFormat = "me ( %s, %s ), victim ( %s, %s ), attacker ( %s, %s )";
			String debugOutput = String.format(
				debugFormat,
				me.toString(),
				myID.toString(),
				victim.toString(),
				victimID,
				attacker.toString(),
				attackerID
			);

			debug(debugOutput);
		}

		// The Guardian is being attacked by someone
		if (imBeingAttacked)
		{
			debug("Guardian: " + me.getCustomName() + " Attacked!");

			if (!event.isApplicable(DamageModifier.ARMOR))
			{
				debug("Guardian: event Applicable, setting Damage");

				event.setDamage(
					DamageModifier.BASE,
					(1.0 - getArmor(me)) * event.getDamage(DamageModifier.BASE)
				);
			}
			else
			{
				debug("Guardian: event NOT Applicable, setting Damage");

				event.setDamage(
					DamageModifier.ARMOR,
					-getArmor(me) * event.getDamage(DamageModifier.BASE)
				);
			}
			return;
		}

		// The guardian is attacking someone
		if (imAttacking)
		{
			debug("Guardian: " + me.getCustomName() + " Attacking!");

			if (plugin.alternateDamage)
			{
				if (canEnforce)
				{
					debug("Guardian: enforcing Damage!");

					canEnforce = false;
					whenAttacksHappened(event);
					if (!event.isCancelled())
					{
						debug("Guardian: " + me.getCustomName() + "Attacking, event not cancelled!");
						me.damage(finalDamage);
					}
					debug("Guardian: enforce damage value to " + finalDamage);
				}
				else {
					debug("Guardian: refuse damage enforcement");
				}
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
			event.setDamage(DamageModifier.BASE, getDamage());
		}

		// The source of damage was a projectile
		if (attacker instanceof Projectile) {
			debug("Guardian: Projectile Attack!");

			ProjectileSource source = ((Projectile) attacker).getShooter();

			// The Guardian is attacking with a Projectile.
			if (source instanceof LivingEntity && ((LivingEntity) source).getUniqueId().equals(myID)) {
				debug("Guardian " + me.getCustomName() + " Attacking with Projectile!");

				if (plugin.alternateDamage) {
					if (canEnforce) {
						canEnforce = false;
						whenAttacksHappened(event);
						if (!event.isCancelled()) {
							((LivingEntity) victim).damage(getDamage());
						}
						debug("Guardian: enforce projectile damage value to " + getDamage());
					}
					else {
						debug("Guardian: refuse projectile damage enforcement");
					}
					event.setDamage(0);
					event.setCancelled(true);
					return;
				}
				debug("Guardian: Applying projectile damage from guardian");

				double dam = getDamage();
				double modder = event.getDamage(DamageModifier.BASE);
				double rel = modder == 0.0 ? 1.0 : dam / modder;
				event.setDamage(DamageModifier.BASE, dam);

				for (DamageModifier mod : DamageModifier.values())
				{
					if (mod != DamageModifier.BASE && event.isApplicable(mod))
					{
						event.setDamage(mod, event.getDamage(mod) * rel);
						debug("Guardian: Set damage for " + mod + " to " + event.getDamage(mod));
					}
				}
			}
		}
	}

	private EntityDamageByEntityEvent damageDeDup = null;

	/**
	 * Called when combat has occurred in the world (and has been processed by all other plugins), to handle things like
	 * cancelling invalid damage to/from a Guardian NPC, adding targets (if combat occurs near an NPC), and, if relevant
	 * handling config options that require overriding damage events.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void whenAttacksHappened(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;

		if (event == damageDeDup) {
			return;
		}
		damageDeDup = event;
		double finalDamage = event.getFinalDamage();
		LivingEntity me = getLivingEntity();
		LivingEntity projectileSource = null;

		if (me == null)
		{
			debug("whenAttacksHappened: Invalid LivingEntity for GuardianTrait");
			return;
		}

		UUID myID = me.getUniqueId();

		Entity victim = event.getEntity();
		UUID victimID = victim.getUniqueId();

		Entity attacker = event.getDamager();
		UUID attackerID = attacker.getUniqueId();

		if (attacker instanceof Projectile) {
			ProjectileSource source = ((Projectile) attacker).getShooter();
			if (source instanceof LivingEntity) {
				projectileSource = (LivingEntity) source;
				attacker = projectileSource;
			}
		}
		boolean imBeingAttacked = victimID.equals(myID);
		boolean imAttacking = attackerID.equals(myID);

		String debugFormat = "me ( %s, %s ), victim ( %s, %s ), attacker ( %s, %s )";
		String debugOutput = String.format(
			debugFormat,
			me.toString(),
			myID.toString(),
			victim.toString(),
			victimID,
			attacker.toString(),
			attackerID
		);

		debug(debugOutput);

		if (plugin.protectFromIgnores && imBeingAttacked)
		{
			if (attacker instanceof LivingEntity && targetingHelper.isIgnored((LivingEntity) attacker)) {
				event.setCancelled(true);
				return;
			}
			else if (projectileSource != null && targetingHelper.isIgnored(projectileSource)) {
				event.setCancelled(true);
				return;
			}
		}

		boolean isKilling = victim instanceof LivingEntity && finalDamage >= ((LivingEntity) victim).getHealth();
		boolean isFriend = getGuarding() != null && victimID.equals(getGuarding());

		if (imBeingAttacked || isFriend)
		{
			if (imAttacking)
			{
				debug("Ignoring damage I did to " + (imBeingAttacked ? "myself." : "my friend."));
				event.setCancelled(true);
				return;
			}

			if (isFriend) {
				if (imBeingAttacked && plugin.noGuardDamage) {
					debug("Ignoring damage from the player we're guarding.");
					event.setCancelled(true);
				}
				return;
			}

			stats_damageTaken += finalDamage;

			if (runaway) {
				debug("Ow! I'm hurt! Run Away!");
				targetingHelper.addAvoid(attackerID);
			}

			if (fightback && (attacker instanceof LivingEntity) && !targetingHelper.isIgnored((LivingEntity) attacker))
			{
				debug("GuardianTrait.whenAttacksHappened: Guardian " + me.getCustomName() +
					" Adding attacker as target!");

				targetingHelper.addTarget(attackerID);
			}

			debug(
				"Took damage of " + finalDamage + " with currently remaining health " + getLivingEntity().getHealth()
				+ (isKilling ? ". This will kill me." : ".")
			);

			if (isKilling && plugin.blockEvents)
			{
				debug("Died! Applying death workaround (due to config setting)");

				generalDeathHandler(getLivingEntity());
				npc.despawn(DespawnReason.PLUGIN);
				event.setCancelled(true);
				return;
			}
			return;
		}

		if (imAttacking) {
			debug("Guardian " + me.getCustomName() + " attacked itself!");

			if (safeShot && !targetingHelper.shouldTarget((LivingEntity) victim)) {
				event.setCancelled(true);
				return;
			}
			stats_damageGiven += event.getFinalDamage();

			if (!enemyDrops && victim.getType() != EntityType.PLAYER) {
				needsDropsClear.add(victimID);

				debug(
					"This " + victim.getType() + " with id " + victimID + " is being tracked for "
					+ "potential drops removal."
				);
			}
		}
	}

	private GuardianCurrentTarget tempTarget = new GuardianCurrentTarget();
	public void whenAnEnemyDies(UUID dead) {
		tempTarget.targetID = dead;
		targetingHelper.currentTargets.remove(tempTarget);
		targetingHelper.currentAvoids.remove(tempTarget);
	}

	@Override
	public void onAttach() {
		plugin = (plugin == null) ? GuardianTargetUtil.getPlugin() : plugin;
		if (plugin == null) {
			throw new NullPointerException("The plugin is null.");
		}
		FileConfiguration config = plugin.getConfig();
		attackRate = config.getInt("guardian defaults.attack rate", 30);
		healRate = config.getInt("guardian defaults.heal rate", 30);
		respawnTime = config.getInt("guardian defaults.respawn time", 100);
		rangedChase = config.getBoolean("guardian defaults.ranged chase target", false);
		closeChase = config.getBoolean("guardian defaults.close chase target", true);
		armor = config.getDouble("guardian defaults.armor", -1);
		damage = config.getDouble("guardian defaults.damage", -1);
		health = config.getDouble("guardian defaults.health", 20);
		if (npc.isSpawned()) {
			getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
			getLivingEntity().setHealth(health);
		}
		setInvincible(config.getBoolean("guardian defaults.invincible", false));
		fightback = config.getBoolean("guardian defaults.fightback", true);
		needsAmmo = config.getBoolean("guardian defaults.needs ammo", false);
		safeShot = config.getBoolean("guardian defaults.safe shot", true);
		enemyDrops = config.getBoolean("guardian defaults.enemy drops", false);
		enemyTargetTime = config.getInt("guardian defaults.enemy target time", 0);
		speed = config.getInt("guardian defaults.speed", 1);
		if (speed <= 0) {
			speed = 1;
		}
		autoswitch = config.getBoolean("guardian defaults.autoswitch", false);
		allIgnores.targets.add(OWNER.name());
		allIgnores.recalculateTargetsCache();

		reach = config.getDouble("reach", 3);
		avoidRange = config.getDouble("guardian defaults.avoid range", 10);
		runaway = config.getBoolean("guardian defaults.runaway", false);
		guardDistanceMinimum = plugin.guardDistanceMinimum;
		guardSelectionRange = plugin.guardDistanceSelectionRange;
		if (npc.isSpawned()) {
			plugin.currentGuardianNPCs.add(this);
			lastEntityUUID = getLivingEntity().getUniqueId();
		}
	}

	/**
	 * Called when the Sentinel trait is removed from an NPC.
	 */
	@Override
	public void onRemove() {
		plugin.currentGuardianNPCs.remove(this);
	}

	/**
	 * Animates the NPC using their item, and stops the animation 10 ticks later (useful for replicating bow draws, etc).
	 */
	public void useItem() {
		if (npc.isSpawned() && getLivingEntity() instanceof Player) {
			if (v1_9) {
				PlayerAnimation.START_USE_MAINHAND_ITEM.play((Player) getLivingEntity());
			}
			BukkitRunnable runner = new BukkitRunnable() {
				@Override
				public void run() {
					if (npc.isSpawned() && getLivingEntity() instanceof Player) {
						PlayerAnimation.STOP_USE_ITEM.play((Player) getLivingEntity());
					}
				}
			};
			runner.runTaskLater(plugin, 10);
		}
	}

	/**
	 * Swings the NPC's weapon (plays an ARM_SWING animation if possible - otherwise, does nothing).
	 */
	public void swingWeapon() {
		if (npc.isSpawned() && getLivingEntity() instanceof Player) {
			PlayerAnimation.ARM_SWING.play((Player) getLivingEntity());
		}
	}

	/**
	 * Gets the minimum distance from the NPC's head to launch a projectile from
	 * (to avoid it colliding with the NPC's own collision box).
	 */
	public double firingMinimumRange() {
		EntityType type = getLivingEntity().getType();
		if (type == EntityType.WITHER || type == EntityType.GHAST) {
			return 8; // Yikes!
		}
		return 2;
	}

	/**
	 * Gets a 'launch detail' (starting location for the projectile position, and a vector holding the exact launch vector,
	 * scaled to the correct speed).
	 */
	public HashMap.SimpleEntry<Location, Vector> getLaunchDetail(Location target, Vector lead) {
		faceLocation(target);
		Location start = getLivingEntity()
			.getEyeLocation().clone()
			.add(
				getLivingEntity()
				.getEyeLocation()
				.getDirection()
				.multiply(firingMinimumRange())
			);
		return GuardianUtilities.getLaunchDetail(start, target, lead);
	}

	/**
	 * Returns a random decimal number within acceptable accuracy range (can be negative).
	 */
	public double randomAcc() {
		return GuardianUtilities.random.nextDouble() * accuracy * 2 - accuracy;
	}

	/**
	 * Alters a vector per accuracy potential (makes the vector less accurate).
	 */
	public Vector fixForAcc(Vector input) {
		if (Double.isInfinite(input.getX()) || Double.isNaN(input.getX())) {
			return new Vector(0, 0, 0);
		}
		return new Vector(input.getX() + randomAcc(), input.getY() + randomAcc(), input.getZ() + randomAcc());
	}

	public void faceLocation(Location l) {
		npc.faceLocation(l.clone().subtract(0, getLivingEntity().getEyeHeight(), 0));
	}

	@SuppressWarnings("deprecation")
	public double getDamage()
	{
		if (damage >= 0)
		{
			return damage;
		}
		ItemStack weapon = itemHelper.getHeldItem();

		if (weapon == null)
		{
			return 1;
		}
		// TODO: Less randomness, more game-like calculations.
		double multiplier = 1;
		multiplier += weapon.getItemMeta() == null || !weapon.getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL)
				? 0 : weapon.getItemMeta().getEnchantLevel(Enchantment.DAMAGE_ALL) * 0.2;
		Material weaponType = weapon.getType();
		Set<Material> bowMaterials = getMaterials("bows");
		if (bowMaterials != null && bowMaterials.contains(weaponType)) {
			return 6 * (1 + (weapon.getItemMeta() == null || !weapon.getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE)
					? 0 : weapon.getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE) * 0.3));
		}
		Double damageMult = getWeaponDamageModifier(weaponType);

		return multiplier * damageMult;
	}

	public double getArmor(LivingEntity ent) {
		if (armor < 0) {
			// TODO: Enchantments!
			double baseArmor = 0;
			ItemStack helmet = ent.getEquipment().getHelmet();
			Double helmetAdder = helmet == null ? null : getArmorProtectionModifier(helmet.getType());
			if (helmetAdder != null) {
				baseArmor += helmetAdder;
			}
			ItemStack chestplate = ent.getEquipment().getChestplate();
			Double chestplateAdder = chestplate == null ? null : getArmorProtectionModifier(chestplate.getType());
			if (chestplateAdder != null) {
				baseArmor += chestplateAdder;
			}
			ItemStack leggings = ent.getEquipment().getLeggings();
			Double leggingsAdder = leggings == null ? null : getArmorProtectionModifier(leggings.getType());
			if (leggingsAdder != null) {
				baseArmor += leggingsAdder;
			}
			ItemStack boots = ent.getEquipment().getBoots();
			Double bootsAdder = boots == null ? null : getArmorProtectionModifier(boots.getType());
			if (bootsAdder != null) {
				baseArmor += bootsAdder;
			}
			return Math.min(baseArmor, 0.80);
		}
		return armor;
	}

	public String getInventoryInfo()
	{
		if (!npc.hasTrait(Inventory.class)) {
			return "Inventory is empty";
		}

		ItemStack[] items = npc.getTrait(Inventory.class).getContents();

		if (items == null)
			return "Inventory is empty";

		StringBuilder builder = new StringBuilder();

		builder.append(GOLD)
			.append("Items\n")
			.append("------\n")
			.append(AQUA);

		Arrays.stream(items)
			.filter(Objects::nonNull)
			.forEach(
				i -> builder.append(i.getType())
					.append("\n")
			);

		return builder.toString();
	}

	/**
	 * Gets the living entity for the NPC.
	 */
	public LivingEntity getLivingEntity() {
		// Not a good idea to turn a non-living NPC into a Guardian for now.
		return (LivingEntity) npc.getEntity();
	}

	/**
	 * Marks that the NPC can see a target (Changes the state of som entity types, eg opening a shulker box).
	 */
	public void specialMarkVision() {
		debug("Guardian: Target! I see you, " + (chasing == null ? "(Unknown)" : chasing.getName()));

		if (v1_11 && getLivingEntity().getType() == EntityType.SHULKER) {
			NMS.setPeekShulker(getLivingEntity(), 100);
		}
	}

	/**
	 * Marks that the NPC can no longer a target (Changes the state of som entity types, eg closing a shulker box).
	 */
	public void specialUnmarkVision() {
		debug("Guardian: Goodbye, visible target " + (chasing == null ? "(Unknown)" : chasing.getName()));

		if (v1_11 && getLivingEntity().getType() == EntityType.SHULKER) {
			NMS.setPeekShulker(getLivingEntity(), 0);
		}
	}

	/**
	 * Causes the NPC to immediately path over to a position.
	 */
	public void pathTo(Location target) {
		pauseWaypoints();
		pathingTo = target;
		npc.getNavigator().getDefaultParameters().distanceMargin(1.5);
		getNPC().getNavigator().setTarget(target);
		chasing = null;
		needsSafeReturn = true;
	}

	/**
	 * Pauses waypoint navigation if currrently navigating.
	 */
	public void pauseWaypoints() {
		Waypoints wp = npc.getTrait(Waypoints.class);
		if (!wp.getCurrentProvider().isPaused()) {
			wp.getCurrentProvider().setPaused(true);
		}
		needsToUnpause = true;
		needsSafeReturn = true;
	}

	/**
	 * Runs a full update cycle on the NPC.
	 */
	public void runUpdate() {
		canEnforce = true;
		timeSinceAttack += plugin.tickRate;
		timeSinceHeal += plugin.tickRate;
		timeSinceTargeted += plugin.tickRate;
		LivingEntity me = getLivingEntity();

		if (me.getLocation().getY() <= 0)
		{
			debug("Guardian: Injuring self, I'm below the map!");

			me.damage(1);

			if (!npc.isSpawned())
			{
				Player guarding = (getGuarding() != null) ? Bukkit.getPlayer(getGuarding()) : null;

				if (guarding != null)
				{
					if (respawnTime > 0 && respawnMe == null) {
						npc.spawn(guarding.getLocation());
					}
				}

				return;
			}
		}

		if (health != me.getMaxHealth())
			me.setMaxHealth(health);

		if (healRate > 0 && timeSinceHeal > healRate && me.getHealth() < health)
		{
			me.setHealth(Math.min(me.getHealth() + 1.0, health));
			timeSinceHeal = 0;
		}

		if (!npc.getNavigator().isNavigating()) {
			pathingTo = null;
		}
		if (getGuarding() != null && npc.hasTrait(Waypoints.class))
		{
			Waypoints wp = npc.getTrait(Waypoints.class);
			wp.getCurrentProvider().setPaused(true);
		}
		else if (npc.hasTrait(Waypoints.class))
		{
			Waypoints wp = npc.getTrait(Waypoints.class);
			wp.getCurrentProvider().setPaused(false);
		}

		double crsq = chaseRange * chaseRange;
		targetingHelper.updateTargets();
		boolean goHome = chased;
		LivingEntity target = targetingHelper.findBestTarget();

		if (target != null) {
			Location near = nearestPathPoint();

			debug("Guardian: target selected to be " + target.getName());

			if (crsq <= 0 || near == null || near.distanceSquared(target.getLocation()) <= crsq)
			{
				debug(
					"Guardian: Attack target within range of safe zone: " +
					(near == null ? "Any" : near.distanceSquared(target.getLocation()))
				);

				if (chasing == null)
					specialMarkVision();

				chasing = target;
				cleverTicks = 0;
				attackHelper.tryAttack(target);
				goHome = false;
			}
			else
			{
				debug("Guardian: Actually, that target is bad!");

				specialUnmarkVision();
				chasing = null;
				cleverTicks = 0;
			}
		}
		else if (chasing != null && chasing.isValid())
		{
			if (plugin.workaroundEntityChasePathfinder)
				attackHelper.rechase();

			cleverTicks++;

			if (cleverTicks >= plugin.cleverTicks)
			{
				debug("Guardian: Valid Chasing, Unmarking Vision!");

				specialUnmarkVision();
				chasing = null;
			}
			else
			{
				Location near = nearestPathPoint();

				if (crsq <= 0 || near == null || near.distanceSquared(chasing.getLocation()) <= crsq)
				{
					attackHelper.tryAttack(chasing);
					goHome = false;
				}
			}
		}
		else if (chasing == null)
		{
			debug("Guardian: Invalid Chasing, Unmarking Vision!");

			specialUnmarkVision();
		}

		if (getGuarding() != null)
		{
			Player player = Bukkit.getPlayer(getGuarding());

			if (player != null)
			{
				Location myLoc = getLivingEntity().getLocation();
				Location theirLoc = player.getLocation();
				double dist = theirLoc.getWorld().equals(myLoc.getWorld()) ? myLoc.distanceSquared(theirLoc) : MAX_DIST;

				if (dist > 60 * 60)
				{
					npc.teleport(
						player.getLocation(),
						TeleportCause.PLUGIN
					);
				}
				if (dist > plugin.guardDistanceMinimum * plugin.guardDistanceMinimum)
				{
					ticksCountGuard += plugin.tickRate;

					if (ticksCountGuard >= 30)
					{
						ticksCountGuard = 0;
						npc.getNavigator().getDefaultParameters().distanceMargin(plugin.guardDistanceMargin);
						npc.getNavigator().getDefaultParameters().range(100);
						npc.getNavigator().getDefaultParameters().stuckAction(TeleportStuckAction.INSTANCE);
						npc.getNavigator().setTarget(pickNear(player.getLocation(), plugin.guardDistanceSelectionRange));
						npc.getNavigator().getLocalParameters().speedModifier((float) speed);
						chased = true;
					}
				}
				goHome = false;
			}
		}

		// The Target being chased no longer exists, reorient the Guardian
		if (goHome && chaseRange > 0 && target == null)
		{
			Location near = nearestPathPoint();

			if (near != null && (chasing == null || near.distanceSquared(chasing.getLocation()) > crsq))
			{
				if (near.distanceSquared(getLivingEntity().getLocation()) > 3 * 3) {
					debug("Guardian: screw you guys, I'm going home!");
				}

				npc.getNavigator().getDefaultParameters().stuckAction(TeleportStuckAction.INSTANCE);
				npc.getNavigator().setTarget(near);
				npc.getNavigator().getLocalParameters().speedModifier((float) speed);
				chased = false;
			}
			else
			{
				if (npc.getNavigator().getEntityTarget() != null)
					npc.getNavigator().cancelNavigation();

				if (debugMe)
					if (near != null && near.distanceSquared(getLivingEntity().getLocation()) > 3 * 3)
						debug("Guardian: I'll just stand here and hope they come out...");
			}
		}
		else if (chasing == null && npc.getNavigator().getEntityTarget() != null)
			npc.getNavigator().cancelNavigation();
	}

	public Location getGuardZone()
	{
		if (getGuarding() != null)
		{
			Player player = Bukkit.getPlayer(getGuarding());

			if (player != null)
				return player.getLocation();
		}
		if (chaseRange > 0)
		{
			Location goal = nearestPathPoint();

			if (goal != null)
				return goal;
		}
		return getLivingEntity().getLocation();
	}

	/**
	 * Gets the nearest pathing point to this NPC.
	 */
	public Location nearestPathPoint()
	{
		if (!v1_9)
		{
			if (waypointHelperAvailable == null)
			{
				try
				{
					Class.forName("net.citizensnpcs.trait.waypoint.WaypointProvider.EnumerableWaypointProvider");
					waypointHelperAvailable = true;
				}
				catch (ClassNotFoundException ex)
				{
					waypointHelperAvailable = false;
					plugin.getLogger().warning(
						"Citizens Installation is **very outdated** and does not contain newer useful APIs. " +
						"Please update your installation of the Citizens plugin!"
					);
				}

				if (!waypointHelperAvailable)
					return null;
			}
		}

		if (!npc.hasTrait(Waypoints.class))
			return null;

		if (getGuarding() != null)
			return null;

		Location baseloc = getLivingEntity().getLocation();
		Location nearest = null;
		double dist = MAX_DIST;
		Waypoints wp = npc.getTrait(Waypoints.class);
		if (wp.getCurrentProvider() instanceof WaypointProvider.EnumerableWaypointProvider) {
			// Find the nearest Waypoint to the Guardian's current location
			for (Waypoint wayp : ((WaypointProvider.EnumerableWaypointProvider) wp.getCurrentProvider()).waypoints())
			{
				Location l = wayp.getLocation();

				if (!l.getWorld().equals(baseloc.getWorld()))
					continue;

				double d = baseloc.distanceSquared(l);

				if (d < dist)
				{
					dist = d;
					nearest = l;
				}
			}
		}
		else if (wp.getCurrentProvider() instanceof WanderWaypointProvider) {
			WanderWaypointProvider wwp = (WanderWaypointProvider) wp.getCurrentProvider();
			for (Location loc : wwp.getRegionCentres()) {
				if (!loc.getWorld().equals(baseloc.getWorld())) {
					continue;
				}
				double d = baseloc.distanceSquared(loc);
				if (d < dist) {
					dist = d;
					nearest = loc;
				}
			}
		}
		else {
			return null;
		}

		return nearest;
	}

	/**
	 * Called every tick to run Guardian updates if needed.
	 */
	@Override
	public void run() {
		if (!npc.isSpawned()) {
			return;
		}
		stats_ticksSpawned++;
		cTick++;
		if (cTick >= plugin.tickRate) {
			cTick = 0;
			runUpdate();
		}
	}

	/**
	 * Called when the NPC spawns in.
	 */
	@Override
	public void onSpawn() {
		lastEntityUUID = getLivingEntity().getUniqueId();
		stats_timesSpawned++;
		setHealth(health);
		setInvincible(invincible);
		if (respawnMe != null) {
			respawnMe.cancel();
			respawnMe = null;
		}
		plugin.currentGuardianNPCs.add(this);
	}

	/**
	 * Causes the NPC to speak a message to a player.
	 * TODO: Should this use the Citizens DefaultSpeechController when TabChannels isn't loaded?
	 */
	public void sayTo(Player player, String message) {
		SpeechContext sc = new SpeechContext(npc, message, player);
		String npcName = npc.getName();
		char colorChar = npcName.charAt(1);
		ChatColor npcColor = ChatColor.getByChar(colorChar);

		if (npcColor == null)
			npcColor = GREEN;

		String text = npcColor + "[" + npc.getName() + "] -> You: "+ sc.getMessage() + SEP;
		plugin.sendChannelMessage(player, text);
	}

	public void sayToNearbyPlayers(String message, int distance)
	{
		if (message == null || message.isEmpty() || getLivingEntity() == null)
			return;

		Location location = getLivingEntity().getEyeLocation();
		int distSquared = distance * distance;

		Bukkit.getServer().getOnlinePlayers().stream()
			.filter(pl -> pl.getWorld() == location.getWorld() &&
				location.distanceSquared(pl.getLocation()) < distSquared)
			.findFirst()
			.ifPresent(nearbyPlayer -> sayTo(nearbyPlayer, message));

	}

	/**
	 * Called whenever a player Right Clicks on an NPC
	 * @param event The Right Click Event
	 */
	@EventHandler
	public void onPlayerRightClick(NPCRightClickEvent event)
	{
		if (event.isCancelled())
			return;
		NPC npc = event.getNPC();
		Player player = event.getClicker();

		if (player == null || npc == null || !player.isSneaking() || getLivingEntity() == null)
			return;

		UUID myID = getLivingEntity().getUniqueId();
		UUID npcID = npc.getEntity().getUniqueId();

		if (npcID == null || npcID != myID)
			return;

		// Player must sneak when interacting
		if (destinationLocation != null)
		{
			if (farewell != null && !farewell.isEmpty())
			{
				int index = NumberUtil.randomInt(0, farewell.size() - 1);
				sayTo(player, farewell.get(index));
			}
			else {
				sayTo(player, "Safe journeys!");
			}
			player.teleport(destinationLocation, TeleportCause.PLUGIN);
			event.setCancelled(true);
		}
	}

	/**
	 * Called whenever a player teleports, for use with NPC guarding logic.
	 */
	public void onPlayerTeleports(final PlayerTeleportEvent event) {
		if (
			event.isCancelled() ||
			getGuarding() == null ||
			!event.getPlayer().getUniqueId().equals(getGuarding()) ||
			!npc.isSpawned()
		){
			return;
		}

		if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			npc.teleport(event.getTo(), TeleportCause.PLUGIN);
		}
		else { // World loading up can cause glitches.
			event.getFrom().getChunk().load();
			event.getTo().getChunk().load();
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				if (!event.getPlayer().getWorld().equals(event.getTo().getWorld())) {
					return;
				}
				event.getFrom().getChunk().load();
				event.getTo().getChunk().load();
				npc.spawn(event.getTo());
			}, 1);
		}
	}

	/**
	 * Called whenever an entity targets the guardian.
	 */
	@EventHandler
	public void onEntityTargetGuardian(EntityTargetLivingEntityEvent event)
	{
		if (
			!npc.isSpawned() ||
			timeSinceTargeted < targetedRate ||
			event.getEntity() == null ||
			!(event.getEntity() instanceof LivingEntity) ||
			event.getTarget() == null ||
			getLivingEntity() == null
		){
			return;
		}

		LivingEntity targeter = (LivingEntity) event.getEntity();
		LivingEntity target = event.getTarget();
		UUID myID = getLivingEntity().getUniqueId();
		UUID targetID = target.getUniqueId();

		if (myID == targetID && targetingHelper.shouldTarget(targeter))
		{
			if (warningText != null && !warningText.isEmpty())
				sayToNearbyPlayers(warningText, (int)greetRange);

			timeSinceTargeted = 0;
		}
	}

	/**
	 * Called every time a player moves at all, for use with monitoring if players move into range of an NPC.
	 */
	@EventHandler
	public void onPlayerMovesInRange(PlayerMoveEvent event) {
		Location toLoc = event.getTo();

		if (toLoc == null || getLivingEntity() == null || toLoc.getWorld() == null)
			return;

		Location myLoc = getLivingEntity().getLocation();

		if (!toLoc.getWorld().equals(myLoc.getWorld()))
			return;

		double dist = toLoc.distanceSquared(myLoc);
		boolean known = greetedAlready.contains(event.getPlayer().getUniqueId());

		if (dist < greetRange && !known && targetingHelper.canSee(event.getPlayer()))
		{
			greetedAlready.add(event.getPlayer().getUniqueId());
			boolean enemy = targetingHelper.shouldTarget(event.getPlayer());

			if (enemy && warningText != null && warningText.length() > 0)
			{
				sayTo(
					event.getPlayer(),
					warningText
				);
			}
			else if (!enemy && dialogue != null && !dialogue.isEmpty())
			{
				int idx = NumberUtil.randomInt(0, dialogue.size() - 1);
				sayTo(event.getPlayer(), dialogue.get(idx));
			}
			else if (!enemy && greetingText != null && greetingText.length() > 0)
			{
				sayTo(
					event.getPlayer(),
					greetingText
				);
			}
		}
		else if (dist >= greetRange + 1 && known)
		{
			greetedAlready.remove(event.getPlayer().getUniqueId());
			// TODO: Farewell text perhaps?
		}
	}

	/**
	 * Called when an entity might die from damage (called before Guardian detects that an NPC might have killed an entity).
	 */
	public void whenSomethingMightDie(UUID mightDie) {
		if (!needsDropsClear.contains(mightDie))
			return;

		debug("ID " + mightDie + " is no longer being tracked.");

		needsDropsClear.remove(mightDie);
	}

	/**
	 * Outputs a debug message (if debug is enabled).
	 */
	public void debug(String message)
	{
		if (debugMe)
		{
			plugin.getLogger()
				.info("WorldGuardian Debug: " + npc.getId() + "/" + npc.getName() + ": " + message);
		}
	}


	/**
	 * Called when the NPC dies.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void whenWeDie(EntityDeathEvent event)
	{
		debug("Died! Death event received.");

		event.getDrops().clear();
		if (event instanceof PlayerDeathEvent && !plugin.deathMessages)
			((PlayerDeathEvent) event).setDeathMessage("");

		if (!plugin.workaroundDrops)
			event.getDrops().addAll(drops);

		event.setDroppedExp(0);
		generalDeathHandler(event.getEntity());
	}

	/**
	 * Handles some basics for when the NPC died.
	 */
	public void generalDeathHandler(LivingEntity entity)
	{
		if (spawnPoint != null) {
			npc.getTrait(CurrentLocation.class).setLocation(spawnPoint.clone());
		}
		if (plugin.workaroundDrops) {
			for (ItemStack item : drops) {
				entity.getWorld().dropItemNaturally(entity.getLocation(), item.clone());
			}
		}
		onDeath();
	}

	/**
	 * Called when any entity dies.
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void whenSomethingDies(EntityDeathEvent event) {
		UUID id = event.getEntity().getUniqueId();
		if (needsDropsClear.contains(id)) {
			needsDropsClear.remove(id);
			if (event.getEntity().getType() != EntityType.PLAYER) {
				debug("A " + event.getEntity().getType() + " with id " + id + " died. Clearing its drops.");
				event.getDrops().clear();
				event.setDroppedExp(0);
			}
		}
		else {
			debug("A " + event.getEntity().getType() + " with id " + id + " died, but that's none of my business.");
		}
		targetingHelper.removeTarget(id);
	}

	/**
	 * Handler for when the NPC died.
	 */
	public void onDeath() {
        /*if (npc.hasTrait(Spawned.class)) {
            npc.getTrait(Spawned.class).setSpawned(false);
        }*/
		greetedAlready.clear();
		targetingHelper.currentTargets.clear();
		targetingHelper.currentAvoids.clear();
		if (respawnTime < 0)
		{
			BukkitRunnable removeMe = new BukkitRunnable() {
				@Override
				public void run() {
					npc.destroy();
				}
			};
			removeMe.runTaskLater(plugin, 1);
		}
		else if (respawnTime > 0)
		{
			final long rsT = respawnTime;
			respawnMe = new BukkitRunnable()
			{
				long timer = 0;

				@Override
				public void run()
				{
					if (CitizensAPI.getNPCRegistry().getById(npc.getId()) != null)
					{
						if (npc.isSpawned())
						{
							this.cancel();
							respawnMe = null;
							return;
						}

						if (timer >= rsT)
						{
							if (spawnPoint == null && npc.getStoredLocation() == null)
							{
								plugin.getLogger().warning("NPC " + npc.getId() + " has a null spawn point and can't be spawned. Perhaps the world was deleted?");
								this.cancel();
								return;
							}

							npc.spawn(spawnPoint == null ? npc.getStoredLocation() : spawnPoint);
							this.cancel();
							respawnMe = null;
							return;
						}
						timer += 10;
					}
					else
					{
						respawnMe = null;
						this.cancel();
					}
				}
			};
			respawnMe.runTaskTimer(plugin, 10, 10);
		}
		else
			npc.getTrait(Spawned.class).setSpawned(false);
	}

	/**
	 * Called when the NPC despawns.
	 */
	@Override
	public void onDespawn() {
		targetingHelper.currentTargets.clear();
		targetingHelper.currentAvoids.clear();
		plugin.currentGuardianNPCs.remove(this);
	}

	/**
	 * Sets the NPC's maximum health.
	 */
	public void setHealth(double heal) {
		health = heal;
		if (npc.isSpawned()) {
			getLivingEntity().setMaxHealth(health);
			getLivingEntity().setHealth(health);
		}
	}

	/**
	 * Sets whether the NPC is invincible.
	 */
	public void setInvincible(boolean inv) {
		invincible = inv;
		npc.setProtected(invincible);
	}

	/**
	 * Validates this Guardian NPC's presence on the current NPCs list.
	 */
	public boolean validateOnList() {
		if (npc == null || !npc.isSpawned() || getLivingEntity() == null) {
			plugin.currentGuardianNPCs.remove(this);
			return false;
		}
		return true;
	}
}
