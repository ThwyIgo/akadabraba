package net.thwy.akadabraba.lib;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBlockLootTables extends FabricBlockLootTableProvider {
    private static final List<DropMap> BLOCK_DROPS = new ArrayList<>();

    public ModBlockLootTables(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    public static void add(Collection<DropMap> dropMaps) {
        BLOCK_DROPS.addAll(dropMaps);
    }

    @Override
    public void generate() {
        BLOCK_DROPS.forEach(dropMap -> addDrop(dropMap.block, dropMap.item));
    }

    public record DropMap(Block block, ItemConvertible item) {
    }
}
