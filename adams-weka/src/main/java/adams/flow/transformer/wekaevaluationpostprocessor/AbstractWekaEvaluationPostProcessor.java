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
 * AbstractWekaEvaluationPostProcessor.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import adams.core.option.AbstractOptionHandler;
import adams.flow.container.WekaEvaluationContainer;
import gnu.trove.list.TIntList;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for classes that post-process Evaluation objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWekaEvaluationPostProcessor
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -1975307519142567955L;

  /**
   * Checks the container whether it can be processed.
   * <br>
   * Default implementation only ensures that it is not null and predictions
   * are present.
   *
   * @param cont	the container to check
   * @return		null if successful, otherwise error message
   */
  protected String check(WekaEvaluationContainer cont) {
    Evaluation	eval;

    if (cont == null)
      return "No evaluation container provided!";
    if (!cont.hasValue(WekaEvaluationContainer.VALUE_EVALUATION))
      return "No Evaluation object in container present!";
    return check((Evaluation) cont.getValue(WekaEvaluationContainer.VALUE_EVALUATION));
  }

  /**
   * Creates a new evaluation container from the specified subset of predictions.
   *
   * @param suffix	the suffix for the relation name
   * @param cont	the container to use as basis
   * @param indices	the indices of the predictions to include in the container
   * @return		the new container
   */
  protected WekaEvaluationContainer newContainer(String suffix, WekaEvaluationContainer cont, TIntList indices) {
    WekaEvaluationContainer	result;
    Evaluation			eval;
    Evaluation			evalNew;

    eval    = (Evaluation) cont.getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    evalNew = newEvaluation(suffix, eval, indices);

    // assemble new container
    result = new WekaEvaluationContainer(evalNew);

    return result;
  }

  /**
   * Post-processes the evaluation container.
   *
   * @param cont	the container to post-process
   * @return		the generated evaluation containers
   */
  protected List<WekaEvaluationContainer> doPostProcess(WekaEvaluationContainer cont) {
    List<WekaEvaluationContainer>	result;
    List<Evaluation>			evals;

    result = new ArrayList<>();
    evals  = postProcess(cont.getValue(WekaEvaluationContainer.VALUE_EVALUATION, Evaluation.class));
    for (Evaluation eval: evals)
      result.add(new WekaEvaluationContainer(eval));

    return result;
  }

  /**
   * Post-processes the evaluation container.
   *
   * @param cont	the container to post-process
   * @return		the generated evaluation containers
   */
  public List<WekaEvaluationContainer> postProcess(WekaEvaluationContainer cont) {
    String	msg;

    msg = check(cont);
    if (msg != null)
      throw new IllegalArgumentException(msg);
    else
      return doPostProcess(cont);
  }

  /**
   * Checks the evaluation whether it can be processed.
   * <br>
   * Default implementation only ensures that it is not null and predictions
   * are present.
   *
   * @param eval	the evaluation to check
   * @return		null if successful, otherwise error message
   */
  protected String check(Evaluation eval) {
    if (eval == null)
      return "No evaluation provided!";
    if (eval.predictions() == null)
      return "No predictions recorded?";
    return null;
  }

  /**
   * Creates a new evaluation from the specified subset of predictions.
   *
   * @param suffix	the suffix for the relation name
   * @param eval	the Evaluation to use as basis
   * @param indices	the indices of the predictions to include in the container
   * @return		the new container
   */
  protected Evaluation newEvaluation(String suffix, Evaluation eval, TIntList indices) {
    Evaluation 			result;
    Instances			data;
    Instance			inst;
    ArrayList<Attribute> 	atts;
    int				i;
    boolean			numeric;
    ArrayList<Prediction>	preds;
    Prediction			pred;

    preds = eval.predictions();

    // create fake data
    atts = new ArrayList<>();
    atts.add(eval.getHeader().classAttribute().copy("Actual"));
    data = new Instances(eval.getHeader().relationName() + suffix, atts, indices.size());
    data.setClassIndex(data.numAttributes() - 1);
    numeric = eval.getHeader().classAttribute().isNumeric();
    for (i = 0; i < indices.size(); i++) {
      // create instance
      pred = preds.get(indices.get(i));
      if (numeric)
	inst = new DenseInstance(pred.weight(), new double[]{pred.actual()});
      else
	inst = new DenseInstance(pred.weight(), ((NominalPrediction) pred).distribution().clone());
      data.add(inst);
    }

    // make fake predictions
    try {
      result = new Evaluation(data);
      for (i = 0; i < indices.size(); i++) {
	inst = data.instance(i);
	pred = preds.get(indices.get(i));
	if (numeric)
	  result.evaluateModelOnceAndRecordPrediction(new double[]{pred.predicted()}, inst);
	else
	  result.evaluateModelOnceAndRecordPrediction(((NominalPrediction) pred).distribution().clone(), inst);
      }
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to make fake predictions!", e);
    }

    return result;
  }

  /**
   * Post-processes the evaluation.
   *
   * @param eval	the evaluation to post-process
   * @return		the generated evaluations
   */
  protected abstract List<Evaluation> doPostProcess(Evaluation eval);

  /**
   * Post-processes the evaluation.
   *
   * @param eval	the evaluation to post-process
   * @return		the generated evaluations
   */
  public List<Evaluation> postProcess(Evaluation eval) {
    String	msg;

    msg = check(eval);
    if (msg != null)
      throw new IllegalArgumentException(msg);
    else
      return doPostProcess(eval);
  }
}
