package generators;

import helpers.Helpers;

/**
 * Created by gogen on 29.04.15.
 */
abstract class AbstractFactory implements Factory {
    int ring;
    int actualRing;
    int deep;
    double[] listOfCos;
    double stoppingCriteria;


    public AbstractFactory(int ring, double delta){
        this.ring = ring;
        actualRing = ring/2;
        deep = 1;
        listOfCos = Helpers.calcListOfCos(ring);
        stoppingCriteria = delta;
    }

    public void nextDeep(){
        deep *= 2;
    }

    public void changeDeep(int deep){
        this.deep = deep;
    }

    public int getDeep(){
        return deep;
    }

    public int getRing(){ return ring; }

    public double getStoppingCriteria(){ return stoppingCriteria; }

}
