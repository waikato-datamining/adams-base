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
 * PlainText.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.annotations;

import adams.core.base.BaseAnnotation.Tag;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;

import java.util.List;

/**
 * Default processor, turns plain text into HTML.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PlainText
  extends AbstractAnnotationProcessor {

  private static final long serialVersionUID = 9193469949051099987L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Default processor, turns plain text into HTML.";
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
    Actor		actor;
    String		colorDef;
    String		sizeDef;
    String		color;
    String		size;
    boolean		font;
    List 		parts;
    Tag			tag;

    result = new StringBuilder();
    actor  = node.getActor();

    colorDef = node.hasOwner() ? node.getOwner().getAnnotationsColor() : "blue";
    sizeDef  = node.hasOwner() ? node.getOwner().getAnnotationsSize() : "-2";

    if (actor.getAnnotations().hasTag()) {
      font = false;
      parts = actor.getAnnotations().getParts();
      for (Object part : parts) {
	if (part instanceof Tag) {
	  tag = (Tag) part;
	  color = colorDef;
	  size = sizeDef;
	  if (tag.getOptions().containsKey("color"))
	    color = tag.getOptions().get("color");
	  if (tag.getOptions().containsKey("size"))
	    size = tag.getOptions().get("size");
	  if (font)
	    result.append("</font>");
	  result.append("<font " + node.generateSizeAttribute(size) + " color='" + color + "'>");
	  result.append(tag.getName());
	  font = true;
	}
	else {
	  if (!font)
	    result.append("<font " + node.generateSizeAttribute(sizeDef) + " color='" + colorDef + "'>");
	  result.append(insertLineBreaks(part.toString()));
	  font = true;
	}
      }
      result.append("</font>");
    }
    else {
      result.append("<font " + node.generateSizeAttribute(sizeDef) + " color='" + colorDef + "'>");
      result.append(insertLineBreaks(actor.getAnnotations().getValue()));
      result.append("</font>");
    }

    return result.toString();
  }
}
