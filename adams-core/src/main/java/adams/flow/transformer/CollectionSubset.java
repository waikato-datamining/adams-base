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
 * CollectionSubset.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.Utils;
import adams.flow.core.Token;
import com.github.fracpete.javautils.enumerate.Enumerated;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Collection;
import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 <!-- globalinfo-start -->
 * Generates a subset of the collection, using the specified elements.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Collection<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Collection<br>
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
 * &nbsp;&nbsp;&nbsp;default: CollectionSubset
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
 * <pre>-elements &lt;adams.core.Range&gt; (property: elements)
 * &nbsp;&nbsp;&nbsp;The range of elements to pick from the collection.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CollectionSubset
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8536100625511019961L;

  /** the elements of the subset to extract. */
  protected Range m_Elements;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a subset of the collection, using the specified elements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "elements", "elements",
      new Range(Range.ALL));
  }

  /**
   * Sets the array elements to pick.
   *
   * @param value	the range of elements
   */
  public void setElements(Range value) {
    m_Elements = value;
    reset();
  }

  /**
   * Returns the array elements to pick.
   *
   * @return		the range of elements
   */
  public Range getElements() {
    return m_Elements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String elementsTipText() {
    return "The range of elements to pick from the collection.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "elements", m_Elements);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the classes
   */
  public Class[] accepts() {
    return new Class[]{Collection.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  public Class[] generates() {
    return new Class[]{Collection.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Collection  collOld;
    Collection  collNew;
    List	listOld;
    List	listNew;
    int[]	indices;
    TIntSet	set;

    result   = null;
    
    collOld = m_InputToken.getPayload(Collection.class);
    m_Elements.setMax(collOld.size());
    indices  = m_Elements.getIntIndices();
    collNew  = null;
    try {
      collNew = collOld.getClass().newInstance();
    }
    catch (Exception e) {
      result = handleException("Failed to create new collection instance of: " + Utils.classToString(collOld), e);
    }
    if (result == null) {
      if (collOld instanceof List) {
        listOld = (List) collOld;
        listNew = (List) collNew;
        for (int index: indices)
          listNew.add(listOld.get(index));
      }
      else {
        set = new TIntHashSet(indices);
        for (Enumerated<Object> o: enumerate(collOld.toArray())) {
	  if (set.contains(o.index))
	    collNew.add(o.value);
	}
      }
    }

    m_OutputToken = new Token(collNew);
    
    return result;
  }
}
