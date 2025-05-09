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
    public static final RegistryKey<PointOfInterestType> TEACHER_POI_KEY = poiKey("teacherpoi");
    public static final PointOfInterestType TEACHER_POI = registerPoi("teacherpoi", ModBlocks.DESK);

//    public static final VillagerProfession TEACHER = registerProfession("teacher", TEACHER_POI_KEY);

    public static final VillagerProfession TEACHER = Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(Vilid.MOD_ID,"teacher"),
            new VillagerProfession("teacher", entry -> entry.matchesKey(TEACHER_POI_KEY), entry -> entry.matchesKey(TEACHER_POI_KEY),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN));

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type){
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(Vilid.MOD_ID,name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN));
    }


    private static PointOfInterestType registerPoi(String name, Block block){
        return PointOfInterestHelper.register(new Identifier(Vilid.MOD_ID,name),1,1,block);
    }

    private static RegistryKey<PointOfInterestType> poiKey(String name){
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(Vilid.MOD_ID,name));
    }

    public static void registerVillagers(){
        Vilid.LOGGER.info("Registering Villagers for "+Vilid.MOD_ID);
    }
}
