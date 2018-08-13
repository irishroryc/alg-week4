import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

/*************************************************************************
 *  Compilation:  javac Solver.java
 *  Execution:    java Solver
 *  Dependencies: Board.java MinPQ.java 
 *
 *  Solver class implementing A* algorithm for finding solution to 8-puzzle
 *  problem.
 *
 *************************************************************************/

public class Solver {
  private List<Board> solutionBoards = new ArrayList<>();
  private boolean solved;
  
//find a solution to the initial board (using the A* algorithm)
  public Solver(Board initial) {
    MinPQ<SolutionStep> solverSteps = new MinPQ<>(new SolutionStepComparator());
    solverSteps.insert(new SolutionStep(initial, 0, null));

    MinPQ<SolutionStep> twinSolverSteps = new MinPQ<>(new SolutionStepComparator());
    twinSolverSteps.insert(new SolutionStep(initial.twin(), 0, null));

    SolutionStep step;
    SolutionStep stepTwin;
        
    while (!solverSteps.min().getBoard().isGoal() && !twinSolverSteps.min().getBoard().isGoal()) {
      step = solverSteps.delMin();
      for (Board neighbor : step.getBoard().neighbors()) {
        if(!alreadyUsed(step, neighbor)) {
          solverSteps.insert(new SolutionStep(neighbor, step.getMoves() + 1, step));
        }
      }
      
      stepTwin = twinSolverSteps.delMin();
      for (Board neighbor : stepTwin.getBoard().neighbors()) {
        if(!alreadyUsed(stepTwin, neighbor)) {
          twinSolverSteps.insert(new SolutionStep(neighbor, stepTwin.getMoves() + 1, stepTwin));
        }
      }
    }
  
    step = solverSteps.delMin();
    solved = step.getBoard().isGoal();

    solutionBoards.add(step.getBoard());
    while ((step = step.getPreviousStep()) != null) {
      solutionBoards.add(0, step.getBoard());
    }
  }
  
  private boolean alreadyUsed(SolutionStep step, Board test) {
    SolutionStep oldStep = step.getPreviousStep();
    boolean alreadyUsed = false;
    while(oldStep != null) {
      if (oldStep.getBoard().equals(test)) {
        alreadyUsed = true;
      }
      oldStep = oldStep.getPreviousStep();
    }
    return alreadyUsed;
  }
  
//is the initial board solvable?
  public boolean isSolvable() {
    return solved;
  }
  
//min number of moves to solve initial board; -1 if unsolvable
  public int moves() {
    int moves;
    if (isSolvable()) {
        moves = solutionBoards.size() - 1;
    } else {
        moves = -1;
    }
    return moves;
  }
  
  
//sequence of boards in a shortest solution; null if unsolvable
  public Iterable<Board> solution() {
    Iterable<Board> iterable;
    if (isSolvable()) {
        iterable = new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return new SolutionIterator();
            }
        };
    } else {
        iterable = null;
    }
    return iterable;
  }

  private static class SolutionStep {

    private int moves;
    private Board stepBoard;
    private SolutionStep previousStep;

    private SolutionStep(Board board, int moves, SolutionStep previousStep) {
        this.stepBoard = board;
        this.moves = moves;
        this.previousStep = previousStep;
    }

    public int getMoves() {
        return moves;
    }

    public int getPriority() {
        return stepBoard.manhattan() + moves;
    }

    public Board getBoard() {
        return stepBoard;
    }

    public SolutionStep getPreviousStep() {
        return previousStep;
    }
  }
  
  private class SolutionIterator implements Iterator<Board> {

    private int index = 0;

    @Override
    public boolean hasNext() {
        return index < solutionBoards.size();
    }

    @Override
    public Board next() {
        return solutionBoards.get(index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("It is not supported to remove a board from the solution.");
    }
  }
  
  private static class SolutionStepComparator implements Comparator<SolutionStep> {

    @Override
    public int compare(SolutionStep step1, SolutionStep step2) {
        return step1.getPriority() - step2.getPriority();
    }
  }
  
  // Test client for solver class
  public static void main(String[] args) {

    // create initial board from file
    In in = new In(args[0]);
    int n = in.readInt();
    int[][] blocks = new int[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
          blocks[i][j] = in.readInt();
    Board initial = new Board(blocks);

    StdOut.println("Initial board:");
    StdOut.println(initial);
    
    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable())
      StdOut.println("No solution possible");
    else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution())
          StdOut.println(board);
    }
  }
}