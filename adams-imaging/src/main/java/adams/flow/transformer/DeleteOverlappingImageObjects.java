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
 * DeleteOverlappingImageObjects.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.annotation.DeprecatedClass;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.AbstractField;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Cleans up overlapping objects, e.g., multiple predicted bounding boxes per object.
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
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: DeleteOverlappingImageObjects
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
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder for locating the objects of interest.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-min-overlap-ratio &lt;double&gt; (property: minOverlapRatio)
 * &nbsp;&nbsp;&nbsp;The minimum ratio that an overlap must have before being considered an actual
 * &nbsp;&nbsp;&nbsp;overlap.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-removal-strategy &lt;REMOVE_SMALLER_OBJECT|REMOVE_LARGER_OBJECT|REMOVE_BOTH&gt; (property: removalStrategy)
 * &nbsp;&nbsp;&nbsp;The strategy for removing overlapping objects.
 * &nbsp;&nbsp;&nbsp;default: REMOVE_SMALLER_OBJECT
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@DeprecatedClass(
  useInstead = RemoveOverlappingImageObjects.class
)
public class DeleteOverlappingImageObjects
  extends AbstractTransformer {

  private static final long serialVersionUID = 3254930183559428182L;

  /**
   * Determines which object of the overlapping ones to remove.
   */
  public enum RemovalStrategy {
    REMOVE_SMALLER_OBJECT,
    REMOVE_LARGER_OBJECT,
    REMOVE_BOTH,
  }

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the minimum overlap ratio to use. */
  protected double m_MinOverlapRatio;

  /** the removal strategy. */
  protected RemovalStrategy m_RemovalStrategy;

  /** whether to check for duplicate object indices or not. */
  protected boolean m_DuplicateIndices;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Cleans up overlapping objects, e.g., multiple predicted bounding boxes per object.";
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
      "min-overlap-ratio", "minOverlapRatio",
      0.0, 0.0, 1.0);

    m_OptionManager.add(
      "removal-strategy", "removalStrategy",
      RemovalStrategy.REMOVE_SMALLER_OBJECT);

    m_OptionManager.add(
      "duplicate-indices", "duplicateIndices",
      false);
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
   * Sets the minimum overlap ratio to use.
   *
   * @param value 	the minimum ratio
   */
  public void setMinOverlapRatio(double value) {
    if (getOptionManager().isValid("minOverlapRatio", value)) {
      m_MinOverlapRatio = value;
      reset();
    }
  }

  /**
   * Returns the minimum overlap ratio to use.
   *
   * @return 		the minimum ratio
   */
  public double getMinOverlapRatio() {
    return m_MinOverlapRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minOverlapRatioTipText() {
    return "The minimum ratio that an overlap must have before being considered an actual overlap.";
  }

  /**
   * Sets the minimum overlap ratio to use.
   *
   * @param value 	the minimum ratio
   */
  public void setRemovalStrategy(RemovalStrategy value) {
    m_RemovalStrategy = value;
    reset();
  }

  /**
   * Returns the minimum overlap ratio to use.
   *
   * @return 		the strategy
   */
  public RemovalStrategy getRemovalStrategy() {
    return m_RemovalStrategy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removalStrategyTipText() {
    return "The strategy for removing overlapping objects.";
  }

  /**
   * Sets the boolean duplicate indices.
   *
   * @param value 	duplicate indices
   */
  public void setDuplicateIndices(boolean value) {
    m_DuplicateIndices = value;
    reset();
  }

  /**
   * Returns the boolean duplicate indices.
   *
   * @return 		duplicate indices
   */
  public boolean getDuplicateIndices() {
    return m_DuplicateIndices;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String duplicateIndicesTipText() {
    return "Whether to check for duplicate indices among objects to be deleted (same object).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result  = QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
    result += QuickInfoHelper.toString(this, "minOverlapRatio", m_MinOverlapRatio, ", overlap ratio: ");
    result += QuickInfoHelper.toString(this, "removalStrategy", m_RemovalStrategy, ", strategy: ");
    result += QuickInfoHelper.toString(this, "duplicateIndices", m_DuplicateIndices, ", duplicate indices: ");

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
    String		result;
    Report 		report;
    Report		newReport;
    Object		output;
    LocatedObjects  	objects;
    int			i;
    int			n;
    int			r;
    LocatedObject	obj1;
    LocatedObject	obj2;
    TIntSet		delete;
    int			area1;
    int			area2;
    int[]		deleteIndices;
    TIntSet		allIndices;
    int			o;
    String		newIndex;

    result = null;

    report   = null;
    output   = null;
    if (m_InputToken.getPayload() instanceof AbstractImageContainer)
      report = ((AbstractImageContainer) m_InputToken.getPayload()).getReport();
    else if (m_InputToken.getPayload() instanceof Report)
      report = (Report) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof ReportHandler)
      report = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      result = "Unsupported input class: " + Utils.classToString(m_InputToken.getPayload());

    if (report != null) {
      objects = m_Finder.findObjects(LocatedObjects.fromReport(report,  m_Finder.getPrefix()));
      delete  = new TIntHashSet();

      // find duplicate object indices and offset if found
      if (m_DuplicateIndices) {
        allIndices = new TIntHashSet();
        for (i = 0; i < objects.size(); i++) {
          obj1 = objects.get(i);
          allIndices.add(obj1.getIndex());
	}
        for (r = 0; r < 2; r++) {
	  for (i = 0; i < objects.size() - 1; i++) {
	    obj1 = objects.get(i);
	    for (n = i + 1; n < objects.size(); n++) {
	      obj2 = objects.get(n);
	      if (obj1.getIndex() == obj2.getIndex()) {
		o = obj2.getIndex();
		while (true) {
		  o++;
		  if (!allIndices.contains(o))
		    break;
		}
		allIndices.add(o);
		newIndex = "" + o;
		obj2.getMetaData().put("index", newIndex);
	      }
	    }
	  }
	}
      }

      // find overlapping objects
      for (i = 0; i < objects.size() - 2; i++) {
        obj1 = objects.get(i);
        for (n = i + 1; n < objects.size() - 1; n++) {
          obj2 = objects.get(n);
	  if ((obj1.overlapRatio(obj2) >= m_MinOverlapRatio) || (obj2.overlapRatio(obj1) >= m_MinOverlapRatio)) {
	    area1 = obj1.getWidth() * obj1.getHeight();
	    area2 = obj2.getWidth() * obj2.getHeight();
	    switch (m_RemovalStrategy) {
	      case REMOVE_LARGER_OBJECT:
	        if (area1 > area2)
	          delete.add(obj1.getIndex());
	        else
	          delete.add(obj2.getIndex());
	        break;
	      case REMOVE_SMALLER_OBJECT:
	        if (area1 < area2)
	          delete.add(obj1.getIndex());
	        else
		  delete.add(obj2.getIndex());
	        break;
	      case REMOVE_BOTH:
	        delete.add(obj1.getIndex());
	        delete.add(obj2.getIndex());
	        break;
	      default:
	        throw new IllegalStateException("Unhandled removal strategy: " + m_RemovalStrategy);
	    }
	  }
	}
      }

      if (delete.size() > 0) {
        // remove flagged objects
	deleteIndices = delete.toArray();
	Arrays.sort(deleteIndices);
        if (isLoggingEnabled())
          getLogger().info("Object indices to remove: " + Utils.arrayToString(deleteIndices));
	objects.remove(deleteIndices);

	// assemble new report
	try {
	  newReport = report.getClass().getDeclaredConstructor().newInstance();
	  // transfer non-object fields
	  for (AbstractField field : report.getFields()) {
	    if (!field.getName().startsWith(m_Finder.getPrefix())) {
	      newReport.addField(field);
	      newReport.setValue(field, report.getValue(field));
	    }
	  }
	  // store objects
	  newReport.mergeWith(objects.toReport(m_Finder.getPrefix()));
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
      else {
        if (isLoggingEnabled())
          getLogger().info("No overlapping objects removed!");
        output = m_InputToken.getPayload();
      }
    }

    if (output != null)
      m_OutputToken = new Token(output);

    return result;
  }
}
