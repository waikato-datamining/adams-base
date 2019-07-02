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
 * ManualBinning.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.core.QuickInfoHelper;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.List;

/**
 * Generates a predefined number of equal sized bins.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of payload
 */
public class ManualBinning<T>
  extends AbstractEqualWidthBinningAlgorithm<T> {

  private static final long serialVersionUID = -3591626302229910556L;

  /** the number of bins. */
  protected int m_NumBins;

  /** whether to use fixed min/max for manual bin calculation. */
  protected boolean m_UseFixedMinMax;

  /** the manual minimum. */
  protected double m_ManualMin;

  /** the manual maximum. */
  protected double m_ManualMax;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a predefined number of equal sized bins.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-bins", "numBins",
      10, 1, null);

    m_OptionManager.add(
      "use-fixed-min-max", "useFixedMinMax",
      false);

    m_OptionManager.add(
      "manual-min", "manualMin",
      0.0);

    m_OptionManager.add(
      "manual-max", "manualMax",
      1.0);
  }

  /**
   * Sets the number of bins to use.
   *
   * @param value 	the number of bins
   */
  public void setNumBins(int value) {
    if (getOptionManager().isValid("numBins", value)) {
      m_NumBins = value;
      reset();
    }
  }

  /**
   * Returns the number of bins to use.
   *
   * @return 		the number of bins
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
    return "The number of bins to use.";
  }

  /**
   * Sets whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @param value 	true if to use user-supplied min/max
   */
  public void setUseFixedMinMax(boolean value) {
    m_UseFixedMinMax = value;
    reset();
  }

  /**
   * Returns whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @return 		true if to use user-supplied min/max
   */
  public boolean getUseFixedMinMax() {
    return m_UseFixedMinMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFixedMinMaxTipText() {
    return
	"If enabled, then the user-specified min/max values are used for the "
	+ "bin calculation rather than the min/max from the data.";
  }

  /**
   * Sets the minimum to use with user-supplied min/max enabled.
   *
   * @param value 	the minimum
   */
  public void setManualMin(double value) {
    if (getOptionManager().isValid("manualMin", value)) {
      m_ManualMin = value;
      reset();
    }
  }

  /**
   * Returns the minimum to use with user-supplied min/max enabled.
   *
   * @return 		the minimum
   */
  public double getManualMin() {
    return m_ManualMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMinTipText() {
    return "The minimum to use with user-supplied min/max enabled.";
  }

  /**
   * Sets the maximum to use with user-supplied max/max enabled.
   *
   * @param value 	the maximum
   */
  public void setManualMax(double value) {
    if (getOptionManager().isValid("manualMax", value)) {
      m_ManualMax = value;
      reset();
    }
  }

  /**
   * Returns the maximum to use with user-supplied max/max enabled.
   *
   * @return 		the maximum
   */
  public double getManualMax() {
    return m_ManualMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMaxTipText() {
    return "The maximum to use with user-supplied max/max enabled.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "numBins", m_NumBins, "#bins: ");
    if (m_UseFixedMinMax) {
      result += QuickInfoHelper.toString(this, "manualMin", m_ManualMin, ", min: ");
      result += QuickInfoHelper.toString(this, "manualMax", m_ManualMax, ", max: ");
    }

    return result;
  }

  /**
   * Hook method for performing checks before binning.
   *
   * @param objects	the objects to bin
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(List<Binnable<T>> objects) {
    String	result;

    result = super.check(objects);

    if (result == null) {
      if (m_UseFixedMinMax) {
        if (m_ManualMin >= m_ManualMax)
          result = "Manual min must be smaller than manual max: min=" + m_ManualMin + ", max=" + m_ManualMax;
      }
    }

    return result;
  }

  /**
   * Performs the actual bin generation on the provided objects.
   *
   * @param objects	the objects to bin
   * @return		the generated bins
   * @throws IllegalStateException	if binning fails
   */
  @Override
  protected List<Bin<T>> doGenerateBins(List<Binnable<T>> objects) {
    Struct2<Double,Double> 	minMax;

    if (m_UseFixedMinMax) {
      minMax = getMinMax(objects);
      if (minMax.value1 < m_ManualMin)
	throw new IllegalStateException("Manual min larger than smallest value: " + m_ManualMin + " > " + minMax.value1);
      if (minMax.value2 > m_ManualMax)
	throw new IllegalStateException("Manual max smaller than largest value: " + m_ManualMax + " < " + minMax.value2);
      return doGenerateBins(m_ManualMin, m_ManualMax, m_NumBins);
    }
    else {
      return doGenerateBins(objects, m_NumBins);
    }
  }
}
