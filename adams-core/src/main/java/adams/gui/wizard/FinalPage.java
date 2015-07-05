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
 * FinalPage.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.gui.core.GUIHelper;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.BorderLayout;

/**
 * Simple start/welcome page.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FinalPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = 4561440274465998533L;
  
  /** for displaying a logo. */
  protected JLabel m_LabelLogo;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_LabelLogo = new JLabel(GUIHelper.getIcon("adams_logo.png"));
    
    add(m_LabelLogo, BorderLayout.NORTH);
    add(m_ScrollPaneDescription, BorderLayout.CENTER);
  }

  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setPageName("Finish");
    setDescription("<html><h2>Finished</h2>Please click on <b>Finish</b> to finish up.</html>");
  }
  
  /**
   * Sets the logo to display.
   * 
   * @return		the logo
   */
  public void setLogo(Icon value) {
    m_LabelLogo.setIcon(value);
  }
  
  /**
   * Returns the current logo.
   * 
   * @return		the logo
   */
  public Icon getLogo() {
    return m_LabelLogo.getIcon();
  }

  /**
   * Does nothing.
   *
   * @param value	ignored
   */
  public void setProperties(Properties value) {
    // does nothing
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    return new Properties();
  }
}
