package generators;

/**
 * Created by gogen on 29.04.15.
 */
public abstract class AbstractFactory implements Factory {
    int ring;
    int deep;
    int[] currSet;

    public AbstractFactory(int ring){
        this.ring = ring;
    }

    @Override
    public void changeDeep(int deep) {
        this.deep = deep;
        currSet = new int[deep];
    }

    public int getDeep(){
        return deep;
    }
}
