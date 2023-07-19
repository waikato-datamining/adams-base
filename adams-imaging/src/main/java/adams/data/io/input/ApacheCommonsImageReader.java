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
 * ApacheCommonsImageReader.java
 * Copyright (C) 2019-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.io.output.ApacheCommonsImageWriter;
import adams.data.io.output.ImageWriter;
import org.apache.commons.imaging.Imaging;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Apache Commons image reader for: bmp, gif, ico, pbm, pgm, png, pnm, ppm, psd, tif, tiff
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
 */
public class ApacheCommonsImageReader
  extends AbstractImageReader<BufferedImageContainer>
  implements InputStreamImageReader<BufferedImageContainer> {
  
  /** for serialization. */
  private static final long serialVersionUID = 5347100846354068540L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Apache Commons image reader for: " + Utils.flatten(getFormatExtensions(), ", ");
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Apache Commons image reader";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"bmp", "dcx", "gif", "hdr", "icns", "ico", "jpg", "pcx", "png", "pnm", "psd", "tif", "tiff", "wbmp", "xbm", "xpm"};
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  public ImageWriter getCorrespondingWriter() {
    return new ApacheCommonsImageWriter();
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
      image = Imaging.getBufferedImage(file.getAbsoluteFile());
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

  /**
   * Reads the image from the stream. Caller must close the stream.
   *
   * @param stream the stream to read from
   * @return the image container, null if failed to read
   */
  @Override
  public BufferedImageContainer read(InputStream stream) {
    BufferedImageContainer	result;
    BufferedImage		image;

    result = null;

    try {
      image = Imaging.getBufferedImage(stream);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load image from stream!", e);
      image = null;
    }

    if (image != null) {
      result = new BufferedImageContainer();
      result.setImage(image);
    }

    return result;
  }
}
