package helpers;

import java.util.Random;

/**
 * Created by gogen on 29.04.15.
 */
public class Helpers {

    private static Random rand = new Random();

    public static double getMaxForAllX(double[] listOfCos, int ring, double previousDelta, int[] set){
        double resultingDelta = 0;
        for(int x = 1; x <= ring/2  + 1 + ring % 2; x++){
            float currentDelta = Math.abs(partialSum(listOfCos, ring, set, x));
            if(currentDelta > previousDelta){
                resultingDelta = previousDelta;
                break;
            }
            resultingDelta = Math.max(resultingDelta, currentDelta);
        }
        return resultingDelta;
    }

    public static Random getRandomizer(){
        return rand;
    }

    public static String showArray(Integer[] arr){
        String s = "[" + arr[0];
        for(int i = 1; i < arr.length; i++){
            s += "," + arr[i];
        }
        s += "]";
        return s;
    }

    private static float partialSum(double[] listOfCos, int ring, int[] set, int x){
        float sum = 0;
        for(int s : set){
            sum += listOfCos[x*s % ring];
        }
        return sum / set.length;
    }

    public static int getLowerBound(int ring, double delta){
        return (int)Math.round(Math.log(ring) / Math.log(2));
    }
    public static int getHightBound(int ring, double delta){
        return Math.min(ring, (int)Math.round(2 * Math.log(2 * ring) / Math.log(2) / delta / delta));
    }

    public static double[] calcListOfCos(int ring){
        double[] list = new double[ring];
        for(int i = 0; i < list.length; i++){
            list[i] = Math.cos(2 * Math.PI * i / ring);
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
