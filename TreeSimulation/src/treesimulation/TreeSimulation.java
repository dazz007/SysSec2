/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treesimulation;

import visidia.simulation.process.algorithm.SynchronousAlgorithm;
import visidia.simulation.process.messages.IntegerMessage;

/**
 *
 * @author Pawel
 */
public class TreeSimulation extends SynchronousAlgorithm {

    
    
    private static final int colorsCount = 6;
    
    private static final String[] colorsVisidia = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Z", "Y", "X", "W", "V",
			"U", "T", "S", "R", "Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G",  "F", "E", "D", "C", "B", "A"};
    private int nrOfBits = 16;
    private static int defaultColorCount = 0;
    private static final int rounds = 6;
    private static final int root = 0;
    
    @Override
    public Object clone() {
        return new TreeSimulation();
    }

    @Override
    public void init() {
        
        int neighborsCount = getArity();
        putProperty("label", colorsVisidia[getId() % colorsVisidia.length]);
        nextPulse();
//        nrOfBits = (int) (Math.log(getNetSize())/Math.log(2));
        sixToThree();
    }

    private void sixToThree() {
        int neighborsCount = getArity();
        int currentColor;
        sixColor(rounds);

        for (int i = 5; i > 2; --i) {
            int childrensColor = shiftDown();
            nextPulse();

            currentColor = getColor();
            if (getId() == root) {
                for (int j = 0; j < neighborsCount; ++j) {
                    sendTo(j, new IntegerMessage(currentColor));
                }
            } else {
                for (int j = 1; j < neighborsCount; ++j) {
                    sendTo(j, new IntegerMessage(currentColor));
                }

                IntegerMessage parentMessage = (IntegerMessage) receiveFrom(0);
                if (currentColor == i) {
                    currentColor = firstFree(parentMessage.value(), childrensColor);
                    putProperty("label", colorsVisidia[currentColor % colorsVisidia.length]);
                }
            }

            nextPulse();
        }
    }

    private void sixColor(int roundCount) {
        while (roundCount > 0) {
            
            int neighborsCount = getArity();
            int currentColor = getColor();

            if (getId() == root) {
                putProperty("label", colorsVisidia[0 % colorsVisidia.length]);

                for (int i = 0; i < neighborsCount; ++i) {
                    sendTo(i, new IntegerMessage(currentColor));
                }
            } else {
                for (int i = 1; i < neighborsCount; ++i) {
                    sendTo(i, new IntegerMessage(currentColor));
                }

                IntegerMessage parentMessage = (IntegerMessage) receiveFrom(0);
                if (currentColor > colorsCount - 1) {
                    String bitStringOfParentMessage = String.format("%" + nrOfBits + "s", Integer.toBinaryString(parentMessage.value())).replace(' ', '0');
                    String bitStringOfCurrentNode = String.format("%" + nrOfBits + "s", Integer.toBinaryString(currentColor)).replace(' ', '0');

                    int differOn = -1;
                    for (int i = bitStringOfCurrentNode.length() - 1; i >= 0; --i) {
                        if (bitStringOfCurrentNode.charAt(i) != bitStringOfParentMessage.charAt(i)) {
                            differOn = nrOfBits - 1 - i;
                            break;
                        }
                    }

                    String bitStringOfNewColor = Integer.toBinaryString(differOn) + bitStringOfCurrentNode.charAt(nrOfBits - 1 - differOn);
                    putProperty("label", colorsVisidia[Integer.parseInt(bitStringOfNewColor, 2) % colorsVisidia.length]);
                }
            }

            nextPulse();
            roundCount--;
        }
    }

    private int shiftDown() {
        int neighborsCount = getArity();
        int currentColor = getColor();
        int[] reqColors = {0, 1, 2};
        if (getId() == root) {
            int returnColor = currentColor;

            for (int i = 0; i < neighborsCount; ++i) {
                sendTo(i, new IntegerMessage(currentColor));
            }

            
            for (int i = 0; i < reqColors.length; ++i) {
                if (currentColor != reqColors[i]) {
                    currentColor = reqColors[i];
                    break;
                }
            }

            putProperty("label", colorsVisidia[currentColor % colorsVisidia.length]);

            return returnColor;
        } else {
            for (int i = 1; i < neighborsCount; ++i) {
                sendTo(i, new IntegerMessage(currentColor));
            }

            IntegerMessage parentMessage = (IntegerMessage) receiveFrom(0);
            putProperty("label", colorsVisidia[parentMessage.value() % colorsVisidia.length]);

            return currentColor;
        }
    }

    private int firstFree(int parentColor, int childColor) {
        int[] reqColors = {0, 1, 2};

        for (int i = 0; i < reqColors.length; ++i) {
            if (reqColors[i] != parentColor && reqColors[i] != childColor) {
                return reqColors[i];
            }
        }

        return reqColors[0];
    }

    private int getColor() {
        String currentColor = getProperty("label").toString();

        for (int i = 0; i < colorsVisidia.length; ++i) {
            if (colorsVisidia[i] == currentColor) {
                return i;
            }
        }

        return -1;
    }

}
