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
 * ImageRenderer.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.data.image.BufferedImageSupporter;
import adams.data.report.ReportHandler;
import adams.gui.visualization.image.ImagePanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

/**
 * Renders image objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /** the cached setup. */
  protected ImagePanel m_LastImagePanel;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return
         ClassLocator.hasInterface(BufferedImageSupporter.class, cls)
      || ClassLocator.isSubclass(BufferedImage.class, cls);
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
    return (m_LastImagePanel != null);
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
    if (obj instanceof BufferedImageSupporter)
      m_LastImagePanel.setCurrentImage(((BufferedImageSupporter) obj).toBufferedImage());
    else
      m_LastImagePanel.setCurrentImage((BufferedImage) obj);
    if (obj instanceof ReportHandler)
      m_LastImagePanel.setAdditionalProperties(((ReportHandler) obj).getReport());
    panel.add(m_LastImagePanel, BorderLayout.CENTER);

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
    ImagePanel		imagePanel;

    imagePanel = new ImagePanel();
    imagePanel.setShowProperties(true);
    imagePanel.setShowLog(false);
    if (obj instanceof BufferedImageSupporter)
      imagePanel.setCurrentImage(((BufferedImageSupporter) obj).toBufferedImage());
    else
      imagePanel.setCurrentImage((BufferedImage) obj);
    if (obj instanceof ReportHandler)
      imagePanel.setAdditionalProperties(((ReportHandler) obj).getReport());
    panel.add(imagePanel, BorderLayout.CENTER);

    m_LastImagePanel = imagePanel;

    return null;
  }
}
