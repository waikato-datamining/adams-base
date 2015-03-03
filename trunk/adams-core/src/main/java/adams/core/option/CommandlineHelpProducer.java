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
 * CommandlineHelpProducer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import adams.core.ClassCrossReference;
import adams.core.EnumWithCustomDisplay;
import adams.core.ExampleProvider;
import adams.core.HelpProvider;
import adams.core.Utils;

/**
 * Generates the help for the command-line.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CommandlineHelpProducer
  extends AbstractOptionProducer<String,StringBuilder> {

  /** for serialization. */
  private static final long serialVersionUID = 4154358361484863539L;

  /** the maximum width for the option text (80 - TAB = 72). */
  public final static int MAX_WIDTH = 72;

  /** the separator for enums. */
  public final static String ENUM_SEPARATOR = "|";
  
  /** the buffer for assembling the help. */
  protected StringBuilder m_OutputBuffer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates help output, that is output on the command-line.";
  }

  /**
   * Initializes the output data structure.
   *
   * @return		the created data structure
   */
  @Override
  protected String initOutput() {
    return "";
  }

  /**
   * Initializes the visitor.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_OutputBuffer = new StringBuilder();
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output
   */
  @Override
  public String getOutput() {
    if (m_Output == null)
      m_Output = m_OutputBuffer.toString();

    return m_Output;
  }

  /**
   * Breaks up the tool tip and adds it to the StringBuilder.
   *
   * @param option	the current option to obtain the data from
   * @param buffer	the buffer to add the tool tip to
   */
  protected void addToolTip(AbstractOption option, StringBuilder buffer) {
    String[]	lines;
    String	text;
    int		i;

    // obtain and add text
    if (option.getToolTipMethod() != null) {
      try {
	text  = (String) option.getToolTipMethod().invoke(option.getOptionHandler(), new Object[]{});
	lines = Utils.breakUp(text, MAX_WIDTH);
	for (i = 0; i < lines.length; i++) {
	  buffer.append("\t");
	  buffer.append(lines[i]);
	  buffer.append("\n");
	}
      }
      catch (Exception e) {
	// this should never happen!
	e.printStackTrace();
      }
    }
  }

  /**
   * Adds additional information about the argument, e.g., the class.
   *
   * @param option	the current option to obtain the data from
   * @param buffer	the buffer to add the information to
   */
  protected void addArgumentInfo(AbstractArgumentOption option, StringBuilder buffer) {
    String	text;
    Method	method;
    Object[]	vals;

    if (option instanceof EnumOption) {
      text = "";
      try {
	method = option.getBaseClass().getMethod("values", new Class[0]);
	vals   = (Object[]) method.invoke(null, new Object[0]);
	if (option.getCurrentValue() instanceof EnumWithCustomDisplay) {
	  for (Object obj: vals) {
	    if (text.length() > 0)
	      text += ENUM_SEPARATOR;
	    text += ((EnumWithCustomDisplay) obj).toRaw();
	  }
	}
	else {
	  text = Utils.flatten(vals, ENUM_SEPARATOR);
	}
      }
      catch (Exception e) {
	e.printStackTrace();
	text = "Error retrieving enum values";
      }
    }
    else {
      text = option.getBaseClass().getName();
    }

    buffer.append(" <" + text + ">");
  }

  /**
   * Visits a boolean option.
   *
   * @param option	the boolean option
   * @return		the last internal data structure that was generated
   */
  @Override
  public StringBuilder processOption(BooleanOption option) {
    return processOption((AbstractArgumentOption) option);
  }

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		the last internal data structure that was generated
   */
  @Override
  public StringBuilder processOption(ClassOption option) {
    return processOption((AbstractArgumentOption) option);
  }

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		the last internal data structure that was generated
   */
  @Override
  public StringBuilder processOption(AbstractArgumentOption option) {
    StringBuilder		result;
    String			text;
    int				n;
    Object			defValue;
    AbstractNumericOption	numeric;
    Object			val;

    result = new StringBuilder();

    result.append("-" + option.getCommandline());

    addArgumentInfo(option, result);

    if (option.isMultiple()) {
      result.append(" [");
      result.append("-" + option.getCommandline());
      result.append(" ...]");
    }

    result.append(" (property: " + option.getProperty() + ")");
    result.append("\n");

    // help text
    addToolTip(option, result);

    // add default value
    defValue = option.getDefaultValue();
    if (option.getOutputDefaultValue()) {
      text = null;

      if (defValue == null) {
	text = "null";
      }
      else if (option.isMultiple()) {
	text = "";
	for (n = 0; n < Array.getLength(defValue); n++) {
	  if (n > 0)
	    text += ", ";
	  text += option.toString(Array.get(defValue, n));
	}
      }
      else {
	text = option.toString(defValue);
      }

      result.append("\tdefault: " + Utils.backQuoteChars(text) + "\n");
    }

    if (option instanceof AbstractNumericOption) {
      numeric = (AbstractNumericOption) option;
      if (numeric.hasLowerBound())
	result.append("\tminimum: " + numeric.getLowerBound() + "\n");
      if (numeric.hasUpperBound())
	result.append("\tmaximum: " + numeric.getUpperBound() + "\n");
    }
    
    val = null;
    if (option.isMultiple()) {
      if (Array.getLength(defValue) > 0) {
	val = Array.get(defValue, 0);
      }
      else {
	try {
	  val = option.getBaseClass().newInstance();
	}
	catch (Exception e) {
	  val = null;
	}
      }
    }
    else {
      val = defValue;
    }
    
    if (val instanceof ExampleProvider)
      result.append("\texample: " + ((ExampleProvider) val).getExample() + "\n");
    
    if ((val instanceof HelpProvider) && (((HelpProvider) val).getHelpURL() != null))
      result.append("\tmore: " + ((HelpProvider) val).getHelpURL() + "\n");

    m_OutputBuffer.append(result);
    m_OutputBuffer.append("\n");

    return result;
  }

  /**
   * Hook method that gets called just before an option gets produced.
   * <p/>
   * Default implementation does nothing
   *
   * @param manager	the option manager
   * @param index	the index of the option
   */
  @Override
  protected void preProduce(OptionManager manager, int index) {
    super.preProduce(manager, index);

    m_Output = null;
  }

  /**
   * Hook-method before starting visiting options. Adds header and global
   * info to the output buffer.
   */
  @Override
  protected void preProduce() {
    Method	method;
    String	globalInfo;
    Class[]	cross;
    int		i;

    m_OutputBuffer = new StringBuilder();
    m_OutputBuffer.append("Command-line help" + "\n");
    m_OutputBuffer.append("=================" + "\n");
    m_OutputBuffer.append("\n");
    m_OutputBuffer.append(getInput().getClass().getName() + "\n");
    m_OutputBuffer.append("\n");

    try {
      method = getInput().getClass().getMethod("globalInfo", new Class[0]);
      if (method != null) {
	globalInfo = (String) method.invoke(getInput(), new Object[0]);
	m_OutputBuffer.append(Utils.insertLineBreaks(globalInfo, MAX_WIDTH));
	m_OutputBuffer.append("\n");
      }
      if (getInput() instanceof ClassCrossReference) {
	m_OutputBuffer.append("See also:\n");
	cross = ((ClassCrossReference) getInput()).getClassCrossReferences();
	for (i = 0; i < cross.length; i++)
	  m_OutputBuffer.append(cross[i].getName() + "\n");
	m_OutputBuffer.append("\n");
      }
    }
    catch (Exception e) {
      // ignored
    }
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public String toString() {
    return getOutput();
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(CommandlineHelpProducer.class, args);
  }
}
