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
 * GUIHelpProducer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.AdditionalInformationHandler;
import adams.core.ClassCrossReference;
import adams.core.ExampleProvider;
import adams.core.HelpProvider;
import adams.core.Utils;
import adams.core.io.FileFormatHandler;
import adams.core.net.HtmlUtils;
import adams.flow.core.ActorWithConditionalEquivalent;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Generates the help for the GUI, i.e., HTML output.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HtmlHelpProducer
  extends AbstractOptionProducer<String,StringBuilder>
  implements FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7520567844837662391L;

  /** the buffer for assembling the help. */
  protected StringBuilder m_OutputBuffer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates HTML 3 help output, which is used in the GUI.";
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
   * Turns the string into HTML. Line feeds are automatically converted
   * into &lt;br&gt;.
   *
   * @param s		the string to convert to HTML
   * @return		the HTML string
   * @see		HtmlUtils#markUpURLs(String, boolean)
   */
  protected String toHTML(String s) {
    return toHTML(s, false);
  }

  /**
   * Turns the string into HTML. Line feeds are automatically converted
   * into &lt;br&gt;.
   *
   * @param s		the string to convert to HTML
   * @param nbsp	whether to convert leading blanks to non-breaking spaces
   * @return		the HTML string
   * @see		HtmlUtils#markUpURLs(String, boolean)
   */
  protected String toHTML(String s, boolean nbsp) {
    String	result;
    
    if (s == null)
      return null;
    
    result = HtmlUtils.markUpURLs(s, true);
    result = HtmlUtils.convertLines(result, nbsp);
    
    return result;
  }

  /**
   * Breaks up the tool tip and adds it to the StringBuilder.
   *
   * @param option	the current option to obtain the data from
   * @param buffer	the buffer to add the tool tip to
   */
  protected void addToolTip(AbstractOption option, StringBuilder buffer) {
    String	text;

    // obtain and add text
    if (option.getToolTipMethod() != null) {
      try {
	text = (String) option.getToolTipMethod().invoke(option.getOptionHandler(), new Object[]{});
	buffer.append("<p>");
	buffer.append(toHTML(text));
	buffer.append("</p>\n");
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
	text   = Utils.arrayToString(vals).replaceAll(",", "|");
      }
      catch (Exception e) {
	e.printStackTrace();
	text = "Error retrieving enum values";
      }
    }
    else {
      text = option.getBaseClass().getName();
    }

    buffer.append(" &lt;" + toHTML(text) + "&gt;");
  }

  /**
   * Visits a boolean option.
   *
   * @param option	the boolean option
   * @return		the last internal data structure that was generated
   */
  @Override
  public StringBuilder processOption(BooleanOption option) {
    StringBuilder	result;

    result = new StringBuilder();

    result.append("<li>\n");
    result.append("<b>" + toHTML(option.getProperty()) + "</b>\n");
    result.append("<br>\n");

    // help
    addToolTip(option, result);

    // command-line
    result.append("<table border=\"1\" cellspacing=\"0\">\n");
    result.append("<tr>\n");
    result.append("<td>command-line</td>");
    result.append("<td><code>-" + toHTML(option.getCommandline()) + "</code></td>\n");
    result.append("</tr>\n");
    result.append("</table>\n");

    result.append("<br>\n");
    result.append("</li>\n");

    m_OutputBuffer.append(result);

    return result;
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
    Object			val;
    AbstractNumericOption	numeric;

    result = new StringBuilder();

    result.append("<li>\n");
    result.append("<b>" + toHTML(option.getProperty()) + "</b>\n");
    result.append("<br>\n");

    // help
    addToolTip(option, result);

    // command-line
    result.append("<table border=\"1\" cellspacing=\"0\">\n");
    result.append("<tr>\n");
    result.append("<td>command-line</td>");
    result.append("<td><code>-" + toHTML(option.getCommandline()));
    addArgumentInfo(option, result);
    if (option.isMultiple()) {
      result.append(" [");
      result.append("-" + toHTML(option.getCommandline()));
      result.append(" ...]");
    }
    result.append("</code></td>\n");
    result.append("</tr>\n");

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

      result.append("<tr>\n");
      result.append("<td>default</td>");
      result.append("<td><code>" + toHTML(Utils.backQuoteChars(text)) + "</code></td>\n");
      result.append("</tr>\n");
    }

    if (option instanceof AbstractNumericOption) {
      numeric = (AbstractNumericOption) option;
      if (numeric.hasLowerBound()) {
	result.append("<tr>\n");
	result.append("<td>minimum</td>");
	result.append("<td><code>" + numeric.getLowerBound() + "</code></td>\n");
	result.append("</tr>\n");
      }
      if (numeric.hasUpperBound()) {
	result.append("<tr>\n");
	result.append("<td>maximum</td>");
	result.append("<td><code>" + numeric.getUpperBound() + "</code></td>\n");
	result.append("</tr>\n");
      }
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
    
    if (val instanceof ExampleProvider) {
      result.append("<tr>\n");
      result.append("<td valign=\"top\">example</td>");
      result.append("<td>" + ((ExampleProvider) val).getExample() + "</td>\n");
      result.append("</tr>\n");
    }
    
    if ((val instanceof HelpProvider) && ((HelpProvider) val).getHelpURL() != null) {
      result.append("<tr>\n");
      result.append("<td valign=\"top\">more</td>");
      result.append("<td><a href=\"" + ((HelpProvider) val).getHelpURL() + "\" target=\"_blank\">" + ((HelpProvider) val).getHelpURL() + "</a></td>\n");
      result.append("</tr>\n");
    }

    result.append("</table>\n");
    result.append("<br>\n");
    result.append("</li>\n");

    m_OutputBuffer.append(result);

    return result;
  }

  /**
   * Hook method that gets called just before an option gets produced.
   * <br><br>
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
    String 	addInfo;
    Class	condEquiv;

    m_OutputBuffer = new StringBuilder();
    m_OutputBuffer.append("<html>" + "\n");
    m_OutputBuffer.append("<head>\n");
    m_OutputBuffer.append("<title>" + getInput().getClass().getName() + "<title>\n");
    m_OutputBuffer.append("</head>\n");
    m_OutputBuffer.append("\n");
    m_OutputBuffer.append("<body>\n");
    m_OutputBuffer.append("<h2>Name</h2>\n");
    m_OutputBuffer.append("<p><code>" + getInput().getClass().getName() + "</code></p>\n");
    m_OutputBuffer.append("<br>\n");
    m_OutputBuffer.append("\n");

    try {
      method = getInput().getClass().getMethod("globalInfo", new Class[0]);
      if (method != null) {
	globalInfo = (String) method.invoke(getInput(), new Object[0]);
	m_OutputBuffer.append("<h2>Synopsis</h2>\n");
	m_OutputBuffer.append("<p>" + toHTML(globalInfo, true) + "</p>\n");
	m_OutputBuffer.append("<br>\n");
	m_OutputBuffer.append("\n");
      }
    }
    catch (Exception e) {
      // ignored
    }

    if (getInput() instanceof ClassCrossReference) {
      m_OutputBuffer.append("<h2>See also</h2>\n");
      cross = ((ClassCrossReference) getInput()).getClassCrossReferences();
      m_OutputBuffer.append("<ul>\n");
      for (i = 0; i < cross.length; i++)
	m_OutputBuffer.append("<li>" + cross[i].getName() + "</li>\n");  // TODO hyperlink to class reference?
      m_OutputBuffer.append("</ul>\n");
      m_OutputBuffer.append("\n");
    }

    if (getInput() instanceof AdditionalInformationHandler) {
      addInfo = ((AdditionalInformationHandler) getInput()).getAdditionalInformation();
      if ((addInfo != null) && (addInfo.length() > 0)) {
	m_OutputBuffer.append("<h2>Additional information</h2>\n");
	m_OutputBuffer.append("<p>" + toHTML(addInfo) + "</p>\n");
	m_OutputBuffer.append("<br>\n");
	m_OutputBuffer.append("\n");
      }
    }

    if (getInput() instanceof ActorWithConditionalEquivalent) {
      condEquiv = ((ActorWithConditionalEquivalent) getInput()).getConditionalEquivalent();
      if (condEquiv != null) {
	m_OutputBuffer.append("<h2>Conditional equivalent</h2>\n");
	m_OutputBuffer.append("<p>" + toHTML(condEquiv.getName()) + "</p>\n");
	m_OutputBuffer.append("<br>\n");
	m_OutputBuffer.append("\n");
      }
    }

    m_OutputBuffer.append("<h2>Options</h2>\n");
    m_OutputBuffer.append("<ul>\n");
  }

  /**
   * Hook-method after visiting options.
   * <br><br>
   * Default implementation does nothing.
   */
  @Override
  protected void postProduce() {
    m_OutputBuffer.append("</ul>\n");
    m_OutputBuffer.append("</body>\n");
    m_OutputBuffer.append("</html>\n");
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
   * Returns the description of the file format.
   *
   * @return		the description
   */
  public String getFormatDescription() {
    return "HTML Help";
  }

  /**
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  public String getDefaultFormatExtension() {
    return "html";
  }

  /**
   * Returns the file extensions (without the dot).
   *
   * @return		the extensions
   */
  public String[] getFormatExtensions() {
    return new String[]{"html", "htm"};
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(HtmlHelpProducer.class, args);
  }
}
