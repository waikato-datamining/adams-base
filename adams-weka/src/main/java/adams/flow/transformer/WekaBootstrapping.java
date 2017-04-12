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
 * WekaBootstrapping.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.base.BaseDouble;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.Percentile;
import adams.data.weka.WekaLabelIndex;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import adams.flow.core.Token;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Performs bootstrapping on the incoming evaluation and outputs a spreadsheet where each row represents the results from bootstrapping sub-sample.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output, Original indices
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
 * &nbsp;&nbsp;&nbsp;default: WekaBootstrapping
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
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed for generating the random sub-samples.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-num-subsamples &lt;int&gt; (property: numSubSamples)
 * &nbsp;&nbsp;&nbsp;The number of random sub-samples to generate.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-percentage &lt;double&gt; (property: percentage)
 * &nbsp;&nbsp;&nbsp;The percentage of the sub-sample size (between 0 and 1).
 * &nbsp;&nbsp;&nbsp;default: 0.66
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-4
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 * <pre>-statistic &lt;Number correct|Number incorrect|Number unclassified|Percent correct|Percent incorrect|Percent unclassified|Kappa statistic|Mean absolute error|Root mean squared error|Relative absolute error|Root relative squared error|Correlation coefficient|SF prior entropy|SF scheme entropy|SF entropy gain|SF mean prior entropy|SF mean scheme entropy|SF mean entropy gain|KB information|KB mean information|KB relative information|True positive rate|Num true positives|False positive rate|Num false positives|True negative rate|Num true negatives|False negative rate|Num false negatives|IR precision|IR recall|F measure|Matthews correlation coefficient|Area under ROC|Area under PRC|Weighted true positive rate|Weighted false positive rate|Weighted true negative rate|Weighted false negative rate|Weighted IR precision|Weighted IR recall|Weighted F measure|Weighted Matthews correlation coefficient|Weighted area under ROC|Weighted area under PRC|Unweighted Macro F measure|Unweighted Micro F measure|Bias|R^2&gt; [-statistic ...] (property: statisticValues)
 * &nbsp;&nbsp;&nbsp;The evaluation values to extract and turn into a spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-class-index &lt;adams.data.weka.WekaLabelIndex&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The index of class label (eg used for AUC).
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from label names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); label names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-percentile &lt;adams.core.base.BaseDouble&gt; [-percentile ...] (property: percentiles)
 * &nbsp;&nbsp;&nbsp;The percentiles to calculate for the errors (0-1; 0.95 is 95th percentile
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-error-calculation &lt;ACTUAL_MINUS_PREDICTED|PREDICTED_MINUS_ACTUAL|BOTH|ABSOLUTE&gt; (property: errorCalculation)
 * &nbsp;&nbsp;&nbsp;Determines how to calculate the error.
 * &nbsp;&nbsp;&nbsp;default: ACTUAL_MINUS_PREDICTED
 * </pre>
 * 
 * <pre>-with-replacement &lt;boolean&gt; (property: withReplacement)
 * &nbsp;&nbsp;&nbsp;If enabled, predictions are drawn using with replacement (i.e., duplicates 
 * &nbsp;&nbsp;&nbsp;are possible).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaBootstrapping
  extends AbstractTransformer
  implements Randomizable {

  private static final long serialVersionUID = 2599800854948082354L;

  /** how to calculate the error. */
  public enum ErrorCalculation {
    ACTUAL_MINUS_PREDICTED,
    PREDICTED_MINUS_ACTUAL,
    BOTH,
    ABSOLUTE,
  }

  /** the random number seed. */
  protected long m_Seed;

  /** the number of random sub-samples to generate. */
  protected int m_NumSubSamples;

  /** the size for the sub-samples (0-1). */
  protected double m_Percentage;

  /** the comparison fields. */
  protected EvaluationStatistic[] m_StatisticValues;

  /** the index of the class label. */
  protected WekaLabelIndex m_ClassIndex;

  /** the percentiles to output (0-1). */
  protected BaseDouble[] m_Percentiles;

  /** the error calculation. */
  protected ErrorCalculation m_ErrorCalculation;

  /** whether to use with replacement or not. */
  protected boolean m_WithReplacement;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs bootstrapping on the incoming evaluation and outputs a "
	+ "spreadsheet where each row represents the results from "
	+ "bootstrapping sub-sample.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "num-subsamples", "numSubSamples",
      10, 1, null);

    m_OptionManager.add(
      "percentage", "percentage",
      0.66, 0.0001, 1.0);

    m_OptionManager.add(
      "statistic", "statisticValues",
      new EvaluationStatistic[0]);

    m_OptionManager.add(
      "class-index", "classIndex",
      new WekaLabelIndex(WekaLabelIndex.FIRST));

    m_OptionManager.add(
      "percentile", "percentiles",
      new BaseDouble[0]);

    m_OptionManager.add(
      "error-calculation", "errorCalculation",
      ErrorCalculation.ACTUAL_MINUS_PREDICTED);

    m_OptionManager.add(
      "with-replacement", "withReplacement",
      true);
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  @Override
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String seedTipText() {
    return "The seed for generating the random sub-samples.";
  }

  /**
   * Sets the number sub-samples to generate.
   *
   * @param value	the number of sub-samples
   */
  public void setNumSubSamples(int value) {
    if (getOptionManager().isValid("numSubSamples", value)) {
      m_NumSubSamples = value;
      reset();
    }
  }

  /**
   * Returns the number of sub-samples to generate.
   *
   * @return  		the number of sub-samples
   */
  public int getNumSubSamples() {
    return m_NumSubSamples;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSubSamplesTipText() {
    return "The number of random sub-samples to generate.";
  }

  /**
   * Sets the percentage (0-1).
   *
   * @param value	the percentage
   */
  public void setPercentage(double value) {
    if (getOptionManager().isValid("percentage", value)) {
      m_Percentage = value;
      reset();
    }
  }

  /**
   * Returns the percentage (0-1).
   *
   * @return  		the percentage
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return "The percentage of the sub-sample size (between 0 and 1).";
  }

  /**
   * Sets the values to extract.
   *
   * @param value	the value
   */
  public void setStatisticValues(EvaluationStatistic[] value) {
    m_StatisticValues = value;
    reset();
  }

  /**
   * Returns the values to extract.
   *
   * @return		the value
   */
  public EvaluationStatistic[] getStatisticValues() {
    return m_StatisticValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticValuesTipText() {
    return "The evaluation values to extract and turn into a spreadsheet.";
  }

  /**
   * Sets the index of class label index (1-based).
   *
   * @param value	the label index
   */
  public void setClassIndex(WekaLabelIndex value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current index of class label (1-based).
   *
   * @return		the label index
   */
  public WekaLabelIndex getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The index of class label (eg used for AUC).";
  }

  /**
   * Sets the percentiles to calculate for the errors.
   *
   * @param value	the percentiles (0-1; 0.95 is 95th percentile)
   */
  public void setPercentiles(BaseDouble[] value) {
    m_Percentiles = value;
    reset();
  }

  /**
   * Returns the percentiles to calculate for the errors.
   *
   * @return  		the percentiles (0-1; 0.95 is 95th percentile)
   */
  public BaseDouble[] getPercentiles() {
    return m_Percentiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentilesTipText() {
    return "The percentiles to calculate for the errors (0-1; 0.95 is 95th percentile).";
  }

  /**
   * Sets how to calculate the errors for the percentiles.
   *
   * @param value	the type
   */
  public void setErrorCalculation(ErrorCalculation value) {
    m_ErrorCalculation = value;
    reset();
  }

  /**
   * Returns how to calculate the errors for the percentiles.
   *
   * @return  		the type
   */
  public ErrorCalculation getErrorCalculation() {
    return m_ErrorCalculation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String errorCalculationTipText() {
    return "Determines how to calculate the error.";
  }

  /**
   * Sets whether to draw predictions using replacement.
   *
   * @param value	true if with replacement
   */
  public void setWithReplacement(boolean value) {
    m_WithReplacement = value;
    reset();
  }

  /**
   * Returns whether to draw predictions using replacement.
   *
   * @return  		true if with replacement
   */
  public boolean getWithReplacement() {
    return m_WithReplacement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String withReplacementTipText() {
    return "If enabled, predictions are drawn using with replacement (i.e., duplicates are possible).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "seed", m_Seed, "seed: ");
    result += QuickInfoHelper.toString(this, "numSubSamples", m_NumSubSamples, ", # sub: ");
    result += QuickInfoHelper.toString(this, "percentage", m_Percentage, ", percentage: ");
    result += QuickInfoHelper.toString(this, "statisticValues", m_StatisticValues.length + " statistic" + (m_StatisticValues.length != 1 ? "s" : ""), ", ");
    result += QuickInfoHelper.toString(this, "classIndex", m_ClassIndex, ", class label: ");
    result += QuickInfoHelper.toString(this, "percentiles", m_Percentiles.length + " percentile" + (m_Percentiles.length != 1 ? "s" : ""), ", ");
    result += QuickInfoHelper.toString(this, "errorCalculation", m_ErrorCalculation, ", errors: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
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
    Row				row;
    Evaluation			evalAll;
    Evaluation			eval;
    WekaEvaluationContainer	cont;
    TIntList			indices;
    Random			random;
    int				i;
    int				iteration;
    int				size;
    List<Prediction> 		preds;
    Instances			header;
    Instances			data;
    ArrayList<Attribute>	atts;
    Instance 			inst;
    boolean			numeric;
    int				classIndex;
    Double[]  			errors;
    Double[]  			errorsRev;
    Percentile<Double>		perc;
    Percentile<Double>		percRev;
    TIntList			subset;

    result = null;

    if (m_InputToken.getPayload() instanceof Evaluation) {
      evalAll = (Evaluation) m_InputToken.getPayload();
    }
    else {
      cont    = (WekaEvaluationContainer) m_InputToken.getPayload();
      evalAll = (Evaluation) cont.getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    }

    if ((evalAll.predictions() == null) || (evalAll.predictions().size() == 0))
      result = "No predictions available!";

    if (result == null) {
      // init spreadsheet
      sheet = new DefaultSpreadSheet();
      row   = sheet.getHeaderRow();
      row.addCell("S").setContentAsString("Subsample");
      for (EvaluationStatistic s: m_StatisticValues)
	row.addCell(s.toString()).setContentAsString(s.toString());
      for (i = 0; i < m_Percentiles.length; i++) {
	switch (m_ErrorCalculation) {
	  case ACTUAL_MINUS_PREDICTED:
	    row.addCell("perc-AmP-" + i).setContentAsString("Percentile-AmP-" + m_Percentiles[i]);
	    break;
	  case PREDICTED_MINUS_ACTUAL:
	    row.addCell("perc-PmA-" + i).setContentAsString("Percentile-PmA-" + m_Percentiles[i]);
	    break;
	  case ABSOLUTE:
	    row.addCell("perc-Abs-" + i).setContentAsString("Percentile-Abs-" + m_Percentiles[i]);
	    break;
	  case BOTH:
	    row.addCell("perc-AmP-" + i).setContentAsString("Percentile-AmP-" + m_Percentiles[i]);
	    row.addCell("perc-PmA-" + i).setContentAsString("Percentile-PmA-" + m_Percentiles[i]);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled error calculation: " + m_ErrorCalculation);
	}
      }

      // set up bootstrapping
      preds   = evalAll.predictions();
      random  = new Random(m_Seed);
      indices = new TIntArrayList();
      size    = (int) Math.round(preds.size() * m_Percentage);
      header  = evalAll.getHeader();
      numeric = header.classAttribute().isNumeric();
      m_ClassIndex.setData(header.classAttribute());
      if (numeric)
	classIndex = -1;
      else
        classIndex = m_ClassIndex.getIntIndex();
      for (i = 0; i < preds.size(); i++)
        indices.add(i);

      // create fake evalutions
      subset = new TIntArrayList();
      for (iteration = 0; iteration < m_NumSubSamples; iteration++) {
	if (m_Stopped) {
	  sheet = null;
	  break;
	}

	// determine
	subset.clear();
	if (m_WithReplacement) {
	  for (i = 0; i < size; i++)
	    subset.add(indices.get(random.nextInt(size)));
	}
	else {
	  indices.shuffle(random);
	  for (i = 0; i < size; i++)
	    subset.add(indices.get(i));
	}

	// create dataset from predictions
	errors    = new Double[size];
	errorsRev = new Double[size];
	atts      = new ArrayList<>();
	atts.add(header.classAttribute().copy("Actual"));
	data = new Instances(header.relationName() + "-" + (iteration+1), atts, size);
	data.setClassIndex(0);
	for (i = 0; i < subset.size(); i++) {
	  inst = new DenseInstance(preds.get(subset.get(i)).weight(), new double[]{preds.get(subset.get(i)).actual()});
	  data.add(inst);
	  switch (m_ErrorCalculation) {
	    case ACTUAL_MINUS_PREDICTED:
	      errors[i] = preds.get(subset.get(i)).actual() - preds.get(subset.get(i)).predicted();
	      break;
	    case PREDICTED_MINUS_ACTUAL:
	      errorsRev[i] = preds.get(subset.get(i)).predicted() - preds.get(subset.get(i)).actual();
	      break;
	    case ABSOLUTE:
	      errors[i] = Math.abs(preds.get(subset.get(i)).actual() - preds.get(subset.get(i)).predicted());
	      break;
	    case BOTH:
	      errors[i] = preds.get(subset.get(i)).actual() - preds.get(subset.get(i)).predicted();
	      errorsRev[i] = preds.get(subset.get(i)).predicted() - preds.get(subset.get(i)).actual();
	      break;
	    default:
	      throw new IllegalStateException("Unhandled error calculation: " + m_ErrorCalculation);
	  }
	}

	// perform "fake" evaluation
	try {
	  eval = new Evaluation(data);
	  for (i = 0; i < subset.size(); i++) {
	    if (numeric)
	      eval.evaluateModelOnceAndRecordPrediction(new double[]{preds.get(subset.get(i)).predicted()}, data.instance(i));
	    else
	      eval.evaluateModelOnceAndRecordPrediction(((NominalPrediction) preds.get(subset.get(i))).distribution().clone(), data.instance(i));
	  }
	}
	catch (Exception e) {
	  result = handleException("Failed to create 'fake' Evaluation object (iteration: " + (iteration+1) + ")!", e);
	  break;
	}

	// add row
	row = sheet.addRow();
	row.addCell("S").setContent(iteration+1);
	for (EvaluationStatistic s: m_StatisticValues) {
	  try {
	    row.addCell(s.toString()).setContent(EvaluationHelper.getValue(eval, s, classIndex));
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to calculate statistic in iteration #" + (iteration+1) + ": " + s, e);
	    row.addCell(s.toString()).setMissing();
	  }
	}
	for (i = 0; i < m_Percentiles.length; i++) {
	  perc = new Percentile<>();
	  perc.addAll(errors);
	  percRev = new Percentile<>();
	  percRev.addAll(errorsRev);
	  switch (m_ErrorCalculation) {
	    case ACTUAL_MINUS_PREDICTED:
	      row.addCell("perc-AmP-" + i).setContent(perc.getPercentile(m_Percentiles[i].doubleValue()));
	      break;
	    case PREDICTED_MINUS_ACTUAL:
	      row.addCell("perc-PmA-" + i).setContent(percRev.getPercentile(m_Percentiles[i].doubleValue()));
	      break;
	    case ABSOLUTE:
	      row.addCell("perc-Abs-" + i).setContent(perc.getPercentile(m_Percentiles[i].doubleValue()));
	      break;
	    case BOTH:
	      row.addCell("perc-AmP-" + i).setContent(perc.getPercentile(m_Percentiles[i].doubleValue()));
	      row.addCell("perc-PmA-" + i).setContent(percRev.getPercentile(m_Percentiles[i].doubleValue()));
	      break;
	    default:
	      throw new IllegalStateException("Unhandled error calculation: " + m_ErrorCalculation);
	  }
	}
      }

      if ((result == null) && (sheet != null))
	m_OutputToken = new Token(sheet);
    }

    return result;
  }
}
