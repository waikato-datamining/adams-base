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
 * UpdateID.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.idupdate.IDUpdater;
import adams.data.idupdate.PassThrough;
import adams.flow.core.Token;

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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class UpdateID
  extends AbstractTransformer {

  private static final long serialVersionUID = -2978040822861434285L;

  /** the group updater to use. */
  protected IDUpdater m_Updater;

  /** the new ID. */
  protected String m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the ID of the object passing through.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "updater", "updater",
      new PassThrough());

    m_OptionManager.add(
      "id", "ID",
      "");
  }

  /**
   * Sets the ID updater to use.
   *
   * @param value	the updater
   */
  public void setUpdater(IDUpdater value) {
    m_Updater = value;
    reset();
  }

  /**
   * Returns the ID updater to use.
   *
   * @return		the updater
   */
  public IDUpdater getUpdater() {
    return m_Updater;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updaterTipText() {
    return "The scheme to use for updating the ID.";
  }

  /**
   * Sets the new ID to use.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the new ID to use.
   *
   * @return		the new ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The new ID to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "updater", m_Updater, "updater: ");
    result += QuickInfoHelper.toString(this, "ID", (m_ID.isEmpty() ? "-none-" : m_ID), ", ID: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Object.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	obj;

    result = null;

    obj = m_InputToken.getPayload();
    if (!m_Updater.handles(obj))
      result = m_InputToken.unhandledData();

    if (result == null) {
      try {
	result        = m_Updater.updateID(obj, m_ID);
	m_OutputToken = new Token(obj);
      }
      catch (Exception e) {
	result = handleException("Failed to update ID of: " + m_InputToken.getPayload(), e);
      }
    }

    return result;
  }
}
