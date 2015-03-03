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
 * GlobalInfoJavadoc.java
 * Copyright (C) 2006 University of Waikato, Hamilton, New Zealand
 */

package adams.doc.javadoc;

import java.lang.reflect.Method;

import adams.core.ClassCrossReference;

/**
 * Generates Javadoc comments from the class's globalInfo method. Can
 * automatically update the comments if they're surrounded by
 * the GLOBALINFO_STARTTAG and GLOBALINFO_ENDTAG (the indention is determined via
 * the GLOBALINFO_STARTTAG). <p/>
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * @see #GLOBALINFO_METHOD
 * @see #GLOBALINFO_STARTTAG
 * @see #GLOBALINFO_ENDTAG
 * @see weka.core.GlobalInfoJavadoc
 */
public class GlobalInfoJavadoc
  extends Javadoc {

  /** for serialization. */
  private static final long serialVersionUID = -7121795851141220273L;

  /** the globalInfo method name. */
  public final static String GLOBALINFO_METHOD = "globalInfo";

  /** the start comment tag for inserting the generated Javadoc. */
  public final static String GLOBALINFO_STARTTAG = "<!-- globalinfo-start -->";

  /** the end comment tag for inserting the generated Javadoc. */
  public final static String GLOBALINFO_ENDTAG = "<!-- globalinfo-end -->";

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_StartTag    = new String[1];
    m_EndTag      = new String[1];
    m_IsBlock     = new boolean[1];
    m_StartTag[0] = GLOBALINFO_STARTTAG;
    m_EndTag[0]   = GLOBALINFO_ENDTAG;
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
    Method	method;
    String	info;
    Class[]	cross;
    int		i;

    result = "";

    if (index == 0) {
      if (!canInstantiateClass())
	return result;

      try {
	method = getInstance().getClass().getMethod(GLOBALINFO_METHOD, (Class[]) null);
      }
      catch (Exception e) {
	// no method "globalInfo"
	return result;
      }

      // retrieve global info
      info = (String) method.invoke(getInstance(), (Object[]) null);
      if (getInstance() instanceof ClassCrossReference) {
	cross = ((ClassCrossReference) getInstance()).getClassCrossReferences();
	info += "\n\nSee also:";
	for (i = 0; i < cross.length; i++)
	  info += "\n" + cross[i].getName();
      }
      result = toHTML(info);
      result = result.trim() + "\n<p/>\n";

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
    runJavadoc(GlobalInfoJavadoc.class, args);
  }
}
