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
 * WekaSpreadSheetToPredictions.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.env.Environment;
import adams.flow.core.Token;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns the predictions stored in the incoming spreadsheet (actual and predicted) into a Weka weka.classifiers.Evaluation object.<br>
 * For recreating the predictions of a nominal class, the class distributions must be present in the spreadsheet as well.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.WekaPredictionsToSpreadSheet
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaSpreadSheetToPredictions
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actual &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: actual)
 * &nbsp;&nbsp;&nbsp;The column with the actual values.
 * &nbsp;&nbsp;&nbsp;default: Actual
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-predicted &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: predicted)
 * &nbsp;&nbsp;&nbsp;The column with the predicted values.
 * &nbsp;&nbsp;&nbsp;default: Predicted
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-class-distribution &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: classDistribution)
 * &nbsp;&nbsp;&nbsp;The columns containing the class distribution (nominal class).
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-column-names-as-class-labels &lt;boolean&gt; (property: useColumnNamesAsClassLabels)
 * &nbsp;&nbsp;&nbsp;If enabled, the names of the class distribution columns are used as labels 
 * &nbsp;&nbsp;&nbsp;in the fake evaluation; automatically removes the surrounding 'Distribution 
 * &nbsp;&nbsp;&nbsp;(...)'.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-weight &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: weight)
 * &nbsp;&nbsp;&nbsp;The (optional) column with the weights of the instances; 1.0 is assumed 
 * &nbsp;&nbsp;&nbsp;by default.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaSpreadSheetToPredictions
  extends AbstractTransformer
  implements ClassCrossReference {

  private static final long serialVersionUID = -2097531874480331676L;

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** the columns with the class distributions. */
  protected SpreadSheetColumnRange m_ClassDistribution;

  /** whether to use the column name as class labels. */
  protected boolean m_UseColumnNamesAsClassLabels;

  /** the (optional) column with the instance weights. */
  protected SpreadSheetColumnIndex m_Weight;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns the predictions stored in the incoming spreadsheet (actual and "
	+ "predicted) into a Weka " + Evaluation.class.getName() + " object.\n"
	+ "For recreating the predictions of a nominal class, the class distributions "
	+ "must be present in the spreadsheet as well.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{WekaPredictionsToSpreadSheet.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actual", "actual",
      new SpreadSheetColumnIndex("Actual"));

    m_OptionManager.add(
      "predicted", "predicted",
      new SpreadSheetColumnIndex("Predicted"));

    m_OptionManager.add(
      "class-distribution", "classDistribution",
      new SpreadSheetColumnRange(""));

    m_OptionManager.add(
      "column-names-as-class-labels", "useColumnNamesAsClassLabels",
      false);

    m_OptionManager.add(
      "weight", "weight",
      new SpreadSheetColumnIndex(""));
  }

  /**
   * Sets the column with the actual values.
   *
   * @param value	the column
   */
  public void setActual(SpreadSheetColumnIndex value) {
    m_Actual = value;
    reset();
  }

  /**
   * Returns the column with the actual values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getActual() {
    return m_Actual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualTipText() {
    return "The column with the actual values.";
  }

  /**
   * Sets the column with the predicted values.
   *
   * @param value	the column
   */
  public void setPredicted(SpreadSheetColumnIndex value) {
    m_Predicted = value;
    reset();
  }

  /**
   * Returns the column with the predicted values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getPredicted() {
    return m_Predicted;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedTipText() {
    return "The column with the predicted values.";
  }

  /**
   * Sets the columns with the class distribution (nominal class).
   *
   * @param value	the range
   */
  public void setClassDistribution(SpreadSheetColumnRange value) {
    m_ClassDistribution = value;
    reset();
  }

  /**
   * Returns the columns with the class distribution (nominal class).
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getClassDistribution() {
    return m_ClassDistribution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classDistributionTipText() {
    return "The columns containing the class distribution (nominal class).";
  }

  /**
   * Sets whether to use the names of the class distribution columns as
   * labels in the fake evaluation.
   *
   * @param value	true if to use column names
   */
  public void setUseColumnNamesAsClassLabels(boolean value) {
    m_UseColumnNamesAsClassLabels = value;
    reset();
  }

  /**
   * Returns whether to use the names of the class distribution columns as
   * labels in the fake evaluation.
   *
   * @return		true if to use column names
   */
  public boolean getUseColumnNamesAsClassLabels() {
    return m_UseColumnNamesAsClassLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColumnNamesAsClassLabelsTipText() {
    return
      "If enabled, the names of the class distribution columns are used as "
	+ "labels in the fake evaluation; automatically removes the "
	+ "surrounding 'Distribution (...)'.";
  }

  /**
   * Sets the (optional) column with the instance weight values.
   *
   * @param value	the column
   */
  public void setWeight(SpreadSheetColumnIndex value) {
    m_Weight = value;
    reset();
  }

  /**
   * Returns the (optional) column with the instance weight values.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getWeight() {
    return m_Weight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String weightTipText() {
    return "The (optional) column with the weights of the instances; 1.0 is assumed by default.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "actual", m_Actual, "actual: ");
    result += QuickInfoHelper.toString(this, "predicted", m_Predicted, ", predicted: ");
    result += QuickInfoHelper.toString(this, "classDistribution", (m_ClassDistribution.isEmpty() ? "-none-" : m_ClassDistribution.getRange()), ", class: ");
    result += QuickInfoHelper.toString(this, "weight", (m_Weight.isEmpty() ? "-none-" : m_Weight.getIndex()), ", weight: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Evaluation.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;
    double[]			actual;
    double[]			predicted;
    double[][]			dist;
    double[]			weight;
    Instances			data;
    ArrayList<Attribute>	atts;
    List<String> 		labels;
    int				i;
    int				n;
    Instance			inst;
    Evaluation 			eval;
    int[]			cols;
    double[]			clsDist;
    String			name;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    m_Actual.setData(sheet);
    m_Predicted.setData(sheet);
    m_ClassDistribution.setData(sheet);
    m_Weight.setData(sheet);
    if (m_Actual.getIntIndex() == -1)
      result = "'Actual' column not found: " + m_Actual;
    else if (m_Predicted.getIntIndex() == -1)
      result = "'Predicted' column not found: " + m_Predicted;

    actual    = null;
    predicted = null;
    dist      = null;
    weight    = null;
    if (result == null) {
      actual = SpreadSheetUtils.getNumericColumn(sheet, m_Actual.getIntIndex());
      predicted = SpreadSheetUtils.getNumericColumn(sheet, m_Predicted.getIntIndex());
      if (actual.length != predicted.length)
	result = "Number of actual and predicted values differ: " + actual.length + " != " + predicted.length;
      else if (actual.length == 0)
	result = "No numeric values?";
      // weights?
      if (m_Weight.getIntIndex() != -1) {
	weight = SpreadSheetUtils.getNumericColumn(sheet, m_Weight.getIntIndex());
	if (actual.length != weight.length)
	  result = "Number of actual and weight values differ: " + actual.length + " != " + weight.length;
      }
      // class distribution?
      cols = m_ClassDistribution.getIntIndices();
      if (cols.length > 0) {
	dist = new double[cols.length][];
	for (i = 0; i < cols.length; i++) {
	  dist[i] = SpreadSheetUtils.getNumericColumn(sheet, cols[i]);
	  if (actual.length != dist[i].length) {
	    result = "Number of actual and class distribution (col #" + (cols[i] + 1) + ") values differ: " + actual.length + " != " + dist[i].length;
	    break;
	  }
	}
      }
    }

    if (result == null) {
      // create dataset from predictions
      atts = new ArrayList<>();
      if (dist == null) {
	atts.add(new Attribute("Prediction"));
      }
      else {
	labels = new ArrayList<>();
	cols   = m_ClassDistribution.getIntIndices();
	for (i = 0; i < dist.length; i++) {
	  if (m_UseColumnNamesAsClassLabels) {
	    name = sheet.getColumnName(cols[i]);
	    if (name.startsWith("Distribution (") && name.endsWith(")"))
	      name = name.substring("Distribution (".length(), name.length() - 1);
	    labels.add(name);
	  }
	  else {
	    labels.add("" + (i + 1));
	  }
	}
	atts.add(new Attribute("Prediction", labels));
      }
      data = new Instances((sheet.hasName() ? sheet.getName() : Environment.getInstance().getProject()), atts, actual.length);
      data.setClassIndex(0);
      for (i = 0; i < actual.length; i++) {
	inst = new DenseInstance((weight == null) ? 1.0 : weight[i], new double[]{actual[i]});
	data.add(inst);
      }

      // perform "fake" evaluation
      try {
	eval = new Evaluation(data);
	for (i = 0; i < actual.length; i++) {
	  if (dist != null) {
	    clsDist = new double[dist.length];
	    for (n = 0; n < clsDist.length; n++)
	      clsDist[n] = dist[n][i];
	    eval.evaluateModelOnceAndRecordPrediction(clsDist, data.instance(i));
	  }
	  else {
	    eval.evaluateModelOnceAndRecordPrediction(new double[]{predicted[i]}, data.instance(i));
	  }
	}
	m_OutputToken = new Token(eval);
      }
      catch (Exception e) {
	result = handleException("Failed to create 'fake' Evaluation object!", e);
      }
    }

    return result;
  }
}
