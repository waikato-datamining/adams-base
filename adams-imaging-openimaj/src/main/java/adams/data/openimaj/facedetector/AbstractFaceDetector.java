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
 * AbstractFaceDetector.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.openimaj.facedetector;

import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;
import org.openimaj.image.Image;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;

import java.util.List;

/**
 * Ancestor for face detector wrappers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFaceDetector
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -2154246904015540410L;

  /** the actual detector. */
  protected transient FaceDetector m_ActualDetector;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualDetector = null;
  }

  /**
   * Creates a new instance of the face detector.
   *
   * @return		the instance
   */
  protected abstract FaceDetector newInstance();

  /**
   * Converts the image container into the required image type for the detector.
   *
   * @param cont	the container to convert
   * @return		the generated image
   */
  protected abstract Image convert(AbstractImageContainer cont);

  /**
   * Detects the faces in the image.
   *
   * @param cont	the container with the image to analyze
   * @return		the detected faces
   */
  public List<DetectedFace> detectFaces(AbstractImageContainer cont) {
    if (m_ActualDetector == null)
      m_ActualDetector = newInstance();
    return m_ActualDetector.detectFaces(convert(cont));
  }
}
