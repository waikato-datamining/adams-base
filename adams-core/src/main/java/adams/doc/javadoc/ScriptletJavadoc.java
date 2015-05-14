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
 * ScriptletJavadoc.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.doc.javadoc;

import adams.core.Utils;
import adams.gui.scripting.AbstractScriptlet;


/**
 * Generates Javadoc comments for an AbstractScriptlet. Can
 * automatically update the comments if they're surrounded by
 * the FLOW_STARTTAG and FLOW_ENDTAG (the indention is determined via
 * the FLOW_STARTTAG).
 * <br><br>
 * In addition to the flow tags, one can also place the tags
 * ACCEPTS_STARTTAG/ACCEPTS_ENDTAG and GENERATES_STARTTAG/GENERATES_ENDTAG
 * in the Javadoc. These tags don't add blocks of comments, but just a single
 * classname.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #DESCRIPTION_STARTTAG
 * @see #DESCRIPTION_ENDTAG
 * @see #PARAMETERS_STARTTAG
 * @see #PARAMETERS_ENDTAG
 */
public class ScriptletJavadoc
  extends Javadoc {

  /** for serialization. */
  private static final long serialVersionUID = -3836221902854905090L;

  /** the start comment tag for inserting the generated Javadoc ("action"). */
  public final static String ACTION_STARTTAG = "<!-- scriptlet-action-start -->";

  /** the end comment tag for inserting the generated Javadoc ("action"). */
  public final static String ACTION_ENDTAG = "<!-- scriptlet-action-end -->";

  /** the start comment tag for inserting the generated Javadoc ("description"). */
  public final static String DESCRIPTION_STARTTAG = "<!-- scriptlet-description-start -->";

  /** the end comment tag for inserting the generated Javadoc ("description"). */
  public final static String DESCRIPTION_ENDTAG = "<!-- scriptlet-description-end -->";

  /** the start comment tag for inserting the generated Javadoc ("parameters"). */
  public final static String PARAMETERS_STARTTAG = "<!-- scriptlet-parameters-start -->";

  /** the end comment tag for inserting the generated Javadoc ("parameters"). */
  public final static String PARAMETERS_ENDTAG = "<!-- scriptlet-parameters-end -->";

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_StartTag    = new String[3];
    m_EndTag      = new String[3];
    m_IsBlock     = new boolean[3];
    m_StartTag[0] = ACTION_STARTTAG;
    m_EndTag[0]   = ACTION_ENDTAG;
    m_IsBlock[0]  = false;
    m_StartTag[1] = PARAMETERS_STARTTAG;
    m_EndTag[1]   = PARAMETERS_ENDTAG;
    m_IsBlock[1]  = true;
    m_StartTag[2] = DESCRIPTION_STARTTAG;
    m_EndTag[2]   = DESCRIPTION_ENDTAG;
    m_IsBlock[2]  = true;
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
    String		result;
    String[]		lines;
    int			i;
    AbstractScriptlet	scriptlet;

    result = "";

    if (!canInstantiateClass())
      return result;

    if (!(getInstance() instanceof AbstractScriptlet))
      return result;
    else
      scriptlet = (AbstractScriptlet) getInstance();

    // action
    if (m_StartTag[index].equals(ACTION_STARTTAG)) {
      result = toHTML(scriptlet.getAction());
      result = result.trim();
    }

    // parameters
    if (m_StartTag[index].equals(PARAMETERS_STARTTAG)) {
      result = "Action parameters:<br>\n" + "<pre>   " + toHTML(scriptlet.getParameterDescription()) + "</pre>";
      result = result.trim() + "\n<br><br>\n";

      // stars?
      if (getUseStars())
	result = indent(result, 1, "* ");
    }

    // description
    if (m_StartTag[index].equals(DESCRIPTION_STARTTAG)) {
      lines  = Utils.breakUp(scriptlet.getDescription(), 72);
      result = "Description:";
      for (i = 0; i < lines.length; i++) {
	result += "\n";
	if (i == 0)
	  result += "<pre>";
	result += "   " + toHTML(lines[i]);
	if (i == lines.length - 1)
	  result += "</pre>";
      }
      result += "\n<br><br>\n";

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
    runJavadoc(ScriptletJavadoc.class, args);
  }
}
