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
 * GenericObjectEditorPage.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.gui.goe.GenericObjectEditor;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Wizard page that use a {@link adams.gui.goe.GenericObjectEditor} for displaying
 * the properties of an object (and allowing user to change the class).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GenericObjectEditorPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** the identifier for the commandline of the object. */
  public final static String PROPERTY_CMDLINE = "Commandline";

  /** the GOE for displaying the parameters. */
  protected GenericObjectEditor m_GOE;

  /**
   * Default constructor.
   */
  public GenericObjectEditorPage() {
    super();
  }

  /**
   * Initializes the page with the given page name.
   *
   * @param pageName	the page name to use
   */
  public GenericObjectEditorPage(String pageName) {
    this();
    setPageName(pageName);
  }
  
  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_GOE = new GenericObjectEditor();
    m_GOE.setUpdateSize(false);
    m_GOE.setButtonsVisible(false);
    m_GOE.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        updateButtons();
      }
    });
    add(m_GOE.getCustomEditor(), BorderLayout.CENTER);
  }

  /**
   * Sets the superclass.
   *
   * @param value	the class
   */
  public void setClassType(Class value) {
    m_GOE.setClassType(value);
  }

  /**
   * Returns the superclass.
   *
   * @return		the object
   */
  public Class getClassType() {
    return m_GOE.getClassType();
  }

  /**
   * Sets whether the user can change the class.
   *
   * @param value	true if can change class
   */
  public void setCanChangeClassInDialog(boolean value) {
    m_GOE.setCanChangeClassInDialog(value);
  }

  /**
   * Returns whether the user can change the class.
   *
   * @return		true if can change class
   */
  public boolean getCanChangeClassInDialog() {
    return m_GOE.getCanChangeClassInDialog();
  }

  /**
   * Sets the object to display the properties for.
   * 
   * @param value	the object
   */
  public void setValue(Object value) {
    m_GOE.setValue(value);
  }
  
  /**
   * Returns the current object.
   * 
   * @return		the object
   */
  public Object getValue() {
    return m_GOE.getValue();
  }

  /**
   * Sets the properties to base the properties on.
   *
   * @param value	the properties to use
   */
  public void setProperties(Properties value) {
    String	cmdline;

    cmdline = value.getProperty(PROPERTY_CMDLINE, OptionUtils.getCommandLine(m_GOE.getValue()));
    try {
      setValue(OptionUtils.forAnyCommandLine(getClassType(), cmdline));
    }
    catch (Exception e) {
      System.err.println("Failed to parse commandline: " + cmdline);
      e.printStackTrace();
      setValue(m_GOE.getValue());
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
    result.setProperty(PROPERTY_CMDLINE, OptionUtils.getCommandLine(m_GOE.getValue()));
    
    return result;
  }
}
