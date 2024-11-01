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
 * SpreadSheetQueryEditorPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.AdditionalInformationHandler;
import adams.core.Properties;
import adams.parser.SpreadSheetQuery;
import adams.parser.SpreadSheetQueryText;

/**
 * Text editor pane with syntax highlighting for spreadsheet formula.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetQueryEditorPanel
  extends AbstractTextEditorPanelWithSimpleSyntaxHighlighting
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3179672756270528025L;
  
  /** the props file with the style definitions. */
  public final static String FILENAME = "adams/gui/core/SpreadSheetQueryEditorPanel.props";

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
   * Returns the additional information.
   * 
   * @return		the additional information
   */
  public String getAdditionalInformation() {
    return new SpreadSheetQuery().getGrammar();
  }
  
  /**
   * Returns the current query.
   * 
   * @return		the query
   */
  public SpreadSheetQueryText getQuery() {
    return new SpreadSheetQueryText(getTextPane().getText());
  }
}
