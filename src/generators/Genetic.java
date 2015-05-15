package generators;

import com.google.common.collect.MinMaxPriorityQueue;
import helpers.Helpers;
import helpers.Tuple;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.System;
import java.util.*;

/**
 * Created by gogen on 30.04.15.
 */
public class Genetic extends AbstractFactory {
    private static final String TYPE= "genetic";
    Population population;
    int ring2;

    public Genetic(int ring, double delta) {
        super(ring, delta);
        population = new Population();
        ring2 = ring/2;
    }

    @Override
    public void changeDeep(int deep) {
        super.changeDeep(deep);
        population = new Population();
    }

    @Override
    public void nextDeep(){
        super.nextDeep();
        population = new Population();
    }

    @Override
    public Tuple<Integer[], Double> getBestSet(double delta) {
        Population.Chromosome chr = population.runGA();
        if(chr.fitness >= delta)
            return null;
        return new <Integer[], Double>Tuple(ArrayUtils.toObject(chr.genes), chr.fitness);
    }

    @Override
    public Tuple<Integer[], Double> getSetForDelta() {
        Population.Chromosome chr = population.runGA();
        if(chr.fitness >= stoppingCriteria)
            return null;
        return new <Integer[], Double>Tuple(ArrayUtils.toObject(chr.genes), chr.fitness);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    class Population {
        private MinMaxPriorityQueue<Chromosome> population;
        private int populationSize;
        private int generationIndex;
        private int generationsMaxCount = 100;
        private double mutationProbability = 0.25;
        private double genomeMutationProbability = 0.1;
        private double generationParamForMutate;


        class Chromosome implements Comparable{
            int[] genes;
            double fitness;

            public Chromosome(){
                genes = new int[deep];
                fitness = 1.0;
            }

            public Chromosome(int[] genes){
                this.genes = genes;
                Arrays.sort(this.genes);
                fitness = 1.0;
            }

            public Chromosome calcFitness(){
                fitness = Helpers.getMaxForAllX(listOfCos, ring, 1.0, genes);
                return this;
            }

            public Chromosome mutation(){
                //System.out.println(param);
                for(int i = 0; i< genes.length; i++)
                    if(Helpers.getRandomizer().nextDouble() < genomeMutationProbability)
                        if (Helpers.getRandomizer().nextInt(2) == 0)
                            genes[i] = genes[i] + (int) Math.ceil((ring2 - genes[i]) *
                                    (1.0 - Math.pow(Helpers.getRandomizer().nextDouble(), generationParamForMutate)));
                        else
                            genes[i] = genes[i] - (int) Math.ceil(genes[i] *
                                    (1.0 - Math.pow(Helpers.getRandomizer().nextDouble(), generationParamForMutate)));
                fitness = 1.0;
                Arrays.sort(genes);
                return this;
            }

            public Chromosome[] crossover(Chromosome chr){
                int snd = Helpers.getRandomizer().nextInt(genes.length - 1) + 1;
                int fst = Helpers.getRandomizer().nextInt(snd);
                int[] fGenes
                        = new int[deep];
                int[] sGenes = new int[deep];
                for(int i = 0; i < fst; i++){
                    fGenes[i] = genes[i];
                    sGenes[i] = chr.genes[i];
                }
                for(int i = fst; i < snd; i++){
                    fGenes[i] = chr.genes[i];
                    sGenes[i] = genes[i];
                }
                for(int i = snd; i < genes.length; i++){
                    fGenes[i] = genes[i];
                    sGenes[i] = chr.genes[i];
                }
                return new Chromosome[]{new Chromosome(fGenes), new Chromosome(sGenes)};
            }

            public double fitness(){
                return fitness;
            }

            @Override
            public int compareTo(Object o) {
                Chromosome chr = (Chromosome) o;
                return (fitness - chr.fitness < 0) ? -1 : 1;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Chromosome that = (Chromosome) o;

                if (!Arrays.equals(genes, that.genes)) return false;

                return true;
            }
        }

        public Population(){
            populationSize = Math.min(deep, 150);
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
                Chromosome chr = new Chromosome(Helpers.calcRandomSet(actualRing, deep));
                if(hasChromosome(chr))
                    continue;
                population.add(chr.calcFitness());
                copyPopulationCount--;
            }
        }

        private boolean hasChromosome(Chromosome chr){
            Iterator<Chromosome> itr = population.iterator();
            while (itr.hasNext()) {
                Chromosome compChr = itr.next();
                if (compChr.equals(chr))
                    return true;
            }
            return false;
        }

        private void nextGenetrtion(){
            Chromosome[] pop = new Chromosome[populationSize];
            population.toArray(pop);
            for(int i = 0; i < populationSize - 1; i++)
                for(int j = i+1; j < populationSize; j++)
                    for (Chromosome c : pop[i].crossover(pop[j])) {
                        c = mutate(c);
                        if (!hasChromosome(c))
                            population.add(c.calcFitness());
                    }
        }

        private Chromosome runGA(){
            generationParamForMutate = Math.pow(1.0 - generationIndex / generationsMaxCount, 2);
            initGeneration();
            Chromosome chr = population.peek();
            while(chr.fitness() > stoppingCriteria && generationIndex < generationsMaxCount){
                nextGenetrtion();
                chr = population.peek();
                System.out.println(generationIndex + " generation, " + chr.fitness + " fitness");
                generationIndex++;
                generationParamForMutate = Math.pow(1.0 - generationIndex / generationsMaxCount, 2);
            }
            return chr;

        }

        private Chromosome mutate(Chromosome chr){
            if(Helpers.getRandomizer().nextDouble() < mutationProbability){
                return chr.mutation();
            }
            return chr;
        }

    }

}