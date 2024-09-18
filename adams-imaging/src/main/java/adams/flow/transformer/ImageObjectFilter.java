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
 * ImageObjectFilter.java
 * Copyright (C) 2017-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.data.objectfilter.ObjectFilter;
import adams.data.objectfilter.PassThrough;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.AbstractField;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Uses the specified object finder to locate objects and then applies the object filter to the located objects. Modifies the report&#47;report handler unless the 'no-copy' is flag is unset.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageObjectFilter
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
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-filter &lt;adams.data.objectfilter.ObjectFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The object filter to apply to the located objects.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfilter.PassThrough
 * </pre>
 *
 * <pre>-keep-all-objects &lt;boolean&gt; (property: keepAllObjects)
 * &nbsp;&nbsp;&nbsp;If enabled, all objects are kept, ie the ones that weren't located by the
 * &nbsp;&nbsp;&nbsp;object finder and the filtered ones.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-clean-indices &lt;boolean&gt; (property: cleanIndices)
 * &nbsp;&nbsp;&nbsp;If enabled, all potential index entries get removed from the meta-data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the report&#47;report handler is created before processing
 * &nbsp;&nbsp;&nbsp;the report.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectFilter
  extends AbstractTransformer
  implements InPlaceProcessing {

  private static final long serialVersionUID = -3992867498417362738L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the object filter to apply to the located objects. */
  protected ObjectFilter m_Filter;

  /** whether to keep all objects. */
  protected boolean m_KeepAllObjects;

  /** whether to clean the object indices. */
  protected boolean m_CleanIndices;

  /** whether to skip creating a copy of the spreadsheet. */
  protected boolean m_NoCopy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified object finder to locate objects and then applies the object filter to the "
	     + "located objects. Modifies the report/report handler unless the 'no-copy' is flag is unset.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "filter", "filter",
      new PassThrough());

    m_OptionManager.add(
      "keep-all-objects", "keepAllObjects",
      false);

    m_OptionManager.add(
      "clean-indices", "cleanIndices",
      false);

    m_OptionManager.add(
      "no-copy", "noCopy",
      true);
  }

  /**
   * Sets the finder to use for locating the objects.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for locating the objects.
   *
   * @return		the finder
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
    return "The object finder to use.";
  }

  /**
   * Sets the filter to apply to the located objects.
   *
   * @param value	the filter
   */
  public void setFilter(ObjectFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter to apply to the located objects.
   *
   * @return		the filter
   */
  public ObjectFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The object filter to apply to the located objects.";
  }

  /**
   * Sets whether to keep all objects, i.e., the ones that weren't
   * located by the object finder and the filtered ones.
   *
   * @param value	true if to keep all objects
   */
  public void setKeepAllObjects(boolean value) {
    m_KeepAllObjects = value;
    reset();
  }

  /**
   * Returns whether to keep all objects, i.e., the ones that weren't
   * located by the object finder and the filtered ones.
   *
   * @return		true if to keep all objects
   */
  public boolean getKeepAllObjects() {
    return m_KeepAllObjects;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepAllObjectsTipText() {
    return
      "If enabled, all objects are kept, ie the ones that weren't "
	+ "located by the object finder and the filtered ones.";
  }

  /**
   * Sets whether to remove index entries from the meta-data.
   *
   * @param value	true if to clean
   */
  public void setCleanIndices(boolean value) {
    m_CleanIndices = value;
    reset();
  }

  /**
   * Returns whether to remove index entries from the meta-data.
   *
   * @return		true if to clean
   */
  public boolean getCleanIndices() {
    return m_CleanIndices;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cleanIndicesTipText() {
    return
      "If enabled, all potential index entries get removed from the meta-data.";
  }

  /**
   * Sets whether to skip creating a copy of the report/report handler before updating the report.
   *
   * @param value	true if to skip creating copy
   */
  @Override
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the report/report handler before updating the report.
   *
   * @return		true if copying is skipped
   */
  @Override
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String noCopyTipText() {
    return "If enabled, no copy of the report/report handler is created before processing the report.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class, MutableReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Report.class, MutableReportHandler.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
    result += QuickInfoHelper.toString(this, "filter", m_Filter, ", filter: ");
    result += QuickInfoHelper.toString(this, "keepAllObjects", m_KeepAllObjects, "keep all", ", ");
    result += QuickInfoHelper.toString(this, "cleanIndices", m_CleanIndices, "clean indices", ", ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    MutableReportHandler	handler;
    int[]			indices;
    Report			report;
    Report			newReport;
    LocatedObjects		objs;
    LocatedObjects		newObjs;
    LocatedObjects		otherObjs;

    result  = null;
    report  = null;
    handler = null;
    if (m_InputToken.hasPayload(MutableReportHandler.class)) {
      handler = m_InputToken.getPayload(MutableReportHandler.class);
      if (!m_NoCopy)
	handler = ObjectCopyHelper.copyObject(handler);
      report  = handler.getReport();
    }
    else if (m_InputToken.hasPayload(Report.class)) {
      report = m_InputToken.getPayload(Report.class);
      if (!m_NoCopy)
	report = ObjectCopyHelper.copyObject(report);
    }
    else {
      result = m_InputToken.unhandledData();
    }

    if (report == null)
      result = "No report available!";

    if (result == null) {
      try {
	objs = LocatedObjects.fromReport(report, m_Finder.getPrefix());

	if (m_CleanIndices)
	  objs.resetIndex();

	// find objects of interest
	indices = m_Finder.find(objs);

	// remove all old objects
	for (AbstractField field : report.getFields()) {
	  if (field.getName().startsWith(m_Finder.getPrefix()))
	    report.removeValue(field);
	}

	// compile new objects
	newObjs = objs.subset(indices);

	// filter objects
	m_Filter.setFlowContext(this);
	newObjs = m_Filter.filter(newObjs);

	// add objects to report
	if (m_KeepAllObjects) {
	  otherObjs = objs.subset(indices, true);
	  newObjs.addAll(otherObjs);
	}
	newReport = newObjs.toReport(m_Finder.getPrefix());
	for (AbstractField field : newReport.getFields()) {
	  report.addField(field);
	  report.setValue(field, newReport.getValue(field));
	}

	if (handler != null) {
	  handler.setReport(report);
	  m_OutputToken = new Token(handler);
	}
	else {
	  m_OutputToken = new Token(report);
	}
      }
      catch (Exception e) {
	result = handleException("Failed to filter objects!", e);
      }
    }

    return result;
  }
}
