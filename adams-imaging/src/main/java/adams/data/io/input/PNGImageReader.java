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
 * PNGImageReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.io.output.AbstractImageWriter;
import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;

import java.awt.image.BufferedImage;
import java.util.logging.Level;

/**
 * Reads images in PNG format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PNGImageReader
  extends AbstractImageReader<BufferedImageContainer> {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads images in PNG format.";
  }

  private static final long serialVersionUID = -4360936418761836176L;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "PNG";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"png"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public AbstractImageWriter getCorrespondingWriter() {
    return null;
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
    PngReader 			reader;
    int				i;
    int				n;
    int				channels;
    IImageLine 			line;
    ImageLineByte 		lineByte;
    ImageLineInt 		lineInt;
    int				color;

    result = new BufferedImageContainer();

    try {
      reader = new PngReader(file.getAbsoluteFile());
      if (isLoggingEnabled())
	getLogger().info(reader.imgInfo.toString());

      channels = reader.imgInfo.channels;
      if (channels == 4) // RGBA
	image = new BufferedImage(reader.imgInfo.cols, reader.imgInfo.rows, BufferedImage.TYPE_INT_ARGB);
      else if (channels == 3)  // RGB
	image = new BufferedImage(reader.imgInfo.cols, reader.imgInfo.rows, BufferedImage.TYPE_INT_RGB);
      else if (channels == 2)  // GrayA
	image = new BufferedImage(reader.imgInfo.cols, reader.imgInfo.rows, BufferedImage.TYPE_INT_ARGB);
      else
	image = new BufferedImage(reader.imgInfo.cols, reader.imgInfo.rows, BufferedImage.TYPE_INT_RGB);
      for (n = 0; n < reader.imgInfo.rows; n++) {
        line = reader.readRow();
	if (line instanceof ImageLineByte) {
	  lineByte = (ImageLineByte) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    if (channels == 4) {
	      color = lineByte.getElem(i * channels + 3) << 24  // A
		| lineByte.getElem(i * channels) << 16          // R
		| lineByte.getElem(i * channels + 1) << 8       // G
		| lineByte.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 3) {
	      color = lineByte.getElem(i * channels) << 16      // R
		| lineByte.getElem(i * channels + 1) << 8       // G
		| lineByte.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 2) {
	      color = lineByte.getElem(i * channels + 1) << 24  // A
		| lineByte.getElem(i * channels) << 16          // gray
		| lineByte.getElem(i * channels) << 8           // gray
		| lineByte.getElem(i * channels);               // gray
	    }
	    else {
	      color = lineByte.getElem(i) << 16      // gray
		| lineByte.getElem(i) << 8           // gray
		| lineByte.getElem(i);               // gray
	    }
	    image.getRaster().setDataElements(i, n, image.getColorModel().getDataElements(color, null));
	  }
	}
	else {
	  lineInt = (ImageLineInt) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    if (channels == 4) {
	      color = lineInt.getElem(i * channels + 3) << 24  // A
		| lineInt.getElem(i * channels) << 16          // R
		| lineInt.getElem(i * channels + 1) << 8       // G
		| lineInt.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 3) {
	      color = lineInt.getElem(i * channels) << 16      // R
		| lineInt.getElem(i * channels + 1) << 8       // G
		| lineInt.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 2) {
	      color = lineInt.getElem(i * channels + 1) << 24  // A
		| lineInt.getElem(i * channels) << 16          // gray
		| lineInt.getElem(i * channels) << 8           // gray
		| lineInt.getElem(i * channels);               // gray
	    }
	    else {
	      color = lineInt.getElem(i) << 16      // gray
		| lineInt.getElem(i) << 8           // gray
		| lineInt.getElem(i);               // gray
	    }
	    image.getRaster().setDataElements(i, n, image.getColorModel().getDataElements(color, null));
	  }
	}
      }
      result.setImage(image);
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read PNG: " + file, e);
    }

    return result;
  }
}
