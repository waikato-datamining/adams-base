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
 * SanselanImageReader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.awt.image.BufferedImage;
import java.util.logging.Level;

import org.apache.sanselan.Sanselan;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.SanselanImageWriter;

/**
 <!-- globalinfo-start -->
 * Sanselan image reader for: bmp, gif, ico, pbm, pgm, png, pnm, ppm, psd, tif, tiff
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SanselanImageReader
  extends AbstractImageReader<BufferedImageContainer> {
  
  /** for serialization. */
  private static final long serialVersionUID = 5347100846354068540L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sanselan image reader for: " + Utils.flatten(getFormatExtensions(), ", ");
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Sanselan image reader";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"bmp", "gif", "ico", "pbm", "pgm", "png", "pnm", "ppm", "psd", "tif", "tiff"};
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  public AbstractImageWriter getCorrespondingWriter() {
    return new SanselanImageWriter();
  }

  /**
   * Performs the actual reading of the image file.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  @Override
  protected BufferedImageContainer doRead(PlaceholderFile file) {
    BufferedImageContainer	result;
    BufferedImage		image;
    
    result = null;
    
    try {
      image = Sanselan.getBufferedImage(file.getAbsoluteFile());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load: " + file, e);
      image = null;
    }
    
    if (image != null) {
      result = new BufferedImageContainer();
      result.setImage(image);
    }
    
    return result;
  }
}
