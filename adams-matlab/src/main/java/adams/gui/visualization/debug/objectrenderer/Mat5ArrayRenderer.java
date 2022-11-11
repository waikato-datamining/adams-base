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
 * MatlabArrayRenderer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.data.matlab.MatlabUtils;
import adams.gui.core.Fonts;
import adams.gui.dialog.TextPanel;
import nz.ac.waikato.cms.locator.ClassLocator;
import us.hebi.matlab.mat.types.Array;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Simply outputs the array dimensions.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5ArrayRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -2309332191934879382L;

  /** the last setup. */
  protected TextPanel m_LastTextPanel;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls the class to check
   * @return true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Array.class, cls);
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
   * Renders the object.
   *
   * @param obj		the object to render
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		the generated string
   */
  protected String render(Object obj, Integer limit) {
    return MatlabUtils.arrayDimensionsToString((Array) obj);
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRenderCached(Object obj, JPanel panel, Integer limit) {
    m_LastTextPanel.setContent(render(obj, limit));
    panel.add(m_LastTextPanel, BorderLayout.CENTER);
    return null;
  }

  /**
   * Performs the actual rendering with a new renderer setup.
   *
   * @param obj   the object to render
   * @param panel the panel to render into
   * @param limit the limit to use for the rendering (if applicable), ignored if null
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel, Integer limit) {
    TextPanel 			textPanel;

    textPanel = new TextPanel();
    textPanel.setTextFont(Fonts.getMonospacedFont());
    textPanel.setCanOpenFiles(false);
    textPanel.setUpdateParentTitle(false);
    textPanel.setContent(render(obj, limit));
    panel.add(textPanel, BorderLayout.CENTER);

    m_LastTextPanel = textPanel;

    return null;
  }
}
