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
 * ErrorMessagePanel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import adams.gui.core.ConsolePanel.PanelType;
import adams.gui.dialog.TextPanel;

/**
 * A panel for displaying an error message, optionally being able to include
 * the console output.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ErrorMessagePanel
  extends BasePanel 
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = -5725086584842991503L;

  /** the error message itself. */
  protected String m_ErrorMessage;
  
  /** the text panel for displaying the error. */
  protected TextPanel m_TextPanel;

  /** the bottom panel with {@link BorderLayout}. */
  protected BasePanel m_PanelBottom;
  
  /** the checkbox for including the console output. */
  protected JCheckBox m_CheckBoxConsole;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ErrorMessage = "No error";
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();

    setLayout(new BorderLayout());
    
    m_TextPanel = new TextPanel();
    m_TextPanel.setTitle("Error");
    m_TextPanel.setEditable(false);
    m_TextPanel.setLineWrap(true);
    add(m_TextPanel, BorderLayout.CENTER);
    
    m_PanelBottom = new BasePanel(new BorderLayout());
    add(m_PanelBottom, BorderLayout.SOUTH);
    
    m_CheckBoxConsole = new JCheckBox("Console output");
    m_CheckBoxConsole.setSelected(false);
    m_CheckBoxConsole.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	update();
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_CheckBoxConsole);
    m_PanelBottom.add(panel, BorderLayout.WEST);
  }
  
  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   * 
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    return m_TextPanel.getMenuBar();
  }
  
  /**
   * Returns the bottom panel, i.e., the one holding the checkbox.
   * 
   * @return		the panel
   * @see		#m_CheckBoxConsole
   * @see		#m_PanelBottom
   */
  public BasePanel getBottomPanel() {
    return m_PanelBottom;
  }
  
  /**
   * Returns the "console" checkbox.
   * 
   * @return		the checkbox
   * @see		#m_CheckBoxConsole
   */
  public JCheckBox getConsoleCheckBox() {
    return m_CheckBoxConsole;
  }
  
  /**
   * Sets the title to use.
   * 
   * @param value	the title
   */
  public void setTitle(String value) {
    m_TextPanel.setTitle(value);
  }
  
  /**
   * Returns the current title.
   * 
   * @return		the title
   */
  public String getTitle() {
    return m_TextPanel.getTitle();
  }
  
  /**
   * Sets the error message and updates the display.
   * 
   * @param value	the error message to display
   */
  public void setErrorMessage(String value) {
    m_ErrorMessage = value;
    update();
  }
  
  /**
   * Returns the error message.
   * 
   * @return		the error message
   */
  public String getErrorMessage() {
    return m_ErrorMessage;
  }

  /**
   * Enables/disables line wrap.
   *
   * @param value	if true line wrap gets enabled
   */
  public void setLineWrap(boolean value) {
    m_TextPanel.setLineWrap(value);
  }

  /**
   * Returns whether line wrap is enabled.
   *
   * @return		true if line wrap enabled
   */
  public boolean getLineWrap() {
    return m_TextPanel.getLineWrap();
  }

  /**
   * Updates the display.
   */
  protected void update() {
    if (m_CheckBoxConsole.isSelected())
      m_TextPanel.setContent(
	  m_ErrorMessage 
	  + "\n\n--- Console output ---\n\n" 
	  + ConsolePanel.getSingleton().getPanel(PanelType.ALL).getContent());
    else
      m_TextPanel.setContent(
	  m_ErrorMessage);
  }
}
