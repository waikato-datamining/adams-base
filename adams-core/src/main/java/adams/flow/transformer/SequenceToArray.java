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
 * SequenceToArray.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Turns a sequence of tokens into arrays with a specified length.<br/>
 * In case of unspecified length (ie -1), an array containing all elements collected so far is output each time a token arrives, i.e., the internal buffer never gets reset.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SequenceToArray
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-length &lt;int&gt; (property: arrayLength)
 * &nbsp;&nbsp;&nbsp;The length of the output array; use -1 to output an array with all collected 
 * &nbsp;&nbsp;&nbsp;elements so far whenever a token arrives.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-array-class &lt;java.lang.String&gt; (property: arrayClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the array; if none is specified, the class of the first 
 * &nbsp;&nbsp;&nbsp;element is used.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequenceToArray
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8411367398473311627L;

  /** the key for storing the current elements in the backup. */
  public final static String BACKUP_ELEMENTS = "elements";

  /** the buffered elements of the array that still need to be broadcasted. */
  protected List m_Elements;

  /** the length of the arrays. */
  protected int m_ArrayLength;

  /** the class for the array. */
  protected String m_ArrayClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns a sequence of tokens into arrays with a specified length.\n"
	+ "In case of unspecified length (ie -1), an array containing all "
	+ "elements collected so far is output each time a token arrives, "
	+ "i.e., the internal buffer never gets reset.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "length", "arrayLength",
	    1, -1, null);

    m_OptionManager.add(
	    "array-class", "arrayClass",
	    "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "arrayLength", m_ArrayLength, "Length: ");
    result += QuickInfoHelper.toString(this, "arrayClass", (m_ArrayClass.length() != 0 ? m_ArrayClass : "-from 1st element-"), ", Class: ");

    return result;
  }

  /**
   * Sets the length of the arrays.
   *
   * @param value	the length
   */
  public void setArrayLength(int value) {
    if ((value > 0) || (value == -1)) {
      m_ArrayLength = value;
      reset();
    }
    else {
      getLogger().severe("Arrays must have a length of at least 1 (or -1 for unspecified length), provided: " + value + "!");
    }
  }

  /**
   * Returns the length of the arrays.
   *
   * @return		the length
   */
  public int getArrayLength() {
    return m_ArrayLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String arrayLengthTipText() {
    return "The length of the output array; use -1 to output an array with all collected elements so far whenever a token arrives.";
  }

  /**
   * Sets the class for the array.
   *
   * @param value	the classname, use empty string to use class of first
   * 			element
   */
  public void setArrayClass(String value) {
    m_ArrayClass = value;
    reset();
  }

  /**
   * Returns the class for the array.
   *
   * @return		the classname, empty string if class of first element
   * 			is used
   */
  public String getArrayClass() {
    return m_ArrayClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String arrayClassTipText() {
    return
        "The class to use for the array; if none is specified, the class of "
      + "the first element is used.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_ELEMENTS);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_ELEMENTS, m_Elements);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_ELEMENTS)) {
      m_Elements = (List) state.get(BACKUP_ELEMENTS);
      state.remove(BACKUP_ELEMENTS);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Elements    = new ArrayList();
    m_OutputToken = null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
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
    String	result;
    int		i;
    Object	array;

    result = null;

    try {
      m_Elements.add(m_InputToken.getPayload());
      getLogger().info("Buffered elements: " + m_Elements.size());
      if ((m_ArrayLength == -1) || (m_Elements.size() == m_ArrayLength)) {
	if (m_ArrayClass.length() == 0)
	  array = Array.newInstance(m_Elements.get(0).getClass(), m_Elements.size());
	else
	  array = Utils.newArray(m_ArrayClass, m_Elements.size());
	getLogger().info("Array type: " + array.getClass().getComponentType());
	for (i = 0; i < m_Elements.size(); i++)
	  Array.set(array, i, m_Elements.get(i));
	m_OutputToken = new Token(array);
	if (m_ArrayLength > -1)
	  m_Elements.clear();
	getLogger().info("Array generated");
      }
    }
    catch (Exception e) {
      result = handleException("Failed to turn sequence into array: ", e);
    }

    return result;
  }
}
