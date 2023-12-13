package net.thwy.akadabraba;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

    // Register all static Item variables in this class annotated with @Register
    public static void register() {
        Akadabraba.LOGGER.info("Registering Itens for " + Akadabraba.MOD_ID);

        final List<Field> regItemFields = Arrays.stream(ModItems.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Register.class)
                        && Modifier.isStatic(field.getModifiers())
                        && Item.class.isAssignableFrom(field.getType())
                ).toList();

        for (Field field : regItemFields) {
            final Register annotation = field.getAnnotation(Register.class);
            final Item item;
            try {
                item = (Item) field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            String name = annotation.name();
            if (name.isEmpty())
                name = field.getName();

            // Regiter item
            Registry.register(Registries.ITEM, new Identifier(Akadabraba.MOD_ID, name), item);

            // Model generation
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
    }
}
