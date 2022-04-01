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
 * ImageObjectToVariables.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;

/**
 <!-- globalinfo-start -->
 * Converts the parameters of an image object into variables.<br>
 * Meta-data can be turned into variables as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.transformer.locateobjects.LocatedObject<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.transformer.locateobjects.LocatedObject<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageObjectToVariables
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
 * <pre>-variable-prefix &lt;java.lang.String&gt; (property: variablePrefix)
 * &nbsp;&nbsp;&nbsp;The string to prefix the variable name with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-include-metadata &lt;boolean&gt; (property: includeMetaData)
 * &nbsp;&nbsp;&nbsp;If enabled, the metadata gets turned into variables as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-metadata-variable-prefix &lt;java.lang.String&gt; (property: metaDataVariablePrefix)
 * &nbsp;&nbsp;&nbsp;The string to prefix the variable name with for metadata values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectToVariables
    extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -184602726110144511L;

  public final static String KEY_X = "x";

  public final static String KEY_Y = "y";

  public final static String KEY_WIDTH = "width";

  public final static String KEY_HEIGHT = "height";

  /** the prefix for the variables. */
  protected String m_VariablePrefix;

  /** whether to include the metadata as well. */
  protected boolean m_IncludeMetaData;

  /** the prefix for the variables. */
  protected String m_MetaDataVariablePrefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Converts the parameters of an image object into variables.\n"
            + "Meta-data can be turned into variables as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "variable-prefix", "variablePrefix",
        "");

    m_OptionManager.add(
        "include-metadata", "includeMetaData",
        false);

    m_OptionManager.add(
        "metadata-variable-prefix", "metaDataVariablePrefix",
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
    String	value;

    result = QuickInfoHelper.toString(this, "variablePrefix", (m_VariablePrefix.isEmpty() ? "-none-" : m_VariablePrefix), "prefix: ");
    result += QuickInfoHelper.toString(this, "includeMetaData", m_IncludeMetaData, "include metadata", ", ");
    if (m_IncludeMetaData)
      result += QuickInfoHelper.toString(this, "metaDataVariablePrefix", (m_MetaDataVariablePrefix.isEmpty() ? "-none-" : m_MetaDataVariablePrefix), ", metadata prefix: ");

    return result;
  }

  /**
   * Sets the prefix for the variables (prefix + name).
   *
   * @param value	the prefix
   */
  public void setVariablePrefix(String value) {
    m_VariablePrefix = value;
    reset();
  }

  /**
   * Returns the prefix for the variables (prefix + name).
   *
   * @return		the prefix
   */
  public String getVariablePrefix() {
    return m_VariablePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablePrefixTipText() {
    return "The string to prefix the variable name with.";
  }

  /**
   * Sets whether to turn the metadata values into variables as well.
   *
   * @param value	true if to include
   */
  public void setIncludeMetaData(boolean value) {
    m_IncludeMetaData = value;
    reset();
  }

  /**
   * Returns whether to turn the metadata values into variables as well.
   *
   * @return		true if to include
   */
  public boolean getIncludeMetaData() {
    return m_IncludeMetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String includeMetaDataTipText() {
    return "If enabled, the metadata gets turned into variables as well.";
  }

  /**
   * Sets the prefix for the metadata variables (prefix + name).
   *
   * @param value	the prefix
   */
  public void setMetaDataVariablePrefix(String value) {
    m_MetaDataVariablePrefix = value;
    reset();
  }

  /**
   * Returns the prefix for the metadata variables (prefix + name).
   *
   * @return		the prefix
   */
  public String getMetaDataVariablePrefix() {
    return m_MetaDataVariablePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataVariablePrefixTipText() {
    return "The string to prefix the variable name with for metadata values.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{LocatedObject.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{LocatedObject.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    LocatedObject 	object;
    String		name;
    boolean		exists;
    Object		value;
    Variables		vars;

    result = null;

    object = null;
    if (m_InputToken.getPayload() instanceof LocatedObject)
      object = (LocatedObject) m_InputToken.getPayload();
    else
      result = m_InputToken.unhandledData();

    if ((result == null) && (object != null)) {
      vars = getVariables();
      vars.set(m_VariablePrefix + KEY_X,      "" + object.getX());
      vars.set(m_VariablePrefix + KEY_Y,      "" + object.getY());
      vars.set(m_VariablePrefix + KEY_WIDTH,  "" + object.getWidth());
      vars.set(m_VariablePrefix + KEY_HEIGHT, "" + object.getHeight());
      if (m_IncludeMetaData) {
        for (String key: object.getMetaData().keySet())
          vars.set(m_MetaDataVariablePrefix + key, "" + object.getMetaData().get(key));
      }
    }

    m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}
