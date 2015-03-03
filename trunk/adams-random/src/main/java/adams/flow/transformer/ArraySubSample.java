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
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates a subset of the array, using a random sub-sample.
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ArraySubSample
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-size &lt;double&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;The size of the sample: 0-1 = percentage, &gt;1 absolute number of elements.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-generator &lt;adams.data.random.RandomIntegerRangeGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The random number generator to use for selecting the elements.
 * &nbsp;&nbsp;&nbsp;default: adams.data.random.JavaRandomInt
 * </pre>
 * 
 * <pre>-split-result &lt;SPLIT|INVERSE|BOTH&gt; (property: splitResult)
 * &nbsp;&nbsp;&nbsp;The type of data to return: e.g., the sample, the inverse of the sample 
 * &nbsp;&nbsp;&nbsp;or both (split and inverse).
 * &nbsp;&nbsp;&nbsp;default: SPLIT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7732 $
 */
public class ArraySubSample
  extends AbstractArraySplitter {

  /** for serialization. */
  private static final long serialVersionUID = 8536100625511019961L;

  /** the size of the sub-sample. */
  protected double m_Size;
  
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "size", m_Size, "size: ");
    result += ", " + super.getQuickInfo();
    
    return result;
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
    
    // randomize indices
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
    
    // create array(s)
    switch (m_SplitResult) {
      case SPLIT:
	indices.sort();
	arrayNew = newArray(arrayOld, indices, "split");
	m_OutputToken = new Token(arrayNew);
	break;
      case INVERSE:
	arrayNew = newArray(arrayOld, available, "inverse");
	m_OutputToken = new Token(arrayNew);
	break;
      case BOTH:
	indices.sort();
	arrayNew = Array.newInstance(arrayOld.getClass(), 2);
	Array.set(arrayNew, 0, newArray(arrayOld, indices, "split"));
	Array.set(arrayNew, 1, newArray(arrayOld, available, "inverse"));
	m_OutputToken = new Token(arrayNew);
	break;
      default:
	throw new IllegalStateException("Unhandled split result: " + m_SplitResult);
    }

    m_OutputToken = new Token(arrayNew);
    updateProvenance(m_OutputToken);
    
    return result;
  }
}
