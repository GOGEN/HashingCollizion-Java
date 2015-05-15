package generators;

import helpers.Helpers;
import helpers.Tuple;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by gogen on 30.04.15.
 */
public class Random extends AbstractFactory {
    private final String TYPE = "random";

    public BigInteger setCount;
    public static final BigInteger maxCount = new BigInteger("200000");

    public Random(int ring, double delta) {
        super(ring, delta);
        setCount = BigInteger.valueOf(deep).multiply(BigInteger.valueOf(ring)).min(maxCount);
    }

    @Override
    public Tuple<Integer[], Double> getBestSet(double previousDelta) {
        int[] set = null;
        double currDelta = previousDelta;
        int[] resultSet = null;
        double resultDelta = 1.0;
        while ((set = nextSet()) != null) {
            currDelta = Helpers.getMaxForAllXOrOne(listOfCos, ring, currDelta, set);
            if (currDelta < resultDelta) {
                resultDelta = currDelta;
                resultSet = set.clone();
            }
        }
        if(resultSet == null)
            return null;
        return new Tuple<Integer[], Double>(ArrayUtils.toObject(resultSet), resultDelta);
    }

    @Override
    public Tuple<Integer[], Double> getSetForDelta() {
        int[] set = null;
        double delta = 1.0;
        while ((set = nextSet()) != null && delta >= stoppingCriteria)
            if ((delta = Helpers.getMaxForAllXOrOne(listOfCos, ring, stoppingCriteria, set)) < stoppingCriteria)
                    return new Tuple(ArrayUtils.toObject(set), delta);
        return null;
    }

    @Override
    public void changeDeep(int deep){
        super.changeDeep(deep);
        setCount = BigInteger.valueOf(deep).multiply(BigInteger.valueOf(ring)).min(maxCount);;
    }

    public void nextDeep(){
        super.nextDeep();
        setCount = BigInteger.valueOf(deep).multiply(BigInteger.valueOf(ring)).min(maxCount);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public int[] nextSet() {
        if(setCount.equals(BigInteger.ZERO))
            return null;
        setCount = setCount.subtract(BigInteger.ONE);
        return Helpers.calcRandomSet(actualRing, deep);
    }

}
