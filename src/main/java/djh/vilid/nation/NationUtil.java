package djh.vilid.nation;

import djh.vilid.commands.ModCommands;
import djh.vilid.villager.VillagerExt;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class NationUtil {
    private static final double NEARBY_RADIUS = 48.0; // roughly one village's spread

    /**
     * Finds the nation of the nearest villager to the given villager that
     * already has a nation assigned. Returns null if none found in range.
     */
    public static String findNearbyNation(VillagerEntity self, ServerWorld world) {
        Box box = new Box(
                self.getX() - NEARBY_RADIUS, self.getY() - NEARBY_RADIUS, self.getZ() - NEARBY_RADIUS,
                self.getX() + NEARBY_RADIUS, self.getY() + NEARBY_RADIUS, self.getZ() + NEARBY_RADIUS
        );

        return world.getEntitiesByClass(VillagerEntity.class, box, v -> v != self)
                .stream()
                .filter(v -> hasNation(v))
                .min((a, b) -> Double.compare(a.squaredDistanceTo(self), b.squaredDistanceTo(self)))
                .map(v -> ((VillagerExt) v).getViewpoint().getNation())
                .orElse(null);
    }

    public static boolean hasNation(VillagerEntity villager) {
        String nation = ((VillagerExt) villager).getViewpoint().getNation();
        return nation != null && !nation.equalsIgnoreCase("NONE");
    }

    public static String generateVillageName(net.minecraft.util.math.BlockPos pos) {
        return "VILLAGE_" + pos.getX() + "_" + pos.getZ();
    }

    public static String generatePartyName(ServerWorld world, String nationName) {
        NationData data = NationData.get(world);
        return nationName+" ASSEMBLY";
    }

    /**
     * Registers a brand new nation with a single starter party and sets it as ruling.
     */
    public static void foundNation(ServerCommandSource source, String nationName, String ideologyString, NationData data) {
        ModCommands.registerNation(source,nationName);
        String starterParty = generatePartyName(source.getWorld(),nationName);
        ModCommands.registerParty(source, nationName, starterParty, ideologyString);
        data.setRulingParty(nationName, starterParty);
    }
}