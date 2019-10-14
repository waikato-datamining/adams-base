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
 * PropertiesToCode.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionHandler;
import adams.env.ClassListerDefinition;
import adams.env.Environment;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Turns a Properties object into Java code.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesToCode
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -1486296514278761751L;

  /** the copyright string. */
  protected String m_Copyright;

  /** the package of the class. */
  protected String m_Package;

  /** the simple name of the class. */
  protected String m_SimpleName;

  /** the comment to use in the class javadoc. */
  protected String m_Comment;

  /** the indentation level. */
  protected int m_Indentation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a Properties object into Java code.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "copyright", "copyright",
      "University of Waikato, Hamilton, New Zealand");

    m_OptionManager.add(
      "package", "package",
      "adams");

    m_OptionManager.add(
      "simple-name", "simpleName",
      "Blah");

    m_OptionManager.add(
      "comment", "comment",
      "");

    m_OptionManager.add(
      "indentation", "indentation",
      2, 1, null);
  }

  /**
   * Sets the copyright entity.
   *
   * @param value	the entity owning the copyright
   */
  public void setCopyright(String value) {
    m_Copyright = value;
    reset();
  }

  /**
   * Returns the copyright entity.
   *
   * @return		the entity owning the copyright
   */
  public String getCopyright() {
    return m_Copyright;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String copyrightTipText() {
    return "The copyright string to add as a comment at the start of the class.";
  }

  /**
   * Sets the simple name (without package) of the class to generate.
   *
   * @param value	the simple name
   */
  public void setSimpleName(String value) {
    m_SimpleName = value;
    reset();
  }

  /**
   * Returns the simple name to use.
   *
   * @return		the simple name
   */
  public String getSimpleName() {
    return m_SimpleName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String simpleNameTipText() {
    return "The simple name of the class (without package prefix).";
  }

  /**
   * Sets the package name of the class to generate.
   *
   * @param value	the package name
   */
  public void setPackage(String value) {
    m_Package = value;
    reset();
  }

  /**
   * Returns the package name to use.
   *
   * @return		the package name
   */
  public String getPackage() {
    return m_Package;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String packageTipText() {
    return "The name of the package.";
  }

  /**
   * Sets the comment to use for the class javadoc.
   * If left empty, an auto-generated comment is used.
   *
   * @param value	the comment
   */
  public void setComment(String value) {
    m_Comment = value;
    reset();
  }

  /**
   * Returns the comment to use for the class javadoc.
   * If left empty, an auto-generated comment is used.
   *
   * @return		the comment
   */
  public String getComment() {
    return m_Comment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commentTipText() {
    return "The comment to use in the class javadoc; if left empty, an auto-generated comment is used.";
  }

  /**
   * Sets the indentation.
   *
   * @param value	the number of spaces
   */
  public void setIndentation(int value) {
    m_Indentation = value;
    reset();
  }

  /**
   * Returns the indentation.
   *
   * @return		the number of spaces
   */
  public int getIndentation() {
    return m_Indentation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indentationTipText() {
    return "The indentation in number of spaces to use.";
  }

  /**
   * Returns the indentation for code at the specified level.
   *
   * @param level 	the indentation level
   * @return		the indentation string
   */
  protected String getIndentation(int level) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < level * m_Indentation; i++)
      result.append(" ");

    return result.toString();
  }

  /**
   * Adds the license preamble.
   *
   * @param output 	the buffer to add to
   */
  protected void addLicensePreamble(StringBuilder output) {
    output.append("/*" + "\n");
    output.append(" *   This program is free software: you can redistribute it and/or modify" + "\n");
    output.append(" *   it under the terms of the GNU General Public License as published by" + "\n");
    output.append(" *   the Free Software Foundation, either version 3 of the License, or" + "\n");
    output.append(" *   (at your option) any later version." + "\n");
    output.append(" *" + "\n");
    output.append(" *   This program is distributed in the hope that it will be useful," + "\n");
    output.append(" *   but WITHOUT ANY WARRANTY; without even the implied warranty of" + "\n");
    output.append(" *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the" + "\n");
    output.append(" *   GNU General Public License for more details." + "\n");
    output.append(" *" + "\n");
    output.append(" *   You should have received a copy of the GNU General Public License" + "\n");
    output.append(" *   along with this program.  If not, see <http://www.gnu.org/licenses/>." + "\n");
    output.append(" */" + "\n");
    output.append("\n");
  }

  /**
   * Adds the copyright notice.
   *
   * @param output 	the buffer to add to
   */
  protected void addCopyright(StringBuilder output) {
    SimpleDateFormat yearFormatter;

    output.append("/*" + "\n");
    output.append(" * " + m_SimpleName + ".java" + "\n");
    if (m_Copyright.length() > 0) {
      yearFormatter = new SimpleDateFormat("yyyy");
      output.append(" * Copyright (C) " + yearFormatter.format(new Date()) + " " + m_Copyright + "\n");
    }
    output.append(" */" + "\n");
    output.append("\n");
  }

  /**
   * Adds the package for the generated code.
   *
   * @param output 	the buffer to add to
   */
  protected void addPackage(StringBuilder output) {
    output.append("package " + m_Package + ";" + "\n");
    output.append("\n");
  }

  /**
   * Returns the classnames that are required for the code to work.
   *
   * @return		the classes
   */
  protected List<String> getRequiredImports() {
    List<String>	result;

    result = new ArrayList<>();
    result.add(Serializable.class.getName());
    result.add(Properties.class.getName());

    return result;
  }

  /**
   * Adds the imports.
   *
   * @param output 	the buffer to add to
   */
  protected void addImports(StringBuilder output) {
    List<String> imports;

    imports = getRequiredImports();
    for (String imp: imports)
      output.append("import " + imp + ";" + "\n");
    output.append("\n");
  }

  /**
   * Adds the Javadoc for the class.
   *
   * @param output 	the buffer to add to
   */
  protected void addClassJavadoc(StringBuilder output) {
    output.append("/**\n");
    if (m_Comment.isEmpty())
      output.append(" * Properties stored as " + m_SimpleName + "." + "\n");
    else
      output.append(" * ").append(m_Comment).append("\n");
    output.append(" *\n");
    output.append(" * @author " + System.getProperty("user.name") + "\n");
    output.append(" * @author " + getClass().getName() + " (code generator)" + "\n");
    output.append(" */\n");
  }

  /**
   * Properly escapes double quotes.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String escapeDoubleQuotes(String s) {
    return Utils.backQuoteChars(Utils.backQuoteChars(s, new char[]{'"'}, new String[]{"\\\""}), new char[]{'"'}, new String[]{"\\\""});
  }

  /**
   * Adds the start of the enclosing "class" statement, including constants
   * and member variables.
   *
   * @param output 	the buffer to add to
   * @param props 	the properties to use
   * @param keys 	the ordered keys
   */
  protected void addClassStart(StringBuilder output, Properties props, List<String> keys) {
    int		i;
    String	indent;

    indent = getIndentation(1);
    output.append("public class " + m_SimpleName + " implements Serializable {\n");
    output.append("\n");
    for (i = 0; i < keys.size(); i++) {
      output.append(indent).append("/** key '").append(keys.get(i)).append("'. */\n");
      output.append(indent).append("public final static String KEY_").append(i).append(" = \"").append(escapeDoubleQuotes(keys.get(i))).append("\";\n");
      output.append("\n");
      output.append(indent).append("/** value for '").append(keys.get(i)).append("'. */\n");
      output.append(indent).append("public final static String VALUE_").append(i).append(" = \"").append(escapeDoubleQuotes(props.getProperty(keys.get(i)))).append("\";\n");
      output.append("\n");
    }
    output.append(indent).append("/** the properties object. */\n");
    output.append(indent).append("protected Properties m_Properties;\n");
    output.append("\n");
  }

  /**
   * Adds the method that returns the properties (and instantiates if necessary).
   *
   * @param output 	the buffer to add to
   * @param props 	the properties to use
   * @param keys 	the ordered keys
   */
  protected void addMethod(StringBuilder output, Properties props, List<String> keys) {
    int		i;
    String	indent;

    indent = getIndentation(3);
    output.append(getIndentation(1)).append("/**\n");
    output.append(getIndentation(1)).append(" * Returns the properties.\n");
    output.append(getIndentation(1)).append(" *\n");
    output.append(getIndentation(1)).append(" * @return the configured properties object\n");
    output.append(getIndentation(1)).append(" */\n");
    output.append(getIndentation(1)).append("public synchronized Properties getProperties() {\n");
    output.append(getIndentation(2)).append("if (m_Properties == null) {\n");
    output.append(indent).append("m_Properties = new Properties();\n");
    for (i = 0; i < keys.size(); i++)
      output.append(indent).append("m_Properties.setProperty(").append("KEY_").append(i).append(", ").append("VALUE_").append(i).append(");\n");
    output.append(getIndentation(2)).append("}\n");
    output.append(getIndentation(2)).append("return m_Properties;\n");
    output.append(getIndentation(1)).append("}\n");
  }

  /**
   * Adds the end of the enclosing "class" statement.
   *
   * @param output 	the buffer to add to
   */
  protected void addClassEnd(StringBuilder output) {
    output.append("}\n");
  }

  /**
   * Turns the properties object into Java code.
   *
   * @param props	the properties to convert
   * @return		the generated code
   */
  public String generate(Properties props) {
    StringBuilder	result;
    List<String>	keys;

    keys = new ArrayList<>(props.keySetAll());
    Collections.sort(keys);

    result = new StringBuilder();
    addLicensePreamble(result);
    addCopyright(result);
    addPackage(result);
    addImports(result);
    addClassJavadoc(result);
    addClassStart(result, props, keys);
    addMethod(result, props, keys);
    addClassEnd(result);

    return result.toString();
  }

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    Properties props = Environment.getInstance().read(ClassListerDefinition.KEY);
    PropertiesToCode propsToCode = new PropertiesToCode();
    String code = propsToCode.generate(props);
    String filename = "/home/fracpete/development/projects/adamsfamily/adams/adams-core/src/main/java/adams/Blah.java";
    if (!FileUtils.writeToFile(filename, code, false))
      System.err.println("Failed to write Java class to: " + filename);
    else
      System.out.println("Java class written to: " + filename);
  }
}
