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
import net.thwy.akadabraba.lib.ModModelGenerator;
import net.thwy.akadabraba.lib.Register;
import net.thwy.akadabraba.lib.VoxelPillarBlock;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ModBlocks {
    @Register(model = "Y_ROTATE")
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
                if (annotation.model().equals("Y_ROTATE"))
                    generateModelY_ROTATE(b);
            } else {
                throw new IllegalArgumentException("I don't know how to register object " + value);
            }
        }
    }

    private static void generateModelY_ROTATE(Block block) {
        ModModelGenerator.add(block, blockStateModelGenerator -> b -> {
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
        });
    }

    protected static void registerBlock(Block block, String name) {
        Registry.register(Registries.BLOCK, new Identifier(Akadabraba.MOD_ID, name), block);
    }
}
