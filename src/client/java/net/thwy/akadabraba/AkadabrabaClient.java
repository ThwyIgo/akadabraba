package net.thwy.akadabraba;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

public class AkadabrabaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (Block b : ModBlocks.CUTOUT_BLOCKS)
			BlockRenderLayerMap.INSTANCE.putBlock(b, RenderLayer.getCutout());
	}
}
