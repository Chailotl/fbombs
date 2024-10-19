package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.init.FBombsBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin
{
    @Inject(
        method = "useOnBlock",
        at = @At("TAIL"),
        cancellable = true)
    private void useGunpowderTrail(ItemUsageContext ctx, CallbackInfoReturnable<ActionResult> cir)
    {
        if (ctx.getStack().isOf(Items.GUNPOWDER))
        {
            cir.setReturnValue(FBombsBlocks.GUNPOWDER_TRAIL_BLOCK.asItem().useOnBlock(ctx));
        }
    }
}