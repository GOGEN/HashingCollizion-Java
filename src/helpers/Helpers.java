package helpers;

import java.util.Random;

/**
 * Created by gogen on 29.04.15.
 */
public class Helpers {

    private static Random rand = new Random();

    public static double getMaxForAllX(double[] listOfCos, int ring, double previousDelta, int[] set){
        double resultingDelta = 0;
        for(int x = 1; x <= ring/2; x++){
            float currentDelta = Math.abs(partialMulti(listOfCos, ring, set, x));
            if(currentDelta > previousDelta){
                resultingDelta = previousDelta;
                break;
            }
            resultingDelta = Math.max(resultingDelta, currentDelta);
        }
        return resultingDelta;
    }

    public static double getMaxForAllXOrOne(double[] listOfCos, int ring, double previousDelta, int[] set){
        double resultingDelta = 0;
        for(int x = 1; x <= ring/2; x++){
            float currentDelta = Math.abs(partialMulti(listOfCos, ring, set, x));
            if(currentDelta > previousDelta){
                resultingDelta = 1.0;
                break;
            }
            resultingDelta = Math.max(resultingDelta, currentDelta);
        }
        return resultingDelta;
    }

    public static Random getRandomizer(){
        return rand;
    }

    private static float partialSum(double[] listOfCos, int ring, int[] set, int x){
        float sum = 0;
        for(int s : set){
            sum += listOfCos[x*s % ring];
        }
        return sum / set.length;
    }

    private static float partialMulti(double[] listOfCos, int ring, int[] set, int x) {
        float mult = 1;
        for (int s : set) {
            mult *= listOfCos[x * s % ring];
        }
        return mult;
    }

    public static int getLowerBound()
    {
        return 2;
    }
    public static int getHightBound(int ring){
        return ring/2;
    }

    public static double[] calcListOfCos(int ring){
        double[] list = new double[ring];
        for(int i = 0; i < list.length; i++){
            list[i] = Math.cos(Math.PI * i / ring);
        }
        return list;
    }

    public static int[] calcRandomSet(int ring, int deep){
        int[] set = new int[deep];
        for(int i = 0; i < deep; i++){
            set[i] = rand.nextInt(ring);
        }
        return set;
    }
}
