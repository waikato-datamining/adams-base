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
 * AbstractRectangleBasedAnnotator.java
 * Copyright (C) 2020-2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.data.report.AnnotationHelper;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.SelectionRectangle;

import java.util.List;

/**
 * Annotators that use a rectangle based approach.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRectangleBasedAnnotator
  extends AbstractReportBasedAnnotator {

  private static final long serialVersionUID = 3536654827455977474L;

  /** the number of digits to use for left-padding the index. */
  protected int m_NumDigits;

  /** the current rectangles. */
  protected List<SelectionRectangle> m_Locations;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-digits", "numDigits",
      getDefaultNumDigits(), 0, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Locations = null;
  }

  /**
   * Returns the default prefix to use for the objects.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultPrefix() {
    return LocatedObjects.DEFAULT_PREFIX;
  }

  /**
   * Returns the default number of digits to use.
   *
   * @return		the default
   */
  protected int getDefaultNumDigits() {
    return 4;
  }

  /**
   * Sets the number of digits to use for the left-padded index.
   *
   * @param value 	the number of digits
   */
  public void setNumDigits(int value) {
    m_NumDigits = value;
    reset();
  }

  /**
   * Returns the number of digits to use for the left-padded index.
   *
   * @return 		the number of digits
   */
  public int getNumDigits() {
    return m_NumDigits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDigitsTipText() {
    return "The number of digits to use for left-padding the index with zeroes.";
  }

  /**
   * Hook method for when annotations change.
   */
  @Override
  public void annotationsChanged() {
    super.annotationsChanged();
    m_Locations = null;
  }

  /**
   * Returns all currently stored locations.
   *
   * @param report	the report to get the locations from
   * @return		the locations
   */
  protected List<SelectionRectangle> getLocations(Report report) {
    return AnnotationHelper.getLocations(report, m_Prefix);
  }
}
