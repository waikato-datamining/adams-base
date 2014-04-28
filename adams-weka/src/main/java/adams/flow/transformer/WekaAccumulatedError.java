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
 * WekaAccumulatedError.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import adams.core.QuickInfoHelper;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates plot containers from an evaluation object's predictions. The predictions are first sorted according to their error, smallest to largest, and then plot containers are created with the RMSE being accumulated.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: AccumulatedError
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-plot-name &lt;java.lang.String&gt; (property: plotName)
 * &nbsp;&nbsp;&nbsp;The name for the plot.
 * &nbsp;&nbsp;&nbsp;default: Plot
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAccumulatedError
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 43672155926689520L;

  /**
   * Container for a classifier prediction, used for sorting.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SortablePrediction
    implements Comparable {

    /** the wrapped prediction. */
    protected Prediction m_Prediction;

    /**
     * Initializes the container.
     *
     * @param pred 	the prediction to wrap
     */
    public SortablePrediction(Prediction pred) {
      super();

      m_Prediction = pred;
    }

    /**
     * Returns the stored prediction.
     *
     * @return		the prediction
     */
    public Prediction getPrediction() {
      return m_Prediction;
    }

    /**
     * Returns the absolute difference between actual and predicted value.
     * Returns Double.MAX_VALUE if at least one of them is NaN.
     *
     * @return		the absolute difference between actual and predicted
     * 			or NaN
     */
    public double getAbsoluteDifference() {
      if (Double.isNaN(m_Prediction.actual()) || Double.isNaN(m_Prediction.predicted()))
	return Double.NaN;
      else
	return Math.abs(m_Prediction.actual() - m_Prediction.predicted()) * m_Prediction.weight();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p/>
     * Uses the absolute difference between actual and predicted values.
     *
     * @param o 	the object to be compared.
     * @return  	a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException 	if the specified object's type prevents it
     *         				from being compared to this object.
     */
    public int compareTo(Object o) {
      SortablePrediction	other;

      if (o == null)
        return 1;

      if (!(o instanceof SortablePrediction))
	return -1;

      other = (SortablePrediction) o;

      if (Double.isNaN(getAbsoluteDifference()) && Double.isNaN(other.getAbsoluteDifference()))
	return 0;
      else if (Double.isNaN(getAbsoluteDifference()))
	return +1;
      else if (Double.isNaN(other.getAbsoluteDifference()))
	return -1;
      else
	return Double.compare(getAbsoluteDifference(), other.getAbsoluteDifference());
    }

    /**
     * Returns a string representation of the wrapped prediction.
     *
     * @return		the string representation
     */
    @Override
    public String toString() {
      return   "actual=" + m_Prediction.actual()
             + ", predicted=" + m_Prediction.predicted()
             + ", weight=" + m_Prediction.weight();
    }
  }

  /** the key for storing the current predictions in the backup. */
  public final static String BACKUP_PREDICTIONS = "predictions";

  /** the key for storing the current number of predictions in the backup. */
  public final static String BACKUP_NUMPREDICTIONS = "num predictions";

  /** the key for storing the current accumulated error in the backup. */
  public final static String BACKUP_ACCUMULATEDERROR = "accumulated error";

  /** the name of the plot. */
  protected String m_PlotName;

  /** the sorted predictions. */
  protected List<SortablePrediction> m_Predictions;

  /** the number of overall predictions. */
  protected int m_NumPredictions;

  /** the accumulated error so far. */
  protected double m_AccumulatdError;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates plot containers from an evaluation object's predictions. "
      + "The predictions are first sorted according to their error, smallest "
      + "to largest, and then plot containers are created with the RMSE being "
      + "accumulated.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "plot-name", "plotName",
	    "Plot");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "plotName", m_PlotName);
  }

  /**
   * Sets the plot name.
   *
   * @param value	the name
   */
  public void setPlotName(String value) {
    m_PlotName = value;
    reset();
  }

  /**
   * Returns the current plot name.
   *
   * @return		the name
   */
  public String getPlotName() {
    return m_PlotName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotNameTipText() {
    return "The name for the plot.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Evaluation.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_PREDICTIONS);
    pruneBackup(BACKUP_NUMPREDICTIONS);
    pruneBackup(BACKUP_ACCUMULATEDERROR);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_PREDICTIONS, m_Predictions);
    result.put(BACKUP_NUMPREDICTIONS, m_NumPredictions);
    result.put(BACKUP_ACCUMULATEDERROR, m_AccumulatdError);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_PREDICTIONS)) {
      m_Predictions = (List<SortablePrediction>) state.get(BACKUP_PREDICTIONS);
      state.remove(BACKUP_PREDICTIONS);
    }
    if (state.containsKey(BACKUP_NUMPREDICTIONS)) {
      m_NumPredictions = (Integer) state.get(BACKUP_NUMPREDICTIONS);
      state.remove(BACKUP_NUMPREDICTIONS);
    }
    if (state.containsKey(BACKUP_ACCUMULATEDERROR)) {
      m_AccumulatdError = (Double) state.get(BACKUP_ACCUMULATEDERROR);
      state.remove(BACKUP_ACCUMULATEDERROR);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Predictions     = new ArrayList<SortablePrediction>();
    m_NumPredictions  = 0;
    m_AccumulatdError = 0;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    ArrayList	predictions;
    int		i;
    Evaluation	eval;

    result = null;

    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer)
      eval = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      eval = (Evaluation) m_InputToken.getPayload();
    predictions = eval.predictions();
    if (predictions != null) {
      m_NumPredictions = predictions.size();
      for (i = 0; i < predictions.size(); i++)
	m_Predictions.add(new SortablePrediction((Prediction) predictions.get(i)));
      Collections.sort(m_Predictions);
    }
    else {
      getLogger().severe("No predictions available from Evaluation object!");
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SequencePlotterContainer.class};
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Predictions.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    Prediction	pred;
    double	error;

    pred               = m_Predictions.get(0).getPrediction();
    error              = Math.sqrt(Math.pow((pred.actual() - pred.predicted()), 2)) / Math.sqrt(m_NumPredictions);
    m_AccumulatdError += error;

    m_Predictions.remove(0);
    m_InputToken  = null;
    result        = new Token(
	new SequencePlotterContainer(
	    m_PlotName,
	    (double) (m_NumPredictions - m_Predictions.size()),
	    m_AccumulatdError));

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Predictions     = null;
    m_AccumulatdError = 0;
    m_NumPredictions  = 0;

    super.wrapUp();
  }
}
