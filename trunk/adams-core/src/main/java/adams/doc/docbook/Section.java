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
 * Section.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.docbook;

import adams.doc.xml.AbstractComplexTag;
import adams.doc.xml.AbstractTag;

/**
 * Represents the "section" tag.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Section
  extends AbstractComplexTag {

  /** for serialization. */
  private static final long serialVersionUID = 6147436275145204642L;

  /**
   * Initializes the tag.
   */
  public Section() {
    super("section");
  }

  /**
   * Initializes the tag.
   *
   * @param title	the title of this section
   */
  public Section(String title) {
    this();
    add(new Title(title));
  }

  /**
   * Initializes the tag.
   *
   * @param title	the title of this section
   * @param content	the content of the section (inside "para" tags)
   */
  public Section(String title, String content) {
    this(title);
    setContent(content);
  }

  /**
   * Removes "para" elements.
   *
   * @param onlyEmpty	whether to remove only empty paragraphs
   */
  protected void removeParagraphs(boolean onlyEmpty) {
    int			i;
    AbstractTag	element;

    i = 0;
    while (i < getChildCount()) {
      element = (AbstractTag) getChildAt(i);
      if (element instanceof Paragraph) {
	if (onlyEmpty && !((Paragraph) element).hasContent())
	  remove(i);
	else if (!onlyEmpty)
	  remove(i);
	else
	  i++;
      }
      else {
	i++;
      }
    }
  }

  /**
   * Sets the content that gets enclosed in "para" tags. Lines are automatically
   * split at line feeds and added as separate "para" tags.
   *
   * @param content	the content to add
   */
  public void setContent(String content) {
    String[] 	lines;

    removeParagraphs(false);

    lines = content.split("\n");
    for (String line: lines)
      add(new Paragraph(line));
  }

  /**
   * Hook method for validating/fixing the XML tree.
   * <p/>
   * Inserts a dummy paragraph if no nested section paragraphs available.
   */
  @Override
  protected void doValidate() {
    boolean	valid;
    int		i;

    valid = false;

    for (i = 0; i < getChildCount(); i++) {
      if (getChildAt(i) instanceof Paragraph) {
	valid = true;
	break;
      }
      if (getChildAt(i) instanceof Section) {
	valid = true;
	break;
      }
    }

    // we have to add a dummy paragraph
    if (!valid) {
      add(new Paragraph());
    }
  }
}
