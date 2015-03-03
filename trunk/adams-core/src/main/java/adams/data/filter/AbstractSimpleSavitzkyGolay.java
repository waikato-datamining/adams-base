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
 * AbstractSimpleSavitzkyGolay.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;

/**
 * Abstract ancestor for simple Savitzky-Golay filters.
 *
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractSimpleSavitzkyGolay<T extends DataContainer>
  extends AbstractFilter<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 343582700139292935L;

  /** window size. */
  protected int m_WindowSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A filter that applies a simplified Savitzky-Golay smoothing.\n\n"
      + "For more information on Savitzky-Golay see:\n\n"
      + getTechnicalInformation().toString();
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
    result.setValue(Field.AUTHOR, "A. Savitzky and Marcel J.E. Golay");
    result.setValue(Field.TITLE, "Smoothing and Differentiation of Data by Simplified Least Squares Procedures");
    result.setValue(Field.JOURNAL, "Analytical Chemistry");
    result.setValue(Field.VOLUME, "36");
    result.setValue(Field.PAGES, "1627-1639");
    result.setValue(Field.YEAR, "1964");
    result.setValue(Field.HTTP, "http://dx.doi.org/10.1021/ac60214a047");

    return result;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "windowSize", "windowSize",
	    7);
  }

  /**
   * Sets the maximum size of the window to optimize (odd, positive number).
   *
   * @param value 	the size
   */
  public void setWindowSize(int value) {
    if ( (value == -1) || ((value >= 3) && (value % 2 == 1)) ) {
      m_WindowSize = value;
      reset();
    }
    else {
      getLogger().severe(
	  "The window size must be at least 3 and an odd number "
	  + "or -1 if no optimization is to be used (provided: " + value + ")!");
    }
  }

  /**
   * Returns the size of the window.
   *
   * @return 		the size
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return "The window size to use for smoothing.";
  }

  /**
   * Returns the Y-value of the point.
   *
   * @param point	the DataPoint to get the Y-value from
   * @return		the Y-value
   */
  protected abstract double getValue(DataPoint point);

  /**
   * Creates a new DataPoint based on the old one and the new Y-value.
   *
   * @param old		the old DataPoint
   * @param y		the new Y-value
   * @return		the new DataPoint
   */
  protected abstract DataPoint newDataPoint(DataPoint old, double y);

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T			result;
    int			i;
    List<DataPoint>	points;
    DataPoint		newPoint;
    int 		winOff;
    double 		y;
    int 		j;

    result = (T) data.getHeader();
    points = data.toList();
    winOff = (m_WindowSize - 1)/2;
    for (i = winOff; i < points.size() - winOff; i++) {
      y = 0;
      for (j = -winOff; j <= winOff; j++)
	y += j * getValue(points.get(i+j));
      newPoint = newDataPoint(points.get(i - winOff), y);
      result.add(newPoint);
    }

    return result;
  }
}
