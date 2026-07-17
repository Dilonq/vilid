package djh.vilid;

import djh.vilid.block.ModBlocks;
import djh.vilid.block.entity.ModBlockEntities;
import djh.vilid.commands.ModCommands;
import djh.vilid.gui.ModGUI;
import djh.vilid.item.ModItemGroups;
import djh.vilid.item.ModItems;
import djh.vilid.nation.NationData;
import djh.vilid.nation.NationUtil;
import djh.vilid.trade.ModCustomTrades;
import djh.vilid.villager.*;
//import djh.vilid.villager.task.SeekTeacherTask;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerProfession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		ModBlockEntities.registerModBlockEntities();

		ModCustomTrades.registerCustomTrades();
		ModProfessions.registerVillagers();

//		ModActivities.register();
//		ModSchedules.register();

		ModGUI.init();

		//register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.register(dispatcher);
		});

		//flag handler
//		ServerTickEvents.END_WORLD_TICK.register(VillageState::debugPrintVillagerNearestBanner);


		//villager name handling
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof VillagerEntity villager && !world.isClient) {
				VillagerExt ext = (VillagerExt) villager;
				ServerWorld sw = (ServerWorld) world;

				ext.genBaseNBT(villager.getVillagerData().getProfession());
				villager.setCustomName(Text.literal(ext.getLegalName()));
				villager.setCustomNameVisible(true);

				NationData data = NationData.get(sw);

				// check cache
				var snapshot = data.villagerStates.get(villager.getUuid());
				if (snapshot != null) {
					// apply cached values to entity
					var vp = ext.getViewpoint();
					vp.setNation(snapshot.nation());
					vp.setHappy(snapshot.happiness());
					vp.setIdeology(snapshot.ideology());
				} else {
					// brand new villager -- try to join a nearby nation immediately
					String nearby = NationUtil.findNearbyNation(villager, sw);
					if (nearby != null) {
						ext.getViewpoint().setNation(nearby);
						LOGGER.info(ext.getLegalName() + " joined " + nearby + " on spawn (nearby villager)");
					}
					// else: leave at (none) -- the socialize/MEET hook founds a nation later
				}

				// update cache from entity too (in case new)
				data.updateVillager(
						villager.getUuid(),
						ext.getViewpoint().getNation(),
						ext.getViewpoint().getHappy(),
						ext.getViewpoint().getIdeology()
				);

				LOGGER.info("Villager loaded: " + ext.getLegalName() + " from nation: " + ext.getViewpoint().getNation());
			}
		});


		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if (entity instanceof VillagerEntity villager && !world.isClient) {
				Entity.RemovalReason reason = villager.getRemovalReason();
				boolean actuallyGone = reason == Entity.RemovalReason.KILLED
						|| reason == Entity.RemovalReason.DISCARDED;
				if (actuallyGone) {
					NationData.get((ServerWorld) world).removeVillager(villager.getUuid());
				}
				// else: just a chunk unload -- keep the snapshot, ENTITY_LOAD will restore it on reload
			}
		});


		//villager daily checks
		ServerTickEvents.START_WORLD_TICK.register(world -> {
			if (!world.isClient()) {
				long day = world.getTimeOfDay() / 24000;
				long timeOfDay = world.getTimeOfDay() % 24000;

				ServerWorld sw = (ServerWorld) world;
				if (day != lastProcessedDay) {
					lastProcessedDay = day;


					for (Entity e : sw.iterateEntities()) {
						if (e instanceof VillagerEntity) {
							VillagerEntity villager = (VillagerEntity) e;
							VillagerExt ext = (VillagerExt) villager;

							ext.updateDaily();


							//run this last, its the most aggressive change
//							if (ext.getViewpoint().happinessUnder(10)) {
//								ext.Criminalize();
//							}

							// log nation data
							NationData.get(sw).updateVillager(
									villager.getUuid(),
									ext.getViewpoint().getNation(),
									ext.getViewpoint().getHappy(),
									ext.getViewpoint().getIdeology()
							);
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