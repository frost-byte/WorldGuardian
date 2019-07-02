package net.frost_byte.worldguardian;

import net.citizensnpcs.api.ai.TargetType;

import net.frost_byte.worldguardian.events.GuardianAttackEvent;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.debugMe;

@SuppressWarnings( { "unused", "WeakerAccess" })
public class GuardianAttackHelper extends GuardianHelperObject
{
	/**
	 * Causes the NPC to chase a target.
	 */
	public void chase(LivingEntity entity) {
		if (getNPC().getNavigator().getTargetType() == TargetType.LOCATION
			&& getNPC().getNavigator().getTargetAsLocation() != null
			&& ((getNPC().getNavigator().getTargetAsLocation().getWorld().equals(entity.getWorld())
				 && getNPC().getNavigator().getTargetAsLocation().distanceSquared(entity.getLocation()) < 2 * 2))) {
			return;
		}
		guardian.cleverTicks = 0;
		guardian.chasing = entity;
		guardian.chased = true;

		if (getNPC().getNavigator().getTargetType() == TargetType.ENTITY
			&& getNPC().getNavigator().getEntityTarget().getTarget().getUniqueId().equals(entity.getUniqueId())) {
			return;
		}
		getNPC().getNavigator().getDefaultParameters().stuckAction(null);
        /*
        Location goal = entity.getLocation().clone().add(entity.getVelocity().clone());
        npc.getNavigator().setTarget(goal);
        bunny_goal = goal;
        */
		if (GuardianTargetUtil.getPlugin().workaroundEntityChasePathfinder)
		{
			getNPC().getNavigator().setTarget(entity.getLocation());
		}
		else
		{
			getNPC().getNavigator().setTarget(entity, false);
		}

		getNPC().getNavigator().getLocalParameters().speedModifier((float) guardian.speed);
	}

	/**
	 * Repeats the last chase instruction (to ensure the NPC keeps going for a target).
	 */
	public void rechase() {
		if (guardian.chasing != null) {
			chase(guardian.chasing);
		}
	}

	/**
	 * Causes the NPC to attempt an attack on a target.
	 */
	public boolean tryAttack(LivingEntity target)
	{
		if (tryAttackInternal(target)) {
			return true;
		}
		LivingEntity quickTarget = targetingHelper.findQuickMeleeTarget();
		if (quickTarget != null) {
			if (itemHelper.isRanged()) {
				if (!guardian.autoswitch) {
					return false;
				}
				itemHelper.swapToMelee();
				if (itemHelper.isRanged()) {
					return false;
				}
			}
			if (tryAttackInternal(quickTarget)) {
				chase(target);
				return true;
			}
			chase(target);
		}
		return false;
	}

