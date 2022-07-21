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
 * ImageObjectOverlapMatrix.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Generates a matrix of overlapping image objects (annotations vs predictions) and their labels.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageObjectOverlapMatrix
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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item to merge with (Report or ReportHandler).
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder for locating the objects of interest.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-algorithm &lt;adams.data.objectoverlap.ObjectOverlap&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The algorithm to use for determining the overlapping objects.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectoverlap.AreaRatio
 * </pre>
 *
 * <pre>-matrix-output &lt;ALL_MATCHES|ONLY_HIGHEST_LABEL&gt; (property: matrixOutput)
 * &nbsp;&nbsp;&nbsp;What matches to store in the matrix.
 * &nbsp;&nbsp;&nbsp;default: ALL_MATCHES
 * </pre>
 *
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The meta-data key that holds the label.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-meta-data-highest-label &lt;java.lang.String&gt; (property: metaDataHighestLabel)
 * &nbsp;&nbsp;&nbsp;The meta-data key that stores the highest label.
 * &nbsp;&nbsp;&nbsp;default: overlap_label_highest
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectOverlapMatrix
    extends AbstractTransformer {

  private static final long serialVersionUID = 8201765866576306524L;

  /**
   * What to output in the matrix.
   */
  public enum MatrixOutput {
    ALL_MATCHES,
    ONLY_HIGHEST_LABEL,
  }

  /** the storage item. */
  protected StorageName m_StorageName;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the image overlap calculation to use. */
  protected ObjectOverlap m_Algorithm;

  /** what to output in the matrix. */
  protected MatrixOutput m_MatrixOutput;

  /** the label meta-data key. */
  protected String m_LabelKey;

  /** the metadata key storing the label of the highest match. */
  protected String m_MetaDataHighestLabel;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a matrix of overlapping image objects (annotations vs predictions) and their labels.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"storage-name", "storageName",
	new StorageName());

    m_OptionManager.add(
	"finder", "finder",
	new AllFinder());

    m_OptionManager.add(
	"algorithm", "algorithm",
	new AreaRatio());

    m_OptionManager.add(
	"matrix-output", "matrixOutput",
	MatrixOutput.ALL_MATCHES);

    m_OptionManager.add(
	"label-key", "labelKey",
	"type");

    m_OptionManager.add(
	"meta-data-highest-label", "metaDataHighestLabel",
	AreaRatio.OVERLAP_LABEL_HIGHEST);
  }

  /**
   * Sets the name of the storage item to merge with (Report or ReportHandler).
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage item to merge with (Report or ReportHandler).
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage item to merge with (Report or ReportHandler).";
  }

  /**
   * Sets the object finder for locating the objects.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns object finder for locating the objects.
   *
   * @return 		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder for locating the objects of interest.";
  }

  /**
   * Sets the algorithm for determining the overlapping objects.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(ObjectOverlap value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public ObjectOverlap getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The algorithm to use for determining the overlapping objects.";
  }

  /**
   * Sets what type of output to store in the matrix.
   *
   * @param value 	the output
   */
  public void setMatrixOutput(MatrixOutput value) {
    m_MatrixOutput = value;
    reset();
  }

  /**
   * Returns what type of output to store in the matrix.
   *
   * @return 		the output
   */
  public MatrixOutput getMatrixOutput() {
    return m_MatrixOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String matrixOutputTipText() {
    return "What matches to store in the matrix.";
  }

  /**
   * Sets the meta-data key that stores the label.
   *
   * @param value	the key
   */
  public void setLabelKey(String value) {
    m_LabelKey = value;
    reset();
  }

  /**
   * Returns the meta-data key that stores the label.
   *
   * @return		the key
   */
  public String getLabelKey() {
    return m_LabelKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelKeyTipText() {
    return "The meta-data key that holds the label.";
  }

  /**
   * Sets the key in the meta-data storing the label of the highest match.
   *
   * @param value 	the key
   */
  public void setMetaDataHighestLabel(String value) {
    m_MetaDataHighestLabel = value;
    reset();
  }

  /**
   * Returns the key in the meta-data storing the label of the highest match.
   *
   * @return 		the key
   */
  public String getMetaDataHighestLabel() {
    return m_MetaDataHighestLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataHighestLabelTipText() {
    return "The meta-data key that stores the highest label.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "finder", m_Finder, ", finder: ");
    result += QuickInfoHelper.toString(this, "algorithm", m_Algorithm, ", algorithm: ");
    result += QuickInfoHelper.toString(this, "metaDataHighestLabel", m_MetaDataHighestLabel, ", highest label: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if ((m_MatrixOutput == MatrixOutput.ONLY_HIGHEST_LABEL) && m_MetaDataHighestLabel.trim().isEmpty())
	result = "No meta-data key specified that holds the highest label";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String					result;
    Report					thisReport;
    Report					otherReport;
    LocatedObjects				thisObjs;
    LocatedObjects				otherObjs;
    Map<LocatedObject, Set<LocatedObject>>	matches;
    LocatedObjects				overlaps;
    SpreadSheet					sheet;
    Row						row;
    Set<LocatedObject>				hits;
    double					highestValue;
    String					highestLabel;

    result = null;

    thisReport = null;
    if (m_InputToken.hasPayload(AbstractImageContainer.class))
      thisReport = m_InputToken.getPayload(AbstractImageContainer.class).getReport();
    else if (m_InputToken.hasPayload(ReportHandler.class))
      thisReport = m_InputToken.getPayload(ReportHandler.class).getReport();
    else if (m_InputToken.hasPayload(Report.class))
      thisReport = m_InputToken.getPayload(Report.class);
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      if (!getStorageHandler().getStorage().has(m_StorageName))
	result = "Report not found in storage: " + m_StorageName;
    }

    if (result == null) {
      otherReport = (Report) getStorageHandler().getStorage().get(m_StorageName);
      thisObjs    = m_Finder.findObjects(thisReport);
      otherObjs   = m_Finder.findObjects(otherReport);
      sheet       = new DefaultSpreadSheet();
      row         = sheet.getHeaderRow();
      row.addCell("A").setContentAsString("Actual");
      row.addCell("P").setContentAsString("Predicted");

      switch (m_MatrixOutput) {
	case ALL_MATCHES:
	  matches = m_Algorithm.matches(thisObjs, otherObjs);
	  for (LocatedObject thisObj: matches.keySet()) {
	    hits = matches.get(thisObj);
	    for (LocatedObject otherObj : hits) {
	      row = sheet.addRow();
	      row.addCell("A").setContentAsString("" + thisObj.getMetaData().get(m_LabelKey));
	      row.addCell("P").setContentAsString("" + otherObj.getMetaData().get(m_LabelKey));
	    }
	  }
	  break;

	case ONLY_HIGHEST_LABEL:
	  overlaps    = m_Algorithm.calculate(thisObjs, otherObjs);
	  for (LocatedObject thisObj: overlaps) {
	    row = sheet.addRow();
	    row.addCell("A").setContentAsString("" + thisObj.getMetaData().get(m_LabelKey));
	    row.addCell("P").setContentAsString("" + thisObj.getMetaData().get(m_MetaDataHighestLabel));
	  }
	  break;

	default:
	  throw new IllegalStateException("Unhandled matrix output: " + m_MatrixOutput);
      }
      m_OutputToken = new Token(sheet);
    }

    return result;
  }
}
