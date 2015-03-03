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
 * AbstractLOWESS.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.utils.LOWESS;

/**
 * Abstract ancestor for LOWESS filters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Michael Fowke (msf8 at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractLOWESS<T extends DataContainer>
  extends AbstractFilter<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7714239052976065971L;
  
  /** Size of window size for calculating lowess. */
  protected int m_WindowSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A filter that applies LOWESS smoothing.\n\n"
      + "For more information see:\n\n"
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
    
    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Local Regression");
    result.setValue(Field.URL, "http://en.wikipedia.org/wiki/Lowess");
    
    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "window-size", "windowSize",
	    20, LOWESS.MIN_WINDOW_SIZE, null);
  }

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setWindowSize(int value) {
    if (value >= LOWESS.MIN_WINDOW_SIZE) {
      m_WindowSize = value;
      reset();
    }
    else {
      getLogger().severe(
	  "The window size must be at least " + LOWESS.MIN_WINDOW_SIZE + " (provided: " + value + ")!");
    }
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
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
    return "The window size to use, must be at least " + LOWESS.MIN_WINDOW_SIZE + ".";
  }

  /**
   * Returns the X/Y values of the DataPoint as Point2D.
   *
   * @param point	the point to get the X/Y values from
   * @return		the X/Y values as Point2D
   */
  protected abstract Point2D convert(DataPoint point);

  /**
   * Creates a new DataPoint from the smoothed one.
   *
   * @param smoothed	the smoothed data point
   * @return		the new DataPoint
   */
  protected abstract DataPoint newDataPoint(Point2D smoothed);

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
    List<Point2D>	raw;
    List<Point2D>	smoothed;

    raw = new ArrayList<Point2D>();
    for (i = 0; i < data.size(); i++)
      raw.add(convert((DataPoint) data.toList().get(i)));
    smoothed = LOWESS.calculate(raw, m_WindowSize);

    result = (T) data.getHeader();
    for (i = 0; i < smoothed.size(); i++)
      result.add(newDataPoint(smoothed.get(i)));

    return result;
  }
}
