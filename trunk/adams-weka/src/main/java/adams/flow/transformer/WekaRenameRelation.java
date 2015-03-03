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
 * WekaRenameRelation.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Modifies relation names.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br/>
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
 * &nbsp;&nbsp;&nbsp;default: Rename
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
 * <pre>-find &lt;java.lang.String&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The regular expression to replace (use '[\s\S]+' to match whole string).
 * &nbsp;&nbsp;&nbsp;default: ([\\s\\S]+)
 * </pre>
 *
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The replacement string.
 * &nbsp;&nbsp;&nbsp;default: $0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaRenameRelation
  extends AbstractWekaInstanceAndWekaInstancesTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 5071747277597147724L;

  /** the string to find. */
  protected String m_Find;

  /** the string to replace with. */
  protected String m_Replace;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Modifies relation names.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "find", "find",
	    "([\\s\\S]+)");

    m_OptionManager.add(
	    "replace", "replace",
	    "$0");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "find", m_Find, "find: ");
    result += QuickInfoHelper.toString(this, "replace", m_Replace, ", replace: ");

    return result;
  }

  /**
   * Sets the string to find.
   *
   * @param value	the string
   */
  public void setFind(String value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the string to find.
   *
   * @return		the string
   */
  public String getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The regular expression to replace (use '[\\s\\S]+' to match whole string).";
  }

  /**
   * Sets the replacement string.
   *
   * @param value	the string
   */
  public void setReplace(String value) {
    m_Replace = value;
    reset();
  }

  /**
   * Returns the replacement string.
   *
   * @return		the string
   */
  public String getReplace() {
    return m_Replace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The replacement string.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    weka.core.Instance			inst;
    weka.core.Instances			data;
    adams.data.instance.Instance	instA;
    String				oldName;
    String				newName;

    result = null;

    if (m_InputToken.getPayload() instanceof weka.core.Instance) {
      inst = (weka.core.Instance) m_InputToken.getPayload();
      data = inst.dataset();
    }
    else if (m_InputToken.getPayload() instanceof adams.data.instance.Instance) {
      inst = ((adams.data.instance.Instance) m_InputToken.getPayload()).toInstance();
      data = inst.dataset();
    }
    else {
      inst = null;
      data = (weka.core.Instances) m_InputToken.getPayload();
    }
    if (isLoggingEnabled())
      getLogger().info("Renaming: " + m_Find + " -> " + m_Replace);

    // perform rename
    if (data != null) {
      oldName = data.relationName();
      newName = oldName.replaceAll(m_Find, m_Replace);
      data.setRelationName(newName);
      if (isLoggingEnabled())
	getLogger().info("Renamed: " + oldName + " -> " + newName);
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("weka.core.Instance doesn't have access to dataset?");
    }

    if (inst == null) {
      m_OutputToken = new Token(data);
    }
    else {
      if (m_InputToken.getPayload() instanceof adams.data.instance.Instance) {
	instA = new adams.data.instance.Instance();
	instA.set(inst);
	m_OutputToken = new Token(instA);
      }
      else {
	m_OutputToken = new Token(inst);
      }
    }

    return result;
  }
}
