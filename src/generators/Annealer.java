package generators;

import helpers.Helpers;
import helpers.Tuple;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * Created by gogen on 07.05.15.
 */
public class Annealer extends AbstractFactory {
    private static final String type = "annealer";

    public Annealer(int ring, double delta) {
        super(ring, delta);
    }

    @Override
    public Tuple<Integer[], Double> getBestSet(double delta) {
        return null;
    }

    @Override
    public Tuple<Integer[], Double> getSetForDelta() {
        Simulated sim = new Simulated((int)Math.pow(deep,2));
        Tuple<Integer[], Double> result = sim.annealer();
        if(result.second > stoppingCriteria)
            return null;
        return result;
    }

    @Override
    public String getType() {
        return type;
    }

    private class Simulated{
        double maxTemp = 1.0;
        double minTemp = 0.0000001;

        public Simulated(double maxTemp){
            this.maxTemp = maxTemp;
        }

        private class Set{
            private int[] values;
            private double energy;

            int[] getValues() {
                return values;
            }

            double getEnergy() {
                return energy;
            }

            Set(int[] values) {
                this.values = values;
                this.energy = Helpers.getMaxForAllX(listOfCos, ring, 1.0, values);
            }

            Set pertubation(){
                int[] newValues = new int[values.length];
                int ring2= ring/2;
                for(int i = 0; i< values.length; i++){
                    newValues[i] = values[i];
                    if(Helpers.getRandomizer().nextDouble() < 0.25)
                        newValues[i] = (newValues[i] + ring2/2 -
                                Helpers.getRandomizer().nextInt(ring2) + ring2) % (ring2);
                }
                return new Set(newValues);
            }

            void print(){
                System.out.println("Delta: " + energy + ", Set: " + Arrays.toString(values));
            }

        }

        private double decreaseTemperature(double temp, int iterationIndex){
            return maxTemp / iterationIndex;
        }

        public Tuple<Integer[], Double> annealer(){
            double currTemp = maxTemp;
            Set set = new Set(Helpers.calcRandomSet(ring, deep));
            set.print();
            Set nextSet;
            int itterationIndex = 1;
            while(currTemp > minTemp && set.getEnergy() > stoppingCriteria){
                nextSet = set.pertubation();
                System.out.print("NEXT: ");
                nextSet.print();
                double energyDifference = nextSet.getEnergy() - set.getEnergy();
                if( energyDifference <= 0){
                    set = nextSet;
                }else{
                    System.out.println("P: " + Math.exp(-energyDifference/currTemp));
                    if(Helpers.getRandomizer().nextDouble() < Math.exp(-energyDifference/currTemp)){
                        set = nextSet;
                    }
                }
                set.print();
                currTemp = decreaseTemperature(currTemp, itterationIndex);
                itterationIndex++;
            }
            return new Tuple(ArrayUtils.toObject(set.getValues()), set.getEnergy());

        }
    }
}
