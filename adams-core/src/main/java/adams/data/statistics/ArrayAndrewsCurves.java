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
 * ArrayAndrewsCurves.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;

/**
 <!-- globalinfo-start -->
 * Generates Andrews Curves from array data.<br/>
 * César Ignacio García Osorio, Colin Fyfe (2003). AN EXTENSION OF ANDREWS CURVES FOR DATA ANALYSIS.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-points &lt;int&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The number of points to generate for the curves.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class ArrayAndrewsCurves<T extends Number>
  extends AbstractArrayStatistic<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6455313543009954062L;
  
  /** the number of data points. */
  protected int m_NumPoints;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates Andrews Curves from array data.\n"
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
    result.setValue(Field.YEAR, "2003");
    result.setValue(Field.AUTHOR, "César Ignacio García Osorio and Colin Fyfe");
    result.setValue(Field.TITLE, "AN EXTENSION OF ANDREWS CURVES FOR DATA ANALYSIS");
    result.setValue(Field.HTTP, "http://cib.uco.es/documents/Garcia03SIGEF.pdf");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-points", "numPoints",
	    100, 1, null);
  }

  /**
   * Sets the number of points to generate.
   *
   * @param value	the number of points
   */
  public void setNumPoints(int value) {
    m_NumPoints = value;
    reset();
  }

  /**
   * Returns the number of points to generate.
   *
   * @return		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsTipText() {
    return "The number of points to generate for the curves.";
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  @Override
  public int getMin() {
    return 1;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  @Override
  public int getMax() {
    return -1;
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  @Override
  protected StatisticContainer doCalculate() {
    StatisticContainer<Number>	result;
    int				i;
    String			prefix;
    int				r;
    double			t;
    double			y;
    int				n;
    Number[]			values;

    result = new StatisticContainer<Number>(size(), m_NumPoints);

    prefix = "t-";

    for (i = 0; i < m_NumPoints; i++)
      result.setHeader(i, prefix + (i+1));
    
    for (r = 0; r < size(); r++) {
      values = get(r);
      for (i = 0; i < m_NumPoints; i++) {
	t = -Math.PI + (Math.PI * 2) / m_NumPoints * i;
	y = values[0].doubleValue() / Math.sqrt(2);
	for (n = 1; n < values.length; n++) {
	  if ((n + 1) % 2 == 0)
	    y += values[n].doubleValue() * Math.sin(t * Math.ceil(n / 2));
	  else
	    y += values[n].doubleValue() * Math.cos(t * Math.ceil(n / 2));
	}
	result.setCell(r, i, y);
      }
    }

    return result;
  }
}
