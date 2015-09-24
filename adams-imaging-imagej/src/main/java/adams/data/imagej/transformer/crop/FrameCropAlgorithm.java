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
 * FrameCropAlgorithm.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagej.transformer.crop;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;

import java.awt.Point;


/**
 <!-- globalinfo-start -->
 * This class crops a framed image. It can't crop a special object!
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 * @author skroes
 * @version $Revision$
 */
public class FrameCropAlgorithm
extends AbstractCropAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = -4663715509860132916L;

  /** 
   *  for the methods findMinimum and findMaximum 
   *  to decide which loop have to be used. 
   */
  protected static final int X = 0;
  protected static final int Y = 1;

  /**
   * the croped area which is the returned image.
   */
  protected Roi m_CropArea;

  /**
   * the bounds of the croped region
   */
  protected int m_XCropMax;
  protected int m_XCropMin;
  protected int m_YCropMax;
  protected int m_YCropMin;

  /**
   * the threshold which differences the background and the foreground
   */
  protected int m_Threshold;

  /**
   * counter for the background and the foreground
   */
  protected int m_Background;
  protected int m_Foreground;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "this class crops a framed image. It can't crop a special object!";
  }

  /**
   * Performs the actual crop.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  @Override
  protected ImagePlus doCrop(ImagePlus img) {
    ImagePlus imp = img.duplicate();
    ImageProcessor ip = imp.getProcessor().convertToByte(true);
    ImageProcessor original = img.getProcessor();
    int width = imp.getProcessor().getWidth();
    int height = imp.getProcessor().getHeight();

    m_Threshold = ip.getAutoThreshold();
    m_XCropMax = findMaximum(ip , width, height, X);
    m_XCropMin = findMinimum(ip , width, height, X);
    m_YCropMax = findMaximum(ip , height, width, Y);
    m_YCropMin = findMinimum(ip , height, width, Y);

    if(m_XCropMax == -1 || m_XCropMin == -1 || m_YCropMax == -1 
	|| m_YCropMin == -1 ){
      return img;  
    }

    m_CropArea = new Roi(m_XCropMin, m_YCropMin, (m_XCropMax - m_XCropMin) + 1, (m_YCropMax - m_YCropMin) + 1);

    original.setRoi(m_CropArea);
    original = original.crop();
    img.setProcessor(original);

    return img;
  }

  /**
   * Hook method after the crop happened.
   * <br><br>
   * Sets the top-left and bottom-right corners.
   * 
   * @param img		the cropped
   */
  @Override
  protected void postCrop(ImagePlus img) {
    super.postCrop(img);
    
    m_TopLeft     = new Point(m_XCropMin, m_YCropMin);
    m_BottomRight = new Point(m_XCropMax, m_YCropMax);
  }
  
  /**
   * finds the first row/line with more foreground pixel than background pixel.
   * 
   * @param ip the imageprocessor which represents the image
   * @param outerLoop the value for the outer loop
   * @param innerLoop the value for the inner loop 
   * @param dimension whether the x or y minimum should be found
   * @return the x or y value for the first found row/line of the image 
   */
  protected int findMinimum(ImageProcessor ip, int outerLoop, int innerLoop, int dimension){
    for(int i = 0; i < outerLoop; i++){
      for(int j = 0; j < innerLoop; j++){
	if(dimension == Y){
	  if(ip.getPixel(j, i) < m_Threshold){
	    m_Background++;
	  }
	  else{ 
	    m_Foreground++;
	  }
	}
	else if(dimension == X){ 
	  if(ip.getPixel(i, j) < m_Threshold){
	    m_Background++;
	  }
	  else{ 
	    m_Foreground++;
	  }
	}
	else{ 
	  return - 1;
	}
      }
      if(m_Foreground > m_Background){
	return i;
      } 
      else{
	m_Background = 0;
	m_Foreground = 0;
      }
    }
    m_Background = 0;
    m_Foreground = 0;
    return -1;
  }


  /**
   * finds the last row/line with more foreground pixel than background pixel.
   * 
   * @param ip the imageprocessor which represents the image
   * @param outerLoop the value for the outer loop
   * @param innerLoop the value for the inner loop 
   * @param dimension whether the x or y maximum should be found
   * @return the x or y value for the last found row/line of the image 
   */
  protected int findMaximum(ImageProcessor ip, int outerLoop, int innerLoop, int dimension){
    for(int i = outerLoop-1; i >= 0; i --){  
      for(int j = 0; j < innerLoop; j++){
	if(dimension == Y){
	  if(ip.getPixel(j, i) < m_Threshold){
	    m_Background++;
	  }
	  else{
	    m_Foreground++;
	  }
	}
	else if(dimension == X){ 
	  if(ip.getPixel(i,j) < m_Threshold){
	    m_Background++;
	  }
	  else{ 
	    m_Foreground++;
	  }
	}
	else{ 
	  return -1;
	}
      }
      if(m_Foreground > m_Background){
	return i;
      }
      else{
	m_Background = 0;
	m_Foreground = 0;
      }
    }
    m_Background = 0;
    m_Foreground = 0;
    return -1; 
  }
}
