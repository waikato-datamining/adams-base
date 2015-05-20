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
 * SelectEmailAddressPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.selection;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.net.EmailAddress;
import adams.core.net.EmailAddressBook;
import adams.core.net.EmailContact;
import adams.core.option.OptionUtils;
import adams.gui.core.BaseDialog;
import adams.gui.core.SearchParameters;
import adams.gui.tools.EmailAddressBookPanel;

/**
 * Allows the selection of email addresses.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectEmailAddressPanel
  extends AbstractTableBasedSelectionPanel<EmailContact> {

  /** for serialization. */
  private static final long serialVersionUID = 8172361673639083461L;

  /**
   * A simple table model for displaying the contacts.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TableModel
    extends AbstractSelectionTableModel<EmailContact> {

    /** for serialization. */
    private static final long serialVersionUID = 6097860917524908958L;

    /** the contacts. */
    protected List<EmailContact> m_Values;
    
    /**
     * the constructor.
     */
    public TableModel() {
      super();
      m_Values = new ArrayList<EmailContact>();
    }

    /**
     * the constructor.
     *
     * @param contacts	the contacts to display
     */
    public TableModel(List<EmailContact> contacts) {
      this();
      m_Values.addAll(contacts);
    }

    /**
     * the constructor.
     *
     * @param contacts	the contacts to display
     */
    public TableModel(EmailContact[] contacts) {
      this();
      m_Values.addAll(Arrays.asList(contacts));
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

    /**
     * Returns the number of contacts.
     * 
     * @return		the number of contacts/rows
     */
    @Override
    public int getRowCount() {
      return m_Values.size();
    }

    /**
     * Returns the item at the specified position.
     *
     * @param row	the (actual, not visible) position of the item
     * @return		the item at the position, null if not valid index
     */
    @Override
    public EmailContact getItemAt(int row) {
      return m_Values.get(row);
    }

    /**
     * Returns the index of the given (visible) item, -1 if not found.
     *
     * @param item	the item to look for
     * @return		the index, -1 if not found
     */
    @Override
    public int indexOf(EmailContact item) {
      int	result;
      int	i;
      
      result = -1;
      
      for (i = 0; i < m_Values.size(); i++) {
	if (m_Values.get(i).getEmail().equals(item.getEmail())) {
	  result = i;
	  break;
	}
      }
      
      return result;
    }
  }

  /** the label for the manual list. */
  protected JLabel m_LabelManualAddresses;

  /** the textfield for manually entering a list of addresses (blank separated). */
  protected JTextField m_TextManualAddresses;

  /** the button for bringing up the addressbook. */
  protected JButton m_ButtonAddressBook;
  
  /**
   * Initializes the GUI elements.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_TableData.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (m_TableData.getSelectedRowCount() > 0)
	  m_TextManualAddresses.setText("");
      }
    });

    m_TextManualAddresses  = new JTextField(15);
    m_LabelManualAddresses = new JLabel();
    m_LabelManualAddresses.setLabelFor(m_LabelManualAddresses);

    m_ButtonAddressBook = new JButton("Addressbook");
    m_ButtonAddressBook.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	openAddressBook();
      }
    });
    
    m_SearchPanel.addToWidgetsPanel(m_LabelManualAddresses);
    m_SearchPanel.addToWidgetsPanel(m_TextManualAddresses);
    m_SearchPanel.addToWidgetsPanel(m_ButtonAddressBook);

    // update "manual" label
    setMultipleSelection(isMultipleSelection());

    m_TableData.setOptimalColumnWidth();
  }

  /**
   * finishes the initialization, loads the contacts.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    reload();
  }

  /**
   * Returns an empty table model.
   *
   * @return		the model
   */
  @Override
  protected AbstractSelectionTableModel<EmailContact> newTableModel() {
    return new TableModel();
  }

  /**
   * Returns the class of the items displayed, same as "T".
   *
   * @return		the class of the items
   */
  @Override
  protected Class getItemClass() {
    return EmailContact.class;
  }

  /**
   * Returns whether to add the item really to the missing list.
   * 
   * @param item	the item to check
   * @return		true if to add the item to the missing list, false otherwise
   */
  @Override
  protected boolean addToMissing(EmailContact item) {
    if (item.getEmail().trim().length() == 0)
      return false;
    if (item.getEmail().equals(EmailAddress.DUMMY_ADDRESS))
      return false;
    return super.addToMissing(item);
  }

  /**
   * Hook method for processing items that were not found when trying to
   * select them initially.
   * <br><br>
   * The missing items are displayed in the "manual" field.
   *
   * @param missing	the missing items
   */
  @Override
  protected void processMissing(List<EmailContact> missing) {
    String[]	list;
    int		i;

    if (isMultipleSelection()) {
      list = new String[missing.size()];
      for (i = 0; i < missing.size(); i++)
	list[i] = missing.get(i).toString();
      m_TextManualAddresses.setText(OptionUtils.joinOptions(list));
    }
    else {
      m_TextManualAddresses.setText(missing.get(0).toString());
    }
  }

  /**
   * Returns the current addresses. Manually entered ones have precedence over
   * the selected ones.
   *
   * @return		the currently entered/selected addresses
   */
  @Override
  protected EmailContact[] getCurrentItems() {
    EmailContact[]	result;
    String[]		addresses;
    int			i;

    if (m_TextManualAddresses.getText().length() > 0) {
      try {
	if (isMultipleSelection())
	  addresses = OptionUtils.splitOptions(m_TextManualAddresses.getText());
	else
	  addresses = new String[]{m_TextManualAddresses.getText()};
	result = (EmailContact[]) Array.newInstance(getItemClass(), addresses.length);
	for (i = 0; i < addresses.length; i++) {
	  result[i] = new EmailContact();
	  result[i].setEmail(addresses[i]);
	}
      }
      catch (Exception e) {
	e.printStackTrace();
	result = null;
      }
    }
    else {
      result = super.getCurrentItems();
    }

    return result;
  }

  /**
   * Sets whether multiple or single selection is used.
   *
   * @param value	if true multiple IDs can be selected
   */
  @Override
  public void setMultipleSelection(boolean value) {
    super.setMultipleSelection(value);

    if (value)
      m_LabelManualAddresses.setText("Manual addresses");
    else
      m_LabelManualAddresses.setText("Manual address");
  }

  /**
   * Reloads the data from the properties.
   */
  protected void reload() {
    m_TableDataModel = new TableModel(EmailAddressBook.getSingleton().getContacts());
    m_TableData.setModel(m_TableDataModel);
    m_TableData.setOptimalColumnWidth();
  }

  /**
   * Opens the address book.
   */
  protected void openAddressBook() {
    BaseDialog			dialog;
    EmailAddressBookPanel	panel;
    
    if (getParentDialog() != null)
      dialog = new BaseDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new BaseDialog(getParentFrame(), true);
    dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Email addressbook");
    panel = new EmailAddressBookPanel();
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(800, 800);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    
    reload();
  }
}
