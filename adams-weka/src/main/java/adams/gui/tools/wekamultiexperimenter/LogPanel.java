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
 * LogPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The log panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogPanel
  extends AbstractExperimenterPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7809897225003422111L;

  /** the text area for outputting the log. */
  protected BaseTextArea m_TextArea;
  
  /** the button for clearing the log. */
  protected JButton m_ButtonClear;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();
    
    m_TextArea = new BaseTextArea();
    m_TextArea.setFont(Fonts.getMonospacedFont());
    add(new BaseScrollPane(m_TextArea));
    
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);
    
    m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
    m_ButtonClear.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    panel.add(m_ButtonClear);
  }
  
  /**
   * Clears the content.
   */
  public void clear() {
    synchronized(m_TextArea) {
      m_TextArea.setText("");
    }
  }
  
  /**
   * Appends the log message.
   * 
   * @param msg		the log message
   */
  public void append(String msg) {
    synchronized(m_TextArea) {
      m_TextArea.append(msg + "\n");
      m_TextArea.setCaretPosition(m_TextArea.getText().length());
    }
  }

  /**
   * Returns the icon to use in the tabbed pane.
   *
   * @return		the icon
   */
  public Icon getTabIcon() {
    return GUIHelper.getIcon("log.gif");
  }
}
