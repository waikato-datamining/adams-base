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
 * HaarCascade.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.openimaj.facedetector;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

/**
 <!-- globalinfo-start -->
 * A face detector based on a Haar cascade. The cascades provided by HaarCascadeDetector.BuiltInCascade are the same as those available in OpenCV.<br>
 * <br>
 * For more information see:<br>
 * Viola, P., Jones, M.: Rapid object detection using a boosted cascade of simple features. In: Computer Vision and Pattern Recognition, 2001. CVPR 2001. Proceedings of the 2001 IEEE Computer Society Conference on, I, 511, I, 518 vol.1, 2001.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;inproceedings{Viola2001,
 *    author = {Viola, P. and Jones, M.},
 *    booktitle = {Computer Vision and Pattern Recognition, 2001. CVPR 2001. Proceedings of the 2001 IEEE Computer Society Conference on},
 *    pages = {I, 511, I, 518 vol.1},
 *    title = {Rapid object detection using a boosted cascade of simple features},
 *    volume = {1},
 *    year = {2001}
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
 * <pre>-cascade &lt;adams.core.io.PlaceholderFile&gt; (property: cascade)
 * &nbsp;&nbsp;&nbsp;The cascade to use.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-min-size &lt;int&gt; (property: minSize)
 * &nbsp;&nbsp;&nbsp;The minimum search window size.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HaarCascade
  extends AbstractFaceDetector
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 4304163800543325831L;

  /** the cascade to load (uses built-in if pointing to directory). */
  protected PlaceholderFile m_Cascade;

  /** the minimum search window size. */
  protected int m_MinSize;

  @Override
  public String globalInfo() {
    return
      "A face detector based on a Haar cascade. The cascades provided "
	+ "by HaarCascadeDetector.BuiltInCascade are the same as those "
	+ "available in OpenCV.\n\n"
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
    result.setValue(Field.AUTHOR, "Viola, P. and Jones, M.");
    result.setValue(Field.TITLE, "Rapid object detection using a boosted cascade of simple features");
    result.setValue(Field.BOOKTITLE, "Computer Vision and Pattern Recognition, 2001. CVPR 2001. Proceedings of the 2001 IEEE Computer Society Conference on");
    result.setValue(Field.PAGES, "I, 511, I, 518 vol.1");
    result.setValue(Field.VOLUME, "1");
    result.setValue(Field.YEAR, "2001");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "cascade", "cascade",
      new PlaceholderFile());

    m_OptionManager.add(
      "min-size", "minSize",
      -1, -1, null);
  }

  /**
   * Sets the cascade to use.
   *
   * @param value	the file
   */
  public void setCascade(PlaceholderFile value) {
    m_Cascade = value;
    reset();
  }

  /**
   * Returns the cascade to use.
   *
   * @return		the file
   */
  public PlaceholderFile getCascade() {
    return m_Cascade;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cascadeTipText() {
    return "The cascade to use.";
  }

  /**
   * Sets the minimum search window size.
   *
   * @param value	the size
   */
  public void setMinSize(int value) {
    m_MinSize = value;
    reset();
  }

  /**
   * Returns the minimum search window size.
   *
   * @return		the size
   */
  public int getMinSize() {
    return m_MinSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minSizeTipText() {
    return "The minimum search window size.";
  }

  /**
   * Creates a new instance of the face detector.
   *
   * @return		the instance
   */
  @Override
  protected FaceDetector newInstance() {
    if (m_Cascade.exists() && m_Cascade.isFile()) {
      if (m_MinSize > -1)
	return new HaarCascadeDetector(m_Cascade.getAbsolutePath(), m_MinSize);
      else
	return new HaarCascadeDetector(m_Cascade.getAbsolutePath());
    }
    else {
      if (m_MinSize > -1)
	return new HaarCascadeDetector(m_MinSize);
      else
	return new HaarCascadeDetector();
    }
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
