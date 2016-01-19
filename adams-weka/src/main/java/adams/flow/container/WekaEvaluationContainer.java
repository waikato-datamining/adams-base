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
 * WekaEvaluationContainer.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import weka.classifiers.Evaluation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for {@link Evaluation} objects, with optional trained model.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaEvaluationContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = -4976094375833503035L;

  /** the identifier for the Evaluation. */
  public final static String VALUE_EVALUATION = "Evaluation";

  /** the identifier for the Model. */
  public final static String VALUE_MODEL = "Model";

  /** the identifier for the prediction output. */
  public final static String VALUE_PREDICTIONOUTPUT = "Prediction output";

  /** the identifier for the original indices. */
  public final static String VALUE_ORIGINALINDICES = "Original indices";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaEvaluationContainer() {
    this(null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param eval	the evaluation to use
   */
  public WekaEvaluationContainer(Evaluation eval) {
    this(eval, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param eval	the evaluation to use
   * @param model	the model to use
   */
  public WekaEvaluationContainer(Evaluation eval, Object model) {
    this(eval, model, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param eval	the evaluation to use
   * @param model	the model to use, can be null
   * @param predOutput  the prediction output string, can be null
   */
  public WekaEvaluationContainer(Evaluation eval, Object model, String predOutput) {
    this(eval, model, predOutput, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param eval	the evaluation to use
   * @param model	the model to use, can be null
   * @param predOutput  the prediction output string, can be null
   * @param originalIndices	the original indices (for tracing the predictions), can be null
   */
  public WekaEvaluationContainer(Evaluation eval, Object model, String predOutput, int[] originalIndices) {
    super();

    store(VALUE_EVALUATION,       eval);
    store(VALUE_MODEL,            model);
    store(VALUE_PREDICTIONOUTPUT, predOutput);
    store(VALUE_ORIGINALINDICES,  originalIndices);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_EVALUATION, "evaluation object " + Evaluation.class.getName());
    addHelp(VALUE_MODEL, "model object; " + Object.class.getName());
    addHelp(VALUE_PREDICTIONOUTPUT, "prediction output text; " + String.class.getName());
    addHelp(VALUE_ORIGINALINDICES, "original indices (0-based); array of " + Integer.TYPE.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_EVALUATION);
    result.add(VALUE_MODEL);
    result.add(VALUE_PREDICTIONOUTPUT);
    result.add(VALUE_ORIGINALINDICES);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_EVALUATION);
  }
}
