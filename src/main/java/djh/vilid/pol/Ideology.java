package djh.vilid.pol;


import net.minecraft.nbt.NbtCompound;

import java.util.Random;

public class Ideology {
    public int economic;
    public int social;
    public int governmental;
    public int happy;

    public Ideology(){}

    public Ideology randomIdeology(Random random){
        Ideology i = new Ideology();
        i.randomize(random);
        return i;
    }

    public Ideology randomIdeology(){
        Random r = new Random(); return randomIdeology(r);
    }

    public void randomize(Random random){
        Ideology i = this;

        i.economic = 25+random.nextInt(49);
        i.social = 25+random.nextInt(49);
        i.governmental = 25+random.nextInt(49);

        i.happy = 100;
    }

    public void randomize(){randomize(new Random());}

    public void add(Ideology ideology){
        this.economic+=ideology.economic;
        this.social+=ideology.social;
        this.governmental+=governmental;

        if (this.economic<0){this.economic=0;}
        if (this.social<0){this.social=0;}
        if (this.governmental<0){this.governmental=0;}
        if (this.economic>99){this.economic=99;}
        if (this.social>99){this.social=99;}
        if (this.governmental>99){this.governmental=99;}
    }

    public void average(Ideology ideology){
        this.economic=(this.economic+ideology.economic)/2;
        this.social=(this.social+ideology.social)/2;
        this.governmental=(this.governmental+ideology.governmental)/2;
    }

    //comparison shit
    public boolean happinessAtLeast(int comp){return happy>=comp;}
    public boolean happinessUnder(int comp){return !happinessAtLeast(comp);}
    public void bringJoy(int joy){happy+=joy;if (happy>99){happy=99;}}
    public void bringSadness(int sad){happy-=sad;if (happy<0){happy=0;}}

    //nbt shit
    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("social",social);
        nbt.putInt("economic",economic);
        nbt.putInt("governmental",governmental);
        nbt.putInt("happy",happy);
        return nbt;
    }

    public static Ideology fromNbt(NbtCompound nbt){
        Ideology i = new Ideology();

        i.social = nbt.getInt("social");
        i.economic = nbt.getInt("economic");
        i.governmental = nbt.getInt("governmental");
        i.happy = nbt.getInt("happy");

        return i;
    }
}
