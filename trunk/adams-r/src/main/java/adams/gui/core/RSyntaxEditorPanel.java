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
 * RSyntaxEditorPanel.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.Properties;
import adams.core.scripting.RScript;

/**
 * 
 * Loads the syntax highlighting properties file for RScript.
 * 
 * @author rsmith
 * @version $Revision$
 */
public class RSyntaxEditorPanel 
  extends AbstractTextEditorPanelWithSimpleSyntaxHighlighting {

  /** for serialization. */
  private static final long serialVersionUID = -4836522671525894328L;

  /** the props file with the style definitions. */
  public final static String FILENAME = "adams/gui/core/RSyntaxEditorPanel.props";

  /**
   * Returns the syntax style definition.
   * 
   * @return the props file with the definitions
   */
  @Override
  protected Properties getStyleProperties() {
    try {
      return Properties.read(FILENAME);
    } catch (Exception e) {
      System.err.println("Failed to load style definitions '" + FILENAME
	  + "': ");
      e.printStackTrace();
      return new Properties();
    }
  }
  
  /**
   * Returns the current script.
   * 
   * @return		the script
   */
  public RScript getScript() {
    return new RScript(getTextPane().getText());
  }
}
