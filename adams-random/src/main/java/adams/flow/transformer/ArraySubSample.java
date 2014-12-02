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
 * ArraySubSample.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.Array;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.random.JavaRandomInt;
import adams.data.random.RandomIntegerRangeGenerator;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Generates a subset of the array, using the specified array elements.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br/>
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
 * &nbsp;&nbsp;&nbsp;default: ArraySubSample
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-elements &lt;adams.core.Range&gt; (property: elements)
 * &nbsp;&nbsp;&nbsp;The range of elements to pick from the array; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; the following placeholders can be used 
 * &nbsp;&nbsp;&nbsp;as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7732 $
 */
public class ArraySubSample
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8536100625511019961L;

  /** the size of the sub-sample. */
  protected double m_Size;
  
  /** the random number generator. */
  protected RandomIntegerRangeGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a subset of the array, using a random sub-sample.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "size", "size",
	    1.0, 0.0, null);

    m_OptionManager.add(
	    "generator", "generator",
	    new JavaRandomInt());
  }

  /**
   * Sets the size of the sample (0-1: percentage, >1: absolute number).
   *
   * @param value 	the size
   */
  public void setSize(double value) {
    if (value > 0) {
      m_Size = value;
      reset();
    }
    else {
      getLogger().warning("Sample size must be >0, provided: " + value);
    }
  }

  /**
   * Returns the size of the sample (0-1: percentage, >1: absolute number).
   *
   * @return 		the size
   */
  public double getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of the sample: 0-1 = percentage, >1 absolute number of elements.";
  }

  /**
   * Sets the random number generator.
   *
   * @param value	the generator
   */
  public void setGenerator(RandomIntegerRangeGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the random number generator.
   *
   * @return		the generator
   */
  public RandomIntegerRangeGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The random number generator to use for selecting the elements.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "size", m_Size, "size: ");
    result += QuickInfoHelper.toString(this, "generator", m_Generator, ", generator: ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		arrayOld;
    Object		arrayNew;
    int			i;
    int			size;
    TIntArrayList	available;
    TIntArrayList	indices;
    
    result   = null;
    
    arrayOld = m_InputToken.getPayload();
    if (m_Size <= 1)
      size = (int) Math.round(Array.getLength(arrayOld) * m_Size);
    else
      size = (int) m_Size;
    if (isLoggingEnabled())
      getLogger().info("Size of sample: " + size);
    available = new TIntArrayList();
    for (i = 0; i < Array.getLength(arrayOld); i++)
      available.add(i);
    indices = new TIntArrayList();
    m_Generator.setMinValue(0);
    while (size > 0) {
      if (available.size() == 1) {
	i = 0;
      }
      else {
	m_Generator.setMaxValue(available.size() - 1);
	i = m_Generator.next().intValue();
      }
      indices.add(available.get(i));
      available.removeAt(i);
      size--;
    }
    indices.sort();
    if (isLoggingEnabled())
      getLogger().info("Indices: " + indices);
    
    arrayNew = Array.newInstance(arrayOld.getClass().getComponentType(), indices.size());
    for (i = 0; i < indices.size(); i++)
      Array.set(arrayNew, i, Utils.deepCopy(Array.get(arrayOld, indices.get(i))));

    m_OutputToken = new Token(arrayNew);
    
    return result;
  }
}
