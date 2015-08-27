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
 * CompareObjects.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.compare.AbstractObjectCompare;
import adams.data.compare.JavaComparable;
import adams.flow.core.Token;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Compares two objects using the specified compare algorithm and forwards the output of the comparison.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Comparable[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
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
 * &nbsp;&nbsp;&nbsp;default: CompareObjects
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
 * <pre>-compare &lt;adams.data.compare.AbstractObjectCompare&gt; (property: compare)
 * &nbsp;&nbsp;&nbsp;The algorithm to use for comparing the objects.
 * &nbsp;&nbsp;&nbsp;default: adams.data.compare.JavaComparable
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CompareObjects
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** the compare algorithm to use. */
  protected AbstractObjectCompare m_Compare;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Compares two objects using the specified compare algorithm and "
	+ "forwards the output of the comparison.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "compare", "compare",
      new JavaComparable());
  }

  /**
   * Sets the comparison algorithm.
   *
   * @param value	the algorithm
   */
  public void setCompare(AbstractObjectCompare value) {
    m_Compare = value;
    reset();
  }

  /**
   * Returns the comparison algorithm.
   *
   * @return		the algorithm
   */
  public AbstractObjectCompare getCompare() {
    return m_Compare;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String compareTipText() {
    return "The algorithm to use for comparing the objects.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Comparable[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    List<Class>		result;
    Class[]		classes;
    Class		arrayCls;

    result  = new ArrayList<>();
    classes = m_Compare.accepts();
    for (Class cls: classes) {
      arrayCls = Array.newInstance(cls, 0).getClass();
      result.add(arrayCls);
    }

    return result.toArray(new Class[result.size()]);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{m_Compare.generates()};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "compare", m_Compare);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	array;
    Object	comp;

    result = null;

    array  =  m_InputToken.getPayload();
    if (!array.getClass().isArray())
      result = "Input is not an array: " + Utils.classToString(array.getClass());
    else if (Array.getLength(array) != 2)
      result = "Input array must be of length two, provided: " + Array.getLength(array);

    if (result == null) {
      try {
	comp = m_Compare.compareObjects(Array.get(array, 0), Array.get(array, 1));
	if (comp != null)
	  m_OutputToken = new Token(comp);
      }
      catch (Exception e) {
	result = handleException("Failed to compare objects: " + Utils.arrayToString(array), e);
      }
    }

    return result;
  }
}
