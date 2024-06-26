package net.thwy.akadabraba;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.thwy.akadabraba.lib.ModBlockLootTables;
import net.thwy.akadabraba.lib.ModModelGenerator;
import net.thwy.akadabraba.lib.ModTagGenerator;

public class AkadabrabaDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModModelGenerator::new);
        pack.addProvider(ModTagGenerator::new);
        pack.addProvider(ModBlockLootTables::new);
    }
}
