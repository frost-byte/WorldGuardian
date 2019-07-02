package net.frost_byte.worldguardian.targeting;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.astar.AStarMachine;
import net.citizensnpcs.api.astar.pathfinder.*;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.frost_byte.worldguardian.GuardianCurrentTarget;
import net.frost_byte.worldguardian.GuardianHelperObject;
import net.frost_byte.worldguardian.GuardianTrait;

import net.frost_byte.worldguardian.events.GuardianNoMoreTargetsEvent;
import net.frost_byte.worldguardian.utility.GuardianTargetType;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import net.frost_byte.worldguardian.utility.GuardianUtilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.debugMe;

@SuppressWarnings( { "WeakerAccess", "unused" })
public class GuardianTargetingHelper extends GuardianHelperObject
{
	/**
	 * Returns whether the NPC can see the target entity.
	 */
	public boolean canSee(LivingEntity entity) {
		if (!getLivingEntity().getWorld().equals(entity.getWorld())) {
			return false;
		}

		if (getLivingEntity().getEyeLocation().distanceSquared(entity.getEyeLocation()) > guardian.range * guardian.range) {
			return false;
		}

		if (!getLivingEntity().hasLineOfSight(entity)) {
			return false;
		}

		if (guardian.realistic) {

			float yaw = getLivingEntity().getEyeLocation().getYaw();

			while (yaw < 0) {
				yaw += 360;
			}

			while (yaw >= 360) {
				yaw -= 360;
			}

			Vector rel = entity.getLocation().toVector().subtract(getLivingEntity().getLocation().toVector()).normalize();
			float yawHelp = GuardianUtilities.getYaw(rel);

			return Math.abs(yawHelp - yaw) < 90 ||
				   Math.abs(yawHelp + 360 - yaw) < 90 ||
				   Math.abs(yaw + 360 - yawHelp) < 90;
		}
		return true;
	}

	/**
	 * Returns whether the NPC is using a potion item.
	 */
	@SuppressWarnings("SimplifiableIfStatement")
	public boolean shouldTarget(LivingEntity entity)
	{
		if (entity.getUniqueId().equals(getLivingEntity().getUniqueId())) {
			return false;
		}
		return isTargeted(entity) && !isIgnored(entity);
	}

	/**
	 * The set of all current targets for this NPC.
	 */
	public HashSet<GuardianCurrentTarget> currentTargets = new HashSet<>();

	/**
	 * The current set of all entities to avoid for this NPC.
	 */
	public HashSet<GuardianCurrentTarget> currentAvoids = new HashSet<>();


	/**
	 * Adds a temporary avoid to this NPC.
	 */
	public void addAvoid(UUID id) {
		if (id.equals(getLivingEntity().getUniqueId())) {
			return;
		}
		if (!(GuardianUtilities.getEntityForID(id) instanceof LivingEntity)) {
			return;
		}
		GuardianCurrentTarget target = new GuardianCurrentTarget();
		target.targetID = id;
		target.ticksLeft = GuardianTargetUtil.getPlugin().runAwayTime;
		currentAvoids.remove(target);
		currentAvoids.add(target);
		if (guardian.squad != null) {
			for (NPC npc : CitizensAPI.getNPCRegistry()) {
				if (npc.hasTrait(GuardianTrait.class)) {
					GuardianTrait squadMade = npc.getTrait(GuardianTrait.class);
					if (squadMade.squad != null && squadMade.squad.equals(guardian.squad)) {
						addTargetNoBounce(id);
					}
				}
			}
		}
	}

	/**
	 * Adds a temporary target to this NPC (and squadmates if relevant).
	 */
	public void addTarget(UUID id)
	{
		if (id.equals(getLivingEntity().getUniqueId())) {
			return;
		}
		if (!(GuardianUtilities.getEntityForID(id) instanceof LivingEntity)) {
			return;
		}
		GuardianCurrentTarget target = new GuardianCurrentTarget();
		target.targetID = id;
		target.ticksLeft = guardian.enemyTargetTime;
		currentTargets.remove(target);
		currentTargets.add(target);
	}

