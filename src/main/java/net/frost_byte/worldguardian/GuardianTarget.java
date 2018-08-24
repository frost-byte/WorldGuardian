package net.frost_byte.worldguardian;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import net.frost_byte.worldguardian.utility.GuardianTargetType;
import net.frost_byte.worldguardian.utility.GuardianTargetUtil;
import org.bukkit.entity.EntityType;

@SuppressWarnings("unused")
public class GuardianTarget
{
	private GuardianTargetType targetType;
	private String[] names;
	private EntityType[] entityTypes;

	public String name() { return names[0]; }

	public EntityType[] getEntityTypes() { return entityTypes; }

	public GuardianTargetType getTargetType() { return targetType; }

	@Inject
	GuardianTarget(
		GuardianTargetType targetType,
		@Assisted WorldGuardianPlugin plugin,
		@Assisted GuardianTargetUtil targetUtil
	){
		this.targetType = targetType;
		this.entityTypes = targetType.getEntityTypes();
		this.names = targetType.getNames();

		for (String name : names) {
			targetUtil.addTargetOption(name, this);
			targetUtil.addTargetOption(name + "S", this);
		}

		for (EntityType type : entityTypes) {
			targetUtil.addTypeMapping(type, this);
		}
	}
}
