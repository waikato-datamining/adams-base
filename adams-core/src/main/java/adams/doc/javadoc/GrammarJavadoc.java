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
 * GrammarJavadoc.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.doc.javadoc;

import java.lang.reflect.Method;

/**
 * Generates Javadoc comments for the GrammarSupplier. Can
 * automatically update the comments if they're surrounded by
 * the GRAMMAR_STARTTAG and GRAMMAR_ENDTAG (the indention is determined via
 * the GRAMMAR_STARTTAG).
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-env &lt;java.lang.String&gt; (property: environment)
 * &nbsp;&nbsp;&nbsp;The class to use for determining the environment.
 * &nbsp;&nbsp;&nbsp;default: adams.env.Environment
 * </pre>
 *
 * <pre>-W &lt;java.lang.String&gt; (property: classname)
 * &nbsp;&nbsp;&nbsp;The class to load.
 * &nbsp;&nbsp;&nbsp;default: adams.doc.AllJavadoc
 * </pre>
 *
 * <pre>-nostars (property: useStars)
 * &nbsp;&nbsp;&nbsp;Controls the use of '*' in the Javadoc.
 * </pre>
 *
 * <pre>-dir &lt;java.lang.String&gt; (property: dir)
 * &nbsp;&nbsp;&nbsp;The directory above the package hierarchy of the class.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-silent (property: silent)
 * &nbsp;&nbsp;&nbsp;Suppresses printing in the console.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #GRAMMAR_STARTTAG
 * @see #GRAMMAR_ENDTAG
 */
public class GrammarJavadoc
  extends Javadoc {

  /** for serialization. */
  private static final long serialVersionUID = 2873100039607513910L;

  /** the start comment tag for inserting the generated Javadoc (all). */
  public final static String GRAMMAR_STARTTAG = "<!-- grammar-start -->";

  /** the end comment tag for inserting the generated Javadoc (all). */
  public final static String GRAMMAR_ENDTAG = "<!-- grammar-end -->";

  /** the getGrammar() method. */
  public final static String GRAMMAR_METHOD = "getGrammar";

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_StartTag    = new String[1];
    m_EndTag      = new String[1];
    m_IsBlock     = new boolean[1];
    m_StartTag[0] = GRAMMAR_STARTTAG;
    m_EndTag[0]   = GRAMMAR_ENDTAG;
    m_IsBlock[0]  = true;
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
    String	result;
    Method	grammar;

    result = "";

    if (!canInstantiateClass())
      return result;

    if (!(getInstance() instanceof adams.parser.GrammarSupplier))
      return result;

    // try to get methods
    try {
      grammar = getInstance().getClass().getMethod(GRAMMAR_METHOD, (Class[]) null);
    }
    catch (Exception e) {
      grammar = null;
    }

    if ((index == 0) && (grammar != null)) {
      if (grammar != null)
	result = (String) grammar.invoke(getInstance(), (Object[]) null);
      if (result.length() > 0) {
	result = toHTML("Input/output:\n" + result).trim().replaceAll("(\t)(.+)(<br\\/>)", "<pre>   $2</pre>");
	result += "\n<br><br>\n";
      }

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
    runJavadoc(GrammarJavadoc.class, args);
  }
}
