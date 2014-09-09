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
 * PassThrough.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Dummy, just forwards container with input image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 78 $
 */
public class PassThrough
  extends AbstractObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = 9160763275489359825L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, just forwards the input image.";
  }
  
  /**
   * Returns the input image as output.
   * 
   * @param image	the image with the bugs
   * @return		the original image
   */
  @Override
  protected List<LocatedObject> doLocate(BufferedImage image) {
    ArrayList<LocatedObject>	result;
    
    result = new ArrayList<LocatedObject>();
    result.add(new LocatedObject(image, 0, 0, image.getWidth(), image.getHeight()));
    
    return result;
  }
}
