package djh.vilid.ideology.politics;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.VillagerProfession;

import java.util.Random;

public class Viewpoint {
    public Random r;
    public String nation;
    public Ideology ideology;
    public int happy;//opposition to ideological changes (0-99)
    public boolean militant;

    public Viewpoint(VillagerProfession prof) {
        r = new Random();
        generateViewpoint(prof);
    }

    public Viewpoint() {
        r = new Random();
    }

    //nbt shit
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("ideology", ideology.name());
        nbt.putBoolean("militancy", militant);
        nbt.putInt("happy", happy);
        nbt.putString("nation", nation);
        return nbt;
    }

    public static Viewpoint fromNbt(NbtCompound nbt) {
        Viewpoint vp = new Viewpoint();

        if (nbt.contains("ideology")) {
            vp.ideology = Ideology.valueOf(nbt.getString("ideology"));
        }

        if (nbt.contains("militancy")) {
            vp.militant = nbt.getBoolean("militancy");
        }

        if (nbt.contains("happy")) {
            vp.happy = nbt.getInt("happy");
        }

        if (nbt.contains("nation")) {
            vp.nation = nbt.getString("nation");
        }

        return vp;
    }

    public void deltaHappy(int delta){this.happy+=delta;}

    public void setHappy(Integer happy) {
        this.happy = happy;
    }

    public Integer getHappy() {
        return happy;
    }

    public void setIdeology(Ideology ideology) {
        this.ideology = ideology;
    }

    public Ideology getIdeology() {
        return this.ideology;
    }

    public void setMilitant(boolean militant) {
        this.militant = militant;
    }

    public Boolean getMilitant() {
        return this.militant;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getNation() {
        return this.nation;
    }

    //actual logic
    public void generateViewpoint(VillagerProfession prof) {
        if (prof.equals(VillagerProfession.NITWIT)) {
            //nitwits will always have extreme views
            if (r.nextBoolean()) {
                ideology = Ideology.COMMUNIST;
            } else {
                ideology = Ideology.REACTIONARY;
            }
        } else {
            //non-nitwits recieve a randomized, reasonable ideology

            ideology = Ideology.CONSERVATIVE;

            Random random = new Random();
            if (random.nextBoolean()) {
                ideology = Ideology.getRandom(Alignment.MODERATE);
            } else {
                if (random.nextBoolean()) {
                    ideology = Ideology.getRandom(Alignment.LEFT);
                } else {
                    ideology = Ideology.getRandom(Alignment.RIGHT);
                }
            }
        }

        //randomized happy
        happy = 70 + r.nextInt(10);
        nation = "NONE";
        militant = false;
    }

    public static int adjustVillager(Viewpoint vp, Ideology newIdeology, Ideology oldIdeology) {
        Ideology current = vp.getIdeology();
        Ideology updated = Ideology.adjustIdeology(current, newIdeology, oldIdeology, vp.getHappy());

        if (!current.equals(updated)) {
            vp.setIdeology(updated);
            return 1;
        }
        return 0;
    }


}