	/**
	 * Removes a temporary target from this NPC (and squadmates if relevant).
	 * Returns whether anything was removed.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public boolean removeTarget(UUID id) {
		GuardianCurrentTarget target = new GuardianCurrentTarget();
		target.targetID = id;
		boolean removed = removeTargetNoBounce(target);
		if (removed && guardian.squad != null) {
			for (NPC npc : CitizensAPI.getNPCRegistry()) {
				if (npc.hasTrait(GuardianTrait.class)) {
					GuardianTrait squadMade = npc.getTrait(GuardianTrait.class);
					if (squadMade.squad != null && squadMade.squad.equals(guardian.squad)) {
						guardian.targetingHelper.removeTargetNoBounce(target);
					}
				}
			}
		}
		return removed;
	}

	/**
	 * Removes a target directly from the NPC. Prefer {@code removeTarget} over this in most cases.
	 * Returns whether anything was removed.
	 */
	public boolean removeTargetNoBounce(GuardianCurrentTarget target) {
		if (currentTargets.isEmpty()) {
			return false;
		}
		if (currentTargets.remove(target)) {
			if (currentTargets.isEmpty()) {
				Bukkit.getPluginManager().callEvent(new GuardianNoMoreTargetsEvent(getNPC()));
			}
			return true;
		}
		return false;
	}

	/**
	 * Adds a target directly to the NPC. Prefer {@code addTarget} over this in most cases.
	 */
	public void addTargetNoBounce(UUID id) {
		GuardianCurrentTarget target = new GuardianCurrentTarget();
		target.targetID = id;
		target.ticksLeft = guardian.enemyTargetTime;
		currentTargets.remove(target);
		currentTargets.add(target);
	}

	/**
	 * Returns whether an entity is invisible to this NPC.
	 */
	public boolean isInvisible(LivingEntity entity) {
		GuardianCurrentTarget sct = new GuardianCurrentTarget();
		sct.targetID = entity.getUniqueId();
		return !currentTargets.contains(sct) && GuardianUtilities.isInvisible(entity);
	}

	/**
	 * Returns whether an entity is ignored by this NPC's ignore lists.
	 */
	@SuppressWarnings("SimplifiableIfStatement")
	public boolean isIgnored(LivingEntity entity) {
		if (isInvisible(entity)) {
			return true;
		}
		if (entity.getUniqueId().equals(getLivingEntity().getUniqueId())) {
			return true;
		}
		if (guardian.getGuarding() != null && entity.getUniqueId().equals(guardian.getGuarding())) {
			return true;
		}
		guardian.allIgnores.checkRecalculateTargetsCache();
		if (guardian.allIgnores.targetsProcessed.contains(
			GuardianTargetUtil.getTarget(GuardianTargetType.OWNER)) &&
			entity.getUniqueId().equals(getNPC().getTrait(Owner.class).getOwnerId()))
		{
			return true;
		}
		return guardian.allIgnores.isTarget(entity);
	}

	private GuardianCurrentTarget tempTarget = new GuardianCurrentTarget();

	/**
	 * Returns whether an entity is targeted by this NPC's ignore lists.
	 */
	@SuppressWarnings("SimplifiableIfStatement")
	public boolean isTargeted(LivingEntity entity) {
		if (isInvisible(entity)) {
			return false;
		}
		if (entity.getUniqueId().equals(getLivingEntity().getUniqueId())) {
			return false;
		}
		if (guardian.getGuarding() != null && entity.getUniqueId().equals(guardian.getGuarding())) {
			return false;
		}
		if (isUntargetable(entity)) {
			return false;
		}
		tempTarget.targetID = entity.getUniqueId();

		if (currentTargets.contains(tempTarget)) {
			return true;
		}

		return guardian.allTargets.isTarget(entity);
	}

	/**
	 * Returns whether an entity is marked to be avoided by this NPC's avoid lists.
	 */
	public boolean isAvoided(LivingEntity entity) {
		if (isInvisible(entity)) {
			return false;
		}
		if (entity.getUniqueId().equals(getLivingEntity().getUniqueId())) {
			return false;
		}
		if (guardian.getGuarding() != null && entity.getUniqueId().equals(guardian.getGuarding())) {
			return false;
		}
		tempTarget.targetID = entity.getUniqueId();
		if (currentAvoids.contains(tempTarget)) {
			return true;
		}
		return guardian.allAvoids.isTarget(entity, guardian);
	}

