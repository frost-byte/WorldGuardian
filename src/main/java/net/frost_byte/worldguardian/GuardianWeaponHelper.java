package net.frost_byte.worldguardian;

import net.citizensnpcs.api.trait.trait.Inventory;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.HashMap;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.debugMe;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.v1_13;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.v1_14;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.v1_9;

@SuppressWarnings( { "unused", "WeakerAccess" })
public class GuardianWeaponHelper extends GuardianHelperObject
{
	public static final EntityType LINGERING_POTION, TIPPED_ARROW;

	static {
		if (v1_14) {
			LINGERING_POTION = EntityType.SPLASH_POTION;
			TIPPED_ARROW = EntityType.ARROW;
		}
		else if (v1_9) {
			LINGERING_POTION = EntityType.valueOf("LINGERING_POTION");
			TIPPED_ARROW = EntityType.valueOf("TIPPED_ARROW");
		}
		else {
			LINGERING_POTION = null;
			TIPPED_ARROW = null;

		}
	}
	/**
	 * Fires a potion from the NPC at a target.
	 */
	public void firePotion(ItemStack potion, Location target, Vector lead) {
		guardian.stats_potionsThrown++;
		HashMap.SimpleEntry<Location, Vector> start = guardian.getLaunchDetail(target, lead);
		Entity entpotion;
		if (v1_14) {
			entpotion = start.getKey().getWorld().spawnEntity(
				start.getKey(),
				EntityType.SPLASH_POTION
			);
		}
		else if (v1_9) {
			entpotion = start.getKey().getWorld().spawnEntity(
				start.getKey(),
				potion.getType() == Material.SPLASH_POTION ? EntityType.SPLASH_POTION : LINGERING_POTION);
		}
		else {
			entpotion = start.getKey().getWorld().spawnEntity(
				start.getKey(),
				EntityType.SPLASH_POTION
			);
		}
		((ThrownPotion) entpotion).setShooter(getLivingEntity());
		((ThrownPotion) entpotion).setItem(potion);
		entpotion.setVelocity(guardian.fixForAcc(start.getValue()));
		guardian.swingWeapon();
	}

	/**
	 * Fires an arrow from the NPC at a target.
	 */
	public void fireArrow(ItemStack type, Location target, Vector lead)
	{
		HashMap.SimpleEntry<Location, Vector> start = guardian.getLaunchDetail(target, lead);

		if (start == null || start.getKey() == null) {
			return;
		}
		guardian.stats_arrowsFired++;
		Entity arrow;

		if (v1_9) {

			if (v1_14) {
				Class toShoot;
				toShoot = type.getType() == Material.SPECTRAL_ARROW ? SpectralArrow.class :
							  (type.getType() == Material.TIPPED_ARROW ? TippedArrow.class : Arrow.class);
				Vector dir = guardian.fixForAcc(start.getValue());
				double length = Math.max(1.0, dir.length());
				arrow = start.getKey().getWorld().spawnArrow(start.getKey(), dir.multiply(1.0 / length), (float) length, 0f, toShoot);
				((Arrow) arrow).setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			}
			else {
				arrow = start.getKey().getWorld().spawnEntity(
					start.getKey(),
					type.getType() == Material.SPECTRAL_ARROW ? EntityType.SPECTRAL_ARROW :
						(type.getType() == Material.TIPPED_ARROW ? TIPPED_ARROW : EntityType.ARROW)
				);
				arrow.setVelocity(guardian.fixForAcc(start.getValue()));
			}
			((Projectile) arrow).setShooter(getLivingEntity());
			if (arrow instanceof TippedArrow && type instanceof PotionMeta) {
				PotionData data = ((PotionMeta) type.getItemMeta()).getBasePotionData();
				if (data.getType() == null || data.getType() == PotionType.UNCRAFTABLE) {
					// TODO: Perhaps a **single** warning?
				}
				else {
					((TippedArrow) arrow).setBasePotionData(data);
					for (PotionEffect effect : ((PotionMeta) type.getItemMeta()).getCustomEffects()) {
						((TippedArrow) arrow).addCustomEffect(effect, true);
					}
				}
			}
		}
		else {
			arrow = start.getKey().getWorld().spawnEntity(start.getKey(), EntityType.ARROW);
			((Projectile) arrow).setShooter(getLivingEntity());
			arrow.setVelocity(guardian.fixForAcc(start.getValue()));
		}

		if (getNPC().getTrait(Inventory.class).getContents()[0].containsEnchantment(Enchantment.ARROW_FIRE))
		{
			arrow.setFireTicks(10000);
		}
		guardian.useItem();
	}

	/**
	 * Fires a snowball from the NPC at a target.
	 */
	public void fireSnowball(Location target) {
		guardian.swingWeapon();
		guardian.stats_snowballsThrown++;
		guardian.faceLocation(target);
		Vector forward = getLivingEntity().getEyeLocation().getDirection();
		Location spawnAt = getLivingEntity().getEyeLocation().clone().add(forward.clone().multiply(guardian.firingMinimumRange()));
		Entity ent = spawnAt.getWorld().spawnEntity(spawnAt, EntityType.SNOWBALL);
		((Projectile) ent).setShooter(getLivingEntity());
		ent.setVelocity(
			guardian.fixForAcc(
				target.clone().subtract(spawnAt).toVector().normalize().multiply(2.0)
			)
		); // TODO: Fiddle with '2.0'.
	}

