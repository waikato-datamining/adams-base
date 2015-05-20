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

/**
 * AbstractImageReader.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.ClassLister;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;
import adams.data.io.output.AbstractImageWriter;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 * Ancestor for image readers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageReader<T extends AbstractImageContainer>
  extends AbstractOptionHandler
  implements FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2069846153567151035L;
  
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
   * @return 			the extension (without the dot!)
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
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public abstract AbstractImageWriter getCorrespondingWriter();
  
  /**
   * Returns whether the reader is actually available.
   * 
   * @return		true if available and ready to use
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Performs checks.
   * 
   * @param file	the image file to check
   */
  protected void check(PlaceholderFile file) {
    if (file == null)
      throw new IllegalStateException("No file provided!");
    if (!file.exists())
      throw new IllegalStateException("File '" + file + "' does not exist!");
    if (file.isDirectory())
      throw new IllegalStateException("File '" + file + "' points to a directory!");
  }
  
  /**
   * Performs the actual reading of the image file.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  protected abstract T doRead(PlaceholderFile file);
  
  /**
   * Performs some post-processing on the container.
   * 
   * @param cont	the container to post-process
   * @param file	the file that was read
   * @return		the (potentially) updated container
   */
  protected T postProcess(T cont, PlaceholderFile file) {
    Report	report;
    
    if (cont.hasReport()) {
      report = cont.getReport();
      report.addField(new Field(AbstractImageContainer.FIELD_FILENAME, DataType.STRING));
      report.setStringValue(AbstractImageContainer.FIELD_FILENAME, file.getAbsolutePath());
      report.addField(new Field(AbstractImageContainer.FIELD_PATH, DataType.STRING));
      report.setStringValue(AbstractImageContainer.FIELD_PATH, file.getParentFile().getAbsolutePath());
      report.addField(new Field(AbstractImageContainer.FIELD_NAME, DataType.STRING));
      report.setStringValue(AbstractImageContainer.FIELD_NAME, file.getName());
    }
    
    return cont;
  }
  
  /**
   * Reads the image file.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  public T read(PlaceholderFile file) {
    T		result;
    
    check(file);
    result = doRead(file);
    if (result != null)
      result = postProcess(result, file);
    
    return result;
  }

  /**
   * Returns a list with classnames of readers.
   *
   * @return		the reader classnames
   */
  public static String[] getReaders() {
    return ClassLister.getSingleton().getClassnames(AbstractImageReader.class);
  }
}
