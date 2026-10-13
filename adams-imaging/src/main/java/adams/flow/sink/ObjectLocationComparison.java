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
 * ObjectLocationComparison.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.sink.objectlocationcomparison.AbstractComparison;
import adams.flow.sink.objectlocationcomparison.AbstractComparisonPanel;
import adams.flow.sink.objectlocationcomparison.SideBySide;
import adams.flow.transformer.CompareObjectLocations;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BasePanel;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * For visualizing differences between object locations, annotations vs predictions.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.CompareObjectLocations
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: ObjectLocationComparison
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
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.Default
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * <pre>-show-flow-control-submenu &lt;boolean&gt; (property: showFlowControlSubMenu)
 * &nbsp;&nbsp;&nbsp;If enabled, adds a flow control sub-menu to the menubar.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-annotations-storage-name &lt;adams.flow.control.StorageName&gt; (property: annotationsStorageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item containing the annotations (ground truth).
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-annotations-prefix &lt;java.lang.String&gt; (property: annotationsPrefix)
 * &nbsp;&nbsp;&nbsp;The object prefix that the annotations use.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-annotations-label-suffix &lt;java.lang.String&gt; (property: annotationsLabelSuffix)
 * &nbsp;&nbsp;&nbsp;The report suffix that the annotations use for storing the label.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-predictions-storage-name &lt;adams.flow.control.StorageName&gt; (property: predictionsStorageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item containing the predictions.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-predictions-prefix &lt;java.lang.String&gt; (property: predictionsPrefix)
 * &nbsp;&nbsp;&nbsp;The object prefix that the predictions use.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-predictions-label-suffix &lt;java.lang.String&gt; (property: predictionsLabelSuffix)
 * &nbsp;&nbsp;&nbsp;The report suffix that the predictions use for storing the label.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-comparison &lt;adams.flow.sink.objectlocationcomparison.AbstractComparison&gt; (property: comparison)
 * &nbsp;&nbsp;&nbsp;The comparison view to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.objectlocationcomparison.SideBySide -annotations-overlay \"adams.gui.visualization.image.ObjectLocationsOverlayFromReport -type-color-provider adams.gui.visualization.core.DefaultColorProvider -shape-color-provider \\\"adams.gui.visualization.core.TranslucentColorProvider -provider adams.gui.visualization.core.DefaultColorProvider\\\"\" -predictions-overlay \"adams.gui.visualization.image.ObjectLocationsOverlayFromReport -type-color-provider adams.gui.visualization.core.DefaultColorProvider -shape-color-provider \\\"adams.gui.visualization.core.TranslucentColorProvider -provider adams.gui.visualization.core.DefaultColorProvider\\\"\"
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectLocationComparison
  extends AbstractGraphicalDisplay
  implements ClassCrossReference {

  private static final long serialVersionUID = 3793091139620311135L;

  /** the storage item with the annotations. */
  protected StorageName m_AnnotationsStorageName;

  /** the annotations object prefix. */
  protected String m_AnnotationsPrefix;

  /** the annotations label suffix. */
  protected String m_AnnotationsLabelSuffix;

  /** the storage item with the predictions. */
  protected StorageName m_PredictionsStorageName;

  /** the predictions object prefix. */
  protected String m_PredictionsPrefix;

  /** the predictions label suffix. */
  protected String m_PredictionsLabelSuffix;

  /** the comparison view to use. */
  protected AbstractComparison m_Comparison;

  /** the generated panel. */
  protected AbstractComparisonPanel m_ComparisonPanel;

  /** the current image. */
  protected transient AbstractImageContainer m_CurrentImage;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For visualizing differences between object locations, annotations vs predictions.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{CompareObjectLocations.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "annotations-storage-name", "annotationsStorageName",
      new StorageName());

    m_OptionManager.add(
      "annotations-prefix", "annotationsPrefix",
      LocatedObjects.DEFAULT_PREFIX);

    m_OptionManager.add(
      "annotations-label-suffix", "annotationsLabelSuffix",
      "type");

    m_OptionManager.add(
      "predictions-storage-name", "predictionsStorageName",
      new StorageName());

    m_OptionManager.add(
      "predictions-prefix", "predictionsPrefix",
      LocatedObjects.DEFAULT_PREFIX);

    m_OptionManager.add(
      "predictions-label-suffix", "predictionsLabelSuffix",
      "type");

    m_OptionManager.add(
      "comparison", "comparison",
      new SideBySide());
  }

  /**
   * Sets the storage item containing the annotations.
   *
   * @param value 	the name
   */
  public void setAnnotationsStorageName(StorageName value) {
    m_AnnotationsStorageName = value;
    reset();
  }

  /**
   * Returns the storage item containing the annotations.
   *
   * @return 		the name
   */
  public StorageName getAnnotationsStorageName() {
    return m_AnnotationsStorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsStorageNameTipText() {
    return "The name of the storage item containing the annotations (ground truth).";
  }

  /**
   * Sets the object prefix to use for the annotations.
   *
   * @param value 	the object prefix
   */
  public void setAnnotationsPrefix(String value) {
    m_AnnotationsPrefix = value;
    reset();
  }

  /**
   * Returns the object prefix to use for the annotations.
   *
   * @return 		the object prefix
   */
  public String getAnnotationsPrefix() {
    return m_AnnotationsPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsPrefixTipText() {
    return "The object prefix that the annotations use.";
  }

  /**
   * Sets the report suffix that the annotations use for storing the label.
   *
   * @param value 	the suffix
   */
  public void setAnnotationsLabelSuffix(String value) {
    m_AnnotationsLabelSuffix = value;
    reset();
  }

  /**
   * Returns the report suffix that the annotations use for storing the label.
   *
   * @return 		the suffix
   */
  public String getAnnotationsLabelSuffix() {
    return m_AnnotationsLabelSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsLabelSuffixTipText() {
    return "The report suffix that the annotations use for storing the label.";
  }

  /**
   * Sets the storage item containing the predictions.
   *
   * @param value 	the name
   */
  public void setPredictionsStorageName(StorageName value) {
    m_PredictionsStorageName = value;
    reset();
  }

  /**
   * Returns the storage item containing the predictions.
   *
   * @return 		the name
   */
  public StorageName getPredictionsStorageName() {
    return m_PredictionsStorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsStorageNameTipText() {
    return "The name of the storage item containing the predictions.";
  }

  /**
   * Sets the object prefix to use for the predictions.
   *
   * @param value 	the object prefix
   */
  public void setPredictionsPrefix(String value) {
    m_PredictionsPrefix = value;
    reset();
  }

  /**
   * Returns the object prefix to use for the predictions.
   *
   * @return 		the object prefix
   */
  public String getPredictionsPrefix() {
    return m_PredictionsPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsPrefixTipText() {
    return "The object prefix that the predictions use.";
  }

  /**
   * Sets the report suffix that the predictions use for storing the label.
   *
   * @param value 	the suffix
   */
  public void setPredictionsLabelSuffix(String value) {
    m_PredictionsLabelSuffix = value;
    reset();
  }

  /**
   * Returns the report suffix that the predictions use for storing the label.
   *
   * @return 		the suffix
   */
  public String getPredictionsLabelSuffix() {
    return m_PredictionsLabelSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsLabelSuffixTipText() {
    return "The report suffix that the predictions use for storing the label.";
  }

  /**
   * Sets the comparison view to use.
   *
   * @param value 	the comparison
   */
  public void setComparison(AbstractComparison value) {
    m_Comparison = value;
    reset();
  }

  /**
   * Returns the comparison view to use.
   *
   * @return 		the comparison
   */
  public AbstractComparison getComparison() {
    return m_Comparison;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String comparisonTipText() {
    return "The comparison view to use.";
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
    result += QuickInfoHelper.toString(this, "annotationsStoragName", m_AnnotationsStorageName, ", annotations: ");
    result += QuickInfoHelper.toString(this, "predictionsStorageName", m_PredictionsStorageName, ", predictions: ");
    result += QuickInfoHelper.toString(this, "comparison", m_Comparison, ", comparison: ");

    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_ComparisonPanel != null)
      m_ComparisonPanel.clearPanel();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_ComparisonPanel = m_Comparison.generate(this);
    return m_ComparisonPanel;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token the token to display
   */
  @Override
  protected void display(Token token) {
    Report annRep;
    LocatedObjects 		annObj;
    Report 			predRep;
    LocatedObjects 		predObj;
    Set<String> labels;
    List<String> labelsSorted;

    // annotations
    if (!getStorageHandler().getStorage().has(m_AnnotationsStorageName)) {
      getLogger().severe("Annotations not available from storage: " + m_AnnotationsStorageName);
      return;
    }
    annRep = (Report) getStorageHandler().getStorage().get(m_AnnotationsStorageName);
    annObj = LocatedObjects.fromReport(annRep, m_AnnotationsPrefix);

    // predictions
    if (!getStorageHandler().getStorage().has(m_PredictionsStorageName)) {
      getLogger().severe("Predictions not available from storage: " + m_PredictionsStorageName);
      return;
    }
    predRep = (Report) getStorageHandler().getStorage().get(m_PredictionsStorageName);
    predObj = LocatedObjects.fromReport(predRep, m_PredictionsPrefix);

    // determine labels
    labels = new HashSet<>();
    for (LocatedObject obj: annObj) {
      if (obj.getMetaData().containsKey(m_AnnotationsLabelSuffix))
	labels.add("" + obj.getMetaData().get(m_AnnotationsLabelSuffix));
    }
    for (LocatedObject obj: predObj) {
      if (obj.getMetaData().containsKey(m_PredictionsLabelSuffix))
	labels.add("" + obj.getMetaData().get(m_PredictionsLabelSuffix));
    }
    labelsSorted = new ArrayList<>(labels);
    Collections.sort(labelsSorted);

    // update GUI
    m_CurrentImage = token.getPayload(AbstractImageContainer.class);
    m_ComparisonPanel.display(m_CurrentImage, labelsSorted, annRep, annObj, predRep, predObj);
  }

  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  @Override
  public JComponent supplyComponent() {
    if (m_ComparisonPanel != null)
      return m_ComparisonPanel.supplyComponent();
    else
      return null;
  }
}
