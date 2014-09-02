package ztech.items;

import net.minecraft.util.DamageSource;

public class ElectricFishDamageSource extends DamageSource
{
	public static ElectricFishDamageSource electricity = new ElectricFishDamageSource("electricity");

	protected ElectricFishDamageSource(String name)
	{
		super(name);
	}
}
