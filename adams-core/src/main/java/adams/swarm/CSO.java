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
abstract public class CSO {

  /*
   * Algorithm parameters
   *
   */
  public int    csoSwarmSize    = 1000;
  public double csoPhi          = 0.1;
  public long   csoSeed         = 42;
  public long   csoRunTime      = 3;
  public boolean csoEvalParallel= false;
  public boolean debug          = false;

  /*
   * Time when CSO starts running
   */
  long startTime;

  /*
   * Matrices to store the swarm positions,
   * velocities, and fitnesses
   *
   */
  DoubleMatrix swarm,vel,fit;

  /*
   * Counter for the number of iterations
   *
   */
  int iterationCounter          = 0;

  /*
   * Problem-specific random particle generator
   * -- should return a one dimensional matrix of fixed length
   *
   */
  abstract public DoubleMatrix randomParticle();

  /*
   * Problem-specific fitness function
   * -- expects a one-dimensional matrix
   * -- returns a non-negative value where lower is better
   * -- should be implemented for different problems
   */
  abstract public double particleFitness(DoubleMatrix particle);

  /*
   * Termination condition
   * -- override for different termination conditions
   *
   */
  public boolean terminate(){
    return System.currentTimeMillis()-startTime>csoRunTime*1000;
  }

  /*
   * Stringifier
   * -- extend this method to add additional problem-specific
   *    information
   *
   */
  public String toString(){
    String result;
    result  = this.getClass().getName();
    result += "\ncsoSwarmSize    = "+csoSwarmSize;
    result += "\ncsoPhi          = "+csoPhi;
    result += "\ncsoSeed         = "+csoSeed;
    result += "\ncsoRunTime      = "+csoRunTime;
    result += "\ncsoEvalParallel = "+csoEvalParallel;
    return result;
  }

  /*
   * Method to get the best particle in the swarm
   *
   */
  public DoubleMatrix getBest(){
    int indexBest=fit.rowArgmins()[0];
    return swarm.getRow(indexBest);
  }

  /*
   * Methods to get information about the swarm as a string
   * -- extend to add additional statistics
   *
   */
  public String reportStringHeader(){ return "iteration\tbest\tmean"; }
  public String reportString(){
    int indexBest=fit.rowArgmins()[0];
    String result = iterationCounter+"\t"+fit.get(0,indexBest)
      +"\t"+fit.mean();
    if (debug){
      result += "\nswarm: "+swarm;
      result += "\nvel:   "+vel;
      result += "\nfit:   "+fit;
    }
    return result;
  }

  /*
   * Helper method to run the competitions, used by run()
   *
   */
  void runCompetitions(int[] swarmIndices,
		       int[] winners, int[] losers){
    for (int i=0;i<csoSwarmSize;i+=2){
      int    particle1Index = swarmIndices[i],
	particle2Index = swarmIndices[i+1];
      double particle1Fit   = fit.get(0,particle1Index),
	particle2Fit   = fit.get(0,particle2Index);
      if (particle1Fit<particle2Fit) {
	winners[i/2] = particle1Index;
	losers[i/2]  = particle2Index;
      } else {
	winners[i/2] = particle2Index;
	losers[i/2]  = particle1Index;
      }
    }
  }

  /*
   * Helper method to update the velocities of losers
   * in the competition, used by run()
   *
   */
  void updateLoserVelocities(int[] winners, int[] losers){
    DoubleMatrix mean=swarm.columnMeans();
    for (int i=0;i<csoSwarmSize/2;i++) {
      // Generate three random constants
      double r1 = Random.nextDouble(),
	r2 = Random.nextDouble(),
	r3 = Random.nextDouble();
      // Get the difference between the winner and loser,
      // the swarm mean, and the difference between mean
      // and loser
      DoubleMatrix winner     = swarm.getRow(winners[i]),
	loser      = swarm.getRow(losers[i]),
	diffWinner = winner.sub(loser),
	diffMean   = mean.sub(loser);
      // Get the loser's current velocity
      DoubleMatrix loserVel = vel.getRow(losers[i]);
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
      vel.putRow(losers[i],loserVel);
    }
  }


  /*
   * Helper method to update the positions of the losers
   * in the competition, used by run()
   *
   */
  void updateLoserPositions(int[] losers){
    for (int i=0;i<losers.length;i++) {
      DoubleMatrix loser=swarm.getRow(losers[i]);
      loser.addi(vel.getRow(losers[i]));
      swarm.putRow(losers[i],loser);
    }
  }

  /*
   * Helper methods to evaluate all or part of the swarm,
   * either in serial or parallel, used by run()
   *
   */
  void evalSwarm(int[] indices){
    IntStream indicesStream=Arrays.stream(indices);
    if (csoEvalParallel)
      indicesStream=indicesStream.parallel();
    indicesStream.forEach((index)->{
      fit.put(0,index,particleFitness(swarm.getRow(index)));
    });
  }
  void evalSwarm(){
    int[] indices=new int[csoSwarmSize];
    for (int i=0;i<indices.length; i++)
      indices[i]=i;
    evalSwarm(indices);
  }

  /*
   * Helper method to shuffle the elements of an integer
   * array randomly, returning a shuffled copy. Used by run().
   *
   */
  int[] shuffle(int[] arr){
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

  /*
   * Run method
   * -- performs the main loop of CSO
   * -- returns best solution found
   *
   */
  public DoubleMatrix run(){
    // Report
    System.out.println(this);
    // Set the seed
    Random.seed(csoSeed);
    // Record start time
    startTime=System.currentTimeMillis();
    // Create the initial swarm
    swarm=randomParticle();
    for (int i=1;i<csoSwarmSize;i++)
      swarm=DoubleMatrix.concatVertically(swarm,randomParticle());
    // Create an vector of fitness values
    fit=DoubleMatrix.zeros(1,csoSwarmSize);
    // Create the initial velocity vectors
    vel=DoubleMatrix.zeros(csoSwarmSize,swarm.columns);
    // Create an array list of indices which will be repeatedly
    // shuffled each iteration so that competitions can be run
    int[] swarmIndices=new int[csoSwarmSize];
    for (int i=0;i<csoSwarmSize;i++)
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
      if (debug)
	System.out.println("swarmIndices="+Arrays.toString(swarmIndices));
      // We use an arrays of integers to store the winners and losers
      int[] winners  = new int[csoSwarmSize/2],
	losers   = new int[csoSwarmSize/2];
      // Run the competitions
      runCompetitions(swarmIndices,winners,losers);
      if (debug) {
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
      iterationCounter++;
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
