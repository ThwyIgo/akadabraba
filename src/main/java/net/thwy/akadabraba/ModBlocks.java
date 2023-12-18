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
import net.thwy.akadabraba.lib.ModBlockLootTables;
import net.thwy.akadabraba.lib.ModModelGenerator;
import net.thwy.akadabraba.lib.Register;
import org.apache.commons.lang3.function.TriFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class ModBlocks {
    @Register
    public static Block MAGICAL_QUARTZ_PILLAR = new MagicalPillarBlock(FabricBlockSettings
            .copyOf(Blocks.QUARTZ_PILLAR)
            .nonOpaque());
    // Used by client
    public static final Block[] CUTOUT_BLOCKS = {MAGICAL_QUARTZ_PILLAR};

    protected static final ModBlockLootTables.DropMap[] BLOCK_DROPS = {
            new ModBlockLootTables.DropMap(MAGICAL_QUARTZ_PILLAR, Blocks.QUARTZ_PILLAR)
    };

    static {
        ModModelGenerator.add(ModBlocks.class);
        ModBlockLootTables.add(List.of(BLOCK_DROPS));
    }

    public static void register() {
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

        ModModelGenerator.addSupplier(MAGICAL_QUARTZ_PILLAR_BlockStateModel());
    }

    protected static void registerBlock(Block block, String name) {
        Registry.register(Registries.BLOCK, new Identifier(Akadabraba.MOD_ID, name), block);
    }

    private static MultipartBlockStateSupplier MAGICAL_QUARTZ_PILLAR_BlockStateModel() {
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

        Akadabraba.LOGGER.info(ModelIds.getBlockModelId(MAGICAL_QUARTZ_PILLAR).toString());

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

        return supplier;
    }
}
