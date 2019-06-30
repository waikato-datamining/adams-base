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
 * ClassCrossReferences.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.help;

import adams.core.ClassCrossReference;
import adams.core.Utils;
import adams.gui.help.HelpFrame;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 <!-- globalinfo-start -->
 * Generates help for cross-references of classes that implement the adams.core.ClassCrossReference interface.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see ClassCrossReference
 */
public class ClassCrossReferences
  extends AbstractHelpGenerator {

  private static final long serialVersionUID = -3885494293535045819L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates help for cross-references of classes that implement the "
	+ Utils.classToString(ClassCrossReference.class) + " interface.";
  }

  /**
   * Checks whether the generator handles this class.
   *
   * @param cls		the class to check
   * @return		true if it can handle the class
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.matches(ClassCrossReference.class, cls);
  }

  /**
   * Generates the help for the object in the requested format.
   *
   * @param obj		the object to generate the help for
   * @param format	the format of the output
   * @return		the generated help
   */
  @Override
  public String generate(Object obj, HelpFormat format) {
    StringBuilder	result;
    Class[]		cross;
    int			i;

    result = new StringBuilder();

    switch (format) {
      case PLAIN_TEXT:
	result.append("See also:\n");
	cross = ((ClassCrossReference) obj).getClassCrossReferences();
	for (i = 0; i < cross.length; i++)
	  result.append(cross[i].getName() + "\n");
	result.append("\n");
	break;

      case HTML:
	result.append("<h2>See also</h2>\n");
	cross = ((ClassCrossReference) obj).getClassCrossReferences();
	result.append("<ul>\n");
	for (i = 0; i < cross.length; i++)
	  result.append("<li><a href=\"" + HelpFrame.toClassCrossRefURL(cross[i].getName()) + "\">" + cross[i].getName() + "</a></li>\n");
	result.append("</ul>\n");
	result.append("\n");
	break;

      default:
	throw new IllegalStateException("Unhandled format: " + format);
    }

    return result.toString();
  }
}
