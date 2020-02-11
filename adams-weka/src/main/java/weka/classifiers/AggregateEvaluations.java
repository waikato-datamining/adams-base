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
 * AggregateEvaluations.java
 * Copyright (C) 2018-2020 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.core.DefaultCompare;
import adams.core.ErrorProvider;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.env.Environment;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Allows the aggregation of {@link Evaluation} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AggregateEvaluations
  extends LoggingObject
  implements ErrorProvider {

  private static final long serialVersionUID = 7021888369517311031L;

  /** dataset name. */
  protected String m_RelationName;

  /** the collected predictions. */
  protected List<Prediction> m_Predictions;

  /** the aggregated evaluation. */
  protected transient Evaluation m_Aggregated;

  /** the optional class labels. */
  protected List<String> m_ClassLabels;

  /** whether to sort the labels. */
  protected boolean m_SortLabels;

  /** the comparator to use. */
  protected Comparator m_Comparator;

  /** whether to reverse the sorting. */
  protected boolean m_Reverse;

  /** the last error. */
  protected String m_LastError;

  /**
   * Initializes the object.
   */
  public AggregateEvaluations() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_RelationName = null;
    m_Predictions  = new ArrayList<>();
    m_Aggregated   = null;
    m_ClassLabels  = null;
    m_SortLabels   = false;
    m_Comparator   = new DefaultCompare();
    m_Reverse      = false;
  }

  /**
   * Returns all the currently stored predictions.
   *
   * @return		the predictions
   */
  public List<Prediction> getPredictions() {
    return m_Predictions;
  }

  /**
   * Sets the class labels to use.
   *
   * @param value	the labels
   */
  public void setClassLabels(List<String> value) {
    m_ClassLabels = new ArrayList<>(value);
    if (m_SortLabels) {
      m_ClassLabels.sort(m_Comparator);
      if (m_Reverse)
        Collections.reverse(m_ClassLabels);
    }
  }

  /**
   * Returns the currently set class labels, if any.
   *
   * @return		the labels, null if none set
   */
  public List<String> getClassLabels() {
    return m_ClassLabels;
  }

  /**
   * Sets whether to sort the labels with the specified comparator.
   *
   * @param value	true if to sort
   */
  public void setSortLabels(boolean value) {
    m_SortLabels = value;
  }

  /**
   * Returns whether to store the labels with the specified comparator.
   *
   * @return		true if to sort
   */
  public boolean getSortLabels() {
    return m_SortLabels;
  }

  /**
   * Sets the comparator to use.
   *
   * @param value	the comparator
   */
  public void setComparator(Comparator value) {
    m_Comparator = value;
  }

  /**
   * Returns the comparator to use.
   *
   * @return		the comparator
   */
  public Comparator getComparator() {
    return m_Comparator;
  }

  /**
   * Sets whether to reverse the sorting.
   *
   * @param value	true if to reverse
   */
  public void setReverse(boolean value) {
    m_Reverse = value;
  }

  /**
   * Returns whether to reverse the sorting.
   *
   * @return		true if to reverse
   */
  public boolean getReverse() {
    return m_Reverse;
  }

  /**
   * Adds the prediction.
   *
   * @param pred	the prediction to add
   * @return		null if successfully added, otherwise error message
   */
  public String add(Prediction pred) {
    if (pred == null)
      return "Cannot add null object!";

    if (m_Predictions.size() > 0) {
      if (m_Predictions.get(0).getClass() != pred.getClass())
	return "Prediction classes differ: "
	  + Utils.classToString(m_Predictions.get(0))
	  + " != "
	  + Utils.classToString(pred);
    }

    m_Predictions.add(pred);
    m_Aggregated = null;
    return null;
  }

  /**
   * Adds the predictions of the given {@link Evaluation} object.
   *
   * @param eval	the evaluation to add
   * @return		null if successfully added, otherwise error message
   */
  public String add(Evaluation eval) {
    int		i;

    if (eval.predictions() == null)
      return "No predictions stored in Evaluation object!";
    if (eval.predictions().size() == 0)
      return null;

    // same type of predictions?
    if (m_Predictions.size() > 0) {
      if (m_Predictions.get(0).getClass() != eval.predictions().get(0).getClass())
	return "Prediction classes differ: "
	  + Utils.classToString(m_Predictions.get(0))
	  + " != "
	  + Utils.classToString(eval.predictions().get(0));
    }
    else {
      m_RelationName = eval.getHeader().relationName();
      if (eval.getHeader().classAttribute().isNominal()) {
	m_ClassLabels = new ArrayList<>();
	for (i = 0; i < eval.getHeader().classAttribute().numValues(); i++)
	  m_ClassLabels.add(eval.getHeader().classAttribute().value(i));
      }
    }

    m_Predictions.addAll(eval.predictions());
    m_Aggregated = null;
    return null;
  }

  /**
   * Adds the data from the spreadsheet as predictions.
   *
   * @param sheet	the spreadsheet with the data
   * @param colAct	the "actual" column index
   * @param colPred	the "predicted" column index
   * @param colWeight	the column with the weight, -1 if not present
   * @param colsDist	the columns with the class distribution, null if not present
   * @param useColNamesAsLabels 	whether to use the column names as class labels, requires colsDist
   * @return		null if successfully added, otherwise error message
   */
  public String add(SpreadSheet sheet, int colAct, int colPred, int colWeight, int[] colsDist, boolean useColNamesAsLabels) {
    String[]			actualLabels;
    double[]			actual;
    String[]			predictedLabels;
    double[]			predicted;
    double[][]			dist;
    double[]			weight;
    int				i;
    int				n;
    List<String>		labels;
    String			name;
    double[]			clsDist;
    boolean			classification;

    labels = null;
    classification = false;

    dist = null;
    if ((colsDist != null) && (colsDist.length == 0))
      colsDist = null;
    if (colsDist != null) {
      dist = new double[colsDist.length][];
      for (i = 0; i < colsDist.length; i++) {
	dist[i] = SpreadSheetUtils.getNumericColumn(sheet, colsDist[i]);
      }
    }

    // class labels
    if ((colsDist != null)) {
      classification = true;
      labels = new ArrayList<>();
      for (i = 0; i < dist.length; i++) {
	if (useColNamesAsLabels) {
	  name = sheet.getColumnName(colsDist[i]);
	  if (name.startsWith("Distribution (") && name.endsWith(")"))
	    name = name.substring("Distribution (".length(), name.length() - 1);
	  labels.add(name);
	}
	else {
	  labels.add("" + (i + 1));
	}
      }
      setClassLabels(labels);
    }

    if (!sheet.isNumeric(colAct)) {
      // do we need to determine class labels ourselves?
      if (labels == null) {
	classification = true;
	labels = new ArrayList<>();
	actualLabels = SpreadSheetUtils.getColumn(sheet, colAct, true, true, "?");
	for (String label: actualLabels) {
	  if (label.equals("?"))
	    continue;
	  labels.add(label);
	}
	setClassLabels(labels);
      }

      actualLabels = SpreadSheetUtils.getColumn(sheet, colAct, false, false, "?");
      actual = new double[actualLabels.length];
      for (i = 0; i < actualLabels.length; i++) {
        if (actualLabels[i].equals("?"))
          actual[i] = weka.core.Utils.missingValue();
        else
          actual[i] = labels.indexOf(actualLabels[i]);
      }
      predictedLabels = SpreadSheetUtils.getColumn(sheet, colPred, false, false, "?");
      predicted = new double[predictedLabels.length];
      for (i = 0; i < predictedLabels.length; i++) {
        if (predictedLabels[i].equals("?"))
          predicted[i] = weka.core.Utils.missingValue();
        else
          predicted[i] = labels.indexOf(predictedLabels[i]);
      }
    }
    else {
      actual = SpreadSheetUtils.getNumericColumn(sheet, colAct);
      predicted = SpreadSheetUtils.getNumericColumn(sheet, colPred);
    }
    if (actual.length != predicted.length)
      return "Number of actual and predicted values differ: " + actual.length + " != " + predicted.length;

    if (colsDist != null) {
      for (i = 0; i < colsDist.length; i++) {
	if (actual.length != dist[i].length)
	  return "Number of actual and class distribution (col #" + (colsDist[i] + 1) + ") values differ: " + actual.length + " != " + dist[i].length;
      }
    }

    weight = null;
    if (colWeight > -1) {
      weight = SpreadSheetUtils.getNumericColumn(sheet, colWeight);
      if (actual.length != weight.length)
	return "Number of actual and weight values differ: " + actual.length + " != " + weight.length;
    }

    // add predictions
    for (i = 0; i < actual.length; i++) {
      if (classification) {
	if (dist != null) {
	  clsDist = new double[dist.length];
	  for (n = 0; n < clsDist.length; n++)
	    clsDist[n] = dist[n][i];
	  if (weight != null)
	    add(new NominalPrediction(actual[i], clsDist, weight[i]));
	  else
	    add(new NominalPrediction(actual[i], clsDist));
	}
	else {
	  clsDist = new double[labels.size()];
	  clsDist[(int) predicted[i]] = 1.0;
	  if (weight != null)
	    add(new NominalPrediction(actual[i], clsDist, weight[i]));
	  else
	    add(new NominalPrediction(actual[i], clsDist));
	}
      }
      else {
        if (weight != null)
	  add(new NumericPrediction(actual[i], predicted[i], weight[i]));
        else
	  add(new NumericPrediction(actual[i], predicted[i]));
      }
    }

    return null;
  }

  /**
   * Performs the aggregation.
   *
   * @return		the aggregated evaluation
   */
  protected Evaluation doAggregate() {
    Instances			data;
    ArrayList<Attribute>	atts;
    List<String> 		labels;
    int				i;
    Instance			inst;
    int				distLen;
    Evaluation 			result;

    // length of class distribution
    distLen = 0;
    if ((m_Predictions.size() > 0) && (m_Predictions.get(0) instanceof NominalPrediction))
      distLen = ((NominalPrediction) m_Predictions.get(0)).distribution().length;

    // create fake dataset
    atts = new ArrayList<>();
    if (distLen == 0) {
      atts.add(new Attribute("Actual"));
    }
    else {
      labels = new ArrayList<>();
      if (m_ClassLabels.size() == distLen) {
	labels = new ArrayList<>(m_ClassLabels);
      }
      else {
	for (i = 0; i < distLen; i++) {
	  if (m_ClassLabels != null)
	    labels.add("" + (i + 1));
	}
      }
      atts.add(new Attribute("Actual", labels));
    }
    data = new Instances(
      (m_RelationName == null ? Environment.getInstance().getProject() : m_RelationName),
      atts, m_Predictions.size());
    data.setClassIndex(0);
    for (i = 0; i < m_Predictions.size(); i++) {
      inst = new DenseInstance(m_Predictions.get(i).weight(), new double[]{m_Predictions.get(i).actual()});
      data.add(inst);
    }

    // perform "fake" evaluation
    try {
      result = new Evaluation(data);
      for (i = 0; i < m_Predictions.size(); i++) {
	if (distLen > 0) {
	  result.evaluateModelOnceAndRecordPrediction(
	    ((NominalPrediction) m_Predictions.get(i)).distribution(), data.instance(i));
	}
	else {
	  result.evaluateModelOnceAndRecordPrediction(
	    new double[]{m_Predictions.get(i).predicted()}, data.instance(i));
	}
      }
    }
    catch (Exception e) {
      result      = null;
      m_LastError = LoggingHelper.handleException(this, "Failed to create 'fake' Evaluation object!", e);
    }

    return result;
  }

  /**
   * Returns the aggregated evaluation.
   *
   * @return		the aggregated evaluation
   */
  public Evaluation aggregated() {
    if (m_Aggregated == null)
      m_Aggregated = doAggregate();
    return m_Aggregated;
  }

  /**
   * Returns whether an error was encountered during the last operation.
   *
   * @return		true if an error occurred
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns the error that occurred during the last operation.
   *
   * @return		the error string, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Returns a short description of current state.
   *
   * @return		short description
   */
  public String toString() {
    return
      "# predictions: " + getPredictions().size() + "\n"
      + "class labels: " + (getClassLabels() != null ? getClassLabels() : "-none-") + "\n"
      + "last error: " + (hasLastError() ? getLastError() : "-none-");
  }
}
