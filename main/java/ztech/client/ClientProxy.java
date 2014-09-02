package ztech.client;

import ztech.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ClientProxy extends CommonProxy
{
	@Override
	public void PlaySound()
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("electricfish:electricOverload")));
	}
}
