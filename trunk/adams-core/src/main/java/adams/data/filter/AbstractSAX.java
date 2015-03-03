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
 * AbstractSAX.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.utils.SAXUtils;

/**
 * Ancestor for SAX filters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 * @param <T> the type of container to process
 */
public abstract class AbstractSAX<T extends DataContainer>
  extends AbstractFilter<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1836858988505886282L;

  /** the number of windows to use for PAA. */
  protected int m_NumWindows;
  
  /** the number of breakpoints to use (for the Gaussian). */
  protected int m_NumBins;
  
  /** whether to output labels or the actual distances. */
  protected boolean m_OutputLabels;
  
  /** the calculated breakpoints. */
  protected double[] m_BreakPoints;

  /** the matrix to use for the distance calculations. */
  protected double[][] m_DistMatrix;

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.PROCEEDINGS);
    result.setValue(Field.AUTHOR, "Chiu, B. and Keogh, E. and Lonardi, S.");
    result.setValue(Field.TITLE, "Probabilistic Discovery of Time Series Motifs");
    result.setValue(Field.BOOKTITLE, "9th ACM SIGKDD International Conference on Knowledge Discovery and Data Mining");
    result.setValue(Field.PAGES, "493-498");
    result.setValue(Field.YEAR, "2003");
    result.setValue(Field.LOCATION, "Washington, DC, USA");
    result.setValue(Field.PDF, "http://www.cs.ucr.edu/~eamonn/SIGKDD_Motif.pdf");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-windows", "numWindows",
	    10, 1, null);

    m_OptionManager.add(
	    "num-bins", "numBins",
	    10, 1, null);

    m_OptionManager.add(
	    "output-labels", "outputLabels",
	    true);
  }
  
  /**
   * Resets the filter.
   */
  @Override
  public void reset() {
    super.reset();
    
    m_BreakPoints = null;
    m_DistMatrix  = null;
  }

  /**
   * Sets the number of windows to use for PAA.
   *
   * @param value 	the number
   */
  public void setNumWindows(int value) {
    if (value >= 1) {
      m_NumWindows = value;
      reset();
    }
    else {
      getLogger().severe("The number of windows must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of windows to use for PAA.
   *
   * @return 		the number
   */
  public int getNumWindows() {
    return m_NumWindows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numWindowsTipText() {
    return "The number of windows to use for Piecewise Aggregate Approximation (PAA).";
  }

  /**
   * Sets the number of bins to use for the Gaussian.
   *
   * @param value 	the number
   */
  public void setNumBins(int value) {
    if (value >= 1) {
      m_NumBins = value;
      reset();
    }
    else {
      getLogger().severe("The number of bins must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of bins to use for the Gaussian.
   *
   * @return 		the number
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use for the Gaussian.";
  }

  /**
   * Sets whether to output labels or distances.
   *
   * @param value 	true if to output labels
   */
  public void setOutputLabels(boolean value) {
    m_OutputLabels = value;
    reset();
  }

  /**
   * Returns whether to output labels or distances.
   *
   * @return 		true if to output labels
   */
  public boolean getOutputLabels() {
    return m_OutputLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputLabelsTipText() {
    return "If enabled, labels are output instead of distances.";
  }

  /**
   * Computes the mean difference between data points on the X axis.
   * 
   * @param data	the data to use for the calculation
   * @return		the mean
   */
  protected abstract double getMeanDeltaX(T data);

  /**
   * Obtains the X value from the given data point.
   * 
   * @param point	the data point to extract the X value from
   * @return		the X value
   */
  protected abstract double getX(DataPoint point);

  /**
   * Obtains the Y value from the given data point.
   * 
   * @param point	the data point to extract the Y value from
   * @return		the Y value
   */
  protected abstract double getY(DataPoint point);

  /**
   * Creates a new data point from the X and Y values.
   * 
   * @param x		the raw X value
   * @param y		the raw Y value
   * @return		the data point
   */
  protected abstract DataPoint newDataPoint(double x, double y);
  
  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T				result;
    List<DataPoint>		points;
    DataPoint			point;
    double[]			values;
    int				i;
    double[]			sax;
    double			meanDelta;

    result = (T) data.getHeader();
    if (data.size() == 0)
      return result;
    
    points    = data.toList();
    meanDelta = getMeanDeltaX(data);

    if (m_BreakPoints == null)
      m_BreakPoints = SAXUtils.calcBreakPoints(m_NumBins);
    if (!m_OutputLabels && (m_DistMatrix == null))
      m_DistMatrix = SAXUtils.calcDistMatrix(m_BreakPoints);
    
    values = new double[points.size()];
    for (i = 0; i < points.size(); i++)
      values[i] = getY(points.get(i));
    
    sax = SAXUtils.toSAX(values, m_NumWindows, m_BreakPoints);
    for (i = 0; i < sax.length; i++) {
      if (m_OutputLabels)
	point = newDataPoint(
	    getX(points.get(0)) + i * meanDelta, 
	    sax[i]);
      else
	point = newDataPoint(
	    getX(points.get(0)) + i * meanDelta, 
	    SAXUtils.minDist(new double[]{sax[i]}, new double[]{0.0}, m_DistMatrix, 1));
      result.add(point);
    }
    
    return result;
  }
}
