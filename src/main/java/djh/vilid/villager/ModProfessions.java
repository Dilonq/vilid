package djh.vilid.villager;

import com.google.common.collect.ImmutableSet;
import djh.vilid.Vilid;
import djh.vilid.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class ModProfessions {
    public static final RegistryKey<PointOfInterestType> CAPITALIST_POI_KEY = poiKey("capitalist_poi");

    public static final PointOfInterestType CAPITALIST_POI = registerPoi("capitalist_poi", Blocks.EMERALD_BLOCK);

    //capitalist employs workers. the more employees, the better trades the capitalist has and the faster they refresh
    public static final VillagerProfession CAPITALIST = Registry.register(
            Registries.VILLAGER_PROFESSION,
            Identifier.of(Vilid.MOD_ID, "capitalist"),
            new VillagerProfession(
                    "capitalist",
                    entry -> entry.matchesKey(CAPITALIST_POI_KEY), // heldWorkstation
                    entry -> entry.matchesKey(CAPITALIST_POI_KEY), // acquirableWorkstation
                    ImmutableSet.of(),                             // gatherable items
                    ImmutableSet.of(Blocks.EMERALD_BLOCK),         // secondary job sites (encourages them to stare at it)
                    SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN     // cosmetic work sound
            )
    );

    //unemployed sad villagers can get a poi-less job, just be talking to a capitalist, but no trades since they dont own
    //their own means of production
    public static final VillagerProfession WORKER = Registry.register(
            Registries.VILLAGER_PROFESSION,
            Identifier.of(Vilid.MOD_ID, "worker"),
            new VillagerProfession(
                    "worker",
                    poiType -> false,          // heldWorkstation — never true, no job site claimed
                    poiType -> false,          // acquirableWorkstation — never true, never seeks one
                    ImmutableSet.of(),         // gatherable items — none
                    ImmutableSet.of(),         // secondary job sites — none
                    SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN // cosmetic work sound
            )
    );

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type){
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(Vilid.MOD_ID,name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN));
    }


    private static PointOfInterestType registerPoi(String name, Block block){
        return PointOfInterestHelper.register(Identifier.of(Vilid.MOD_ID,name),1,1,block);
    }

    private static RegistryKey<PointOfInterestType> poiKey(String name){
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(Vilid.MOD_ID,name));
    }

    public static void registerVillagers(){
        Vilid.LOGGER.info("Registering Villagers for "+Vilid.MOD_ID);
    }
}
