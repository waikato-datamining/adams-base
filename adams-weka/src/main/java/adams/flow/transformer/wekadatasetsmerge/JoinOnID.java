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
 * JoinOnID.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekadatasetsmerge;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Joins the datasets by concatenating rows that share a unique ID.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-class-finder &lt;adams.data.weka.columnfinder.ColumnFinder&gt; (property: classFinder)
 * &nbsp;&nbsp;&nbsp;The column finder to use to find class attributes in the datasets.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.columnfinder.Class
 * </pre>
 *
 * <pre>-dataset-names &lt;adams.core.base.BaseString&gt; [-dataset-names ...] (property: datasetNames)
 * &nbsp;&nbsp;&nbsp;The list of dataset names to use in attribute renaming.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-attr-renames-exp &lt;adams.core.base.BaseRegExp&gt; [-attr-renames-exp ...] (property: attributeRenamesExp)
 * &nbsp;&nbsp;&nbsp;The expressions to use to select attribute names for renaming (one per dataset
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-attr-renames-format &lt;adams.core.base.BaseString&gt; [-attr-renames-format ...] (property: attributeRenamesFormat)
 * &nbsp;&nbsp;&nbsp;One format string for each renaming expression to specify how to rename
 * &nbsp;&nbsp;&nbsp;the attribute. Can contain the {DATASET} keyword which will be replaced
 * &nbsp;&nbsp;&nbsp;by the dataset name, and also group identifiers which will be replaced by
 * &nbsp;&nbsp;&nbsp;groups from the renaming regex.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-output-name &lt;java.lang.String&gt; (property: outputName)
 * &nbsp;&nbsp;&nbsp;The name to use for the merged dataset.
 * &nbsp;&nbsp;&nbsp;default: output
 * </pre>
 *
 * <pre>-ensure-equal-values &lt;boolean&gt; (property: ensureEqualValues)
 * &nbsp;&nbsp;&nbsp;Whether multiple attributes being merged into a single attribute require
 * &nbsp;&nbsp;&nbsp;equal values from all sources.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-unique-id &lt;java.lang.String&gt; (property: uniqueID)
 * &nbsp;&nbsp;&nbsp;The name of the attribute to use as the joining key for the merge.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-complete-rows-only &lt;boolean&gt; (property: completeRowsOnly)
 * &nbsp;&nbsp;&nbsp;Whether only those IDs that have source data in all datasets should be merged.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 * <p>
 * Performs a merge by using a unique ID attribute for each source dataset
 * to concatenate rows with the same ID.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class JoinOnID
  extends AbstractMerge {

  private static final long serialVersionUID = 5764925655464274742L;

  /** The name of the attribute to use as the merge key. */
  protected String m_UniqueID;

  /** Whether or not to skip IDs that don't exist in all source datasets. */
  protected boolean m_CompleteRowsOnly;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Joins the datasets by concatenating rows that share a unique ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    // Define the default options
    super.defineOptions();

    // Define the unique ID option
    m_OptionManager.add(
      "unique-id", "uniqueID",
      "");

    // Define the complete-rows-only option
    m_OptionManager.add(
      "complete-rows-only", "completeRowsOnly",
      false);
  }

  /**
   * Gets the name of the unique ID attribute that the merge is joining on.
   *
   * @return The name of the unique ID attribute.
   */
  public String getUniqueID() {
    return m_UniqueID;
  }

  /**
   * Sets the name of the unique ID attribute that the merge is joining on.
   *
   * @param value The name of the unique ID attribute.
   */
  public void setUniqueID(String value) {
    m_UniqueID = value;
    reset();
  }

  /**
   * Gets the tip-text for the unique ID option.
   *
   * @return The tip-text as a String.
   */
  public String uniqueIDTipText() {
    return "The name of the attribute to use as the joining key for the merge.";
  }

  /**
   * Gets whether incomplete rows should be skipped.
   *
   * @return Whether incomplete rows should be skipped.
   */
  public boolean getCompleteRowsOnly() {
    return m_CompleteRowsOnly;
  }

  /**
   * Sets whether incomplete rows should be skipped.
   *
   * @param value Whether incomplete rows should be skipped.
   */
  public void setCompleteRowsOnly(boolean value) {
    m_CompleteRowsOnly = value;
    reset();
  }

  /**
   * Gets the tip-text for the complete-rows-only option.
   *
   * @return The tip-text as a String.
   */
  public String completeRowsOnlyTipText() {
    return "Whether only those IDs that have source data in all datasets should be merged.";
  }

  /**
   * Checks that each of the given datasets has the unique ID attribute. Also checks that
   * the unique ID attribute is the same data type for all datasets.
   *
   * @param datasets The datasets that are to be merged.
   * @return Null if all datasets have the unique ID attribute, otherwise an error message.
   */
  protected String checkAllDatasetsHaveIDAttribute(Instances[] datasets) {
    // Record the type the ID needs to be
    Attribute checkAgainst = null;

    // Check each dataset in turn
    for (Instances dataset : datasets) {
      // Get the merge attribute
      Attribute idAttribute = dataset.attribute(m_UniqueID);

      // Check the merge attribute exists
      if (idAttribute == null)
	return "Dataset " +
	  dataset.relationName() +
	  " does not have the ID attribute (" +
	  m_UniqueID +
	  ")";

      // Initialise the required type to the type in the first dataset
      if (checkAgainst == null) {
	checkAgainst = idAttribute;
      }
      // If the type in this dataset is different, report it
      else if (idAttribute.type() != checkAgainst.type()) {
	return "Dataset " +
	  dataset.relationName() +
	  "has a different type for it's ID attribute (" +
	  Attribute.typeToString(idAttribute.type()) +
	  " instead of " +
	  Attribute.typeToString(checkAgainst.type()) +
	  ")";
	// Check the id attributes are the same
      }
      else {
	String message = checkAgainst.equalsMsg(idAttribute);
	if (message != null) return message;
      }
    }

    // If we get here, all datasets check out
    return null;
  }

  /**
   * Whether the given attribute name is the name of the unique ID
   * attribute.
   *
   * @param attributeName The attribute name to check.
   * @return True if the given attribute name is the unique ID name,
   * false otherwise.
   */
  protected boolean isUniqueIDName(String attributeName) {
    return attributeName.equals(m_UniqueID);
  }

  /**
   * Finds the index of the unique ID attribute in the given dataset.
   *
   * @param dataset The dataset to search.
   * @return The index of the unique ID attribute, or -1 if not found.
   */
  protected int findAttributeIndexOfUniqueID(Instances dataset) {
    // Check each attribute in turn
    for (int attributeIndex = 0; attributeIndex < dataset.numAttributes(); attributeIndex++) {
      // Get the attribute
      Attribute attribute = dataset.attribute(attributeIndex);

      // See if it has the right name. If so, return its index
      if (isUniqueIDName(attribute.name())) return attributeIndex;
    }

    // The unique ID attribute wasn't found in the dataset
    return -1;
  }

  /**
   * Compares two lists of source attributes to determine the order in which their
   * mapped attributes should appear in the merged dataset.
   *
   * @param sources1 The source attributes of the first mapped attribute.
   * @param sources2 The source attributes of the second mapped attribute.
   * @return sources1 < sources2 => -1,
   * sources1 > sources2 => 1,
   * otherwise 0;
   */
  @Override
  protected int compare(List<SourceAttribute> sources1, List<SourceAttribute> sources2) {
    // Check if the attribute is the unique ID attribute
    boolean idName1 = isUniqueIDName(sources1.get(0).attributeName);
    boolean idName2 = isUniqueIDName(sources2.get(0).attributeName);

    // Put the ID attribute before all other attributes
    if (idName1 && !idName2) {
      return -1;
    }
    else if (!idName1 && idName2) {
      return 1;
    }
    else {
      // Otherwise, just use the default ordering
      return super.compare(sources1, sources2);
    }
  }

  /**
   * Gets the name of the attribute in the merged dataset that the given
   * source attribute maps to.
   *
   * @param source  The source attribute.
   * @return The name of the mapped attribute in the merged dataset.
   */
  @Override
  protected String getMappedAttributeName(SourceAttribute source) {
    if (isUniqueIDName(source.attributeName)) {
      // The unique ID name can't be renamed
      return source.attributeName;
    }
    else {
      // Otherwise, default
      return super.getMappedAttributeName(source);
    }
  }

  /**
   * Allows specific merge methods to specify the order in which rows are placed
   * into the merged dataset, and which rows from the source datasets are used
   * for the source data.
   *
   * @return An enumeration of the source rows, one row for each dataset.
   */
  @Override
  protected Enumeration<int[]> getRowSetEnumeration() {
    return new UniqueIDEnumeration(m_Datasets);
  }

  /**
   * Hook method for performing checks before attempting the merge.
   *
   * @param datasets the datasets to merge
   * @return null if successfully checked, otherwise error message
   */
  @Override
  protected String check(Instances[] datasets) {
    // Perform the standard checks first
    String result = super.check(datasets);
    if (result != null) return result;

    // Check all datasets have the ID attribute
    return checkAllDatasetsHaveIDAttribute(datasets);
  }

  /**
   * Makes sure the source data for each mapped attribute is the same type.
   *
   * @param attributeMapping  The attribute mapping.
   * @return  Null if all mappings are okay, or an error message if not.
   */
  @Override
  protected String checkAttributeMapping(Map<String, List<SourceAttribute>> attributeMapping) {
    // Perform the standard checks first
    String result = super.checkAttributeMapping(attributeMapping);
    if (result != null) return result;

    // Make sure the unique ID is not also a class attribute
    if (isAnyClassAttribute(attributeMapping.get(m_UniqueID)))
      result = "The provided unique ID (" + getUniqueID() + ") is also a class attribute.";

    return result;
  }

  /**
   * Enumeration class that returns the rows from the source datasets
   * joined on the unique ID attribute.
   */
  public class UniqueIDEnumeration implements Enumeration<int[]> {

    /** The set of all unique IDs across the datasets. */
    private Map<Object, int[]> m_UniqueIDRowMap;

    /** Iterator over the unique IDs. */
    private Iterator<Object> m_InternalIterator;

    /**
     * Constructs an enumeration over the unique keys in the
     * given datasets, and the rows that they appear in.
     *
     * @param datasets The datasets being merged.
     */
    private UniqueIDEnumeration(Instances[] datasets) {
      // Generate the lookup map
      recordUniqueIDs(datasets);

      // Initialise the internal iterator
      m_InternalIterator = m_UniqueIDRowMap.keySet().iterator();
    }

    /**
     * Records the set of unique IDs that exist in the given datasets, and
     * also maps them to the rows in which they appear in the individual
     * datasets.
     *
     * @param datasetsToMerge The set of datasets being merged.
     */
    private void recordUniqueIDs(Instances[] datasetsToMerge) {
      // Create the map
      m_UniqueIDRowMap = new LinkedHashMap<>();

      // Process each dataset in turn
      for (int datasetIndex = 0; datasetIndex < datasetsToMerge.length; datasetIndex++) {
	// Get the next dataset to process
	Instances dataset = datasetsToMerge[datasetIndex];

	// Find the index of the attribute that contains the unique ID
	int uniqueIDAttributeIndex = findAttributeIndexOfUniqueID(dataset);

	// If the unique ID attribute doesn't exist in this dataset, we can skip it
	if (uniqueIDAttributeIndex == -1) continue;

	// Process the unique IDs one-by-one
	for (int instanceIndex = 0; instanceIndex < dataset.size(); instanceIndex++) {
	  // Get the next instance
	  Instance instance = dataset.instance(instanceIndex);

	  // Get the unique ID for this instance
	  Object id = getValue(instance, uniqueIDAttributeIndex);

	  // If the unique ID is missing, skip this instance
	  if (id == null) continue;

	  // Make sure the mapping has an entry for this ID (create it
	  // if it doesn't exist)
	  if (!m_UniqueIDRowMap.containsKey(id))
	    m_UniqueIDRowMap.put(id, initialiseRowSet(datasetsToMerge.length));

	  // Get the mapping for the unique ID
	  int[] rowInstanceTable = m_UniqueIDRowMap.get(id);

	  // Record which row contains the unique ID for this dataset
	  rowInstanceTable[datasetIndex] = instanceIndex;
	}
      }

      // If we only want complete rows, remove any incomplete ones
      if (getCompleteRowsOnly()) removeIncompleteRows();
    }

    /**
     * Creates an empty row-set where all rows are missing.
     *
     * @param size The size of the row-set (should equal the number of merging datasets).
     * @return The initialised row-set.
     */
    private int[] initialiseRowSet(int size) {
      // Create the row-set
      int[] rowSet = new int[size];

      // Set all rows to missing
      for (int i = 0; i < size; i++) {
	rowSet[i] = ROW_MISSING;
      }

      // Return the row-set
      return rowSet;
    }

    /**
     * Removes any entries from the ID map that don't have source
     * data in all source datasets.
     */
    private void removeIncompleteRows() {
      // Get an iterator over the ID map
      Iterator<Object> idIterator = m_UniqueIDRowMap.keySet().iterator();

      // Check each entry in turn
      while (idIterator.hasNext()) {
	// Get the ID for this row
	Object id = idIterator.next();

	// Get the source data for this row
	int[] rowSet = m_UniqueIDRowMap.get(id);

	// Check that there are no null entries
	for (int rowEntry : rowSet) {
	  // If there is a null entry, remove this row
	  if (rowEntry == ROW_MISSING) {
	    idIterator.remove();
	    break;
	  }
	}
      }
    }

    @Override
    public boolean hasMoreElements() {
      return m_InternalIterator.hasNext();
    }

    @Override
    public int[] nextElement() {
      Object nextID = m_InternalIterator.next();

      return m_UniqueIDRowMap.get(nextID);
    }
  }
}