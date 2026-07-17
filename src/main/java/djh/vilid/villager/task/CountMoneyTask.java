package djh.vilid.villager.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;

public class CountMoneyTask extends MultiTickTask<VillagerEntity> {

    public CountMoneyTask() {
        // This task can ONLY run if the brain actually remembers where the job site is
        super(ImmutableMap.of(
                MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT
        ));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, VillagerEntity capitalist) {
        Optional<GlobalPos> jobSite = capitalist.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isEmpty() || jobSite.get().getDimension() != world.getRegistryKey()) {
            return false;
        }

        // Let vanilla AI handle the walking. Only take over once they are within 3 blocks.
        BlockPos pos = jobSite.get().getPos();
        return pos.isWithinDistance(capitalist.getPos(), 3.0);
    }

    @Override
    protected void run(ServerWorld world, VillagerEntity capitalist, long time) {
        // Task started: lock eyes on the emerald block
        Optional<GlobalPos> jobSite = capitalist.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        jobSite.ifPresent(globalPos -> capitalist.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, VillagerEntity capitalist, long time) {
        // Stop staring if someone pushes them far away or they lose the job site memory
        Optional<GlobalPos> jobSite = capitalist.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isEmpty()) return false;

        return jobSite.get().getPos().isWithinDistance(capitalist.getPos(), 3.0);
    }

    @Override
    protected void keepRunning(ServerWorld world, VillagerEntity capitalist, long time) {
        // Continuously force them to look at it every tick, overriding their natural desire to look around
        Optional<GlobalPos> jobSite = capitalist.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        jobSite.ifPresent(globalPos -> capitalist.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
    }
}