	/**
	 * Fires a trident from the NPC at a target.
	 */
	public void fireTrident(Location target) {
		if (!v1_13) {
			return;
		}
		guardian.swingWeapon();
		guardian.stats_arrowsFired++;
		guardian.faceLocation(target);
		Vector forward = getLivingEntity().getEyeLocation().getDirection();
		Location spawnAt = getLivingEntity().getEyeLocation().clone().add(forward.clone().multiply(guardian.firingMinimumRange() + 2));
		Trident ent = (Trident) spawnAt.getWorld().spawnEntity(spawnAt, EntityType.TRIDENT);
		if (v1_14) {
			ent.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		}
		ent.setShooter(getLivingEntity());
		ent.setVelocity(guardian.fixForAcc(target.clone().subtract(spawnAt).toVector().normalize().multiply(2.0))); // TODO: Fiddle with '2.0'.
	}

	/**
	 * Fires an egg from the NPC at a target.
	 */
	public void fireEgg(Location target) {
		guardian.swingWeapon();
		guardian.stats_eggsThrown++;
		guardian.faceLocation(target);
		Vector forward = getLivingEntity().getEyeLocation().getDirection();
		Location spawnAt = getLivingEntity().getEyeLocation().clone().add(forward.clone().multiply(guardian.firingMinimumRange()));
		Entity ent = spawnAt.getWorld().spawnEntity(spawnAt, EntityType.EGG);
		((Projectile) ent).setShooter(getLivingEntity());
		ent.setVelocity(guardian.fixForAcc(target.clone().subtract(spawnAt).toVector().normalize().multiply(2.0))); // TODO: Fiddle with '2.0'.
	}

	/**
	 * Fires a pearl from the NPC at a target.
	 */
	public void firePearl(LivingEntity target) {
		guardian.swingWeapon();
		guardian.faceLocation(target.getEyeLocation());
		// TODO: Maybe require entity is-on-ground?
		guardian.stats_pearlsUsed++;
		target.setVelocity(target.getVelocity().add(new Vector(0, guardian.getDamage(), 0)));
	}

	/**
	 * Fires a fireballs from the NPC at a target.
	 */
	public void fireFireball(Location target) {
		guardian.swingWeapon();
		guardian.stats_fireballsFired++;
		guardian.faceLocation(target);
		Vector forward = getLivingEntity().getEyeLocation().getDirection();
		Location spawnAt = getLivingEntity().getEyeLocation().clone().add(forward.clone().multiply(guardian.firingMinimumRange()));
		Entity ent = spawnAt.getWorld().spawnEntity(spawnAt, EntityType.SMALL_FIREBALL);
		((Projectile) ent).setShooter(getLivingEntity());
		ent.setVelocity(guardian.fixForAcc(target.clone().subtract(spawnAt).toVector().normalize().multiply(4))); // TODO: Fiddle with '4'.
	}

	/**
	 * Fires a skull from the NPC at a target.
	 */
	public void fireSkull(Location target) {
		guardian.swingWeapon();
		guardian.stats_skullsThrown++;
		guardian.faceLocation(target);
		Vector forward = getLivingEntity().getEyeLocation().getDirection();
		Location spawnAt = getLivingEntity().getEyeLocation().clone().add(forward.clone().multiply(guardian.firingMinimumRange()));
		Entity ent = spawnAt.getWorld().spawnEntity(spawnAt, EntityType.WITHER_SKULL);
		((Projectile) ent).setShooter(getLivingEntity());
		ent.setVelocity(guardian.fixForAcc(target.clone().subtract(spawnAt).toVector().normalize().multiply(4))); // TODO: Fiddle with '4'.
	}

	/**
	 * Makes an NPC punch a target.
	 */
	public void punch(LivingEntity entity) {
		guardian.faceLocation(entity.getLocation());
		guardian.swingWeapon();
		guardian.stats_punches++;
		WorldGuardianPlugin plugin = GuardianTargetUtil.getPlugin();
		if (plugin.workaroundDamage) {
			if (debugMe) {
				debug("workaround damage value at " + guardian.getDamage() + " yields "
					  + ((guardian.getDamage() * (1.0 - guardian.getArmor(entity)))));
			}
			entity.damage(guardian.getDamage() * (1.0 - guardian.getArmor(entity)));
			knockback(entity);
			if (!guardian.enemyDrops) {
				guardian.needsDropsClear.add(entity.getUniqueId());
			}
		}
		else {
			if (debugMe) {
				debug("Punch/natural for " + guardian.getDamage());
			}
			entity.damage(guardian.getDamage(), getLivingEntity());
		}
	}

	/**
	 * Knocks a target back from damage received (for hacked-in damage applications when required by config).
	 */
	public void knockback(LivingEntity entity) {
		Vector relative = entity.getLocation().toVector().subtract(getLivingEntity().getLocation().toVector());
		relative = relative.normalize();
		relative.setY(0.75);
		relative.multiply(0.5 / Math.max(1.0, entity.getVelocity().length()));
		entity.setVelocity(entity.getVelocity().multiply(0.25).add(relative));

		if (debugMe) {
			debug("applied knockback velocity adder of " + relative);
		}
	}
}