	private ArrayList<LivingEntity> avoidanceList = new ArrayList<>();

	/**
	 * Process avoid necessary avoidance. Builds a list of things we need to run away from, and then runs.
	 */
	public void processAvoidance() {
		avoidanceList.clear();
		double range = guardian.avoidRange + 10;
		for (Entity entity : getLivingEntity().getWorld().getNearbyEntities(getLivingEntity().getLocation(), range, 16, range)) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			tempTarget.targetID = entity.getUniqueId();
			if (!canSee((LivingEntity) entity) && !targetingHelper.currentAvoids.contains(tempTarget)) {
				continue;
			}
			if (targetingHelper.currentAvoids.contains(tempTarget) || isAvoided((LivingEntity) entity)) {
				avoidanceList.add((LivingEntity) entity);
				targetingHelper.addAvoid(entity.getUniqueId());
			}
		}
		if (avoidanceList.isEmpty()) {
			return;
		}
		Location runTo = findBestRunSpot();
		if (runTo != null) {
			guardian.pathTo(runTo);
			if (debugMe) {
				guardian.debug("Running from threats, movement vector: " +
							   runTo.clone().subtract(getLivingEntity().getLocation()).toVector().toBlockVector().toString());
			}
		}
		else {
			if (debugMe) {
				guardian.debug("I have nowhere to run!");
			}
		}
	}

	/**
	 * Finds a spot this NPC should run to, to avoid threats. Returns null if there's nowhere to run.
	 */
	public Location findBestRunSpot() {
		if (guardian.avoidReturnPoint != null
			&& guardian.avoidReturnPoint.getWorld().equals(getLivingEntity().getWorld())) {
			return guardian.avoidReturnPoint;
		}
		Location pos = guardian.getGuardZone();
		if (!pos.getWorld().equals(getLivingEntity().getWorld())) {
			// Emergency corrective measures...
			getNPC().getNavigator().cancelNavigation();
			getLivingEntity().teleport(guardian.getGuardZone());
			return null;
		}
		LivingEntity closestThreat = null;
		double threatRangeSquared = 1000 * 1000;
		for (LivingEntity entity : avoidanceList) {
			double dist = entity.getLocation().distanceSquared(pos);
			if (dist < threatRangeSquared) {
				closestThreat = entity;
				threatRangeSquared = dist;
			}
		}
		if (closestThreat == null) {
			return null;
		}
		if (threatRangeSquared >= guardian.avoidRange * guardian.avoidRange) {
			if (debugMe) {
				guardian.debug("Threats are getting close... holding my post.");
			}
			return pos;
		}
		return runDirection(pos);
	}

	private double[] threatDists = new double[36];

	private static Vector[] directionReferenceVectors = new Vector[36];

	static {
		for (int i = 0; i < 36; i++) {
			double yaw = i * 10;
			// negative yaw in x because Minecraft worlds are inverted
			directionReferenceVectors[i] = new Vector(Math.sin(-yaw * (Math.PI / 180)), 0, Math.cos(yaw * (Math.PI / 180)));
		}
	}

	private static AStarMachine ASTAR = AStarMachine.createWithDefaultStorage();

	private static BlockExaminer examiner = new MinecraftBlockExaminer();

	/**
	 * Returns a spot to run to if running in a certain direction.
	 * Returns null if can't reasonable run that direction.
	 */
	public static Location findSpotForRunDirection(Location start, double distance, Vector direction) {
		VectorGoal goal = new VectorGoal(start.clone().add(direction.clone().multiply(distance)), 4);
		VectorNode startNode = new VectorNode(goal, start, new ChunkBlockSource(start, (float)distance + 10), examiner);
		Path resultPath = (Path) ASTAR.runFully(goal, startNode, (int)(distance * 50));
		if (resultPath == null || resultPath.isComplete()) {
			return null;
		}
		Vector current = resultPath.getCurrentVector();
		while (!resultPath.isComplete()) {
			current = resultPath.getCurrentVector();
			resultPath.update(null);
		}
		return current.toLocation(start.getWorld());
	}

	/**
	 * Returns a direction to run in, avoiding threatening entities as best as possible.
	 * Returns a location of the spot to run to.
	 * Returns null if nowhere to run.
	 */
	public Location runDirection(Location center) {
		for (int i = 0; i < 36; i++) {
			threatDists[i] = 1000 * 1000;
		}
		double range = guardian.avoidRange;
		Vector centerVec = center.toVector();
		for (LivingEntity entity : avoidanceList) {
			Vector relative = entity.getLocation().toVector().subtract(centerVec);
			for (int i = 0; i < 36; i++) {
				double dist = relative.distanceSquared(directionReferenceVectors[i].clone().multiply(range));
				if (dist < threatDists[i]) {
					threatDists[i] = dist;
				}
			}
		}
		double longestDistance = 0;
		Location runTo = null;
		for (int i = 0; i < 36; i++) {
			if (threatDists[i] > longestDistance) {
				Location newRunTo = findSpotForRunDirection(center, range, directionReferenceVectors[i].clone());
				if (newRunTo != null) {
					runTo = newRunTo;
					longestDistance = threatDists[i];
				}
			}
		}
		if (debugMe) {
			debug("(TEMP) Run to get threat distance: " + longestDistance + " to " + runTo + " from " + center.toVector());
		}
		return runTo;
	}

	/**
	 * Process all current multi-targets.
	 */
	public void processAllMultiTargets() {
		processMultiTargets(guardian.allTargets, TargetListType.TARGETS);
		processMultiTargets(guardian.allAvoids, TargetListType.AVOIDS);
	}

	/**
	 * The types of target lists.
	 */
	public enum TargetListType {
		TARGETS, IGNORES, AVOIDS
	}

	/**
	 * Process a specific set of multi-targets.
	 */
	public void processMultiTargets(GuardianTargetList baseList, TargetListType type) {
		if (type == null) {
			return;
		}
		if (baseList.byMultiple.isEmpty()) {
			return;
		}
		ArrayList<GuardianTargetList> subList = new ArrayList<>(baseList.byMultiple.size());
		for (GuardianTargetList list : baseList.byMultiple) {
			GuardianTargetList toAdd = list.duplicate();
			toAdd.recalculateCacheNoClear();
			subList.add(toAdd);
			if (debugMe) {
				debug("Multi-Target Debug: " + toAdd.totalTargetsCount() + " at start: " + toAdd.toMultiTargetString());
			}
		}
		Location pos = guardian.getGuardZone();
		for (Entity loopEnt : getLivingEntity().getWorld().getNearbyEntities(pos, guardian.range, guardian.range, guardian.range)) {
			if (!(loopEnt instanceof LivingEntity)) {
				continue;
			}
			LivingEntity ent = (LivingEntity) loopEnt;
			if (ent.isDead()) {
				continue;
			}
			if (isIgnored(ent)) {
				continue;
			}
			if (!canSee(ent)) {
				continue;
			}
			for (GuardianTargetList lister : subList) {
				if (lister.ifIsTargetDeleteTarget(ent)) {
					if (debugMe) {
						debug("Multi-Target Debug: " + ent.getName() + " (" + ent.getType().name() + ") checked off for a list.");
					}
					lister.tempTargeted.add(ent);
					if (lister.totalTargetsCount() == 0) {
						if (debugMe) {
							debug("Multi-Target Debug: " + lister.totalTargetsCount() + " completed: " + lister.toMultiTargetString());
						}
						for (LivingEntity subEnt : lister.tempTargeted) {
							if (type == TargetListType.TARGETS) {
								addTarget(subEnt.getUniqueId());
							}
							else if (type == TargetListType.AVOIDS) {
								addAvoid(subEnt.getUniqueId());
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Finds a nearby target that can be hit with a melee attack.
	 */
	public LivingEntity findQuickMeleeTarget() {
		double range = guardian.reach * 0.75;
		Location pos = getLivingEntity().getEyeLocation();
		for (Entity loopEnt : getLivingEntity().getWorld().getNearbyEntities(pos, range, range, range)) {
			if (loopEnt instanceof LivingEntity && shouldTarget((LivingEntity) loopEnt)
				&& canSee((LivingEntity) loopEnt)) {
				return (LivingEntity) loopEnt;
			}
		}
		return null;
	}

	/**
	 * Updates the current avoids set for the NPC.
	 */
	public void updateAvoids() {
		for (GuardianCurrentTarget curTarg : new HashSet<>(currentAvoids)) {
			Entity e = GuardianUtilities.getEntityForID(curTarg.targetID);
			if (e == null) {
				currentAvoids.remove(curTarg);
				continue;
			}
			if (e.isDead()) {
				currentAvoids.remove(curTarg);
				continue;
			}
			if (curTarg.ticksLeft > 0) {
				curTarg.ticksLeft -= GuardianTargetUtil.getPlugin().tickRate;
				if (curTarg.ticksLeft <= 0) {
					currentAvoids.remove(curTarg);
				}
			}
		}
	}

	/**
	 * Returns whether an entity is not able to be targeted at all.
	 */
	public static boolean isUntargetable(Entity e) {
		return e == null ||
			   (e instanceof Player && (((Player) e).getGameMode() == GameMode.CREATIVE || ((Player) e).getGameMode() == GameMode.SPECTATOR)) ||
			   e.isDead();
	}

	/**
	 * This method searches for the nearest targetable entity with direct line-of-sight.
	 * Failing a direct line of sight, the nearest entity in range at all will be chosen.
	 */
	public LivingEntity findBestTarget() {
		boolean ignoreGlow = itemHelper.usesSpectral();
		double rangesquared = guardian.range * guardian.range;
		double crsq = guardian.chaseRange * guardian.chaseRange;
		Location pos = guardian.getGuardZone();
		if (!guardian.getGuardZone().getWorld().equals(getLivingEntity().getWorld())) {
			// Emergency corrective measures...
			getNPC().getNavigator().cancelNavigation();
			getLivingEntity().teleport(guardian.getGuardZone());
			return null;
		}
		if (!pos.getWorld().equals(getLivingEntity().getWorld())) {
			return null;
		}
		LivingEntity closest = null;
		boolean wasLos = false;
		for (LivingEntity ent : getLivingEntity().getWorld().getLivingEntities()) {
			if ((ignoreGlow && ent.isGlowing()) || ent.isDead() || ent.getWorld() != pos.getWorld()) {
				continue;
			}
			double dist = ent.getEyeLocation().distanceSquared(pos);
			GuardianCurrentTarget sct = new GuardianCurrentTarget();
			sct.targetID = ent.getUniqueId();
			if ((dist < rangesquared && shouldTarget(ent) && canSee(ent)) || (dist < crsq && currentTargets.contains(sct))) {
				boolean hasLos = canSee(ent);
				if (!wasLos || hasLos) {
					rangesquared = dist;
					closest = ent;
					wasLos = hasLos;
				}
			}
		}
		return closest;
	}

	/**
	 * Updates the current targets set for the NPC.
	 */
	public void updateTargets() {
		for (GuardianCurrentTarget uuid : new HashSet<>(currentTargets)) {
			Entity e = GuardianUtilities.getEntityForID(uuid.targetID);
			if (e == null) {
				currentTargets.remove(uuid);
				continue;
			}
			if (e instanceof Player && (((Player) e).getGameMode() == GameMode.CREATIVE || ((Player) e).getGameMode() == GameMode.SPECTATOR)) {
				currentTargets.remove(uuid);
				continue;
			}
			if (e.isDead()) {
				currentTargets.remove(uuid);
				continue;
			}
			double d = e.getWorld().equals(getLivingEntity().getWorld()) ?
						   e.getLocation().distanceSquared(getLivingEntity().getLocation())
						   : 10000.0 * 10000.0;
			if (d > guardian.range * guardian.range * 4 && d > guardian.chaseRange * guardian.chaseRange * 4) {
				currentTargets.remove(uuid);
				continue;
			}
			if (uuid.ticksLeft > 0) {
				uuid.ticksLeft -= GuardianTargetUtil.getPlugin().tickRate;
				if (uuid.ticksLeft <= 0) {
					currentTargets.remove(uuid);
				}
			}
		}
		if (guardian.chasing != null) {
			GuardianCurrentTarget cte = new GuardianCurrentTarget();
			cte.targetID = guardian.chasing.getUniqueId();
			if (!currentTargets.contains(cte)) {
				guardian.chasing = null;
				getNPC().getNavigator().cancelNavigation();
			}
		}
	}
}
