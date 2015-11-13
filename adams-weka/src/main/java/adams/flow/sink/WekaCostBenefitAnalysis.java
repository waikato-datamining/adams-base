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
 * WekaCostBenefitAnalysis.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.data.weka.WekaLabelIndex;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Attribute;
import weka.core.Instances;
import weka.gui.beans.CostBenefitAnalysis;
import weka.gui.visualize.PlotData2D;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a cost benefit analysis dialog.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output
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
 * &nbsp;&nbsp;&nbsp;default: WekaCostBenefitAnalysis
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 1200
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 700
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-index &lt;adams.data.weka.WekaLabelIndex&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The range of class label indices (eg used for AUC).
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from label names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCostBenefitAnalysis
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 3247255046513744115L;

  /** the index of the class label. */
  protected WekaLabelIndex m_ClassIndex;

  /** the panel. */
  protected CostBenefitAnalysis m_CostBenefitPanel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying a cost benefit analysis dialog.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "classIndex",
	    new WekaLabelIndex(WekaLabelIndex.FIRST));
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 1200;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 700;
  }

  /**
   * Sets the index of class label (1-based).
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
    return "The range of class label indices (eg used for AUC).";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    // can't clear
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;

    result = new BasePanel(new BorderLayout());
    m_CostBenefitPanel = new CostBenefitAnalysis();
    result.add(m_CostBenefitPanel, BorderLayout.CENTER);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Evaluation.class, adams.flow.container.WekaEvaluationContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }

  /**
   * Plots the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    Evaluation		eval;
    Attribute 		classAtt;
    Attribute 		classAttToUse;
    int 		classValue;
    ThresholdCurve 	tc;
    Instances 		result;
    ArrayList<String> 	newNames;
    CostBenefitAnalysis	cbAnalysis;
    PlotData2D 		tempd;
    boolean[] 		cp;
    int 		n;

    try {
      if (token.getPayload() instanceof WekaEvaluationContainer)
	eval = (Evaluation) ((WekaEvaluationContainer) token.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
      else
	eval = (Evaluation) token.getPayload();
      if (eval.predictions() == null) {
	getLogger().severe("No predictions available from Evaluation object!");
	return;
      }
      classAtt   = eval.getHeader().classAttribute();
      m_ClassIndex.setData(classAtt);
      classValue = m_ClassIndex.getIntIndex();
      tc         = new ThresholdCurve();
      result     = tc.getCurve(eval.predictions(), classValue);

      // Create a dummy class attribute with the chosen
      // class value as index 0 (if necessary).
      classAttToUse = eval.getHeader().classAttribute();
      if (classValue != 0) {
	newNames = new ArrayList<>();
	newNames.add(classAtt.value(classValue));
	for (int k = 0; k < classAtt.numValues(); k++) {
	  if (k != classValue)
	    newNames.add(classAtt.value(k));
	}
	classAttToUse = new Attribute(classAtt.name(), newNames);
      }
      // assemble plot data
      tempd = new PlotData2D(result);
      tempd.setPlotName(result.relationName());
      tempd.m_alwaysDisplayPointsOfThisSize = 10;
      // specify which points are connected
      cp = new boolean[result.numInstances()];
      for (n = 1; n < cp.length; n++)
	cp[n] = true;
      tempd.setConnectPoints(cp);
      // add plot
      m_CostBenefitPanel.setCurveData(tempd, classAttToUse);
    }
    catch (Exception e) {
      handleException("Failed to display token: " + token, e);
    }
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    if (m_CostBenefitPanel != null)
      m_CostBenefitPanel = null;
  }

  /**
   * Returns the {@link Evaluation} object from the token.
   *
   * @param token	the token to extract the {@link Evaluation} object from
   * @return		the {@link Evaluation} object
   */
  protected Evaluation getEvaluation(Token token) {
    if (token.getPayload() instanceof WekaEvaluationContainer)
      return (Evaluation) ((WekaEvaluationContainer) token.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      return (Evaluation) token.getPayload();
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;
    String			name;

    if (token != null)
      name = "Cost curve (" + getEvaluation(token).getHeader().relationName() + ")";
    else
      name = "Cost curve";

    result = new AbstractComponentDisplayPanel(name) {
      private static final long serialVersionUID = -3513994354297811163L;
      protected CostBenefitAnalysis m_VisualizePanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_VisualizePanel = new CostBenefitAnalysis();
	add(m_VisualizePanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	try {
	  Evaluation eval = getEvaluation(token);
	  Attribute classAtt= eval.getHeader().classAttribute();
	  m_ClassIndex.setData(classAtt);
	  int classValue = m_ClassIndex.getIntIndex();
	  ThresholdCurve tc = new ThresholdCurve();
	  Instances result = tc.getCurve(eval.predictions(), classValue);

	  // Create a dummy class attribute with the chosen
	  // class value as index 0 (if necessary).
	  Attribute classAttToUse = eval.getHeader().classAttribute();
	  if (classValue != 0) {
	    ArrayList<String> newNames = new ArrayList<>();
	    newNames.add(classAtt.value(classValue));
	    for (int k = 0; k < classAtt.numValues(); k++) {
	      if (k != classValue)
		newNames.add(classAtt.value(k));
	    }
	    classAttToUse = new Attribute(classAtt.name(), newNames);
	  }
	  // assemble plot data
	  PlotData2D tempd = new PlotData2D(result);
	  tempd.setPlotName(result.relationName());
	  tempd.m_alwaysDisplayPointsOfThisSize = 10;
	  // specify which points are connected
	  boolean[] cp = new boolean[result.numInstances()];
	  for (int n = 1; n < cp.length; n++)
	    cp[n] = true;
	  tempd.setConnectPoints(cp);
	  // add plot
	  m_VisualizePanel.setCurveData(tempd, classAttToUse);
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to display token: " + token, e);
	}
      }
      @Override
      public JComponent supplyComponent() {
	return m_VisualizePanel;
      }
      @Override
      public void clearPanel() {
      }
      public void cleanUp() {
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return true;
  }
}
