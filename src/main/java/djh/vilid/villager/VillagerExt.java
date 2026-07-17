package djh.vilid.villager;

import djh.vilid.ideology.politics.Viewpoint;
import net.minecraft.village.VillagerProfession;

public interface VillagerExt {
    String getLegalName();
    void setLegalName(String name);

    Viewpoint getViewpoint();
    void setViewpoint(Viewpoint i);

    void criminalize();

    void genBaseNBT(VillagerProfession prof);

    void markMetToday();
    boolean hasMetToday();
    void resetMetToday();
    //daily check stuff
    void updateDaily();

    //happiness testers
    int getMoodOutlook();
    boolean hasBed();
    boolean isIronGolemNearby(double radius);
    int getNearbyVillagerCount(double radius);
}
