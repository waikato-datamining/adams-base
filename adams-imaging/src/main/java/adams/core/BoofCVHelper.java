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
 * BoofCVHelper.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.awt.image.BufferedImage;

import adams.data.Notes;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import adams.data.image.AbstractImage;
import adams.data.report.Report;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;

/**
 * Helper class for BoofCV operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVHelper {

  /**
   * Turns the image into a buffered image.
   * 
   * @img		the cimage to convert
   * @return		the buffered image
   */
  public static BufferedImage toBufferedImage(ImageBase img) {
    if (ImageUInt8.class == img.getClass())
      return VisualizeBinaryData.renderBinary((ImageUInt8) img, null);
    else
      return ConvertBufferedImage.convertTo(img, null);
  }
  
  /**
   * Clones the image.
   * 
   * @param img		the image to clone
   * @return		the clone
   */
  public static ImageBase clone(ImageBase img) {
    if (img instanceof ImageSingleBand)
      return ((ImageSingleBand) img).clone();
    else
      return img.subimage(0, 0, img.getWidth() - 1, img.getHeight() - 1);
  }

  /**
   * Converts the image to the specified type if necessary.
   * 
   * @param img		the image to convert
   * @param type	the type of image
   * @return		the converted container
   */
  public static ImageBase toBoofCVImage(ImageBase img, BoofCVImageType type) {
    if (img.getClass() == type.getImageClass())
      return img;
    else
      return toBoofCVImage(toBufferedImage(img), type);
  }

  /**
   * Converts the image to the specified type if necessary.
   * 
   * @param img		the image to convert
   * @param type	the type of image
   * @return		the converted container
   */
  public static ImageBase toBoofCVImage(BufferedImage img, BoofCVImageType type) {
    return ConvertBufferedImage.convertFromSingle(img, null, type.getImageClass());
  }
  
  /**
   * Creates a {@link BoofCVImageContainer} container if necessary, otherwise
   * it just casts the object. In either, the correct image type is generated.
   * 
   * @param cont	the cont to cast/convert
   * @param type	the type of image
   * @return		the casted/converted container
   */
  public static BoofCVImageContainer toBoofCVImageContainer(AbstractImage cont, BoofCVImageType type) {
    BoofCVImageContainer	result;
    Report			report;
    Notes			notes;
    
    if (cont instanceof BoofCVImageContainer) {
      if (((BoofCVImageContainer) cont).getImage().getClass() == type.getImageClass()) {
	result = (BoofCVImageContainer) cont;
      }
      else {
	result = (BoofCVImageContainer) cont.getHeader();
	result.setImage(toBoofCVImage(((BoofCVImageContainer) cont).getImage(), type));
      }
    }
    else {
      report = cont.getReport().getClone();
      notes  = cont.getNotes().getClone();
      result = new BoofCVImageContainer();
      if (cont.getImage() instanceof ImageBase)
	result.setImage(toBoofCVImage((ImageBase) cont.getImage(), type));
      else
	result.setImage(toBoofCVImage(cont.toBufferedImage(), type));
      result.setReport(report);
      result.setNotes(notes);
    }
    
    return result;
  }
}
