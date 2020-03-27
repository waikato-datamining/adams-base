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
 * AttachOverlappingMetaData.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.report.Report;
import adams.flow.control.StorageName;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Attaches meta-data from the stored report
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the report in storage to obtain the meta-data from.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder for locating the objects of interest in the storage report.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-overlap-detection &lt;adams.data.objectoverlap.ObjectOverlap&gt; (property: overlapDetection)
 * &nbsp;&nbsp;&nbsp;The algorithm to use for determining the overlapping objects.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectoverlap.AreaRatio
 * </pre>
 *
 * <pre>-meta-data-key &lt;adams.core.base.BaseString&gt; [-meta-data-key ...] (property: metaDataKeys)
 * &nbsp;&nbsp;&nbsp;The keys of the meta-data values to attach.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AttachOverlappingMetaData
  extends AbstractObjectFilter {

  private static final long serialVersionUID = 5647107073729835067L;

  /** the storage item. */
  protected StorageName m_StorageName;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the object overlap calculation to use. */
  protected ObjectOverlap m_OverlapDetection;

  /** the meta-data keys to attach. */
  protected BaseString[] m_MetaDataKeys;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Attaches meta-data from the stored report";
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
      "overlap-detection", "overlapDetection",
      new AreaRatio());

    m_OptionManager.add(
      "meta-data-key", "metaDataKeys",
      new BaseString[0]);
  }

  /**
   * Sets the name of the report in storage to obtain the meta-data from.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the report in storage to obtain the meta-data from.
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
    return "The name of the report in storage to obtain the meta-data from.";
  }

  /**
   * Sets the object finder for locating the objects in the storage report.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns object finder for locating the objects in the storage report.
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
    return "The object finder for locating the objects of interest in the storage report.";
  }

  /**
   * Sets the algorithm for determining the overlapping objects
   *
   * @param value 	the algorithm
   */
  public void setOverlapDetection(ObjectOverlap value) {
    m_OverlapDetection = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public ObjectOverlap getOverlapDetection() {
    return m_OverlapDetection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapDetectionTipText() {
    return "The algorithm to use for determining the overlapping objects.";
  }

  /**
   * Sets the keys of the meta-data values to attach.
   *
   * @param value 	the keys
   */
  public void setMetaDataKeys(BaseString[] value) {
    m_MetaDataKeys = value;
    reset();
  }

  /**
   * Returns the keys of the meta-data values to attach.
   *
   * @return 		the keys
   */
  public BaseString[] getMetaDataKeys() {
    return m_MetaDataKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeysTipText() {
    return "The keys of the meta-data values to attach.";
  }

  /**
   * Returns whether flow context is actually required.
   *
   * @return		true if required
   */
  @Override
  protected boolean requiresFlowContext() {
    return true;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "finder", m_Finder, ", finder: ");
    result += QuickInfoHelper.toString(this, "overlapDetection", m_OverlapDetection, ", overlap: ");

    return result;
  }

  /**
   * Hook method for checking the object list before processing it.
   *
   * @param objects	the object list to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(LocatedObjects objects) {
    String	result;

    result = super.check(objects);

    if (result == null) {
      if (!m_FlowContext.getStorageHandler().getStorage().has(m_StorageName))
        result = "Report is not available from storage: " + m_StorageName;
    }

    return result;
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the objects to filter
   * @return		the updated object list
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    Report					report;
    LocatedObjects				others;
    Map<LocatedObject, Set<LocatedObject>> 	matches;
    Set<LocatedObject>				overlaps;
    int						index;
    Iterator<LocatedObject>			iter;
    LocatedObject				overlap;

    report   = (Report) m_FlowContext.getStorageHandler().getStorage().get(m_StorageName);
    others   = m_Finder.findObjects(report);
    matches = m_OverlapDetection.matches(objects, others);

    for (LocatedObject object : matches.keySet()) {
      index = objects.indexOf(object);
      if (index == -1) {
        getLogger().warning("Failed to locate object: " + object);
	continue;
      }
      overlaps = matches.get(object);
      if (overlaps.size() > 1)
        getLogger().warning("More than one overlap for: " + object);
      iter = overlaps.iterator();
      if (iter.hasNext()) {
	overlap = iter.next();
	for (BaseString key : m_MetaDataKeys) {
	  if (overlap.getMetaData().containsKey(key.getValue()))
	    objects.get(index).getMetaData().put(key.getValue(), overlap.getMetaData().get(key.getValue()));
	}
      }
    }

    return objects;
  }
}
