package net.thwy.akadabraba.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Function;

public class BlinkSpellItem extends Item {
    public BlinkSpellItem(Settings settings) {
        super(settings);
    }

    private static Optional<Vec3d> searchSpot(Vec3d initialPos, Vec3d arrow, World world, double maxDistance) {
        final Vec3d norm = arrow.normalize();
        final Function<BlockPos, Boolean> hasCollision = pos -> !world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();

        for (Vec3d finalPos = initialPos.add(arrow); initialPos.isInRange(finalPos, maxDistance); finalPos = finalPos.add(norm)) {
            final BlockPos playerBlockPos = new BlockPos((int) Math.round(finalPos.getX()), (int) Math.round(finalPos.getY()), (int) Math.round(finalPos.getZ()));

            if (!hasCollision.apply(playerBlockPos)) {
                if (!hasCollision.apply(playerBlockPos.down()))
                    return Optional.of(playerBlockPos.down().toCenterPos());
                else if (!hasCollision.apply(playerBlockPos.up()))
                    return Optional.of(playerBlockPos.toCenterPos());
            }
        }
        return Optional.empty();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        final BlockPos blockPos = context.getBlockPos();
        final PlayerEntity player = context.getPlayer();

        if (!context.getWorld().isClient() && player != null) {
            final Vec3d distance = player.getPos().negate().add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            final Optional<Vec3d> newPos = searchSpot(player.getPos(), distance, context.getWorld(), 15);
            if (newPos.isPresent()) {
                player.requestTeleport(newPos.get().getX(), newPos.get().getY(), newPos.get().getZ());
                context.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}
