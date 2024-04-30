package net.thwy.akadabraba;

import net.fabricmc.api.ModInitializer;
import net.thwy.akadabraba.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Akadabraba implements ModInitializer {
    public static final String MOD_ID = "akadabraba";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ModItems.register();
        ModBlocks.register();
        ModStructures.register();
    }
}
