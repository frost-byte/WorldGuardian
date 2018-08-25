package net.frost_byte.worldguardian.utility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class EnumUtil
{
	private EnumUtil() {}

	public static List<String> getAll(Class<? extends Enum<?>> e)
	{
		return Arrays.stream(e.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
	}
}
