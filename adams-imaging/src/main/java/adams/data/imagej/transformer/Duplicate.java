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
 * Duplicate.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagej.transformer;

import ij.ImagePlus;
import adams.data.imagej.ImagePlusContainer;


 /**
 <!-- globalinfo-start -->
 * returns an ImagePlusContainer with the original image and an 8-bit gray copy of it.
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
 *
 * @author skroes
 * @version $Revision$
 */
public class Duplicate
  extends AbstractImageJTransformer {
  

  /** for serialization. */
  private static final long serialVersionUID = 3821758349578824745L;


  /**
   * Performs no transformation at all, just duplicates the image and returns 
   * both in an ImagePlusContainer array.  
   * 
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		ImagePlusConainter array with the original and a 8 bit gray copy of it.
   */
  
  protected ImagePlusContainer[] doTransform(ImagePlusContainer img) {
   
    ImagePlusContainer[] result;
    ImagePlus original, copy;
    
    original = img.getImage();
    copy = original.duplicate();
    copy.setProcessor(copy.getProcessor().convertToByte(true));
    
    result = new ImagePlusContainer[2];
    
    result[0] = (ImagePlusContainer) img.getHeader();
    result[0].setImage(original);
    result[1] = (ImagePlusContainer) img.getHeader();
    result[1].setImage(copy);
	 
    return result;
    
  }
   
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    
    return " returns an ImagePlusContainer with the orignial image and an 8-bit gray copy of it."; 
    
  }

}
