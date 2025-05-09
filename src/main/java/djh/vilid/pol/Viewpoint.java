package djh.vilid.pol;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.VillagerProfession;

import java.util.Map;
import java.util.Random;

import static java.util.Map.entry;

public class Viewpoint {
    Random r;
    Ideology ideology;
    Ideology OGIdeology;
    CoreValue cv;
    int rigidity;//opposition to ideological changes (0-99)

    public Viewpoint(VillagerProfession prof){
        r = new Random();
        generateViewpoint(prof);
    }

    public Viewpoint(){
        r = new Random();
    }

    //nbt shit
    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();
//        nbt.putString("ideology",ideology.name());
//        nbt.putString("ogideology", OGIdeology.name());
        nbt.putString("corevalue",cv.name());
        nbt.putInt("rigidity",rigidity);
        return nbt;
    }

    public static Viewpoint fromNbt(NbtCompound nbt){
        Viewpoint vp = new Viewpoint();
//        vp.ideology = Ideology.valueOf(nbt.getString("ideology"));
//        vp.OGIdeology = Ideology.valueOf(nbt.getString("ogideology"));
        vp.cv = CoreValue.valueOf(nbt.getString("corevalue"));
        vp.rigidity = nbt.getInt("rigidity");
        return vp;
    }

    public void incRigidity(){incRigidity(r.nextInt(5)+5);}
    public void decRigidity(){decRigidity(r.nextInt(5)+5);}
    public void incRigidity(Integer val){rigidity+=val;if (rigidity>99){rigidity=99;}}
    public void decRigidity(Integer val){rigidity-=val;if (rigidity<0){rigidity=0;}}

    public void setRigidity(Integer rigidity){this.rigidity=rigidity;}
    public Integer getRigidity(){return rigidity;}
//    public void setIdeology(Ideology ideology){this.ideology=ideology;}
//    public Ideology getIdeology(){return this.ideology;}
    public void setCV(CoreValue cv){this.cv=cv;}
    public CoreValue getCV(){return this.cv;}

    //actual logic
    private void generateViewpoint(VillagerProfession prof) {
//        if (prof.equals(VillagerProfession.NITWIT)) {
//            //nitwits will always have extreme, confident views
//            rigidity = 99;
//
//            if (r.nextBoolean()){
//                ideology = Ideology.FASCISM;
//                cv = CoreValue.NATIONALIST;
//            }else{
//                ideology = Ideology.COMMUNISM;
//                cv = CoreValue.SJW;
//            }
//        } else {
//            //non-nitwits recieve a randomized, reasonable ideology
//
//            //randomized rigidity 49-99
//            rigidity = 50+r.nextInt(50);
//
//            cv = CoreValue.randomize(r);
//            System.err.println(cv.name());//fix ideology not matching with corevalue
//
//            ideology = cv.getDirection().equals(Direction.RIGHT) ? Ideology.CONSERVATISM : Ideology.LIBERALISM;
//            System.err.println(ideology.name());
//
//            //25% to make more extreme
//            if (r.nextBoolean() && r.nextBoolean()) {
//                forceInfluence(cv.getDirection());
//                System.err.println("more extreme:"+ideology.name());
//            }
//        }
//
//        //remember this starting ideology
//        OGIdeology = ideology;
    }

    public void resetIdeology(){
//        ideology = OGIdeology;
    }

    public Boolean testRigidity(int minSway, int maxSway){
        return rigidity<(minSway+r.nextInt(maxSway-minSway));
    }

    public Boolean testRigidity(){return testRigidity(25,75);}

    public void forceRadicalize(){
//        forceInfluence(Ideology.leans.get(ideology));
    }

    public void attemptRadicalize(){
        if (testRigidity()){
            forceRadicalize();
        }
    }

    public void attemptRadicalize(int minSway, int maxSway){
        if (testRigidity(minSway,maxSway)){forceRadicalize();}
    }

//    public void forceInfluence(Direction dir){
//        switch(dir){
//            case FARLEFT -> ideology = ideology.previous().previous();
//            case LEFT -> ideology = ideology.previous();
//            case MODERATE -> ideology = ideology;
//            case RIGHT -> ideology = ideology.next();
//            case FARRIGHT -> ideology = ideology.next().next();
//        }
//
//        //increase confidence in newfound ideology
//        for (int i=0;i<5;i++){
//            incRigidity();
//        }
//    }

//    public void attemptInfluence(Direction dir){
//        if (testRigidity()){forceInfluence(dir);}
//    }
//
//    public void attemptInfluence(Direction dir,int minSway,int maxSway){
//        if (testRigidity(minSway,maxSway)){
//            forceInfluence(dir);
//        }
//    }
}


//public static final Map<VillagerProfession, Direction> jobWeights = Map.ofEntries(
//        entry(VillagerProfession.NONE,Direction.MODERATE),
//        entry(VillagerProfession.ARMORER,Direction.RIGHT),
//        entry(VillagerProfession.BUTCHER,Direction.RIGHT),
//        entry(VillagerProfession.CARTOGRAPHER,Direction.LEFT),
//        entry(VillagerProfession.CLERIC,Direction.FARRIGHT),
//        entry( VillagerProfession.FARMER,Direction.MODERATE),
//        entry(VillagerProfession.FISHERMAN,Direction.MODERATE),
//        entry(VillagerProfession.FLETCHER,Direction.RIGHT),
//        entry( VillagerProfession.LEATHERWORKER,Direction.MODERATE),
//        entry( VillagerProfession.WEAPONSMITH,Direction.FARRIGHT),
//        entry(VillagerProfession.LIBRARIAN,Direction.FARLEFT),
//        entry(VillagerProfession.MASON,Direction.MODERATE)
//);