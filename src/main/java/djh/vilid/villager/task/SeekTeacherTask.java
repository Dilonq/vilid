//package djh.vilid.villager.task;
//
//import djh.vilid.villager.ModProfessions;
//import net.minecraft.entity.ai.brain.EntityLookTarget;
//import net.minecraft.entity.ai.brain.MemoryModuleState;
//import net.minecraft.entity.ai.brain.MemoryModuleType;
//import net.minecraft.entity.ai.brain.WalkTarget;
//import net.minecraft.entity.ai.brain.task.MultiTickTask;
//import net.minecraft.entity.ai.brain.task.Task;
//import net.minecraft.entity.passive.VillagerEntity;
//import net.minecraft.particle.ParticleTypes;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.village.VillagerProfession;
//
//import java.time.Duration;
//import java.util.Comparator;
//import java.util.Map;
//import java.util.Optional;
//
//public class SeekTeacherTask extends Task<VillagerEntity> {
//    private static final int RADIUS = 16;
//
//    public SeekTeacherTask() {
//        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), 200);
//    }
//
//    @Override
//    protected boolean shouldRun(ServerWorld world, VillagerEntity villager) {
//        return villager.isBaby() ||
//                villager.getVillagerData().getProfession().equals(VillagerProfession.NONE) ||
//                villager.getVillagerData().getProfession().equals(VillagerProfession.NITWIT);
//    }
//
//    @Override
//    protected void run(ServerWorld world, VillagerEntity villager, long time) {
//        // ✨ PARTICLE DEBUG
//        world.spawnParticles(
//                ParticleTypes.HAPPY_VILLAGER,       // Use whatever particle you want
//                villager.getX(),                    // X
//                villager.getY() + 1.0,              // Y (a bit above head)
//                villager.getZ(),                    // Z
//                5,                                  // count
//                0.5, 0.5, 0.5,                      // offset X/Y/Z (spread)
//                0.1                                 // speed
//        );
//
//        Optional<VillagerEntity> target = world.getEntitiesByClass(
//                VillagerEntity.class,
//                villager.getBoundingBox().expand(RADIUS),
//                v -> !v.isBaby() && v.getVillagerData().getProfession() == ModProfessions.TEACHER
//        ).stream().min(Comparator.comparingDouble(v -> v.squaredDistanceTo(villager)));
//
//        target.ifPresent(teacher -> {
//            villager.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(
//                    new EntityLookTarget(teacher, true), 0.5F, 1));
//        });
//    }
//}