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
 * JavaInstantiationProducer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.option;

import adams.core.Utils;
import adams.core.io.FileFormatHandler;
import adams.env.Environment;

/**
 * Generates Java code for instantiating the object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JavaInstantiationProducer
  extends AbstractJavaCodeProducer
  implements FileFormatHandler {

  private static final long serialVersionUID = 7403978835300503452L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates Java code for instantiating the object.";
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
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  public String getDefaultFormatExtension() {
    return "java";
  }

  /**
   * Returns the file extensions (without the dot).
   *
   * @return		the extensions
   */
  public String[] getFormatExtensions() {
    return new String[]{getDefaultFormatExtension()};
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"simple-name", "simpleName",
	"Blah");

    m_OptionManager.add(
      "package", "package",
      "adams");
  }

  /**
   * Sets the simple name (without package) of the class to generate.
   *
   * @param value	the simple name
   */
  @Override
  public void setSimpleName(String value) {
    super.setSimpleName(value);
  }

  /**
   * Returns the simple name to use.
   *
   * @return		the simple name
   */
  @Override
  public String getSimpleName() {
    return super.getSimpleName();
  }

  /**
   * Sets the package name of the class to generate.
   *
   * @param value	the package name
   */
  @Override
  public void setPackage(String value) {
    super.setPackage(value);
  }

  /**
   * Returns the package name to use.
   *
   * @return		the package name
   */
  @Override
  public String getPackage() {
    return super.getPackage();
  }

  /**
   * The outer most variable name.
   *
   * @return		the variable name
   */
  @Override
  protected String getOuterVariableName() {
    return m_Input.getClass().getSimpleName().toLowerCase();
  }

  /**
   * Returns the indentation for code inside the try-catch-block.
   *
   * @return		the indentation string
   */
  @Override
  protected String getIndentation() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder("    ");
    for (i = 0; i < m_Indentation; i++)
      result.append("  ");

    return result.toString();
  }

  /**
   * Adds the method that encloses the generated code.
   */
  @Override
  protected void addMethodStart() {
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Used to create an instance of a specific class.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @return a suitably configured <code>" + Utils.classToString(getInput()) + "</code> value\n");
    m_OutputBuffer.append("   * @throws Exception if set up fails\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public " + Utils.classToString(getInput()) + " getInstance() throws Exception {\n");
    m_OutputBuffer.append("    AbstractArgumentOption argOption;\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    " + m_Input.getClass().getName() + " " + getOuterVariableName() + " = new " + m_Input.getClass().getName() + "();\n");
    m_OutputBuffer.append("    \n");
  }

  /**
   * Closes the method with the generated code.
   */
  @Override
  protected void addMethodEnd() {
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    return " + getOuterVariableName() + ";\n");
    m_OutputBuffer.append("  }\n");
  }

  /**
   * Hook method for adding a main method.
   * <br><br>
   * Default implementation merely instantiates a new instance of the class.
   */
  protected void addMainMethod() {
    m_OutputBuffer.append("  \n");
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Only for testing.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param args ignored\n");
    m_OutputBuffer.append("   * @throws Exception if set up fails\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public static void main(String[] args) throws Exception {\n");
    m_OutputBuffer.append("    Environment.setEnvironmentClass(" + Environment.getEnvironmentClass().getName() + ".class);\n");
    m_OutputBuffer.append("    " + m_SimpleName + " " + m_SimpleName.toLowerCase() + " = new " + m_SimpleName + "();\n");
    m_OutputBuffer.append("    " + m_Input.getClass().getName() + " instance = " + m_SimpleName.toLowerCase() + ".getInstance();\n");
    m_OutputBuffer.append("  }\n");
  }

  /**
   * Executes the producer from commandline.
   *
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(JavaInstantiationProducer.class, args);
  }
}
