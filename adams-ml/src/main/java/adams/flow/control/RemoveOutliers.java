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
 * RemoveOutliers.java
 * Copyright (C) 2015-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.container.OutlierContainer;
import adams.flow.control.removeoutliers.AbstractOutlierDetector;
import adams.flow.control.removeoutliers.Null;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.sink.sequenceplotter.OutlierPaintlet;
import adams.flow.sink.sequenceplotter.PolygonSelectionPaintlet;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.ToggleOutlier;
import adams.flow.transformer.AbstractInteractiveTransformerDialog;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.core.Undo;
import adams.gui.event.UndoEvent;
import adams.gui.event.UndoEvent.UndoType;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.LinearRegressionOverlayPaintlet;
import adams.gui.visualization.sequence.MultiPaintlet;
import adams.gui.visualization.sequence.StraightLineOverlayPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Allows the user to interactively remove outliers.<br>
 * You can toggle the 'outlier' state of individual points by left-clicking them.You can also toggle whole regions by selecting a polygon around them: use SHIFT+left-click to place vertices and SHIFT+right-click to finish the polygon.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.OutlierContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.OutlierContainer: Original, Clean, Outliers
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RemoveOutliers
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
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
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 * <pre>-detector &lt;adams.flow.control.removeoutliers.AbstractOutlierDetector&gt; (property: detector)
 * &nbsp;&nbsp;&nbsp;The detector to use for the initial outlier detection.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.removeoutliers.Null
 * </pre>
 *
 * <pre>-col-actual &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnActual)
 * &nbsp;&nbsp;&nbsp;The spreadsheet column with the actual values.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-predicted &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnPredicted)
 * &nbsp;&nbsp;&nbsp;The spreadsheet column with the predicted values.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveOutliers
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = 5761075187069480059L;

  /** the meta-data key for the row index. */
  public final static String KEY_INDEX = "Index";

  /** the meta-data key for the initial flag. */
  public final static String KEY_INITIAL = "Initial";

  /** the meta-data key for the outlier flag. */
  public final static String KEY_OUTLIER = "Outlier";

  /** the detector to use for the initial outlier detection. */
  protected AbstractOutlierDetector m_Detector;

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_ColumnActual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_ColumnPredicted;

  /** the sequence plotter panel. */
  protected SequencePlotterPanel m_PlotterPanel;

  /** whether the data was accepted. */
  protected boolean m_Accepted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to interactively remove outliers.\n"
	     + "You can toggle the 'outlier' state of individual points by left-clicking them.\n"
	     + "You can also toggle whole regions by selecting a polygon around them: "
	     + "use SHIFT+left-click to place vertices and SHIFT+right-click to finish the polygon.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "detector", "detector",
      new Null());

    m_OptionManager.add(
      "col-actual", "columnActual",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "col-predicted", "columnPredicted",
      new SpreadSheetColumnIndex("2"));
  }

  /**
   * Sets the detector to use.
   *
   * @param value        the detector
   */
  public void setDetector(AbstractOutlierDetector value) {
    m_Detector = value;
    reset();
  }

  /**
   * Returns the detector to use.
   *
   * @return		the detector
   */
  public AbstractOutlierDetector getDetector() {
    return m_Detector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String detectorTipText() {
    return "The detector to use for the initial outlier detection.";
  }

  /**
   * Sets the spreadsheet column with the actual values.
   *
   * @param value	the column
   */
  public void setColumnActual(SpreadSheetColumnIndex value) {
    m_ColumnActual = value;
    reset();
  }

  /**
   * Returns the spreadsheet column with the actual values.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnActual() {
    return m_ColumnActual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnActualTipText() {
    return "The spreadsheet column with the actual values.";
  }

  /**
   * Sets the spreadsheet column with the predicted values.
   *
   * @param value	the column
   */
  public void setColumnPredicted(SpreadSheetColumnIndex value) {
    m_ColumnPredicted = value;
    reset();
  }

  /**
   * Returns the spreadsheet column with the predicted values.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnPredicted() {
    return m_ColumnPredicted;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnPredictedTipText() {
    return "The spreadsheet column with the predicted values.";
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
    result += QuickInfoHelper.toString(this, "detector", m_Detector, ", detector: ");
    result += QuickInfoHelper.toString(this, "columnActual", m_ColumnActual, ", actual: ");
    result += QuickInfoHelper.toString(this, "columnPredicted", m_ColumnPredicted, ", predicted: ");

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
    return new Class[]{OutlierContainer.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    ((SequencePlotterPanel) m_Panel).getContainerManager().clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel				result;
    AxisPanelOptions			axis;
    JPanel 				panelRight;
    JPanel 				panelButtonsRight;
    JPanel 				panelBottom;
    JPanel 				panelButtonsBottom;
    JPanel				panelButtonsUndoRedo;
    final Undo				undo;
    final BaseButton			buttonReset;
    final BaseButton			buttonClear;
    final BaseButton			buttonUndo;
    final BaseButton			buttonRedo;
    final BaseButton			buttonOK;
    final BaseButton			buttonCancel;
    OutlierPaintlet			paintlet;
    ToggleOutlier 			mouseClick;
    MultiPaintlet 			overlays;
    StraightLineOverlayPaintlet 	diagonal;
    PolygonSelectionPaintlet		polygonSelection;
    LinearRegressionOverlayPaintlet 	lrPaintlet;

    result = new BasePanel(new BorderLayout());

    m_PlotterPanel = new SequencePlotterPanel("Outliers");
    m_PlotterPanel.setSidePanelVisible(false);

    undo = new Undo();
    m_PlotterPanel.setUndo(undo);

    axis = new AxisPanelOptions();
    axis.setNthValueToShow(2);
    axis.setTickGenerator(new FancyTickGenerator());
    axis.setLabel("actual");
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    axis.setTopMargin(0.05);
    axis.setBottomMargin(0.05);
    axis.setWidth(40);
    axis.configure(m_PlotterPanel.getPlot(), Axis.BOTTOM);

    axis = new AxisPanelOptions();
    axis.setNthValueToShow(2);
    axis.setTickGenerator(new FancyTickGenerator());
    axis.setLabel("predicted");
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    axis.setTopMargin(0.05);
    axis.setBottomMargin(0.05);
    axis.setWidth(60);
    axis.configure(m_PlotterPanel.getPlot(), Axis.LEFT);

    paintlet   = new OutlierPaintlet();
    mouseClick = new ToggleOutlier();
    mouseClick.setHitDetector(paintlet.getHitDetector());

    overlays = new MultiPaintlet();
    diagonal = new StraightLineOverlayPaintlet();
    diagonal.setColor(Color.RED.darker());
    lrPaintlet = new LinearRegressionOverlayPaintlet();
    lrPaintlet.setOutputSlopeIntercept(true);
    polygonSelection = new PolygonSelectionPaintlet();
    overlays.setSubPaintlets(new XYSequencePaintlet[]{diagonal, lrPaintlet, polygonSelection});

    m_PlotterPanel.setDataPaintlet(paintlet);
    m_PlotterPanel.setMouseClickAction(mouseClick);
    m_PlotterPanel.setOverlayPaintlet(overlays);
    ActorUtils.updateFlowAwarePaintlet(m_PlotterPanel.getDataPaintlet(), this);
    result.add(m_PlotterPanel, BorderLayout.CENTER);

    panelRight = new JPanel(new BorderLayout());
    panelRight.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    result.add(panelRight, BorderLayout.EAST);

    panelButtonsRight = new JPanel(new GridLayout(3, 1, 5, 5));
    panelRight.add(panelButtonsRight, BorderLayout.NORTH);

    buttonReset = new BaseButton("Reset", ImageManager.getIcon("revert.png"));
    buttonReset.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	XYSequenceContainerManager manager = m_PlotterPanel.getContainerManager();
	if (manager.countVisible() == 0)
	  return;
	m_PlotterPanel.addUndoPoint("reset");
	manager.startUpdate();
	XYSequence seq = manager.getVisible(0).getData();
	for (int i = 0; i < seq.size(); i++) {
	  SequencePlotPoint point = (SequencePlotPoint) seq.toList().get(i);
	  if (point.hasMetaData()) {
	    if (point.getMetaData().containsKey(KEY_OUTLIER))
	      point.getMetaData().put(KEY_OUTLIER, point.getMetaData().containsKey(KEY_INITIAL));
	  }
	}
	manager.finishUpdate();
      }
    });
    panelButtonsRight.add(buttonReset);

    buttonClear = new BaseButton("Clear", ImageManager.getIcon("new.gif"));
    buttonClear.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	XYSequenceContainerManager manager = m_PlotterPanel.getContainerManager();
	if (manager.countVisible() == 0)
	  return;
	manager.startUpdate();
	m_PlotterPanel.addUndoPoint("clear");
	XYSequence seq = manager.getVisible(0).getData();
	for (int i = 0; i < seq.size(); i++) {
	  SequencePlotPoint point = (SequencePlotPoint) seq.toList().get(i);
	  if (point.hasMetaData()) {
	    if (point.getMetaData().containsKey(KEY_OUTLIER))
	      point.getMetaData().put(KEY_OUTLIER, false);
	  }
	}
	manager.finishUpdate();
      }
    });
    panelButtonsRight.add(buttonClear);

    panelButtonsUndoRedo = new JPanel(new GridLayout(1, 2, 5, 0));
    panelButtonsRight.add(panelButtonsUndoRedo);

    buttonUndo = new BaseButton(ImageManager.getIcon("undo.gif"));
    buttonUndo.addActionListener((ActionEvent e) -> m_PlotterPanel.undo());
    panelButtonsUndoRedo.add(buttonUndo);

    buttonRedo = new BaseButton(ImageManager.getIcon("redo.gif"));
    buttonRedo.addActionListener((ActionEvent e) -> m_PlotterPanel.redo());
    panelButtonsUndoRedo.add(buttonRedo);

    panelBottom = new JPanel(new BorderLayout());
    result.add(panelBottom, BorderLayout.SOUTH);

    panelButtonsBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBottom.add(panelButtonsBottom, BorderLayout.EAST);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Accepted = true;
	m_Dialog.setVisible(false);
      }
    });
    panelButtonsBottom.add(buttonOK);

    buttonCancel = new BaseButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Accepted = false;
	m_Dialog.setVisible(false);
      }
    });
    panelButtonsBottom.add(buttonCancel);

    undo.addUndoListener((UndoEvent e) -> {
      if ((e.getType() == UndoType.UNDO) || (e.getType() == UndoType.REDO))
	m_PlotterPanel.update();
      buttonUndo.setEnabled(m_PlotterPanel.canUndo());
      buttonRedo.setEnabled(m_PlotterPanel.canRedo());
    });

    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		null if successfully interacted, otherwise error message
   */
  @Override
  public String doInteract() {
    SpreadSheet 		original;
    Set<Integer> 		outlierIndices;
    XYSequenceContainerManager 	manager;
    XYSequenceContainer 	cont;
    SequencePlotSequence 	seq;
    SequencePlotPoint 		point;
    int				i;
    Double			act;
    Double			pred;
    SpreadSheet			clean;
    SpreadSheet			outliers;
    boolean			isOutlier;
    int				index;
    Row				row;
    TIntList			indices;

    original = (SpreadSheet) m_InputToken.getPayload();
    m_ColumnActual.setData(original);
    m_ColumnPredicted.setData(original);
    outlierIndices = m_Detector.detect(original, m_ColumnActual, m_ColumnPredicted);
    if (isLoggingEnabled())
      getLogger().info("Outlier indices: " + outlierIndices);

    manager  = m_PlotterPanel.getContainerManager();
    manager.startUpdate();
    seq      = new SequencePlotSequence();
    seq.setComparison(Comparison.X_AND_Y);
    seq.setMetaDataKey(KEY_INDEX);
    if (original.hasName())
      seq.setID(original.getName());
    else
      seq.setID("Pred vs Act");
    cont = manager.newContainer(seq);
    manager.add(cont);

    for (i = 0; i < original.getRowCount(); i++) {
      act  = original.getCell(i, m_ColumnActual.getIntIndex()).toDouble();
      pred = original.getCell(i, m_ColumnPredicted.getIntIndex()).toDouble();
      if ((act != null) && (pred != null)) {
	point = new SequencePlotPoint("" + seq.size(), act, pred);
	point.setMetaData(new HashMap<>());
	point.getMetaData().put(KEY_INDEX, i);
	if (outlierIndices.contains(i)) {
	  point.getMetaData().put(KEY_OUTLIER, true);
	  point.getMetaData().put(KEY_INITIAL, true);
	}
	seq.add(point);
      }
    }
    manager.finishUpdate();

    registerWindow(m_Dialog, m_Dialog.getTitle());
    m_Accepted = false;
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);

    if (m_Accepted) {
      clean    = original.getHeader();
      outliers = original.getHeader();
      indices  = new TIntArrayList();
      for (i = 0; i < seq.size(); i++) {
	point     = (SequencePlotPoint) seq.toList().get(i);
	index     = (Integer) point.getMetaData().get(KEY_INDEX);
	isOutlier = false;
	if (point.getMetaData().containsKey(KEY_OUTLIER))
	  isOutlier = (Boolean) point.getMetaData().get(KEY_OUTLIER);
	if (isOutlier) {
	  row = original.getRow(index).getClone(outliers);
	  outliers.addRow().assign(row);
	  indices.add(index);
	}
	else {
	  row = original.getRow(index).getClone(clean);
	  clean.addRow().assign(row);
	}
      }
      m_OutputToken = new Token(
	new OutlierContainer(original, clean, outliers, indices.toArray()));
    }

    return null;
  }
}
