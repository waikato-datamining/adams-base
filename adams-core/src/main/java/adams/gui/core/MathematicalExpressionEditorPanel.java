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
 * MathematicalExpressionEditorPanel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.AdditionalInformationHandler;
import adams.core.Properties;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;

/**
 * Text editor pane with syntax highlighting for mathematical expressions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MathematicalExpressionEditorPanel
  extends AbstractTextEditorPanelWithSyntaxHighlighting
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6311158717675828816L;

  /** the props file with the style definitions. */
  public final static String FILENAME = "adams/gui/core/MathematicalExpressionEditorPanel.props";

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
    return new MathematicalExpression().getGrammar();
  }
  
  /**
   * Returns the current expression.
   * 
   * @return		the expression
   */
  public MathematicalExpressionText getExpression() {
    return new MathematicalExpressionText(getTextPane().getText());
  }
}
