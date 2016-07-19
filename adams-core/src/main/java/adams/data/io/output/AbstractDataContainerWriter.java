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
 * AbstractDataContainerWriter.java
 * Copyright (C) 2008-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.AdditionalInformationHandler;
import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract ancestor for writers that write data containers to files in various
 * formats.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to handle
 */
public abstract class AbstractDataContainerWriter<T extends DataContainer>
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler, ShallowCopySupporter<AbstractDataContainerWriter>,
             FileFormatHandler, AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7097110023547675936L;

  /** the file to write to. */
  protected PlaceholderFile m_Output;

  /** indicates whether the output has to be a file (= default) or directory. */
  protected boolean m_OutputIsFile;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
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
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    StringBuilder	result;

    result = new StringBuilder();

    result.append("Supported file extensions: " + Utils.flatten(getFormatExtensions(), ", "));
    result.append("\n");
    result.append("Default file extension: " + getDefaultFormatExtension());

    return result.toString();
  }

  /**
   * Resets the writer (but does not clear the input data!). Derived classes
   * must call this method in set-methods of parameters to assure the
   * invalidation of previously generated data.
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
	    "output", "output",
	    new PlaceholderFile(
              TempUtils.createTempFile("out.tmp")));
  }

  /**
   * Returns whether the output needs to be a file or directory.
   *
   * @return true if the output needs to be a file, a directory otherwise
   */
  public boolean isOutputFile() {
    return m_OutputIsFile;
  }

  /**
   * Sets the file/directory to write to.
   *
   * @param value
   *          the file/directory to write to
   */
  public void setOutput(PlaceholderFile value) {
    if (value == null)
      m_Output = new PlaceholderFile(".");
    else
      m_Output = value;
  }

  /**
   * The file/directory to write to.
   *
   * @return the file/directory to write to
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String outputTipText() {
    if (m_OutputIsFile)
      return "The file to write the container to.";
    else
      return "The directory to write the container to.";
  }
  
  /**
   * Returns whether writing of multiple containers is supported.
   * 
   * @return 		true if multiple containers are supported
   */
  public abstract boolean canWriteMultiple();

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   * @see		#write(List)
   */
  public boolean write(T data) {
    List<T>	list;

    list = new ArrayList<T>();
    list.add(data);

    return write(list);
  }

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  public boolean write(List<T> data) {
    checkData(data);
    return writeData(data);
  }

  /**
   * The default implementation only checks whether the provided file is an
   * actual file and whether it exists (if m_OutputIsFile is TRUE). Otherwise
   * the directory has to exist.
   *
   * @param data	the data to write
   */
  protected void checkData(List<T> data) {
    if (!canWriteMultiple() && data.size() > 1)
      throw new IllegalStateException(getClass().getName() + " does not support writing of multiple containers!");
    
    if (m_OutputIsFile) {
      if (m_Output.isDirectory())
	throw new IllegalStateException(
	    "No output file but directory provided ('" + m_Output + "')!");
      if (!m_Output.getParentFile().exists())
	throw new IllegalStateException("Output file's directory '"
	    + m_Output.getParentFile() + "' does not exist!");
    }
    else {
      if (!m_Output.isDirectory())
	throw new IllegalStateException("Output is not a  directory ('"
	    + m_Output + "')!");
      if (!m_Output.exists())
	throw new IllegalStateException("Output directory '" + m_Output
	    + "' does not exist!");
    }
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  protected abstract boolean writeData(List<T> data);

  /**
   * Can be used to free up memory. Default implementation just calls reset()
   * and sets the spectrum to null. Derived classes can add additional code.
   *
   * @see #reset()
   */
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
   * Compares this object with the specified object for order. Returns a
   * negative integer, zero, or a positive integer as this object is less than,
   * equal to, or greater than the specified object. <br><br> Only compares the
   * commandlines of the two objects.
   *
   * @param o
   *          the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *         less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException
   *           if the specified object's type prevents it from being compared to
   *           this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same. <br><br> Only compares the
   * commandlines of the two objects.
   *
   * @param o
   *          the object to be compared
   * @return true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return the shallow copy
   */
  public AbstractDataContainerWriter shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return 		the shallow copy
   */
  public AbstractDataContainerWriter shallowCopy(boolean expand) {
    return (AbstractDataContainerWriter) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(
	AbstractDataContainerWriter.class);
  }

  /**
   * Instantiates the spectrum writer with the given options.
   *
   * @param classname
   *          the classname of the writer to instantiate
   * @param options
   *          the options for the writer
   * @return the instantiated writer or null if an error occurred
   */
  public static AbstractDataContainerWriter forName(String classname,
      String[] options) {
    AbstractDataContainerWriter result;

    try {
      result = (AbstractDataContainerWriter) OptionUtils.forName(
	  AbstractDataContainerWriter.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the spectrum writer from the given commandline (i.e.,
   * classname and optional options).
   *
   * @param cmdline
   *          the classname (and optional options) of the writer to instantiate
   * @return the instantiated writer or null if an error occurred
   */
  public static AbstractDataContainerWriter forCommandLine(String cmdline) {
    return (AbstractDataContainerWriter) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
