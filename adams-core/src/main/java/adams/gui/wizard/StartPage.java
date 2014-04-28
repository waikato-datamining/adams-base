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
 * StartPage.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JLabel;

import adams.core.Properties;
import adams.gui.core.GUIHelper;

/**
 * Simple start/welcome page.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StartPage
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
    setPageName("Welcome");
    setDescription("<html><h2>Welcome</h2>This wizard will guide you through the process.<br>Please click on <b>Next</b> to continue.</html>", true);
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
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    return new Properties();
  }
}
