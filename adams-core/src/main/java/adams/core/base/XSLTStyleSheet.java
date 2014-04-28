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
 * XSLTStyleSheet.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

import adams.core.HelpProvider;
import adams.gui.core.AbstractScript;
import adams.gui.core.AbstractTextEditorPanelWithSyntaxHighlighting;
import adams.gui.core.XSLTSyntaxEditorPanel;

/**
 * Encapsulates an XSLT Stylesheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XSLTStyleSheet
  extends AbstractScript
  implements HelpProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6084976027405972444L;

  /**
   * Initializes the string with length 0.
   */
  public XSLTStyleSheet() {
    super();
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public XSLTStyleSheet(String s) {
    super(s);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "XSLT Stylesheet";
  }
  
  /**
   * Returns a URL with additional information.
   * 
   * @return		the URL, null if not available
   */
  public String getHelpURL() {
    return "http://www.w3schools.com/xsl/";
  }
  
  /**
   * Returns a long help description, e.g., used in tiptexts.
   * 
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    return "More information on XPath";
  }
  
  /**
   * Returns a short title for the help, e.g., used for buttons.
   * 
   * @return		the short title, null if not available
   */
  public String getHelpTitle() {
    return null;
  }
  
  /**
   * Returns the name of a help icon, e.g., used for buttons.
   * 
   * @return		the icon name, null if not available
   */
  public String getHelpIcon() {
    return "help2.png";
  }

  /**
   * Returns the tip text for the script.
   *
   * @return		the tool tip
   */
  @Override
  protected String getScriptTipText() {
    return "XSLT Stylesheet";
  }

  /**
   * Returns the configured text editor panel to use in the GOE.
   *
   * @return		the text editor panel
   */
  @Override
  public AbstractTextEditorPanelWithSyntaxHighlighting getTextEditorPanel() {
    return new XSLTSyntaxEditorPanel();
  }

  /**
   * Returns whether inline editing in the GOE is allowed.
   * 
   * @return		true if inline editing is allowed
   */
  @Override
  public boolean allowsInlineEditing() {
    return false;
  }
}
