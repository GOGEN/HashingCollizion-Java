package generators;

import helpers.Helpers;
import helpers.Tuple;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by gogen on 29.04.15.
 */
public class Bruteforse extends AbstractFactory {
    private final String TYPE = "bruteforse";

    public Bruteforse(int ring, double delta) {
        super(ring, delta);
    }

    @Override
    public Tuple<Integer[], Double> getBestSet(double previousDelta) {
        int[] set = new int[deep];
        int[] resultSet = null;
        double currDelta = Helpers.getMaxForAllX(listOfCos, ring, previousDelta, set);
        while ((set = nextSet(set)) != null && currDelta > stoppingCriteria) {
            currDelta = Helpers.getMaxForAllX(listOfCos, ring, previousDelta, set);
            if (currDelta < previousDelta) {
                previousDelta = currDelta;
                resultSet = set.clone();
            }
        }
        if(resultSet == null){
            return null;
        }
        return new <Integer[], Double>Tuple(ArrayUtils.toObject(resultSet), previousDelta);
    }

    @Override
    public void changeDeep(int deep){
        super.changeDeep(deep);
    }
    
    @Override
    public String getType() {
        return TYPE;
    }

    private int[] nextSet(int[] set) {
        int index = deep - 1;
        int bound = ring/2 + ring % 2 + 1;
        set[index] = (set[index] + 1) % bound;
        while(set[index] == 0 && index > 0){
            index --;
            set[index] = (set[index] + 1) % bound;
            for(int i = index+1; i < deep;i++){
                set[i] = set[index];
            }
        }
        if(index == 0 && set[index] == 0){
            return null;
        }else{
            return set;
        }

    }

}
