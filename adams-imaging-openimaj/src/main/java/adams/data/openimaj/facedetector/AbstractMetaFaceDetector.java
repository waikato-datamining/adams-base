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
 * AbstractMetaFaceDetector.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.openimaj.facedetector;

/**
 * Ancestor for detectors that make use of a base detector.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaFaceDetector
  extends AbstractFaceDetector {

  private static final long serialVersionUID = -4353186526125934513L;

  /** the base detector to use. */
  protected AbstractFaceDetector m_Detector;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "detector", "detector",
      getDefaultDetector());
  }

  /**
   * Returns the default detector to use.
   *
   * @return		the detector
   */
  protected abstract AbstractFaceDetector getDefaultDetector();

  /**
   * Sets the base detector to use.
   *
   * @param value	the base detector
   */
  public void setDetector(AbstractFaceDetector value) {
    m_Detector = value;
    reset();
  }

  /**
   * Returns the base detector to use.
   *
   * @return		the base detector
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
    return "The base detector to use.";
  }
}
