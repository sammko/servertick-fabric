package net.cavoj.servertick;

import net.minecraft.util.Identifier;

public class Packets {
    public static final Identifier PACKET_TOGGLE_DEBUG_SCREEN = new Identifier("servertick", "test");
    public static final Identifier PACKET_FULL_METRICS = new Identifier("servertick", "metrics/full");
    public static final Identifier PACKET_SAMPLE_METRICS = new Identifier("servertick", "metrics/sample");
}
