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
 * WekaClustererGenerator.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.io.Serializable;

import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.MathParameter;

/**
 <!-- globalinfo-start -->
 * Generates multiple clusterer setups.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.clusterers.Clusterer<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ClustererGenerator
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-setup &lt;java.lang.Object [options]&gt; (property: setup)
 * &nbsp;&nbsp;&nbsp;The base clusterer to use for the setups.
 * &nbsp;&nbsp;&nbsp;default: weka.clusterers.SimpleKMeans -N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10
 * </pre>
 *
 * <pre>-parameter &lt;weka.core.setupgenerator.AbstractParameter [options]&gt; [-parameter ...] (property: parameters)
 * &nbsp;&nbsp;&nbsp;The parameters to use for generating the setups.
 * &nbsp;&nbsp;&nbsp;default: weka.core.setupgenerator.MathParameter -property numClusters -min 2.0 -max 5.0 -step 1.0 -base 10.0 -expression I
 * </pre>
 *
 * <pre>-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If set to true, then an array of setups will be output instead of a sequence.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClustererGenerator
  extends AbstractWekaSetupGenerator<weka.clusterers.Clusterer> {

  /** for serialization. */
  private static final long serialVersionUID = -6802585691566163552L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generates multiple clusterer setups.";
  }

  /**
   * Returns the default setup. Used in the options as default value.
   *
   * @return		the default setup
   */
  protected weka.clusterers.Clusterer getDefaultSetup() {
    return new weka.clusterers.SimpleKMeans();
  }

  /**
   * Returns the default parameters. Used in the options as default value.
   *
   * @return		the default parameters
   */
  protected AbstractParameter[] getDefaultParameters() {
    AbstractParameter[]		result;

    result    = new AbstractParameter[1];
    result[0] = new MathParameter();
    ((MathParameter) result[0]).setProperty("numClusters");
    ((MathParameter) result[0]).setMin(2);
    ((MathParameter) result[0]).setMax(5);
    ((MathParameter) result[0]).setStep(1);
    ((MathParameter) result[0]).setBase(10);
    ((MathParameter) result[0]).setExpression("I");

    return result;
  }

  /**
   * Returns the default super class, the same as the type "T" when defining
   * the generics.
   *
   * @return		the default super class
   */
  protected Class getDefaultSuperClass() {
    return weka.clusterers.Clusterer.class;
  }

  /**
   * Sets the base clusterer.
   *
   * @param value	the clusterer
   */
  public void setSetup(weka.clusterers.Clusterer value) {
    m_Generator.setBaseObject((Serializable) value);
    reset();
  }

  /**
   * Returns the base clusterer.
   *
   * @return		the clusterer
   */
  public weka.clusterers.Clusterer getSetup() {
    return (weka.clusterers.Clusterer) m_Generator.getBaseObject();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String setupTipText() {
    return "The base clusterer to use for the setups.";
  }
}
