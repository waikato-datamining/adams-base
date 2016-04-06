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

/**
 * Text.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.core.base.BaseText;
import adams.scripting.command.AbstractFlowAwareCommand;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * Just sends some text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Text
  extends AbstractFlowAwareCommand {

  private static final long serialVersionUID = -1657908444959620122L;

  /** the text to send. */
  protected BaseText m_Text;

  /** the actual payload. */
  protected String m_Payload;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Simply sends the supplied text.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "text", "text",
      new BaseText());
  }

  /**
   * Sets the text to send.
   *
   * @param value	the text
   */
  public void setText(BaseText value) {
    m_Text = value;
    reset();
  }

  /**
   * Returns the text to send.
   *
   * @return		the text
   */
  public BaseText getText() {
    return m_Text;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String textTipText() {
    return "The text to send.";
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
    if (value.length == 0) {
      m_Payload = "";
      return;
    }

    m_Payload = new String(value);
  }

  /**
   * Hook method for preparing the request payload,
   */
  protected void prepareRequestPayload() {
    m_Payload = m_Text.getValue();
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return m_Payload.getBytes();
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[]{m_Payload};
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @return		null if successful, otherwise error message
   */
  protected String doHandleRequest(RemoteScriptingEngine engine) {
    getLogger().info(m_Payload);
    return null;
  }
}
