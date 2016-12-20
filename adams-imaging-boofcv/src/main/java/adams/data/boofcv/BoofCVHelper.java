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
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv;

import adams.data.Notes;
import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.struct.image.*;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

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
   * @param img		the image to convert
   * @return		the buffered image
   */
  public static BufferedImage toBufferedImage(ImageBase img) {
    BufferedImage dst = null;
    if (ImageUInt8.class == img.getClass())
      return VisualizeBinaryData.renderBinary((ImageUInt8) img, null);
    else {
      if(img instanceof MultiSpectral){
        int numBands = ((MultiSpectral) img).getNumBands();

        int w = img.getWidth();
        int h = img.getHeight();
        switch (numBands) {
          case 3:
            dst = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
            break;
          case 4:
            dst = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            break;
          default:
            dst = null;
        }
      }
      return ConvertBufferedImage.convertTo(img, dst, true);
    }
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
      return img.subimage(0, 0, img.getWidth(), img.getHeight(), null);
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
   * Attempts to convert the image to the closest BoofCV type.
   * 
   * @param img		the image to convert
   * @return		the converted container
   */
  public static ImageBase toBoofCVImage(BufferedImage img) {
    switch (img.getType()) {
      case BufferedImage.TYPE_BYTE_BINARY:
	return toBoofCVImage(img, BoofCVImageType.UNSIGNED_INT_8);
      case BufferedImage.TYPE_BYTE_GRAY:
	return toBoofCVImage(img, BoofCVImageType.UNSIGNED_INT_8);
      case BufferedImage.TYPE_3BYTE_BGR:
      case BufferedImage.TYPE_4BYTE_ABGR:
      case BufferedImage.TYPE_4BYTE_ABGR_PRE:
      case BufferedImage.TYPE_INT_ARGB:
      case BufferedImage.TYPE_INT_ARGB_PRE:
      case BufferedImage.TYPE_INT_BGR:
      case BufferedImage.TYPE_INT_RGB:
        return toBoofCVMultiBandImage(img);
      default:
	return toBoofCVImage(img, BoofCVImageType.FLOAT_32);
    }
  }

  /**
   * Converts the image to the specified type if necessary.
   *
   * @param img		the image to convert
   * @return		the converted image
   */
  public static ImageBase toBoofCVMultiBandImage(BufferedImage img) {
    return ConvertBufferedImage.convertFromMulti(img, null, true, ImageUInt8.class);
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
   * Converts the image container to the specified image type if necessary.
   * 
   * @param cont	the image container to convert
   * @param type	the type of image
   * @return		the converted image
   */
  public static ImageBase toBoofCVImage(AbstractImageContainer cont, BoofCVImageType type) {
    if (cont instanceof BoofCVImageContainer)
      return toBoofCVImage(((BoofCVImageContainer) cont).getImage(), type);
    else
      return toBoofCVImage(cont.toBufferedImage(), type);
  }
  
  /**
   * Creates a {@link BoofCVImageContainer} container if necessary, using 
   * {@link BoofCVImageType#FLOAT_32}, otherwise it just casts the object.
   * 
   * @param cont	the cont to cast/convert
   * @return		the casted/converted container
   */
  public static BoofCVImageContainer toBoofCVImageContainer(AbstractImageContainer cont) {
    if (cont instanceof BoofCVImageContainer)
      return (BoofCVImageContainer) cont;
    else
      return toBoofCVImageContainer(cont, null);
  }
  
  /**
   * Creates a {@link BoofCVImageContainer} container if necessary, otherwise
   * it just casts the object. In either, the correct image type is generated.
   * 
   * @param cont	the cont to cast/convert
   * @param type	the type of image
   * @return		the casted/converted container
   */
  public static BoofCVImageContainer toBoofCVImageContainer(AbstractImageContainer cont, BoofCVImageType type) {
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
      if (type != null) {
        if (cont.getImage() instanceof ImageBase)
          result.setImage(toBoofCVImage((ImageBase) cont.getImage(), type));
        else
          result.setImage(toBoofCVImage(cont.toBufferedImage(), type));
      }
      else {
        result.setImage(toBoofCVImage(cont.toBufferedImage()));
      }
      result.setReport(report);
      result.setNotes(notes);
    }
    
    return result;
  }

  /**
   * Converts an image to greyscale and checks if it is binary or not.
   * @param img the image to covert
   * @return True if the image is a binary image, false if not.
   */
  public static boolean isBinary(BoofCVImageContainer img) {
    boolean result = true;
    ImageUInt8 greyscale = (ImageUInt8) toBoofCVImage(img, BoofCVImageType.UNSIGNED_INT_8);
    Set<Byte> values = new HashSet<>();
    for(byte pixel : greyscale.getData()) {
      values.add(pixel);
      if(values.size() > 2) {
	result = false;
	break;
      }
    }
    return result;
  }
}
