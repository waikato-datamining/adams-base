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
 * OpenIMAJObjectDetector.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.locateobjects;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.openimaj.objectdetector.AbstractObjectDetector;
import adams.data.openimaj.objectdetector.HaarCascade;
import org.openimaj.math.geometry.shape.Rectangle;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenIMAJObjectDetector
  extends AbstractObjectLocator {

  private static final long serialVersionUID = -5521919703087480870L;

  /** the detector to use. */
  protected AbstractObjectDetector m_Detector;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified OpenIMAJ object detector algorithm to locate objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "detector", "detector",
      new HaarCascade());
  }

  /**
   * Sets the detector to use.
   *
   * @param value	the detector
   */
  public void setDetector(AbstractObjectDetector value) {
    m_Detector = value;
    reset();
  }

  /**
   * Returns the detector to use.
   *
   * @return		the detector
   */
  public AbstractObjectDetector getDetector() {
    return m_Detector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String detectorTipText() {
    return "The detector algorithm to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "detector", m_Detector, "detector: ");
  }

  /**
   * Performs the actual locating of the objects.
   *
   * @param image	  the image to process
   * @param annotateOnly  whether to annotate only
   * @return		  the containers of located objects
   */
  @Override
  protected LocatedObjects doLocate(BufferedImage image, boolean annotateOnly) {
    LocatedObjects		result;
    BufferedImageContainer 	cont;
    List			detected;
    LocatedObject		obj;
    Rectangle 			rect;

    // convert image
    cont = new BufferedImageContainer();
    cont.setImage(image);

    // detect objects
    detected = m_Detector.detect(cont);
    result   = new LocatedObjects();
    for (Object object: detected) {
      if (object instanceof Rectangle) {
	rect = (Rectangle) object;
	obj = new LocatedObject(
	  image.getSubimage((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height),
	  (int) rect.x,
	  (int) rect.y,
	  (int) rect.width,
	  (int) rect.height);
	result.add(obj);
      }
    }

    return result;
  }
}
