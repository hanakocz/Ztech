package ztech.items;

import ztech.Ztech;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFoodOnAStick extends ItemFood
{
    public ItemFoodOnAStick(String name, int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat)
    {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
		setUnlocalizedName(name);
		setTextureName(Ztech.MODID + ":" + name);
        GameRegistry.registerItem(this, name);		
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player)
    {
        // Eat food
        super.onFoodEaten(stack, world, player);

        // Return stick back to player
        ItemStack stick = new ItemStack(Items.stick);
        if (!player.inventory.addItemStackToInventory(stick)) 
        {
        	player.dropPlayerItemWithRandomChoice(stick, false);
        }
    }
}
