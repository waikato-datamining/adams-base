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
 * ConstrainedLocalModel.java
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
import org.openimaj.image.processing.face.detection.CLMFaceDetector;
import org.openimaj.image.processing.face.detection.FaceDetector;

/**
 <!-- globalinfo-start -->
 * Face detector based on a constrained local model. Fits a 3D face model to each detection.<br>
 * <br>
 * For more information see:<br>
 * Jason M. Saragih, Simon Lucey, Jeffrey F. Cohn: Face alignment through subspace constrained mean-shifts. In: IEEE 12th International Conference on Computer Vision, ICCV 2009, Kyoto, Japan, September 27 - October 4, 2009, 1034-1041, 2009.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;inproceedings{Saragih2009,
 *    author = {Jason M. Saragih and Simon Lucey and Jeffrey F. Cohn},
 *    booktitle = {IEEE 12th International Conference on Computer Vision, ICCV 2009, Kyoto, Japan, September 27 - October 4, 2009},
 *    pages = {1034-1041},
 *    publisher = {IEEE},
 *    title = {Face alignment through subspace constrained mean-shifts},
 *    year = {2009}
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
public class ConstrainedLocalModel
  extends AbstractFaceDetector
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 4304163800543325831L;

  @Override
  public String globalInfo() {
    return
      "Face detector based on a constrained local model. Fits a 3D face model to each detection.\n\n"
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
    result.setValue(Field.AUTHOR, "Jason M. Saragih and Simon Lucey and Jeffrey F. Cohn");
    result.setValue(Field.TITLE, "Face alignment through subspace constrained mean-shifts");
    result.setValue(Field.BOOKTITLE, "IEEE 12th International Conference on Computer Vision, ICCV 2009, Kyoto, Japan, September 27 - October 4, 2009");
    result.setValue(Field.YEAR, "2009");
    result.setValue(Field.PAGES, "1034-1041");
    result.setValue(Field.PUBLISHER, "IEEE");

    return result;
  }

  /**
   * Creates a new instance of the face detector.
   *
   * @return		the instance
   */
  @Override
  protected FaceDetector newInstance() {
    return new CLMFaceDetector();
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
