package djh.vilid.villager;

import com.google.common.collect.ImmutableList;
import djh.vilid.Vilid;
//import djh.vilid.villager.task.SeekTeacherTask;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.task.ScheduleActivityTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.datafixers.util.Pair;

public class ModActivities {
    public static final Activity LEARN = new Activity("learn");

    public static void register(){
        Registry.register(Registries.ACTIVITY, new Identifier(Vilid.MOD_ID,"learn"),LEARN);
    }

//    public static ImmutableList<com.mojang.datafixers.util.Pair<Integer, ? extends Task<? super VillagerEntity>>> createLearnTasks() {
//        return ImmutableList.of(
////                new Pair<>(1, new SeekTeacherTask()),
//                new Pair<>(2,new ScheduleActivityTask())
//        );
//    }
}
