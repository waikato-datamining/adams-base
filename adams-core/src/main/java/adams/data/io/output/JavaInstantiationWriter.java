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
 * JavaInstantiationWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.JavaInstantiationProducer;
import adams.core.option.OptionHandler;
import adams.data.io.input.AbstractObjectReader;

/**
 * Generates Java code from {@link OptionHandler} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JavaInstantiationWriter
  extends AbstractObjectWriter {

  private static final long serialVersionUID = 7242878829736390245L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates Java code from " + Utils.classToString(OptionHandler.class) + " objects.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Java source code";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"java"};
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractObjectReader getCorrespondingReader() {
    return null;
  }

  /**
   * Performs checks.
   *
   * @param obj	the object to check
   */
  protected void check(Object obj) {
    super.check(obj);
    if (!(obj instanceof OptionHandler))
      throw new IllegalStateException("Object does not implement " + Utils.classToString(OptionHandler.class));
  }

  /**
   * Performs the actual writing of the object file.
   *
   * @param file	the file to write to
   * @param obj	        the object to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, Object obj) {
    String			result;
    JavaInstantiationProducer	producer;
    String			code;

    result   = null;
    producer = new JavaInstantiationProducer();
    producer.setSimpleName(FileUtils.replaceExtension(file.getName(), ""));
    code     = producer.produce((OptionHandler) obj);

    if (code != null) {
      if (!FileUtils.writeToFile(file.getAbsolutePath(), code, false))
	result = "Failed to write Java code to: " + file;
    }

    return result;
  }
}
