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
 * DescriptionPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Base panel that offers a description at the top.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DescriptionPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -8706194264102837649L;

  /** the description of the page. */
  protected BaseTextPane m_TextDescription;
  
  /** the scroll pane for the description. */
  protected BaseScrollPane m_ScrollPaneDescription;

  /**
   * Initializes the panel.
   */
  public DescriptionPanel() {
    super();
  }

  /**
   * Initializes the panel with the given layout.
   *
   * @param manager	the layout manager to use
   */
  public DescriptionPanel(LayoutManager manager) {
    super(manager);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TextDescription = new BaseTextPane();
    m_TextDescription.setEditable(false);
    setDescriptionHeight(100);
    
    m_ScrollPaneDescription = new BaseScrollPane(m_TextDescription);
    add(m_ScrollPaneDescription, BorderLayout.NORTH);
  }

  /**
   * Sets the description of the page.
   * 
   * @param value	the description
   * @param isHtml	if true then text is interpreted as HTML 3
   */
  public void setDescription(String value, boolean isHtml) {
    if (isHtml)
      m_TextDescription.setEditorKit(new HTMLEditorKit());
    else
      m_TextDescription.setEditorKit(new StyledEditorKit());
    m_TextDescription.setText(value);
  }
  
  /**
   * Returns the description of the page.
   * 
   * @return		the description (may be html)
   */
  public String getDescription() {
    return m_TextDescription.getText();
  }
  
  /**
   * Sets the preferred height of the description box.
   * 
   * @param value	the new height in pixel
   */
  public void setDescriptionHeight(int value) {
    m_TextDescription.setPreferredSize(new Dimension(0, value));
  }
  
  /**
   * Returns the preferred height of the description box.
   * 
   * @return		the height in pixel
   */
  public int getDescriptionHeight() {
    return m_TextDescription.getPreferredSize().height;
  }
}
