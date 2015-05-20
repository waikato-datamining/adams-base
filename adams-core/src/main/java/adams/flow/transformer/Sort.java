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
 * Sort.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import adams.core.DefaultCompare;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Actor for sorting arrays.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: Sort
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
 * <pre>-reverse &lt;boolean&gt; (property: reverse)
 * &nbsp;&nbsp;&nbsp;If enabled, then the sorting will be reversed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-unique &lt;boolean&gt; (property: unique)
 * &nbsp;&nbsp;&nbsp;If enabled, then only unique entries will be output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-use-comparator &lt;boolean&gt; (property: useComparator)
 * &nbsp;&nbsp;&nbsp;If enabled, the selected comparator is used for sorting.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-comparator &lt;java.util.Comparator&gt; (property: comparator)
 * &nbsp;&nbsp;&nbsp;The comparator to use; must implement java.util.Comparator and java.io.Serializable
 * &nbsp;&nbsp;&nbsp;default: adams.core.DefaultCompare
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Sort
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -1405106607250617855L;

  /** reverse sorting. */
  protected boolean m_Reverse;

  /** unqiue entries. */
  protected boolean m_Unique;

  /** whether to use a specific {@link Comparator} for sorting. */
  protected boolean m_UseComparator;
  
  /** the comparator to use. */
  protected Comparator m_Comparator;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for sorting arrays.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reverse", "reverse",
	    false);

    m_OptionManager.add(
	    "unique", "unique",
	    false);

    m_OptionManager.add(
	    "use-comparator", "useComparator",
	    false);

    m_OptionManager.add(
	    "comparator", "comparator",
	    new DefaultCompare());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "reverse", m_Reverse, "order reversed"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "unique", m_Unique, "unique"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useComparator", m_UseComparator, "comparator"));
    result = QuickInfoHelper.flatten(options);

    if (result.length() == 0)
      return null;
    else
      return result;
  }

  /**
   * Sets whether to reverse the sorting.
   *
   * @param value	if true then the sorting will be reversed
   */
  public void setReverse(boolean value) {
    m_Reverse = value;
    reset();
  }

  /**
   * Returns whether the sorting is reversed.
   *
   * @return		true if reversed
   */
  public boolean getReverse() {
    return m_Reverse;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reverseTipText() {
    return "If enabled, then the sorting will be reversed.";
  }

  /**
   * Sets whether to allow only unique entries.
   *
   * @param value	if true then only unique entries are output
   */
  public void setUnique(boolean value) {
    m_Unique = value;
    reset();
  }

  /**
   * Returns whether only unique entries are output.
   *
   * @return		true if only unique entries are output
   */
  public boolean getUnique() {
    return m_Unique;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String uniqueTipText() {
    return "If enabled, then only unique entries will be output.";
  }

  /**
   * Sets whether to use a custom comparator for sorting.
   *
   * @param value	if true then the selected comparator is used
   */
  public void setUseComparator(boolean value) {
    m_UseComparator = value;
    reset();
  }

  /**
   * Returns whether to use a custom comparator for sorting.
   *
   * @return		true if to use comparator
   */
  public boolean getUseComparator() {
    return m_UseComparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useComparatorTipText() {
    return "If enabled, the selected comparator is used for sorting.";
  }

  /**
   * Sets the comparator to use.
   *
   * @param value	the comparator
   */
  public void setComparator(Comparator value) {
    m_Comparator = value;
    reset();
  }

  /**
   * Returns the comparator to use.
   *
   * @return		the comparator
   */
  public Comparator getComparator() {
    return m_Comparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String comparatorTipText() {
    return "The comparator to use; must implement " + Comparator.class.getName() + " and " + Serializable.class.getName();
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
    String	result;
    Object[]	obj;
    Object[]	newObj;
    HashSet	entries;
    int		i;

    result = null;

    if (!m_InputToken.getPayload().getClass().isArray()) {
      getLogger().severe("Input is not an array - ignored!");
      m_OutputToken = new Token(m_InputToken.getPayload());
    }
    else {
      obj = (Object[]) Utils.deepCopy(m_InputToken.getPayload());
      if (isLoggingEnabled())
	getLogger().info("unsorted: " + Utils.arrayToString(obj));

      // no duplicates?
      if (m_Unique) {
	entries = new HashSet(Arrays.asList(obj));
	obj     = (Object[]) Array.newInstance(obj.getClass().getComponentType(), entries.size());
	i       = 0;
	for (Object o: entries) {
	  Array.set(obj, i, o);
	  i++;
	}
	if (isLoggingEnabled())
	  getLogger().info("unique: " + Utils.arrayToString(obj));
      }

      // sort
      if (m_UseComparator)
	Arrays.sort(obj, m_Comparator);
      else
	Arrays.sort(obj);
      if (!m_Reverse) {
	newObj = obj;
      }
      else {
	newObj = (Object[]) Array.newInstance(obj.getClass().getComponentType(), obj.length);
	for (i = 0; i < obj.length; i++)
	  newObj[i] = obj[obj.length - i - 1];
      }
      if (isLoggingEnabled())
	getLogger().info("sorted: " + Utils.arrayToString(newObj));

      m_OutputToken = new Token(newObj);
    }

    return result;
  }
}
