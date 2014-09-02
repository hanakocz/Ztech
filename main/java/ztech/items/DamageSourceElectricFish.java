package ztech.items;

import net.minecraft.util.DamageSource;

public class DamageSourceElectricFish extends DamageSource
{
	public static DamageSourceElectricFish electricity = new DamageSourceElectricFish("electricity");

	protected DamageSourceElectricFish(String name)
	{
		super(name);
	}
}
