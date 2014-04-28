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
 * AbstractQuantitationReader.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import java.util.List;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.Stoppable;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.report.Report;

/**
 * Abstract ancestor for readers that read files in various formats and
 * creates a reports.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report
 */
public abstract class AbstractReportReader<T extends Report>
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler, ShallowCopySupporter<AbstractReportReader>, Stoppable, FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = -630224132021076920L;

  /** the file to parse. */
  protected PlaceholderFile m_Input;
  
  /** whether the reader got stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Resets the reader.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "input", "input",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the file/directory to read.
   *
   * @param value	the file/directory to read
   */
  public void setInput(PlaceholderFile value) {
    if (value == null)
      m_Input = new PlaceholderFile(".");
    else
      m_Input = value;
    reset();
  }

  /**
   * The file/directory to read.
   *
   * @return		the file/directory to read
   */
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTipText() {
    return "The file to read and turn into a report.";
  }


  /**
   * Performs checks and (always) reads the data.
   *
   * @return		the report loaded from the file
   */
  public List<T> read() {
    List<T>	data;

    m_Stopped = false;
    
    checkData();
    data = readData();
    if (!m_Stopped)
      postRead(data);

    if (m_Stopped && (data != null))
      data.clear();
    
    return data;
  }

  /**
   * The default implementation only checks whether the provided file object
   * is an actual file and whether it exists (if m_InputIsFile = true), or
   * if the file object is a directory and whether it exists.
   */
  protected void checkData() {
    if (!m_Input.exists())
      throw new IllegalStateException("Input file '" + m_Input + "' does not exist!");
    if (m_Input.isDirectory())
      throw new IllegalStateException("No input file but directory provided ('" + m_Input + "')!");
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  protected abstract List<T> readData();

  /**
   * Post-processing after reading the data.
   *
   * @param data	the reports to post-process
   */
  protected void postRead(List<T> data) {
    for (T d: data)
      d.setDatabaseID(determineParentID(d));
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  protected abstract int determineParentID(Report report);

  /**
   * Can be used to free up memory. Default implementation just calls reset().
   * Derived classes can add additional code.
   *
   * @see		#reset()
   */
  public void cleanUp() {
    reset();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <p/>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }

  /**
   * Returns a new instance of the report class in use.
   *
   * @return		the new (empty) report
   */
  public abstract T newInstance();

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractReportReader shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractReportReader shallowCopy(boolean expand) {
    return (AbstractReportReader) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of readers.
   *
   * @return		the reader classnames
   */
  public static String[] getReaders() {
    return ClassLister.getSingleton().getClassnames(AbstractReportReader.class);
  }

  /**
   * Instantiates the report reader with the given options.
   *
   * @param classname	the classname of the reader to instantiate
   * @param options	the options for the reader
   * @return		the instantiated reader or null if an error occurred
   */
  public static AbstractReportReader forName(String classname, String[] options) {
    AbstractReportReader	result;

    try {
      result = (AbstractReportReader) OptionUtils.forName(AbstractReportReader.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the report reader from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			reader to instantiate
   * @return		the instantiated reader or null if an error occurred
   */
  public static AbstractReportReader forCommandLine(String cmdline) {
    return (AbstractReportReader) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
