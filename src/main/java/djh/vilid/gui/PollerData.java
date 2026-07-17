package djh.vilid.gui;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

public record PollerData(int villagerId, int happiness, String ideology, List<String> lines, List<String> moodLines) {

    // This codec replaces your manual buffer writing/reading
    public static final PacketCodec<RegistryByteBuf, PollerData> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, PollerData::villagerId,
            PacketCodecs.INTEGER, PollerData::happiness,
            PacketCodecs.STRING, PollerData::ideology,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), PollerData::lines,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), PollerData::moodLines,
            PollerData::new
    );
}