package net.thwy.akadabraba;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.thwy.akadabraba.lib.ModModelGenerator;
import net.thwy.akadabraba.lib.ModelGen;
import net.thwy.akadabraba.lib.Register;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ModItems {
    @Register
    @ModelGen("GENERATED")
    public static final Item spell = new Item(new Item.Settings().maxCount(1));
    @Register
    public static final ItemGroup AKADABRABA = FabricItemGroup.builder()
            .icon(() -> new ItemStack(spell))
            .displayName(Text.translatable("itemGroup." + Akadabraba.MOD_ID + ".akadabraba"))
            .entries((context, entries) -> entries.add(spell))
            .build();

    static {
        ModModelGenerator.add(ModItems.class);
    }

    // Register all static variables in this class annotated with @Register
    public static void register() {
        final List<Field> regFields = Arrays.stream(ModItems.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Register.class)
                        && Modifier.isStatic(field.getModifiers())
                ).toList();

        for (Field field : regFields) {
            final Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            final Register annotation = field.getAnnotation(Register.class);
            String name = (annotation.name().isEmpty()
                    ? field.getName()
                    : annotation.name()
            ).toLowerCase();

            switch (value) {
                case Item i -> registerItem(i, name);
                case ItemGroup ig -> registerItemGroup(ig, name);
                default -> throw new IllegalArgumentException("I don't know how to register object " + value);
            }
        }
    }

    protected static void registerItem(Item item, String name) {
        Registry.register(Registries.ITEM, new Identifier(Akadabraba.MOD_ID, name), item);
    }

    protected static void registerItemGroup(ItemGroup itemGroup, String name) {
        Registry.register(Registries.ITEM_GROUP, new Identifier(Akadabraba.MOD_ID, name), itemGroup);
    }
}
