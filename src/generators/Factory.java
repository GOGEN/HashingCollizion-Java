package generators;

/**
 * Created by gogen on 29.04.15.
 */
public interface Factory {
    public static Factory initBruteforse(int ring){
        return new Bruteforse(ring);
    }

    public int[] nextSet();

    public void changeDeep(int deep);

    public int getDeep();

}
