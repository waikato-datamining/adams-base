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
 * AbstractPropertiesPreferencesPanel.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Properties;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

/**
 * Displays all properties in a props file as preferences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPropertiesPreferencesPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -822178750857036833L;
  
  /** the panel with the preferences. */
  protected PropertiesParameterPanel m_PanelPreferences;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());
    
    m_PanelPreferences = new PropertiesParameterPanel();
    add(new BaseScrollPane(m_PanelPreferences), BorderLayout.CENTER);
  }
  
  /**
   * The title of the preferences.
   * 
   * @return		the title
   */
  @Override
  public abstract String getTitle();

  /**
   * Activates the settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public abstract String activate();
  
  /**
   * Removes all property/preference type relations.
   */
  public void clearPropertyTypes() {
    m_PanelPreferences.clearPropertyTypes();
  }

  /**
   * Adds a preference.
   *
   * @param identifier	the unique identifier of the preference
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @throws IllegalArgumentException	if the identifier already exists
   */
  public void addPreference(String identifier, String label, Component comp) {
    m_PanelPreferences.addProperty(identifier, label, comp);
  }

  /**
   * Adds the chooser panel at the end.
   *
   * @param identifier	the unique identifier of the preference
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param chooser	the chooser panel to add
   * @throws IllegalArgumentException	if the identifier already exists
   */
  public void addPreference(String identifier, String label, AbstractChooserPanel chooser) {
    m_PanelPreferences.addProperty(identifier, label, chooser);
  }

  /**
   * Returns the component at the specified location.
   *
   * @param index	the index of the specified location
   * @return		the component at the position
   */
  public Component getPreference(int index) {
    return m_PanelPreferences.getProperty(index);
  }

  /**
   * Returns the component associated with the identifier.
   *
   * @param identifier	the identifier of the preference to return
   * @return		the associated component, null if none found
   */
  public Component getPreference(String identifier) {
    return m_PanelPreferences.getProperty(identifier);
  }

  /**
   * Returns the number of preferences currently displayed.
   *
   * @return		the number of preferences
   */
  public int getPreferenceCount() {
    return m_PanelPreferences.getPropertyCount();
  }

  /**
   * Associates the preference type with the specified property.
   *
   * @param property	the property to associate a type with
   * @param type	the preference type
   */
  public void addPropertyType(String property, PropertyType type) {
    m_PanelPreferences.addPropertyType(property, type);
  }

  /**
   * Checks whether a preference type has been specified for a particular
   * property.
   *
   * @param property	the property to associate a type with
   * @return		true if a type has been specified
   */
  public boolean hasPropertyType(String property) {
    return m_PanelPreferences.hasPropertyType(property);
  }

  /**
   * Checks whether a preference type has been specified for a particular
   * property.
   *
   * @param property	the property to associate a type with
   * @return		true if a type has been specified
   */
  public PropertyType getPropertyType(String property) {
    return m_PanelPreferences.getPropertyType(property);
  }

  /**
   * Checks whether a preference type has been specified for a particular
   * property.
   *
   * @param property	the property to associate a type with
   * @return		true if a type has been specified
   */
  public PropertyType getActualPropertyType(String property) {
    return m_PanelPreferences.getActualPropertyType(property);
  }

  /**
   * Checks whether a chooser has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a chooser has been specified
   */
  public boolean hasChooser(String property) {
    return m_PanelPreferences.hasChooser(property);
  }

  /**
   * Associates the chooser with a particular property.
   *
   * @param property	the property to associate the chooser with
   * @param value	the chooser to use
   */
  public void setChooser(String property, AbstractChooserPanel value) {
    m_PanelPreferences.setChooser(property, value);
  }

  /**
   * Returns the chooser associated with a particular
   * property.
   *
   * @param property	the property to get the chooser for
   * @return		the chooser, null if none available
   */
  public AbstractChooserPanel getChooser(String property) {
    return m_PanelPreferences.getChooser(property);
  }

  /**
   * Checks whether a enum has been specified for a particular
   * property.
   *
   * @param property	the property to check
   * @return		true if a enum has been specified
   */
  public boolean hasEnum(String property) {
    return m_PanelPreferences.hasEnum(property);
  }

  /**
   * Associates the enum with a particular property.
   *
   * @param property	the property to associate the enum with
   * @param value	the enum to use
   */
  public void setEnum(String property, Class value) {
    m_PanelPreferences.setEnum(property, value);
  }

  /**
   * Returns the enum associated with a particular
   * property.
   *
   * @param property	the property to get the enum for
   * @return		the enum, null if none available
   */
  public Class getEnum(String property) {
    return m_PanelPreferences.getEnum(property);
  }

  /**
   * Checks whether a list has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a list has been specified
   */
  public boolean hasList(String property) {
    return m_PanelPreferences.hasList(property);
  }

  /**
   * Associates the list with a particular property.
   *
   * @param property	the property to associate the list with
   * @param value	the list to use
   */
  public void setList(String property, String[] value) {
    m_PanelPreferences.setList(property, value);
  }

  /**
   * Returns the list associated with a particular
   * property.
   *
   * @param property	the property to get the list for
   * @return		the list, null if none available
   */
  public String[] getList(String property) {
    return m_PanelPreferences.getList(property);
  }

  /**
   * Checks whether a help has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a help has been specified
   */
  public boolean hasHelp(String property) {
    return m_PanelPreferences.hasHelp(property);
  }

  /**
   * Associates the help with a particular property.
   *
   * @param property	the property to associate the help with
   * @param value	the help to use
   */
  public void setHelp(String property, String value) {
    m_PanelPreferences.setHelp(property, value);
  }

  /**
   * Returns the help associated with a particular
   * property.
   *
   * @param property	the property to get the help for
   * @return		the help, null if none available
   */
  public String getHelp(String property) {
    return m_PanelPreferences.getHelp(property);
  }

  /**
   * Sets the order for the properties.
   *
   * @param value	the ordered property names
   */
  public void setPropertyOrder(String[] value) {
    m_PanelPreferences.setPropertyOrder(value);
  }

  /**
   * Sets the order for the properties.
   *
   * @param value	the ordered property names
   */
  public void setPropertyOrder(List<String> value) {
    m_PanelPreferences.setPropertyOrder(value);
  }

  /**
   * Returns the order for the properties.
   *
   * @return		the ordered property names
   */
  public List<String> getPropertyOrder() {
    return m_PanelPreferences.getPropertyOrder();
  }

  /**
   * Sets the properties to base the preferences on.
   *
   * @param value	the properties to use
   */
  public void setPreferences(Properties value) {
    m_PanelPreferences.setProperties(value);
  }
  
  /**
   * Returns the currently display preferences as a properties object.
   *
   * @return		the preferences
   */
  public Properties getPreferences() {
    return m_PanelPreferences.getProperties();
  }
}