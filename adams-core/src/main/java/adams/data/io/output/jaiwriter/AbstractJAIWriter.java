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
 * AbstractJAIWriter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output.jaiwriter;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.BufferedImageHelper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Ancestor for classes that return a configured JAI ImageIO writer.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractJAIWriter
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 866349334075701756L;

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the writer.
   *
   * @return		the writer
   * @throws Exception	if determining of writer fails
   */
  public abstract ImageWriter getWriter() throws Exception;

  /**
   * Returns the parameters for the writer.
   *
   * @return		the parameters
   * @throws Exception	if determining of writer or setting of parameters fails
   */
  public abstract ImageWriteParam getParameters() throws Exception;

  /**
   * Returns whether the alpha channel must be removed.
   *
   * @return		true if to remove
   */
  public abstract boolean removeAlphaChannel();

  /**
   * Performs the actual writing of the image file.
   *
   * @param file	the file to write to
   * @param img		the image to write
   * @return		null if successfully written, otherwise error message
   */
  public String write(PlaceholderFile file, BufferedImage img) {
    FileOutputStream 		fos;
    BufferedOutputStream 	bos;

    fos = null;
    bos = null;
    try {
      fos = new FileOutputStream(file.getAbsoluteFile());
      bos = new BufferedOutputStream(fos);
      return write(bos, img);
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to write image!", e);
    }
    finally {
      FileUtils.closeQuietly(bos);
      FileUtils.closeQuietly(fos);
    }
  }

  /**
   * Writes the image to the stream. Callers must close the stream.
   *
   * @param stream 	the stream to write to
   * @param img   	the image to write
   * @return 		null if successfully written, otherwise error message
   */
  public String write(OutputStream stream, BufferedImage img) {
    ImageWriter		writer;
    ImageOutputStream 	ios;

    ios = null;
    try {
      ios    = ImageIO.createImageOutputStream(stream);
      writer = getWriter();
      writer.setOutput(ios);
      if (removeAlphaChannel())
	img = BufferedImageHelper.removeAlphaChannel(img);
      writer.write(null, new IIOImage(img, null, null), getParameters());
      writer.dispose();
      return null;
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to write image!", e);
    }
    finally {
      FileUtils.closeQuietly(ios);
    }
  }
}
