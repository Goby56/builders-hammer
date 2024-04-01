package com.goby56.buildershammer.event;

import com.goby56.buildershammer.render.PresetOutlineRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler implements ClientTickEvents.EndTick {
    @Override
    public void onEndTick(MinecraftClient client) {
        PresetOutlineRenderer.tick();
    }
}
