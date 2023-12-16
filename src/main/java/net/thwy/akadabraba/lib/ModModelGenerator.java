package net.thwy.akadabraba.lib;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Make sure your objects are registered in net.minecraft.registry
public class ModModelGenerator extends FabricModelProvider {
    private static final List<Field> FIELDS = new ArrayList<>();
    private static final List<MultipartBlockStateSupplier> mpbsSuppliers = new ArrayList<>();

    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    public static void add(Class<?> c) {
        final var fields = Arrays.stream(c.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(ModelGen.class)
                        && Modifier.isStatic(f.getModifiers()))
                .toList();

        FIELDS.addAll(fields);
    }

    // Add custom Model generation
    public static void addSupplier(MultipartBlockStateSupplier multipartBlockStateSupplier) {
        mpbsSuppliers.add(multipartBlockStateSupplier);
    }

    private static MultipartBlockStateSupplier blockStateModelY_ROTATE(Block block) {
        MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(block);
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
                            .put(VariantSettings.MODEL, ModelIds.getBlockModelId(block))
            );
        }

        return supplier;
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        for (Field field : FIELDS) {
            final ModelGen annotation = field.getAnnotation(ModelGen.class);
            final String modelName = annotation.value();
            Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value instanceof Block b)
                switch (modelName) {
                    case "Y_ROTATE" -> blockStateModelGenerator.blockStateCollector.accept(blockStateModelY_ROTATE(b));
                    default -> throw new IllegalArgumentException("Model " + modelName + " isn't valid");
                }
        }

        mpbsSuppliers.forEach(blockStateModelGenerator.blockStateCollector);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (Field field : FIELDS) {
            Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            final ModelGen annotation = field.getAnnotation(ModelGen.class);
            final String modelName = annotation.value();

            if (value instanceof Item i)
                itemModelGenerator.register(i, getItemModel(modelName));
        }
    }

    protected Model getItemModel(String modelName) {
        Object value;
        try {
            value = Models.class.getField(modelName).get(null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Model " + modelName + " doesn't exist");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (value instanceof Model m)
            return m;
        else
            throw new IllegalArgumentException("Field with name "
                    + modelName + " found, but it is not a Model");
    }
}
