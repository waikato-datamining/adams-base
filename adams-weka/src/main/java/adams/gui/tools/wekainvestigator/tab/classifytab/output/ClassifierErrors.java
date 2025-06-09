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
 * ClassifierErrors.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.AutoOnOff;
import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;
import adams.flow.sink.ActualVsPredictedPlot;
import adams.flow.sink.ActualVsPredictedPlot.LimitType;
import adams.flow.sink.sequenceplotter.OutlierPaintlet;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MultiPagePane;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.tools.wekainvestigator.datatable.DataTable;
import adams.gui.tools.wekainvestigator.datatable.DataTableModel;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.ClassifyTab;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.classifiererrors.RemoveOutliersClickAction;
import adams.gui.visualization.sequence.MetaDataValuePaintlet;
import adams.gui.visualization.sequence.StraightLineOverlayPaintlet;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import adams.gui.visualization.sequence.metadatacolor.AbstractMetaDataColor;
import adams.gui.visualization.sequence.metadatacolor.Dummy;
import com.github.fracpete.javautils.enumerate.Enumerated;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Generates classifier errors plot.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClassifierErrors
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the maximum number of data points before turning off anti-aliasing. */
  public final static int MAX_DATA_POINTS = 1000;

  public static final String META_DATA_INSTANCE_INDEX = "MetaData-Instance Index";

  /** the limit type. */
  protected LimitType m_Limit;

  /** the minimum to use for the actual values (neg inf = no restriction). */
  protected double m_ActualMin;

  /** the maximum to use for the actual values (pos inf = no restriction). */
  protected double m_ActualMax;

  /** the minimum to use for the predicted values (neg inf = no restriction). */
  protected double m_PredictedMin;

  /** the maximum to use for the predicted values (pos inf = no restriction). */
  protected double m_PredictedMax;

  /** whether to swap the axes. */
  protected boolean m_SwapAxes;

  /** the diameter of the cross. */
  protected int m_Diameter;

  /** whether to use the error for the cross-size. */
  protected boolean m_UseError;

  /** whether anti-aliasing is enabled. */
  protected AutoOnOff m_AntiAliasingEnabled;

  /** for obtaining the color from the meta-data. */
  protected AbstractMetaDataColor m_MetaDataColor;

  /** whether to use a custom paintlet. */
  protected boolean m_UseCustomPaintlet;

  /** the custom paintlet. */
  protected XYSequencePaintlet m_CustomPaintlet;

  /** the overlays to use. */
  protected XYSequencePaintlet[] m_Overlays;

  /** the ID of the last dataset selected. */
  protected int m_LastID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates classifier errors plot.\n"
	     + "\n"
	     + "Mouse interaction:"
	     + "Use CTRL+left-click to toggle individual points.\n"
	     + "Use SHIFT+left-click to add polygon vertices and CTRL+SHIFT+left-click to finalize the "
	     + "polygon and toggle points enclosed by the polygon. CTRL+right-click discards the polygon vertices.\n"
	     + "SHIFT+right-clicks displays the outliers scheduled for removal.\n"
	     + "CTRL+SHIFT+right-click clears all outlier flags.\n"
	     + "\n"
	     + "CAUTION:"
	     + "The removal works solely by row index and will only work correctly with results from "
	     + "cross-validations and explicit test sets that have been loaded into the Investigator."
	     + "\n"
	     + "NB: using a custom paintlet disables the ability to toggle and remove outliers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actual-min", "actualMin",
      Double.NEGATIVE_INFINITY);

    m_OptionManager.add(
      "actual-max", "actualMax",
      Double.POSITIVE_INFINITY);

    m_OptionManager.add(
      "predicted-min", "predictedMin",
      Double.NEGATIVE_INFINITY);

    m_OptionManager.add(
      "predicted-max", "predictedMax",
      Double.POSITIVE_INFINITY);

    m_OptionManager.add(
      "swap-axes", "swapAxes",
      false);

    m_OptionManager.add(
      "limit", "limit",
      LimitType.NONE);

    m_OptionManager.add(
      "diameter", "diameter",
      7, 1, null);

    m_OptionManager.add(
      "use-error", "useError",
      false);

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      AutoOnOff.AUTO);

    m_OptionManager.add(
      "meta-data-color", "metaDataColor",
      new Dummy());

    m_OptionManager.add(
      "use-custom-paintlet", "useCustomPaintlet",
      false);

    m_OptionManager.add(
      "custom-paintlet", "customPaintlet",
      new MetaDataValuePaintlet());

    m_OptionManager.add(
      "overlay", "overlays",
      new XYSequencePaintlet[]{new StraightLineOverlayPaintlet()});
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LastID = -1;
  }

  /**
   * Sets the lower limit in use for the actual values.
   *
   * @param value	the limit, neg inf if unlimited
   */
  public void setActualMin(double value) {
    m_ActualMin = value;
    reset();
  }

  /**
   * Returns the lower limit in use for the actual values.
   *
   * @return		the limit, neg inf if unlimited
   */
  public double getActualMin() {
    return m_ActualMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualMinTipText() {
    return "The minimum to use for the display of the actual axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the upper limit in use for the actual values.
   *
   * @param value	the limit, pos inf if unlimited
   */
  public void setActualMax(double value) {
    m_ActualMax = value;
    reset();
  }

  /**
   * Returns the upper limit in use for the actual values.
   *
   * @return		the limit, pos inf if unlimited
   */
  public double getActualMax() {
    return m_ActualMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualMaxTipText() {
    return "The maximum to use for the display of the actual axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the lower limit in use for the predicted values.
   *
   * @param value	the limit, neg inf if unlimited
   */
  public void setPredictedMin(double value) {
    m_PredictedMin = value;
    reset();
  }

  /**
   * Returns the lower limit in use for the predicted values.
   *
   * @return		the limit, neg inf if unlimited
   */
  public double getPredictedMin() {
    return m_PredictedMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedMinTipText() {
    return "The minimum to use for the display of the predicted axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the upper limit in use for the predicted values.
   *
   * @param value	the limit, pos inf if unlimited
   */
  public void setPredictedMax(double value) {
    m_PredictedMax = value;
    reset();
  }

  /**
   * Returns the upper limit in use for the predicted values.
   *
   * @return		the limit, pos inf if unlimited
   */
  public double getPredictedMax() {
    return m_PredictedMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedMaxTipText() {
    return "The maximum to use for the display of the predicted axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets whether to swap the axes.
   *
   * @param value	true if to swap
   */
  public void setSwapAxes(boolean value) {
    m_SwapAxes = value;
    reset();
  }

  /**
   * Returns whether to swap the axes.
   *
   * @return		true if to swap
   */
  public boolean getSwapAxes() {
    return m_SwapAxes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String swapAxesTipText() {
    return "If enabled, the axes get swapped.";
  }

  /**
   * Sets the limit to impose on the axes.
   *
   * @param value	the limit type
   */
  public void setLimit(LimitType value) {
    m_Limit = value;
    reset();
  }

  /**
   * Returns the limit to impose on the axes.
   *
   * @return		the limit type
   */
  public LimitType getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return
      "The type of limit to impose on the axes; NONE just uses the range "
	+ "determined from the data; ACTUAL uses the min/max from the actual "
	+ "column for both axes; SPECIFIED uses the specified limits or, if "
	+ "a value is 'infinity' then the corresponding value from the "
	+ "determine range.";
  }

  /**
   * Sets the cross diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    if (getOptionManager().isValid("diameter", value)) {
      m_Diameter = value;
      reset();
    }
  }

  /**
   * Returns the diameter of the cross.
   *
   * @return		the diameter
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the cross in pixels (if no error data supplied).";
  }

  /**
   * Sets whether to use the numeric error for the cross size.
   *
   * @param value	true if to use error
   */
  public void setUseError(boolean value) {
    m_UseError = value;
    reset();
  }

  /**
   * Returns whether to use the error for the cross size.
   *
   * @return		true if to use error
   */
  public boolean getUseError() {
    return m_UseError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useErrorTipText() {
    return "If enabled, the numeric error is used for the cross size.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Errors";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if anti-aliasing is used
   */
  public void setAntiAliasingEnabled(AutoOnOff value) {
    m_AntiAliasingEnabled = value;
    reset();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		if anti-aliasing is used
   */
  public AutoOnOff isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "In auto mode, anti-aliasing is turned off if more than " + MAX_DATA_POINTS + " present or the system wide setting has been turned off.";
  }

  /**
   * Sets the scheme for extracting the color from the meta-data.
   *
   * @param value	the scheme
   */
  public void setMetaDataColor(AbstractMetaDataColor value) {
    m_MetaDataColor = value;
    reset();
  }

  /**
   * Returns the scheme for extracting the color from the meta-data.
   *
   * @return		the scheme
   */
  public AbstractMetaDataColor getMetaDataColor() {
    return m_MetaDataColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataColorTipText() {
    return "The scheme to use for extracting the color from the meta-data; ignored if " + Dummy.class.getName() + ".";
  }

  /**
   * Sets whether to use the custom paintlet.
   *
   * @param value	true if custom
   */
  public void setUseCustomPaintlet(boolean value) {
    m_UseCustomPaintlet = value;
    reset();
  }

  /**
   * Returns whether to use the custom paintlet.
   *
   * @return		true if custom
   */
  public boolean getUseCustomPaintlet() {
    return m_UseCustomPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomPaintletTipText() {
    return "If enabled, the custom paintlet is used instead of cross/error paintlet, anti-aliasing and meta-data color scheme.";
  }

  /**
   * Sets the custom paintlet.
   *
   * @param value	the paintlet
   */
  public void setCustomPaintlet(XYSequencePaintlet value) {
    m_CustomPaintlet = value;
    reset();
  }

  /**
   * Returns the custom paintlet.
   *
   * @return		the paintlet
   */
  public XYSequencePaintlet getCustomPaintlet() {
    return m_CustomPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customPaintletTipText() {
    return "The custom paintlet to use instead of cross/error paintlet, anti-aliasing and meta-data color scheme.";
  }

  /**
   * Sets the overlays to use in the plot.
   *
   * @param value	the overlays
   */
  public void setOverlays(XYSequencePaintlet[] value) {
    m_Overlays = value;
    reset();
  }

  /**
   * Returns the overlays to use in the plot.
   *
   * @return		the overlays
   */
  public XYSequencePaintlet[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlaysTipText() {
    return "The overlays to use in the plot.";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation()
	     && (item.getEvaluation().predictions() != null)
	     && (item.getEvaluation().getHeader() != null)
	     && (item.getEvaluation().getHeader().classAttribute().isNumeric());
  }

  /**
   * Determines the last selected row.
   *
   * @param tab		the classify tab to obtain the data from
   * @return		the last selected row (0 by default)
   */
  protected int determineLastSelectedRow(ClassifyTab tab) {
    int		d;

    if (m_LastID > -1) {
      for (d = 0; d < tab.getData().size(); d++) {
	if (tab.getData().get(d).getID() == m_LastID)
	  return d;
      }
    }

    return 0;
  }

  /**
   * Removes the instances from the current dataset.
   *
   * @param data	the data points to remove
   */
  protected void removeData(ResultItem item, SpreadSheet data) {
    int			colIndex;
    ClassifyTab		tab;
    ApprovalDialog	dialog;
    DataTableModel	model;
    DataTable		table;
    BaseCheckBox	checkCopy;
    JPanel		panel;
    DataContainer	cont;
    DataContainer	contNew;
    Instances		inst;
    int			index;
    TIntList		indices;
    int 		selRow;

    colIndex = data.getHeaderRow().indexOfContent(META_DATA_INSTANCE_INDEX);
    if (colIndex == -1) {
      GUIHelper.showErrorMessage(null, "Failed to locate column: " + META_DATA_INSTANCE_INDEX);
      return;
    }

    tab = (ClassifyTab) GUIHelper.getParent(item.getTabbedPane(), ClassifyTab.class);
    if (tab == null) {
      GUIHelper.showErrorMessage(null, "Failed to get classify tab!");
      return;
    }

    // let user select dataset to remove the data points from
    if (GUIHelper.getParentDialog(tab) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(tab), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(tab), true);
    dialog.setTitle("Select dataset to update");
    model = new DataTableModel(tab.getData(), true);
    table = new DataTable(model);
    table.setAutoResizeMode(DataTable.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setSelectedRow(determineLastSelectedRow(tab));
    checkCopy = new BaseCheckBox("Create copy of dataset first before removing rows");
    panel = new JPanel(new BorderLayout(5, 5));
    panel.add(new BaseScrollPane(table), BorderLayout.CENTER);
    panel.add(checkCopy, BorderLayout.SOUTH);
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(tab);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;
    selRow = table.getSelectedRow();
    if (selRow < 0)
      return;

    // remove data
    cont = tab.getData().get(selRow);
    if (checkCopy.isSelected()) {
      contNew = new MemoryContainer(new Instances(cont.getData()));
      contNew.getData().setRelationName("Copy of " + cont.getData().relationName());
      tab.getData().add(contNew);
      cont = contNew;
    }
    m_LastID = cont.getID();
    cont.addUndoPoint("Classifier errors: remove rows");
    inst    = cont.getData();
    indices = new TIntArrayList();
    for (Row row: data.rows()) {
      index = row.getCell(colIndex).toLong().intValue() - 1;
      if (index >= 0)
	indices.add(index);
    }
    indices.sort();
    indices.reverse();
    for (int i: indices.toArray())
      inst.remove(i);
    cont.setModified(true);
    tab.getOwner().fireDataChange(new WekaInvestigatorDataEvent(tab.getOwner()));
  }

  /**
   * Generates a plot actor from the evaluation.
   *
   * @param eval		the evaluation to use
   * @param originalIndices 	the original indices, can be null
   * @param additionalAttributes 	the additional attribute to use, can be null
   * @param errors 		for collecting errors
   * @return			the generated panel, null if failed to generate
   */
  protected ComponentContentPanel createOutput(ResultItem item, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, MessageCollection errors) {
    ActualVsPredictedPlot 		sink;
    boolean				showError;
    Token				token;
    SpreadSheet				sheet;
    List<String>			additional;
    int					i;
    JPanel 				panel;
    RemoveOutliersClickAction 		action;

    showError = m_UseError && eval.getHeader().classAttribute().isNumeric();
    sheet     = PredictionHelper.toSpreadSheet(this, errors, eval, originalIndices, additionalAttributes, showError);
    if (sheet == null) {
      if (errors.isEmpty())
	errors.add("Failed to generate prediction!");
      return null;
    }
    token = new Token(sheet);

    sink  = new ActualVsPredictedPlot();
    sink.setActualMin(m_ActualMin);
    sink.setActualMax(m_ActualMax);
    sink.setPredictedMin(m_PredictedMin);
    sink.setPredictedMax(m_PredictedMax);
    sink.setSwapAxes(m_SwapAxes);
    sink.setLimit(m_Limit);
    sink.setDiameter(m_Diameter);
    sink.setShowSidePanel(false);
    sink.setAdditional(new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
    sink.setMetaDataColor(ObjectCopyHelper.copyObject(m_MetaDataColor));
    sink.setUseCustomPaintlet(true);
    if (m_UseCustomPaintlet)
      sink.setCustomPaintlet(ObjectCopyHelper.copyObject(m_CustomPaintlet));
    else
      sink.setCustomPaintlet(new OutlierPaintlet());
    sink.setOverlays(ObjectCopyHelper.copyObjects(m_Overlays));
    action = new RemoveOutliersClickAction();
    action.setRemoveDataListener((SpreadSheet data) -> removeData(item, data));
    sink.setMouseClickAction(action);
    switch (m_AntiAliasingEnabled) {
      case AUTO:
	sink.setAntiAliasingEnabled(
	  ((SpreadSheet) token.getPayload()).getRowCount() <= MAX_DATA_POINTS
	    && GUIHelper.AntiAliasingEnabled);
	break;
      case ON:
	sink.setAntiAliasingEnabled(true);
	break;
      case OFF:
	sink.setAntiAliasingEnabled(false);
	break;
    }
    if (showError)
      sink.setError(new SpreadSheetColumnIndex("Error"));
    additional = null;
    if (additionalAttributes != null) {
      additional = new ArrayList<>();
      for (i = 0; i < additionalAttributes.getColumnCount(); i++)
	additional.add(SpreadSheetColumnRange.escapeName(additionalAttributes.getColumnName(i)));
    }
    if ((additional != null) && !additional.isEmpty())
      sink.setAdditional(new SpreadSheetColumnRange(Utils.flatten(additional, ",")));

    panel = sink.createDisplayPanel(token);

    return new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane());
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    MultiPagePane		multiPage;

    if (item.hasFoldEvaluations()) {
      multiPage = newMultiPagePane(item);
      addPage(multiPage, "Full", createOutput(item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors), 0);
      for (Enumerated<Evaluation> eval: enumerate(item.getFoldEvaluations()))
	addPage(multiPage, "Fold " + (eval.index + 1), createOutput(item, item.getFoldEvaluations()[eval.index], null, null, errors), eval.index + 1);
      if (multiPage.getPageCount() > 0)
	multiPage.setSelectedIndex(0);
      return multiPage;
    }
    else {
      return createOutput(item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors);
    }
  }
}
