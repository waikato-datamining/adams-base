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
 * Sandeep.java
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
import org.openimaj.image.processing.face.detection.SandeepFaceDetector;

/**
 <!-- globalinfo-start -->
 * Implementation of a face detector along the lines of "Human Face Detection in Cluttered Color Images Using Skin Color and Edge Information" K. Sandeep and A. N. Rajagopalan (IIT&#47;Madras).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{Sandeep2002,
 *    author = {Sandeep, K and Rajagopalan, A N},
 *    journal = {Electrical Engineering},
 *    publisher = {citeseer},
 *    title = {Human Face Detection in Cluttered Color Images Using Skin Color and Edge Information},
 *    year = {2002},
 *    HTTP = {http:&#47;&#47;citeseerx.ist.psu.edu&#47;viewdoc&#47;download?doi:10.1.1.12.730&amp;rep:rep1&amp;type:pdf}
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Sandeep
  extends AbstractFaceDetector
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 4304163800543325831L;

  @Override
  public String globalInfo() {
    return
      "Implementation of a face detector along the lines of \"Human Face "
        + "Detection in Cluttered Color Images Using Skin Color and Edge "
        + "Information\" K. Sandeep and A. N. Rajagopalan (IIT/Madras).";
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

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "Sandeep, K and Rajagopalan, A N");
    result.setValue(Field.TITLE, "Human Face Detection in Cluttered Color Images Using Skin Color and Edge Information");
    result.setValue(Field.JOURNAL, "Electrical Engineering");
    result.setValue(Field.YEAR, "2002");
    result.setValue(Field.HTTP, "http://citeseerx.ist.psu.edu/viewdoc/download?doi:10.1.1.12.730&rep:rep1&type:pdf");
    result.setValue(Field.PUBLISHER, "citeseer");

    return result;
  }

  /**
   * Creates a new instance of the face detector.
   *
   * @return		the instance
   */
  @Override
  protected FaceDetector newInstance() {
    return new SandeepFaceDetector();
  }

  /**
   * Converts the image container into the required image type for the detector.
   *
   * @param cont	the container to convert
   * @return		the generated image
   */
  protected Image convert(AbstractImageContainer cont) {
    return ImageUtilities.createMBFImage(cont.toBufferedImage(), false);
  }
}
