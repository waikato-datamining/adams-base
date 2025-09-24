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
 * AbstractDataContainerReader.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract ancestor for readers that read files in various formats and
 * turn them into data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to read
 */
public abstract class AbstractDataContainerReader<T extends DataContainer>
  extends AbstractOptionHandler
  implements DataContainerReader<T> {

  /** for serialization. */
  private static final long serialVersionUID = -4690065186988048507L;

  /** the file to parse. */
  protected PlaceholderFile m_Input;

  /** whether to create a dumy report if none is present. */
  protected boolean m_CreateDummyReport;

  /** the data containers that have been read. */
  protected List<T> m_ReadData;

  /** indicates whether the data has been processed. */
  protected boolean m_Processed;

  /** indicates whether the input has to be a file (= default) or directory. */
  protected boolean m_InputIsFile;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  @Override
  public String getAdditionalInformation() {
    StringBuilder	result;

    result = new StringBuilder();

    result.append("Supported file extensions: ").append(Utils.flatten(getFormatExtensions(), ", "));
    result.append("\n");
    result.append("Default file extension: ").append(getDefaultFormatExtension());

    return result.toString();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ReadData    = new ArrayList<T>();
    m_InputIsFile = true;
  }

  /**
   * Resets the reader (but does not clear the input data!).
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ReadData  = new ArrayList<T>();
    m_Processed = false;
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

    m_OptionManager.add(
	    "create-dummy-report", "createDummyReport",
	    false);
  }

  /**
   * Returns whether the input needs to be a file or directory.
   *
   * @return		true if the input needs to be a file, a directory
   * 			otherwise
   */
  @Override
  public boolean isInputFile() {
    return m_InputIsFile;
  }

  /**
   * Sets the file/directory to read.
   *
   * @param value	the file/directory to read
   */
  @Override
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
  @Override
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String inputTipText() {
    if (m_InputIsFile)
      return "The file to read and turn into a container.";
    else
      return "The directory to read and turn into a container.";
  }

  /**
   * Sets whether to create a dummy report if none present.
   *
   * @param value	if true then a dummy report is generated if necessary
   */
  @Override
  public void setCreateDummyReport(boolean value) {
    m_CreateDummyReport = value;
    reset();
  }

  /**
   * Returns whether to create a dummy report if none present.
   *
   * @return		true if a dummy report is generated if necessary
   */
  @Override
  public boolean getCreateDummyReport() {
    return m_CreateDummyReport;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String createDummyReportTipText() {
    return "If true, then a dummy report is created if none present.";
  }

  /**
   * Returns the spectrums generated from the file. If necessary,
   * performs the parsing (e.g., if not yet read).
   *
   * @return		the spectrums generated from the file
   */
  @Override
  public List<T> read() {
    if (this instanceof IncrementalDataContainerReader) {
      m_ReadData.clear();
      if (!m_Processed || ((IncrementalDataContainerReader) this).hasMoreData())
	return doRead();
      else
	return m_ReadData;
    }
    else {
      if (!m_Processed)
	return doRead();
      else
	return m_ReadData;
    }
  }

  /**
   * Performs checks and (always) reads the data.
   *
   * @return		the spectrums generated from the file
   */
  protected List<T> doRead() {
    if (!m_Processed)
      checkData();
    readData();
    postProcessData();

    m_Processed = true;

    return m_ReadData;
  }

  /**
   * The default implementation only checks whether the provided file object
   * is an actual file and whether it exists (if m_InputIsFile = true), or
   * if the file object is a directory and whether it exists.
   *
   * @see	#m_InputIsFile
   */
  protected void checkData() {
    if (m_InputIsFile) {
      if (!m_Input.exists())
	throw new IllegalStateException("Input file '" + m_Input + "' does not exist!");
      if (m_Input.isDirectory())
	throw new IllegalStateException("No input file but directory provided ('" + m_Input + "')!");
    }
    else {
      if (!m_Input.exists())
	throw new IllegalStateException("Input directory '" + m_Input + "' does not exist!");
      if (!m_Input.isDirectory())
	throw new IllegalStateException("No input directory but file provided ('" + m_Input + "')!");
    }
  }

  /**
   * Performs the actual reading.
   */
  protected abstract void readData();

  /**
   * For performing post-processing.
   * <br><br>
   * Default implementation adds dummy reports.
   *
   * @see #createDummyReport(T)
   */
  protected void postProcessData() {
    int				i;
    T				cont;
    MutableReportHandler	handler;

    if (m_CreateDummyReport) {
      for (i = 0; i < m_ReadData.size(); i++) {
	cont = m_ReadData.get(i);
	if (cont instanceof MutableReportHandler) {
	  handler = (MutableReportHandler) cont;
	  if (!handler.hasReport())
	    handler.setReport(createDummyReport(cont));
	}
      }
    }
  }

  /**
   * Creates a dummy report.
   * <br><br>
   * Default implementation returns null.
   *
   * @param cont	the data container the dummy is for
   * @return		the dummy report or null
   * @see 		#m_CreateDummyReport
   * @see		#postProcessData()
   */
  protected Report createDummyReport(T cont) {
    return null;
  }

  /**
   * Can be used to free up memory. Default implementation just calls reset().
   * Derived classes can add additional code.
   *
   * @see		#reset()
   */
  @Override
  public void cleanUp() {
    reset();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  @Override
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
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
  @Override
  public DataContainerReader shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public DataContainerReader shallowCopy(boolean expand) {
    return (DataContainerReader) OptionUtils.shallowCopy(this, expand);
  }
}
