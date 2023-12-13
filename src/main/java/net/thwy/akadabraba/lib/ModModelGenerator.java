package net.thwy.akadabraba.lib;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModModelGenerator extends FabricModelProvider {
    private static final List<pair<Item, Model>> itemModels = new ArrayList<>();
    private static final List<pair<Block, Function<BlockStateModelGenerator, Consumer<Block>>>> blockModels = new ArrayList<>();

    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    public static void add(Item item, Model model) {
        itemModels.add(new pair<>(item, model));
    }

    public static void add(Block block, Function<BlockStateModelGenerator, Consumer<Block>> f) {
        blockModels.add(new pair<>(block, f));
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockModels.forEach(pair -> pair.second.apply(blockStateModelGenerator).accept(pair.first));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModels.forEach(pair -> itemModelGenerator.register(pair.first, pair.second));
    }

    private record pair<T, S>(T first, S second) {
    }
}