	/**
	 * Internal attack attempt logic.
	 */
	public boolean tryAttackInternal(LivingEntity entity) {

		if (!entity.getWorld().equals(getLivingEntity().getWorld())) {
			return false;
		}
		if (!getLivingEntity().hasLineOfSight(entity)) {
			return false;
		}
		// TODO: Simplify this code!
		guardian.stats_attackAttempts++;
		double dist = getLivingEntity().getEyeLocation().distanceSquared(entity.getEyeLocation());
		if (debugMe) {
			debug("tryAttack at range " + dist);
		}
		if (guardian.autoswitch && dist > guardian.reach * guardian.reach) {
			itemHelper.swapToRanged();
		}
		else if (guardian.autoswitch && dist < guardian.reach * guardian.reach) {
			itemHelper.swapToMelee();
		}
		guardian.chasing = entity;
		GuardianAttackEvent sat = new GuardianAttackEvent(getNPC());
		Bukkit.getPluginManager().callEvent(sat);
		if (sat.isCancelled()) {
			if (debugMe) {
				debug("tryAttack refused, event cancellation");
			}
			return false;
		}
		targetingHelper.addTarget(entity.getUniqueId());
		for (GuardianIntegration si : WorldGuardianPlugin.integrations) {
			if (si.tryAttack(guardian, entity)) {
				return true;
			}
		}
		if (itemHelper.usesBow()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				ItemStack item = itemHelper.getArrow();
				if (item != null) {
					weaponHelper.fireArrow(item, entity.getEyeLocation(), entity.getVelocity());
					if (guardian.needsAmmo) {
						itemHelper.reduceDurability();
						itemHelper.takeArrow();
						itemHelper.grabNextItem();
					}
				}
			}
			else if (guardian.rangedChase) {
				chase(entity);
			}
		}
		else if (itemHelper.usesSnowball()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				ItemStack item = itemHelper.getArrow();
				if (item != null) {
					weaponHelper.fireSnowball(entity.getEyeLocation());
					if (guardian.needsAmmo) {
						itemHelper.takeSnowball();
						itemHelper.grabNextItem();
					}
				}
			}
			else if (guardian.rangedChase) {
				chase(entity);
			}
		}
		else if (itemHelper.usesTrident()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				weaponHelper.fireTrident(entity.getEyeLocation());
				if (guardian.needsAmmo) {
					itemHelper.takeOne();
					itemHelper.grabNextItem();
				}
				return true;
			}
			else if (guardian.rangedChase) {
				chase(entity);
				return false;
			}
		}
		else if (itemHelper.usesPotion()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				weaponHelper.firePotion(
					getLivingEntity().getEquipment().getItemInMainHand(),
					entity.getEyeLocation(), entity.getVelocity()
				);

				if (guardian.needsAmmo) {
					itemHelper.takeOne();
					itemHelper.grabNextItem();
				}
			}
			else if (guardian.rangedChase) {
				chase(entity);
			}
		}
		else if (itemHelper.usesEgg()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				weaponHelper.fireEgg(entity.getEyeLocation());
				if (guardian.needsAmmo) {
					itemHelper.takeOne();
					itemHelper.grabNextItem();
				}
				return true;
			}
			else if (guardian.rangedChase) {
				chase(entity);
				return false;
			}
		}
		else if (itemHelper.usesPearl()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				weaponHelper.firePearl(entity);
				if (guardian.needsAmmo) {
					itemHelper.takeOne();
					itemHelper.grabNextItem();
				}
				return true;
			}
			else if (guardian.rangedChase) {
				chase(entity);
				return false;
			}
		}
		else if (itemHelper.usesWitherSkull()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				weaponHelper.fireSkull(entity.getEyeLocation());
				if (guardian.needsAmmo) {
					itemHelper.takeOne();
					itemHelper.grabNextItem();
				}
				return true;
			}
			else if (guardian.rangedChase) {
				chase(entity);
				return false;
			}
		}
		else if (itemHelper.usesFireball()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				weaponHelper.fireFireball(entity.getEyeLocation());
				if (guardian.needsAmmo) {
					itemHelper.takeOne();
					itemHelper.grabNextItem();
				}
				return true;
			}
			else if (guardian.rangedChase) {
				chase(entity);
				return false;
			}
		}
		else if (itemHelper.usesLightning()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				guardian.swingWeapon();
				entity.getWorld().strikeLightningEffect(entity.getLocation());
				if (debugMe) {
					debug("Lightning hits for " + guardian.getDamage());
				}
				entity.damage(guardian.getDamage());
				if (guardian.needsAmmo) {
					itemHelper.takeOne();
					itemHelper.grabNextItem();
				}
				return true;
			}
			else if (guardian.rangedChase) {
				chase(entity);
			}
		}
		else if (itemHelper.usesSpectral()) {
			if (targetingHelper.canSee(entity)) {
				if (guardian.timeSinceAttack < guardian.attackRateRanged) {
					if (guardian.rangedChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				if (!entity.isGlowing()) {
					guardian.swingWeapon();
					try {
						Sound snd = GuardianTargetUtil.getPlugin().spectralSound;
						if (snd != null) {
							entity.getWorld().playSound(entity.getLocation(), snd, 1f, 1f);
						}
					}
					catch (Exception e) {
						// Do nothing!
					}
					entity.setGlowing(true);
					if (guardian.needsAmmo) {
						itemHelper.takeOne();
						itemHelper.grabNextItem();
					}
					return true;
				}
			}
			else if (guardian.rangedChase) {
				chase(entity);
				return false;
			}
		}
		else {
			if (dist < guardian.reach * guardian.reach) {
				if (guardian.timeSinceAttack < guardian.attackRate) {
					if (debugMe) {
						debug("tryAttack refused, timeSinceAttack");
					}
					if (guardian.closeChase) {
						rechase();
					}
					return false;
				}
				guardian.timeSinceAttack = 0;
				// TODO: Damage sword if needed!
				if (debugMe) {
					debug("tryAttack passed!");
				}
				weaponHelper.punch(entity);
				if (guardian.needsAmmo && itemHelper.shouldTakeDura()) {
					itemHelper.reduceDurability();
					itemHelper.grabNextItem();
				}
				return true;
			}
			else if (guardian.closeChase) {
				if (debugMe) {
					debug("tryAttack refused, range");
				}
				chase(entity);
				return false;
			}
		}
		return false;
	}
}
