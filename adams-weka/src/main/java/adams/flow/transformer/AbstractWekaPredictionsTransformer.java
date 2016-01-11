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
 * AbstractWekaPredictionsTransformer.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import weka.classifiers.Evaluation;
import adams.flow.container.WekaEvaluationContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for transformers that convert the predictions stored in an
 * Evaluation object into a different format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWekaPredictionsTransformer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8682062846689759416L;

  /** whether to prefix the labels with a 1-based index (only nominal classes). */
  protected boolean m_AddLabelIndex;

  /** whether to add an error colunm. */
  protected boolean m_ShowError;

  /** whether to output the probability of the prediction (only nominal classes). */
  protected boolean m_ShowProbability;

  /** whether to output the class distribution (only nominal classes). */
  protected boolean m_ShowDistribution;

  /** whether to output the weight as well. */
  protected boolean m_ShowWeight;

  /** whether to align output with original dataset (if possible). */
  protected boolean m_UseOriginalIndices;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "add-index", "addLabelIndex",
	    false);

    m_OptionManager.add(
	    "error", "showError",
	    false);

    m_OptionManager.add(
	    "probability", "showProbability",
	    false);

    m_OptionManager.add(
	    "distribution", "showDistribution",
	    false);

    m_OptionManager.add(
	    "weight", "showWeight",
	    false);

    m_OptionManager.add(
	    "use-original-indices", "useOriginalIndices",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "addLabelIndex", m_AddLabelIndex, "label-index"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "showError", m_ShowError, "error"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "showProbability", m_ShowProbability, "probability"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "showDistribution", m_ShowDistribution, "distribution"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "showWeight", m_ShowWeight, "weight"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useOriginalWeights", m_UseOriginalIndices, "original indices"));
    result = QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets whether to prefix the labels with the index.
   *
   * @param value	true if the label is prefixed with the index
   */
  public void setAddLabelIndex(boolean value) {
    m_AddLabelIndex = value;
    reset();
  }

  /**
   * Returns whether to show the error as well.
   *
   * @return		true if the label is prefixed with the index
   */
  public boolean getAddLabelIndex() {
    return m_AddLabelIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addLabelIndexTipText() {
    return "If set to true, then the label is prefixed with the index.";
  }

  /**
   * Sets whether to show the error as well.
   *
   * @param value	true if the error is to be displayed as well
   */
  public void setShowError(boolean value) {
    m_ShowError = value;
    reset();
  }

  /**
   * Returns whether to show the error as well.
   *
   * @return		true if the error is displayed as well
   */
  public boolean getShowError() {
    return m_ShowError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showErrorTipText() {
    return "If set to true, then the error will be displayed as well.";
  }

  /**
   * Sets whether to show the probability of the prediction as well.
   *
   * @param value	true if the probability is to be displayed as well
   */
  public void setShowProbability(boolean value) {
    m_ShowProbability = value;
    reset();
  }

  /**
   * Returns whether to show the probability as well.
   *
   * @return		true if the probability is displayed as well
   */
  public boolean getShowProbability() {
    return m_ShowProbability;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showProbabilityTipText() {
    return
        "If set to true, then the probability of the prediction will be "
      + "displayed as well (only for nominal class attributes).";
  }

  /**
   * Sets whether to show the class distribution as well.
   *
   * @param value	true if the class distribution is to be displayed as well
   */
  public void setShowDistribution(boolean value) {
    m_ShowDistribution = value;
    reset();
  }

  /**
   * Returns whether to show the class distribution as well.
   *
   * @return		true if the class distribution is displayed as well
   */
  public boolean getShowDistribution() {
    return m_ShowDistribution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showDistributionTipText() {
    return
        "If set to true, then the class distribution will be displayed as "
      + "well (only for nominal class attributes).";
  }

  /**
   * Sets whether to show the weight as well.
   *
   * @param value	true if the weight is to be displayed as well
   */
  public void setShowWeight(boolean value) {
    m_ShowWeight = value;
    reset();
  }

  /**
   * Returns whether to show the weight as well.
   *
   * @return		true if the weight is displayed as well
   */
  public boolean getShowWeight() {
    return m_ShowWeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showWeightTipText() {
    return
        "If set to true, then the instance weight will be displayed as well.";
  }

  /**
   * Sets whether to align with original data
   * (requires: WekaEvaluationContainer as input and original indices in container).
   *
   * @param value	true if to align with original data
   */
  public void setUseOriginalIndices(boolean value) {
    m_UseOriginalIndices = value;
    reset();
  }

  /**
   * Returns whether to align with original data
   * (requires: WekaEvaluationContainer as input and original indices in container).
   *
   * @return		true if to align with original data
   */
  public boolean getUseOriginalIndices() {
    return m_UseOriginalIndices;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useOriginalIndicesTipText() {
    return
        "If set to true, the input token is a " + WekaEvaluationContainer.class.getName()
          + " and it contains the original indices ('" + WekaEvaluationContainer.VALUE_ORIGINALINDICES + "')"
          + " then the output will get aligned with the original data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }
}
