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
 * RastriginProblem.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.swarm;

import adams.env.Environment;
import org.jblas.DoubleMatrix;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author Mike Mayo (mmayo at waikato dot ac dot nz) - original code
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RastriginProblem
  extends AbstractCatSwarmOptimization {

  private static final long serialVersionUID = 770726498906964673L;

  /** the dimensions. */
  protected int m_ProbDimensions;

  /** the minimum value. */
  protected double m_ProbMaxValue;

  /** the maximum value. */
  protected double m_ProbMinValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rastrigin problem\n\n"
      + "For more information see:\n"
      + "https://en.wikipedia.org/wiki/Rastrigin_function";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prob-dimensions", "probDimensions",
      30, 1, null);

    m_OptionManager.add(
      "prob-min-value", "probMinValue",
      -5.12);

    m_OptionManager.add(
      "prob-max-value", "probMaxValue",
      5.12);
  }

  /**
   * Sets the problem dimensions.
   *
   * @param value	the dimensions
   */
  public void setProbDimensions(int value) {
    if (getOptionManager().isValid("probDimensions", value)) {
      m_ProbDimensions = value;
      reset();
    }
  }

  /**
   * Returns the problem dimensions.
   *
   * @return		the dimensions
   */
  public int getProbDimensions() {
    return m_ProbDimensions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String probDimensionsTipText() {
    return "The problem dimensions.";
  }

  /**
   * Sets the minimum value for the problem.
   *
   * @param value	the minimum
   */
  public void setProbMinValue(double value) {
    if (getOptionManager().isValid("probMinValue", value)) {
      m_ProbMinValue = value;
      reset();
    }
  }

  /**
   * Returns the minimum value for the problem.
   *
   * @return		the minimum
   */
  public double getProbMinValue() {
    return m_ProbMinValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String probMinValueTipText() {
    return "The minimum value for the problem.";
  }

  /**
   * Sets the maximum value for the problem.
   *
   * @param value	the maximum
   */
  public void setProbMaxValue(double value) {
    if (getOptionManager().isValid("probMaxValue", value)) {
      m_ProbMaxValue = value;
      reset();
    }
  }

  /**
   * Returns the maximum value for the problem.
   *
   * @return		the maximum
   */
  public double getProbMaxValue() {
    return m_ProbMaxValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String probMaxValueTipText() {
    return "The maximum value for the problem.";
  }

  /**
   * Implementation of random particle generator
   *
   */
  @Override
  public DoubleMatrix randomParticle() {
    DoubleMatrix particle = new DoubleMatrix(1, m_ProbDimensions);
    for(int i = 0; i < m_ProbDimensions; ++i)
      particle.data[i] = m_Random.nextDouble();
    particle.muli(m_ProbMaxValue - m_ProbMinValue);
    particle.addi(m_ProbMinValue);
    return particle;
  }

  /**
   * Implementation of fitness function for the rastrigin problem
   *
   */
  @Override
  public double particleFitness(DoubleMatrix particle){
    double result = 10* m_ProbDimensions;
    for (int i = 0; i < m_ProbDimensions; i++) {
      double x = particle.get(0,i);
      result += x*x;
      result -= 10*Math.cos(2*Math.PI*x);
    }
    return result;
  }

  /**
   * Launcher method
   * -- load and set parameters and then call run()
   *
   */
  public static void main(String[] args) {
    runSwarm(Environment.class, RastriginProblem.class, args);
  }
}
