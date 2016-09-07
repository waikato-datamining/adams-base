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
 * RefineRange.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.optimise;


/**
 <!-- globalinfo-start -->
 * Grid Search searches the parameter hyper-grid space. The search is refineable.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-iterations &lt;int&gt; (property: iterations)
 *         The number of iterations to use.
 *         default: 1000
 * </pre>
 *
 * <pre>-grids &lt;int&gt; (property: grids)
 *         The number of grids to use.
 *         default: 3
 * </pre>
 *
 <!-- options-end -->
 *
 * @author dale
 * @version $Revision$
 */
public class RefineRange
  extends AbstractOptimiser {

  /** suid. */
  private static final long serialVersionUID = -7185430374252569572L;

  /** number of parameter refinements. */
  protected int m_Refinements;

  /** optimiser. */
  protected AbstractOptimiser m_optimiser;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Refines search space.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "refinements", "refinements",
	    2);

    m_OptionManager.add(
	    "optimiser", "optimiser",
	    new GridSearch());
  }

  @Override
  public void reset() {
    super.reset();
    if (getOptimiser() != null)
      getOptimiser().reset();
  }

  /**
   * Set optimiser to use.
   * @param ao	optimiser.
   */
  public void setOptimiser(AbstractOptimiser ao) {
    m_optimiser=ao;
  }

  /**
   * get optimiser.
   * @return	optimiser.
   */
  public AbstractOptimiser getOptimiser() {
    return(m_optimiser);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optimiserTipText() {
    return("optimiser");
  }
  /**
   * Sets the refinements to use.
   *
   * @param value	 the num.
   */
  public void setRefinements(int value) {
    m_Refinements = value;
  }

  /**
   * Gets the refinements use.
   *
   * @return		the number
   */
  public int getRefinements() {
    return m_Refinements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String refinementsTipText() {
    return "The number of refinements to apply.";
  }

  /**
   * Do Optimisation.
   *
   * @param datadef the vars
   * @param fitness fitness function
   * @return best vars
   */
  @Override
  public OptData optimise(OptData datadef, FitnessFunction fitness) {
    OptData sofar=getOptimiser().optimise(datadef, fitness);
    for (int i=0;i<getRefinements();i++) {
      // recalc max_mins
      getLogger().info("refinement:"+i);
      for (String var:datadef.getVarNames()) {
	OptVar ov=datadef.getVar(var);
	ov.m_max=(ov.m_max-sofar.get(var))/2+sofar.get(var);
	ov.m_min=sofar.get(var)-(sofar.get(var)-ov.m_min)/2;
      }
      sofar.cleanUp();
      sofar=getOptimiser().optimise(datadef, fitness);
    }
    return(sofar);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_optimiser.cleanUp();
  }
}
