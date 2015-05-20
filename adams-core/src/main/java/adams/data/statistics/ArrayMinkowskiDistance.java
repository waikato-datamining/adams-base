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
 * ArrayMinkowskiDistance.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 <!-- globalinfo-start -->
 * Calculates the Minkowski distance between the first array and the remaining arrays. The arrays must be numeric, of course.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-exponent &lt;double&gt; (property: exponent)
 * &nbsp;&nbsp;&nbsp;The exponent 'p' to use for 'sum(|x-y|^p)^(1&#47;p)'.
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayMinkowskiDistance
  extends AbstractArrayDistance {

  /** for serialization. */
  private static final long serialVersionUID = 6119558442855668422L;
  
  /** the exponent p. */
  protected double m_Exponent;
  
  /**
   * Returns the name of the distance.
   * 
   * @return		the name
   */
  @Override
  protected String getDistanceName() {
    return "Minkowski";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "exponent", "exponent",
	    2.0);
  }

  /**
   * Sets the exponent p.
   *
   * @param value	the exponent
   */
  public void setExponent(double value) {
    m_Exponent = value;
    reset();
  }

  /**
   * Returns the exponent p.
   *
   * @return		the exponent
   */
  public double getExponent() {
    return m_Exponent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String exponentTipText() {
    return "The exponent 'p' to use for 'sum(|x-y|^p)^(1/p)'.";
  }
  
  /**
   * Creates the cell header prefix to use.
   * 
   * @return		the prefix
   */
  @Override
  protected String createCellHeader() {
    return super.createCellHeader() + " (p=" + m_Exponent + ")";
  }

  /**
   * Calculates the distance between the two arrays.
   * 
   * @param first	the first array
   * @param second	the second array
   * @return		the distance
   */
  @Override
  protected double calcDistance(double[] first, double[] second) {
    double	result;
    int		i;
    
    result = 0.0;

    for (i = 0; i < first.length; i++)
      result += Math.pow(Math.abs(first[i] - second[i]), m_Exponent);
    
    result = Math.pow(result, 1/m_Exponent);
    
    return result;
  }
}
