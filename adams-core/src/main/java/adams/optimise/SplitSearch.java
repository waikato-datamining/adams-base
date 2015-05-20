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
 * SplitSearch.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise;

import java.util.Hashtable;

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
public class SplitSearch
  extends AbstractOptimiser {

  /** suid. */
  private static final long serialVersionUID = 8007183147566682576L;

  /** number of iterations. */
  protected int m_NumSplits;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Grid Search searches the parameter hyper-grid space.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "splits", "splits",
	    3);
  }

  /**
   * Sets the iterations to use.
   *
   * @param value	 the iterations.
   */
  public void setSplits(int value) {
    m_NumSplits = value;
  }

  /**
   * Gets the iterations use.
   *
   * @return		the iterations
   */
  public int getSplits() {
    return m_NumSplits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitsTipText() {
    return "The number of splits.";
  }

  /**
   * Initialise vars to their minimum.
   * @param datadef	vars
   */
  protected void setMin(OptData datadef) {
    for (String var:datadef.getVarNames()) {
      OptVar ov=datadef.getVar(var);
      datadef.set(var, ov.m_min);
    }
  }

  /**
   * Initialise vars to their maximum.
   * @param datadef	vars
   */
  protected void setMax(OptData datadef) {
    for (String var:datadef.getVarNames()) {
      OptVar ov=datadef.getVar(var);
      datadef.set(var, ov.m_max);
    }
  }

  /**
   * Calc for this grid.
   * @param datadef vars
   * @param fitness fitness fn
   * @return best
   */
  public OptData doGrid(OptData datadef, FitnessFunction fitness) {
    Hashtable<String,Double> stepsize=new Hashtable<String,Double>();
    setStepSizes(datadef,stepsize);
    setMin(datadef);
    OptData initVals=datadef.getClone();
    checkBest(fitness.evaluate(datadef),datadef,fitness);

    boolean cont=true;
    while (cont) {
      cont=false;
      for (String var:datadef.getVarNames()) {
	initVals.set(var, initVals.get(var)+stepsize.get(var));
	if (initVals.get(var) > initVals.getVar(var).m_max || stepsize.get(var)==0) {
	  initVals.set(var, initVals.getVar(var).m_min);
	} else {
	  cont=true;
	  break;
	}
      }
      checkBest(fitness.evaluate(initVals),initVals,fitness);
    }
    setMax(initVals);
    checkBest(fitness.evaluate(initVals),initVals,fitness);
    initVals.cleanUp();
    return(m_bestv);
  }

  /**
   * Do grid Optimisation.
   * @param datadef the vars
   * @param fitness fitness function
   * @return best vars
   */
  public OptData optimise(OptData datadef, FitnessFunction fitness) {
    OptData sofar=doGrid(datadef,fitness).getClone();
    return(sofar);
  }


/**
 * set Set sizes for params.
 * @param datadef	params
 * @param stepsize	stepsize store
 */
  protected void setStepSizes(OptData datadef, Hashtable<String,Double> stepsize) {

    for (String var:datadef.getVarNames()) {
      OptVar ov=datadef.getVar(var);
      //double ss=(double)its/(double)numps;
      stepsize.put(var, ov.getStepSize(getSplits()));
    }

  }
}
