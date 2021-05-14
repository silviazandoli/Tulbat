package it.unibo.ai.didattica.competition.tablut.tulbat.heuristics;

import it.unibo.ai.didattica.competition.tablut.tulbat.heuristics.*;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.*;

public class BlackHeuristicsMonteCarlo extends MonteCarloHeuristics {

    private final String RHOMBUS_POSITIONS = "rhombusPositions";
    private final String WHITE_EATEN = "numberOfWhiteEaten";
    private final String BLACK_ALIVE = "numberOfBlackAlive";
    private final String BLACK_SURROUND_KING = "blackSurroundKing";

    //Threshold used to decide whether to use rhombus configuration
    private final int THRESHOLD = 10;
    //Number of tiles on rhombus
    private final int NUM_TILES_ON_RHOMBUS = 8;

    private final Map<String,Double> weights;
    private String[] keys;

    //Flag to enable console print
    private boolean flag = false;

    //Matrix of favourite black positions in initial stages and to block the escape ways
    private final int[][] rhombus = {
            {1,2},       {1,6},
            {2,1},                   {2,7},

            {6,1},                   {6,7},
            {7,2},       {7,6}
    };

    private double numberOfBlack;
    private double numberOfWhiteEaten;

    public BlackHeuristicsMonteCarlo(State state) {

        super(state);
        //Initializing weights
        weights = new HashMap<String, Double>();
        weights.put(BLACK_ALIVE, 35.0);
        weights.put(WHITE_EATEN, 48.0);
        weights.put(BLACK_SURROUND_KING, 15.0);
        weights.put(RHOMBUS_POSITIONS, 2.0);

        //Extraction of keys
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }

    /**
     *
     * @return the evaluation of the states using a weighted sum
     */
    @Override
    public double evaluateState() {

        double utilityValue = 0.0;

        //Atomic functions to combine to get utility value through the weighted sum
        numberOfBlack = (double) blackPieces / GameAshtonTablut.NUM_BLACK;
        numberOfWhiteEaten = (double) (GameAshtonTablut.NUM_WHITE - whitePieces) / GameAshtonTablut.NUM_WHITE;
        double  pawnsNearKing = super.blackAroundKing + super.whiteAroundKing;
        double numberOfPawnsOnRhombus = (double) blackInBestPositions / NUM_TILES_ON_RHOMBUS;

        if(flag){
            System.out.println("Number of rhombus: " + numberOfPawnsOnRhombus);
            System.out.println("Number of pawns near to the king:" + pawnsNearKing);
            System.out.println("Number of white pawns eaten: " + numberOfWhiteEaten);
            System.out.println("Black pawns: " + numberOfBlack);
        }


        //Weighted sum of functions to get final utility value
        Map<String,Double> atomicUtilities = new HashMap<String,Double>();
        atomicUtilities.put(BLACK_ALIVE,numberOfBlack);
        atomicUtilities.put(WHITE_EATEN, numberOfWhiteEaten);
        atomicUtilities.put(BLACK_SURROUND_KING,pawnsNearKing);
        atomicUtilities.put(RHOMBUS_POSITIONS,numberOfPawnsOnRhombus);

        for (int i = 0; i < weights.size(); i++){
            utilityValue += weights.get(keys[i]) * atomicUtilities.get(keys[i]);
            if(flag) {
                System.out.println(keys[i] + ": " +
                        weights.get(keys[i]) + "*" +
                        atomicUtilities.get(keys[i]) +
                        "= " + weights.get(keys[i]) * atomicUtilities.get(keys[i]));
            }
        }
        System.out.println(utilityValue);
        return utilityValue;

    }

    /**
     *
     * @return number of black pawns on tiles if condition is true, 0 otherwise
     */
    public int getNumberOnRhombus(){

        if (blackPieces >= THRESHOLD) {
            return getValuesOnRhombus();
        }else{
            return 0;
        }
    }

    /**
     *
     * @return number of black pawns on rhombus configuration
     */
    public int getValuesOnRhombus(){

        int count = 0;
        for (int[] position : rhombus) {
            if (state.getPawn(position[0], position[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++;
            }
        }
        return count;

    }

}
