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
 * FileChooserBookmarksManagementPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import adams.core.Properties;
import adams.core.io.PlaceholderDirectory;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.chooser.FileChooserBookmarksPanel.FileChooserBookmarksFactory;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanelWithButtons;
import adams.gui.core.SearchParameters;

import com.googlecode.jfilechooserbookmarks.Bookmark;

/**
 * Panel for managing email addresses.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileChooserBookmarksManagementPanel
  extends AbstractManagementPanelWithProperties<Bookmark> {

  /** for serialization. */
  private static final long serialVersionUID = 2870352856009767535L;

  /**
   * A simple table model for displaying the contacts.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TableModel
    extends AbstractManagementTableModel<Bookmark> {

    /** for serialization. */
    private static final long serialVersionUID = 6097860917524908958L;

    /**
     * the constructor.
     */
    public TableModel() {
      super(false);
    }

    /**
     * the constructor.
     *
     * @param bookmarks	the bookmarks to display
     */
    public TableModel(List<Bookmark> bookmarks) {
      super(bookmarks, false);
    }

    /**
     * the constructor.
     *
     * @param bookmarks	the bookmarks to display
     */
    public TableModel(Bookmark[] bookmarks) {
      super(bookmarks, false);
    }

    /**
     * Returns the number of columns in the table, i.e., 2.
     *
     * @return		the number of columns, always 2
     */
    @Override
    public int getColumnCount() {
      return 2;
    }

    /**
     * Returns the name of the column.
     *
     * @param column 	the column to get the name for
     * @return		the name of the column
     */
    @Override
    public String getColumnName(int column) {
      if (column == 0)
	return "Name";
      else if (column == 1)
	return "Directory";
      else
	throw new IllegalArgumentException("Column " + column + " is invalid!");
    }

    /**
     * Returns the class type of the column.
     *
     * @param columnIndex	the column to get the class for
     * @return			the class for the column
     */
    @Override
    public Class getColumnClass(int columnIndex) {
      if (columnIndex == 0)
	return String.class;
      else if (columnIndex == 1)
	return String.class;
      else
	throw new IllegalArgumentException("Column " + columnIndex + " is invalid!");
    }

    /**
     * Returns the substance value at the given position.
     *
     * @param row	the row
     * @param column	the column (ignored, since only 1 column)
     * @return		the value
     */
    @Override
    public Object getValueAt(int row, int column) {
      if (column == 0) {
	return m_Values.get(row).getName();
      }
      if (column == 1) {
	return m_Values.get(row).getDirectory().toString();
      }
      else {
	throw new IllegalArgumentException("Column " + column + " is invalid!");
      }
    }

    /**
     * Tests whether the search matches the specified row.
     *
     * @param params	the search parameters
     * @param row	the row of the underlying, unsorted model
     * @return		true if the search matches this row
     */
    @Override
    public boolean isSearchMatch(SearchParameters params, int row) {
      // name
      if (params.matches(m_Values.get(row).getName()))
	return true;
      // dir
      if (params.matches(m_Values.get(row).getDirectory().toString()))
	return true;

      return false;
    }
  }

  /** the name. */
  protected JTextField m_TextName;

  /** the directory. */
  protected DirectoryChooserPanel m_PanelDirectory;
  
  /** the move up button. */
  protected JButton m_ButtonUp;
  
  /** the move down button. */
  protected JButton m_ButtonDown;
  
  /** the manager. */
  protected FileChooserBookmarksFactory m_Factory;
  
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Factory = new FileChooserBookmarksFactory(); 
  }
  
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    // up/down buttons
    m_ButtonUp = new JButton(GUIHelper.getIcon("arrow_up.gif"));
    m_ButtonUp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int index = m_TableValues.getSelectedRow();
	if (index == -1)
	  return;
	if (m_ModelValues.moveUp(index)) {
	  storeProperties();
	  m_TableValues.getSelectionModel().setSelectionInterval(index - 1, index - 1);
	}
      }
    });
    m_TableValues.addToButtonsPanel(m_ButtonUp);

    m_ButtonDown = new JButton(GUIHelper.getIcon("arrow_down.gif"));
    m_ButtonDown.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int index = m_TableValues.getSelectedRow();
	if (index == -1)
	  return;
	if (m_ModelValues.moveDown(index)) {
	  storeProperties();
	  m_TableValues.getSelectionModel().setSelectionInterval(index + 1, index + 1);
	}
      }
    });
    m_TableValues.addToButtonsPanel(m_ButtonDown);
  }

  /**
   * For adding all the fields.
   * 
   * @param panel	the panel to add the fields to
   */
  @Override
  protected void addFields(ParameterPanelWithButtons panel) {
    m_TextName = new JTextField(30);
    addListener(m_TextName);
    panel.addParameter("_Name", m_TextName);
    
    m_PanelDirectory = new DirectoryChooserPanel();
    addListener(m_PanelDirectory);
    panel.addParameter("_Directory", m_PanelDirectory);
  }
  
  /**
   * Returns the properties to work with. Loads them, if necessary.
   * 
   * @return		the properties
   */
  @Override
  protected synchronized Properties getProperties() {
    Properties			result;
    java.util.Properties	props;
    
    props = m_Factory.getBookmarksManager().getProperties();
    if (!(props instanceof Properties))
      result = new Properties(props);
    else
      result = (Properties) props;
    
    return result;
  }

  /**
   * Saves the properties on disk.
   * 
   * @return		true if successfully saved
   */
  @Override
  protected boolean storeProperties() {
    return m_Factory.getBookmarksManager().save(m_ModelValues.toList());
  }
  
  /**
   * Loads all the objects.
   * 
   * @return		all available Objects
   */
  @Override
  protected List<Bookmark> loadAll() {
    return m_Factory.getBookmarksManager().load();
  }
  
  /**
   * Stores the object.
   * 
   * @param value	the value to store
   * @return		true if successfully stored
   */
  @Override
  protected boolean store(Bookmark value) {
    m_ModelValues.add(value);
    return storeProperties();
  }
  
  /**
   * Removes the object.
   * 
   * @param value	the value to remove
   * @return		true if successfully removed
   */
  @Override
  protected boolean remove(Bookmark value) {
    m_ModelValues.remove(value);
    return storeProperties();
  }

  /**
   * Creates the key for storing the object in the properties.
   * 
   * @param value	the object to create the key from
   * @return		the generated key
   */
  @Override
  protected String createKey(Bookmark value) {
    return value.getName();
  }

  /**
   * Turns the string obtained from the properties file into an object.
   * 
   * @param s		the string to parse
   * @return		always null
   */
  @Override
  protected Bookmark fromString(String s) {
    return null;
  }

  /**
   * Turns the object into a string to be stored in the properties file.
   * 
   * @param value	the object to convert
   * @return		always the name
   */
  @Override
  protected String toString(Bookmark value) {
    return value.getName();
  }

  /**
   * Returns an instance of a new table model.
   * 
   * @return		the table model
   */
  @Override
  protected AbstractManagementTableModel<Bookmark> newTableModel() {
    return new TableModel();
  }

  /**
   * Returns the class that is being managed.
   * 
   * @return		the class being managed
   */
  @Override
  protected Class getManagedClass() {
    return Bookmark.class;
  }

  /**
   * Resets the input fields.
   */
  @Override
  protected void clear() {
    m_TextName.setText("");
    m_PanelDirectory.setCurrent(new PlaceholderDirectory("."));
  }

  /**
   * Turns the fields into an object.
   * 
   * @return		the generated object
   */
  @Override
  protected Bookmark fieldsToObject() {
    return new Bookmark(m_TextName.getText().trim(), new PlaceholderDirectory(m_PanelDirectory.getCurrent()));
  }

  /**
   * Updates the field with the specified object.
   * 
   * @param value	the object to display
   */
  @Override
  protected void objectToFields(Bookmark value) {
    m_TextName.setText(value.getName());
    m_PanelDirectory.setCurrent(value.getDirectory());
  }

  /**
   * Returns whether the fields can be cleared, i.e., if there is any input.
   * 
   * @return		true if input can be cleared
   */
  @Override
  protected boolean canClearFields() {
    return (m_TextName.getText().length() > 0);
  }

  /**
   * Returns whether modified data cannot be stored.
   * 
   * @return		true if storing is not available
   */
  @Override
  protected boolean isReadOnly() {
    return false;
  }

  /**
   * Returns whether all the required fields are set to add the object.
   * 
   * @return		true if required fields are filled in
   */
  @Override
  protected boolean canAddObject() {
    return (m_TextName.getText().trim().length() > 0);
  }
  
  /**
   * Updates the enabled state of the widgets.
   */
  @Override
  protected void update() {
    super.update();
    
    m_ButtonUp.setEnabled((m_TableValues.getSelectedRowCount() == 1) && (m_TableValues.getSelectedRow() > 0));
    m_ButtonDown.setEnabled((m_TableValues.getSelectedRowCount() == 1) && (m_TableValues.getSelectedRow() < m_TableValues.getRowCount() - 1));
  }
}
