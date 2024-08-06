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
 * WekaPackageManagerAction.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.transformer.wekapackagemanageraction.AbstractWekaPackageManagerAction;
import adams.flow.transformer.wekapackagemanageraction.InstallOfficial;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaPackageManagerAction
  extends AbstractTransformer {

  private static final long serialVersionUID = 7790018933824161813L;

  /** the action to execute. */
  protected AbstractWekaPackageManagerAction m_Action;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the selected Weka Package Manager action to the incoming data and forwards the generated output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "action", "action",
      new InstallOfficial());
  }

  /**
   * Sets the action to use.
   *
   * @param value	the action
   */
  public void setAction(AbstractWekaPackageManagerAction value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the action in use.
   *
   * @return		the action
   */
  public AbstractWekaPackageManagerAction getAction() {
    return m_Action;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actionTipText() {
    return "The action to execute.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "action", m_Action, "action: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    if (m_Action != null)
      return m_Action.accepts();
    else
      return new Class[0];
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (m_Action != null)
      return m_Action.generates();
    else
      return new Class[0];
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		input;
    Object		output;
    MessageCollection 	errors;

    result = null;
    input  = m_InputToken.getPayload();
    errors = new MessageCollection();
    m_Action.setFlowContext(this);
    output = m_Action.execute(input, errors);
    if (!errors.isEmpty())
      result = errors.toString();
    else if (output != null)
      m_OutputToken = new Token(output);

    return result;
  }
}
