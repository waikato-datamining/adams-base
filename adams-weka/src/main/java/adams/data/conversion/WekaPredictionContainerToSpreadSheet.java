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
 * WekaPredictionContainerToSpreadSheet.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.core.Instance;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.WekaPredictionContainer;

/**
 <!-- globalinfo-start -->
 * Turns a WEKA prediction container into a SpreadSheet object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-title-name-column &lt;java.lang.String&gt; (property: titleNameColumn)
 * &nbsp;&nbsp;&nbsp;The title of the first column.
 * &nbsp;&nbsp;&nbsp;default: Name
 * </pre>
 *
 * <pre>-title-value-column &lt;java.lang.String&gt; (property: titleValueColumn)
 * &nbsp;&nbsp;&nbsp;The title of the second column.
 * &nbsp;&nbsp;&nbsp;default: Value
 * </pre>
 *
 * <pre>-add-classification (property: addClassification)
 * &nbsp;&nbsp;&nbsp;If enabled, then the numeric classification (index of class label for nominal
 * &nbsp;&nbsp;&nbsp;classes) is added to the spreadsheet.
 * </pre>
 *
 * <pre>-classification-entry &lt;java.lang.String&gt; (property: classificationEntry)
 * &nbsp;&nbsp;&nbsp;The value to use in the 'Name' column for the numeric classification.
 * &nbsp;&nbsp;&nbsp;default: Classification
 * </pre>
 *
 * <pre>-add-classification-label (property: addClassificationLabel)
 * &nbsp;&nbsp;&nbsp;If enabled, then the classification label (only for nominal classes) is
 * &nbsp;&nbsp;&nbsp;added to the spreadsheet.
 * </pre>
 *
 * <pre>-classification-label-entry &lt;java.lang.String&gt; (property: classificationLabelEntry)
 * &nbsp;&nbsp;&nbsp;The value to use in the 'Name' column for the classification label.
 * &nbsp;&nbsp;&nbsp;default: Class
 * </pre>
 *
 * <pre>-add-distribution (property: addDistribution)
 * &nbsp;&nbsp;&nbsp;If enabled, then the class distribution (only for nominal classes) is added
 * &nbsp;&nbsp;&nbsp;to the spreadsheet.
 * </pre>
 *
 * <pre>-distribution-format &lt;java.lang.String&gt; (property: distributionFormat)
 * &nbsp;&nbsp;&nbsp;The format to use in the 'Name' column for the class distribution; '{INDEX
 * &nbsp;&nbsp;&nbsp;}' can be used for the 1-based index, '{LABEL}' can be used for the class
 * &nbsp;&nbsp;&nbsp;label (if available; if not, the 1-based index is used instead)
 * &nbsp;&nbsp;&nbsp;default: {LABEL}
 * </pre>
 *
 * <pre>-distribution-sorting &lt;NONE|DESCENDING|ASCENDING&gt; (property: distributionSorting)
 * &nbsp;&nbsp;&nbsp;The type of sorting to apply to the distribution array.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPredictionContainerToSpreadSheet
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 124581970397295630L;

  /**
   * How to sort the distribution.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Sorting {
    /** no sorting. */
    NONE,
    /** descending. */
    DESCENDING,
    /** ascending. */
    ASCENDING
  }

  /**
   * Helper class for sorting the distribution.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SortContainer
    implements Comparable<SortContainer> {

    /** the distribution index (0-based). */
    protected int m_Index;

    /** the probability. */
    protected double m_Probability;

    /**
     * Initializes the container.
     *
     * @param index		the 0-based index in the distribution array
     * @param probability 	the probability in the distribution
     */
    public SortContainer(int index, double probability) {
      super();

      m_Index       = index;
      m_Probability = probability;
    }

    /**
     * Returns the index.
     *
     * @return		the index
     */
    public int getIndex() {
      return m_Index;
    }

    /**
     * Returns the probability.
     *
     * @return		the probability
     */
    public double getProbability() {
      return m_Probability;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    public int compareTo(SortContainer o) {
      return new Double(getProbability()).compareTo(new Double(o.getProbability()));
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof SortContainer)
	return (compareTo((SortContainer) obj) == 0);
      else
	return false;
    }

    /**
     * Returns a short string description of the container.
     *
     * @return		the description
     */
    @Override
    public String toString() {
      return "index=" + m_Index + ", prob=" + m_Probability;
    }
  }

  /** the placeholder for the class label in the distribution format. */
  public final static String PLACEHOLDER_LABEL = "{LABEL}";

  /** the placeholder for the class index in the distribution format. */
  public final static String PLACEHOLDER_INDEX = "{INDEX}";

  /** the name column in the spreadsheet. */
  public final static String COLUMN_NAME = "Name";

  /** the value column in the spreadsheet. */
  public final static String COLUMN_VALUE = "Value";

  /** the title of the name column. */
  protected String m_TitleNameColumn;

  /** the title of the value column. */
  protected String m_TitleValueColumn;

  /** whether to add the numeric classification to the output. */
  protected boolean m_AddClassification;

  /** the entry to use for the numeric classification in the spreadsheet. */
  protected String m_ClassificationEntry;

  /** whether to add the classification label to the output (nominal classes only). */
  protected boolean m_AddClassificationLabel;

  /** the entry to use for the classification label in the spreadsheet. */
  protected String m_ClassificationLabelEntry;

  /** whether to add the distribution to the output (nominal classes only). */
  protected boolean m_AddDistribution;

  /** the format to use for the distribution entries in the spreadsheet. */
  protected String m_DistributionFormat;

  /** the type of sorting to apply to the distribution array (if applicable). */
  protected Sorting m_DistributionSorting;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a WEKA prediction container into a SpreadSheet object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "title-name-column", "titleNameColumn",
	    COLUMN_NAME);

    m_OptionManager.add(
	    "title-value-column", "titleValueColumn",
	    COLUMN_VALUE);

    m_OptionManager.add(
	    "add-classification", "addClassification",
	    false);

    m_OptionManager.add(
	    "classification-entry", "classificationEntry",
	    "Classification");

    m_OptionManager.add(
	    "add-classification-label", "addClassificationLabel",
	    false);

    m_OptionManager.add(
	    "classification-label-entry", "classificationLabelEntry",
	    "Class");

    m_OptionManager.add(
	    "add-distribution", "addDistribution",
	    false);

    m_OptionManager.add(
	    "distribution-format", "distributionFormat",
	    PLACEHOLDER_LABEL);

    m_OptionManager.add(
	    "distribution-sorting", "distributionSorting",
    	    Sorting.NONE);
  }

  /**
   * Sets the title of the "Name" column, i.e., the first column.
   *
   * @param value	the title
   */
  public void setTitleNameColumn(String value) {
    m_TitleNameColumn = value;
    reset();
  }

  /**
   * Returns the title of the "Name" column, i.e., the first column.
   *
   * @return		the title
   */
  public String getTitleNameColumn() {
    return m_TitleNameColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleNameColumnTipText() {
    return "The title of the first column.";
  }

  /**
   * Sets the title of the "Value" column, i.e., the first column.
   *
   * @param value	the title
   */
  public void setTitleValueColumn(String value) {
    m_TitleValueColumn = value;
    reset();
  }

  /**
   * Returns the title of the "Value" column, i.e., the first column.
   *
   * @return		the title
   */
  public String getTitleValueColumn() {
    return m_TitleValueColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleValueColumnTipText() {
    return "The title of the second column.";
  }

  /**
   * Sets whether to add the numeric classification (label index for nominal classes).
   *
   * @param value	true if to add numeric classification
   */
  public void setAddClassification(boolean value) {
    m_AddClassification = value;
    reset();
  }

  /**
   * Returns whether to add the numeric classification (label index for nominal classes).
   *
   * @return		true if numeric classification is added
   */
  public boolean getAddClassification() {
    return m_AddClassification;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addClassificationTipText() {
    return
        "If enabled, then the numeric classification (index of class label "
      + "for nominal classes) is added to the spreadsheet.";
  }

  /**
   * Sets the value for the 'Name' column for the numeric classification.
   *
   * @param value	the name
   */
  public void setClassificationEntry(String value) {
    m_ClassificationEntry = value;
    reset();
  }

  /**
   * Returns the value for the 'Name' column for the numeric classification.
   *
   * @return		the name
   */
  public String getClassificationEntry() {
    return m_ClassificationEntry;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classificationEntryTipText() {
    return "The value to use in the '" + COLUMN_NAME + "' column for the numeric classification.";
  }

  /**
   * Sets whether to add the classification label (only for nominal classes).
   *
   * @param value	true if to add classification label
   */
  public void setAddClassificationLabel(boolean value) {
    m_AddClassificationLabel = value;
    reset();
  }

  /**
   * Returns whether to add the classification label (only for nominal classes).
   *
   * @return		true if classification label is added
   */
  public boolean getAddClassificationLabel() {
    return m_AddClassificationLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addClassificationLabelTipText() {
    return
        "If enabled, then the classification label (only "
      + "for nominal classes) is added to the spreadsheet.";
  }

  /**
   * Sets the value for the 'Name' column for the classification label.
   *
   * @param value	the name
   */
  public void setClassificationLabelEntry(String value) {
    m_ClassificationLabelEntry = value;
    reset();
  }

  /**
   * Returns the value for the 'Name' column for the classification label.
   *
   * @return		the name
   */
  public String getClassificationLabelEntry() {
    return m_ClassificationLabelEntry;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classificationLabelEntryTipText() {
    return "The value to use in the '" + COLUMN_NAME + "' column for the classification label.";
  }

  /**
   * Sets whether to add the class distribution (only for nominal classes).
   *
   * @param value	true if to add class distribution
   */
  public void setAddDistribution(boolean value) {
    m_AddDistribution = value;
    reset();
  }

  /**
   * Returns whether to add the class distribution (only for nominal classes).
   *
   * @return		true if class distribution is added
   */
  public boolean getAddDistribution() {
    return m_AddDistribution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addDistributionTipText() {
    return
        "If enabled, then the class distribution (only "
      + "for nominal classes) is added to the spreadsheet.";
  }

  /**
   * Sets the format for the 'Name' column for the class distribution.
   *
   * @param value	the format
   * @see		#PLACEHOLDER_INDEX
   * @see		#PLACEHOLDER_LABEL
   */
  public void setDistributionFormat(String value) {
    m_DistributionFormat = value;
    reset();
  }

  /**
   * Returns the format for the 'Name' column for the numeric classification.
   *
   * @return		the format
   * @see		#PLACEHOLDER_INDEX
   * @see		#PLACEHOLDER_LABEL
   */
  public String getDistributionFormat() {
    return m_DistributionFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String distributionFormatTipText() {
    return
        "The format to use in the '" + COLUMN_NAME + "' column for the "
      + "class distribution; '" + PLACEHOLDER_INDEX + "' can be used for the "
      + "1-based index, '" + PLACEHOLDER_LABEL + "' can be used for the class "
      + "label (if available; if not, the 1-based index is used instead)";
  }

  /**
   * Sets the sorting for the distribution array.
   *
   * @param value	the sorting
   */
  public void setDistributionSorting(Sorting value) {
    m_DistributionSorting = value;
    reset();
  }

  /**
   * Returns the format for the 'Name' column for the numeric classification.
   *
   * @return		the format
   * @see		#PLACEHOLDER_INDEX
   * @see		#PLACEHOLDER_LABEL
   */
  public Sorting getDistributionSorting() {
    return m_DistributionSorting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String distributionSortingTipText() {
    return "The type of sorting to apply to the distribution array.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return WekaPredictionContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet			result;
    WekaPredictionContainer	cont;
    Row				row;
    double[]			dist;
    double[]			distSorted;
    int[]			distIndex;
    int				i;
    String			entry;
    Instance			inst;
    String			label;
    List<SortContainer>		dists;

    result = new SpreadSheet();
    cont   = (WekaPredictionContainer) m_Input;

    // header
    row = result.getHeaderRow();
    row.addCell(COLUMN_NAME).setContent(m_TitleNameColumn);
    row.addCell(COLUMN_VALUE).setContent(m_TitleValueColumn);

    // data
    if (m_AddClassification && cont.hasValue(WekaPredictionContainer.VALUE_CLASSIFICATION)) {
      row = result.addRow("" + result.getRowCount());
      row.addCell(COLUMN_NAME).setContent(m_ClassificationEntry);
      row.addCell(COLUMN_VALUE).setContent((Double) cont.getValue(WekaPredictionContainer.VALUE_CLASSIFICATION));
    }
    if (m_AddClassificationLabel && cont.hasValue(WekaPredictionContainer.VALUE_CLASSIFICATION_LABEL)) {
      row = result.addRow("" + result.getRowCount());
      row.addCell(COLUMN_NAME).setContent(m_ClassificationLabelEntry);
      row.addCell(COLUMN_VALUE).setContent((String) cont.getValue(WekaPredictionContainer.VALUE_CLASSIFICATION_LABEL));
    }
    if (m_AddDistribution && cont.hasValue(WekaPredictionContainer.VALUE_DISTRIBUTION)) {
      dist      = (double[]) cont.getValue(WekaPredictionContainer.VALUE_DISTRIBUTION);
      distIndex = new int[dist.length];
      for (i = 0; i < dist.length; i++)
	distIndex[i] = i;
      // sort?
      if (m_DistributionSorting != Sorting.NONE) {
	dists = new ArrayList<SortContainer>();
	for (i = 0; i < dist.length; i++) {
	  if (m_DistributionSorting == Sorting.DESCENDING)
	    dists.add(new SortContainer(distIndex[i], 1.0 - dist[i]));
	  else
	    dists.add(new SortContainer(distIndex[i], dist[i]));
	}
	Collections.sort(dists);
	distSorted = new double[dist.length];
	for (i = 0; i < dists.size(); i++) {
	  distSorted[i] = dist[dists.get(i).getIndex()];
	  distIndex[i]  = dists.get(i).getIndex();
	}
	dist = distSorted;
      }

      inst = (Instance) cont.getValue(WekaPredictionContainer.VALUE_INSTANCE);
      for (i = 0; i < dist.length; i++) {
	entry = m_DistributionFormat;
	entry = entry.replace(PLACEHOLDER_INDEX, "" + (distIndex[i]+1));
	if (inst == null)
	  label = "" + (distIndex[i]+1);
	else
	  label = inst.classAttribute().value(distIndex[i]);
	entry = entry.replace(PLACEHOLDER_LABEL, label);
	row  = result.addRow("" + result.getRowCount());
	row.addCell(COLUMN_NAME).setContent(entry);
	row.addCell(COLUMN_VALUE).setContent(dist[i]);
      }
    }

    return result;
  }
}
