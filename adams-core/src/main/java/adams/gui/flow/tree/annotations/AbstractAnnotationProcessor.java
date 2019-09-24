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
 * AbstractAnnotationProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.annotations;

import adams.core.net.HtmlUtils;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for annotation processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAnnotationProcessor
  extends AbstractOptionHandler
  implements AnnotationProcessor {

  private static final long serialVersionUID = -884528900861143801L;

  /**
   * Inserts line breaks.
   *
   * @param s		the string to process
   * @return		the updated string
   */
  protected String insertLineBreaks(String s) {
    StringBuilder	result;
    String[]		lines;
    int			i;
    int			n;
    String		line;
    boolean		trailingLF;

    result     = new StringBuilder();
    trailingLF = s.endsWith("\n");
    lines      = s.split("\n");
    for (i = 0; i < lines.length; i++) {
      if (lines[i].startsWith(" ")) {
	line = lines[i].trim();
	for (n = 0; n < lines[i].length() - line.length(); n++)
	  line = "&nbsp;" + HtmlUtils.toHTML(line);
      }
      else {
	line = HtmlUtils.toHTML(lines[i]);
      }
      if (i > 0)
	result.append("<br>");
      result.append(line);
    }

    if (trailingLF)
      result.append("<br>");

    return result.toString();
  }
}
