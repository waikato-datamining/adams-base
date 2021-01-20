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
 * Fallback.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import adams.core.ObjectCopyHelper;
import adams.core.option.OptionUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.functions.GPD;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * In case the base classifier fails to make predictions, uses fallback one.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Fallback
  extends AbstractClassifier {

  private static final long serialVersionUID = 2724955696815687608L;

  /** the base classifier. */
  protected Classifier m_Base = getDefaultBase();

  /** the actual base classifier. */
  protected Classifier m_ActualBase;

  /** the fallback classifier. */
  protected Classifier m_Fallback = getDefaultFallback();

  /** the actual fallback classifier. */
  protected Classifier m_ActualFallback;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "In case the base classifier fails to make predictions, uses fallback one.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();

    Enumeration enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
      "\tBase classifier.\n"
	+ "\t(default: " + OptionUtils.getCommandLine(getDefaultBase()) +  ")",
      "base", 1, "-base <classname+options>"));

    result.addElement(new Option(
      "\tFallback classifier.\n"
	+ "\t(default: " + OptionUtils.getCommandLine(getDefaultBase()) +  ")",
      "fallback", 1, "-fallback <classname+options>"));

    result.addElement(new Option(
      "",
      "", 0, "\nOptions specific to base classifier "
      + getBase().getClass().getName() + ":"));
    result.addAll(Collections.list(((OptionHandler)getBase()).listOptions()));

    result.addElement(new Option(
      "",
      "", 0, "\nOptions specific to fallback classifier "
      + getFallback().getClass().getName() + ":"));
    result.addAll(Collections.list(((OptionHandler)getFallback()).listOptions()));

    return result.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String	opt;

    opt = Utils.getOption("base", options);
    if (opt.isEmpty())
      setBase(getDefaultBase());
    else
      setBase((Classifier) OptionUtils.forAnyCommandLine(Classifier.class, opt));

    opt = Utils.getOption("fallback", options);
    if (opt.isEmpty())
      setFallback(getDefaultFallback());
    else
      setFallback((Classifier) OptionUtils.forAnyCommandLine(Classifier.class, opt));

    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    result.add("-base");
    result.add(OptionUtils.getCommandLine(getBase()));

    result.add("-fallback");
    result.add(OptionUtils.getCommandLine(getFallback()));

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Returns the default base classifier.
   *
   * @return		the default
   */
  protected Classifier getDefaultBase() {
    return new GPD();
  }

  /**
   * Sets the base classifier.
   *
   * @param value	the base
   */
  public void setBase(Classifier value) {
    m_Base = value;
  }

  /**
   * Returns the base classifier.
   *
   * @return		the base
   */
  public Classifier getBase() {
    return m_Base;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String baseTipText() {
    return "The base classifier to use for making predictions.";
  }

  /**
   * Returns the default fallback classifier.
   *
   * @return		the default
   */
  protected Classifier getDefaultFallback() {
    return new ZeroR();
  }

  /**
   * Sets the fallback classifier.
   *
   * @param value	the fallback
   */
  public void setFallback(Classifier value) {
    m_Fallback = value;
  }

  /**
   * Returns the fallback classifier.
   *
   * @return		the fallback
   */
  public Classifier getFallback() {
    return m_Fallback;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String fallbackTipText() {
    return "The fallback classifier to use for making predictions.";
  }

  /**
   * Returns the Capabilities of this classifier.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = (Capabilities) m_Base.getCapabilities().clone();
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.setOwner(this);

    return result;
  }

  /**
   * Generates a classifier.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);

    data = new Instances(data);
    data.deleteWithMissingClass();

    m_ActualBase     = null;
    m_ActualFallback = null;

    try {
      if (getDebug())
        System.out.println("Training base classifier");
      m_ActualBase = ObjectCopyHelper.copyObject(m_Base);
      m_ActualBase.buildClassifier(data);
    }
    catch (Exception e) {
      System.err.println("Failed to build base classifier: " + OptionUtils.getCommandLine(m_Base));
      e.printStackTrace();
      m_ActualBase = null;
    }

    if (getDebug())
      System.out.println("Training fallback classifier");
    m_ActualFallback = ObjectCopyHelper.copyObject(m_Fallback);
    m_ActualFallback.buildClassifier(data);
  }

  /**
   * Classifies the given test instance.
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   *         Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    if (m_ActualBase == null)
      return m_ActualFallback.classifyInstance(instance);

    try {
      return m_ActualBase.classifyInstance(instance);
    }
    catch (Exception e) {
      if (getDebug())
        System.err.println("Falling back, classifyInstance failed on: " + instance);
      return m_ActualFallback.classifyInstance(instance);
    }
  }

  /**
   * Predicts the class memberships for a given instance. If an instance is
   * unclassified, the returned array elements must be all zero. If the class is
   * numeric, the array must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership probabilities of the
   *         test instance in each class or the numeric prediction
   * @throws Exception if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    if (m_ActualBase == null)
      return m_ActualFallback.distributionForInstance(instance);

    try {
      return m_ActualBase.distributionForInstance(instance);
    }
    catch (Exception e) {
      if (getDebug())
        System.err.println("Falling back, distributionForInstance failed on: " + instance);
      return m_ActualFallback.distributionForInstance(instance);
    }
  }

  /**
   * Returns a description of the model.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("Base\n").append("====\n\n").append("" + m_ActualBase);
    result.append("\n");
    result.append("Fallback\n").append("========\n\n").append(""  +m_ActualFallback);

    return result.toString();
  }
}
