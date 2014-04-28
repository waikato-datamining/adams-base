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
 * BaseTextPaneWithWordWrap.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.text.Document;

/**
 * A panel containing a {@link BaseTextPane}, to allow wordwrap functionality.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTextPaneWithWordWrap
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 1382187615423454310L;

  /** the wrapped text pane. */
  protected BaseTextPane m_TextPane;
  
  /** the wrapper panel. */
  protected JPanel m_PanelWrapper;
  
  /** the scroll pane. */
  protected BaseScrollPane m_ScrollPane;
  
  /** whether wordwrap is on. */
  protected boolean m_WordWrap;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_WordWrap = false;
  }
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_PanelWrapper = new JPanel(new BorderLayout());
    m_ScrollPane = new BaseScrollPane(m_PanelWrapper);
    add(m_ScrollPane, BorderLayout.CENTER);
    
    m_TextPane = new BaseTextPane();
    m_PanelWrapper.add(m_TextPane, BorderLayout.CENTER);
  }
  
  /**
   * Returns the underlying {@link BaseTextPane}.
   * 
   * @return		the pane
   */
  public BaseTextPane getTextPane() {
    return m_TextPane;
  }

  /**
   * Returns the underlying document.
   *
   * @return		the document
   */
  public Document getDocument() {
    return m_TextPane.getDocument();
  }
  
  /**
   * Sets the wordwrap state.
   * 
   * @param value	true if to turn wordwrap on
   */
  public void setWordWrap(boolean value) {
    m_WordWrap = value;
    if (!m_WordWrap) {
      m_ScrollPane.setViewportView(m_PanelWrapper);
      m_PanelWrapper.add(m_TextPane, BorderLayout.CENTER);
    }
    else {
      m_ScrollPane.setViewportView(m_TextPane);
    }
  }
  
  /**
   * Returns whether wordwrap is on.
   * 
   * @return		true if wordwrap is on
   */
  public boolean getWordWrap() {
    return m_WordWrap;
  }
}
