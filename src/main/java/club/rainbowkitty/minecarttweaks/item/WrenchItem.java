package club.rainbowkitty.minecarttweaks.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import club.rainbowkitty.minecarttweaks.block.SwitchedRailBlock;

/** A tool that throws a right-clicked switched rail to its other branch while it is idle. */
public class WrenchItem extends Item implements PolymerItem {

    /** Constructs a wrench with the given item properties. */
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof SwitchedRailBlock)) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        SwitchedRailBlock.toggle(serverLevel, pos, state);
        Player player = context.getPlayer();
        if (player != null) {
            player.swing(context.getHand(), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return Items.STICK;
    }
}
