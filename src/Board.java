import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;

public class Board {
  private int blocks[][];
  private Board[] neighbors;
  private static boolean DEBUG = false;
  
//construct a board from an n-by-n array of blocks
//(where blocks[i][j] = block in row i, column j)  
  public Board(int[][] inputBlocks) {
    blocks = copyBlocks(inputBlocks);
    neighbors = null;
  }
      
    
 // board dimension n
    public int dimension() {
      return blocks.length;
    }
    
 // number of blocks out of place
    public int hamming() {
      int hammingNo = 0;
      int goalValue = 0;
      
      for (int i = 0; i < dimension(); i++) {
        for (int j = 0; j < dimension(); j++) {
          goalValue = i * dimension() + j + 1;
          if (blocks[i][j] != goalValue && blocks[i][j] != 0) {
            hammingNo++;
          }
        }
      }
      return hammingNo;
    }
    
 // sum of Manhattan distances between blocks and goal
    public int manhattan() {
      int manhattanNo = 0;
      int goalValue = 0;
      int actualValue = 0;
      
      for (int i = 0; i < dimension(); i++) {
        for (int j = 0; j < dimension(); j++) {
          goalValue = i * dimension() + j + 1;
          actualValue = blocks[i][j];
          int iGoal = (actualValue - 1) / dimension();
          int jGoal = (actualValue - 1) % dimension();
          
          if (actualValue != goalValue && actualValue != 0) {
            debug("Block["+ i + "][" + j + "]:");
            debug("Value = " + actualValue);
            debug("Goal: Block[" + iGoal + "][" + jGoal + "] = " + actualValue);
            manhattanNo += Math.abs(i - iGoal) + Math.abs(j - jGoal);
            debug("Manhattan number is now: " + manhattanNo);
          }
        }
      }
      return manhattanNo;
    }
    
 // is this board the goal board?
    public boolean isGoal() {
      return hamming() == 0;
    }
    
 // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
      int[][] twinBlocks = copyBlocks(blocks);
      
      if (blocks[0][0] == 0 || blocks[0][1] == 0) {
        swapBlocks(twinBlocks, 1, 0, 1, 1);
      } else {
        swapBlocks(twinBlocks, 0, 0, 0, 1);
      }
      return new Board(twinBlocks);
    }
    
    // exchanges values of two blocks
    private void swapBlocks(int[][] blocks, int iFirst, int jFirst, int iSecond, int jSecond) {
      int firstVal = blocks[iFirst][jFirst];
      blocks[iFirst][jFirst] = blocks[iSecond][jSecond];
      blocks[iSecond][jSecond] = firstVal;
    }
    
    // copy 2d array of integers
    private int[][] copyBlocks(int[][] blockArray) {
      int[][] localCopy = new int[blockArray.length][];
      for (int i = 0; i < blockArray.length; i++) {
        localCopy[i] = blockArray[i].clone();
      }
      return localCopy;
    }
    
    /**
     * Compares this Board to the specified object.
     *
     * @param  y the other Board
     * @return true if this Board is equal to {@code y}; false otherwise
     */
    @Override
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        
        Board that = (Board) y;
        
        if (blocks.length != that.blocks.length) return false;
        for (int i = 0; i < blocks.length; i++) {
          if (blocks[i].length != that.blocks[i].length) return false;
          for (int j = 0; j < blocks[i].length; j++) {
            if (blocks[i][j] != that.blocks[i][j]) return false;
          }
        }        
        return true;
    }
    
 // all neighboring boards
    public Iterable<Board> neighbors() {
      return new Iterable<Board>() {
        @Override
        public Iterator<Board> iterator() {
            if (neighbors == null) {
                findNeighbors();
            }
            return new BoardIterator();
        }
      };
    }
     
    private class BoardIterator implements Iterator<Board> {

      private int index = 0;

      @Override
      public boolean hasNext() {
          return index < neighbors.length;
      }

      @Override
      public Board next() {
          if (hasNext()) {
              return neighbors[index++];
          } else {
              throw new NoSuchElementException("There is no next neighbor.");
          }
      }

      @Override
      public void remove() {
          throw new UnsupportedOperationException("Removal of neighbors not supported.");
      }
    }
    
    private void findNeighbors() {
      List<Board> neighborList = new ArrayList<>();
      
      int i = 0;
      int j = 0;
      while (blocks[i][j] != 0) {
        if (j < dimension() - 1) {
          j++;
        } else {
          j = 0;
          i++;
        }
      }
      
      if (i > 0) {
        int[][] blockCopy = copyBlocks(blocks);
        swapBlocks(blockCopy, i, j, i - 1, j);
        neighborList.add(new Board(blockCopy));
      }
      if (i < dimension() - 1) {
        int[][] blockCopy = copyBlocks(blocks);
        swapBlocks(blockCopy, i, j, i + 1, j);
        neighborList.add(new Board(blockCopy));
      }
      if (j > 0) {
        int[][] blockCopy = copyBlocks(blocks);
        swapBlocks(blockCopy, i, j, i, j - 1);
        neighborList.add(new Board(blockCopy));
      }
      if (j < dimension() - 1) {
        int[][] blockCopy = copyBlocks(blocks);
        swapBlocks(blockCopy, i, j, i, j + 1);
        neighborList.add(new Board(blockCopy));
      }

      neighbors = neighborList.toArray(new Board[neighborList.size()]);
    }
    
 // string representation of this board (in the output format specified below)
    public String toString() {
      StringBuilder s = new StringBuilder();
      s.append(dimension() + "\n");
      for (int i = 0; i < dimension(); i++) {
          for (int j = 0; j < dimension(); j++) {
              s.append(String.format("%2d ", blocks[i][j]));
          }
          s.append("\n");
      }
      return s.toString();
    }
    
    private static void debug(String s) {
      if(DEBUG) {
        StdOut.println("DEBUG: " + s);
      }
    }
    
    private static void testOut(String s) {
      StdOut.println("TEST: " + s);
    }

    public static void main(String[] args) {
      int[][]  testArray = new int[3][3];
      testArray[0][0] = 0;
      testArray[0][1] = 1;
      testArray[0][2] = 4;
      testArray[1][0] = 3;
      testArray[1][1] = 2;
      testArray[1][2] = 5;
      testArray[2][0] = 6;
      testArray[2][1] = 8;
      testArray[2][2] = 7;
      Board testBoard = new Board(testArray);
      testOut(" Test board is:");
      testOut(testBoard.toString());
      
      Board twinBoard = testBoard.twin();
      testOut("Twin test:\n " + twinBoard);
      
      testOut("Hamnming number for test board is " + testBoard.hamming());
      testOut("Manhattan number for test board is " + testBoard.manhattan());
      
      testOut(" Test board is:");
      testOut(testBoard.toString());
      
      for (Board b : testBoard.neighbors()) {
        testOut("Test neighbor = " + b);
      }
      
      int[][] completeArray = new int[3][3];
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (i == 2 && j == 2) {
            completeArray[i][j] = 0;
          } else {
            completeArray[i][j] = (i * 3) + j + 1;
          }
        }
      }
      
      Board completeBoard = new Board(completeArray);
      testOut("Complete board:");
      testOut("Is complete board complete? " + completeBoard.isGoal());
      testOut("Is test board complete? " + testBoard.isGoal());
      testOut(completeBoard.toString());
      testOut("Hamming number for complete board is " + completeBoard.hamming());
      testOut("Manhattan number for complete board is " + completeBoard.manhattan());
    }
}