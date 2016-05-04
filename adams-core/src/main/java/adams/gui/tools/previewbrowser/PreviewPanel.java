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
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.CleanUpHandler;
import adams.gui.core.BasePanel;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 * The ancestor for all preview panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreviewPanel
  extends BasePanel
  implements CleanUpHandler {

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
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
   * @param value	the content
   */
  public void setContent(JComponent value) {
    m_Content = value;
  }
  
  /**
   * Returns the actual content. E.g., a BaseTextArea without the
   * surrounding BaseScrollPane.
   * <br><br>
   * The default implementation returns simply this panel.
   * 
   * @return		the actual content.
   * @see		#m_Content
   */
  public JComponent getContent() {
    return m_Content;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_Content instanceof CleanUpHandler)
      ((CleanUpHandler) m_Content).cleanUp();
    if (getComponent(0) instanceof CleanUpHandler)
      ((CleanUpHandler) getComponent(0)).cleanUp();
  }
}
