package djh.vilid.villager.task;

import com.google.common.collect.ImmutableMap;
import djh.vilid.villager.ModProfessions;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import djh.vilid.villager.CapitalistTracker;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;

import java.util.List;

public class HireUnemployedVillagersTask extends MultiTickTask<VillagerEntity> {
    private VillagerEntity target;

    public HireUnemployedVillagersTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, VillagerEntity capitalist) {
        // Rate limiting: only scan the area once a second (every 20 ticks)
        if (world.getTime() % 20 != 0) return false;

        // --- NEW: Simpler "Hiring Hours" ---
        // Get the current time in the 24,000 tick day cycle
        long timeOfDay = world.getTimeOfDay() % 24000;

        // Villagers work from tick 2000 to 9000.
        // We open a 5-second (100 tick) window twice a day.
        boolean isMorningHiringHour = (timeOfDay >= 3000 && timeOfDay <= 3100);
        boolean isAfternoonHiringHour = (timeOfDay >= 7000 && timeOfDay <= 7100);

        // If it's not currently a hiring window, abort.
        if (!isMorningHiringHour && !isAfternoonHiringHour) {
            return false;
        }

        // Find unemployed adult villagers within a set radius
        Box box = capitalist.getBoundingBox().expand(20.0);
        List<VillagerEntity> nearby = world.getEntitiesByClass(
                VillagerEntity.class, box,
                v -> v.getVillagerData().getProfession() == VillagerProfession.NONE && !v.isBaby()
        );

        if (!nearby.isEmpty()) {
            this.target = nearby.get(0);
            return true; // Target found, start the task
        }
        return false;
    }

    @Override
    protected void run(ServerWorld world, VillagerEntity capitalist, long time) {
        capitalist.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(this.target, 0.5f, 2));
        capitalist.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.target, true));
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, VillagerEntity capitalist, long time) {
        return this.target != null && this.target.isAlive()
                && this.target.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    @Override
    protected void keepRunning(ServerWorld world, VillagerEntity capitalist, long time) {
        if (capitalist.squaredDistanceTo(this.target) < 9.0) {
            this.target.setVillagerData(this.target.getVillagerData().withProfession(ModProfessions.WORKER));
            this.target.setExperience(1);


            // Link the Worker to this specific Capitalist
//            ((CapitalistTracker) this.target).vilid$setBossUuid(capitalist.getUuid());

            this.target.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            capitalist.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0f, 1.0f);

            this.target = null;
            capitalist.getBrain().forget(MemoryModuleType.WALK_TARGET);
            capitalist.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        }
    }
}