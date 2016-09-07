/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * CSO.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.swarm;

import org.jblas.DoubleMatrix;
import org.jblas.util.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Ancestor for Cat Swarm Optimizations (CSO).
 *
 * @author Mike Mayo (mmayo at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class CSO {

  /*
   * Algorithm parameters
   *
   */
  protected int m_CsoSwarmSize = 1000;
  protected  double m_CsoPhi = 0.1;
  protected long m_CsoSeed = 42;
  protected long m_CsoRunTime = 3;
  protected boolean m_CsoEvalParallel = false;
  protected boolean m_Debug = false;

  /**
   * Time when CSO starts running
   */
  protected long startTime;

  /**
   * Matrices to store the swarm positions,
   * velocities, and fitnesses
   *
   */
  protected DoubleMatrix m_Swarm;
  protected DoubleMatrix m_Velocities;
  protected DoubleMatrix m_Fitnesses;

  /**
   * Counter for the number of iterations
   *
   */
  protected int m_IterationCounter = 0;

  /**
   * Problem-specific random particle generator
   * -- should return a one dimensional matrix of fixed length
   *
   */
  public abstract DoubleMatrix randomParticle();

  /**
   * Problem-specific fitness function
   * -- expects a one-dimensional matrix
   * -- returns a non-negative value where lower is better
   * -- should be implemented for different problems
   */
  public abstract double particleFitness(DoubleMatrix particle);

  /**
   * Termination condition
   * -- override for different termination conditions
   *
   */
  public boolean terminate(){
    return System.currentTimeMillis()-startTime> m_CsoRunTime *1000;
  }

  /**
   * Stringifier
   * -- extend this method to add additional problem-specific
   *    information
   *
   */
  public String toString(){
    String result;
    result  = this.getClass().getName();
    result += "\ncsoSwarmSize    = "+ m_CsoSwarmSize;
    result += "\ncsoPhi          = "+ m_CsoPhi;
    result += "\ncsoSeed         = "+ m_CsoSeed;
    result += "\ncsoRunTime      = "+ m_CsoRunTime;
    result += "\ncsoEvalParallel = "+ m_CsoEvalParallel;
    return result;
  }

  /**
   * Method to get the best particle in the swarm
   *
   */
  public DoubleMatrix getBest(){
    int indexBest= m_Fitnesses.rowArgmins()[0];
    return m_Swarm.getRow(indexBest);
  }

  /**
   * Methods to get information about the swarm as a string
   * -- extend to add additional statistics
   *
   */
  public String reportStringHeader() {
    return "iteration\tbest\tmean";
  }

  public String reportString(){
    int indexBest= m_Fitnesses.rowArgmins()[0];
    String result = m_IterationCounter +"\t"+ m_Fitnesses.get(0,indexBest)
      +"\t"+ m_Fitnesses.mean();
    if (m_Debug){
      result += "\nswarm: "+ m_Swarm;
      result += "\nvel:   "+ m_Velocities;
      result += "\nfit:   "+ m_Fitnesses;
    }
    return result;
  }

  /**
   * Helper method to run the competitions, used by run()
   *
   */
  protected void runCompetitions(int[] swarmIndices, int[] winners, int[] losers){
    for (int i=0;i< m_CsoSwarmSize;i+=2){
      int particle1Index = swarmIndices[i];
      int particle2Index = swarmIndices[i+1];
      double particle1Fit = m_Fitnesses.get(0,particle1Index);
      double particle2Fit = m_Fitnesses.get(0,particle2Index);
      if (particle1Fit<particle2Fit) {
	winners[i/2] = particle1Index;
	losers[i/2]  = particle2Index;
      } else {
	winners[i/2] = particle2Index;
	losers[i/2]  = particle1Index;
      }
    }
  }

  /**
   * Helper method to update the velocities of losers
   * in the competition, used by run()
   *
   */
  protected void updateLoserVelocities(int[] winners, int[] losers){
    DoubleMatrix mean= m_Swarm.columnMeans();
    for (int i=0;i< m_CsoSwarmSize /2;i++) {
      // Generate three random constants
      double r1 = Random.nextDouble(),
	r2 = Random.nextDouble(),
	r3 = Random.nextDouble();
      // Get the difference between the winner and loser,
      // the swarm mean, and the difference between mean
      // and loser
      DoubleMatrix winner     = m_Swarm.getRow(winners[i]),
	loser      = m_Swarm.getRow(losers[i]),
	diffWinner = winner.sub(loser),
	diffMean   = mean.sub(loser);
      // Get the loser's current velocity
      DoubleMatrix loserVel = m_Velocities.getRow(losers[i]);
      // Scale the loser's velocity by r1
      loserVel.muli(r1);
      // Add a component of the difference with the winner
      // to the loser's velocity
      diffWinner.muli(r2);
      loserVel.addi(diffWinner);
      // Add a component of the difference to the swarm
      // mean to the loser's velocity
      diffMean.muli(r3);
      loserVel.addi(diffMean);
      // Update the velocity of the loser
      m_Velocities.putRow(losers[i],loserVel);
    }
  }


  /**
   * Helper method to update the positions of the losers
   * in the competition, used by run()
   *
   */
  protected void updateLoserPositions(int[] losers){
    for (int i=0;i<losers.length;i++) {
      DoubleMatrix loser= m_Swarm.getRow(losers[i]);
      loser.addi(m_Velocities.getRow(losers[i]));
      m_Swarm.putRow(losers[i],loser);
    }
  }

  /**
   * Helper methods to evaluate all or part of the swarm,
   * either in serial or parallel, used by run()
   *
   */
  protected void evalSwarm(int[] indices) {
    IntStream indicesStream=Arrays.stream(indices);
    if (m_CsoEvalParallel)
      indicesStream=indicesStream.parallel();
    indicesStream.forEach((index)->{
      m_Fitnesses.put(0,index,particleFitness(m_Swarm.getRow(index)));
    });
  }

  /**
   * Evalutaes the swarm.
   */
  protected void evalSwarm() {
    int[] indices=new int[m_CsoSwarmSize];
    for (int i=0;i<indices.length; i++)
      indices[i]=i;
    evalSwarm(indices);
  }

  /**
   * Helper method to shuffle the elements of an integer
   * array randomly, returning a shuffled copy. Used by run().
   *
   */
  protected int[] shuffle(int[] arr){
    int[] shuffled=new int[arr.length];
    ArrayList<Integer> arrList=new ArrayList<Integer>(arr.length);
    for (int i=0;i<arr.length;i++)
      arrList.add(arr[i]);
    for (int i=0;i<shuffled.length;i++) {
      int j=Random.nextInt(arrList.size());
      shuffled[i]=arrList.get(j);
      arrList.remove(j);
    }
    return shuffled;
  }

  /**
   * Run method
   * -- performs the main loop of CSO
   * -- returns best solution found
   *
   */
  public DoubleMatrix run(){
    // Report
    System.out.println(this);
    // Set the seed
    Random.seed(m_CsoSeed);
    // Record start time
    startTime=System.currentTimeMillis();
    // Create the initial swarm
    m_Swarm =randomParticle();
    for (int i=1;i< m_CsoSwarmSize;i++)
      m_Swarm =DoubleMatrix.concatVertically(m_Swarm,randomParticle());
    // Create an vector of fitness values
    m_Fitnesses =DoubleMatrix.zeros(1, m_CsoSwarmSize);
    // Create the initial velocity vectors
    m_Velocities =DoubleMatrix.zeros(m_CsoSwarmSize, m_Swarm.columns);
    // Create an array list of indices which will be repeatedly
    // shuffled each iteration so that competitions can be run
    int[] swarmIndices=new int[m_CsoSwarmSize];
    for (int i=0;i< m_CsoSwarmSize;i++)
      swarmIndices[i]=i;
    // Print out the report header
    System.out.println(reportStringHeader());
    // Evaluate the initial swarm
    evalSwarm();
    // Iterate
    while(!terminate()){
      // Report
      System.out.println(reportString());
      // Shuffle the swarm indices for the competitions
      swarmIndices=shuffle(swarmIndices);
      if (m_Debug)
	System.out.println("swarmIndices="+Arrays.toString(swarmIndices));
      // We use an arrays of integers to store the winners and losers
      int[] winners  = new int[m_CsoSwarmSize /2],
	losers   = new int[m_CsoSwarmSize /2];
      // Run the competitions
      runCompetitions(swarmIndices,winners,losers);
      if (m_Debug) {
	System.out.println("winners="+Arrays.toString(winners));
	System.out.println("losers="+Arrays.toString(losers));
      }
      // Update the velocities of the losers
      updateLoserVelocities(winners,losers);
      // Update the positions of the losers
      updateLoserPositions(losers);
      // Re-evaluate the losers
      evalSwarm(losers);
      // Increment iteration counter
      m_IterationCounter++;
    }
    // Report on final swarm
    System.out.println(reportString());
    // Report best solution
    DoubleMatrix best=getBest();
    System.out.println("best particle:\n"+best);
    System.out.println("best fitness:\n" +particleFitness(best));
    // Done
    return best;
  }
}
