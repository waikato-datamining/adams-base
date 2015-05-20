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
 * WekaCostCurve.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.util.logging.Level;

import javax.swing.JComponent;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;
import weka.gui.visualize.VisualizePanel;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a cost curve.
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
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model
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
 * &nbsp;&nbsp;&nbsp;default: WekaCostCurve
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 480
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
 * <pre>-index &lt;adams.core.Range&gt; (property: classLabelRange)
 * &nbsp;&nbsp;&nbsp;The indices of the class labels to use for the plot.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCostCurve
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 3247255046513744115L;

  /** the panel. */
  protected ThresholdVisualizePanel m_VisualizePanel;

  /** the class label range. */
  protected Range m_ClassLabelRange;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying a cost curve.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "classLabelRange",
	    new Range(Range.FIRST));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ClassLabelRange = new Range("first");
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 640;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 480;
  }

  /**
   * Sets the class label indices.
   *
   * @param value 	the range
   */
  public void setClassLabelRange(Range value) {
    m_ClassLabelRange = value;
    reset();
  }

  /**
   * Returns the class label indices.
   *
   * @return 		the range
   */
  public Range getClassLabelRange() {
    return m_ClassLabelRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classLabelRangeTipText() {
    return "The indices of the class labels to use for the plot.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "classLabelRange", m_ClassLabelRange, ", class label: ");
    
    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_VisualizePanel != null)
      m_VisualizePanel.removeAllPlots();
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
    m_VisualizePanel = new ThresholdVisualizePanel();
    result.add(m_VisualizePanel, BorderLayout.CENTER);

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
    weka.classifiers.evaluation.CostCurve 	curve;
    Evaluation					eval;
    PlotData2D					plot;
    boolean[] 					connectPoints;
    int						cp;
    Instances 					data;
    int[]					indices;

    try {
      if (token.getPayload() instanceof WekaEvaluationContainer)
	eval = (Evaluation) ((WekaEvaluationContainer) token.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
      else
	eval = (Evaluation) token.getPayload();
      if (eval.predictions() == null) {
	getLogger().severe("No predictions available from Evaluation object!");
	return;
      }
      m_ClassLabelRange.setMax(eval.getHeader().classAttribute().numValues());
      indices = m_ClassLabelRange.getIntIndices();
      for (int index: indices) {
	curve = new weka.classifiers.evaluation.CostCurve();
	data = curve.getCurve(eval.predictions(), index);
	plot = new PlotData2D(data);
	plot.setPlotName(eval.getHeader().classAttribute().value(index));
	plot.m_displayAllPoints = true;
	connectPoints = new boolean [data.numInstances()];
	for (cp = 1; cp < connectPoints.length; cp++)
	  connectPoints[cp] = true;
	plot.setConnectPoints(connectPoints);
	m_VisualizePanel.addPlot(plot);
      }
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

    if (m_VisualizePanel != null) {
      m_VisualizePanel.removeAllPlots();
      m_VisualizePanel = null;
    }
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
      protected VisualizePanel m_VisualizePanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_VisualizePanel = new VisualizePanel();
	add(m_VisualizePanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	try {
	  Evaluation eval = getEvaluation(token);
	  m_ClassLabelRange.setMax(eval.getHeader().classAttribute().numValues());
	  int[] indices = m_ClassLabelRange.getIntIndices();
	  for (int index: indices) {
	    weka.classifiers.evaluation.CostCurve curve = new weka.classifiers.evaluation.CostCurve();
	    Instances data = curve.getCurve(eval.predictions(), index);
	    PlotData2D plot = new PlotData2D(data);
	    plot.setPlotName(eval.getHeader().classAttribute().value(index));
	    plot.m_displayAllPoints = true;
	    boolean[] connectPoints = new boolean [data.numInstances()];
	    for (int cp = 1; cp < connectPoints.length; cp++)
	      connectPoints[cp] = true;
	    plot.setConnectPoints(connectPoints);
	    m_VisualizePanel.addPlot(plot);
	  }
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
	m_VisualizePanel.removeAllPlots();
      }
      public void cleanUp() {
	m_VisualizePanel.removeAllPlots();
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
