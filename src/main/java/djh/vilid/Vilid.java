package djh.vilid;

import djh.vilid.block.ModBlocks;
import djh.vilid.item.ModItemGroups;
import djh.vilid.item.ModItems;
import djh.vilid.util.ModCustomTrades;
import djh.vilid.villager.*;
//import djh.vilid.villager.task.SeekTeacherTask;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Vilid implements ModInitializer {
	public static final String MOD_ID = "vilid";
	public static long lastProcessedDay = -1;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		//mod initialization stuff
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModCustomTrades.registerCustomTrades();
		ModProfessions.registerVillagers();

		ModActivities.register();
		ModSchedules.register();


		//villager name handling
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof VillagerEntity villager && !world.isClient) {

				VillagerExt ext = (VillagerExt) villager;

				ext.genBaseNBT(villager.getVillagerData().getProfession());

				// Avoid setting the name repeatedly if it's already set
				if (villager.getCustomName() == null) {
					String name = ext.getLegalName();
					villager.setCustomName(Text.literal(name));
					villager.setCustomNameVisible(true);
				}
			}
		});

		//villager daily checks
		ServerTickEvents.START_WORLD_TICK.register(world -> {
			if (!world.isClient()) {
				long day = world.getTimeOfDay() / 24000;
				long timeOfDay = world.getTimeOfDay() % 24000;

				ServerWorld sw = (ServerWorld) world;
				//THIS WILL ONLY WORK IN THE OVERWORLD!!!!!
				if (timeOfDay == 50 && day != lastProcessedDay && world.getRegistryKey()== ServerWorld.OVERWORLD) { // morning
					lastProcessedDay = day;


					for (Entity e : sw.iterateEntities()) {
						if (e instanceof VillagerEntity) {
							VillagerEntity villager = (VillagerEntity) e;
							VillagerExt ext = (VillagerExt) villager;

							if (ext.hasMetToday()) {
								ext.getIdeology().bringJoy(12);
								ext.resetMetToday();
							} else {
								ext.getIdeology().bringSadness(6);
								ext.resetMetToday();
							}


							//run this last, its the most aggressive change
							if (ext.getIdeology().happinessUnder(10)) {
								ext.Criminalize();
							}
						}
					}
				}
			}
		});

//		//baby villager new scheduling (with learning phase)
//		ServerEntityEvents.ENTITY_LOAD.register(((entity, serverWorld) -> {
//			if (entity instanceof VillagerEntity villager && villager.isBaby()) {
//				villager.getBrain().setSchedule(ModSchedules.BABY_SCHEDULE);
//			}
//		}));
	}
}