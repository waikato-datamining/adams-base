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
 * FrontalKeypointEnriched.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.openimaj.facedetector;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.image.AbstractImageContainer;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;

/**
 <!-- globalinfo-start -->
 * A face detector that uses an underlying face detector to detect frontal faces in an image, and then looks for facial keypoints within the detections. <br>
 * <br>
 * For more information see:<br>
 * Mark Everingham, Josef Sivic, Andrew Zisserman: Hello! My name is... Buffy - Automatic naming of characters in TV video. In: In BMVC, 2006.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;inproceedings{Everingham2006,
 *    author = {Mark Everingham and Josef Sivic and Andrew Zisserman},
 *    booktitle = {In BMVC},
 *    title = {Hello! My name is... Buffy - Automatic naming of characters in TV video},
 *    year = {2006}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-detector &lt;adams.data.openimaj.facedetector.AbstractFaceDetector&gt; (property: detector)
 * &nbsp;&nbsp;&nbsp;The base detector to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.openimaj.facedetector.HaarCascade
 * </pre>
 * 
 * <pre>-patch-scale &lt;float&gt; (property: patchScale)
 * &nbsp;&nbsp;&nbsp;The scale of the patch compared to the patch extracted by the internal detector.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FrontalKeypointEnriched
  extends AbstractMetaFaceDetector
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 4304163800543325831L;

  /** the scale of the patch compared to the patch extracted by the internal detector. */
  protected float m_PatchScale;

  @Override
  public String globalInfo() {
    return
      "A face detector that uses an underlying face detector to detect frontal faces in an image, and then looks for facial keypoints within the detections. \n\n"
	+ "For more information see:\n"
	+ getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.INPROCEEDINGS);
    result.setValue(Field.AUTHOR, "Mark Everingham and Josef Sivic and Andrew Zisserman");
    result.setValue(Field.TITLE, "Hello! My name is... Buffy - Automatic naming of characters in TV video");
    result.setValue(Field.BOOKTITLE, "In BMVC");
    result.setValue(Field.YEAR, "2006");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "patch-scale", "patchScale",
      1.0f, 0.0f, null);
  }

  /**
   * Returns the default detector to use.
   *
   * @return		the detector
   */
  protected AbstractFaceDetector getDefaultDetector() {
    return new HaarCascade();
  }

  /**
   * Sets the scale of the patch compared to the patch extracted by the
   * internal detector.
   *
   * @param value	the scale
   */
  public void setPatchScale(float value) {
    m_PatchScale = value;
    reset();
  }

  /**
   * Returns the scale of the patch compared to the patch extracted by the
   * internal detector.
   *
   * @return		the scale
   */
  public float getPatchScale() {
    return m_PatchScale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String patchScaleTipText() {
    return "The scale of the patch compared to the patch extracted by the internal detector.";
  }

  /**
   * Creates a new instance of the face detector.
   *
   * @return		the instance
   */
  @Override
  protected FaceDetector newInstance() {
    FKEFaceDetector	result;
    FaceDetector	base;

    base   = m_Detector.newInstance();
    result = new FKEFaceDetector(base, m_PatchScale);

    return result;
  }

  /**
   * Converts the image container into the required image type for the detector.
   *
   * @param cont	the container to convert
   * @return		the generated image
   */
  protected Image convert(AbstractImageContainer cont) {
    return ImageUtilities.createFImage(cont.toBufferedImage());
  }
}
