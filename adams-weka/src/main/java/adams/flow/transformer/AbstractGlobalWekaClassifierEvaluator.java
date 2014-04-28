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
 * AbstractGlobalWekaClassifierEvaluator.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallabledActorHelper;
import adams.flow.source.WekaClassifierSetup;

/**
 * Ancestor for classifier evaluators that make use of a callable classifier.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGlobalWekaClassifierEvaluator
  extends AbstractWekaClassifierEvaluator {

  /** for serialization. */
  private static final long serialVersionUID = 3440872619963043115L;

  /** the name of the callable weka classifier. */
  protected CallableActorReference m_Classifier;

  /** whether to discard predictions. */
  protected boolean m_DiscardPredictions;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new CallableActorReference(WekaClassifierSetup.class.getSimpleName()));

    m_OptionManager.add(
	    "no-predictions", "discardPredictions",
	    false);
  }

  /**
   * Sets the name of the callable classifier to use.
   *
   * @param value	the name
   */
  public void setClassifier(CallableActorReference value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the name of the callable classifier in use.
   *
   * @return		the name
   */
  public CallableActorReference getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String classifierTipText();

  /**
   * Sets whether to discard the predictions instead of collecting them
   * for future use, in order to conserve memory.
   *
   * @param value	true if to discard predictions
   */
  public void setDiscardPredictions(boolean value) {
    m_DiscardPredictions = value;
    reset();
  }

  /**
   * Returns whether to discard the predictions in order to preserve memory.
   *
   * @return		true if predictions discarded
   */
  public boolean getDiscardPredictions() {
    return m_DiscardPredictions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String discardPredictionsTipText() {
    return
        "If enabled, the collection of predictions during evaluation is "
      + "suppressed, wich will conserve memory.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "classifier", m_Classifier);
    result += QuickInfoHelper.toString(this, "discardPredictions", m_DiscardPredictions, "discarding predictions", ",");
    
    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      variable = getOptionManager().getVariableForProperty("classifier");
      if (variable == null) {
	if (m_Classifier.isEmpty())
	  result = "No classifier specified!";
      }
    }

    return result;
  }

  /**
   * Returns an instance of the callable classifier.
   *
   * @return		the classifier
   */
  protected weka.classifiers.Classifier getClassifierInstance() {
    return (weka.classifiers.Classifier) CallabledActorHelper.getSetup(weka.classifiers.Classifier.class, m_Classifier, this);
  }
}
