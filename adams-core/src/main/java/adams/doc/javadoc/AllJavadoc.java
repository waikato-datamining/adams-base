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
 * AllJavadoc.java
 * Copyright (C) 2006-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.doc.javadoc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Applies all known Javadoc-derived classes to a source file.
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
 */
public class AllJavadoc
  extends Javadoc {

  /** for serialization. */
  private static final long serialVersionUID = 8491098698586623712L;

  /** contains all the javadoc generators. */
  protected List m_Javadocs;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    String[] 	list;
    int 	i;
    Class 	cls;

    super.initialize();

    // instantiate all apart from itself
    list = Javadoc.getJavadocs();
    m_Javadocs = new ArrayList();
    for (i = 0; i < list.length; i++) {
      if (list[i].equals(AllJavadoc.class.getName()))
	continue;
      try {
	cls = Class.forName(list[i]);
	m_Javadocs.add(cls.newInstance());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate: " + list[i], e);
      }
    }
  }

  /**
   * sets the classname of the class to generate the Javadoc for.
   *
   * @param value	the new classname
   */
  @Override
  public void setClassname(String value) {
    super.setClassname(value);
    for (int i = 0; i < m_Javadocs.size(); i++)
      ((Javadoc) m_Javadocs.get(i)).setClassname(value);
  }

  /**
   * sets whether to prefix the Javadoc with "*".
   *
   * @param value	true if stars are to be used
   */
  @Override
  public void setUseStars(boolean value) {
    super.setUseStars(value);
    for (int i = 0; i < m_Javadocs.size(); i++)
      ((Javadoc) m_Javadocs.get(i)).setUseStars(value);
  }

  /**
   * sets whether to suppress output in the console.
   *
   * @param value	true if output is to be suppressed
   */
  @Override
  public void setSilent(boolean value) {
    super.setSilent(value);
    for (int i = 0; i < m_Javadocs.size(); i++)
      ((Javadoc) m_Javadocs.get(i)).setSilent(value);
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
    throw new Exception("Not used!");
  }

  /**
   * updates the Javadoc in the given source code, using all the found
   * Javadoc updaters.
   *
   * @param content	the source code
   * @return		the updated source code
   * @throws Exception 	in case the generation fails
   */
  @Override
  protected String updateJavadoc(String content) throws Exception {
    String	result;
    int		i;

    result = content;

    for (i = 0; i < m_Javadocs.size(); i++) {
      result = ((Javadoc) m_Javadocs.get(i)).updateJavadoc(result);
    }

    return result;
  }

  /**
   * Parses the given commandline parameters and generates the Javadoc.
   *
   * @param args	the commandline parameters for the object
   */
  public static void main(String[] args) {
    runJavadoc(AllJavadoc.class, args);
  }
}
