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
 * PlainTextRenderer.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.data.textrenderer.AbstractTextRenderer;
import adams.gui.core.Fonts;
import adams.gui.dialog.TextPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Renders objects as plain text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PlainTextRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /** the last setup. */
  protected TextPanel m_LastTextPanel;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return true;
  }

  /**
   * Checks whether the renderer can use a cached setup to render an object.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		true if possible
   */
  @Override
  public boolean canRenderCached(Object obj, JPanel panel) {
    return (m_LastTextPanel != null);
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRenderCached(Object obj, JPanel panel) {
    m_LastTextPanel.setContent(AbstractTextRenderer.renderObject(obj));
    panel.add(m_LastTextPanel, BorderLayout.CENTER);
    return null;
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel) {
    TextPanel 			textPanel;

    textPanel = new TextPanel();
    textPanel.setTextFont(Fonts.getMonospacedFont());
    textPanel.setCanOpenFiles(false);
    textPanel.setUpdateParentTitle(false);
    textPanel.setContent(AbstractTextRenderer.renderObject(obj));
    panel.add(textPanel, BorderLayout.CENTER);

    m_LastTextPanel = textPanel;

    return null;
  }
}
