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
 * SanselanImageWriter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.util.HashMap;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.Sanselan;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.SanselanImageReader;

/**
 <!-- globalinfo-start -->
 * Sanselan image writer for: bmp, gif, ico, pbm, pgm, png, pnm, ppm, tif, tiff
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
public class SanselanImageWriter
  extends AbstractImageWriter<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6385191315392140321L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sanselan image writer for: " + Utils.flatten(getFormatExtensions(), ", ");
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Sanselan image writer";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"bmp", "gif", "ico", "pbm", "pgm", "png", "pnm", "ppm", "tif", "tiff"};
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return new SanselanImageReader();
  }

  /**
   * Performs the actual writing of the image file.
   * 
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, BufferedImageContainer cont) {
    String	result;
    String	ext;
    ImageFormat	format;

    result = null;
    
    // determine file format
    ext = FileUtils.getExtension(file);
    if (ext.equals("bmp"))
      format = ImageFormat.IMAGE_FORMAT_BMP;
    else if (ext.equals("gif"))
      format = ImageFormat.IMAGE_FORMAT_GIF;
    else if (ext.equals("ico"))
      format = ImageFormat.IMAGE_FORMAT_ICO;
    else if (ext.equals("jpg") || ext.equals("jpeg"))
      format = ImageFormat.IMAGE_FORMAT_JPEG;
    else if (ext.equals("pbm"))
      format = ImageFormat.IMAGE_FORMAT_PBM;
    else if (ext.equals("pgm"))
      format = ImageFormat.IMAGE_FORMAT_PGM;
    else if (ext.equals("png"))
      format = ImageFormat.IMAGE_FORMAT_PNG;
    else if (ext.equals("pnm"))
      format = ImageFormat.IMAGE_FORMAT_PNM;
    else if (ext.equals("ppm"))
      format = ImageFormat.IMAGE_FORMAT_PPM;
    else if (ext.equals("tif") || ext.equals("tiff"))
      format = ImageFormat.IMAGE_FORMAT_TIFF;
    else
      format = null;
    if (format == null)
      result = "Unhandled file extension: " + ext;

    if (result == null) {
      try {
	Sanselan.writeImage(cont.toBufferedImage(), file.getAbsoluteFile(), format, new HashMap());
      }
      catch (Exception e) {
	result = Utils.handleException(this, "Failed to write image to: " + file, e);
      }
    }
    
    return result;
  }
}
