package generators;

import helpers.Tuple;

/**
 * Created by gogen on 29.04.15.
 */
public interface Factory {
    public static Factory initBruteforse(int ring, double delta){
        return new Bruteforse(ring, delta);
    }

    public static Factory initGenetic(int ring, double delta){
        return new Genetic(ring, delta);
    }

    public static Factory initRandom(int ring, double delta){
        return new Random(ring, delta);
    }

    public void nextDeep();

    public Tuple<Integer[], Double> getBestSet(double delta);

    public void changeDeep(int deep);

    public int getDeep();

    public String getType();

}
