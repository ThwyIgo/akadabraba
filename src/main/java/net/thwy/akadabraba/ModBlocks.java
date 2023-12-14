package net.thwy.akadabraba;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.thwy.akadabraba.lib.BlockState;
import net.thwy.akadabraba.lib.ModModelGenerator;
import net.thwy.akadabraba.lib.Register;
import net.thwy.akadabraba.lib.VoxelPillarBlock;
import org.apache.commons.lang3.function.TriFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ModBlocks {
    @Register
    public static Block MAGICAL_QUARTZ_PILLAR = new VoxelPillarBlock(FabricBlockSettings
            .copyOf(Blocks.QUARTZ_PILLAR).nonOpaque());

    public static final Block[] CUTOUT_BLOCKS = {MAGICAL_QUARTZ_PILLAR};

    public static void register() {
        Akadabraba.LOGGER.info("Registering ModBlocks");

        final List<Field> regFields = Arrays.stream(ModBlocks.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Register.class)
                        && Modifier.isStatic(field.getModifiers())
                ).toList();

        for (Field field : regFields) {
            Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            final Register annotation = field.getAnnotation(Register.class);
            String name = annotation.name();
            if (name.isEmpty())
                name = field.getName();
            name = name.toLowerCase();

            if (value instanceof Block b) {
                registerBlock(b, name);
            } else {
                throw new IllegalArgumentException("I don't know how to register object " + value);
            }
        }
    }

    public static void generateBlockStateModels() {
        Akadabraba.LOGGER.info("Registering ModBlocks");

        final List<Field> BlkStateFields = Arrays.stream(ModBlocks.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(BlockState.class)
                        && Modifier.isStatic(field.getModifiers())
                ).toList();

        for (Field field : BlkStateFields) {
            final BlockState annotation = field.getAnnotation(BlockState.class);
            final String model = annotation.model();
            final Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value instanceof Block b) {
                switch (model) {
                    case "Y_ROTATE" -> ModModelGenerator.add(b, ModBlocks::blockStateModelY_ROTATE);
                    default -> throw new IllegalArgumentException("Model " + model + " isn't valid");
                }
            } else
                throw new IllegalArgumentException("BlockState applies only to Block, but object " +
                        value + " was given");
        }

        // Call block-specific functions
        MAGICAL_QUARTZ_PILLAR_generateBlockStateModel();
    }

    private static Consumer<Block> blockStateModelY_ROTATE(BlockStateModelGenerator blockStateModelGenerator) {
        return b -> {
            MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(b);
            final Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            final VariantSettings.Rotation[] rotations = {
                    VariantSettings.Rotation.R0,
                    VariantSettings.Rotation.R90,
                    VariantSettings.Rotation.R180,
                    VariantSettings.Rotation.R270
            };

            for (int i = 0; i < directions.length; i++) {
                supplier.with(
                        When.create()
                                .set(Properties.HORIZONTAL_FACING, directions[i]),
                        BlockStateVariant.create()
                                .put(VariantSettings.Y, rotations[i])
                                .put(VariantSettings.MODEL, ModelIds.getBlockModelId(b))
                );
            }

            blockStateModelGenerator.blockStateCollector.accept(supplier);
        };

    }

    protected static void registerBlock(Block block, String name) {
        Registry.register(Registries.BLOCK, new Identifier(Akadabraba.MOD_ID, name), block);
    }

    private static void MAGICAL_QUARTZ_PILLAR_generateBlockStateModel() {
        MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(MAGICAL_QUARTZ_PILLAR);
        final Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        final VariantSettings.Rotation[] rotations = {
                VariantSettings.Rotation.R0,
                VariantSettings.Rotation.R90,
                VariantSettings.Rotation.R180,
                VariantSettings.Rotation.R270
        };

        final TriFunction<Integer, Boolean, Boolean, When> whenHelper =
                (i, up, down) -> When.create()
                        .set(Properties.HORIZONTAL_FACING, directions[i])
                        .set(Properties.UP, up)
                        .set(Properties.DOWN, down);
        final BiFunction<Integer, String, BlockStateVariant> stateVariantHelper =
                (i, suffix) -> BlockStateVariant.create()
                        .put(VariantSettings.Y, rotations[i])
                        .put(VariantSettings.MODEL, ModelIds.getBlockModelId(MAGICAL_QUARTZ_PILLAR).withSuffixedPath(suffix));

        for (int i = 0; i < directions.length; i++) {
            supplier.with(whenHelper.apply(i, false, false),
                    stateVariantHelper.apply(i, "")
            ).with(whenHelper.apply(i, true, false),
                    stateVariantHelper.apply(i, "_up")
            ).with(whenHelper.apply(i, false, true),
                    stateVariantHelper.apply(i, "_down")
            ).with(whenHelper.apply(i, true, true),
                    stateVariantHelper.apply(i, "_both")
            );
        }

        ModModelGenerator.add(null, blockStateModelGenerator -> b ->
                blockStateModelGenerator.blockStateCollector.accept(supplier));
    }

}
