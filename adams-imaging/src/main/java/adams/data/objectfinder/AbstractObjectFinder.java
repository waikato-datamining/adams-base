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
 * AbstractObjectFinder.java
 * Copyright (C) 2017-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.data.objectfinder;

import adams.core.LenientModeSupporter;
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Ancestor for finders that locate objects in the report of an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectFinder
  extends AbstractOptionHandler
  implements ObjectFinder, QuickInfoSupporter, LenientModeSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 2092237222859238898L;

  /** the prefix of the objects in the report. */
  protected String m_Prefix;

  /** boolean lenient. */
  protected boolean m_Lenient;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "lenient", "lenient",
      false);
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Sets whether to suppress error if -1 indices found.
   *
   * @param value	true if to suppress
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to suppress error if -1 indices found.
   *
   * @return		true if to suppress
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return "If enabled, then no error is generated if -1 indices are returned.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "prefix", m_Prefix, "prefix: ");
    result += QuickInfoHelper.toString(this, "lenient", m_Lenient, "lenient", ", ");

    return result;
  }

  /**
   * Hook method for performing checks.
   * <br><br>
   * Default implementation returns null.
   *
   * @param objects  	the list of objects to check
   * @return		null if successful check, otherwise error message
   */
  protected String check(LocatedObjects objects) {
    return null;
  }

  /**
   * Performs the actual finding of the objects in the list.
   * 
   * @param objects  	the list of objects to process
   * @return		the indices
   */
  protected abstract int[] doFind(LocatedObjects objects);

  /**
   * Finds the objects in the list of objects.
   *
   * @param objects	the list of objects to process
   * @return		the indices
   */
  public int[] find(LocatedObjects objects) {
    int[]	result;
    String	msg;
    int		count;

    msg = check(objects);
    if (msg != null)
      throw new IllegalStateException(msg);

    result = doFind(objects);
    // check if -1 indices presnet
    count = 0;
    for (int index: result) {
      if (index == -1)
        count++;
    }
    if (count > 0) {
      if (m_Lenient)
	getLogger().warning("Number of indices returned as -1: " + count);
      else
        throw new IllegalStateException("Number of indices returned as -1: " + count);
    }

    return result;
  }

  /**
   * Finds the objects in the report.
   * 
   * @param report  	the report to process
   * @return		the indices
   */
  public int[] find(Report report) {
    if (report == null)
      throw new IllegalStateException("No report provided!");

    return find(LocatedObjects.fromReport(report, m_Prefix));
  }

  /**
   * Finds the objects in the list of objects.
   *
   * @param objects	the list of objects to process
   * @return		the indices
   */
  public LocatedObjects findObjects(LocatedObjects objects) {
    int[]		indices;

    indices = find(objects);
    return objects.subset(indices);
  }

  /**
   * Finds the objects in the report.
   *
   * @param report	the report to process
   * @return		the indices
   */
  public LocatedObjects findObjects(Report report) {
    return findObjects(LocatedObjects.fromReport(report, m_Prefix));
  }
}
