package djh.vilid.pol;

import java.util.Random;

public enum CoreValue {
    TRADITIONALIST,
    REFORMER,

    BUSINESS_FOCUSED,
    PRO_WORKER,

    SJW,
    BIGOT,

    NATIONALIST,
    GLOBALIST,

    WEAPONS_ENTHUSIAST,
    SAFETY_ADVOCATE;

    private static final CoreValue[] vals = values();
//
//    public static Direction getDirection(CoreValue coreValue){
//        switch(coreValue){
//            case WEAPONS_ENTHUSIAST -> {return Direction.RIGHT;}
//            case SAFETY_ADVOCATE -> {return Direction.LEFT;}
//            case REFORMER -> {return Direction.LEFT;}
//            case TRADITIONALIST -> {return Direction.RIGHT;}
//            case GLOBALIST -> {return Direction.LEFT;}
//            case NATIONALIST -> {return Direction.RIGHT;}
//            case PRO_WORKER -> {return Direction.LEFT;}
//            case BIGOT -> {return Direction.RIGHT;}
//            case BUSINESS_FOCUSED -> {return Direction.RIGHT;}
//            case SJW -> {return Direction.LEFT;}
//            default -> {return Direction.MODERATE;}
//        }
//    }
//
//    public Direction getDirection(){
//        return getDirection(this);
//    }

    public static CoreValue randomize(){
        return randomize(new Random());
    }

    public static CoreValue randomize(Random random){
        return vals[random.nextInt(vals.length)];
    }
}
