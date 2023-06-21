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
 * SimpleDetrend.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import adams.core.Range;
import adams.core.Utils;
import adams.env.Environment;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.WekaOptionUtils;
import weka.filters.SimpleBatchFilter;
import weka.filters.unsupervised.attribute.detrend.AbstractDetrend;
import weka.filters.unsupervised.attribute.detrend.RangeBased;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Performs Detrend, using the specified correction scheme.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleDetrend
  extends SimpleBatchFilter {

  private static final long serialVersionUID = -9044105839662849793L;

  public final static String ATTRIBUTE_RANGE = "R";

  /** the range of attributes to work on. */
  protected Range m_AttributeRange = getDefaultAttributeRange();

  public final static String CORRECTION = "correction";

  /** the correction to use. */
  protected AbstractDetrend m_Correction = getDefaultCorrection();

  /** the determined indices. */
  protected int[] m_AttIndices = null;

  /** the fake wave numbers. */
  protected double[] m_WaveNo = null;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   * explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Performs Detrend, using the specified correction scheme.\n"
      + "Simply enumerates the selected attributes and uses that as wave number (starting at 0).";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, attributeRangeTipText(), getDefaultAttributeRange().getRange(), ATTRIBUTE_RANGE);
    WekaOptionUtils.addOption(result, correctionTipText(), getDefaultCorrection().toCommandLine(), CORRECTION);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setAttributeRange((Range) WekaOptionUtils.parse(options, ATTRIBUTE_RANGE, getDefaultAttributeRange()));
    setCorrection((AbstractDetrend) WekaOptionUtils.parse(options, CORRECTION, (adams.core.option.OptionHandler) getDefaultCorrection()));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, ATTRIBUTE_RANGE, getAttributeRange());
    WekaOptionUtils.add(result, CORRECTION, (adams.core.option.OptionHandler) getCorrection());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the default regular expression for identifying the attributes to process.
   *
   * @return		the default
   */
  protected Range getDefaultAttributeRange() {
    return new Range(Range.ALL);
  }

  /**
   * Sets the range of attributes to detrend.
   *
   * @param value	the range
   */
  public void setAttributeRange(Range value) {
    m_AttributeRange = value;
    reset();
  }

  /**
   * Returns the range of attributes to detrend.
   *
   * @return		the range
   */
  public Range getAttributeRange() {
    return m_AttributeRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeRangeTipText() {
    return "The range of attributes to detrend; " + m_AttributeRange.getExample();
  }

  /**
   * Returns the default correction scheme.
   *
   * @return		the default
   */
  protected AbstractDetrend getDefaultCorrection() {
    return new RangeBased();
  }

  /**
   * Sets the correction scheme to apply.
   *
   * @param value	the scheme
   */
  public void setCorrection(AbstractDetrend value) {
    m_Correction = value;
    reset();
  }

  /**
   * Returns the correction scheme to apply.
   *
   * @return		the scheme
   */
  public AbstractDetrend getCorrection() {
    return m_Correction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String correctionTipText() {
    return "The correction scheme to apply.";
  }

  @Override
  public Capabilities getCapabilities() {
    Capabilities result;

    result = new Capabilities(this);
    result.enableAll();
    result.enable(Capability.NO_CLASS);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Resets the filter.
   */
  @Override
  protected void reset() {
    super.reset();
    m_AttIndices = null;
    m_WaveNo     = null;
  }

  /**
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called from
   * batchFinished().
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    int		i;

    // determine attributes
    m_AttributeRange.setMax(inputFormat.numAttributes());
    m_AttIndices = m_AttributeRange.getIntIndices();
    if (m_AttIndices.length == 0)
      throw new Exception("No attributes identified using range: " + m_AttributeRange.getRange());
    if (getDebug())
      System.err.println(getClass().getName() + ": Identified indices: " + Utils.arrayToString(m_AttIndices));

    // extract wave numbers
    m_WaveNo = new double[m_AttIndices.length];
    for (i = 0; i < m_AttIndices.length; i++)
      m_WaveNo[i] = i;
    if (getDebug())
      System.err.println(getClass().getName() + ": Fake wave numbers: " + Utils.arrayToString(m_WaveNo));

    return new Instances(inputFormat, 0);
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   * @see #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances	result;
    int		i;
    int		n;
    Instance 	inst;
    Instance	instNew;
    double[]	data;
    double[]	values;

    if (instances.numInstances() == 0)
      return instances;

    // correct data
    result = new Instances(instances, instances.numInstances());
    for (n = 0; n < instances.numInstances(); n++) {
      inst = instances.instance(n);
      data = new double[m_AttIndices.length];
      for (i = 0; i < m_AttIndices.length; i++)
	data[i] = inst.value(m_AttIndices[i]);
      data   = m_Correction.correct(m_WaveNo, data);
      values = inst.toDoubleArray();
      for (i = 0; i < m_AttIndices.length; i++)
        values[m_AttIndices[i]] = data[i];
      instNew = inst.copy(values);
      copyValues(instNew, true, inst.dataset(), getOutputFormat());
      result.add(instNew);
    }

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    Environment.setEnvironmentClass(Environment.class);
    runFilter(new SimpleDetrend(), args);
  }
}
