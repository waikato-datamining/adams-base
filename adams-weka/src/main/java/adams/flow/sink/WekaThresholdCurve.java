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
 * WekaThresholdCurve.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.util.logging.Level;

import javax.swing.JComponent;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;
import weka.gui.visualize.VisualizePanel;
import adams.core.EnumWithCustomDisplay;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.option.AbstractOption;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;

/**
 <!-- globalinfo-start -->
 * Actor for displaying threshold curves, like ROC or precision&#47;recall.
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
 * &nbsp;&nbsp;&nbsp;default: WekaThresholdCurve
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
 * <pre>-attribute-x &lt;TRUE_POS|FALSE_NEG|FALSE_POS|TRUE_NEG|FP_RATE|TP_RATE|PRECISION|RECALL|FALLOUT|FMEASURE|SAMPLE_SIZE|LIFT|THRESHOLD&gt; (property: attributeX)
 * &nbsp;&nbsp;&nbsp;The attribute to show on the X axis.
 * &nbsp;&nbsp;&nbsp;default: FP_RATE
 * </pre>
 * 
 * <pre>-attribute-y &lt;TRUE_POS|FALSE_NEG|FALSE_POS|TRUE_NEG|FP_RATE|TP_RATE|PRECISION|RECALL|FALLOUT|FMEASURE|SAMPLE_SIZE|LIFT|THRESHOLD&gt; (property: attributeY)
 * &nbsp;&nbsp;&nbsp;The attribute to show on the Y axis.
 * &nbsp;&nbsp;&nbsp;default: TP_RATE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaThresholdCurve
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 3247255046513744115L;

  /**
   * The type of the fields.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @see ThresholdCurve
   */
  public enum AttributeName
    implements EnumWithCustomDisplay<AttributeName> {

    TRUE_POS(ThresholdCurve.TRUE_POS_NAME),
    FALSE_NEG(ThresholdCurve.FALSE_NEG_NAME),
    FALSE_POS(ThresholdCurve.FALSE_POS_NAME),
    TRUE_NEG(ThresholdCurve.TRUE_NEG_NAME),
    FP_RATE(ThresholdCurve.FP_RATE_NAME),
    TP_RATE(ThresholdCurve.TP_RATE_NAME),
    PRECISION(ThresholdCurve.PRECISION_NAME),
    RECALL(ThresholdCurve.RECALL_NAME),
    FALLOUT(ThresholdCurve.FALLOUT_NAME),
    FMEASURE(ThresholdCurve.FMEASURE_NAME),
    SAMPLE_SIZE(ThresholdCurve.SAMPLE_SIZE_NAME),
    LIFT(ThresholdCurve.LIFT_NAME),
    THRESHOLD(ThresholdCurve.THRESHOLD_NAME);

    /** the display string. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * The constructor.
     *
     * @param display	the string to use as display
     */
    private AttributeName(String display) {
      m_Display = display;
      m_Raw     = super.toString();
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Display;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return toDisplay();
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public AttributeName parse(String s) {
      return (AttributeName) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((AttributeName) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str	the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static AttributeName valueOf(AbstractOption option, String str) {
      AttributeName	result;

      result = null;

      // default parsing
      try {
	result = valueOf(str);
      }
      catch (Exception e) {
	// ignored
      }

      // try display
      if (result == null) {
	for (AttributeName dt: values()) {
	  if (dt.toDisplay().equals(str)) {
	    result = dt;
	    break;
	  }
	}
      }

      return result;
    }
  }
  
  /** the text area. */
  protected ThresholdVisualizePanel m_VisualizePanel;

  /** the class label indices. */
  protected Range m_ClassLabelRange;
  
  /** the attribute on the X axis. */
  protected AttributeName m_AttributeX;
  
  /** the attribute on the Y axis. */
  protected AttributeName m_AttributeY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying threshold curves, like ROC or precision/recall.";
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

    m_OptionManager.add(
	    "attribute-x", "attributeX",
	    AttributeName.FP_RATE);

    m_OptionManager.add(
	    "attribute-y", "attributeY",
	    AttributeName.TP_RATE);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ClassLabelRange = new Range(Range.FIRST);
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
   * Sets the attribute to show on the X axis.
   *
   * @param value 	the attribute
   */
  public void setAttributeX(AttributeName value) {
    m_AttributeX = value;
    reset();
  }

  /**
   * Returns the attribute to show on the X axis.
   *
   * @return 		the attribute
   */
  public AttributeName getAttributeX() {
    return m_AttributeX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeXTipText() {
    return "The attribute to show on the X axis.";
  }

  /**
   * Sets the attribute to show on the Y axis.
   *
   * @param value 	the attribute
   */
  public void setAttributeY(AttributeName value) {
    m_AttributeY = value;
    reset();
  }

  /**
   * Returns the attribute to show on the Y axis.
   *
   * @return 		the attribute
   */
  public AttributeName getAttributeY() {
    return m_AttributeY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeYTipText() {
    return "The attribute to show on the Y axis.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "classLabelRange", m_ClassLabelRange, ", class label: ");
    result += QuickInfoHelper.toString(this, "attributeX", m_AttributeX, ", x-axis: ");
    result += QuickInfoHelper.toString(this, "attributeY", m_AttributeY, ", y-axis: ");
    
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
    ThresholdCurve 	curve;
    Evaluation		eval;
    PlotData2D		plot;
    boolean[] 		connectPoints;
    int			cp;
    Instances 		data;
    int[]		indices;
    
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
	curve = new ThresholdCurve();
	data = curve.getCurve(eval.predictions(), index);
	plot = new PlotData2D(data);
	plot.setPlotName(eval.getHeader().classAttribute().value(index));
	plot.m_displayAllPoints = true;
	connectPoints = new boolean [data.numInstances()];
	for (cp = 1; cp < connectPoints.length; cp++)
	  connectPoints[cp] = true;
	plot.setConnectPoints(connectPoints);
	m_VisualizePanel.addPlot(plot);
	if (data.attribute(m_AttributeX.toDisplay()) != null)
	  m_VisualizePanel.setXIndex(data.attribute(m_AttributeX.toDisplay()).index());
	if (data.attribute(m_AttributeY.toDisplay()) != null)
	  m_VisualizePanel.setYIndex(data.attribute(m_AttributeY.toDisplay()).index());
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
    if (token == null)
      return null;
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
      name = "Threshold curve (" + getEvaluation(token).getHeader().relationName() + ")";
    else
      name = "Threshold curve";

    result = new AbstractComponentDisplayPanel(name) {
      private static final long serialVersionUID = -7362768698548152899L;
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
	    ThresholdCurve curve = new ThresholdCurve();
	    Instances data = curve.getCurve(eval.predictions(), index);
	    PlotData2D plot = new PlotData2D(data);
	    plot.setPlotName(eval.getHeader().classAttribute().value(index));
	    plot.m_displayAllPoints = true;
	    boolean[] connectPoints = new boolean [data.numInstances()];
	    for (int cp = 1; cp < connectPoints.length; cp++)
	      connectPoints[cp] = true;
	    plot.setConnectPoints(connectPoints);
	    m_VisualizePanel.addPlot(plot);
	    if (data.attribute(m_AttributeX.toDisplay()) != null)
	      m_VisualizePanel.setXIndex(data.attribute(m_AttributeX.toDisplay()).index());
	    if (data.attribute(m_AttributeY.toDisplay()) != null)
	      m_VisualizePanel.setYIndex(data.attribute(m_AttributeY.toDisplay()).index());
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
