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
 * Veto.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import adams.data.weka.WekaLabelIndex;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * If the specified label is predicted by the required minimum number of classifiers of the ensemble, then this label is predicted. Otherwise, Vote with majority rule is used.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -label &lt;value&gt;
 *  The index of the label to check.
 *  (default: first)</pre>
 * 
 * <pre> -support &lt;value&gt;
 *  The percentage (0-1 excl) or number of base-classifiers (&gt;= 1) that need to chose the label in order to predict it
 *  (default: 1.0)</pre>
 * 
 * <pre> -B &lt;classifier specification&gt;
 *  Full class name of classifier to include, followed
 *  by scheme options. May be specified multiple times.
 *  (default: "weka.classifiers.rules.ZeroR")</pre>
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Veto
  extends MultipleClassifiersCombiner  {

  private static final long serialVersionUID = 943666951855888860L;

  /** the label to check. */
  protected WekaLabelIndex m_Label = new WekaLabelIndex(WekaLabelIndex.FIRST);

  /** the index of the label to check. */
  protected int m_ActualLabel;

  /** the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it. */
  protected double m_Support = getDefaultSupport();

  /** the actual number of classifiers that need to support the label. */
  protected int m_ActualSupport;

  /** the ensemble. */
  protected Vote m_Vote = null;

  /**
   * Returns a string describing classifier
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "If the specified label is predicted by the required minimum number "
	+ "of classifiers of the ensemble, then this label is predicted. "
	+ "Otherwise, Vote with majority rule is used.";
  }

  /**
   * Returns the default label index.
   *
   * @return 		the default
   */
  protected WekaLabelIndex getDefaultLabel() {
    return new WekaLabelIndex(WekaLabelIndex.FIRST);
  }

  /**
   * Sets the label index to use.
   *
   * @param value 	the label index
   */
  public void setLabel(WekaLabelIndex value) {
    m_Label = value;
  }

  /**
   * Returns the label index.
   *
   * @return 		the label index
   */
  public WekaLabelIndex getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String labelTipText() {
    return "The index of the label to check.";
  }

  /**
   * Returns the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it.
   *
   * @return 		the default
   */
  protected double getDefaultSupport() {
    return 1.0;
  }

  /**
   * Sets the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it.
   *
   * @param value 	the support
   */
  public void setSupport(double value) {
    if (value > 0)
      m_Support = value;
    else
      System.err.println("Support must meet >0, provided: " + value);
  }

  /**
   * Returns the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it.
   *
   * @return 		the support
   */
  public double getSupport() {
    return m_Support;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String supportTipText() {
    return "The percentage (0-1 excl) or number of base-classifiers (>= 1) that need to chose the label in order to predict it";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, labelTipText(), "" + getDefaultLabel().getIndex(), "label");
    WekaOptionUtils.addOption(result, supportTipText(), "" + getDefaultSupport(), "support");
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
    setLabel(new WekaLabelIndex(WekaOptionUtils.parse(options, "label", getDefaultLabel().getIndex())));
    setSupport(WekaOptionUtils.parse(options, "support", getDefaultSupport()));
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
    WekaOptionUtils.add(result, "label", getLabel().getIndex());
    WekaOptionUtils.add(result, "support", getSupport());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the ensemble's capabilities.
   *
   * @return		the capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = super.getCapabilities();
    result.disable(Capability.DATE_CLASS);
    result.disable(Capability.NUMERIC_CLASS);
    result.disable(Capability.RELATIONAL_CLASS);

    return super.getCapabilities();
  }

  /**
   * Builds the ensemble.
   *
   * @param data	the training data
   * @throws Exception	if training fails
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    int		i;

    getCapabilities().testWithFail(data);

    if (m_Support >= 1)
      m_ActualSupport = (int) m_Support;
    else
      m_ActualSupport = (int) Math.round(m_Support * m_Classifiers.length);
    if (getDebug())
      System.out.println("Actual support: " + m_ActualSupport);

    m_Vote = new Vote();
    m_Vote.setCombinationRule(new SelectedTag(Vote.MAJORITY_VOTING_RULE, Vote.TAGS_RULES));
    m_Vote.setClassifiers(m_Classifiers);
    m_Vote.buildClassifier(data);

    m_Label.setData(data.classAttribute());
    m_ActualLabel = m_Label.getIntIndex();
    if (getDebug())
      System.out.println("Actual label index: " + m_ActualLabel);
  }

  /**
   * Predicts the class label index for the given instance.
   *
   * @param instance		the instance to make the prediction for
   * @return			the class label index
   * @throws Exception		if prediction fails
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    int		support;
    int		label;
    int		i;

    support = 0;
    for (i = 0; i < m_Classifiers.length; i++) {
      label = (int) m_Classifiers[i].classifyInstance(instance);
      if (label == m_ActualLabel)
	support++;
    }

    if (getDebug())
      System.out.println("support[" + support + "] >= act.support[" + m_ActualSupport + "]? " + (support >= m_ActualSupport));

    if (support >= m_ActualSupport)
      return m_ActualLabel;
    else
      return m_Vote.classifyInstance(instance);
  }

  /**
   * Outputs the ensemble model.
   *
   * @return		the model
   */
  @Override
  public String toString() {
    StringBuilder	result;
    int			i;

    if (m_Vote == null)
      return "No model built yet";

    result = new StringBuilder();
    result.append(getClass().getSimpleName() + "\n");
    result.append(getClass().getSimpleName().replaceAll(".", "=") + "\n\n");
    result.append("Support: " + m_Support + "\n");
    result.append("Actual support: " + m_ActualSupport + "\n");
    result.append("Label: " + m_Label.getIndex() + "\n");
    result.append("Actual label index: " + m_ActualLabel + "\n");

    for (i = 0; i < m_Classifiers.length; i++) {
      result.append("\n");
      result.append("Classifier #" + (i+1) + "\n");
      result.append(new String("Classifier #" + (i+1)).replaceAll(".", "-") + "\n\n");
      result.append(m_Classifiers[i].toString());
    }

    return result.toString();
  }
}
