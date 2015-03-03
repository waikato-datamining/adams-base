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
 * AbstractTextEditorPanelWithSimpleSyntaxHighlighting.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import javax.swing.text.Document;

import adams.core.Properties;
import adams.gui.scripting.SyntaxDocument;

/**
 * A panel that allows the editing of text, including undo/redo support,
 * and custom syntax highlighting.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTextEditorPanelWithSimpleSyntaxHighlighting
  extends StyledTextEditorPanel {

  /** for serialization. */
  private static final long serialVersionUID = 7416099580088156868L;

  /**
   * Returns the syntax style definition.
   *
   * @return		the props file with the definitions
   */
  protected abstract Properties getStyleProperties();

  /**
   * Returns a new text pane.
   *
   * @return		the text pane
   */
  @Override
  protected BaseTextPaneWithWordWrap newBaseTextPane() {
    BaseTextPaneWithWordWrap	result;
    Document		doc;

    result = new BaseTextPaneWithWordWrap();
    doc    = new SyntaxDocument(getStyleProperties());
    result.getTextPane().setDocument(doc);

    return result;
  }
}
