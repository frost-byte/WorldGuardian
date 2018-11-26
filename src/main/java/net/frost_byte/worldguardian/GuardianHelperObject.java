package net.frost_byte.worldguardian;

import net.citizensnpcs.api.npc.NPC;
import net.frost_byte.worldguardian.targeting.GuardianTargetingHelper;
import org.bukkit.entity.LivingEntity;

@SuppressWarnings("WeakerAccess")
public abstract class GuardianHelperObject
{
	/**
	 * The relevant GuardianTrait instance.
	 */
	public GuardianTrait guardian;

	protected GuardianItemHelper itemHelper;

	protected GuardianWeaponHelper weaponHelper;

	protected GuardianTargetingHelper targetingHelper;

	protected GuardianAttackHelper attackHelper;

	/**
	 * Sets the Guardian trait object (and fills other helper object values).
	 */
	public void setTraitObject(GuardianTrait trait) {
		guardian = trait;
		itemHelper = trait.itemHelper;
		weaponHelper = trait.weaponHelper;
		targetingHelper = guardian.targetingHelper;
		attackHelper = guardian.attackHelper;
	}

	/**
	 * Gets the relevant NPC.
	 */
	public NPC getNPC() {
		return guardian.getNPC();
	}

	/**
	 * Gets the NPC's living entity.
	 */
	public LivingEntity getLivingEntity() {
		return guardian.getLivingEntity();
	}

	/**
	 * Outputs a debug message.
	 */
	public void debug(String message) {
		guardian.debug(message);
	}
}
