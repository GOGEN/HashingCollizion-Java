package generators;

import helpers.Helpers;
import helpers.Tuple;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by gogen on 30.04.15.
 */
public class Random extends AbstractFactory {
    private final String TYPE = "random";

    public int setCount;

    public Random(int ring, double delta) {
        super(ring, delta);
        setCount = (int)Math.pow(2, deep) * ring;
        System.out.println("Set count: " + setCount);
    }

    @Override
    public Tuple<Integer[], Double> getBestSet(double previousDelta) {
        int[] set = Helpers.calcRandomSet(ring, deep);
        int[] resultSet = null;
        double currDelta = Helpers.getMaxForAllX(listOfCos, ring, previousDelta, set);
        while ((set = nextSet()) != null && currDelta > stoppingCriteria) {
            currDelta = Helpers.getMaxForAllX(listOfCos, ring, previousDelta, set);
            if (currDelta < previousDelta) {
                previousDelta = currDelta;
                resultSet = set.clone();
            }
        }
        if(resultSet == null){
            return null;
        }
        return new Tuple<Integer[], Double>(ArrayUtils.toObject(resultSet), previousDelta);
    }

    @Override
    public void changeDeep(int deep){
        super.changeDeep(deep);
        setCount = (int)Math.pow(2, deep) * ring;
        System.out.println("Set count: " + setCount);
    }

    public void nextDeep(){
        changeDeep(deep + 1);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public int[] nextSet() {
        if(setCount == 0){
            return null;
        }
        setCount --;
        return Helpers.calcRandomSet(ring/2, deep);
    }

}
