package generators;

import com.google.common.collect.MinMaxPriorityQueue;
import helpers.Helpers;
import helpers.Tuple;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Comparator;

/**
 * Created by gogen on 30.04.15.
 */
public class Genetic extends AbstractFactory {
    private static final String TYPE= "genetic";
    Population population;

    public Genetic(int ring, double delta) {
        super(ring, delta);
        population = new Population();
    }

    @Override
    public void nextDeep() {
        changeDeep(deep+1);
    }

    @Override
    public void changeDeep(int deep) {
        super.changeDeep(deep);
        population = new Population();
    }

    @Override
    public Tuple<Integer[], Double> getBetterSet(double delta) {
        Chromosome chr = population.runGA();
        if(chr.fitness >= delta)
            return null;
        return new <Integer[], Double>Tuple(ArrayUtils.toObject(chr.genes), chr.fitness);
    }

    @Override
    public Tuple<Integer[], Double> getSetForDelta() {
        Chromosome chr = population.runGA();
        if(chr.fitness >= stoppingCriteria)
            return null;
        return new <Integer[], Double>Tuple(ArrayUtils.toObject(chr.genes), chr.fitness);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    class Chromosome implements Comparable{
        int[] genes;
        double fitness;

        public Chromosome(int[] genes){
            this.genes = genes;
            fitness = 1.0;
        }

        public Chromosome calcFitness(){
            fitness = Helpers.getMaxForAllX(listOfCos, ring, 1.0, genes);
            return this;
        }

        public Chromosome mutation(double genomMutateProbability){
            for(int i = 0; i< genes.length; i++){
                if(Helpers.getRandomizer().nextDouble() < genomMutateProbability){
                    genes[i] = (genes[i] + 1 - Helpers.getRandomizer().nextInt(3) + ring/2) % (ring/2);
                }
            }
            fitness = 1.0;
            return this;
        }

        public Chromosome crossover(Chromosome chr){
            int idx = Helpers.getRandomizer().nextInt(genes.length);
            int[] newGenes = new int[genes.length];
            for(int i = 0; i < idx; i++){
                newGenes[i] = genes[i];
            }
            for(int i = idx; i < genes.length; i++){
                newGenes[i] = chr.genes[i];
            }
            return new Chromosome(newGenes);
        }

        public double fitness(){
            return fitness;
        }


        @Override
        public int compareTo(Object o) {
            Chromosome chr = (Chromosome) o;
            return (fitness - chr.fitness < 0) ? -1 : 1;
        }
    }

    class Population {
        private MinMaxPriorityQueue<Chromosome> population;
        private int populationSize;
        private int generationIndex;
        private int generationsMaxCount = 100;
        private double mutationProbability = 0.25;
        private double genomeMutationProbability = 0.1;

        public Population(){
            populationSize = deep;
            population = MinMaxPriorityQueue.orderedBy(new Comparator<Chromosome>() {
                @Override
                public int compare(Chromosome o1, Chromosome o2) {
                    return o1.compareTo(o2);
                }
            }).maximumSize(populationSize).create();
        }

        private void initGeneration(){
            generationIndex = 1;
            int copyPopulationCount = populationSize;
            while(copyPopulationCount > 0){
                population.add((new Chromosome(Helpers.calcRandomSet(ring, deep))).calcFitness());
                copyPopulationCount--;
            }

        }

        private void nextGenetrtion(){
            Chromosome[] pop = new Chromosome[populationSize];
            population.toArray(pop);
            population.clear();
            for(int i = 0; i < populationSize; i++){
                for(int j = i+1; j < populationSize; j++){
                    population.add(mutate(pop[i].crossover(pop[j])).calcFitness());
                }
            }
        }

        private Chromosome runGA(){
            initGeneration();
            Chromosome chr = population.peek();
            while(chr.fitness() > stoppingCriteria && generationIndex < generationsMaxCount){
                nextGenetrtion();
                chr = population.peek();
                System.out.println(generationIndex + " generation, " + chr.fitness + " fitness");
                generationIndex++;
            }
            return chr;

        }

        private Chromosome mutate(Chromosome chr){
            if(Helpers.getRandomizer().nextDouble() < 1.0){
                return chr.mutation(genomeMutationProbability);
            }
            return chr;
        }

    }

}
