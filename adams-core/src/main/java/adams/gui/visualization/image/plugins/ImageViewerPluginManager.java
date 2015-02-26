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
 * ImageViewerPluginManager.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.plugins;

import adams.core.ClassLister;
import adams.gui.plugin.AbstractToolPluginManager;
import adams.gui.visualization.image.ImageViewerPanel;

/**
 * Manages the plugins for the image viewer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageViewerPluginManager
  extends AbstractToolPluginManager<ImageViewerPanel, AbstractImageViewerPlugin>{

  /**
   * Initializes the manager.
   *
   * @param owner the owning tool
   */
  public ImageViewerPluginManager(ImageViewerPanel owner) {
    super(owner);
  }

  /**
   * Returns a list of plugin classnames.
   *
   * @return		all the available plugins
   */
  @Override
  public String[] getPlugins() {
    return ClassLister.getSingleton().getClassnames(AbstractImageViewerPlugin.class);
  }
}
