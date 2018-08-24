package net.frost_byte.worldguardian;

import java.util.UUID;

public class GuardianCurrentTarget
{

	public UUID targetID;

	public long ticksLeft;

	@Override
	public int hashCode() {
		return targetID.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof GuardianCurrentTarget && ((GuardianCurrentTarget) o).targetID.equals(targetID);
	}
}
