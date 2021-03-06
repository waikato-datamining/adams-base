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
 * RandomNumberGenerator.java
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.data.random.JavaRandomDouble;

/**
 <!-- globalinfo-start -->
 * Random number generator. The type of random numbers depends on the chosen generator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Number<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RandomNumberGenerator
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the random numbers are output as array rather than one by one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-generator &lt;adams.data.random.RandomNumberGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The random number generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.random.JavaRandomDouble
 * </pre>
 * 
 * <pre>-max-num &lt;int&gt; (property: maxNum)
 * &nbsp;&nbsp;&nbsp;The maximum number of random numbers to generate; -1 means unlimited.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RandomNumberGenerator
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6216146938771296415L;

  /** the random number generator to use. */
  protected adams.data.random.RandomNumberGenerator m_Generator;

  /** the maximum number of random numbers to generate. */
  protected int m_MaxNum;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Random number generator. The type of random numbers depends on the chosen generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new JavaRandomDouble());

    m_OptionManager.add(
	    "max-num", "maxNum",
	    1000, -1, null);
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Number.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the random numbers are output as array rather than one by one.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "generator", m_Generator);
    result += QuickInfoHelper.toString(this, "maxNum", m_MaxNum, "/");
    result += QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"), ", ");

    return result;
  }

  /**
   * Sets the random number generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(adams.data.random.RandomNumberGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the random number generator to use.
   *
   * @return		the generator
   */
  public adams.data.random.RandomNumberGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The random number generator to use.";
  }

  /**
   * Sets the maximum number of random numbers to generate.
   *
   * @param value	the maximum number, -1 means unlimited
   */
  public void setMaxNum(int value) {
    if ((value == -1) || (value >= 1)) {
      m_MaxNum = value;
      reset();
    }
    else {
      getLogger().warning("Maximum number must be >= 1 or -1 for unlimited, provided: " + value);
    }
  }

  /**
   * Returns the maximum number of random numbers to generate.
   *
   * @return		the maximum number, -1 means unlimited
   */
  public int getMaxNum() {
    return m_MaxNum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxNumTipText() {
    return 
	"The maximum number of random numbers to generate; -1 means unlimited.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    if (m_Generator != null)
      m_Generator.reset();
  }

  /**
   * Does nothing.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    int		i;

    m_Queue.clear();
    for (i = 0; i < m_MaxNum; i++)
      m_Queue.add(m_Generator.next());

    return null;
  }
}
