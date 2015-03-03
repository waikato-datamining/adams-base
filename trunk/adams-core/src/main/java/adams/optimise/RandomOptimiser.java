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

/*
 * RandomOptimiser.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise;

import java.util.Random;

import adams.core.Randomizable;

/**
 <!-- globalinfo-start -->
 * Generate random parameter values.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-iterations &lt;int&gt; (property: iterations)
 * &nbsp;&nbsp;&nbsp;The number of iterations to use.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * </pre>
 *
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the random number generator.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author dale
 * @version $Revision$
 */
public class RandomOptimiser
  extends AbstractOptimiser
  implements Randomizable {

  /** suid. */
  private static final long serialVersionUID = -6032771539666237896L;

  /** number of iterations. */
  protected int m_Iterations;

  /** the seed value. */
  protected long m_Seed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generate random parameter values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "iterations", "iterations",
	    1000);

    m_OptionManager.add(
	    "seed", "seed",
	    1L);
  }

  /**
   * Sets the iterations to use.
   *
   * @param value	 the iterations.
   */
  public void setIterations(int value) {
    m_Iterations = value;
    reset();
  }

  /**
   * Gets the iterations use.
   *
   * @return		the iterations
   */
  public int getIterations() {
    return m_Iterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String iterationsTipText() {
    return "The number of iterations to use.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
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
    return "The seed value for the random number generator.";
  }

  /**
   * Do the optimisation.
   *
   * @param datadef	data initialisations.
   * @param fitness	fitness function.
   * @return		best vars
   */
  public OptData optimise(OptData datadef, FitnessFunction fitness) {
    Random rand = new Random(m_Seed);
    OptData sofar = datadef;
    for (int i=0;i<getIterations();i++) {
      // recalc max_mins
      for (String var:datadef.getVarNames()) {
	OptVar ov=datadef.getVar(var);
	Double val=rand.nextDouble();
	val=(ov.m_max-ov.m_min)*val+ov.m_min;
	if (ov.m_isInteger) {
	  sofar.set(var, val.intValue());
	} else {
	  sofar.set(var, val);
	}
      }
      checkBest(fitness.evaluate(sofar),sofar,fitness);
    }
    return(m_bestv);
  }
}
