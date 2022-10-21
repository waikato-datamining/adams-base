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
 * DetermineOverlappingObjects.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.areaoverlap.AreaOverlap;
import adams.data.areaoverlap.PolygonBased;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.report.AbstractField;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DetermineOverlappingAreas
    extends AbstractTransformer {

  private static final long serialVersionUID = 8175397929496972306L;

  /** the storage item. */
  protected StorageName m_StorageName;

  /** whether to compare with itself. */
  protected boolean m_CompareWithItself;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the object overlap calculation to use. */
  protected ObjectOverlap m_OverlapAlgorithm;

  /** the area calculation to use. */
  protected AreaOverlap m_AreaAlgorithm;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes the overlap of objects with the specified report from "
	+ "storage (or itself) using the specified overlap algorithm and then computes the overlapping areas.";
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
	"compare-with-itself", "compareWithItself",
	false);

    m_OptionManager.add(
	"finder", "finder",
	new AllFinder());

    m_OptionManager.add(
	"overlap-algorithm", "overlapAlgorithm",
	new AreaRatio());

    m_OptionManager.add(
	"area-algorithm", "areaAlgorithm",
	new PolygonBased());
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
   * Sets whether to compare the report with itself rather than the one
   * from storage.
   *
   * @param value	true if to compare with itself
   */
  public void setCompareWithItself(boolean value) {
    m_CompareWithItself = value;
    reset();
  }

  /**
   * Returns whether to compare the report with itself rather than the one
   * from storage.
   *
   * @return		true if to compare with itself
   */
  public boolean getCompareWithItself() {
    return m_CompareWithItself;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String compareWithItselfTipText() {
    return "If enabled, compares the incoming report with itself rather than the one from storage.";
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
  public void setOverlapAlgorithm(ObjectOverlap value) {
    m_OverlapAlgorithm = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public ObjectOverlap getOverlapAlgorithm() {
    return m_OverlapAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapAlgorithmTipText() {
    return "The algorithm to use for determining the overlapping objects.";
  }

  /**
   * Sets the algorithm for determining the overlapping objects.
   *
   * @param value 	the algorithm
   */
  public void setAreaAlgorithm(AreaOverlap value) {
    m_AreaAlgorithm = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public AreaOverlap getAreaAlgorithm() {
    return m_AreaAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String areaAlgorithmTipText() {
    return "The algorithm to use for calculating the areas.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result = QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
    result += QuickInfoHelper.toString(this, "overlapAlgorithm", m_OverlapAlgorithm, ", overlap: ");
    if (m_CompareWithItself)
      result += ", with itself";
    else
      result += QuickInfoHelper.toString(this, "storageName", m_StorageName, ", storage: ");
    result += QuickInfoHelper.toString(this, "areaAlgorithm", m_AreaAlgorithm, ", area: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object			obj;
    Report			newReport;
    Report			thisReport;
    Report 			otherReport;
    LocatedObjects		thisObjs;
    LocatedObjects		otherObjs;
    Map<LocatedObject, Map<LocatedObject,Double>>   matches;
    MessageCollection		errors;
    LocatedObjects 		newObjs;
    Set<LocatedObject> 		overlaps;
    LocatedObjects		allObjs;
    int				i;
    Object			output;

    result = null;

    output      = null;
    thisReport  = null;
    otherReport = null;
    newObjs     = null;

    if (m_InputToken.getPayload() instanceof AbstractImageContainer)
      thisReport = ((AbstractImageContainer) m_InputToken.getPayload()).getReport();
    else if (m_InputToken.getPayload() instanceof Report)
      thisReport = (Report) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof ReportHandler)
      thisReport = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      result = "Unsupported input class: " + Utils.classToString(m_InputToken.getPayload());

    if (thisReport != null) {
      if (m_CompareWithItself) {
	allObjs  = m_Finder.findObjects(LocatedObjects.fromReport(thisReport, m_Finder.getPrefix()));
	newObjs  = new LocatedObjects();
	overlaps = new HashSet<>();
	for (i = 0; i < allObjs.size() - 1; i++) {
	  thisObjs  = new LocatedObjects(allObjs.get(i));
	  otherObjs = new LocatedObjects(allObjs.subList(i + 1, i + 2));
	  overlaps.addAll(m_OverlapAlgorithm.calculate(thisObjs, otherObjs));
	}
	newObjs.addAll(overlaps);
      }
      else {
	obj = getStorageHandler().getStorage().get(m_StorageName);
	if (obj == null)
	  result = "Failed to retrieve storage item: " + m_StorageName;
	else {
	  if (obj instanceof Report)
	    otherReport = (Report) obj;
	  else if (obj instanceof ReportHandler)
	    otherReport = ((ReportHandler) obj).getReport();
	  else
	    result = "Unhandled type of storage item '" + m_StorageName + "': " + Utils.classToString(obj);
	}
	if (otherReport != null) {
	  thisObjs  = m_Finder.findObjects(LocatedObjects.fromReport(thisReport, m_Finder.getPrefix()));
	  otherObjs = m_Finder.findObjects(LocatedObjects.fromReport(otherReport, m_Finder.getPrefix()));
	  matches   = m_OverlapAlgorithm.matches(thisObjs, otherObjs);
	  errors    = new MessageCollection();
	  newObjs   = m_AreaAlgorithm.calculate(matches, errors);
	  if (newObjs == null)
	    result = errors.toString();
	}
      }
    }

    if (newObjs != null) {
      // assemble new report
      try {
	newReport = thisReport.getClass().getDeclaredConstructor().newInstance();
	// transfer non-object fields
	for (AbstractField field: thisReport.getFields()) {
	  if (!field.getName().startsWith(m_Finder.getPrefix())) {
	    newReport.addField(field);
	    newReport.setValue(field, thisReport.getValue(field));
	  }
	}
	// store objects
	newReport.mergeWith(newObjs.toReport(m_Finder.getPrefix()));
	// update report
	if (m_InputToken.getPayload() instanceof AbstractImageContainer) {
	  output = m_InputToken.getPayload();
	  ((AbstractImageContainer) output).setReport(newReport);
	}
	else if (m_InputToken.getPayload() instanceof MutableReportHandler) {
	  output = m_InputToken.getPayload();
	  ((MutableReportHandler) output).setReport(newReport);
	}
	else {
	  output = newReport;
	}
      }
      catch (Exception e) {
	result = handleException("Failed to create new report with updated objects!", e);
	output = null;
      }
    }

    if (output != null)
      m_OutputToken = new Token(output);

    return result;
  }
}
