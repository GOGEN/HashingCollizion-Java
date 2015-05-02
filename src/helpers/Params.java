package helpers;

/**
 * Created by gogen on 01.05.15.
 */
public class Params {
    public double[] getListOfCos() {
        return listOfCos;
    }

    public int getRing() {
        return ring;
    }

    double[] listOfCos;
    int ring;

    public Params(int ring){
        this.ring = ring;
        listOfCos = Helpers.calcListOfCos(ring);
    }
}