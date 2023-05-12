package de.rusticprism.scrollabletooltips;

import net.fabricmc.api.ClientModInitializer;

public class ScrollableTooltips implements ClientModInitializer {
    public static int scrollDistance;
    @Override
    public void onInitializeClient() {
        scrollDistance = 0;
    }
}
