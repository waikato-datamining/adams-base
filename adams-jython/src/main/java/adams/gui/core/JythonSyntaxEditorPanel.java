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
 * JythonSyntaxEditorPanel.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.Properties;
import adams.core.scripting.JythonScript;

/**
 * Text editor pane with Jython syntax highlighting.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JythonSyntaxEditorPanel
  extends AbstractTextEditorPanelWithSyntaxHighlighting {

  /** for serialization. */
  private static final long serialVersionUID = -6311158717675828816L;

  /** the props file with the style definitions. */
  public final static String FILENAME = "adams/gui/core/JythonSyntaxEditorPanel.props";

  /**
   * Returns the syntax style definition.
   *
   * @return		the props file with the definitions
   */
  @Override
  protected Properties getStyleProperties() {
    try {
      return Properties.read(FILENAME);
    }
    catch (Exception e) {
      System.err.println("Failed to load style definitions '" + FILENAME + "': ");
      e.printStackTrace();
      return new Properties();
    }
  }
  
  /**
   * Returns the current script.
   * 
   * @return		the script
   */
  public JythonScript getScript() {
    return new JythonScript(getTextPane().getText());
  }
}
