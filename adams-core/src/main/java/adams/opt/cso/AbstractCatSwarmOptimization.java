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
 * AbstractCatSwarmOptimization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso;

import adams.core.Pausable;
import adams.core.Randomizable;
import adams.core.StoppableWithFeedback;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.opt.cso.stopping.AbstractStoppingCriterion;
import adams.opt.cso.stopping.MaxTrainTime;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.jblas.DoubleMatrix;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.IntStream;

/**
 * Ancestor for Cat Swarm Optimizations (CSO).
 *
 * @author Mike Mayo (mmayo at waikato dot ac dot nz) - original code
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCatSwarmOptimization
  extends AbstractOptionHandler
  implements Randomizable, StoppableWithFeedback, Pausable, TechnicalInformationHandler {

  private static final long serialVersionUID = 6351368598598576158L;

  /** the swarm size. */
  protected int m_SwarmSize;

  /** the phi (between 0 and 1). */
  protected  double m_Phi;

  /** the seed value. */
  protected long m_Seed;

  /** whether to evaluate in parallel. */
  protected boolean m_EvalParallel;

  /** the stopping criterion. */
  protected AbstractStoppingCriterion m_Stopping;

  /** the swarm positions. */
  protected DoubleMatrix m_Positions;

  /** the swarm velocities. */
  protected DoubleMatrix m_Velocities;

  /** the swarm fitnesses. */
  protected DoubleMatrix m_Fitnesses;

  /** the random number generator. */
  protected Random m_Random;

  /** Counter for the number of iterations */
  protected int m_IterationCounter;

  /** whether execution has been stopped. */
  protected boolean m_Stopped;

  /** whether execution has been paused. */
  protected boolean m_Paused;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "swarm-size", "swarmSize",
      1000, 1, null);

    m_OptionManager.add(
      "phi", "phi",
      0.1, 0.0, 1.0);

    m_OptionManager.add(
      "seed", "seed",
      42L);

    m_OptionManager.add(
      "eval-parallel", "evalParallel",
      false);

    m_OptionManager.add(
      "stopping", "stopping",
      new MaxTrainTime());
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.INBOOK);
    result.setValue(Field.AUTHOR, "Chu, Shu-Chuan and Tsai, Pei-wei and Pan, Jeng-Shyang");
    result.setValue(Field.TITLE, "Cat Swarm Optimization");
    result.setValue(Field.BOOKTITLE, "PRICAI 2006: Trends in Artificial Intelligence: 9th Pacific Rim International Conference on Artificial Intelligence Guilin, China, August 7-11, 2006 Proceedings");
    result.setValue(Field.EDITOR, "Yang, Qiang and Webb, Geoff");
    result.setValue(Field.PUBLISHER, "Springer Berlin Heidelberg");
    result.setValue(Field.PAGES, "854-858");
    result.setValue(Field.ISBN, "978-3-540-36668-3");
    result.setValue(Field.YEAR, "2006");
    result.setValue(Field.URL, "http://dx.doi.org/10.1007/978-3-540-36668-3_94");

    return result;
  }

  /**
   * Sets the swarm size to use.
   *
   * @param value	the size
   */
  public void setSwarmSize(int value) {
    if (getOptionManager().isValid("swarmSize", value)) {
      m_SwarmSize = value;
      reset();
    }
  }

  /**
   * Returns the swarm size in use.
   *
   * @return		the size
   */
  public int getSwarmSize() {
    return m_SwarmSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String swarmSizeTipText() {
    return "The size of the swarm.";
  }

  /**
   * Sets the phi parameter.
   *
   * @param value	phi
   */
  public void setPhi(double value) {
    if (getOptionManager().isValid("phi", value)) {
      m_Phi = value;
      reset();
    }
  }

  /**
   * Returns the phi parameter.
   *
   * @return		phi
   */
  public double getPhi() {
    return m_Phi;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String phiTipText() {
    return "The phi parameter.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    if (getOptionManager().isValid("seed", value)) {
      m_Seed = value;
      reset();
    }
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for randomization.";
  }

  /**
   * Sets whether to evaluate in parallel.
   *
   * @param value	true if in parallel
   */
  public void setEvalParallel(boolean value) {
    m_EvalParallel = value;
    reset();
  }

  /**
   * Returns  whether to evaluate in parallel.
   *
   * @return		true if in parallel
   */
  public boolean getEvalParallel() {
    return m_EvalParallel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String evalParallelTipText() {
    return "The size of the swarm.";
  }

  /**
   * Sets the stopping criterion.
   *
   * @param value	the criterion
   */
  public void setStopping(AbstractStoppingCriterion value) {
    m_Stopping = value;
    reset();
  }

  /**
   * Returns the stopping criterion.
   *
   * @return		the criterion
   */
  public AbstractStoppingCriterion getStopping() {
    return m_Stopping;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stoppingTipText() {
    return "The criterion for stopping.";
  }

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
   * Returns the current iteration.
   *
   * @return		the iteration
   */
  public int getCurrentIteration() {
    return m_IterationCounter;
  }

  /**
   * Stringifier
   * -- extend this method to add additional problem-specific
   *    information
   *
   */
  public String toString() {
    return OptionUtils.getCommandLine(this);
  }

  /**
   * Method to get the best particle in the swarm
   *
   */
  public DoubleMatrix getBest() {
    int indexBest= m_Fitnesses.rowArgmins()[0];
    return m_Positions.getRow(indexBest);
  }

  /**
   * Methods to get information about the swarm as a string
   * -- extend to add additional statistics
   *
   */
  public String reportStringHeader() {
    return "iteration\tbest\tmean";
  }

  /**
   * Returns the current fitness.
   *
   * @return		the fitness
   */
  public double getCurrentFitness() {
    int indexBest= m_Fitnesses.rowArgmins()[0];
    return m_Fitnesses.get(0,indexBest);
  }

  public String reportString() {
    int indexBest= m_Fitnesses.rowArgmins()[0];
    StringBuilder result = new StringBuilder(
      m_IterationCounter
	+ "\t"
	+ m_Fitnesses.get(0,indexBest)
	+ "\t"
	+ m_Fitnesses.mean());
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE)) {
      result.append("\nswarm: " + m_Positions);
      result.append("\nvel:   "+ m_Velocities);
      result.append("\nfit:   "+ m_Fitnesses);
    }
    return result.toString();
  }

  /**
   * Helper method to run the competitions, used by run()
   *
   */
  protected void runCompetitions(int[] swarmIndices, int[] winners, int[] losers) {
    for (int i=0;i< m_SwarmSize;i+=2) {
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
  protected void updateLoserVelocities(int[] winners, int[] losers) {
    DoubleMatrix mean= m_Positions.columnMeans();
    for (int i=0;i< m_SwarmSize /2;i++) {
      // Generate three random constants
      double r1 = m_Random.nextDouble();
      double r2 = m_Random.nextDouble();
      double r3 = m_Random.nextDouble();
      // Get the difference between the winner and loser,
      // the swarm mean, and the difference between mean
      // and loser
      DoubleMatrix winner = m_Positions.getRow(winners[i]);
      DoubleMatrix loser = m_Positions.getRow(losers[i]);
      DoubleMatrix diffWinner = winner.sub(loser);
      DoubleMatrix diffMean = mean.sub(loser);
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
  protected void updateLoserPositions(int[] losers) {
    for (int i=0;i<losers.length;i++) {
      DoubleMatrix loser= m_Positions.getRow(losers[i]);
      loser.addi(m_Velocities.getRow(losers[i]));
      m_Positions.putRow(losers[i],loser);
    }
  }

  /**
   * Helper methods to evaluate all or part of the swarm,
   * either in serial or parallel, used by run()
   *
   */
  protected void evalSwarm(int[] indices) {
    IntStream indicesStream=Arrays.stream(indices);
    if (m_EvalParallel)
      indicesStream=indicesStream.parallel();
    indicesStream.forEach((index)->{
      m_Fitnesses.put(0,index,particleFitness(m_Positions.getRow(index)));
    });
  }

  /**
   * Evalutaes the swarm.
   */
  protected void evalSwarm() {
    int[] indices=new int[m_SwarmSize];
    for (int i=0;i<indices.length; i++)
      indices[i]=i;
    evalSwarm(indices);
  }

  /**
   * Helper method to shuffle the elements of an integer
   * array randomly, returning a shuffled copy. Used by run().
   *
   */
  protected int[] shuffle(int[] arr) {
    TIntList result = new TIntArrayList(arr);
    result.shuffle(m_Random);
    return result.toArray();
  }

  /**
   * Gets executed before the actual run starts.
   */
  protected void preRun() {
    getLogger().info(this.toString());

    m_IterationCounter = 0;
    m_Stopped          = false;
    m_Paused           = false;
    m_Random           = new Random(m_Seed);
    m_Stopping.start();

    // Create the initial swarm
    m_Positions = randomParticle();
    for (int i = 1; i < m_SwarmSize; i++)
      m_Positions = DoubleMatrix.concatVertically(m_Positions,randomParticle());

    // Create an vector of fitness values
    m_Fitnesses = DoubleMatrix.zeros(1, m_SwarmSize);

    // Create the initial velocity vectors
    m_Velocities = DoubleMatrix.zeros(m_SwarmSize, m_Positions.columns);
  }

  /**
   * Performs the actual optimization.
   *
   * @return		the best result
   */
  protected DoubleMatrix doRun() {
    // Create an array list of indices which will be repeatedly
    // shuffled each iteration so that competitions can be run
    int[] swarmIndices = new int[m_SwarmSize];
    for (int i = 0; i < m_SwarmSize;i++)
      swarmIndices[i] = i;

    // Print out the report header
    getLogger().info(reportStringHeader());

    // Evaluate the initial swarm
    evalSwarm();

    // Iterate
    while (!m_Stopping.checkStopping(this) && !m_Stopped) {
      if (isPaused() && !isStopped()) {
	Utils.wait(this, this, 1000, 100);
	continue;
      }

      if (isStopped()) {
	getLogger().severe("Interrupted!");
	break;
      }

      // Report
      getLogger().info(reportString());

      // Shuffle the swarm indices for the competitions
      swarmIndices = shuffle(swarmIndices);
      if (isLoggingEnabled())
	getLogger().fine("swarmIndices=" + Arrays.toString(swarmIndices));

      // We use an arrays of integers to store the winners and losers
      int[] winners = new int[m_SwarmSize /2];
      int[] losers  = new int[m_SwarmSize /2];

      // Run the competitions
      runCompetitions(swarmIndices,winners,losers);
      if (isLoggingEnabled()) {
	getLogger().fine("winners=" + Arrays.toString(winners));
	getLogger().fine("losers=" + Arrays.toString(losers));
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

    return getBest();
  }

  /**
   * Gets called after the optimization finishes.
   *
   * @param best	the best result, if any
   * @return		the (potentially) updated best result
   */
  protected DoubleMatrix postRun(DoubleMatrix best) {
    // Report on final swarm
    getLogger().info(reportString());

    // Report best solution
    getLogger().info("best particle:\n" + best);
    getLogger().info("best fitness:\n" + particleFitness(best));

    return best;
  }

  /**
   * Run method
   * -- performs the main loop of CSO
   * -- returns best solution found
   *
   */
  public DoubleMatrix run() {
    DoubleMatrix	result;

    preRun();

    result = doRun();

    return postRun(result);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
    m_Paused  = false;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    m_Paused = true;
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    return m_Paused;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    m_Paused = false;
  }

  /**
   * Runs the swarm from commandline.
   *
   * @param env		the environment class to use
   * @param swarm	the flow class to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runSwarm(Class env, Class swarm, String[] args) {
    AbstractCatSwarmOptimization 	swarmInst;
    DoubleMatrix			result;

    Environment.setEnvironmentClass(env);
    Environment.setHome(OptionUtils.getOption(args, "-home"));
    LoggingHelper.useHandlerFromOptions(args);

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("Help requested...\n");
	swarmInst = (AbstractCatSwarmOptimization) OptionUtils.forName(AbstractCatSwarmOptimization.class, swarm.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(swarmInst));
	LoggingHelper.outputHandlerOption();
      }
      else {
	swarmInst = (AbstractCatSwarmOptimization) OptionUtils.forName(AbstractCatSwarmOptimization.class, swarm.getName(), new String[0]);
	ArrayConsumer.setOptions(swarmInst, args);
	result = swarmInst.run();
	if (result != null) {
	  System.out.println(result);
	}
	else {
	  System.err.println("Failed!");
	  System.exit(1);
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
