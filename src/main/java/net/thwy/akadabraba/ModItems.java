package net.thwy.akadabraba;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.thwy.akadabraba.lib.ModModelGenerator;
import net.thwy.akadabraba.lib.Register;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ModItems {
    @Register(model = "GENERATED")
    public static final Item spell = new Item(new FabricItemSettings().maxCount(1));

    @Register(name = "akadabraba")
    public static final ItemGroup AKADABRABA = FabricItemGroup.builder()
            .icon(() -> new ItemStack(spell))
            .displayName(Text.translatable("itemGroup." + Akadabraba.MOD_ID + ".akadabraba"))
            .entries((context, entries) -> entries.add(spell))
            .build();

    // Register all static variables in this class annotated with @Register
    public static void register() {
        Akadabraba.LOGGER.info("Registering ModItens");

        final List<Field> regItemFields = Arrays.stream(ModItems.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Register.class)
                        && Modifier.isStatic(field.getModifiers())
                ).toList();

        for (Field field : regItemFields) {
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

            // Register ItemGroup
            if (value instanceof ItemGroup ig)
                registerItemGroup(ig, name);
                // Register item
            else if (value instanceof Item i)
                registerItem(i, name, annotation);
            else {
                throw new IllegalArgumentException("I don't know how to register object " + value);
            }
        }
    }

    protected static void registerItem(Item item, String name, Register annotation) {
        Registry.register(Registries.ITEM, new Identifier(Akadabraba.MOD_ID, name), item);

        // Model generation
        if (annotation.model().isEmpty()) {
            Akadabraba.LOGGER.info("Skipping model generation for " + name);
            return;
        }
        try {
            Object m = Models.class.getField(annotation.model()).get(null);
            if (m instanceof Model)
                ModModelGenerator.add(item, (Model) m);
            else {
                throw new RuntimeException("Model generation failed for " + annotation.model());
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("There's no model named " + annotation.model());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void registerItemGroup(ItemGroup itemGroup, String name) {
        Registry.register(Registries.ITEM_GROUP, new Identifier(Akadabraba.MOD_ID, name), itemGroup);
    }
}
