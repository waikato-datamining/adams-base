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
 * CroppedLocator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.image.transformer.crop.AbstractCropAlgorithm;
import adams.data.image.transformer.crop.NoCrop;

/**
 <!-- globalinfo-start -->
 * Uses the defined crop algorithm to first crop the image before locating objects. The locations get adjusted to fit the original image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-locator &lt;adams.flow.transformer.locateobjects.AbstractObjectLocator&gt; (property: locator)
 * &nbsp;&nbsp;&nbsp;The base locator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.locateobjects.PassThrough
 * </pre>
 * 
 * <pre>-crop &lt;adams.data.image.transformer.crop.AbstractCropAlgorithm&gt; (property: crop)
 * &nbsp;&nbsp;&nbsp;The crop algorithm to apply to the image.
 * &nbsp;&nbsp;&nbsp;default: adams.data.image.transformer.crop.NoCrop
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CroppedLocator
  extends AbstractMetaObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = -3902398122751433577L;
  
  /** the crop algorithm to use. */
  protected AbstractCropAlgorithm m_Crop;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses the defined crop algorithm to first crop the image before "
	+ "locating objects. The locations get adjusted to fit the original image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "crop", "crop",
	    new NoCrop());
  }

  /**
   * Sets the crop algorithm.
   *
   * @param value 	the algorithm
   */
  public void setCrop(AbstractCropAlgorithm value) {
    m_Crop = value;
    reset();
  }

  /**
   * Returns the crop algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractCropAlgorithm getCrop() {
    return m_Crop;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cropTipText() {
    return "The crop algorithm to apply to the image.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "crop", m_Crop, "crop: ");
    result += ", " + super.getQuickInfo();
    
    return result;
  }

  /**
   * Performs the actual locating of the objects.
   * 
   * @param imp	        the image to process
   * @return		the containers of located objects
   */
  @Override
  protected List<LocatedObject> doLocate(BufferedImage image) {
    List<LocatedObject>		result;
    List<LocatedObject>		temp;
    BufferedImage		cropped;
    int				left;
    int				top;
    LocatedObject		adjusted;

    // crop image
    if (m_Crop instanceof NoCrop) {
      cropped = image;
      left    = 0;
      top     = 0;
    }
    else {
      cropped = m_Crop.crop(image);
      left    = (int) m_Crop.getTopLeft().getX();
      top     = (int) m_Crop.getTopLeft().getY();
    }
    
    // located objects
    temp = m_Locator.locate(cropped);

    // adjust locations
    result = new ArrayList<LocatedObject>();
    for (LocatedObject obj: temp) {
      adjusted = new LocatedObject(
	  obj.getImage(), 
	  left + obj.getX(), top + obj.getY(), 
	  obj.getWidth(), obj.getHeight());
      result.add(adjusted);
    }
    
    return result;
  }
}
