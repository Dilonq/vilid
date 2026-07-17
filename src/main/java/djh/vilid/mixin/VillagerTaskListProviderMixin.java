package djh.vilid.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import djh.vilid.villager.ModProfessions;
import djh.vilid.villager.task.CountMoneyTask;
import djh.vilid.villager.task.HireUnemployedVillagersTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(VillagerTaskListProvider.class)
public class VillagerTaskListProviderMixin {

    @Inject(method = "createWorkTasks", at = @At("RETURN"), cancellable = true)
    private static void addCapitalistTasks(VillagerProfession profession, float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir) {

        if (profession == ModProfessions.CAPITALIST) {
            // Get the vanilla tasks that were just generated
            List<Pair<Integer, ? extends Task<? super VillagerEntity>>> customTasks = new ArrayList<>(cir.getReturnValue());

            //add tasks, wiht priority (lower is higher)
            customTasks.add(Pair.of(2, new HireUnemployedVillagersTask()));
            customTasks.add(Pair.of(3, new CountMoneyTask()));

            // Overwrite the return value with our new list
            cir.setReturnValue(ImmutableList.copyOf(customTasks));
        }
    }
}