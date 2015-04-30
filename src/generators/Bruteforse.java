package generators;

import helpers.Helpers;

/**
 * Created by gogen on 29.04.15.
 */
public class Bruteforse extends AbstractFactory {

    public Bruteforse(int ring) {
        super(ring);
        changeDeep(1);
    }

    @Override
    public String toString() {
        return "Bruteforse{" +
                "deep=" + deep +
                ", ring=" + ring +
                '}';
    }

    @Override
    public int[] nextSet() {
        int index = deep - 1;
        int bound = ring/2 + ring % 2 + 1;
        currSet[index] = (currSet[index] + 1) % bound;
        while(currSet[index] == 0 && index > 0){
            index --;
            currSet[index] = (currSet[index] + 1) % bound;
            for(int i = index+1; i < deep;i++){
                currSet[i] = currSet[index];
            }
        }
        if(index == 0 && currSet[index] == 0){
            changeDeep(deep + 1);
            return null;
        }else{
            return currSet;
        }

    }

    public int[] getCurrSet(){
        return currSet;
    }

}
