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

/*
 * AbstractImplicitBackgroundPNGAnnotationImageSegmentationWriter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

/**
 * Ancestor for image segmentation annotation writers that store the annotations in a single PNG file
 * and can use an implicit background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImplicitBackgroundPNGAnnotationImageSegmentationWriter
  extends AbstractPNGAnnotationImageSegmentationWriter {

  private static final long serialVersionUID = 3566330074754565825L;

  /** whether to use an implicit background (ie first layer = 1). */
  protected boolean m_ImplicitBackground;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "implicit-background", "implicitBackground",
        false);
  }

  /**
   * Sets whether to use an implicit background, ie layers with start at index 1 rather than 0.
   *
   * @param value	true if to use implicit background
   */
  public void setImplicitBackground(boolean value) {
    m_ImplicitBackground = value;
    reset();
  }

  /**
   * Returns whether to use an implicit background, ie layers with start at index 1 rather than 0.
   *
   * @return		true if to use implicit background
   */
  public boolean getImplicitBackground() {
    return m_ImplicitBackground;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String implicitBackgroundTipText() {
    return "When using an implicit background, the layers will start at index 1 rather than 0.";
  }
}
