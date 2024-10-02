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
 * XYSequencePanel.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.io.output.MetaFileWriter;
import adams.data.io.output.MultiSheetSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.report.AbstractField;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.core.Undo;
import adams.gui.core.Undo.UndoPoint;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.event.PaintListener;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.ScriptingEngine;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.DataHelper;
import adams.gui.visualization.core.AbstractPaintlet;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.CoordinatesPaintlet;
import adams.gui.visualization.core.CoordinatesPaintlet.Coordinates;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PaintletWithFixedXRange;
import adams.gui.visualization.core.PaintletWithFixedYRange;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.FixedLabelTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.HitDetector;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import adams.gui.visualization.core.plot.TipTextCustomizer;

import javax.swing.JMenuItem;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A panel for displaying XY sequences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class XYSequencePanel
  extends DataContainerPanelWithContainerList<XYSequence, XYSequenceContainerManager, XYSequenceContainer>
  implements PaintListener, TipTextCustomizer {

  /** for serialization. */
  private static final long serialVersionUID = 7985845939008731534L;

  /** paintlet for drawing the sequence data. */
  protected XYSequencePaintlet m_XYSequencePaintlet;

  /** paintlet for drawing the X-axis. */
  protected CoordinatesPaintlet m_CoordinatesPaintlet;

  /** the undo manager. */
  protected Undo m_Undo;

  /** whether to adjust to visible data or not. */
  protected boolean m_AdjustToVisibleData;

  /** whether user can resize dialog. */
  protected boolean m_AllowResize;

  /** the file chooser for saving a specific sequence. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the GOE dialog for saving the visible sequences. */
  protected XYSequenceExportDialog m_ExportDialog;

  /** the dialog for displaying a sequence. */
  protected List<SpreadSheetDialog> m_ViewDialogs;

  /**
   * Initializes the panel with no title.
   */
  public XYSequencePanel() {
    super();
  }

  /**
   * Initializes the panel with the specified title.
   *
   * @param title	the title to use
   */
  public XYSequencePanel(String title) {
    super(title);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    m_Undo                = null;
    m_AdjustToVisibleData = true;
    m_AllowResize         = false;

    super.initialize();
  }

  /**
   * Returns whether the panel can handle fixed lables for its axes.
   *
   * @return		true if panel can handle it
   */
  @Override
  public boolean canHandleFixedLabels() {
    return true;
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the container manager to use.
   *
   * @return		the container manager
   */
  @Override
  protected XYSequenceContainerManager newContainerManager() {
    return new XYSequenceContainerManager(this);
  }

  /**
   * Returns the paintlet used for painting the containers.
   *
   * @return		the paintlet
   */
  @Override
  public XYSequencePaintlet getContainerPaintlet() {
    return m_XYSequencePaintlet;
  }

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    Properties	props;

    super.initGUI();

    props = getProperties();

    setAdjustToVisibleData(props.getBoolean("Plot.AdjustToVisibleData", false));

    getPlot().setPopupMenuCustomizer(this);
    getPlot().setTipTextCustomizer(this);

    // paintlets
    m_XYSequencePaintlet = new StickPaintlet();
    m_XYSequencePaintlet.setPanel(this);
    setDataPaintlet(
      AbstractPaintlet.forCommandLine(
	props.getPath("Plot.Paintlet", new StickPaintlet().toCommandLine())));

    m_CoordinatesPaintlet = new CoordinatesPaintlet();
    m_CoordinatesPaintlet.setYInvisible(true);
    m_CoordinatesPaintlet.setPanel(this);
    m_CoordinatesPaintlet.setXColor(props.getColor("Plot.CoordinatesColor." + Coordinates.X, Color.DARK_GRAY));
    m_CoordinatesPaintlet.setYColor(props.getColor("Plot.CoordinatesColor." + Coordinates.Y, Color.DARK_GRAY));

    try {
      getContainerManager().setColorProvider(
	  (ColorProvider) OptionUtils.forAnyCommandLine(
	      ColorProvider.class,
	      props.getProperty("Plot.ColorProvider", DefaultColorProvider.class.getName())));
    }
    catch (Exception e) {
      System.err.println(getClass().getName() + " - Failed to set the color provider:");
      getContainerManager().setColorProvider(new DefaultColorProvider());
    }
  }

  /**
   * Returns the container list.
   *
   * @return		the list
   */
  @Override
  protected XYSequenceContainerList createContainerList() {
    XYSequenceContainerList 	result;

    result = new XYSequenceContainerList();
    result.setManager(getContainerManager());
    result.setAllowSearch(getProperties().getBoolean("ContainerList.AllowSearch", false));
    result.setPopupMenuSupplier(this);

    return result;
  }

  /**
   * Returns the current container manager.
   *
   * @return		the manager
   */
  public AbstractContainerManager getSequenceManager() {
    return m_Manager;
  }

  /**
   * Returns the paintlet used for painting the data.
   *
   * @return		the paintlet
   */
  @Override
  public XYSequencePaintlet getDataPaintlet() {
    return m_XYSequencePaintlet;
  }

  /**
   * Sets the paintlet to use for painting the data.
   *
   * @param value	the paintlet
   */
  public void setDataPaintlet(Paintlet value) {
    removePaintlet(m_XYSequencePaintlet);
    m_XYSequencePaintlet = (XYSequencePaintlet) value;
    m_XYSequencePaintlet.setPanel(this);
    addPaintlet(m_XYSequencePaintlet);
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value) {
    m_Undo = value;
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  public Undo getUndo() {
    return m_Undo;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported() {
    return (m_Undo != null);
  }

  /**
   * Returns a copy of the current state of the sequence manager.
   *
   * @return		the current state
   */
  protected List<XYSequence> getState() {
    return ObjectCopyHelper.copyObject(getSequenceManager().getAll());
  }

  /**
   * Overwrites the sequences with the specified state.
   *
   * @param state	the new state to use
   */
  protected void setState(List<XYSequence> state) {
    getSequenceManager().clear();
    getSequenceManager().startUpdate();
    getSequenceManager().addAll(state);
    getSequenceManager().finishUpdate();
  }

  /**
   * Adds an undo point, if undo is enabled.
   *
   * @param comment	the comment for the undo
   */
  public void addUndoPoint(String comment) {
    if (!isUndoSupported())
      return;
    getUndo().addUndo(getState(), comment);
  }

  /**
   * Returns whether an undo is possible.
   *
   * @return		true if possible
   */
  public boolean canUndo() {
    return isUndoSupported() && getUndo().canUndo();
  }

  /**
   * Performs an undo.
   */
  public void undo() {
    UndoPoint 	point;

    if (canUndo()) {
      getUndo().addRedo(getState(), getUndo().peekUndoComment());
      point = getUndo().undo();
      SwingUtilities.invokeLater(() -> setState((List<XYSequence>) point.getData()));
    }
  }

  /**
   * Returns whether a redo is possible.
   *
   * @return		true if possible
   */
  public boolean canRedo() {
    return isUndoSupported() && getUndo().canRedo();
  }

  /**
   * Performs a redo.
   */
  public void redo() {
    UndoPoint 	point;

    if (canRedo()) {
      getUndo().addUndo(getState(), getUndo().peekRedoComment(), true);
      point = getUndo().redo();
      SwingUtilities.invokeLater(() -> setState((List<XYSequence>) point.getData()));
    }
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  @Override
  protected boolean canPaint(Graphics g) {
    return ((getPlot() != null) && (m_Manager != null));
  }

  /**
   * Sets whether the display is adjusted to only the visible data or
   * everything currently loaded.
   *
   * @param value	if true then plot is adjusted to visible data
   */
  public void setAdjustToVisibleData(boolean value) {
    m_AdjustToVisibleData = value;
    update();
  }

  /**
   * Returns whether the display is adjusted to only the visible spectrums
   * or all of them.
   *
   * @return		true if the plot is adjusted to only the visible data
   */
  public boolean getAdjustToVisibleData() {
    return m_AdjustToVisibleData;
  }

  /**
   * Updates the axes with the min/max of the new data.
   */
  @Override
  public void prepareUpdate() {
    XYSequence			seq;
    double 			minX;
    double 			maxX;
    double 			minY;
    double 			maxY;
    int				i;
    boolean			determineYRange;
    boolean			determineXRange;
    boolean			isFixedX;
    boolean			isFixedY;
    FixedLabelTickGenerator	fixed;
    double[]			keys;
    List<String>		labels;

    determineYRange = !(m_XYSequencePaintlet instanceof PaintletWithFixedYRange);
    determineXRange = !(m_XYSequencePaintlet instanceof PaintletWithFixedXRange);
    isFixedX        = false;
    isFixedY        = false;
    minY            = Double.MAX_VALUE;
    maxY            = -Double.MAX_VALUE;
    minX            = Double.MAX_VALUE;
    maxX            = -Double.MAX_VALUE;

    // fixed labels?
    if (getContainerManager().countVisible() > 0) {
      if (getPlot().getAxis(Axis.LEFT).getTickGenerator() instanceof FixedLabelTickGenerator) {
	isFixedY = true;
	seq      = getContainerManager().getVisible(0).getData(); // TODO first visible?
	labels   = seq.getLabelsY();
	fixed    = (FixedLabelTickGenerator) getPlot().getAxis(Axis.LEFT).getTickGenerator();
	fixed.setLabels(labels);
	if (!labels.isEmpty()) {
	  keys = seq.getMappingsX().keys();
	  minY = StatUtils.min(keys);
	  maxY = StatUtils.max(keys);
	}
	else {
	  minY = 0;
	  maxY = 1;
	}
      }
      if (getPlot().getAxis(Axis.BOTTOM).getTickGenerator() instanceof FixedLabelTickGenerator) {
	isFixedX = true;
	seq      = getContainerManager().getVisible(0).getData(); // TODO first visible?
	labels   = seq.getLabelsX();
	fixed    = (FixedLabelTickGenerator) getPlot().getAxis(Axis.BOTTOM).getTickGenerator();
	fixed.setLabels(labels);
	if (!labels.isEmpty()) {
	  keys = seq.getMappingsX().keys();
	  minX = StatUtils.min(keys);
	  maxX = StatUtils.max(keys);
	}
	else {
	  minX = 0;
	  maxX = 1;
	}
      }
    }

    if (!determineYRange && !isFixedY) {
      minY = ((PaintletWithFixedYRange) m_XYSequencePaintlet).getMinimumY();
      maxY = ((PaintletWithFixedYRange) m_XYSequencePaintlet).getMaximumY();
    }
    if (!determineXRange && !isFixedX) {
      minX = ((PaintletWithFixedXRange) m_XYSequencePaintlet).getMinimumX();
      maxX = ((PaintletWithFixedXRange) m_XYSequencePaintlet).getMaximumX();
    }

    if (determineXRange || determineYRange) {
      for (i = 0; i < getContainerManager().count(); i++) {
	if (m_AdjustToVisibleData) {
	  if (!getContainerManager().isVisible(i))
	    continue;
	}

	seq = getContainerManager().get(i).getData();
	if (seq.isEmpty())
	  continue;

	// determine min/max
	if (determineXRange) {
	  minX = Math.min(minX, XYSequencePoint.toDouble(seq.getMinX().getMinX()));
	  maxX = Math.max(maxX, XYSequencePoint.toDouble(seq.getMaxX().getMaxX()));
	}

	if (determineYRange) {
	  minY = Math.min(minY, XYSequencePoint.toDouble(seq.getMinY().getMinY()));
	  maxY = Math.max(maxY, XYSequencePoint.toDouble(seq.getMaxY().getMaxY()));
	}
      }
    }

    // center, if only 1 data point
    if (minX == maxX) {
      minX -= 1;
      maxX += 1;
    }
    if (minY == maxY) {
      minY -= 1;
      maxY += 1;
    }

    // update axes
    getPlot().getAxis(Axis.LEFT).setMinimum(minY);
    getPlot().getAxis(Axis.LEFT).setMaximum(maxY);
    getPlot().getAxis(Axis.BOTTOM).setMinimum(minX);
    getPlot().getAxis(Axis.BOTTOM).setMaximum(maxX);
  }

  /**
   * Returns a popup menu for the table of the spectrum list.
   *
   * @param table	the affected table
   * @param row	the row the mouse is currently over
   * @return		the popup menu
   */
  @Override
  public BasePopupMenu getContainerListPopupMenu(final ContainerTable table, final int row) {
    BasePopupMenu		result;
    JMenuItem			item;
    final int[] 		indices;

    result  = super.getContainerListPopupMenu(table, row);
    indices = getActualSelectedContainerIndices(table, row);

    result.addSeparator();

    item = new JMenuItem("Save as...");
    item.setEnabled(indices.length == 1);
    item.addActionListener((ActionEvent e) ->
      saveSequence(getContainerManager().get(indices[0])));
    result.add(item);

    item = new JMenuItem("View as table");
    item.setEnabled(indices.length == 1);
    item.addActionListener((ActionEvent e) ->
      viewSequence(getContainerManager().get(indices[0])));
    result.add(item);

    return result;
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    if (m_XYSequencePaintlet instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_XYSequencePaintlet).setAntiAliasingEnabled(value);
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return (m_XYSequencePaintlet instanceof AntiAliasingSupporter)
      && ((AntiAliasingSupporter) m_XYSequencePaintlet).isAntiAliasingEnabled();
  }

  /**
   * Saves all the visible sequences to a directory with a user-specified
   * spreadsheet writer.
   *
   * @param xRange	optional limits for X values
   * @param yRange	optional limits for Y values
   */
  protected void saveVisible(double[] xRange, double[] yRange) {
    SpreadSheetWriter 	writer;
    String 		prefix;
    XYSequence 		seq;
    String[] 		ext;
    String		filename;
    List<SpreadSheet>	data;

    if (m_ExportDialog == null) {
      if (getParentDialog() != null)
	m_ExportDialog = new XYSequenceExportDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_ExportDialog = new XYSequenceExportDialog(getParentFrame(), true);
    }

    m_ExportDialog.setLocationRelativeTo(this);
    m_ExportDialog.setVisible(true);
    if (m_ExportDialog.getOption() != XYSequenceExportDialog.APPROVE_OPTION)
      return;

    writer = m_ExportDialog.getExport();
    if (writer instanceof MetaFileWriter)
      ext = ((MetaFileWriter) writer).getActualFormatExtensions();
    else
      ext = writer.getFormatExtensions();
    if ((writer instanceof MultiSheetSpreadSheetWriter) && m_ExportDialog.getCombine()) {
      filename = getContainerManager().getVisible(0).getDisplayID() + "_and_" + (getContainerManager().countVisible() - 1) + "_more";
      filename = FileUtils.createFilename(filename, "");
      filename = m_ExportDialog.getDirectory().getAbsolutePath() + File.separator + filename + "." + ext[0];
      data = new ArrayList<>();
      for (XYSequenceContainer c: getTableModelContainers(true))
	data.add(DataHelper.filter(c.getData().toSpreadSheet(), "X", xRange, "Y", yRange));
      if (!((MultiSheetSpreadSheetWriter) writer).write(data.toArray(new SpreadSheet[0]), filename))
	GUIHelper.showErrorMessage(this, "Failed to write sequence data to '" + filename + "'!");
    }
    else {
      prefix = m_ExportDialog.getDirectory().getAbsolutePath();
      for (XYSequenceContainer c: getTableModelContainers(true)) {
	seq      = c.getData();
	filename = prefix + File.separator + seq.getID() + "." + ext[0];
	if (!writer.write(DataHelper.filter(seq.toSpreadSheet(), "X", xRange, "Y", yRange), filename)) {
	  GUIHelper.showErrorMessage(this, "Failed to write sequence #" + c + " to '" + filename + "'!");
	  break;
	}
      }
    }
  }

  /**
   * Saves all the visible sequences to a directory with a user-specified
   * spreadsheet writer.
   */
  public void saveVisibleSequences() {
    saveVisible(null, null);
  }

  /**
   * Saves all the visible data points to a directory with a user-specified
   * spreadsheet writer.
   */
  public void saveVisibleDataPoints() {
    saveVisible(
      new double[]{
	m_PlotPanel.getAxis(Axis.BOTTOM).getActualMinimum(),
	m_PlotPanel.getAxis(Axis.BOTTOM).getActualMaximum()
      },
      new double[]{
	m_PlotPanel.getAxis(Axis.LEFT).getActualMinimum(),
	m_PlotPanel.getAxis(Axis.LEFT).getActualMaximum()
      });
  }

  /**
   * Saves the specified sequence as spreadsheet file.
   *
   * @param cont	the sequence to save
   */
  public void saveSequence(XYSequenceContainer cont) {
    int			retVal;
    XYSequence 		seq;
    SpreadSheetWriter	writer;

    if (m_FileChooser == null)
      m_FileChooser = new SpreadSheetFileChooser();

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    seq    = cont.getData();
    writer = m_FileChooser.getWriter();
    if (!writer.write(seq.toSpreadSheet(), m_FileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(
	  this, "Failed to save sequence to file:\n" + m_FileChooser.getSelectedFile());
  }

  /**
   * Views the specified sequence in a table.
   *
   * @param cont	the sequence to save
   */
  public void viewSequence(XYSequenceContainer cont) {
    XYSequence 		seq;
    SpreadSheetDialog	dialog;
    SpreadSheet		sheet;

    if (m_ViewDialogs == null)
      m_ViewDialogs = new ArrayList<>();

    seq = cont.getData();
    sheet = seq.toSpreadSheet();
    if (getParentDialog() != null)
      dialog = new SpreadSheetDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(getParentFrame(), false);
    m_ViewDialogs.add(dialog);
    dialog.setTitle("Sequence: " + cont.getDisplayID());
    if (sheet.getColumnCount() > 2)
      dialog.setSize(GUIHelper.getDefaultDialogDimension());
    else
      dialog.setSize(GUIHelper.rotate(GUIHelper.getDefaultSmallDialogDimension()));
    dialog.setLocationRelativeTo(this);
    dialog.setSpreadSheet(sheet);
    dialog.setShowSearch(true);
    dialog.setVisible(true);
  }

  /**
   * Processes the given tip text. Among the current mouse position, the
   * panel that initiated the call are also provided.
   *
   * @param panel	the content panel that initiated this call
   * @param mouse	the mouse position
   * @param tiptext	the tiptext so far
   * @return		the processed tiptext
   */
  @Override
  public String processTipText(PlotPanel panel, Point mouse, String tiptext) {
    String		result;
    MouseEvent		event;
    String		hit;
    HitDetector 	detector;

    result = tiptext;
    event  = new MouseEvent(
			getPlot().getContent(),
			MouseEvent.MOUSE_MOVED,
			new Date().getTime(),
			0,
			(int) mouse.getX(),
			(int) mouse.getY(),
			0,
			false);

    if (m_XYSequencePaintlet instanceof HitDetectorSupporter) {
      detector = ((HitDetectorSupporter) m_XYSequencePaintlet).getHitDetector();
      if (detector != null) {
	hit = (String) detector.detect(event);
	if (hit != null)
	  result += hit;
      }
    }

    return result;
  }

  /**
   * Returns true if storing the color in the report of container's data object
   * is supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsStoreColorInReport() {
    return false;
  }

  /**
   * Does nothing.
   *
   * @param indices	the indices of the containers of the container manager
   * @param name	the field name to use
   */
  @Override
  public void storeColorInReport(int[] indices, String name) {
  }

  /**
   * Returns true if storing a value in the report of container's data object
   * is supported.
   *
   * @return		true if supported
   */
  public boolean supportsStoreValueInReport() {
    return false;
  }

  /**
   * Stores the value in the report of container's data object.
   *
   * @param indices	the indices of the containers of the container manager
   * @param field	the field to use
   * @param value	the value to store
   */
  public void storeValueInReport(int[] indices, AbstractField field, Object value) {
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  @Override
  public AbstractScriptingEngine getScriptingEngine() {
    return ScriptingEngine.getSingleton(getDatabaseConnection());
  }

  /**
   * Sets whether the user can resize the plot (actually the parent dialog/frame)
   * via the popup menu.
   *
   * @param value	if true then user can resize the plot
   */
  public void setAllowResize(boolean value) {
    m_AllowResize = value;
  }

  /**
   * Returns whether the user can reize the plot (actually the parent dialog/frame)
   * via the popup menu.
   *
   * @return		true if the user can resize the plot
   */
  public boolean getAllowResize() {
    return m_AllowResize;
  }

  /**
   * Shows a dialog that allows the user to resize the plot.
   */
  public void showResizeDialog() {
    ApprovalDialog	dialog;
    ParameterPanel		paramPanel;
    JSpinner			spinnerWidth;
    JSpinner			spinnerHeight;

    if (getParentDialog() != null)
      dialog = ApprovalDialog.getDialog(getParentDialog());
    else
      dialog = ApprovalDialog.getDialog(getParentFrame());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Resizing plot");

    paramPanel = new ParameterPanel();
    dialog.getContentPane().add(paramPanel, BorderLayout.CENTER);

    spinnerWidth = new JSpinner();
    ((SpinnerNumberModel) spinnerWidth.getModel()).setMinimum(1);
    ((SpinnerNumberModel) spinnerWidth.getModel()).setStepSize(10);
    ((SpinnerNumberModel) spinnerWidth.getModel()).setValue(getPlot().getContent().getWidth());
    paramPanel.addParameter("_Width", spinnerWidth);

    spinnerHeight = new JSpinner();
    ((SpinnerNumberModel) spinnerHeight.getModel()).setMinimum(1);
    ((SpinnerNumberModel) spinnerHeight.getModel()).setStepSize(10);
    ((SpinnerNumberModel) spinnerHeight.getModel()).setValue(getPlot().getContent().getHeight());
    paramPanel.addParameter("_Height", spinnerHeight);

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      resizePlot(
	  new Dimension(
	      ((Number) spinnerWidth.getValue()).intValue(),
	      ((Number) spinnerHeight.getValue()).intValue()));
    }
  }

  /**
   * Resizes the plot (actually the parent dialog/frame) to the new dimensions.
   *
   * @param size	the new size
   */
  public void resizePlot(Dimension size) {
    Dimension	current;
    int		width;
    int		height;
    Dimension	newSize;

    // determine differences
    current = getPlot().getContent().getSize();
    width   = size.width  - current.width;
    height  = size.height - current.height;

    // determine current size
    current = null;
    if (getParentDialog() != null)
      current = getParentDialog().getSize();
    else if (getParentFrame() != null)
      current = getParentFrame().getSize();
    else if (getParentInternalFrame() != null)
      current = getParentInternalFrame().getSize();

    // update size
    if (current != null) {
      newSize = new Dimension(current.width + width, current.height + height);
      if (getParentDialog() != null)
	getParentDialog().setSize(newSize);
      else if (getParentFrame() != null)
	getParentFrame().setSize(newSize);
      else if (getParentInternalFrame() != null)
	getParentInternalFrame().setSize(newSize);
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_ExportDialog != null) {
      m_ExportDialog.dispose();
      m_ExportDialog = null;
    }
    if (m_ViewDialogs != null) {
      for (SpreadSheetDialog dialog: m_ViewDialogs)
	dialog.dispose();
      m_ViewDialogs.clear();
      m_ViewDialogs = null;
    }
  }
}
