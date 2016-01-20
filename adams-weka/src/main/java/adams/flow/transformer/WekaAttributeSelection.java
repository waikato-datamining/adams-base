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
 * WekaAttributeSelection.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.WekaAttributeSelectionContainer;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.core.Token;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.AttributeTransformer;
import weka.attributeSelection.RankedOutputSearch;
import weka.core.Instances;

import java.util.Random;


/**
 <!-- globalinfo-start -->
 * Performs attribute selection on the incoming data.<br>
 * In case of input in form of a class adams.flow.container.WekaTrainTestSetContainer object, the training set stored in the container is being used.<br>
 * NB: In case of cross-validation no reduced or transformed data can get generated!
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaTrainTestSetContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaAttributeSelectionContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaTrainTestSetContainer: Train, Test, Seed, FoldNumber, FoldCount<br>
 * - adams.flow.container.WekaAttributeSelectionContainer: Train, Reduced, Transformed, Evaluation, Statistics, Selected attributes, Seed, FoldCount
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
 * &nbsp;&nbsp;&nbsp;default: WekaAttributeSelection
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-evaluator &lt;weka.attributeSelection.ASEvaluation&gt; (property: evaluator)
 * &nbsp;&nbsp;&nbsp;The evaluation method to use.
 * &nbsp;&nbsp;&nbsp;default: weka.attributeSelection.CfsSubsetEval -P 1 -E 1
 * </pre>
 * 
 * <pre>-search &lt;weka.attributeSelection.ASSearch&gt; (property: search)
 * &nbsp;&nbsp;&nbsp;The search method to use.
 * &nbsp;&nbsp;&nbsp;default: weka.attributeSelection.BestFirst -D 1 -N 5
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for cross-validation (used for randomization).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in the cross-validation; no cross-validation 
 * &nbsp;&nbsp;&nbsp;is performed if folds &lt; 2.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAttributeSelection
  extends AbstractTransformer 
  implements Randomizable {

  /** for serialization. */
  private static final long serialVersionUID = 4145361817914402084L;

  /** the evaluation. */
  protected weka.attributeSelection.ASEvaluation m_Evaluator;

  /** the search method. */
  protected weka.attributeSelection.ASSearch m_Search;
  
  /** the number of folds. */
  protected int m_Folds;

  /** the seed value. */
  protected long m_Seed;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs attribute selection on the incoming data.\n"
        + "In case of input in form of a " + WekaTrainTestSetContainer.class + " object, "
        + "the training set stored in the container is being used.\n"
        + "NB: In case of cross-validation no reduced or transformed data can get generated!";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "evaluator", "evaluator",
	    new weka.attributeSelection.CfsSubsetEval());

    m_OptionManager.add(
	    "search", "search",
	    new weka.attributeSelection.BestFirst());

    m_OptionManager.add(
	    "seed", "seed",
	    1L);

    m_OptionManager.add(
	    "folds", "folds",
	    10, -1, null);
  }

  /**
   * Sets the evaluation method to use.
   *
   * @param value	the evaluation method
   */
  public void setEvaluator(weka.attributeSelection.ASEvaluation value) {
    m_Evaluator = value;
    reset();
  }

  /**
   * Returns the evaluation method in use.
   *
   * @return		the evaluation method
   */
  public weka.attributeSelection.ASEvaluation getEvaluator() {
    return m_Evaluator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String evaluatorTipText() {
    return "The evaluation method to use.";
  }

  /**
   * Sets the evaluation method to use.
   *
   * @param value	the evaluation method
   */
  public void setSearch(weka.attributeSelection.ASSearch value) {
    m_Search = value;
    reset();
  }

  /**
   * Returns the evaluation method in use.
   *
   * @return		the evaluation method
   */
  public weka.attributeSelection.ASSearch getSearch() {
    return m_Search;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String searchTipText() {
    return "The search method to use.";
  }

  /**
   * Sets the number of folds.
   *
   * @param value	the folds
   */
  public void setFolds(int value) {
    if (value >= -1) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Number of folds must be >= -1, provided: " + value);
    }
  }

  /**
   * Returns the number of folds.
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in the cross-validation; no cross-validation is performed if folds < 2.";
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
   * @return		the seed
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
    return "The seed value for cross-validation (used for randomization).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;

    result  = QuickInfoHelper.toString(this, "evaluator", m_Evaluator.getClass(), "eval: ");
    result += QuickInfoHelper.toString(this, "search", m_Search.getClass(), ", search: ");

    variable = QuickInfoHelper.getVariable(this, "folds");
    if ((variable != null) || (m_Folds >= 2)) {
      result += ", folds: " + (variable == null ? m_Folds : variable);
      result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    }
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, adams.flow.container.WekaTrainTestSetContainer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class, WekaTrainTestSetContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaAttributeSelectionContainer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{WekaAttributeSelectionContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    Instances				data;
    Instances				reduced;
    Instances				transformed;
    AttributeSelection			eval;
    boolean 				crossValidate;
    int					fold;
    Instances 				train;
    WekaAttributeSelectionContainer	cont;
    SpreadSheet				stats;
    int					i;
    Row					row;
    int[]				selected;
    double[][]				ranked;
    Range 				range;
    String				rangeStr;
    boolean				useReduced;

    result = null;

    try {
      if (m_InputToken.getPayload() instanceof Instances)
	data = (Instances) m_InputToken.getPayload();
      else
	data = (Instances) ((WekaTrainTestSetContainer) m_InputToken.getPayload()).getValue(WekaTrainTestSetContainer.VALUE_TRAIN);

      if (result == null) {
	crossValidate = (m_Folds >= 2);

	// setup evaluation
	eval = new AttributeSelection();
	eval.setEvaluator(m_Evaluator);
	eval.setSearch(m_Search);
	eval.setFolds(m_Folds);
	eval.setSeed((int) m_Seed);
	eval.setXval(crossValidate);
	
	// select attributes
	if (crossValidate) {
	  Random random = new Random(m_Seed);
	  data = new Instances(data);
	  data.randomize(random);
	  if ((data.classIndex() > -1) && data.classAttribute().isNominal()) {
	    if (isLoggingEnabled())
	      getLogger().info("Stratifying instances...");
	    data.stratify(m_Folds);
	  }
	  for (fold = 0; fold < m_Folds; fold++) {
	    if (isLoggingEnabled())
	      getLogger().info("Creating splits for fold " + (fold + 1)
		+ "...");
	    train = data.trainCV(m_Folds, fold, random);
	    if (isLoggingEnabled())
	      getLogger().info("Selecting attributes using all but fold " + (fold + 1) + "...");
	    eval.selectAttributesCVSplit(train);
	  }
	}
	else {
          eval.SelectAttributes(data);
	}
	
	// generate reduced/transformed dataset
	reduced     = null;
	transformed = null;
	if (!crossValidate) {
	  reduced = eval.reduceDimensionality(data);
	  if (m_Evaluator instanceof AttributeTransformer)
	    transformed = ((AttributeTransformer) m_Evaluator).transformedData(data);
	}
	
	// generated stats
	stats = null;
	if (!crossValidate) {
	  stats = new SpreadSheet();
	  row   = stats.getHeaderRow();
	  
	  useReduced = false;
	  if (m_Search instanceof RankedOutputSearch) {
	    i = reduced.numAttributes();
	    if (reduced.classIndex() > -1)
	      i--;
	    ranked = eval.rankedAttributes();
	    useReduced = (ranked.length == i);
	  }
	  
	  if (useReduced) {
	    for (i = 0; i < reduced.numAttributes(); i++)
	      row.addCell("" + i).setContent(reduced.attribute(i).name());
	    row = stats.addRow();
	    for (i = 0; i < reduced.numAttributes(); i++)
	      row.addCell(i).setContent(0.0);
	  }
	  else {
	    for (i = 0; i < data.numAttributes(); i++)
	      row.addCell("" + i).setContent(data.attribute(i).name());
	    row = stats.addRow();
	    for (i = 0; i < data.numAttributes(); i++)
	      row.addCell(i).setContent(0.0);
	  }
	  
	  if (m_Search instanceof RankedOutputSearch) {
	    ranked = eval.rankedAttributes();
	    for (i = 0; i < ranked.length; i++)
	      row.getCell((int) ranked[i][0]).setContent(ranked[i][0]);
	  }
	  else {
	    selected = eval.selectedAttributes();
	    for (i = 0; i < selected.length; i++)
	      row.getCell(selected[i]).setContent(1.0);
	  }
	}
	
	// selected attributes
	rangeStr = null;
	if (!crossValidate) {
	  range = new Range();
	  range.setIndices(eval.selectedAttributes());
	  rangeStr = range.getRange();
	}

	// setup container
	if (crossValidate)
	  cont = new WekaAttributeSelectionContainer(data, reduced, transformed, eval, m_Seed, m_Folds);
	else
	  cont = new WekaAttributeSelectionContainer(data, reduced, transformed, eval, stats, rangeStr);
	m_OutputToken = new Token(cont);
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data:", e);
    }

    return result;
  }
}
