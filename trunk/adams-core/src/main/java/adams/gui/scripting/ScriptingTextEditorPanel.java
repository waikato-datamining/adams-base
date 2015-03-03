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
 * ScriptingTextEditorPanel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.Properties;
import adams.env.Environment;
import adams.env.ScriptingDialogDefinition;
import adams.gui.core.AbstractTextEditorPanelWithSimpleSyntaxHighlighting;

/**
 * A text editor panel with syntax highlighting for the scripting commands
 * in the GUI.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingTextEditorPanel
  extends AbstractTextEditorPanelWithSimpleSyntaxHighlighting {

  /** for serialization. */
  private static final long serialVersionUID = 2811465827523143114L;

  /**
   * Returns the syntax style definition.
   *
   * @return		the props file with the definitions
   */
  @Override
  protected Properties getStyleProperties() {
    return Environment.getInstance().read(ScriptingDialogDefinition.KEY);
  }
}
