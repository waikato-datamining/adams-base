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
 * MOAClusterVisualization.java
 * Copyright (C) 2010 Jansen moa@cs.rwth-aachen.de
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseMeasureCollection;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.source.MOAClustererSetup;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextPanel;
import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import moa.clusterers.ClusterGenerator;
import moa.evaluation.MeasureCollection;
import moa.gui.visualization.DataPoint;
import moa.gui.visualization.GraphCanvas;
import moa.gui.visualization.StreamPanel;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Visualizes MOA clusters.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
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
 * &nbsp;&nbsp;&nbsp;default: MOAClusterVisualization
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
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
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
 * <pre>-clusterer &lt;adams.flow.core.CallableActorReference&gt; (property: clusterer)
 * &nbsp;&nbsp;&nbsp;The name of the callable MOA clusterer to visualize.
 * &nbsp;&nbsp;&nbsp;default: MOAClustererSetup
 * </pre>
 * 
 * <pre>-measure &lt;adams.core.base.BaseMeasureCollection&gt; [-measure ...] (property: measures)
 * &nbsp;&nbsp;&nbsp;The measures to collect.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-decay-horizon &lt;int&gt; (property: decayHorizon)
 * &nbsp;&nbsp;&nbsp;The size of the decay horizon in data points (= sliding window).
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-process-frequency &lt;int&gt; (property: processFrequency)
 * &nbsp;&nbsp;&nbsp;The amount of instances to process in one step.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-redraw-interval &lt;int&gt; (property: redrawInterval)
 * &nbsp;&nbsp;&nbsp;After how many instances do we repaint.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-draw-points &lt;boolean&gt; (property: drawPoints)
 * &nbsp;&nbsp;&nbsp;Whether to draw the points.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-draw-ground-truth &lt;boolean&gt; (property: drawGroundTruth)
 * &nbsp;&nbsp;&nbsp;Whether to draw the ground truth.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-draw-micro-clustering &lt;boolean&gt; (property: drawMicroClustering)
 * &nbsp;&nbsp;&nbsp;Whether to draw the micro clustering.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-draw-clustering &lt;boolean&gt; (property: drawClustering)
 * &nbsp;&nbsp;&nbsp;Whether to draw the clustering.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author Jansen moa@cs.rwth-aachen.de (original MOA code)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Jansen moa@cs.rwth-aachen.de",
  license = License.APACHE2,
  note = "Code extracted from: moa.gui.visualization.RunVisualizer"
)
public class MOAClusterVisualization
  extends AbstractGraphicalDisplay {

  private static final long serialVersionUID = -7015408219912566981L;

  /** the name of the callable clusterer to use. */
  protected CallableActorReference m_Clusterer;

  /** the measures to use. */
  protected BaseMeasureCollection[] m_Measures;

  /** the measure collections contain all the measures */
  protected MeasureCollection[] m_ActualMeasures;

  /** amount of relevant instances; older instances will be dropped;
   creates the 'sliding window' over the stream;
   is strongly connected to the decay rate and decay threshold*/
  protected int m_DecayHorizon;

  /** the decay threshold defines the minimum weight of an instance to be relevant */
  protected double m_DecayThreshold;

  /** whether to draw the points. */
  protected boolean m_DrawPoints;

  /** whether to draw the ground truth. */
  protected boolean m_DrawGroundTruth;

  /** whether to draw the micro clustering. */
  protected boolean m_DrawMicroClustering;

  /** whether to draw the clustering. */
  protected boolean m_DrawClustering;

  /** amount of instances to process in one step */
  protected int m_ProcessFrequency;

  /** after how many instances do we repaint the streampanel?
   *  the GUI becomes very slow with small values. */
  protected int m_RedrawInterval;

  /** stream panel that datapoints and clusterings will be drawn to */
  protected StreamPanel m_StreamPanel;

  /** the cluster algorithm to use. */
  protected AbstractClusterer m_ActualClusterer;

  /** the decay rate of the stream, often reffered to as lambda;
   is being calculated from the horizion and the threshold
   as these are more intuitive to define */
  protected double m_DecayRate;

  /** all possible clusterings */
  //not pretty to have all the clusterings, but otherwise we can't just redraw clusterings
  protected Clustering m_gtClustering;
  protected Clustering m_Macro;
  protected Clustering m_Micro;

  /** panel to hold the graph */
  protected GraphCanvas m_Graphcanvas;

  /** for outputting textual stats. */
  protected TextPanel m_LogPanel;

  /** the buffer for the data points. */
  protected LinkedList<DataPoint> m_PointBuffer;

  /** the array of points to process. */
  protected ArrayList<DataPoint> m_PointArray;

  /** the timestamp for the data. */
  protected int m_Timestamp;

  /** the process counter. */
  protected int m_ProcessCounter;

  /** the speed counter. */
  protected int m_SpeedCounter;

  /** the combobox for the X dimension. */
  protected JComboBox m_ComboBoxDimX;

  /** the combobox for the Y dimension. */
  protected JComboBox m_ComboBoxDimY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Visualizes MOA clusters.";
  }

  /**
   * Resets the object. Removes graphical components as well.
   */
  @Override
  protected void reset() {
    super.reset();

    m_PointBuffer    = new LinkedList<DataPoint>();
    m_PointArray     = null;
    m_Timestamp      = 0;
    m_ProcessCounter = 0;
    m_SpeedCounter   = 0;
    m_gtClustering   = null;
    m_Macro          = null;
    m_Micro          = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "clusterer", "clusterer",
      new CallableActorReference(MOAClustererSetup.class.getSimpleName()));

    m_OptionManager.add(
      "measure", "measures",
      new BaseMeasureCollection[0]);

    m_OptionManager.add(
      "decay-horizon", "decayHorizon",
      1000, 1, null);

    m_OptionManager.add(
      "process-frequency", "processFrequency",
      1000, 1, null);

    m_OptionManager.add(
      "redraw-interval", "redrawInterval",
      100, 1, null);

    m_OptionManager.add(
      "draw-points", "drawPoints",
      true);

    m_OptionManager.add(
      "draw-ground-truth", "drawGroundTruth",
      true);

    m_OptionManager.add(
      "draw-micro-clustering", "drawMicroClustering",
      true);

    m_OptionManager.add(
      "draw-clustering", "drawClustering",
      true);
  }

  /**
   * Sets the callable clusterer to use.
   *
   * @param value	the clusterer name
   */
  public void setClusterer(CallableActorReference value) {
    m_Clusterer = value;
    reset();
  }

  /**
   * Returns the callable clusterer to use.
   *
   * @return		the clusterer name
   */
  public CallableActorReference getClusterer() {
    return m_Clusterer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clustererTipText() {
    return "The name of the callable MOA clusterer to visualize.";
  }

  /**
   * Sets the size of the decay horizon.
   *
   * @param value	the size in data points
   */
  public void setDecayHorizon(int value) {
    m_DecayHorizon = value;
    reset();
  }

  /**
   * Returns the size of the decay horizon.
   *
   * @return		the size in data points
   */
  public int getDecayHorizon() {
    return m_DecayHorizon;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String decayHorizonTipText() {
    return "The size of the decay horizon in data points (= sliding window).";
  }

  /**
   * Sets the amount of instances to process in one step.
   *
   * @param value	the number of instances
   */
  public void setProcessFrequency(int value) {
    m_ProcessFrequency = value;
    reset();
  }

  /**
   * Returns the amount of instances to process in one step.
   *
   * @return		the number of instances
   */
  public int getProcessFrequency() {
    return m_ProcessFrequency;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processFrequencyTipText() {
    return "The amount of instances to process in one step.";
  }

  /**
   * Sets after how many instances do we repaint.
   *
   * @param value	the number of instances
   */
  public void setRedrawInterval(int value) {
    m_RedrawInterval = value;
    reset();
  }

  /**
   * Returns after how many instances do we repaint.
   *
   * @return		the number of instances
   */
  public int getRedrawInterval() {
    return m_RedrawInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String redrawIntervalTipText() {
    return "After how many instances do we repaint.";
  }

  /**
   * Sets whether to draw the points.
   *
   * @param value	true if to draw
   */
  public void setDrawPoints(boolean value) {
    m_DrawPoints = value;
    reset();
  }

  /**
   * Returns whether to draw the points.
   *
   * @return		true if to draw
   */
  public boolean getDrawPoints() {
    return m_DrawPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String drawPointsTipText() {
    return "Whether to draw the points.";
  }

  /**
   * Sets whether to draw the ground truth.
   *
   * @param value	true if to draw
   */
  public void setDrawGroundTruth(boolean value) {
    m_DrawGroundTruth = value;
    reset();
  }

  /**
   * Returns whether to draw the ground truth.
   *
   * @return		true if to draw
   */
  public boolean getDrawGroundTruth() {
    return m_DrawGroundTruth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String drawGroundTruthTipText() {
    return "Whether to draw the ground truth.";
  }

  /**
   * Sets whether to draw the micro clustering.
   *
   * @param value	true if to draw
   */
  public void setDrawMicroClustering(boolean value) {
    m_DrawMicroClustering = value;
    reset();
  }

  /**
   * Returns whether to draw the micro clustering.
   *
   * @return		true if to draw
   */
  public boolean getDrawMicroClustering() {
    return m_DrawMicroClustering;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String drawMicroClusteringTipText() {
    return "Whether to draw the micro clustering.";
  }

  /**
   * Sets whether to draw the clustering.
   *
   * @param value	true if to draw
   */
  public void setDrawClustering(boolean value) {
    m_DrawClustering = value;
    reset();
  }

  /**
   * Returns whether to draw the clustering.
   *
   * @return		true if to draw
   */
  public boolean getDrawClustering() {
    return m_DrawClustering;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String drawClusteringTipText() {
    return "Whether to draw the clustering.";
  }

  /**
   * Sets the measures to collect.
   *
   * @param value	the measures
   */
  public void setMeasures(BaseMeasureCollection[] value) {
    int		i;

    m_Measures       = value;
    m_ActualMeasures = new MeasureCollection[m_Measures.length];
    for (i = 0; i < m_Measures.length; i++)
      m_ActualMeasures[i] = m_Measures[i].collectionValue();

    reset();
  }

  /**
   * Returns the measures to collect.
   *
   * @return		the measures
   */
  public BaseMeasureCollection[] getMeasures() {
    return m_Measures;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String measuresTipText() {
    return "The measures to collect.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "clusterer", m_Clusterer);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instance.class, Instances.class};
  }

  /**
   * Returns an instance of the callable clusterer.
   *
   * @return		the clusterer
   */
  protected AbstractClusterer getClustererInstance() {
    return (AbstractClusterer) CallableActorHelper.getSetup(AbstractClusterer.class, m_Clusterer, this);
  }

  /**
   * Draws the clusterings.
   *
   * @param points	the points to draw
   */
  protected void drawClusterings(List<DataPoint> points) {
    if (m_Macro != null && m_Macro.size() > 0)
      m_StreamPanel.drawMacroClustering(m_Macro, points, Color.RED);
    if (m_Micro != null && m_Micro.size() > 0)
      m_StreamPanel.drawMicroClustering(m_Micro, points, Color.GREEN);
    if (m_gtClustering != null && m_gtClustering.size() > 0)
      m_StreamPanel.drawGTClustering(m_gtClustering, points, Color.BLACK);
  }

  /**
   * Evaluates the clustering.
   *
   * @param found_clustering
   * @param trueClustering
   * @param points
   * @param algorithm
   */
  protected void evaluateClustering(Clustering found_clustering, Clustering trueClustering, ArrayList<DataPoint> points, boolean algorithm) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < m_ActualMeasures.length; i++) {
      if (algorithm) {
	if ((found_clustering != null) && (found_clustering.size() > 0)) {
	  try {
	    double msec = m_ActualMeasures[i].evaluateClusteringPerformance(found_clustering, trueClustering, points);
	    sb.append(m_ActualMeasures[i].getClass().getSimpleName()+" took "+msec+"ms (Mean:"+ m_ActualMeasures[i].getMeanRunningTime()+")");
	    sb.append("\n");

	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to evaluate clustering performance:", e);
	  }
	}
	else{
	  for (int j = 0; j < m_ActualMeasures[i].getNumMeasures(); j++) {
	    m_ActualMeasures[i].addEmptyValue(j);
	  }
	}
      }
    }
    m_LogPanel.setContent(sb.toString());
    m_Graphcanvas.updateCanvas();
  }

  /**
   * Processes the clusterings and initiates the drawing.
   *
   * @param points	the data points
   */
  protected void processClusterings(ArrayList<DataPoint> points) {
    m_gtClustering = new Clustering(points);
    Clustering evalClustering;

    //special case for ClusterGenerator
    if (m_ActualClusterer instanceof ClusterGenerator)
      ((ClusterGenerator) m_ActualClusterer).setSourceClustering(m_gtClustering);

    m_Macro = m_ActualClusterer.getClusteringResult();
    evalClustering = m_Macro;

    // TODO: should we check if micro/macro is being drawn or needed for evaluation and skip otherwise to speed things up?
    if (m_ActualClusterer.implementsMicroClusterer()) {
      m_Micro = m_ActualClusterer.getMicroClusteringResult();
      if (m_Macro == null && m_Micro != null) {
	// TODO: we need a Macro Clusterer Interface and the option for kmeans to use the non optimal centers
	m_Macro = moa.clusterers.KMeans.gaussianMeans(m_gtClustering, m_Micro);
      }
      if (m_ActualClusterer.evaluateMicroClusteringOption.isSet())
	evalClustering = m_Micro;
      else
	evalClustering = m_Macro;
    }

    evaluateClustering(evalClustering, m_gtClustering, points, true);

    drawClusterings(points);
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    List<Instance>	list;
    boolean		first;
    int			i;
    List<String>	atts;

    first = false;

    if (m_ActualClusterer == null) {
      m_ActualClusterer = getClustererInstance();
      if (m_ActualClusterer == null)
	throw new IllegalStateException("Failed to get instance of clusterer '" + m_Clusterer + "'!");
      m_ActualClusterer.prepareForUse();
      first = true;
    }

    if (m_ActualMeasures.length == 0)
      throw new IllegalStateException("No measures configured!");

    m_DecayRate = (Math.log(1.0/ m_DecayThreshold)/Math.log(2)/ m_DecayHorizon);;

    if (token.getPayload() instanceof Instance) {
      list = new ArrayList<>();
      list.add((Instance) token.getPayload());
    }
    else {
      list = new ArrayList<>((Instances) token.getPayload());
    }

    // dimensions
    if (first) {
      atts = new ArrayList<>();
      for (i = 0; i < list.get(0).dataset().numAttributes(); i++) {
	if (i == list.get(0).classIndex())
	  continue;
	atts.add(list.get(0).dataset().attribute(i).name());
      }
      if (atts.size() > 0) {
	m_ComboBoxDimX.setModel(new DefaultComboBoxModel<String>(atts.toArray(new String[atts.size()])));
	m_ComboBoxDimX.setSelectedIndex(0);
	m_ComboBoxDimY.setModel(new DefaultComboBoxModel<String>(atts.toArray(new String[atts.size()])));
	m_ComboBoxDimY.setSelectedIndex(atts.size() > 1 ? 1 : 0);
      }
    }

    for (Instance item : list) {
      m_Timestamp++;
      m_SpeedCounter++;
      m_ProcessCounter++;

      DataPoint point = new DataPoint(item, m_Timestamp);

      m_PointBuffer.add(point);
      while (m_PointBuffer.size() > m_DecayHorizon) {
	m_PointBuffer.removeFirst();
      }

      if (m_DrawPoints) {
	if (m_Timestamp == 1)
	  m_StreamPanel.componentResized(null);
	m_StreamPanel.drawPoint(point);
	if (m_ProcessCounter % m_RedrawInterval == 0)
	  m_StreamPanel.applyDrawDecay(m_DecayHorizon / (float) (m_RedrawInterval));
      }

      Instance trainInst = new DenseInstance(point);
      if (m_ActualClusterer.keepClassLabel())
	trainInst.setDataset(point.dataset());
      else
	trainInst.deleteAttributeAt(point.classIndex());
      m_ActualClusterer.trainOnInstanceImpl(trainInst);

      if (m_ProcessCounter >= m_ProcessFrequency) {
	m_ProcessCounter = 0;
	for (DataPoint p: m_PointBuffer)
	  p.updateWeight(m_Timestamp, m_DecayRate);

	m_PointArray = new ArrayList<DataPoint>(m_PointBuffer);

	processClusterings(m_PointArray);
      }
    }
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel			result;
    BaseSplitPane		split;
    BaseTabbedPane		tabs;
    JPanel			panelClusters;
    JPanel			panelGraph;
    BaseScrollPane 		graphScrollPanel;
    JPanel 			panel;
    JPanel 			panel2;
    JLabel 			label;
    final JComboBox<String>	comboBox;
    JButton 			button;
    List<String>		measures;
    int				i;

    result = new BasePanel(new BorderLayout());

    split = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    split.setOneTouchExpandable(true);
    result.add(split, BorderLayout.CENTER);

    panelClusters = new JPanel(new BorderLayout());
    split.setTopComponent(panelClusters);

    m_StreamPanel = new StreamPanel();
    m_StreamPanel.setGroundTruthLayerVisibility(m_DrawGroundTruth);
    m_StreamPanel.setMicroLayerVisibility(m_DrawMicroClustering);
    m_StreamPanel.setMacroLayerVisibility(m_DrawClustering);
    m_StreamPanel.setPointVisibility(m_DrawPoints);
    panelClusters.add(new BaseScrollPane(m_StreamPanel), BorderLayout.CENTER);

    m_ComboBoxDimX = new JComboBox(new String[0]);
    m_ComboBoxDimX.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_StreamPanel.setActiveXDim(m_ComboBoxDimX.getSelectedIndex());
	m_StreamPanel.repaint();
      }
    });
    m_ComboBoxDimY = new JComboBox(new String[0]);
    m_ComboBoxDimY.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_StreamPanel.setActiveYDim(m_ComboBoxDimY.getSelectedIndex());
	m_StreamPanel.repaint();
      }
    });

    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    panelClusters.add(panel, BorderLayout.WEST);

    panel2 = new JPanel(new GridLayout(4, 1));
    panel.add(panel2, BorderLayout.NORTH);

    label = new JLabel("Dim X");
    label.setDisplayedMnemonic('X');
    label.setLabelFor(m_ComboBoxDimX);
    panel2.add(label);
    panel2.add(m_ComboBoxDimX);

    label = new JLabel("Dim Y");
    label.setDisplayedMnemonic('Y');
    label.setLabelFor(m_ComboBoxDimY);
    panel2.add(label);
    panel2.add(m_ComboBoxDimY);

    tabs = new BaseTabbedPane();
    split.setBottomComponent(tabs);

    // graph
    panelGraph = new JPanel(new BorderLayout());
    tabs.addTab("Graph", panelGraph);

    m_Graphcanvas = new GraphCanvas();
    m_Graphcanvas.setPreferredSize(new Dimension(500, 111));
    graphScrollPanel = new BaseScrollPane();
    GroupLayout graphCanvasLayout = new GroupLayout(m_Graphcanvas);
    m_Graphcanvas.setLayout(graphCanvasLayout);
    graphCanvasLayout.setHorizontalGroup(
      graphCanvasLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 515, Short.MAX_VALUE));
    graphCanvasLayout.setVerticalGroup(
      graphCanvasLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 128, Short.MAX_VALUE));
    graphScrollPanel.setViewportView(m_Graphcanvas);
    m_Graphcanvas.setGraph(m_ActualMeasures[0], null, 0, m_ProcessFrequency);
    panelGraph.add(new BaseScrollPane(m_Graphcanvas), BorderLayout.CENTER);

    // measures
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    panelGraph.add(panel, BorderLayout.WEST);

    panel2   = new JPanel(new GridLayout(2, 1));
    panel.add(panel2, BorderLayout.NORTH);
    measures = new ArrayList<>();
    for (MeasureCollection measure: m_ActualMeasures) {
      for (i = 0; i < measure.getNumMeasures(); i++)
	measures.add(measure.getName(i));
    }
    comboBox = new JComboBox<String>(measures.toArray(new String[measures.size()]));
    comboBox.setSelectedIndex(0);
    comboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (comboBox.getSelectedIndex() == -1)
	  return;
	String name = (String) comboBox.getSelectedItem();
	int sel = -1;
	MeasureCollection coll = null;
	for (MeasureCollection measure: m_ActualMeasures) {
	  for (int i = 0; i < measure.getNumMeasures(); i++) {
	    if (measure.getName(i).equals(name)) {
	      coll = measure;
	      sel  = i;
	      break;
	    }
	  }
	  if (coll != null)
	    break;
	}
	m_Graphcanvas.setGraph(coll, null, sel, m_ProcessFrequency);
      }
    });
    label    = new JLabel("Measure");
    label.setDisplayedMnemonic('M');
    label.setLabelFor(comboBox);
    panel2.add(label);
    panel2.add(comboBox);

    // zoom
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    panelGraph.add(panel, BorderLayout.EAST);

    panel2 = new JPanel(new GridLayout(4, 1));
    panel.add(panel2, BorderLayout.NORTH);

    button = new JButton("X", GUIHelper.getIcon("zoom_in.png"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Graphcanvas.scaleXResolution(false);
      }
    });
    panel2.add(button);

    button = new JButton("X", GUIHelper.getIcon("zoom_out.png"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Graphcanvas.scaleXResolution(true);
      }
    });
    panel2.add(button);

    button = new JButton("Y", GUIHelper.getIcon("zoom_in.png"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Graphcanvas.scaleYResolution(false);
      }
    });
    panel2.add(button);

    button = new JButton("Y", GUIHelper.getIcon("zoom_out.png"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Graphcanvas.scaleYResolution(true);
      }
    });
    panel2.add(button);

    // log
    m_LogPanel = new TextPanel();
    m_LogPanel.setUpdateParentTitle(false);
    tabs.addTab("Log", new BaseScrollPane(m_LogPanel));

    return result;
  }
}
