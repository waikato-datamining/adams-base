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
 * AbstractLinearRegressionBased.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.baseline;

import java.util.logging.Level;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.SelectedTag;
import adams.core.Utils;
import adams.data.container.DataContainer;

/**
 * Abstract ancestor for linear regression based baseline correction schemes.
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractLinearRegressionBased<T extends DataContainer>
  extends AbstractBaselineCorrection<T> {

  /** for serialization. */
  private static final long serialVersionUID = -6634948158083409766L;

  /** the ridge. */
  protected double m_Ridge;

  /** whether to return the line as fake data or the corrected data. */
  protected boolean m_GenerateLine;

  /**
   * Returns a string describing the object.
   *
   * @return         a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A simple linear regression based baseline correction scheme.\n"
      + "Fits a line through the data using linear regression and then "
      + "removes this line from the data to correct the baseline.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "ridge", "ridge",
	    1.0E-8);

    m_OptionManager.add(
	    "line", "generateLine",
	    false);
  }

  /**
   * Sets the ridge parameter.
   *
   * @param value	the ridge
   */
  public void setRidge(double value) {
    m_Ridge = value;
    reset();
  }

  /**
   * Returns the ridge parameter.
   *
   * @return		the ridge
   */
  public double getRidge() {
    return m_Ridge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ridgeTipText() {
    return "The ridge parameter for linear regression.";
  }

  /**
   * Sets whether to return the line as fake data or the corrected data.
   *
   * @param value 	true if debug output should be printed
   */
  public void setGenerateLine(boolean value) {
    m_GenerateLine = value;
    reset();
  }

  /**
   * Returns whether to return the line as fake data or the corrected data.
   *
   * @return 		true if debugging output is on
   */
  public boolean getGenerateLine() {
    return m_GenerateLine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generateLineTipText() {
    return "Whether to generate the determined line instead of correcting the data.";
  }

  /**
   * Returns a dataset containing the x and y values.
   *
   * @param data	the original data
   * @return		the dataset for LinearRegression
   */
  protected abstract Instances getInstances(T data);

  /**
   * Corrects the data with the given coefficients.
   *
   * @param data	the original data
   * @param coeff	the coefficients to use for correcting the data,
   * 			the last element is the offset
   * @return		the baseline corrected data
   */
  protected abstract T correctData(T data, double[] coeff);

  /**
   * Generates fake data for the plotting the line.
   *
   * @param data	the original data
   * @param coeff	the coefficients to use for generating the line data,
   * 			the last element is the offset
   * @return		the fake data for the line
   */
  protected abstract T generateLine(T data, double[] coeff);


  /**
   * Performs the actual correcting.
   *
   * @param data	the data to correct
   * @return		the corrected data
   */
  @Override
  protected T processData(T data) {
    T			result;
    Instances		inst;
    LinearRegression	linear;
    double[]		coeff;

    inst = getInstances(data);
    if (isLoggingEnabled())
      getLogger().info("inst:\n" + inst);

    try {
      linear = new LinearRegression();
      linear.setAttributeSelectionMethod(new SelectedTag(LinearRegression.SELECTION_NONE, LinearRegression.TAGS_SELECTION));
      linear.setEliminateColinearAttributes(false);
      linear.buildClassifier(inst);
      coeff = linear.coefficients();
      if (isLoggingEnabled())
	getLogger().info("coeff: " + Utils.arrayToString(coeff));

      if (getGenerateLine())
	result = generateLine(data, coeff);
      else
	result = correctData(data, coeff);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to apply linear regression", e);
      result = (T) data.getClone();
    }

    return result;
  }
}
