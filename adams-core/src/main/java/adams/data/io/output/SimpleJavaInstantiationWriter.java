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
 * SimpleJavaInstantiationWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractCommandLineHandler;
import adams.core.option.OptionUtils;
import adams.data.io.input.AbstractObjectReader;

/**
 * Generates Java code from objects, using their command-line options to instantiate them.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleJavaInstantiationWriter
  extends AbstractObjectWriter {

  private static final long serialVersionUID = 7242878829736390245L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates Java code from objects, using their command-line options to instantiate them.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Java source code (simple)";
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
   * Performs the actual writing of the object file.
   *
   * @param file	the file to write to
   * @param obj	        the object to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, Object obj) {
    String			result;
    StringBuilder		code;
    String			simple;
    String[]			options;
    int				i;
    AbstractCommandLineHandler	handler;

    result   = null;
    simple   = FileUtils.replaceExtension(file.getName(), "");
    handler  = AbstractCommandLineHandler.getHandler(obj);
    options  = OptionUtils.getOptions(obj);
    for (i = 0; i < options.length; i++)
      options[i] = "\"" + Utils.backQuoteChars(options[i]) + "\"";

    code = new StringBuilder();
    code.append("package adams;\n");
    code.append("\n");

    code.append("public class ");
    code.append(simple);
    code.append(" {\n");
    code.append("  \n");

    code.append("  public static void main(String[] args) throws Exception {\n");

    code.append("    ");
    code.append(obj.getClass().getName());
    code.append(" ");
    code.append(simple.toLowerCase());
    code.append(" = new ");
    code.append(obj.getClass().getName());
    code.append("();\n");

    code.append("    ");
    code.append(handler.getClass().getName());
    code.append(" handler = new ");
    code.append(handler.getClass().getName());
    code.append("();\n");

    code.append("    ");
    code.append("handler.setOptions(");
    code.append(simple.toLowerCase());
    code.append(", new String[]{");
    code.append(Utils.flatten(options, ", "));
    code.append("});\n");

    code.append("  }\n");
    code.append("}\n");

    if (!FileUtils.writeToFile(file.getAbsolutePath(), code, false))
      result = "Failed to write Java code to: " + file;

    return result;
  }
}
