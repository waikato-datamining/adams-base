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
 * WekaClassifierErrors.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JComponent;

import adams.core.Shortening;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.visualize.Plot2D;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.weka.predictions.AbstractErrorScaler;
import adams.data.weka.predictions.AutoScaler;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;

/**
 <!-- globalinfo-start -->
 * Actor for displaying classifier errors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierErrors
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
 * <pre>-scaler &lt;adams.data.weka.predictions.AbstractErrorScaler&gt; (property: errorScaler)
 * &nbsp;&nbsp;&nbsp;The scaler to use for scaling the errors.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.predictions.AutoScaler -scaler adams.data.weka.predictions.RelativeNumericErrorScaler
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierErrors
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 3247255046513744115L;

  /**
   * Helper class for generating visualization data.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DataGenerator {

    /** the underlying Evaluation object. */
    protected Evaluation m_Evaluation;

    /** the underlying data. */
    protected Instances m_PlotInstances;

    /** for storing the plot shapes. */
    protected FastVector m_PlotShapes;

    /** for storing the plot sizes. */
    protected FastVector m_PlotSizes;

    /** the scaler scheme to use. */
    protected AbstractErrorScaler m_ErrorScaler;

    /** whether the data has already been processed. */
    protected boolean m_Processed;

    /**
     * Initializes the generator.
     *
     * @param eval	the Evaluation object to use
     * @param scaler	the scaler scheme to use for the errors
     */
    public DataGenerator(Evaluation eval, AbstractErrorScaler scaler) {
      super();

      m_Evaluation  = eval;
      m_ErrorScaler = scaler;
      m_Processed   = false;
    }

    /**
     * Processes the data if necessary.
     */
    protected void process() {
      Capabilities		cap;
      ArrayList<Integer>	scaled;

      if (m_Processed)
	return;

      m_Processed = true;

      createDataset(m_Evaluation);

      try {
	cap = m_ErrorScaler.getCapabilities();
	cap.testWithFail(m_PlotInstances.classAttribute(), true);
	scaled = m_ErrorScaler.scale(m_PlotSizes);
	m_PlotSizes = new FastVector();
	m_PlotSizes.addAll(scaled);
      }
      catch (Exception e) {
	e.printStackTrace();
	m_PlotInstances = new Instances(m_PlotInstances, 0);
	m_PlotSizes     = new FastVector();
	m_PlotShapes    = new FastVector();
      }
    }

    /**
     * Returns the underlying Evaluation object.
     *
     * @return		the Evaluation object
     */
    public Evaluation getEvaluation() {
      return m_Evaluation;
    }

    /**
     * Returns the scaling scheme.
     *
     * @return		the scaler
     */
    public AbstractErrorScaler getErrorScaler() {
      return m_ErrorScaler;
    }

    /**
     * Returns the generated dataset that is plotted.
     *
     * @return		the dataset
     */
    public Instances getPlotInstances() {
      process();

      return m_PlotInstances;
    }

    /**
     * Generates a dataset, containing the predicted vs actual values.
     *
     * @param eval	for obtaining the dataset information and predictions
     */
    protected void createDataset(Evaluation eval) {
      ArrayList<Attribute>	atts;
      Attribute			classAtt;
      ArrayList<Prediction>	preds;
      int			i;
      double[]			values;
      Instance			inst;
      Prediction		pred;

      m_PlotShapes = new FastVector();
      m_PlotSizes  = new FastVector();
      classAtt     = eval.getHeader().classAttribute();
      preds        = eval.predictions();

      // generate header
      atts     = new ArrayList<Attribute>();
      atts.add(classAtt.copy("predicted" + classAtt.name()));
      atts.add((Attribute) classAtt.copy());
      m_PlotInstances = new Instances(
  	eval.getHeader().relationName() + "-classifier_errors", atts, preds.size());
      m_PlotInstances.setClassIndex(m_PlotInstances.numAttributes() - 1);

      // add data
      for (i = 0; i < preds.size(); i++) {
        pred   = preds.get(i);
        values = new double[]{pred.predicted(), pred.actual()};
        inst   = new DenseInstance(pred.weight(), values);
        m_PlotInstances.add(inst);

        if (classAtt.isNominal()) {
          if (weka.core.Utils.isMissingValue(pred.actual()) || weka.core.Utils.isMissingValue(pred.predicted())) {
            m_PlotShapes.addElement(new Integer(Plot2D.MISSING_SHAPE));
          }
          else if (pred.predicted() != pred.actual()) {
            // set to default error point shape
            m_PlotShapes.addElement(new Integer(Plot2D.ERROR_SHAPE));
          }
          else {
            // otherwise set to constant (automatically assigned) point shape
            m_PlotShapes.addElement(new Integer(Plot2D.CONST_AUTOMATIC_SHAPE));
          }
          m_PlotSizes.addElement(new Integer(Plot2D.DEFAULT_SHAPE_SIZE));
        }
        else {
          // store the error (to be converted to a point size later)
          Double errd = null;
          if (!weka.core.Utils.isMissingValue(pred.actual()) && !weka.core.Utils.isMissingValue(pred.predicted())) {
            errd = new Double(pred.predicted() - pred.actual());
            m_PlotShapes.addElement(new Integer(Plot2D.CONST_AUTOMATIC_SHAPE));
          }
          else {
            // missing shape if actual class not present or prediction is missing
            m_PlotShapes.addElement(new Integer(Plot2D.MISSING_SHAPE));
          }
          m_PlotSizes.addElement(errd);
        }
      }
    }

    /**
     * Assembles and returns the plot. The relation name of the dataset gets
     * added automatically.
     *
     * @return			the plot
     * @throws Exception	if plot generation fails
     */
    public PlotData2D getPlotData() throws Exception {
      PlotData2D 	result;

      process();

      result = new PlotData2D(m_PlotInstances);
      result.setShapeSize(m_PlotSizes);
      result.setShapeType(m_PlotShapes);
      result.setPlotName("Classifier Errors" + " (" + m_PlotInstances.relationName() + ")");
      result.addInstanceNumberAttribute();

      return result;
    }
  }

  /** the Weka plot panel. */
  protected VisualizePanel m_VisualizePanel;

  /** The scheme for scaling the errors. */
  protected AbstractErrorScaler m_ErrorScaler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying classifier errors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "scaler", "errorScaler",
	    new AutoScaler());
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
   * Sets the scheme for scaling the errors.
   *
   * @param value 	the scheme
   */
  public void setErrorScaler(AbstractErrorScaler value) {
    m_ErrorScaler = value;
    reset();
  }

  /**
   * Returns the scheme to use for scaling the errors.
   *
   * @return 		the scheme
   */
  public AbstractErrorScaler getErrorScaler() {
    return m_ErrorScaler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String errorScalerTipText() {
    return "The scaler to use for scaling the errors.";
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
    result += QuickInfoHelper.toString(this, "errorScaler", Shortening.shortenEnd(OptionUtils.getShortCommandLine(m_ErrorScaler), 40), ", error scaler: ");
    
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
    m_VisualizePanel = new VisualizePanel();
    result.add(m_VisualizePanel, BorderLayout.CENTER);

    return result;
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
   * ClassifierErrorss the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    DataGenerator	generator;
    Evaluation		eval;

    try {
      if (token.getPayload() instanceof WekaEvaluationContainer)
	eval = (Evaluation) ((WekaEvaluationContainer) token.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
      else
	eval = (Evaluation) token.getPayload();
      if (eval.predictions() == null) {
	getLogger().severe("No predictions available from Evaluation object!");
	return;
      }
      generator = new DataGenerator(eval, m_ErrorScaler);
      PlotData2D plotdata = generator.getPlotData();
      plotdata.setPlotName(generator.getPlotInstances().relationName());
      m_VisualizePanel.addPlot(plotdata);
      m_VisualizePanel.setColourIndex(plotdata.getPlotInstances().classIndex());
      if ((m_VisualizePanel.getXIndex() == 0) && (m_VisualizePanel.getYIndex() == 1)) {
	try {
	  m_VisualizePanel.setXIndex(m_VisualizePanel.getInstances().classIndex());  // class
	  m_VisualizePanel.setYIndex(m_VisualizePanel.getInstances().classIndex() - 1);  // predicted class
	}
	catch (Exception e) {
	  // ignored
	}
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
    if (m_VisualizePanel != null) {
      m_VisualizePanel.removeAllPlots();
      m_VisualizePanel = null;
    }

    super.cleanUpGUI();
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
      name = "Classifier errors (" + getEvaluation(token).getHeader().relationName() + ")";
    else
      name = "Classifier errors";

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
	  DataGenerator generator = new DataGenerator(eval, m_ErrorScaler);
	  PlotData2D plotdata = generator.getPlotData();
	  plotdata.setPlotName(generator.getPlotInstances().relationName());
	  m_VisualizePanel.addPlot(plotdata);
	  m_VisualizePanel.setColourIndex(plotdata.getPlotInstances().classIndex());
	  if ((m_VisualizePanel.getXIndex() == 0) && (m_VisualizePanel.getYIndex() == 1)) {
	    try {
	      m_VisualizePanel.setXIndex(m_VisualizePanel.getInstances().classIndex());  // class
	      m_VisualizePanel.setYIndex(m_VisualizePanel.getInstances().classIndex() - 1);  // predicted class
	    }
	    catch (Exception e) {
	      // ignored
	    }
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
