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
 * EmailAddressBookPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.util.List;

import javax.swing.JTextField;

import adams.core.Properties;
import adams.core.net.EmailAddress;
import adams.core.net.EmailAddressBook;
import adams.core.net.EmailContact;
import adams.gui.core.BaseTextArea;
import adams.gui.core.ParameterPanelWithButtons;
import adams.gui.core.SearchParameters;

/**
 * Panel for managing email addresses.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailAddressBookPanel
  extends AbstractManagementPanelWithProperties<EmailContact> {

  /** for serialization. */
  private static final long serialVersionUID = 2870352856009767535L;

  /**
   * A simple table model for displaying the contacts.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TableModel
    extends AbstractManagementTableModel<EmailContact> {

    /** for serialization. */
    private static final long serialVersionUID = 6097860917524908958L;

    /**
     * the constructor.
     */
    public TableModel() {
      super();
    }

    /**
     * the constructor.
     *
     * @param contacts	the contacts to display
     */
    public TableModel(List<EmailContact> contacts) {
      super(contacts);
    }

    /**
     * the constructor.
     *
     * @param contacts	the contacts to display
     */
    public TableModel(EmailContact[] contacts) {
      super(contacts);
    }

    /**
     * Returns the number of columns in the table, i.e., 6.
     *
     * @return		the number of columns, always 6
     */
    @Override
    public int getColumnCount() {
      return 6;
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
	return "First";
      else if (column == 1)
	return "Last";
      else if (column == 2)
	return "Email";
      else if (column == 3)
	return "Phone";
      else if (column == 4)
	return "Address";
      else if (column == 5)
	return "Note";
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
      else if (columnIndex == 2)
	return String.class;
      else if (columnIndex == 3)
	return String.class;
      else if (columnIndex == 4)
	return String.class;
      else if (columnIndex == 5)
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
	return m_Values.get(row).getFirstName();
      }
      if (column == 1) {
	return m_Values.get(row).getLastName();
      }
      else if (column == 2) {
	return m_Values.get(row).getEmail();
      }
      else if (column == 3) {
	return m_Values.get(row).getPhone();
      }
      else if (column == 4) {
	return m_Values.get(row).getAddress();
      }
      else if (column == 5) {
	return m_Values.get(row).getNote();
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
      // email
      if (params.matches(m_Values.get(row).getEmail()))
	return true;
      // first
      if (params.matches(m_Values.get(row).getFirstName()))
	return true;
      // last
      if (params.matches(m_Values.get(row).getLastName()))
	return true;
      // note
      if (params.matches(m_Values.get(row).getNote()))
	return true;
      // phone
      if (params.matches(m_Values.get(row).getPhone()))
	return true;
      // address
      if (params.matches(m_Values.get(row).getAddress()))
	return true;

      return false;
    }
  }

  /** the first name. */
  protected JTextField m_TextFirstName;

  /** the last name. */
  protected JTextField m_TextLastName;

  /** the email address. */
  protected JTextField m_TextEmail;

  /** the phone number. */
  protected BaseTextArea m_TextPhone;

  /** the address. */
  protected BaseTextArea m_TextAddress;

  /** the note. */
  protected BaseTextArea m_TextNote;

  /**
   * For adding all the fields.
   * 
   * @param panel	the panel to add the fields to
   */
  @Override
  protected void addFields(ParameterPanelWithButtons panel) {
    m_TextFirstName = new JTextField(30);
    addListener(m_TextFirstName);
    panel.addParameter("_First", m_TextFirstName);
    
    m_TextLastName = new JTextField(30);
    addListener(m_TextLastName);
    panel.addParameter("_Last", m_TextLastName);
    
    m_TextEmail = new JTextField(30);
    addListener(m_TextEmail);
    panel.addParameter("_Email", m_TextEmail);
    
    m_TextPhone = new BaseTextArea(2, 30);
    addListener(m_TextPhone);
    panel.addParameter("_Phone", m_TextPhone);
    
    m_TextAddress = new BaseTextArea(3, 30);
    addListener(m_TextAddress);
    panel.addParameter("_Address", m_TextAddress);
    
    m_TextNote = new BaseTextArea(3, 30);
    addListener(m_TextNote);
    panel.addParameter("_Note", m_TextNote);
  }
  
  /**
   * Returns the properties to work with. Loads them, if necessary.
   * 
   * @return		the properties
   */
  @Override
  protected synchronized Properties getProperties() {
    return EmailAddressBook.getSingleton().getProperties();
  }

  /**
   * Saves the properties on disk.
   * 
   * @return		true if successfully saved
   */
  @Override
  protected boolean storeProperties() {
    return EmailAddressBook.getSingleton().save();
  }

  /**
   * Creates the key for storing the object in the properties.
   * 
   * @param value	the object to create the key from
   * @return		the generated key
   */
  @Override
  protected String createKey(EmailContact value) {
    return EmailAddressBook.createKey(value);
  }

  /**
   * Turns the string obtained from the properties file into an object.
   * 
   * @param s		the string to parse
   * @return		the generated object, null if failed to generate
   */
  @Override
  protected EmailContact fromString(String s) {
    return EmailAddressBook.fromString(s);
  }

  /**
   * Turns the object into a string to be stored in the properties file.
   * 
   * @param value	the object to convert
   * @return		the generated strings
   */
  @Override
  protected String toString(EmailContact value) {
    return EmailAddressBook.toString(value);
  }

  /**
   * Returns an instance of a new table model.
   * 
   * @return		the table model
   */
  @Override
  protected AbstractManagementTableModel<EmailContact> newTableModel() {
    return new TableModel();
  }

  /**
   * Returns the class that is being managed.
   * 
   * @return		the class being managed
   */
  @Override
  protected Class getManagedClass() {
    return EmailContact.class;
  }

  /**
   * Resets the input fields.
   */
  @Override
  protected void clear() {
    m_TextFirstName.setText("");
    m_TextLastName.setText("");
    m_TextEmail.setText("");
    m_TextPhone.setText("");
    m_TextAddress.setText("");
    m_TextNote.setText("");
  }

  /**
   * Turns the fields into an object.
   * 
   * @return		the generated object
   */
  @Override
  protected EmailContact fieldsToObject() {
    EmailContact	result;
    
    result = new EmailContact();
    result.setFirstName(m_TextFirstName.getText().trim());
    result.setLastName(m_TextLastName.getText().trim());
    result.setEmail(m_TextEmail.getText().trim());
    result.setAddress(m_TextAddress.getText());
    result.setPhone(m_TextPhone.getText());
    result.setNote(m_TextNote.getText());
    
    return result;
  }

  /**
   * Updates the field with the specified object.
   * 
   * @param value	the object to display
   */
  @Override
  protected void objectToFields(EmailContact value) {
    m_TextFirstName.setText(value.getFirstName());
    m_TextLastName.setText(value.getLastName());
    m_TextEmail.setText(value.getEmail());
    m_TextAddress.setText(value.getAddress());
    m_TextPhone.setText(value.getPhone());
    m_TextNote.setText(value.getNote());
  }

  /**
   * Returns whether the fields can be cleared, i.e., if there is any input.
   * 
   * @return		true if input can be cleared
   */
  @Override
  protected boolean canClearFields() {
    return 
	   (m_TextFirstName.getText().length() > 0)
	|| (m_TextLastName.getText().length() > 0)
	|| (m_TextEmail.getText().length() > 0)
	|| (m_TextAddress.getText().length() > 0)
	|| (m_TextPhone.getText().length() > 0)
	|| (m_TextNote.getText().length() > 0);
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
    return 
	   (m_TextEmail.getText().trim().length() > 0) 
	&& new EmailAddress().isValid(m_TextEmail.getText().trim());
  }
}
