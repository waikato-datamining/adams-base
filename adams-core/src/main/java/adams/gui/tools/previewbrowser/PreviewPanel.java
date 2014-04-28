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
 * PreviewPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import adams.gui.core.BasePanel;

/**
 * The ancestor for all preview panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreviewPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 3358752718380986817L;

  /** the actual content. */
  protected JComponent m_Content;

  /**
   * Initializes the panel (with {@link BorderLayout}) and uses the
   * given component as content and places it at {@link BorderLayout#CENTER}.
   */
  public PreviewPanel(JComponent content) {
    this(content, content);
  }

  /**
   * Initializes the panel (with {@link BorderLayout}). The specified component
   * gets added at {@link BorderLayout#CENTER} and the content parameter used
   * as actual content.
   */
  public PreviewPanel(JComponent component, JComponent content) {
    super(new BorderLayout());
    setContent(content);
    add(component, BorderLayout.CENTER);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
   super.initialize();
   
   setContent(this);
  }
  
  /**
   * Sets the actual content.
   * 
   * @return		the content
   */
  public void setContent(JComponent value) {
    m_Content = value;
  }
  
  /**
   * Returns the actual content. E.g., a BaseTextArea without the
   * surrounding BaseScrollPane.
   * <p/>
   * The default implementation returns simply this panel.
   * 
   * @return		the actual content.
   * @see		#m_Content
   */
  public JComponent getContent() {
    return m_Content;
  }
}
