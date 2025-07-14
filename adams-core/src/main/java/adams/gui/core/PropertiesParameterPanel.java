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
 * PropertiesParameterPanel.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.EnumHelper;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BasePassword;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.base.BaseTime;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.core.option.parsing.FontParsing;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.BaseTextChooserPanel;
import adams.gui.chooser.ColorChooserPanel;
import adams.gui.chooser.DateChooserPanel;
import adams.gui.chooser.DateTimeChooserPanel;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.chooser.FontChooserPanel;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.chooser.TimeChooserPanel;
import adams.gui.goe.GenericArrayEditorPanel;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Displays all properties in a props file as parameters (alphabetically
 * sorted if no custom order for properties provided).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see #setPropertyOrder(List)
 */
public class PropertiesParameterPanel
    extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -822178750857036833L;

  /** the default width for choosers. */
  public final static int DEFAULT_WIDTH_CHOOSERS = 250;

  /**
   * The various data types a property can have.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PropertyType {
    /** boolean. */
    BOOLEAN,
    /** integer. */
    INTEGER,
    /** long. */
    LONG,
    /** double. */
    DOUBLE,
    /** string. */
    STRING,
    /** password. */
    PASSWORD,
    /** password (plain text). */
    PASSWORD_PLAIN,
    /** SQL query. */
    SQL,
    /** time. */
    TIME,
    /** date. */
    DATE,
    /** date/time. */
    DATETIME,
    /** color. */
    COLOR,
    /** font. */
    FONT,
    /** file. */
    FILE,
    /** file (absolute path). */
    FILE_ABSOLUTE,
    /** directory. */
    DIRECTORY,
    /** directory (absolute path). */
    DIRECTORY_ABSOLUTE,
    /** enumeration. */
    ENUM,
    /** manually maintained list of string values. */
    LIST,
    /** manually maintained list of string values (multiple selection). */
    LIST_MULTI_SELECTION,
    /** string representing a comma-separated list. */
    COMMA_SEPARATED_LIST,
    /** string representing a comma-separated list (fixed list). */
    COMMA_SEPARATED_LIST_FIXED,
    /** string representing a blank-separated list. */
    BLANK_SEPARATED_LIST,
    /** string representing a blank-separated list (fixed list). */
    BLANK_SEPARATED_LIST_FIXED,
    /** Object editor. */
    OBJECT_EDITOR,
    /** index (1-based). */
    INDEX,
    /** range (1-based). */
    RANGE,
    /** regular expression. */
    REGEXP,
    /** string constrained by regular expression. */
    REGEXP_CONSTRAINED_STRING,
    /** custom component. */
    CUSTOM_COMPONENT,
    /** Array editor. */
    ARRAY_EDITOR,
  }

  /**
   * Enumeration of hints for properties.
   */
  public enum PropertyHint {
    FORWARD_SLASHES,
    MULTI_FORWARD_SLASHES,
  }

  /** the panel for the properties. */
  protected ParameterPanel m_PanelProperties;

  /** the identifiers of the property. */
  protected List<String> m_Identifiers;

  /** the property/property type relation. */
  protected Map<String,PropertyType> m_PropertyTypes;

  /** the actual property/property type relation. */
  protected Map<String,PropertyType> m_ActualPropertyTypes;

  /** the property/chooser relation. */
  protected Map<String,AbstractChooserPanel> m_Choosers;

  /** the property/enum relation. */
  protected Map<String,Class> m_Enums;

  /** the property/lists relation. */
  protected Map<String,String[]> m_Lists;

  /** the property/list default relation. */
  protected Map<String,String> m_ListDefaults;

  /** the property/help relation. */
  protected Map<String,String> m_Help;

  /** the property/label relation. */
  protected Map<String,String> m_Label;

  /** the property/regexp relation. */
  protected Map<String,BaseRegExp> m_RegExp;

  /** the property/component relation. */
  protected Map<String,Component> m_Component;

  /** the property/arrayclass relation. */
  protected Map<String,Class> m_ArrayClass;

  /** the property/arrayseparator relation. */
  protected Map<String,String> m_ArraySeparator;

  /** the property/hints relation. */
  protected Map<String,Set<PropertyHint>> m_PropertyHints;

  /** the custom order for the properties. */
  protected List<String> m_Order;

  /** the panel for the buttons. */
  protected JPanel m_PanelButtons;

  /** the load props button. */
  protected BaseButton m_ButtonLoad;

  /** the save props button. */
  protected BaseButton m_ButtonSave;

  /** the filechooser for loading/saving properties. */
  protected BaseFileChooser m_FileChooser;

  /** the default size for SQL fields. */
  protected Dimension m_DefaultSQLDimension;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Identifiers         = new ArrayList<>();
    m_PropertyTypes       = new HashMap<>();
    m_ActualPropertyTypes = new HashMap<>();
    m_Choosers            = new HashMap<>();
    m_Enums               = new HashMap<>();
    m_Lists               = new HashMap<>();
    m_ListDefaults        = new HashMap<>();
    m_Help                = new HashMap<>();
    m_Label               = new HashMap<>();
    m_RegExp              = new HashMap<>();
    m_Component           = new HashMap<>();
    m_ArrayClass          = new HashMap<>();
    m_ArraySeparator      = new HashMap<>();
    m_PropertyHints       = new HashMap<>();
    m_Order               = new ArrayList<>();
    m_FileChooser         = null;
    m_DefaultSQLDimension = new Dimension(200, 70);
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelProperties = new ParameterPanel();
    add(new BaseScrollPane(m_PanelProperties), BorderLayout.CENTER);

    m_PanelButtons = new JPanel(new BorderLayout());
    add(m_PanelButtons, BorderLayout.SOUTH);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelButtons.add(panel, BorderLayout.WEST);

    m_ButtonLoad = new BaseButton(ImageManager.getIcon("open.gif"));
    m_ButtonLoad.setToolTipText("Load settings from file");
    m_ButtonLoad.addActionListener((ActionEvent e) -> loadProperties());
    panel.add(m_ButtonLoad);

    m_ButtonSave = new BaseButton(ImageManager.getIcon("save.gif"));
    m_ButtonSave.setToolTipText("Save current settings to file");
    m_ButtonSave.addActionListener((ActionEvent e) -> saveProperties());
    panel.add(m_ButtonSave);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setButtonPanelVisible(false);
  }

  /**
   * Sets the default dimension to use for SQL query fields.
   *
   * @param value	the preferred size
   */
  public void setDefaultSQLDimension(Dimension value) {
    int				i;
    SQLSyntaxEditorPanel	query;

    m_DefaultSQLDimension = value;

    // update fields
    for (i = 0; i < m_PanelProperties.getParameterCount(); i++) {
      if (m_PanelProperties.getParameter(i) instanceof SQLSyntaxEditorPanel) {
	query = (SQLSyntaxEditorPanel) m_PanelProperties.getParameter(i);
	query.setPreferredSize(m_DefaultSQLDimension);
      }
    }
  }

  /**
   * Returns the dimension to use for SQL query fields.
   *
   * @return		the preferred size
   */
  public Dimension getDefaultSQLDimension() {
    return m_DefaultSQLDimension;
  }

  /**
   * Removes all property/property type relations.
   */
  public void clearPropertyTypes() {
    m_PropertyTypes.clear();
    m_ActualPropertyTypes.clear();
    m_Choosers.clear();
    m_ArrayClass.clear();
    m_ArraySeparator.clear();
    m_Enums.clear();
    m_Lists.clear();
    m_ListDefaults.clear();
    m_Help.clear();
    m_PropertyHints.clear();
  }

  /**
   * Removes all properties.
   */
  protected void clearProperties() {
    m_Identifiers.clear();
    m_PanelProperties.clearParameters();
  }

  /**
   * Adds a property.
   *
   * @param identifier	the unique identifier of the property
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @throws IllegalArgumentException	if the identifier already exists
   */
  public void addProperty(String identifier, String label, Component comp) {
    if (m_Identifiers.contains(identifier))
      throw new IllegalArgumentException("Identifier '" + identifier + "' already present!");
    m_Identifiers.add(identifier);
    m_PanelProperties.addParameter(label, comp);
  }

  /**
   * Adds the chooser panel at the end.
   *
   * @param identifier	the unique identifier of the property
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param chooser	the chooser panel to add
   * @throws IllegalArgumentException	if the identifier already exists
   */
  public void addProperty(String identifier, String label, AbstractChooserPanel chooser) {
    if (m_Identifiers.contains(identifier))
      throw new IllegalArgumentException("Identifier '" + identifier + "' already present!");
    m_Identifiers.add(identifier);
    m_PanelProperties.addParameter(label, chooser);
    setLabel(identifier, label);
  }

  /**
   * Returns the component at the specified location.
   *
   * @param index	the index of the specified location
   * @return		the component at the position
   */
  public Component getProperty(int index) {
    return m_PanelProperties.getParameter(index);
  }

  /**
   * Returns the component associated with the identifier.
   *
   * @param identifier	the identifier of the property to return
   * @return		the associated component, null if none found
   */
  public Component getProperty(String identifier) {
    int		index;

    index = m_Identifiers.indexOf(identifier);
    if (index == -1)
      return null;
    else
      return m_PanelProperties.getParameter(index);
  }

  /**
   * Returns the number of properties currently displayed.
   *
   * @return		the number of properties
   */
  public int getPropertyCount() {
    return m_PanelProperties.getParameterCount();
  }

  /**
   * Associates the property type with the specified property.
   *
   * @param property	the property to associate a type with
   * @param type	the property type
   */
  public void addPropertyType(String property, PropertyType type) {
    m_PropertyTypes.put(property, type);
  }

  /**
   * Checks whether a property type has been specified for a particular
   * property.
   *
   * @param property	the property to associate a type with
   * @return		true if a type has been specified
   */
  public boolean hasPropertyType(String property) {
    return m_PropertyTypes.containsKey(property);
  }

  /**
   * Returns the property type that has been specified for a particular
   * property or STRING if not.
   *
   * @param property	the property to get the type for
   * @return		the associated type or STRING if none associated
   */
  public PropertyType getPropertyType(String property) {
    if (hasPropertyType(property))
      return m_PropertyTypes.get(property);
    else
      return PropertyType.STRING;
  }

  /**
   * Returns the (actual) property type that has been specified for a particular
   * property or STRING if not.
   *
   * @param property	the property to get the type for
   * @return		the associated type or STRING if none associated
   */
  public PropertyType getActualPropertyType(String property) {
    return m_ActualPropertyTypes.getOrDefault(property, PropertyType.STRING);
  }

  /**
   * Sets the order for the properties.
   *
   * @param value	the ordered property names
   */
  public void setPropertyOrder(String[] value) {
    setPropertyOrder(Arrays.asList(value));
  }

  /**
   * Sets the order for the properties.
   *
   * @param value	the ordered property names
   */
  public void setPropertyOrder(List<String> value) {
    m_Order.clear();
    m_Order.addAll(value);
  }

  /**
   * Returns the order for the properties.
   *
   * @return		the ordered property names
   */
  public List<String> getPropertyOrder() {
    return m_Order;
  }

  /**
   * Associates the property hint with the specified property.
   *
   * @param property	the property to associate a hint with
   * @param hint	the property hint
   */
  public void addPropertyHint(String property, PropertyHint hint) {
    if (!m_PropertyHints.containsKey(property))
      m_PropertyHints.put(property, new HashSet<>());
    m_PropertyHints.get(property).add(hint);
  }

  /**
   * Checks whether any property hints have been specified for a particular
   * property.
   *
   * @param property	the property to check
   * @return		true if at least one hint has been specified
   */
  public boolean hasPropertyHints(String property) {
    return m_PropertyHints.containsKey(property)
	&& !m_PropertyHints.get(property).isEmpty();
  }

  /**
   * Returns the hints associated with a property.
   *
   * @param property	the property to get the hints for
   * @return		the hints
   */
  public Set<PropertyHint> getPropertyHints(String property) {
    if (hasPropertyHints(property))
      return m_PropertyHints.get(property);
    else
      return new HashSet<>();
  }

  /**
   * Checks whether a chooser has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a chooser has been specified
   */
  public boolean hasChooser(String property) {
    return m_Choosers.containsKey(property);
  }

  /**
   * Associates the chooser with a particular property.
   *
   * @param property	the property to associate the chooser with
   * @param value	the chooser to use
   */
  public void setChooser(String property, AbstractChooserPanel value) {
    m_Choosers.put(property, value);
  }

  /**
   * Returns the chooser associated with a particular
   * property.
   *
   * @param property	the property to get the chooser for
   * @return		the chooser, null if none available
   */
  public AbstractChooserPanel getChooser(String property) {
    return m_Choosers.get(property);
  }

  /**
   * Checks whether a custom component has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a custom component has been specified
   */
  public boolean hasComponent(String property) {
    return m_Component.containsKey(property);
  }

  /**
   * Associates the custom component with a particular property.\
   * Must have setText(String) and getText() methods!
   *
   * @param property	the property to associate the chooser with
   * @param value	the custom component to use
   */
  public void setComponent(String property, Component value) {
    m_Component.put(property, value);
  }

  /**
   * Returns the custom component associated with a particular
   * property.
   *
   * @param property	the property to get the chooser for
   * @return		the custom component, null if none available
   */
  public Component getComponent(String property) {
    return m_Component.get(property);
  }

  /**
   * Checks whether a custom array class has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a custom array class has been specified
   */
  public boolean hasArrayClass(String property) {
    return m_ArrayClass.containsKey(property);
  }

  /**
   * Associates the custom array class with a particular property.\
   *
   * @param property	the property to associate the chooser with
   * @param value	the custom array class to use
   */
  public void setArrayClass(String property, Class value) {
    m_ArrayClass.put(property, value);
  }

  /**
   * Returns the custom array class associated with a particular
   * property.
   *
   * @param property	the property to get the chooser for
   * @return		the custom array class, null if none available
   */
  public Class getArrayClass(String property) {
    return m_ArrayClass.get(property);
  }

  /**
   * Checks whether a custom array separator has been specified for a particular
   * property. If no separator specified, comma is used.
   *
   * @param property	the property check
   * @return		true if a custom array separator has been specified
   */
  public boolean hasArraySeparator(String property) {
    return m_ArraySeparator.containsKey(property);
  }

  /**
   * Associates the custom array separator with a particular property.
   * If no separator specified, comma is used.
   *
   * @param property	the property to associate the chooser with
   * @param value	the custom array separator to use
   */
  public void setArraySeparator(String property, String value) {
    m_ArraySeparator.put(property, value);
  }

  /**
   * Returns the custom array separator associated with a particular
   * property. If no separator specified, comma is used.
   *
   * @param property	the property to get the chooser for
   * @return		the custom array separator, null if none available
   */
  public String getArraySeparator(String property) {
    return m_ArraySeparator.get(property);
  }

  /**
   * Checks whether a enum has been specified for a particular
   * property.
   *
   * @param property	the property to check
   * @return		true if a enum has been specified
   */
  public boolean hasEnum(String property) {
    return m_Enums.containsKey(property);
  }

  /**
   * Associates the enum with a particular property.
   *
   * @param property	the property to associate the enum with
   * @param value	the enum to use
   */
  public void setEnum(String property, Class value) {
    m_Enums.put(property, value);
  }

  /**
   * Returns the enum associated with a particular
   * property.
   *
   * @param property	the property to get the enum for
   * @return		the enum, null if none available
   */
  public Class getEnum(String property) {
    return m_Enums.get(property);
  }

  /**
   * Checks whether a list has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a list has been specified
   */
  public boolean hasList(String property) {
    return m_Lists.containsKey(property);
  }

  /**
   * Associates the list with a particular property.
   *
   * @param property	the property to associate the list with
   * @param value	the list to use
   */
  public void setList(String property, String[] value) {
    m_Lists.put(property, value);
  }

  /**
   * Returns the list associated with a particular
   * property.
   *
   * @param property	the property to get the list for
   * @return		the list, null if none available
   */
  public String[] getList(String property) {
    return m_Lists.get(property);
  }

  /**
   * Checks whether a list default has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a list default has been specified
   */
  public boolean hasListDefault(String property) {
    return m_ListDefaults.containsKey(property);
  }

  /**
   * Associates the list default item with a particular property.
   *
   * @param property	the property to associate the list with
   * @param value	the default to use
   */
  public void setListDefault(String property, String value) {
    m_ListDefaults.put(property, value);
  }

  /**
   * Returns the default list item associated with a particular
   * property.
   *
   * @param property	the property to get the default for
   * @return		the default, null if none available
   */
  public String getListDefault(String property) {
    return m_ListDefaults.get(property);
  }

  /**
   * Checks whether a help has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a help has been specified
   */
  public boolean hasHelp(String property) {
    return m_Help.containsKey(property);
  }

  /**
   * Associates the help with a particular property.
   *
   * @param property	the property to associate the help with
   * @param value	the help to use
   */
  public void setHelp(String property, String value) {
    m_Help.put(property, value);
  }

  /**
   * Returns the help associated with a particular
   * property.
   *
   * @param property	the property to get the help for
   * @return		the help, null if none available
   */
  public String getHelp(String property) {
    return m_Help.get(property);
  }

  /**
   * Checks whether a label has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a label has been specified
   */
  public boolean hasLabel(String property) {
    return m_Label.containsKey(property);
  }

  /**
   * Associates the label with a particular property.
   *
   * @param property	the property to associate the label with
   * @param value	the label to use
   */
  public void setLabel(String property, String value) {
    m_Label.put(property, value);
  }

  /**
   * Returns the label associated with a particular
   * property.
   *
   * @param property	the property to get the label for
   * @return		the label, null if none available
   */
  public String getLabel(String property) {
    return m_Label.get(property);
  }

  /**
   * Checks whether a regexp has been specified for a particular
   * property.
   *
   * @param property	the property check
   * @return		true if a regexp has been specified
   */
  public boolean hasRegExp(String property) {
    return m_RegExp.containsKey(property);
  }

  /**
   * Associates the regexp with a particular property.
   *
   * @param property	the property to associate the label with
   * @param value	the regexp to use
   */
  public void setRegExp(String property, BaseRegExp value) {
    m_RegExp.put(property, value);
  }

  /**
   * Returns the regexp associated with a particular
   * property.
   *
   * @param property	the property to get the label for
   * @return		the regexp, null if none available
   */
  public BaseRegExp getRegExp(String property) {
    return m_RegExp.get(property);
  }

  /**
   * Makes sure that the specified property type can be actually displayed.
   *
   * @param key		the property
   * @param type	the type
   * @return		the (potentially) fixed type
   */
  protected PropertyType fixPropertyType(String key, PropertyType type) {
    if (type == PropertyType.OBJECT_EDITOR) {
      if (!hasChooser(key))
	type = PropertyType.STRING;
    }
    if (type == PropertyType.ARRAY_EDITOR) {
      if (!hasChooser(key) || !hasArrayClass(key))
	type = PropertyType.STRING;
    }
    else if (type == PropertyType.CUSTOM_COMPONENT) {
      if (!hasComponent(key))
	type = PropertyType.STRING;
    }
    else if (type == PropertyType.REGEXP_CONSTRAINED_STRING) {
      if (!hasRegExp(key))
	type = PropertyType.STRING;
    }
    else if (type == PropertyType.ENUM) {
      if (!hasEnum(key))
	type = PropertyType.STRING;
    }
    else if (type == PropertyType.LIST) {
      if (!hasList(key))
	type = PropertyType.STRING;
    }
    return type;
  }

  /**
   * Updates the tool tip.
   *
   * @param comp	the component to update
   * @param help	the tip text to use, ignored if null
   */
  protected void updateToolTipText(Component comp, String help) {
    if (help == null)
      return;
    if (comp instanceof JComponent)
      ((JComponent) comp).setToolTipText(help);
  }

  /**
   * Updates the text of the component.
   *
   * @param comp	the component to update
   * @param text	the text to set
   */
  protected void setText(Component comp, String text) {
    Method	method;

    if (comp instanceof JTextComponent) {
      ((JTextComponent) comp).setText(text);
    }
    else if (comp instanceof AbstractChooserPanel) {
      ((AbstractChooserPanel) comp).setCurrentAsString(text);
    }
    else {
      try {
	method = comp.getClass().getMethod("setText", String.class);
	method.invoke(comp, text);
      }
      catch (Exception e) {
	throw new IllegalStateException("Class " + comp.getClass().getName() + " has no setText(String) method!");
      }
    }
  }

  /**
   * Returns the text from the component.
   *
   * @param comp	the component to get the text from
   * @return		the obtained text
   */
  protected String getText(Component comp) {
    Method	method;

    if (comp instanceof JTextComponent) {
      return ((JTextComponent) comp).getText();
    }
    else if (comp instanceof AbstractChooserPanel) {
      return ((AbstractChooserPanel) comp).getCurrentAsString();
    }
    else {
      try {
	method = comp.getClass().getMethod("getText");
	return (String) method.invoke(comp);
      }
      catch (Exception e) {
	throw new IllegalStateException("Class " + comp.getClass().getName() + " has no getText() method that returns a String object!");
      }
    }
  }

  /**
   * Sets the properties to base the properties on.
   *
   * @param value	the properties to use
   */
  public void setProperties(Properties value) {
    List<String>		keys;
    BaseCheckBox		checkbox;
    JSpinner			spinner;
    PropertyType		type;
    FontChooserPanel 		fontPanel;
    DirectoryChooserPanel	dirPanel;
    FileChooserPanel		filePanel;
    ColorChooserPanel		colorPanel;
    TimeChooserPanel		timePanel;
    DateChooserPanel		datePanel;
    DateTimeChooserPanel	dateTimePanel;
    AbstractChooserPanel	chooserPanel;
    IndexTextField		indexText;
    RangeTextField		rangeText;
    RegExpTextField		regexpText;
    RegExpConstrainedTextField	regexpConstText;
    BaseComboBox		combo;
    SelectOptionPanel		optionPanel;
    Component			comp;
    String			help;
    BaseString[]		list;
    String[]			parts;
    int				i;
    String			label;
    Object			array;
    String			sep;

    clearProperties();
    keys = new ArrayList<>(value.keySetAll());
    keys.removeAll(m_Order);
    Collections.sort(keys);
    keys.addAll(0, m_Order);
    for (String key: keys) {
      if (!m_PropertyTypes.containsKey(key)) {
	System.err.println(getClass().getName() + ": key '" + key + "' in properties is not defined as a parameter, skipping!");
	continue;
      }

      type = fixPropertyType(key, getPropertyType(key));
      help = getHelp(key);

      m_ActualPropertyTypes.put(key, type);
      label = hasLabel(key) ? getLabel(key) : key;

      try {
	switch (type) {
	  case TIME:
	    timePanel = new TimeChooserPanel();
	    timePanel.setCurrent(new BaseTime(value.getTime(key)).timeValue());
	    if (help != null)
	      timePanel.setToolTipText(help);
	    timePanel.setInlineEditingEnabled(true);
	    addProperty(key, label, timePanel);
	    break;
	  case DATE:
	    datePanel = new DateChooserPanel();
	    datePanel.setCurrent(new BaseDate(value.getDate(key)).dateValue());
	    if (help != null)
	      datePanel.setToolTipText(help);
	    datePanel.setInlineEditingEnabled(true);
	    addProperty(key, label, datePanel);
	    break;
	  case DATETIME:
	    dateTimePanel = new DateTimeChooserPanel();
	    dateTimePanel.setCurrent(new BaseDateTime(value.getDateTime(key)).dateTimeValue());
	    if (help != null)
	      dateTimePanel.setToolTipText(help);
	    dateTimePanel.setInlineEditingEnabled(true);
	    addProperty(key, label, dateTimePanel);
	    break;
	  case DOUBLE: {
	    final BaseTextField textfield = new BaseTextField(20);
	    textfield.setText(value.getProperty(key));
	    if (help != null)
	      textfield.setToolTipText(help);
	    textfield.setBorder(BorderFactory.createEtchedBorder());
	    textfield.getDocument().addDocumentListener(new DocumentListener() {
	      @Override
	      public void removeUpdate(DocumentEvent e) {
		check(e);
	      }
	      @Override
	      public void insertUpdate(DocumentEvent e) {
		check(e);
	      }
	      @Override
	      public void changedUpdate(DocumentEvent e) {
		check(e);
	      }
	      private void check(DocumentEvent e) {
		String text = textfield.getText();
		if (text.isEmpty() || Utils.isDouble(text))
		  textfield.setBorder(BorderFactory.createEtchedBorder());
		else
		  textfield.setBorder(BorderFactory.createLineBorder(Color.RED));
	      }
	    });
	    addProperty(key, label, textfield);
	    break;
	  }
	  case STRING:
	    final BaseTextChooserPanel textfield = new BaseTextChooserPanel();
	    textfield.setCurrent(new BaseText(value.getProperty(key)));
	    if (help != null)
	      textfield.setToolTipText(help);
	    textfield.setInlineEditingEnabled(true);
	    addProperty(key, label, textfield);
	    break;
	  case PASSWORD:
	  case PASSWORD_PLAIN:
	    final BasePasswordFieldWithButton pwField = new BasePasswordFieldWithButton(20);
	    pwField.setShowPopupMenu(true);
	    pwField.setText(value.getPassword(key).getValue());
	    if (help != null)
	      pwField.setToolTipText(help);
	    addProperty(key, label, pwField);
	    break;
	  case SQL:
	    final SQLSyntaxEditorPanel query = new SQLSyntaxEditorPanel();
	    query.setLineWrap(true);
	    query.setPreferredSize(m_DefaultSQLDimension);
	    query.setContent(value.getProperty(key));
	    if (help != null)
	      query.setToolTipText(help);
	    addProperty(key, label, query);
	    break;
	  case BOOLEAN:
	    checkbox = new BaseCheckBox();
	    checkbox.setSelected(value.getBoolean(key));
	    if (help != null)
	      checkbox.setToolTipText(help);
	    addProperty(key, label, checkbox);
	    break;
	  case INTEGER:
	    spinner = new JSpinner();
	    spinner.setValue(value.getInteger(key));
	    if (help != null)
	      spinner.setToolTipText(help);
	    addProperty(key, label, spinner);
	    break;
	  case LONG:
	    spinner = new JSpinner();
	    spinner.setValue(value.getLong(key));
	    spinner.setToolTipText(help);
	    addProperty(key, label, spinner);
	    break;
	  case FONT:
	    fontPanel = new FontChooserPanel();
	    fontPanel.setCurrent((Font) FontParsing.valueOf(null, value.getProperty(key)));
	    if (help != null)
	      fontPanel.setToolTipText(help);
	    addProperty(key, label, fontPanel);
	    break;
	  case DIRECTORY:
	  case DIRECTORY_ABSOLUTE:
	    dirPanel = new DirectoryChooserPanel();
	    dirPanel.setCurrent(new PlaceholderDirectory(value.getPath(key)));
	    dirPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH_CHOOSERS, dirPanel.getPreferredSize().height));
	    if (help != null)
	      dirPanel.setToolTipText(help);
	    dirPanel.setInlineEditingEnabled(true);
	    addProperty(key, label, dirPanel);
	    break;
	  case FILE:
	  case FILE_ABSOLUTE:
	    filePanel = new FileChooserPanel();
	    filePanel.setCurrent(new PlaceholderFile(value.getPath(key)));
	    filePanel.setPreferredSize(new Dimension(DEFAULT_WIDTH_CHOOSERS, filePanel.getPreferredSize().height));
	    if (help != null)
	      filePanel.setToolTipText(help);
	    filePanel.setInlineEditingEnabled(true);
	    addProperty(key, label, filePanel);
	    break;
	  case COLOR:
	    colorPanel = new ColorChooserPanel();
	    colorPanel.setCurrent(value.getColor(key));
	    if (help != null)
	      colorPanel.setToolTipText(help);
	    colorPanel.setInlineEditingEnabled(true);
	    addProperty(key, label, colorPanel);
	    break;
	  case ENUM:
	    combo = new BaseComboBox(EnumHelper.getValues(getEnum(key)));
	    combo.setSelectedItem(EnumHelper.parse(getEnum(key), value.getProperty(key)));
	    if (help != null)
	      combo.setToolTipText(help);
	    addProperty(key, label, combo);
	    break;
	  case LIST_MULTI_SELECTION:
	    optionPanel = new SelectOptionPanel();
	    optionPanel.setOptions(getList(key));
	    optionPanel.setMultiSelect(true);
	    optionPanel.setCurrent(OptionUtils.splitOptions(value.getProperty(key)));
	    addProperty(key, label, optionPanel);
	    break;
	  case LIST:
	  case BLANK_SEPARATED_LIST_FIXED:
	  case COMMA_SEPARATED_LIST_FIXED:
	    if (type == PropertyType.BLANK_SEPARATED_LIST_FIXED)
	      combo = new BaseComboBox(SpreadSheetUtils.split(value.getProperty(key), ' ', true));
	    else if (type == PropertyType.COMMA_SEPARATED_LIST_FIXED)
	      combo = new BaseComboBox(SpreadSheetUtils.split(value.getProperty(key), ',', true));
	    else
	      combo = new BaseComboBox(getList(key));
	    if (hasListDefault(key) && !value.hasKey(key))
	      combo.setSelectedItem(getListDefault(key));
	    else
	      combo.setSelectedItem(value.getProperty(key));
	    if (help != null)
	      combo.setToolTipText(help);
	    addProperty(key, label, combo);
	    break;
	  case BLANK_SEPARATED_LIST:
	  case COMMA_SEPARATED_LIST:
	    chooserPanel = new GenericArrayEditorPanel(new BaseString[0]);
	    chooserPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH_CHOOSERS, chooserPanel.getPreferredSize().height));
	    if (type == PropertyType.BLANK_SEPARATED_LIST)
	      parts = SpreadSheetUtils.split(value.getProperty(key), ' ', true);
	    else
	      parts = SpreadSheetUtils.split(value.getProperty(key), ',', true);
	    list = new BaseString[parts.length];
	    for (i = 0; i < parts.length; i++)
	      list[i] = new BaseString(parts[i]);
	    chooserPanel.setCurrent(list);
	    addProperty(key, label, chooserPanel);
	    break;
	  case OBJECT_EDITOR:
	    chooserPanel = getChooser(key);
	    chooserPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH_CHOOSERS, chooserPanel.getPreferredSize().height));
	    if (help != null)
	      chooserPanel.setToolTipText(help);
	    try {
	      if (!value.getProperty(key).trim().isEmpty())
		chooserPanel.setCurrent(OptionUtils.forAnyCommandLine(Object.class, value.getProperty(key)));
	    }
	    catch (Exception e) {
	      ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to instantiate (" + key + "): " + value.getProperty(key), e);
	    }
	    addProperty(key, label, chooserPanel);
	    break;
	  case ARRAY_EDITOR:
	    if (hasArraySeparator(key))
	      sep = getArraySeparator(key);
	    else
	      sep = ",";
	    chooserPanel = getChooser(key);
	    chooserPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH_CHOOSERS, chooserPanel.getPreferredSize().height));
	    if (help != null)
	      chooserPanel.setToolTipText(help);
	    try {
	      if (!value.getProperty(key).trim().isEmpty()) {
		if (sep.equals(" "))
		  parts = OptionUtils.splitOptions(value.getProperty(key).trim());
		else
		  parts = value.getProperty(key).trim().split(sep);
	      }
	      else {
		parts = new String[0];
	      }
	      array = Array.newInstance(getArrayClass(key), parts.length);
	      for (i = 0; i < parts.length; i++)
		Array.set(array, i, OptionUtils.valueOf(getArrayClass(key), parts[i].trim()));
	      chooserPanel.setCurrent(array);
	    }
	    catch (Exception e) {
	      ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to create array (" + key + "): " + value.getProperty(key), e);
	    }
	    addProperty(key, label, chooserPanel);
	    break;
	  case INDEX:
	    indexText = new IndexTextField();
	    indexText.setColumns(20);
	    indexText.setText(value.getProperty(key));
	    if (help != null)
	      indexText.setToolTipText(help);
	    addProperty(key, label, indexText);
	    break;
	  case RANGE:
	    rangeText = new RangeTextField();
	    rangeText.setColumns(20);
	    rangeText.setText(value.getProperty(key));
	    if (help != null)
	      rangeText.setToolTipText(help);
	    addProperty(key, label, rangeText);
	    break;
	  case REGEXP:
	    regexpText = new RegExpTextField();
	    regexpText.setColumns(20);
	    regexpText.setText(value.getProperty(key));
	    if (help != null)
	      regexpText.setToolTipText(help);
	    addProperty(key, label, regexpText);
	    break;
	  case REGEXP_CONSTRAINED_STRING:
	    regexpConstText = new RegExpConstrainedTextField(hasRegExp(key) ? getRegExp(key) : new BaseRegExp(BaseRegExp.MATCH_ALL));
	    regexpConstText.setColumns(20);
	    regexpConstText.setText(value.getProperty(key));
	    if (help != null)
	      regexpConstText.setToolTipText(help);
	    addProperty(key, label, regexpConstText);
	    break;
	  case CUSTOM_COMPONENT:
	    comp = getComponent(key);
	    setText(comp, value.getProperty(key));
	    updateToolTipText(comp, help);
	    addProperty(key, label, getComponent(key));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled property type (property '" + keys + "'): " + type);
	}
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to set property/type: '" + key + "'/" + type, e);
      }
    }
    invalidate();
    validate();
    repaint();
  }

  /**
   * Fixes the path(s), if necessary.
   *
   * @param property	the property this path is/these paths are from
   * @param path	the path(s) to fix
   * @return		the fixed path(s)
   * @see		PropertyHint#FORWARD_SLASHES
   * @see		PropertyHint#MULTI_FORWARD_SLASHES
   * @see		#getPropertyHints(String)
   */
  protected String fixPath(String property, String path) {
    String[]	paths;
    int		i;

    if (getPropertyHints(property).contains(PropertyHint.FORWARD_SLASHES)) {
      return FileUtils.useForwardSlashes(path);
    }
    else if (getPropertyHints(property).contains(PropertyHint.MULTI_FORWARD_SLASHES)) {
      try {
	paths = OptionUtils.splitOptions(path);
	for (i = 0; i < paths.length; i++)
	  paths[i] = FileUtils.useForwardSlashes(paths[i]);
	return OptionUtils.joinOptions(paths);
      }
      catch (Exception e) {
	LoggingHelper.global().log(Level.SEVERE, "Failed to fix path: " + path, e);
	return path;
      }
    }
    else {
      return path;
    }
  }

  /**
   * Returns the currently display properties as a properties object.
   *
   * @return		the properties
   */
  public Properties getProperties() {
    Properties			result;
    int				i;
    Component			comp;
    PropertyType		type;
    JTextField 			textfield;
    BasePasswordFieldWithButton	pwfield;
    SQLSyntaxEditorPanel	query;
    JCheckBox 			checkbox;
    JSpinner			spinner;
    FontChooserPanel 		fontPanel;
    DirectoryChooserPanel	dirPanel;
    FileChooserPanel		filePanel;
    ColorChooserPanel		colorPanel;
    TimeChooserPanel		timePanel;
    DateChooserPanel		datePanel;
    DateTimeChooserPanel	dateTimePanel;
    AbstractChooserPanel	chooserPanel;
    BaseTextChooserPanel        textPanel;
    IndexTextField		indexText;
    RangeTextField		rangeText;
    RegExpTextField		regexpText;
    RegExpConstrainedTextField	regexpConstText;
    JComboBox			comboEnum;
    JComboBox			comboList;
    SelectOptionPanel		optionPanel;
    BaseString[]		list;
    String			key;
    Object			array;
    String[]			items;
    int				n;
    String			sep;

    result = new Properties();

    for (i = 0; i < getPropertyCount(); i++) {
      comp = getProperty(i);
      key  = m_Identifiers.get(i);
      type = getActualPropertyType(key);

      switch (type) {
	case TIME:
	  timePanel = (TimeChooserPanel) comp;
	  result.setTime(key, timePanel.getCurrent());
	  break;
	case DATE:
	  datePanel = (DateChooserPanel) comp;
	  result.setDate(key, datePanel.getCurrent());
	  break;
	case DATETIME:
	  dateTimePanel = (DateTimeChooserPanel) comp;
	  result.setDateTime(key, dateTimePanel.getCurrent());
	  break;
	case DOUBLE:
	  textfield = (JTextField) comp;
	  result.setProperty(key, textfield.getText());
	  break;
	case STRING:
	  textPanel = (BaseTextChooserPanel) comp;
	  result.setProperty(key, textPanel.getCurrent().getValue());
	  break;
	case PASSWORD:
	  pwfield = (BasePasswordFieldWithButton) comp;
	  result.setPassword(key, new BasePassword(new String(pwfield.getPassword())));
	  break;
	case PASSWORD_PLAIN:
	  pwfield = (BasePasswordFieldWithButton) comp;
	  result.setProperty(key, new String(pwfield.getPassword()));
	  break;
	case SQL:
	  query = (SQLSyntaxEditorPanel) comp;
	  result.setProperty(key, query.getContent());
	  break;
	case BOOLEAN:
	  checkbox = (JCheckBox) comp;
	  result.setBoolean(key, checkbox.isSelected());
	  break;
	case INTEGER:
	  spinner = (JSpinner) comp;
	  result.setInteger(key, ((Number) spinner.getValue()).intValue());
	  break;
	case LONG:
	  spinner = (JSpinner) comp;
	  result.setLong(key, ((Number) spinner.getValue()).longValue());
	  break;
	case FONT:
	  fontPanel = (FontChooserPanel) comp;
	  result.setProperty(key, FontParsing.toString(null, fontPanel.getCurrent()));
	  break;
	case DIRECTORY:
	  dirPanel = (DirectoryChooserPanel) comp;
	  result.setProperty(key, fixPath(key, dirPanel.getCurrent().getPath()));
	  break;
	case DIRECTORY_ABSOLUTE:
	  dirPanel = (DirectoryChooserPanel) comp;
	  result.setProperty(key, fixPath(key, dirPanel.getCurrent().getAbsolutePath()));
	  break;
	case FILE:
	  filePanel = (FileChooserPanel) comp;
	  result.setProperty(key, fixPath(key, filePanel.getCurrent().getPath()));
	  break;
	case FILE_ABSOLUTE:
	  filePanel = (FileChooserPanel) comp;
	  result.setProperty(key, fixPath(key, filePanel.getCurrent().getAbsolutePath()));
	  break;
	case COLOR:
	  colorPanel = (ColorChooserPanel) comp;
	  result.setColor(key, colorPanel.getCurrent());
	  break;
	case ENUM:
	  comboEnum = (JComboBox) comp;
	  if (comboEnum.getSelectedIndex() > -1)
	    result.setProperty(key, "" + comboEnum.getSelectedItem());
	  break;
	case LIST_MULTI_SELECTION:
	  optionPanel = (SelectOptionPanel) comp;
	  result.setProperty(key, OptionUtils.joinOptions(optionPanel.getCurrent()));
	  break;
	case LIST:
	case BLANK_SEPARATED_LIST_FIXED:
	case COMMA_SEPARATED_LIST_FIXED:
	  comboList = (JComboBox) comp;
	  if (comboList.getSelectedIndex() > -1)
	    result.setProperty(key, "" + comboList.getSelectedItem());
	  break;
	case BLANK_SEPARATED_LIST:
	case COMMA_SEPARATED_LIST:
	  chooserPanel = (AbstractChooserPanel) comp;
	  list         = (BaseString[]) chooserPanel.getCurrent();
	  if (type == PropertyType.BLANK_SEPARATED_LIST)
	    result.setProperty(key, Utils.flatten(list, " "));
	  else
	    result.setProperty(key, Utils.flatten(list, ","));
	  break;
	case OBJECT_EDITOR:
	  chooserPanel = (AbstractChooserPanel) comp;
	  result.setProperty(key, OptionUtils.getCommandLine(chooserPanel.getCurrent()));
	  break;
	case ARRAY_EDITOR:
	  chooserPanel = (AbstractChooserPanel) comp;
	  array = chooserPanel.getCurrent();
	  items = new String[Array.getLength(array)];
	  if (hasArraySeparator(key))
	    sep = getArraySeparator(key);
	  else
	    sep = ",";
	  for (n = 0; n < items.length; n++)
	    items[n] = OptionUtils.toString(Array.get(array, n));
	  if (sep.equals(" "))
	    result.setProperty(key, OptionUtils.joinOptions(items));
	  else
	    result.setProperty(key, Utils.flatten(items, sep));
	  break;
	case INDEX:
	  indexText = (IndexTextField) comp;
	  result.setProperty(key, indexText.getText());
	  break;
	case RANGE:
	  rangeText = (RangeTextField) comp;
	  result.setProperty(key, rangeText.getText());
	  break;
	case REGEXP:
	  regexpText = (RegExpTextField) comp;
	  result.setProperty(key, regexpText.getText());
	  break;
	case REGEXP_CONSTRAINED_STRING:
	  regexpConstText = (RegExpConstrainedTextField) comp;
	  result.setProperty(key, regexpConstText.getText());
	  break;
	case CUSTOM_COMPONENT:
	  result.setProperty(key, getText(comp));
	  break;
	default:
	  throw new IllegalStateException("Unhandled property type (property '" + key + "'): " + type);
      }
    }

    return result;
  }

  /**
   * Returns the file chooser to use for loading/saving of props files.
   *
   * @return		the file chooser
   */
  protected synchronized BaseFileChooser getFileChooser() {
    FileFilter	filter;

    if (m_FileChooser == null) {
      m_FileChooser = new BaseFileChooser();
      m_FileChooser.setAutoAppendExtension(true);
      filter        = ExtensionFileFilter.getPropertiesFileFilter();
      m_FileChooser.addChoosableFileFilter(filter);
      m_FileChooser.setFileFilter(filter);
    }

    return m_FileChooser;
  }

  /**
   * Loads properties from a file, prompts the user to select props file.
   */
  protected void loadProperties() {
    int		retVal;
    Properties	props;

    getFileChooser().setDialogTitle("Load settings");
    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    props = new Properties();
    if (!props.load(getFileChooser().getSelectedFile().getAbsolutePath())) {
      GUIHelper.showErrorMessage(this, "Failed to load properties from: " + getFileChooser().getSelectedFile());
      return;
    }

    setProperties(props);
  }

  /**
   * Saves properties to a file, prompts the user to select props file.
   */
  protected void saveProperties() {
    int		retVal;
    Properties	props;

    getFileChooser().setDialogTitle("Save settings");
    retVal = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    props = getProperties();
    if (!props.save(getFileChooser().getSelectedFile().getAbsolutePath()))
      GUIHelper.showErrorMessage(this, "Failed to save properties to: " + getFileChooser().getSelectedFile());
  }

  /**
   * Sets the visibility state of the buttons panel (load/save).
   *
   * @param value	true if to show buttons
   */
  public void setButtonPanelVisible(boolean value) {
    m_PanelButtons.setVisible(value);
  }

  /**
   * Returns the visibility state of the buttons panel (load/save).
   *
   * @return		true if buttons displayed
   */
  public boolean isButtonPanelVisible() {
    return m_PanelButtons.isVisible();
  }

  /**
   * Adds the change listener.
   *
   * @param l		the change listener
   */
  public void addChangeListener(ChangeListener l) {
    m_PanelProperties.addChangeListener(l);
  }

  /**
   * Removes the change listener.
   *
   * @param l		the change listener
   */
  public void removeChangeListener(ChangeListener l) {
    m_PanelProperties.removeChangeListener(l);
  }

  /**
   * Sets whether to interpret "_" in the name as a mnemonic indicator
   * (the next letter is to be used as mnemonic indicator).
   *
   * @param value	true if to use mnemonic indicators
   */
  public void setUseMnemonicIndicators(boolean value) {
    m_PanelProperties.setUseMnemonicIndicators(value);
  }

  /**
   * Returns whether to interpret "_" in the name as a mnemonic indicator
   * (the next letter is to be used as mnemonic indicator).
   *
   * @return		true if to use mnemonic indicators
   */
  public boolean getUseMnemonicIndicators() {
    return m_PanelProperties.getUseMnemonicIndicators();
  }
}