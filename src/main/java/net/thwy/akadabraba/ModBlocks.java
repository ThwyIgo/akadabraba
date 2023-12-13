package net.thwy.akadabraba;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.thwy.akadabraba.lib.VoxelPillarBlock;
import net.thwy.akadabraba.lib.Register;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ModBlocks {
    @Register
    public static Block MAGICAL_QUARTZ_PILLAR = new VoxelPillarBlock(FabricBlockSettings.copyOf(Blocks.QUARTZ_PILLAR));

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

    protected static void registerBlock(Block block, String name) {
        Akadabraba.LOGGER.info("BLOCK: " + name);
        Registry.register(Registries.BLOCK, new Identifier(Akadabraba.MOD_ID, name), block);
    }
}
