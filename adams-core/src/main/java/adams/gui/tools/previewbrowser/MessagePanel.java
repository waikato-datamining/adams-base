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
 * AbstractMessagePanel.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.net.HtmlUtils;
import adams.gui.core.BasePanel;

import javax.swing.JLabel;
import java.awt.BorderLayout;

/**
 * Allows the display of a message. Automatically converts text with multiple
 * lines into HTML to properly display it.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MessagePanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -842675700403438848L;

  /** the label for displaying the message. */
  protected JLabel m_LabelMessage;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());
    m_LabelMessage = new JLabel("", JLabel.CENTER);
    add(m_LabelMessage, BorderLayout.CENTER);
  }

  /**
   * Sets the message to display.
   *
   * @param value	the message
   */
  public void setMessage(String value) {
    if (value.contains("\n") && !value.toLowerCase().startsWith("<html>")) {
      value = HtmlUtils.toHTML(value);
      value = HtmlUtils.convertLines(value, true);
      value = "<html>" + value + "</html>";
    }
    m_LabelMessage.setText(value);
  }

  /**
   * Returns the currently displayed message.
   *
   * @return		the message
   */
  public String getMessage() {
    return m_LabelMessage.getText();
  }
}
