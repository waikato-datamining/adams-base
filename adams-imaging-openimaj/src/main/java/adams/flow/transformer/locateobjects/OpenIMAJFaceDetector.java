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
 * OpenIMAJFaceDetector.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.locateobjects;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.openimaj.facedetector.AbstractFaceDetector;
import adams.data.openimaj.facedetector.HaarCascade;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the specified OpenIMAJ face detector algorithm to locate faces.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-center-on-canvas &lt;boolean&gt; (property: centerOnCanvas)
 * &nbsp;&nbsp;&nbsp;If enabled, the located objects get centered on a canvas of fixed size.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-canvas-width &lt;int&gt; (property: canvasWidth)
 * &nbsp;&nbsp;&nbsp;The width of the canvas in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-canvas-height &lt;int&gt; (property: canvasHeight)
 * &nbsp;&nbsp;&nbsp;The height of the canvas in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-canvas-color &lt;java.awt.Color&gt; (property: canvasColor)
 * &nbsp;&nbsp;&nbsp;The color to use for filling the canvas.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 * <pre>-detector &lt;adams.data.openimaj.facedetector.AbstractFaceDetector&gt; (property: detector)
 * &nbsp;&nbsp;&nbsp;The detector algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.openimaj.facedetector.HaarCascade
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenIMAJFaceDetector
  extends AbstractObjectLocator {

  private static final long serialVersionUID = -5521919703087480870L;

  /** the detector to use. */
  protected AbstractFaceDetector m_Detector;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified OpenIMAJ face detector algorithm to locate faces.";
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
  public void setDetector(AbstractFaceDetector value) {
    m_Detector = value;
    reset();
  }

  /**
   * Returns the detector to use.
   *
   * @return		the detector
   */
  public AbstractFaceDetector getDetector() {
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
    List<DetectedFace>		detected;
    LocatedObject		obj;

    // convert image
    cont = new BufferedImageContainer();
    cont.setImage(image);

    // detect faces
    detected = m_Detector.detectFaces(cont);
    result   = new LocatedObjects();
    for (DetectedFace face: detected) {
      obj = new LocatedObject(
	ImageUtilities.createBufferedImage(face.getFacePatch()),
	(int) face.getBounds().x,
	(int) face.getBounds().y,
	(int) face.getBounds().width,
	(int) face.getBounds().height);
      result.add(obj);
    }

    return result;
  }
}
