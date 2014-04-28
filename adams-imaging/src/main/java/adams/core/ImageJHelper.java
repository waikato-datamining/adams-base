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
 * ImageJHelper.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import ij.ImagePlus;

import java.io.File;
import java.util.Date;

import adams.data.Notes;
import adams.data.image.AbstractImage;
import adams.data.imagej.ImagePlusContainer;
import adams.data.report.Report;
import adams.env.Environment;

/**
 * Helper class for ImageJ.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageJHelper {

  /** the system property that ImageJ queries for the plugins directory. */
  public final static String PROPERTY_PLUGINS_DIR = "plugins.dir";
  
  /** the environment variable to specify an alternative ImageJ directory,
   * just above the "plugins" directory. */
  public final static String ENV_IMAGEJ_DIR = "ADAMS_IMAGEJ_DIR";
  
  /** whether the plugins directory has been set already. */
  protected static Boolean m_PluginsDirectorySet;
  
  /**
   * Sets the ImageJ directory as ".$PROJECTHOME/imagej" and the plugins
   * directory therefore as ".$PROJECTHOME/imagej/plugins"
   */
  public static synchronized void setPluginsDirectory() {
    String	dir;
    File	file;
    
    if (m_PluginsDirectorySet == null) {
      dir = null;

      // environment variable set?
      if (System.getenv(ENV_IMAGEJ_DIR) != null) {
	file = new File(System.getenv(ENV_IMAGEJ_DIR));
	if (file.exists() && file.isDirectory())
	  dir = file.getAbsolutePath();
      }

      // default to .$PROJECTHOME/imagej
      if (dir == null)
	dir = Environment.getInstance().getHome() + File.separator + "imagej";

      System.setProperty(PROPERTY_PLUGINS_DIR, dir);

      m_PluginsDirectorySet = true;
    }
  }
  
  /**
   * Creates a {@link ImagePlusContainer} container if necessary, otherwise
   * it just casts the object.
   * 
   * @param cont	the cont to cast/convert
   * @return		the casted/converted container
   */
  public static ImagePlusContainer toImagePlusContainer(AbstractImage cont) {
    ImagePlusContainer	result;
    Report		report;
    Notes		notes;
    
    if (cont instanceof ImagePlusContainer)
      return (ImagePlusContainer) cont;

    report = cont.getReport().getClone();
    notes  = cont.getNotes().getClone();
    result = new ImagePlusContainer();
    result.setImage(new ImagePlus("" + new Date(), cont.toBufferedImage()));
    result.setReport(report);
    result.setNotes(notes);
    
    return result;
  }
}
