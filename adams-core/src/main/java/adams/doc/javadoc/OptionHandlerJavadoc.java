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
 * OptionHandlerJavadoc.java
 * Copyright (C) 2006 University of Waikato, Hamilton, New Zealand
 */

package adams.doc.javadoc;


import java.util.List;

import adams.core.ClassLocator;
import adams.core.option.AbstractOption;
import adams.core.option.CommandlineHelpProducer;
import adams.core.option.OptionHandler;

/**
 * Generates Javadoc comments from the OptionHandler's options. Can
 * automatically update the option comments if they're surrounded by
 * the OPTIONS_STARTTAG and OPTIONS_ENDTAG (the indention is determined via
 * the OPTIONS_STARTTAG). <br><br>
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-env &lt;java.lang.String&gt; (property: environment)
 *         The class to use for determining the environment.
 *         default: adams.core.Environment
 * </pre>
 *
 * <pre>-W &lt;java.lang.String&gt; (property: classname)
 *         The class to load.
 *         default: adams.doc.AllJavadoc
 * </pre>
 *
 * <pre>-nostars (property: useStars)
 *         Controls the use of '*' in the Javadoc.
 * </pre>
 *
 * <pre>-dir &lt;java.lang.String&gt; (property: dir)
 *         The directory above the package hierarchy of the class.
 *         default: .
 * </pre>
 *
 * <pre>-silent (property: silent)
 *         Suppresses printing in the console.
 * </pre>
 *
 * <pre>-noprolog (property: Prolog)
 *         Controls the 'Valid options are...' prolog in the Javadoc.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #OPTIONS_STARTTAG
 * @see #OPTIONS_ENDTAG
 * @see weka.core.OptionHandlerJavadoc
 */
public class OptionHandlerJavadoc
  extends Javadoc {

  /** for serialization. */
  private static final long serialVersionUID = 6411696179436469435L;

  /** the start comment tag for inserting the generated Javadoc. */
  public final static String OPTIONS_STARTTAG = "<!-- options-start -->";

  /** the end comment tag for inserting the generated Javadoc. */
  public final static String OPTIONS_ENDTAG = "<!-- options-end -->";

  /** whether to include the "Valid options..." prolog in the Javadoc. */
  protected boolean m_Prolog;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_StartTag    = new String[1];
    m_EndTag      = new String[1];
    m_IsBlock     = new boolean[1];
    m_StartTag[0] = OPTIONS_STARTTAG;
    m_EndTag[0]   = OPTIONS_ENDTAG;
    m_IsBlock[0]  = true;
    m_Prolog      = true;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"noprolog", "Prolog", false);
  }

  /**
   * sets whether to add the "Valid options are..." prolog.
   *
   * @param value	true if the prolog is to be added
   */
  public void setProlog(boolean value) {
    m_Prolog = value;
  }

  /**
   * whether "Valid options are..." prolog is included in the Javadoc.
   *
   * @return		true if the prolog is printed
   */
  public boolean getProlog() {
    return m_Prolog;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String PrologTipText() {
    return "Controls the 'Valid options are...' prolog in the Javadoc.";
  }

  /**
   * Generates valid HTML Javadoc from the given option.
   *
   * @param option	the option to generate Javadoc for
   * @return		the generated Javadoc
   */
  protected String generateJavadoc(String option) {
    StringBuilder	result;
    String		line;
    String[]		lines;
    int			i;

    lines  = option.split("\n");
    result = new StringBuilder();
    for (i = 0; i < lines.length; i++) {
      line = toHTML(lines[i]);
      if (i > 0)
	line = line.replaceAll("\\t", "        ");
      result.append(line + "\n");
    }

    return result.toString();
  }

  /**
   * generates and returns the Javadoc for the specified start/end tag pair.
   *
   * @param index	the index in the start/end tag array
   * @return		the generated Javadoc
   * @throws Exception 	in case the generation fails
   */
  @Override
  protected String generateJavadoc(int index) throws Exception {
    String			result;
    OptionHandler		handler;
    List<AbstractOption>	options;
    StringBuilder		buffer;
    int				i;
    CommandlineHelpProducer	help;

    result = "";

    if (index == 0) {
      if (!canInstantiateClass())
	return result;

      if (!ClassLocator.hasInterface(OptionHandler.class, getInstance().getClass()))
	throw new Exception("Class '" + getClassname() + "' is not an OptionHandler!");

      // any options at all?
      handler = (OptionHandler) getInstance();
      options = handler.getOptionManager().getOptionsList();
      if (options.size() == 0)
	return result;

      // prolog?
      if (getProlog())
	result = "Valid options are: <br>\n\n";

      // options
      buffer = new StringBuilder();
      for (i = 0; i < options.size(); i++) {
	help = new CommandlineHelpProducer();
	buffer.append("<pre>");
	buffer.append(generateJavadoc(help.doProduce(options.get(i)).toString()));
	buffer.append("</pre>\n\n");
      }

      result += buffer.toString();

      // stars?
      if (getUseStars())
	result = indent(result, 1, "* ");
    }

    return result;
  }

  /**
   * Parses the given commandline parameters and generates the Javadoc.
   *
   * @param args	the commandline parameters for the object
   */
  public static void main(String[] args) {
    runJavadoc(OptionHandlerJavadoc.class, args);
  }
}
