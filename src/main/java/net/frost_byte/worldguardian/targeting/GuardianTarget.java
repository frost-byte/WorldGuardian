package net.frost_byte.worldguardian.targeting;

import net.frost_byte.worldguardian.Target;
import net.frost_byte.worldguardian.utility.GuardianTargetType;
import org.bukkit.entity.EntityType;

import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
public class GuardianTarget implements Target
{
	private GuardianTargetType targetType;
	private String[] names;
	private EntityType[] entityTypes;

	@Override
	public String getName() { return names[0]; }

	@Override
	public EntityType[] getEntityTypes() { return entityTypes; }

	@Override
	public GuardianTargetType getTargetType() { return targetType; }

	public GuardianTarget(GuardianTargetType targetType){
		this.targetType = targetType;
		this.entityTypes = targetType.getEntityTypes();
		this.names = targetType.getNames();

		for (String name : names) {
			addTargetOption(name, this);
			addTargetOption(name + "S", this);
		}

		for (EntityType type : entityTypes) {
			addTypeMapping(type, this);
		}
	}
}
