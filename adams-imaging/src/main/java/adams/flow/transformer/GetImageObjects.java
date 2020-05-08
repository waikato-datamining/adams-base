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
 * GetImageObjects.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Uses the specified object finder to locate objects and forwards the objects.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.transformer.locateobjects.LocatedObject<br>
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
 * &nbsp;&nbsp;&nbsp;default: GetImageObjects
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the indices get output as array instead of one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use for locating objects in the report.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-clean-indices &lt;boolean&gt; (property: cleanIndices)
 * &nbsp;&nbsp;&nbsp;If enabled, all potential index entries get removed from the meta-data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GetImageObjects
  extends AbstractArrayProvider {

  private static final long serialVersionUID = -5644432725273726622L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** whether to clean the object indices. */
  protected boolean m_CleanIndices;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified object finder to locate objects and forwards the objects.";
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
      "clean-indices", "cleanIndices",
      false);
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return LocatedObject.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the indices get output as array instead of one-by-one.";
  }

  /**
   * Sets the object finder to use.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the object finder in use.
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
    return "The object finder to use for locating objects in the report.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, Report.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
    result += QuickInfoHelper.toString(this, "cleanIndices", m_CleanIndices, "clean indices", ", ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Report		report;
    LocatedObjects	objects;
    boolean		cleaned;

    result = null;

    report = null;
    if (m_InputToken.hasPayload(AbstractImageContainer.class))
      report = m_InputToken.getPayload(AbstractImageContainer.class).getReport();
    else if (m_InputToken.hasPayload(Report.class))
      report = m_InputToken.getPayload(Report.class);
    else
      result = m_InputToken.unhandledData();

    m_Queue.clear();
    if (result == null) {
      objects = LocatedObjects.fromReport(report, m_Finder.getPrefix());
      if (m_CleanIndices) {
        cleaned = false;
        for (LocatedObject object : objects) {
          if (object.getMetaData().containsKey(LocatedObjects.KEY_INDEX)) {
            cleaned = true;
            object.getMetaData().remove(LocatedObjects.KEY_INDEX);
          }
        }
        if (isLoggingEnabled())
          getLogger().info("Cleaned any indices? " + cleaned);
      }
      m_Queue.addAll(objects.subset(m_Finder.find(report)));
    }

    return result;
  }
}
