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
 * AbstractCurrentImageFilter.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;

import adams.gui.visualization.image.ImagePanel;

/**
 * Ancestor for plugins that filter the current image.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCurrentImageFilter
  extends AbstractImageViewerPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 869121794905442017L;

  /** for storing filtering errors. */
  protected String m_FilterError;

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   * <p/>
   * Panel must be non-null and must contain an image.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(ImagePanel panel) {
    return (panel != null) && (panel.getCurrentImage() != null);
  }

  /**
   * Filters the image.
   *
   * @param image	the image to filter
   * @return		the processed image
   */
  protected abstract BufferedImage filter(BufferedImage image);

  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    BufferedImage	input;
    BufferedImage	output;
    File		file;

    result = null;

    m_CurrentPanel.addUndoPoint("Saving undo data...", "Filtering image: " + getCaption());
    
    input         = m_CurrentPanel.getCurrentImage();
    m_FilterError = null;
    try {
      output = filter(input);

      // did user abort filtering?
      if (m_CanceledByUser)
	return result;

      if (output == null) {
	result = "Failed to filter image: ";
	if (m_FilterError == null)
	  result += "unknown reason";
	else
	  result += m_FilterError;
      }
      else {
	file = m_CurrentPanel.getCurrentFile();
	m_CurrentPanel.setCurrentImage(output);
	m_CurrentPanel.setCurrentFile(file);
	m_CurrentPanel.setModified(true);
      }
    }
    catch (Exception e) {
      m_FilterError = e.toString();
      result = "Failed to filter image: ";
      getLogger().log(Level.SEVERE, result, e);
      result += m_FilterError;
    }

    return result;
  }
}
