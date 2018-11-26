package net.frost_byte.worldguardian.targeting;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.frost_byte.worldguardian.GuardianCurrentTarget;
import net.frost_byte.worldguardian.GuardianHelperObject;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.utility.GuardianTargetType;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import net.frost_byte.worldguardian.utility.GuardianUtilities;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.UUID;

@SuppressWarnings( { "WeakerAccess", "unused" })
public class GuardianTargetingHelper extends GuardianHelperObject
{
	/**
	 * Returns whether the NPC can see the target entity.
	 */
	public boolean canSee(LivingEntity entity) {
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
		addTargetNoBounce(id);
		if (guardian.squad != null)
		{
			for (NPC npc : CitizensAPI.getNPCRegistry())
			{
				if (npc.hasTrait(GuardianTrait.class))
				{
					GuardianTrait squadMade = npc.getTrait(GuardianTrait.class);
					if (squadMade.squad != null && squadMade.squad.equals(guardian.squad))
					{
						addTargetNoBounce(id);
					}
				}
			}
		}
	}

	/**
	 * Removes a temporary target from this NPC (and squadmates if relevant).
	 * Returns whether anything was removed.
	 */
	public boolean removeTarget(UUID id) {
		GuardianCurrentTarget target = new GuardianCurrentTarget();
		target.targetID = id;
		boolean removed = currentTargets.remove(target);
		if (removed && guardian.squad != null) {
			for (NPC npc : CitizensAPI.getNPCRegistry()) {
				if (npc.hasTrait(GuardianTrait.class)) {
					GuardianTrait squadMade = npc.getTrait(GuardianTrait.class);
					if (squadMade.squad != null && squadMade.squad.equals(guardian.squad)) {
						guardian.targetingHelper.currentTargets.remove(target);
					}
				}
			}
		}
		return removed;
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
	@SuppressWarnings("SimplifiableIfStatement") public boolean isIgnored(LivingEntity entity) {
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

	/**
	 * Returns whether an entity is targeted by this NPC's ignore lists.
	 */
	@SuppressWarnings("SimplifiableIfStatement") public boolean isTargeted(LivingEntity entity) {
		if (isInvisible(entity)) {
			return false;
		}
		if (entity.getUniqueId().equals(getLivingEntity().getUniqueId())) {
			return false;
		}
		if (guardian.getGuarding() != null && entity.getUniqueId().equals(guardian.getGuarding())) {
			return false;
		}
		GuardianCurrentTarget target = new GuardianCurrentTarget();
		target.targetID = entity.getUniqueId();
		if (currentTargets.contains(target)) {
			return true;
		}
		guardian.allTargets.checkRecalculateTargetsCache();
		if (
			guardian.allTargets.targetsProcessed.contains(GuardianTargetUtil.getTarget(GuardianTargetType.OWNER)) &&
			entity.getUniqueId().equals(getNPC().getTrait(Owner.class).getOwnerId())
		){
			return true;
		}
		return guardian.allTargets.isTarget(entity);
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
			if ((ignoreGlow && ent.isGlowing()) || ent.isDead()) {
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
