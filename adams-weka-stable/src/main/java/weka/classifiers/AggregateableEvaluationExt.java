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
 * AggregateableEvaluationExt.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import weka.classifiers.evaluation.AbstractEvaluationMetric;
import weka.classifiers.evaluation.InformationRetrievalEvaluationMetric;
import weka.classifiers.evaluation.InformationTheoreticEvaluationMetric;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.StandardEvaluationMetric;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixes aggregation of pluggable metrics.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AggregateableEvaluationExt
  extends AggregateableEvaluation {

  private static final long serialVersionUID = -6834419290844510454L;

  /**
   * Constructs a new AggregateableEvaluationExt object
   *
   * @param data the Instances to use
   * @throws Exception if a problem occurs
   */
  public AggregateableEvaluationExt(Instances data) throws Exception {
    super(data);
  }

  /**
   * Constructs a new AggregateableEvaluationExt object
   *
   * @param data the Instances to use
   * @param costMatrix the cost matrix to use
   * @throws Exception if a problem occurs
   */
  public AggregateableEvaluationExt(Instances data, CostMatrix costMatrix) throws Exception {
    super(data, costMatrix);
  }

  /**
   * Constructs a new AggregateableEvaluationExt object based on an Evaluation
   * object
   *
   * @param eval the Evaluation object to use
   */
  public AggregateableEvaluationExt(Evaluation eval) throws Exception {
    super(eval);
    addPredictions(eval);
  }

  /**
   * Adds the statistics encapsulated in the supplied Evaluation object into
   * this one. Does not perform any checks for compatibility between the
   * supplied Evaluation object and this one.
   *
   * @param evaluation the evaluation object to aggregate
   */
  @Override
  public void aggregate(Evaluation evaluation) {
    super.aggregate(evaluation);
    addPredictions(evaluation);
  }

  /**
   * Adds the predictions to the metrics.
   *
   * @param eval	the evaluation to get the predictions from
   */
  protected void addPredictions(Evaluation eval) {
    NominalPrediction			nominal;
    NumericPrediction			numeric;
    ArrayList<Attribute> 		atts;
    Instances				data;
    Instance 				instance;
    List<AbstractEvaluationMetric> 	metrics;

    if (eval.predictions() == null)
      return;
    metrics = getPluginMetrics();
    if (metrics == null)
      return;

    // fake header
    atts = new ArrayList<>();
    atts.add((Attribute) eval.getHeader().classAttribute().copy());
    data = new Instances(eval.getHeader().relationName(), atts, 0);
    data.setClassIndex(data.numAttributes() - 1);

    for (Prediction pred: eval.predictions()) {
      nominal = null;
      numeric = null;
      if (pred instanceof NominalPrediction)
	nominal = (NominalPrediction) pred;
      else if (pred instanceof NumericPrediction)
	numeric = (NumericPrediction) pred;
      else
        continue;

      // fake instance
      instance = new DenseInstance(pred.weight(), new double[]{pred.actual()});
      instance.setDataset(data);

      for (AbstractEvaluationMetric m : metrics) {
	try {
	  // updateStatsForClassifier
	  if (nominal != null) {
	    if (m instanceof StandardEvaluationMetric)
	      ((StandardEvaluationMetric) m).updateStatsForClassifier(nominal.distribution(), instance);
	    else if (m instanceof InformationRetrievalEvaluationMetric)
	      ((InformationRetrievalEvaluationMetric) m).updateStatsForClassifier(nominal.distribution(), instance);
	    else if (m instanceof InformationTheoreticEvaluationMetric)
	      ((InformationTheoreticEvaluationMetric) m).updateStatsForClassifier(nominal.distribution(), instance);
	  }
	  // updateStatsForPredictor
	  if (numeric != null) {
	    if (m instanceof StandardEvaluationMetric)
	      ((StandardEvaluationMetric) m).updateStatsForPredictor(numeric.predicted(), instance);
	    else if (m instanceof InformationTheoreticEvaluationMetric)
	      ((InformationTheoreticEvaluationMetric) m).updateStatsForPredictor(numeric.predicted(), instance);
	  }
	  // updateStatsForIntervalEstimator
	  // not possible, since no model available
	}
	catch (Exception e) {
	  System.err.println("Failed to aggregate statistic for: " + m);
	  e.printStackTrace();
	}
      }
    }
  }
}
