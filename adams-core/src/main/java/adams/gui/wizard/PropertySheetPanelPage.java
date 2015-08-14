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
 * PropertySheetPanelPage.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.gui.goe.PropertySheetPanel;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Wizard page that use a {@link PropertySheetPanel} for displaying
 * the properties of an object.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertySheetPanelPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** the identifier for the commandline of the object. */
  public final static String PROPERTY_CMDLINE = "Commandline";
  
  /** the parameter panel for displaying the parameters. */
  protected PropertySheetPanel m_PanelSheet;

  /**
   * Default constructor.
   */
  public PropertySheetPanelPage() {
    super();
  }
  
  /**
   * Initializes the page with the given page name.
   * 
   * @param pageName	the page name to use
   */
  public PropertySheetPanelPage(String pageName) {
    this();
    setPageName(pageName);
  }
  
  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_PanelSheet = new PropertySheetPanel();
    m_PanelSheet.setShowAboutBox(false);
    m_PanelSheet.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        updateButtons();
      }
    });
    add(m_PanelSheet, BorderLayout.CENTER);
  }

  /**
   * Returns the underlying property sheet panel.
   * 
   * @return		the property sheet panel
   */
  public PropertySheetPanel getParameterPanel() {
    return m_PanelSheet;
  }
  
  /**
   * Sets the object to display the properties for.
   * 
   * @param value	the object
   */
  public void setTarget(Object value) {
    m_PanelSheet.setTarget(value);
  }
  
  /**
   * Returns the current object.
   * 
   * @return		the object
   */
  public Object getTarget() {
    return m_PanelSheet.getTarget();
  }

  /**
   * Sets the properties to base the properties on.
   *
   * @param value	the properties to use
   */
  public void setProperties(Properties value) {
    String	cmdline;

    cmdline = value.getProperty(PROPERTY_CMDLINE, OptionUtils.getCommandLine(m_PanelSheet.getTarget()));
    try {
      setTarget(OptionUtils.forAnyCommandLine(Object.class, cmdline));
    }
    catch (Exception e) {
      System.err.println("Failed to parse commandline: " + cmdline);
      e.printStackTrace();
      setTarget(m_PanelSheet.getTarget());
    }
    updateButtons();
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    Properties	result;
    
    result = new Properties();
    result.setProperty(PROPERTY_CMDLINE, OptionUtils.getCommandLine(m_PanelSheet.getTarget()));
    
    return result;
  }
}
