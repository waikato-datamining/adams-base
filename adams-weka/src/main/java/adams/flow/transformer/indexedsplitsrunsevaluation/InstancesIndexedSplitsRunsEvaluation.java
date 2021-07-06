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
 * InstancesIndexedSplitsRunsEvaluation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsevaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.source.WekaClassifierSetup;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates the specified classifier on the indexed splits runs applied to the incoming data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstancesIndexedSplitsRunsEvaluation
  extends AbstractIndexedSplitsRunsEvaluation<Instances, WekaEvaluationContainer[]>{

  private static final long serialVersionUID = 2181874663254037648L;

  /** the split to use for training. */
  protected String m_TrainSplitName;

  /** the split to use for testing. */
  protected String m_TestSplitName;
  
  /** the name of the callable weka classifier. */
  protected CallableActorReference m_Classifier;

  /** whether to discard predictions. */
  protected boolean m_DiscardPredictions;

  /** a programmatically supplied classifier. */
  protected Classifier m_ManualClassifier;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates the specified classifier on the indexed splits runs applied to the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "train-split-name", "trainSplitName",
      "train");

    m_OptionManager.add(
      "test-split-name", "testSplitName",
      "test");

    m_OptionManager.add(
      "classifier", "classifier",
      new CallableActorReference(WekaClassifierSetup.class.getSimpleName()));

    m_OptionManager.add(
      "no-predictions", "discardPredictions",
      false);
  }

  /**
   * Sets the name of the split to use for training.
   *
   * @param value	the name
   */
  public void setTrainSplitName(String value) {
    m_TrainSplitName = value;
    reset();
  }

  /**
   * Returns the name of the split to use for training.
   *
   * @return		the name
   */
  public String getTrainSplitName() {
    return m_TrainSplitName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trainSplitNameTipText() {
    return "The name of the split to use for training.";
  }

  /**
   * Sets the name of the split to use for testing.
   *
   * @param value	the name
   */
  public void setTestSplitName(String value) {
    m_TestSplitName = value;
    reset();
  }

  /**
   * Returns the name of the split to use for testing.
   *
   * @return		the name
   */
  public String getTestSplitName() {
    return m_TestSplitName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testSplitNameTipText() {
    return "The name of the split to use for testing.";
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
  public String classifierTipText() {
    return "The classifier to evaluate on the splits.";
  }

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
   * Sets the manual classifier to use instead of obtaining it from the flow.
   *
   * @param value	the classifier
   */
  public void setManualClassifier(Classifier value) {
    m_ManualClassifier = value;
  }

  /**
   * Returns the manual to use instead of obtaining it from the flow.
   *
   * @return		the classifier
   */
  public Classifier getManualClassifier() {
    return m_ManualClassifier;
  }

  /**
   * The accepted classes.
   *
   * @return the array of accepted types
   */
  @Override
  public Class accepts() {
    return Instances.class;
  }

  /**
   * The generated classes.
   *
   * @return the array of generated types
   */
  @Override
  public Class generates() {
    return WekaEvaluationContainer[].class;
  }

  /**
   * Returns whether flow context is actually required.
   *
   * @return true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return (m_ManualClassifier == null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "trainSplitName", m_TrainSplitName, "train: ");
    result += QuickInfoHelper.toString(this, "testSplitName", m_TestSplitName, ", test: ");
    result += QuickInfoHelper.toString(this, "classifier", m_Classifier, ", classifier: ");
    result += QuickInfoHelper.toString(this, "discardPredictions", m_DiscardPredictions, "discarding predictions", ",");

    return result;
  }

  /**
   * Returns an instance of the callable classifier.
   *
   * @param errors 	for collecting errors
   * @return		the classifier
   */
  protected Classifier getClassifierInstance(MessageCollection errors) {
    Classifier			result;

    if (m_ManualClassifier != null)
      return m_ManualClassifier;

    result = (weka.classifiers.Classifier) CallableActorHelper.getSetup(weka.classifiers.Classifier.class, m_Classifier, m_FlowContext, errors);
    if (result == null) {
      if (!errors.isEmpty())
	getLogger().severe(errors.toString());
    }

    return result;
  }

  /**
   * Applies the splits defined in the indexed split and returns the generated subsets.
   *
   * @param indexedSplit		the run to apply
   * @param data	the data to obtain the subsets from
   * @return		the generated splits
   */
  protected Map<String,Instances> applyIndexedSplit(IndexedSplit indexedSplit, Instances data) {
    Map<String,Instances>	result;
    SplitIndices 		splitIndices;
    int[]			indices;
    Instances 			split;
    Instance 			inst;
    int				i;

    result = new HashMap<>();

    for (String key: indexedSplit.getIndices().keySet()) {
      splitIndices = indexedSplit.getIndices().get(key);
      indices      = splitIndices.getIndices();
      split        = new Instances(data, indices.length);
      for (i = 0; i < indices.length; i++) {
        inst = (Instance) data.instance(indices[i]).copy();
        split.add(inst);
      }
      result.put(key, split);
    }

    return result;
  }

  /**
   * Performs an evaluation by applying the indexed splits runs to the data.
   *
   * @param data   the data to use for evaluation
   * @param runs   the indexed splits to use
   * @param errors for collecting errors
   * @return the generated evaluations, null in case of error
   */
  @Override
  protected WekaEvaluationContainer[] doEvaluate(Instances data, IndexedSplitsRuns runs, MessageCollection errors) {
    List<WekaEvaluationContainer> 	result;
    WekaEvaluationContainer		cont;
    Evaluation				evaluation;
    Classifier				template;
    Classifier				classifier;
    int					run;
    IndexedSplitsRun 			indexedSplitsRun;
    IndexedSplits			indexedSplits;
    int					split;
    Map<String,Instances>		namedSplits;
    Instances				train;
    Instances				test;
    Instances				testFull;

    result   = new ArrayList<>();
    template = getClassifierInstance(errors);
    if (template == null)
      return null;

    try {
      for (run = 0; run < runs.size(); run++) {
        if (m_Stopped)
          return null;

	indexedSplitsRun = runs.get(run);
	indexedSplits    = indexedSplitsRun.getSplits();
	evaluation       = new Evaluation(data);
	testFull         = new Instances(data, 0);
	for (split = 0; split < indexedSplits.size(); split++) {
	  if (m_Stopped)
	    return null;

	  // TODO parallelize?
	  namedSplits = applyIndexedSplit(indexedSplits.get(split), data);
	  if (!namedSplits.containsKey(m_TrainSplitName))
	    throw new IllegalArgumentException(
	      "Failed to locate train split '" + m_TrainSplitName + "' (run=" + run + ", split=" + split + "), "
                + "available: " + Utils.flatten(namedSplits.keySet().toArray(), ","));
	  if (!namedSplits.containsKey(m_TestSplitName))
	    throw new IllegalArgumentException(
	      "Failed to locate test split '" + m_TestSplitName + "' (run=" + run + ", split=" + split + "), "
                + "available: " + Utils.flatten(namedSplits.keySet().toArray(), ","));
	  train      = namedSplits.get(m_TrainSplitName);
	  test       = namedSplits.get(m_TestSplitName);
	  classifier = ObjectCopyHelper.copyObject(template);
	  classifier.buildClassifier(train);
	  evaluation.evaluateModel(classifier, test);
	  testFull.addAll(test);
	}
	cont = new WekaEvaluationContainer(evaluation);
	cont.setValue(WekaEvaluationContainer.VALUE_TESTDATA, testFull);
	result.add(cont);
      }
    }
    catch (Exception e) {
      errors.add("Failed to evaluate!", e);
    }

    if (errors.isEmpty())
      return result.toArray(new WekaEvaluationContainer[0]);
    else
      return null;
  }
}
