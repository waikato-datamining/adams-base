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
 * InputSmearing.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import adams.data.statistics.StatUtils;
import adams.data.weka.WekaAttributeRange;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Randomizable;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InputSmearing
  extends SimpleBatchFilter
  implements Randomizable, UnsupervisedFilter, TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4180301757935955561L;

  /** The random number seed. */
  protected int m_Seed = 1;

  /** the range of the attributes to work on. */
  protected WekaAttributeRange m_AttributeRange = new WekaAttributeRange(WekaAttributeRange.ALL);

  /** the standard deviation multiplier to use. */
  protected double m_StdDev = 1.0;

  /** the indices to work on. */
  protected int[] m_Indices = new int[0];

  /** the std devs to use. */
  protected Double[] m_StdDevs = new Double[0];

  /** the random number generator to use. */
  protected Random m_Random = new Random(m_Seed);

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
	"Performs input smearing on numeric attributes.\n\n"
      + "For more information, see:\n"
      + getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;

    result = new TechnicalInformation(Type.INCOLLECTION);
    result.setValue(Field.BOOKTITLE, "Advances in Knowledge Discovery and Data Mining");
    result.setValue(Field.EDITOR, "Ng, Wee-Keong and Kitsuregawa, Masaru and Li, Jianzhong and Chang, Kuiyu");
    result.setValue(Field.TITLE, "Improving on Bagging with Input Smearing");
    result.setValue(Field.AUTHOR, "Frank, Eibe and Pfahringer, Bernhard");
    result.setValue(Field.SERIES, "Lecture Notes in Computer Science");
    result.setValue(Field.VOLUME, "3918");
    result.setValue(Field.PAGES, "97-106");
    result.setValue(Field.YEAR, "2006");
    result.setValue(Field.ISBN, "978-3-540-33206-0");
    result.setValue(Field.URL, "http://dx.doi.org/10.1007/11731139_14");
    result.setValue(Field.PUBLISHER, "Springer Berlin Heidelberg");

    return result;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<Option>();

    result.addElement(new Option(
      "\tRandom number seed.\n"
	+ "\t(default 1)",
      "S", 1, "-S <num>"));

    result.addElement(new Option(
      "\tThe multiplier for the standard deviation of a numeric attribute\n"
	+ "\tto use for performing the smearing (default 1.0)",
      "stddev", 1, "-stddev <number>"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }


  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption("R", options);
    if (!tmpStr.isEmpty())
      setAttributeRange(new WekaAttributeRange(tmpStr));
    else
      setAttributeRange(new WekaAttributeRange(WekaAttributeRange.ALL));

    tmpStr = Utils.getOption('S', options);
    if (!tmpStr.isEmpty())
      setSeed(Integer.parseInt(tmpStr));
    else
      setSeed(1);

    tmpStr = Utils.getOption("stddev", options);
    if (!tmpStr.isEmpty())
      setStdDev(Double.parseDouble(tmpStr));
    else
      setStdDev(1.0);

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String> result = new Vector<String>();

    result.add("-R");
    result.add(getAttributeRange().getRange());

    result.add("-S");
    result.add("" + getSeed());

    result.add("-stddev");
    result.add("" + getStdDev());

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Set the seed for random number generation.
   *
   * @param seed the seed
   */
  public void setSeed(int seed) {
    m_Seed = seed;
    reset();
  }

  /**
   * Gets the seed for the random number generations
   *
   * @return the seed for the random number generation
   */
  public int getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String seedTipText() {
    return "The random number seed to be used.";
  }

  /**
   * Gets the multiplier for the standard deviation to use for input smearing.
   *
   * @return the multiplier
   */
  public double getStdDev() {
    return m_StdDev;
  }

  /**
   * Sets the multiplier for the standard deviation to use for input smearing.
   *
   * @param value the multiplier
   */
  public void setStdDev(double value) {
    m_StdDev = value;
    reset();
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String stdDevTipText() {
    return "The standard deviation to use for the input smearing.";
  }

  /**
   * Sets the range (1-based) of the attributes to work on.
   *
   * @param value 	the range (1-based)
   */
  public void setAttributeRange(WekaAttributeRange value) {
    m_AttributeRange = value;
    reset();
  }

  /**
   * Returns the 1-based range of the attributes to work on.
   *
   * @return 		the range (1-based)
   */
  public WekaAttributeRange getAttributeRange() {
    return m_AttributeRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeRangeTipText() {
    return "The range of attributes to work on; " + m_AttributeRange.getExample();
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);
    result.disable(Capability.RELATIONAL_ATTRIBUTES);

    // classes
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.disable(Capability.RELATIONAL_CLASS);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * hasImmediateOutputFormat() returns false, then this method will called
   * from batchFinished() after the call of preprocess(Instances), in which,
   * e.g., statistics for the actual processing step can be gathered.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    m_AttributeRange.setData(inputFormat);
    return new Instances(inputFormat, 0);
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   * @see               #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances	result;
    Instance	newInst;
    double[]	values;
    int		i;

    // determine stdevs in data
    if (!isFirstBatchDone()) {
      m_Random  = new Random(m_Seed);
      m_Indices = m_AttributeRange.getIntIndices();
      m_StdDevs = new Double[m_Indices.length];
      for (i = 0; i < m_Indices.length; i++) {
	values       = instances.attributeToDoubleArray(m_Indices[i]);
	m_StdDevs[i] = StatUtils.stddev(values, true);
      }
    }

    result = getOutputFormat();

    // add noise
    for (Instance inst: instances) {
      newInst = (Instance) inst.copy();
      for (i = 0; i < m_Indices.length; i++)
	newInst.setValue(m_Indices[i], newInst.value(m_Indices[i]) + m_Random.nextGaussian() * m_StdDevs[i] * m_StdDev);
      result.add(newInst);
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
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new InputSmearing(), args);
  }
}
