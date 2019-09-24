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
 * Markdown.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.annotations;

import adams.gui.flow.tree.Node;
import org.markdownj.MarkdownProcessor;

/**
 * Turns the markdown annotations into HTML.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Markdown
  extends AbstractAnnotationProcessor {

  private static final long serialVersionUID = -312797137959308577L;

  protected transient MarkdownProcessor m_MarkdownProcessor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the markdown annotations into HTML.";
  }

  /**
   * Returns the markdown processor to use.
   *
   * @return		the processor
   */
  protected synchronized MarkdownProcessor getProcessor() {
    if (m_MarkdownProcessor == null)
      m_MarkdownProcessor = new MarkdownProcessor();
    return m_MarkdownProcessor;
  }

  /**
   * Turns the actor's annotations into HTML.
   *
   * @param node 	the node to process the annotations for
   * @return		the generated HTML
   */
  @Override
  public String toHTML(Node node) {
    StringBuilder	result;
    String		colorDef;
    String		sizeDef;

    colorDef = node.hasOwner() ? node.getOwner().getAnnotationsColor() : "blue";
    sizeDef  = node.hasOwner() ? node.getOwner().getAnnotationsSize() : "-2";

    result = new StringBuilder();
    result.append("<font " + node.generateSizeAttribute(sizeDef) + " color='" + colorDef + "'>");
    result.append(getProcessor().markdown(node.getActor().getAnnotations().getValue()));
    result.append("</font>");

    return result.toString();
  }
}
