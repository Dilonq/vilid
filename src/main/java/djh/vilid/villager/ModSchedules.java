package djh.vilid.villager;

import djh.vilid.Vilid;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModSchedules {
    public static final Schedule BABY_SCHEDULE = new ScheduleBuilder(new Schedule())
            .withActivity(0, Activity.IDLE)
            .withActivity(2000,ModActivities.LEARN)
            .withActivity(9000,Activity.IDLE)
//            .withActivity(10000,Activity.IDLE)
            .withActivity(10000,Activity.PLAY)
            .withActivity(12000,Activity.REST)
            .build();

    public static void register(){
        Registry.register(Registries.SCHEDULE, new Identifier(Vilid.MOD_ID,"baby_schedule"),BABY_SCHEDULE);
    }
}
