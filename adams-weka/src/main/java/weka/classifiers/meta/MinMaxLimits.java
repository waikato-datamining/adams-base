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
 * MinMaxLimits.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import adams.core.EnumHelper;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.AttributeStats;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Allows to influence the handling of lower/upper limits of the built classifier when making predictions.<br/>
 * The following types of handling are available: AS_IS, MANUAL, CLASS_RANGE<br/>
 * Details on the types:<br/>
 * - AS_IS: prediction does not get changed<br/>
 * - MANUAL: applies the manual limit, ie at most this limit is output<br/>
 * - CLASS_RANGE: applies the percentage leeway to the class attribute range of the training set to determine the actual limit value.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -min-handling &lt;AS_IS|MANUAL|CLASS_RANGE&gt;
 *  How the lower limit is handled.
 *  (default: AS_IS)</pre>
 * 
 * <pre> -min-manual &lt;num&gt;
 *  The manual lower limit
 *  (default: 0.0)</pre>
 * 
 * <pre> -min-class-percentage &lt;num&gt;
 *  The class range percentage leeway for the lower limit
 *  0-1 = 0-100%
 *  (default: 0.0)</pre>
 * 
 * <pre> -max-handling &lt;AS_IS|MANUAL|CLASS_RANGE&gt;
 *  How the upper limit is handled.
 *  (default: AS_IS)</pre>
 * 
 * <pre> -max-manual &lt;num&gt;
 *  The manual upper limit
 *  (default: 0.0)</pre>
 * 
 * <pre> -max-class-percentage &lt;num&gt;
 *  The class range percentage leeway for the upper limit
 *  0-1 = 0-100%
 *  (default: 0.0)</pre>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MinMaxLimits
  extends SingleClassifierEnhancer
  implements WeightedInstancesHandler{

  /** for serialization. */
  private static final long serialVersionUID = 1233549562504476266L;

  /**
   * Determines the type of handling for the limit
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum LimitHandling {
    /** no special handling, just as-is. */
    AS_IS,
    /** manually supplied value for limit. */
    MANUAL,
    /** determined based on class attribute range. */
    CLASS_RANGE
  }

  /** the default handling of the lower limit. */
  public final static LimitHandling DEFAULT_MIN_HANDLING = LimitHandling.AS_IS;

  /** the default manual limit of the lower limit. */
  public final static double DEFAULT_MIN_MANUAL = 0.0;

  /** the default class range percentage for the lower limit. */
  public final static double DEFAULT_MIN_CLASS_RANGE_PERCENTAGE = 0.0;

  /** the default handling of the lower limit. */
  public final static LimitHandling DEFAULT_MAX_HANDLING = LimitHandling.AS_IS;

  /** the default manual limit of the lower limit. */
  public final static double DEFAULT_MAX_MANUAL = 0.0;

  /** the default class range percentage for the lower limit. */
  public final static double DEFAULT_MAX_CLASS_RANGE_PERCENTAGE = 0.0;

  /** how the lower limit is handled. */
  protected LimitHandling m_MinHandling = DEFAULT_MIN_HANDLING;

  /** the manual limit for the lower limit. */
  protected double m_MinManual = DEFAULT_MIN_MANUAL;

  /** the percentage leeway for the class range of the lower limit (0-1 = 0-100%). */
  protected double m_MinClassRangePercentage = DEFAULT_MIN_CLASS_RANGE_PERCENTAGE;

  /** the actual limit to use for the lower limit. */
  protected Double m_MinActual;

  /** how the upper limit is handled. */
  protected LimitHandling m_MaxHandling = DEFAULT_MAX_HANDLING;

  /** the manual limit for the upper limit. */
  protected double m_MaxManual = DEFAULT_MAX_MANUAL;

  /** the percentage leeway for the class range of the upper limit (0-1 = 0-100%). */
  protected double m_MaxClassRangePercentage = DEFAULT_MAX_CLASS_RANGE_PERCENTAGE;

  /** the actual limit to use for the upper limit. */
  protected Double m_MaxActual;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Allows to influence the handling of lower/upper limits of the built "
	+ "classifier when making predictions.\n"
	+ "The following types of handling are available: " + adams.core.Utils.flatten(EnumHelper.getValues(LimitHandling.class), ", ") + "\n"
	+ "Details on the types:\n"
	+ "- " + LimitHandling.AS_IS + ": prediction does not get changed\n"
	+ "- " + LimitHandling.MANUAL + ": applies the manual limit, ie at most this limit is output\n"
	+ "- " + LimitHandling.CLASS_RANGE + ": applies the percentage leeway to the class attribute range of the training set to determine the actual limit value.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;
    Enumeration	enm;

    result = new Vector();

    result.addElement(new Option(
      "\tHow the lower limit is handled.\n"
        + "\t(default: " + DEFAULT_MIN_HANDLING + ")",
      "min-handling", 1, "-min-handling <" + adams.core.Utils.flatten(EnumHelper.getValues(LimitHandling.class), "|") + ">"));

    result.addElement(new Option(
      "\tThe manual lower limit\n"
        + "\t(default: " + DEFAULT_MIN_MANUAL + ")",
      "min-manual", 1, "-min-manual <num>"));

    result.addElement(new Option(
      "\tThe class range percentage leeway for the lower limit\n"
        + "\t0-1 = 0-100%\n"
        + "\t(default: " + DEFAULT_MIN_CLASS_RANGE_PERCENTAGE + ")",
      "min-class-percentage", 1, "-min-class-percentage <num>"));

    result.addElement(new Option(
      "\tHow the upper limit is handled.\n"
        + "\t(default: " + DEFAULT_MAX_HANDLING + ")",
      "max-handling", 1, "-max-handling <" + adams.core.Utils.flatten(EnumHelper.getValues(LimitHandling.class), "|") + ">"));

    result.addElement(new Option(
      "\tThe manual upper limit\n"
        + "\t(default: " + DEFAULT_MAX_MANUAL + ")",
      "max-manual", 1, "-max-manual <num>"));

    result.addElement(new Option(
      "\tThe class range percentage leeway for the upper limit\n"
        + "\t0-1 = 0-100%\n"
        + "\t(default: " + DEFAULT_MAX_CLASS_RANGE_PERCENTAGE + ")",
      "max-class-percentage", 1, "-max-class-percentage <num>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption("min-handling", options);
    if (tmpStr.length() != 0)
      setMinHandling(LimitHandling.valueOf(tmpStr));
    else
      setMinHandling(DEFAULT_MIN_HANDLING);

    tmpStr = Utils.getOption("min-manual", options);
    if (tmpStr.length() != 0)
      setMinManual(Double.parseDouble(tmpStr));
    else
      setMinManual(DEFAULT_MIN_MANUAL);

    tmpStr = Utils.getOption("min-class-percentage", options);
    if (tmpStr.length() != 0)
      setMinClassRangePercentage(Double.parseDouble(tmpStr));
    else
      setMinClassRangePercentage(DEFAULT_MIN_CLASS_RANGE_PERCENTAGE);

    tmpStr = Utils.getOption("max-handling", options);
    if (tmpStr.length() != 0)
      setMaxHandling(LimitHandling.valueOf(tmpStr));
    else
      setMaxHandling(DEFAULT_MAX_HANDLING);

    tmpStr = Utils.getOption("max-manual", options);
    if (tmpStr.length() != 0)
      setMaxManual(Double.parseDouble(tmpStr));
    else
      setMaxManual(DEFAULT_MAX_MANUAL);

    tmpStr = Utils.getOption("max-class-percentage", options);
    if (tmpStr.length() != 0)
      setMaxClassRangePercentage(Double.parseDouble(tmpStr));
    else
      setMaxClassRangePercentage(DEFAULT_MAX_CLASS_RANGE_PERCENTAGE);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-min-handling");
    result.add("" + getMinHandling());

    if (getMinHandling() == LimitHandling.MANUAL) {
      result.add("-min-manual");
      result.add("" + getMinManual());
    }

    if (getMinHandling() == LimitHandling.CLASS_RANGE) {
      result.add("-min-class-percentage");
      result.add("" + getMinClassRangePercentage());
    }

    result.add("-max-handling");
    result.add("" + getMaxHandling());

    if (getMaxHandling() == LimitHandling.MANUAL) {
      result.add("-max-manual");
      result.add("" + getMaxManual());
    }

    if (getMaxHandling() == LimitHandling.CLASS_RANGE) {
      result.add("-max-class-percentage");
      result.add("" + getMaxClassRangePercentage());
    }

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Set how the lower limit is handled.
   *
   * @param value 	how the lower limit is handled
   */
  public void setMinHandling(LimitHandling value) {
    m_MinHandling = value;
  }

  /**
   * Get how the lower limit is handled.
   *
   * @return 		how the lower limit is handled
   */
  public LimitHandling getMinHandling() {
    return m_MinHandling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String minHandlingTipText() {
    return "Determines how the lower limit is handled.";
  }

  /**
   * Set the manual lower limit.
   *
   * @param value 	the manual lower limit
   */
  public void setMinManual(double value) {
    m_MinManual = value;
  }

  /**
   * Get the manual lower limit.
   *
   * @return 		the manual lower limit
   */
  public double getMinManual() {
    return m_MinManual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String minManualTipText() {
    return "In case of " + LimitHandling.MANUAL + ", defines the lower limit.";
  }

  /**
   * Set the percentage of leeway to apply to the lower limit determined by
   * the range of the class attribute in the training data.
   *
   * @param value 	the percentage (0-1 = 0-100%)
   */
  public void setMinClassRangePercentage(double value) {
    m_MinClassRangePercentage = value;
  }

  /**
   * Get the percentage of leeway to apply to the lower limit determined by
   * the range of the class attribute in the training data.
   *
   * @return 		the percentage (0-1 = 0-100%)
   */
  public double getMinClassRangePercentage() {
    return m_MinClassRangePercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String minClassRangePercentageTipText() {
    return
      "In case of " + LimitHandling.CLASS_RANGE + ", defines the leeway to "
	+ "apply to the lower limit determine by the class attribute in the "
	+ "training data (0-1 = 0-100%).";
  }

  /**
   * Set how the upper limit is handled.
   *
   * @param value 	how the upper limit is handled
   */
  public void setMaxHandling(LimitHandling value) {
    m_MaxHandling = value;
  }

  /**
   * Get how the upper limit is handled.
   *
   * @return 		how the upper limit is handled
   */
  public LimitHandling getMaxHandling() {
    return m_MaxHandling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maxHandlingTipText() {
    return "Determaxes how the upper limit is handled.";
  }

  /**
   * Set the manual upper limit.
   *
   * @param value 	the manual upper limit
   */
  public void setMaxManual(double value) {
    m_MaxManual = value;
  }

  /**
   * Get the manual upper limit.
   *
   * @return 		the manual upper limit
   */
  public double getMaxManual() {
    return m_MaxManual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maxManualTipText() {
    return "In case of " + LimitHandling.MANUAL + ", defines the upper limit.";
  }

  /**
   * Set the percentage of leeway to apply to the upper limit determaxed by
   * the range of the class attribute in the training data.
   *
   * @param value 	the percentage (0-1 = 0-100%)
   */
  public void setMaxClassRangePercentage(double value) {
    m_MaxClassRangePercentage = value;
  }

  /**
   * Get the percentage of leeway to apply to the upper limit determaxed by
   * the range of the class attribute in the training data.
   *
   * @return 		the percentage (0-1 = 0-100%)
   */
  public double getMaxClassRangePercentage() {
    return m_MaxClassRangePercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maxClassRangePercentageTipText() {
    return
      "In case of " + LimitHandling.CLASS_RANGE + ", defines the leeway to "
	+ "apply to the upper limit determaxe by the class attribute in the "
	+ "training data (0-1 = 0-100%).";
  }

  /**
   * Returns default capabilities of the base classifier.
   *
   * @return      the capabilities of the base classifier
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = super.getCapabilities();
    result.disableAllClasses();
    result.enable(Capability.NUMERIC_CLASS);

    return result;
  }

  /**
   * Builds the classifier.
   *
   * @param data		the training data
   * @throws Exception	if training fails
   */
  public void buildClassifier(Instances data) throws Exception {
    double		min;
    double		max;
    double		range;
    AttributeStats	stats;

    getCapabilities().testWithFail(data);

    stats = data.attributeStats(data.classIndex());
    min   = stats.numericStats.min;
    max   = stats.numericStats.max;
    range = max - min;
    if (getDebug()) {
      System.out.println("Min: " + min);
      System.out.println("Max: " + max);
      System.out.println("Range: " + range);
    }

    switch (m_MinHandling) {
      case AS_IS:
	m_MinActual = null;
	break;
      case MANUAL:
	m_MinActual = m_MinManual;
	break;
      case CLASS_RANGE:
	m_MinActual = min - range * m_MinClassRangePercentage;
	break;
    }
    if (getDebug())
      System.out.println("Actual lower limit: " + (m_MinActual == null ? "-none-" : "" + m_MinActual));

    switch (m_MaxHandling) {
      case AS_IS:
	m_MaxActual = null;
	break;
      case MANUAL:
	m_MaxActual = m_MaxManual;
	break;
      case CLASS_RANGE:
	m_MaxActual = max + range * m_MaxClassRangePercentage;
	break;
    }
    if (getDebug())
      System.out.println("Actual upper limit: " + (m_MaxActual == null ? "-none-" : "" + m_MaxActual));

    m_Classifier.buildClassifier(data);
  }

  /**
   * Returns the prediction.
   *
   * @param inst	the instance to predict
   * @return		the prediction
   * @throws Exception	if prediction fails
   */
  public double classifyInstance(Instance inst) throws Exception {
    double	result;

    result = m_Classifier.classifyInstance(inst);

    if (m_MinActual != null) {
      if (result < m_MinActual) {
	if (getDebug())
	  System.out.println(result + " < " + m_MinActual + " -> " + m_MinActual);
	result = m_MinActual;
      }
    }

    if (m_MaxActual != null) {
      if (result > m_MaxActual) {
	if (getDebug())
	  System.out.println(result + " > " + m_MaxActual + " -> " + m_MaxActual);
	result = m_MaxActual;
      }
    }

    return result;
  }

  /**
   * Returns description of classifier.
   *
   * @return		the model
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("MinMax\n");
    result.append("======\n\n");
    result.append("Lower limit:\n");
    result.append("- handling: " + m_MinHandling + "\n");
    result.append("- actual limit: " + (m_MinActual == null ? "N/A" : m_MinActual) + "\n");
    result.append("Upper limit\n");
    result.append("- handling: " + m_MaxHandling + "\n");
    result.append("- actual limit: " + (m_MaxActual == null ? "N/A" : m_MaxActual) + "\n");
    result.append("\n\n");

    result.append(m_Classifier.toString());

    return result.toString();
  }

  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this class.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runClassifier(new MinMaxLimits(), args);
  }
}
