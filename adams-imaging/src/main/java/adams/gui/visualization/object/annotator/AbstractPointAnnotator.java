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
 * AbstractPointAnnotator.java
 * Copyright (C) 2021-2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.SelectionPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for point annotators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPointAnnotator
  extends AbstractReportBasedAnnotator {

  private static final long serialVersionUID = 711225849711247916L;

  /** the number of digits to use for left-padding the index. */
  protected int m_NumDigits;

  /** the current rectangles. */
  protected List<SelectionPoint> m_Locations;

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
   * Returns the default prefix to use for the objects.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultPrefix() {
    return "Point.";
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
   * Retruns all currently stored locations.
   *
   * @param report	the report to get the locations from
   * @return		the locations
   */
  protected List<SelectionPoint> getLocations(Report report) {
    List<SelectionPoint>	result;
    List<AbstractField>		fields;
    String			name;
    SelectionPoint 		point;

    result = new ArrayList<>();
    fields = report.getFields();

    for (AbstractField field: fields) {
      if (field.getName().startsWith(m_Prefix)) {
        name = field.getName().substring(m_Prefix.length());
        if (name.indexOf('.') > -1)
          name = name.substring(0, name.indexOf('.'));
        try {
          point = new SelectionPoint(
            report.getDoubleValue(m_Prefix + name + LocatedObjects.KEY_X).intValue(),
            report.getDoubleValue(m_Prefix + name + LocatedObjects.KEY_Y).intValue(),
            Integer.parseInt(name));
          if (!result.contains(point))
            result.add(point);
        }
        catch (Exception e) {
          // ignored
        }
      }
    }

    return result;
  }
}
