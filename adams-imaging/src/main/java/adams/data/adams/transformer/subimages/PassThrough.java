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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.adams.transformer.subimages;

import java.util.ArrayList;
import java.util.List;

import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Dummy generator, simply passes the original image through.
 * <p/>
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
public class PassThrough
  extends AbstractSubImagesGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -2752350477173134757L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy generator, simply passes the original image through.";
  }

  /**
   * Performs the actual generation of the subimages.
   * 
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  @Override
  protected List<BufferedImageContainer> doProcess(BufferedImageContainer image) {
    List<BufferedImageContainer>	result;
    
    result = new ArrayList<BufferedImageContainer>();
    result.add(image);
    
    return result;
  }
}
