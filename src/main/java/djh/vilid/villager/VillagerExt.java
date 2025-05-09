package djh.vilid.villager;

import djh.vilid.pol.CoreValue;
import djh.vilid.pol.Ideology;
import djh.vilid.pol.Viewpoint;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public interface VillagerExt {
    String getLegalName();
    void setLegalName(String name);

    Ideology getIdeology();
    void setIdeology(Ideology i);

    void Criminalize();

    void genBaseNBT(VillagerProfession prof);

    void markMetToday();
    boolean hasMetToday();
    void resetMetToday();
}
