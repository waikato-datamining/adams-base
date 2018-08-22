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
 * HtmlDisplay.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.HtmlPane;

import javax.swing.JComponent;

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
public class HTMLDisplay
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  private static final long serialVersionUID = -7643175608274064631L;

  /** the panel with the HTML content. */
  protected HtmlPane m_Panel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For displaying HTML.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_Panel.setText(token.getPayload(String.class));
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_Panel.setText("");
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_Panel = new HtmlPane();
    return m_Panel;
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractComponentDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 8813993014507662967L;
      protected HtmlPane m_Panel;
      @Override
      public JComponent supplyComponent() {
	return m_Panel;
      }
      @Override
      public void display(Token token) {
        m_Panel = new HtmlPane();
        m_Panel.setText(token.getPayload(String.class));
      }
      @Override
      public void clearPanel() {
        if (m_Panel != null)
	  m_Panel.setText("");
      }
      @Override
      public void cleanUp() {
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }
}
