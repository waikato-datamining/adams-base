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
 * WekaAttributeIterator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.base.BaseRegExp;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Iterates through all attributes of a dataset and outputs the names.<br>
 * The attributes can be limited with the range parameter and furthermore with the regular expression applied to the names.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaAttributeIterator
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
 * <pre>-range &lt;java.lang.String&gt; (property: range)
 * &nbsp;&nbsp;&nbsp;The range of attributes to iterate over; A range is a comma-separated list
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); the following
 * &nbsp;&nbsp;&nbsp;placeholders can be used as well: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp;last.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used to further limit the attribute set.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAttributeIterator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7689330704841468990L;

  /** the key for storing the names in the backup. */
  public final static String BACKUP_NAMES = "names";

  /** the range of attributes to work on. */
  protected Range m_Range;

  /** the regular expression applied to the attribute names. */
  protected BaseRegExp m_RegExp;

  /** the names of the attributes to output. */
  protected List<String> m_Names;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Iterates through all attributes of a dataset and outputs the names.\n"
      + "The attributes can be limited with the range parameter and "
      + "furthermore with the regular expression applied to the names.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "range", "range",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Range = new Range(Range.ALL);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Names = new ArrayList<String>();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "range", m_Range);
    value = QuickInfoHelper.toString(this, "regExp", m_RegExp, ", subset: ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the range of attributes to operate on.
   *
   * @param value	the range
   */
  public void setRange(Range value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the range of attributes to operate on.
   *
   * @return		the range
   */
  public Range getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The range of attributes to iterate over; " + m_Range.getExample() + ".";
  }

  /**
   * Sets the regular expression for the names.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for the name.
   *
   * @return		the prefix
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used to further limit the attribute set.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
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

    if (m_Names != null)
      result.put(BACKUP_NAMES, m_Names);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_NAMES)) {
      m_Names = (List<String>) state.get(BACKUP_NAMES);
      state.remove(BACKUP_NAMES);
    }

    super.restoreState(state);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instances	data;
    int[]	indices;
    String	name;
    boolean	useRegExp;

    result = null;

    if (m_InputToken.getPayload() instanceof Instance)
      data = ((Instance) m_InputToken.getPayload()).dataset();
    else
      data = ((Instances) m_InputToken.getPayload());

    m_Range.setMax(data.numAttributes());
    indices   = m_Range.getIntIndices();
    useRegExp = !m_RegExp.isEmpty() && !m_RegExp.isMatchAll();
    m_Names.clear();
    for (int index: indices) {
      name = data.attribute(index).name();
      if (useRegExp)
	if (!m_RegExp.isMatch(name))
	  continue;
      m_Names.add(name);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Names != null) && (m_Names.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = new Token(m_Names.get(0));
    m_Names.remove(0);

    m_InputToken = null;

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_Names = null;
  }
}
