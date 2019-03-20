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
 * ColorProvider.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.core.VariableMonitor;
import adams.gui.visualization.core.ColorProviderWithNameSupport;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Outputs a color for each token passing through.<br>
 * If the color provider implements adams.gui.visualization.core.ColorProviderWithNameSupport then the color associated with the incoming string token is forwarded.<br>
 * The color provider can be reset using the specified variable.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.awt.Color<br>
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
 * &nbsp;&nbsp;&nbsp;default: ColorProvider
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
 * <pre>-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: provider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for generating the colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to monitor.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColorProvider
  extends AbstractTransformer
  implements VariableMonitor {

  /** for serialization. */
  private static final long serialVersionUID = -3505768725369077351L;

  /** the key in the backup for the color provider. */
  public final static String BACKUP_PROVIDER = "provider";

  /** the color provider to use. */
  protected adams.gui.visualization.core.ColorProvider m_Provider;

  /** the actual color provider to use. */
  protected adams.gui.visualization.core.ColorProvider m_ActualProvider;

  /** the variable to listen to. */
  protected VariableName m_VariableName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs a color for each token passing through.\n"
      + "If the color provider implements " + Utils.classToString(ColorProviderWithNameSupport.class) + " "
      + "then the color associated with the incoming string token is forwarded.\n"
      + "The color provider can be reset using the specified variable.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "provider", "provider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "var-name", "variableName",
      new VariableName());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualProvider = null;
  }

  /**
   * Removes entries from the backup.
   *
   * @see		#reset()
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_PROVIDER);
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

    if (m_ActualProvider != null)
      result.put(BACKUP_PROVIDER, m_ActualProvider);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_PROVIDER)) {
      m_InputToken = (Token) state.get(BACKUP_PROVIDER);
      state.remove(BACKUP_PROVIDER);
    }

    super.restoreState(state);
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the provider
   */
  public void setProvider(adams.gui.visualization.core.ColorProvider value) {
    m_Provider = value;
    reset();
  }

  /**
   * Returns the color provider in use.
   *
   * @return		the provider
   */
  public adams.gui.visualization.core.ColorProvider getProvider() {
    return m_Provider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String providerTipText() {
    return "The color provider to use for generating the colors.";
  }

  /**
   * Sets the name of the variable to monitor.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to monitor.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The variable to monitor.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "provider", m_Provider);
    result += QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue(), ", variable: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Color.class};
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    super.variableChanged(e);
    if ((e.getType() == Type.MODIFIED) || (e.getType() == Type.ADDED)) {
      if (e.getName().equals(m_VariableName.getValue())) {
        if (m_ActualProvider != null) {
          m_ActualProvider.resetColors();
	  if (isLoggingEnabled())
	    getLogger().info("Color provider reset");
	}
      }
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	input;

    result = null;
    input  = m_InputToken.getPayload();

    if (m_ActualProvider == null)
      m_ActualProvider = m_Provider.shallowCopy();

    if ((m_ActualProvider instanceof ColorProviderWithNameSupport) && (input instanceof String))
      m_OutputToken = new Token(((ColorProviderWithNameSupport) m_ActualProvider).next((String) input));
    else
      m_OutputToken = new Token(m_ActualProvider.next());

    return result;
  }
}
