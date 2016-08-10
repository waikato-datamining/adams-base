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
 * NestedProducer.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.management.Java;
import adams.core.option.NestedFormatHelper.Line;
import adams.env.Environment;
import adams.env.Modules;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Generates the nested format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedProducer
  extends AbstractRecursiveOptionProducer<List,List>
  implements BlacklistedOptionProducer, EncodingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7096746086060792830L;

  /** the line comment character in files storing nested option handlers. */
  public final static String COMMENT = "#";

  /** the information about the project. */
  public static final String PROJECT = "Project";

  /** the information on the date. */
  public static final String DATE = "Date";

  /** the information on the user. */
  public static final String USER = "User";

  /** the information on the character set used. */
  public static final String CHARSET = "Charset";

  /** the information on the modules used. */
  public static final String MODULES = "Modules";

  /** the information on the class path. */
  public static final String CLASS_PATH = "Class-Path";

  /** the characters to backquote. */
  public static final char[] BACKQUOTE_CHARS = new char[]{'\r', '\n', '\t'};

  /** the backquoted stringsf. */
  public static final String[] BACKQUOTE_STRINGS = new String[]{"\\r", "\\n", "\\t"};

  /** blacklisted classes. */
  protected Class[] m_Blacklisted;

  /** whether to suppress the prolog. */
  protected boolean m_OutputProlog;

  /** whether to print the classpath. */
  protected boolean m_OutputClasspath;

  /** whether to print the modules. */
  protected boolean m_OutputModules;

  /** whether to print line numbers. */
  protected boolean m_OutputLineNumbers;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** for formatting dates. */
  protected static DateFormat m_DateFormat;
  static {
    m_DateFormat = new DateFormat("yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the nested format (tab indentation in string representation, nested ArrayList objects in object representation).";
  }

  /**
   * Used for initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Blacklisted       = new Class[0];
    m_OutputProlog      = true;
    m_OutputClasspath   = false;
    m_OutputModules     = false;
    m_OutputLineNumbers = false;
    m_Encoding          = new BaseCharset();
  }

  /**
   * Sets the classes to avoid.
   *
   * @param value	the classes
   */
  @Override
  public void setBlacklisted(Class[] value) {
    if (value == null)
      value = new Class[0];
    m_Blacklisted = value;
  }

  /**
   * Returns the blacklisted classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getBlacklisted() {
    return m_Blacklisted;
  }

  /**
   * Sets whether to output the prolog (in comments) or not.
   *
   * @param value	if true then the prolog is generated
   */
  public void setOutputProlog(boolean value) {
    m_OutputProlog = value;
  }

  /**
   * Returns whether the prolog (comments) is generated.
   *
   * @return		true if the prolog is generated
   */
  public boolean getOutputProlog() {
    return m_OutputProlog;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputPrologTipText() {
    return "Whether to output the prolog with comments about software version, date/time of generation, etc.";
  }

  /**
   * Sets whether to output the classpath (in comments) or not.
   *
   * @param value	if true then the classpath is generated
   */
  public void setOutputClasspath(boolean value) {
    m_OutputClasspath = value;
  }

  /**
   * Returns whether the classpath (comments) is generated.
   *
   * @return		true if the classpath is generated
   */
  public boolean getOutputClasspath() {
    return m_OutputClasspath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputClasspathTipText() {
    return "Whether to output the classpath in the comments as well.";
  }

  /**
   * Sets whether to output the modules in the comments.
   *
   * @param value	if true then the modules are output
   */
  public void setOutputModules(boolean value) {
    m_OutputModules = value;
  }

  /**
   * Returns whether the modules are output in comments.
   *
   * @return		true if the modules are output
   */
  public boolean getOutputModules() {
    return m_OutputModules;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputModulesTipText() {
    return "Whether to output the modules in the comments as well.";
  }

  /**
   * Sets whether to output the line numbers.
   *
   * @param value	if true then the lines get prefixed with line numbers
   */
  public void setOutputLineNumbers(boolean value) {
    m_OutputLineNumbers = value;
  }

  /**
   * Returns whether line numbers get output.
   *
   * @return		true if lines get prefixed with line numbers
   */
  public boolean getOutputLineNumbers() {
    return m_OutputLineNumbers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputLineNumbersTipText() {
    return "Whether to prefix each line with the line number.";
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when writing the file, use empty string for default.";
  }

  /**
   * Initializes the output data structure.
   *
   * @return		the created data structure
   */
  @Override
  protected List initOutput() {
    return new ArrayList();
  }

  /**
   * Visits a boolean option.
   *
   * @param option	the boolean option
   * @return		the last internal data structure that was generated
   */
  @Override
  public List processOption(BooleanOption option) {
    return processOption((AbstractArgumentOption) option);
  }

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		the last internal data structure that was generated
   */
  @Override
  public List processOption(ClassOption option) {
    ArrayList			result;
    Object			currValue;
    Object			currValues;
    Object			value;
    int				i;
    ArrayList			nested;
    ArrayList			nestedDeeper;
    AbstractCommandLineHandler	handler;

    result = new ArrayList();

    if (option.isVariableAttached() && !m_OutputVariableValues) {
      result.add(new Line(getOptionIdentifier(option)));
      result.add(new Line(option.getVariable()));
    }
    else {
      currValue = getCurrentValue(option);

      if (!isDefaultValue(option, currValue)) {
	currValues = null;

	if (currValue != null) {
	  if (!option.isMultiple()) {
	    currValues = Array.newInstance(option.getBaseClass(), 1);
	    Array.set(currValues, 0, currValue);
	  }
	  else {
	    currValues = currValue;
	  }

	  for (i = 0; i < Array.getLength(currValues); i++) {
	    value = Array.get(currValues, i);
	    result.add(new Line(getOptionIdentifier(option)));
	    nested = new ArrayList();
	    result.add(nested);
	    nested.add(new Line(value.getClass().getName()));
	    nestedDeeper = new ArrayList();
	    nested.add(nestedDeeper);
	    if (value instanceof OptionHandler) {
	      m_Nesting.push(nested);
	      m_Nesting.push(nestedDeeper);
	      doProduce(((OptionHandler) value).getOptionManager());
	      m_Nesting.pop();
	      m_Nesting.pop();
	    }
	    else {
	      handler = AbstractCommandLineHandler.getHandler(value);
	      for (String line: handler.getOptions(value))
                nestedDeeper.add(new Line(Utils.backQuoteChars(line, BACKQUOTE_CHARS, BACKQUOTE_STRINGS)));
	    }
	  }
	}
      }
    }

    if (m_Nesting.empty())
      m_Output.addAll(result);
    else
      m_Nesting.peek().addAll(result);

    return result;
  }

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		the last internal data structure that was generated
   */
  @Override
  public List processOption(AbstractArgumentOption option) {
    ArrayList	result;
    Object	currValue;
    Object	currValues;
    int		i;

    result = new ArrayList();

    if (option.isVariableAttached() && !m_OutputVariableValues) {
      result.add(new Line(getOptionIdentifier(option)));
      result.add(new Line(option.getVariable()));
    }
    else {
      currValue = getCurrentValue(option);

      if (!isDefaultValue(option, currValue)) {
	currValues = null;

	if (currValue != null) {
	  if (!option.isMultiple()) {
	    currValues = Array.newInstance(option.getBaseClass(), 1);
	    Array.set(currValues, 0, currValue);
	  }
	  else {
	    currValues = currValue;
	  }

	  for (i = 0; i < Array.getLength(currValues); i++) {
	    result.add(new Line(getOptionIdentifier(option)));
	    result.add(new Line(option.toString(Array.get(currValues, i))));
	  }
	}
      }
    }

    if (result.size() > 0) {
      if (m_Nesting.empty())
	m_Output.addAll(result);
      else
	m_Nesting.peek().addAll(result);
    }

    return result;
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public String toString() {
    StringBuilder	result;
    List<String>	lines;
    int			i;
    List		nested;

    try {
      result = new StringBuilder();

      // create nested structure
      nested = getOutput();
      if (m_OutputLineNumbers)
	NestedFormatHelper.renumber(nested);
      lines = NestedFormatHelper.nestedToLines(nested, m_OutputLineNumbers);

      // add meta-data
      if (m_OutputProlog) {
	result.append(COMMENT + " " + PROJECT + ": " + Environment.getInstance().getProject() + "\n");
	result.append(COMMENT + " " + DATE + ": " + m_DateFormat.format(new Date()) + "\n");
	result.append(COMMENT + " " + USER + ": " + System.getProperty("user.name") + "\n");
        result.append(COMMENT + " " + CHARSET + ": " + m_Encoding.charsetValue().name() + "\n");
	if (m_OutputModules)
	  result.append(COMMENT + " " + MODULES + ": " + Utils.flatten(Modules.getSingleton().getModules(), ",") + "\n");
        if (m_OutputClasspath)
          result.append(COMMENT + " " + CLASS_PATH + ": " + Java.getClassPath(true) + "\n");
	result.append(COMMENT + "\n");
      }

      // add actual data
      for (i = 0; i < lines.size(); i++) {
	result.append(lines.get(i));
	result.append("\n");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    if (result == null)
      return null;
    else
      return result.toString();
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    ArrayList	nested;

    super.preProduce();

    m_Output.clear();
    m_Output.add(new Line(m_Input.getClass().getName()));
    nested = new ArrayList();
    m_Output.add(nested);

    m_Nesting.push(nested);
  }

  /**
   * Visits the option and obtains information from it.
   *
   * @param option	the current option
   * @return		the last internal data structure that was generated
   */
  @Override
  public List doProduce(AbstractOption option) {
    int		i;

    for (i = 0; i < m_Blacklisted.length; i++) {
      if (option.getReadMethod().getReturnType() == m_Blacklisted[i])
	return null;
    }

    return super.doProduce(option);
  }

  /**
   * Hook-method after visiting options.
   */
  @Override
  protected void postProduce() {
    super.postProduce();

    m_Nesting.pop();
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(NestedProducer.class, args);
  }
}
