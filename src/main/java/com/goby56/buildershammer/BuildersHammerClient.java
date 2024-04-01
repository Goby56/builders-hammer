package com.goby56.buildershammer;

import com.goby56.buildershammer.event.ClientTickHandler;
import com.goby56.buildershammer.render.PresetOutlineRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class BuildersHammerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(new PresetOutlineRenderer());
        ClientTickEvents.END_CLIENT_TICK.register(new ClientTickHandler());
    }
}
