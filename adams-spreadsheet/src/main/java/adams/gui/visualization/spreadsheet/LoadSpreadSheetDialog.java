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
 * LoadSpreadSheetDialog.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.spreadsheet;

import adams.core.Index;
import adams.core.Range;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooserPanel;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.event.SearchEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * A dialog for loading spreadsheets from disk.
 *
 * @author  fracete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11506 $
 */
public class LoadSpreadSheetDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 3881690262061461134L;

  /** the "no sorting" constant. */
  public final static String NO_SORTING = "-no sorting-";

  /** the "no id" constant. */
  public final static String NO_ID = "-no ID-";

  /** the dialog itself. */
  protected LoadSpreadSheetDialog m_Self;

  /** for selecting the dataset file. */
  protected SpreadSheetFileChooserPanel m_FilePanel;

  /** the button for reloading an existing file. */
  protected JButton m_ButtonReload;

  /** the table for displaying the instances. */
  protected SpreadSheetTable m_TableData;

  /** the Load button. */
  protected JButton m_ButtonLoad;

  /** the Close button. */
  protected JButton m_ButtonClose;

  /** the search panel. */
  protected SearchPanel m_SearchPanel;

  /** the sorting index. */
  protected JComboBox m_ComboBoxSorting;

  /** the soriting index model. */
  protected DefaultComboBoxModel m_ComboBoxSortingModel;

  /** the ID index. */
  protected JComboBox m_ComboBoxID;

  /** the ID index model. */
  protected DefaultComboBoxModel m_ComboBoxIDModel;

  /** the list of additional attribute values to store in the report. */
  protected JList m_ListAdditionalAttributes;

  /** the model for the additional attributes. */
  protected DefaultListModel m_ListAdditionalAttributesModel;

  /** the text field for the attribute range. */
  protected JTextField m_TextAttributeRange;

  /** the default range. */
  protected Range m_DefaultAttributeRange;

  /** the full dataset. */
  protected SpreadSheet m_Sheet;

  /** the selected indices. */
  protected int[] m_Indices;

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;

  /** the default class index. */
  protected Index m_DefaultClassIndex;

  /** the default ID index. */
  protected Index m_DefaultIDIndex;

  /** the default sort index. */
  protected Index m_DefaultSortIndex;

  /** the listener waiting for the user to accept the input. */
  protected ChangeListener m_AcceptListener;

  /** the listener waiting for the user to cancel the dialog. */
  protected ChangeListener m_CancelListener;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public LoadSpreadSheetDialog(Dialog owner) {
    this(owner, "Load spreadsheet");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public LoadSpreadSheetDialog(Dialog owner, String title) {
    super(owner, title, ModalityType.MODELESS);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public LoadSpreadSheetDialog(Frame owner) {
    this(owner, "Load dataset");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public LoadSpreadSheetDialog(Frame owner, String title) {
    super(owner, title, false);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Self                          = this;
    m_Sheet                         = null;
    m_ComboBoxSortingModel          = new DefaultComboBoxModel();
    m_ComboBoxSortingModel.addElement(NO_SORTING);
    m_ComboBoxIDModel               = new DefaultComboBoxModel();
    m_ComboBoxIDModel.addElement(NO_ID);
    m_ListAdditionalAttributesModel = new DefaultListModel();
    m_DefaultClassIndex             = new Index();
    m_DefaultIDIndex                = new Index();
    m_DefaultSortIndex              = new Index();
    m_DefaultAttributeRange         = new Range(Range.ALL);
    m_AcceptListener                = null;
    m_CancelListener                = null;
  }

  /**
   * Initializes the GUI elements.
   */
  protected void initGUI() {
    JPanel	panelAll;
    JPanel	contentPanel;
    JPanel	panel;
    JPanel	panelInstances;
    JPanel	panelAttributes;
    JLabel	label;

    super.initGUI();

    getContentPane().setLayout(new BorderLayout());
    contentPanel = new JPanel(new BorderLayout());
    getContentPane().add(contentPanel, BorderLayout.CENTER);

    panelAll = new JPanel(new BorderLayout(5, 5));
    contentPanel.add(panelAll, BorderLayout.CENTER);

    // dataset
    panel = new JPanel(new BorderLayout());
    panelAll.add(panel, BorderLayout.NORTH);
    m_FilePanel = new SpreadSheetFileChooserPanel();
    m_FilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    m_FilePanel.setPrefix("File");
    m_FilePanel.addChangeListener((ChangeEvent e) -> {
      m_ButtonLoad.setEnabled(getFilename().length() > 0);
      if (m_ButtonLoad.isEnabled())
        loadFile(false);
      m_ButtonReload.setEnabled(m_ButtonLoad.isEnabled());
    });
    panel.add(m_FilePanel, BorderLayout.CENTER);

    m_ButtonReload = new JButton(GUIHelper.getIcon("refresh.gif"));
    m_ButtonReload.setEnabled(false);
    m_ButtonReload.addActionListener((ActionEvent e) -> loadFile(true));
    panelAttributes = new JPanel(new BorderLayout());
    panelAttributes.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    panelAttributes.add(m_ButtonReload, BorderLayout.CENTER);
    panel.add(panelAttributes, BorderLayout.EAST);

    // tabbed pane
    m_TabbedPane = new BaseTabbedPane();
    panelAll.add(m_TabbedPane, BorderLayout.CENTER);

    // Instances tab
    panelInstances = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Instances", panelInstances);
    m_TableData = new SpreadSheetTable(new SpreadSheetTableModel());
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
    panel.add(new BaseScrollPane(m_TableData), BorderLayout.CENTER);
    panelInstances.add(panel, BorderLayout.CENTER);

    // search
    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_SearchPanel.setTextColumns(15);
    m_SearchPanel.addSearchListener((SearchEvent e) -> search());
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_SearchPanel);
    panel.setBorder(BorderFactory.createEmptyBorder());
    panelInstances.add(panel, BorderLayout.SOUTH);

    // attributes tab
    panelAttributes = new JPanel(new BorderLayout());
    panelAttributes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_TabbedPane.addTab("Attributes", panelAttributes);

    panel = new JPanel(new GridLayout(14, 1));
    panelAttributes.add(panel, BorderLayout.NORTH);

    // ID
    m_ComboBoxID = new JComboBox(m_ComboBoxIDModel);
    m_ComboBoxID.setSelectedIndex(0);
    m_ComboBoxID.setToolTipText("The selected attribute gets used as ID for the instances, rather than the row index");
    label = new JLabel("ID");
    label.setDisplayedMnemonic('I');
    label.setLabelFor(m_ComboBoxID);
    panel.add(label);
    panel.add(m_ComboBoxID);

    // attribute range
    m_TextAttributeRange = new JTextField(10);
    m_TextAttributeRange.setText(Range.ALL);
    m_TextAttributeRange.setToolTipText("For limiting the attributes being displayed");
    label = new JLabel("Attribute range");
    label.setDisplayedMnemonic('r');
    label.setLabelFor(m_TextAttributeRange);
    panel.add(label);
    panel.add(m_TextAttributeRange);

    // sorting
    m_ComboBoxSorting = new JComboBox(m_ComboBoxSortingModel);
    m_ComboBoxSorting.setSelectedIndex(0);
    m_ComboBoxSorting.setToolTipText("The attribute to sort the data on (ascending)");
    label = new JLabel("Sorting");
    label.setDisplayedMnemonic('r');
    label.setLabelFor(m_ComboBoxSorting);
    panel.add(label);
    panel.add(m_ComboBoxSorting);

    // additional attributes
    m_ListAdditionalAttributes = new JList(m_ListAdditionalAttributesModel);
    m_ListAdditionalAttributes.setToolTipText("Additional attribute values to store in the report");
    label = new JLabel("Attributes to store in report");
    label.setLabelFor(m_ListAdditionalAttributes);
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    panelAttributes.add(panel, BorderLayout.CENTER);
    panel.add(label, BorderLayout.NORTH);
    panel.add(new BaseScrollPane(m_ListAdditionalAttributes), BorderLayout.CENTER);

    // buttons
    panelAll = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    contentPanel.add(panelAll, BorderLayout.SOUTH);

    m_ButtonLoad = new JButton("OK");
    m_ButtonLoad.setMnemonic('O');
    m_ButtonLoad.setEnabled(false);
    m_ButtonLoad.addActionListener((ActionEvent e) -> acceptSelection());
    panelAll.add(m_ButtonLoad);

    m_ButtonClose = new JButton("Cancel");
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addActionListener((ActionEvent e) -> close());
    panelAll.add(m_ButtonClose);

    pack();
    setLocationRelativeTo(getOwner());
  }

  /**
   * Returns the attributes indices of the original dataset to include in the
   * reports.
   *
   * @return		the indices of the attributes
   */
  public int[] getAdditionalAttributes() {
    return m_ListAdditionalAttributes.getSelectedIndices();
  }

  /**
   * Sets the default range of attributes to use.
   *
   * @param value	the range
   */
  public void setDefaultAttributeRange(String value) {
    m_DefaultAttributeRange.setRange(value);
  }

  /**
   * Returns the default range of attributes to use.
   *
   * @return		the range of attributes to use
   */
  public String getDefaultAttributeRange() {
    return m_DefaultAttributeRange.getRange();
  }

  /**
   * Returns the current range of attributes to use.
   *
   * @return		the range of attributes to use
   */
  public Range getCurrentAttributeRange() {
    return new Range(m_TextAttributeRange.getText());
  }

  /**
   * Sets the default ID index to use.
   *
   * @param value	the ID index, 1-based integer or 'first'/'last',
   * 			use empty string for none
   * @see		Index
   */
  public void setDefaultIDIndex(String value) {
    m_DefaultIDIndex.setIndex(value);
  }

  /**
   * Returns the default ID index in use.
   *
   * @return		the ID index, 1-based integer or 'first'/'last',
   * 			empty string for none
   * @see		Index
   */
  public String getDefaultIDIndex() {
    return m_DefaultIDIndex.getIndex();
  }

  /**
   * Returns the currently selected ID index.
   *
   * @return		the ID index, -1 if none selected
   */
  public int getCurrentIDIndex() {
    if (m_ComboBoxID.getSelectedIndex() > -1)
      return m_ComboBoxID.getSelectedIndex() - 1;
    else
      return -1;
  }

  /**
   * Sets the default sort index to use.
   *
   * @param value	the sort index, 1-based integer or 'first'/'last',
   * 			use empty string for none
   * @see		Index
   */
  public void setDefaultSortIndex(String value) {
    m_DefaultSortIndex.setIndex(value);
  }

  /**
   * Returns the default sort index in use.
   *
   * @return		the sort index, 1-based integer or 'first'/'last',
   * 			empty string for none
   * @see		Index
   */
  public String getDefaultSortIndex() {
    return m_DefaultSortIndex.getIndex();
  }

  /**
   * Returns the currently selected sort index.
   *
   * @return		the sort index, -1 if none selected
   */
  public int getCurrentSortIndex() {
    if (m_ComboBoxSorting.getSelectedIndex() > -1)
      return m_ComboBoxSorting.getSelectedIndex() - 1;
    else
      return -1;
  }

  /**
   * Returns the full spreadsheet, can be null if none loaded.
   *
   * @return		the full spreadsheet
   */
  public SpreadSheet getSpreadSheet() {
    int		index;
    SpreadSheet	result;

    result = m_Sheet.getClone();
    if (m_ComboBoxSorting.getSelectedIndex() > 0)
      result.sort(m_ComboBoxSorting.getSelectedIndex() - 1, true);

    return result;
  }

  /**
   * Returns the indices of the (actual) selected rows.
   *
   * @return		the indices, null if "Close" was selected
   */
  public int[] getIndices() {
    return m_Indices;
  }

  /**
   * Returns the currently selected filename, "" if none selected.
   *
   * @return		the filename, "" if none selected
   */
  public String getFilename() {
    return m_FilePanel.getCurrent().getAbsolutePath();
  }

  /**
   * Loads the file and displays the IDs.
   *
   * @param reload	whether we're loading a new file or just reloading
   * 			the current one
   */
  protected void loadFile(boolean reload) {
    int			i;
    String		oldID;
    int			oldIDIndex;
    String		oldSorting;
    int			oldSortingIndex;
    int[]		oldAdditional;

    if (!m_FilePanel.getCurrent().isFile())
      return;

    // ID
    oldID = null;
    if (reload && (m_Sheet != null)) {
      oldIDIndex = m_ComboBoxID.getSelectedIndex() - 1;
      if (oldIDIndex > -1)
	oldID = m_Sheet.getColumnName(oldIDIndex);
    }

    // sorting
    oldSorting = null;
    if (reload && (m_Sheet != null)) {
      oldSortingIndex = m_ComboBoxSorting.getSelectedIndex() - 1;
      if (oldSortingIndex > -1)
	oldSorting = m_Sheet.getColumnName(oldSortingIndex);
    }

    // additional attributes
    oldAdditional = new int[0];
    if (reload && (m_Sheet != null)) {
      oldAdditional = m_ListAdditionalAttributes.getSelectedIndices();
    }

    try {
      if (m_FilePanel.getReader() == null)
	throw new IllegalStateException("Cannot determine reader for file!");
      m_Sheet = m_FilePanel.getReader().read(m_FilePanel.getCurrent());
      m_TextAttributeRange.setToolTipText("#columns: " + m_Sheet.getColumnCount());
    }
    catch (Exception e) {
      m_TextAttributeRange.setToolTipText(null);
      m_Sheet = null;
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  this, "Error loading file '" + getFilename() + "' - exception:\n" + e);
    }

    if (!reload)
      m_TextAttributeRange.setText(m_DefaultAttributeRange.getRange());
    m_ComboBoxIDModel.removeAllElements();
    m_ComboBoxIDModel.addElement(NO_ID);
    m_ComboBoxID.setSelectedIndex(0);
    m_ComboBoxSortingModel.removeAllElements();
    m_ComboBoxSortingModel.addElement(NO_SORTING);
    m_ComboBoxSorting.setSelectedIndex(0);
    m_ListAdditionalAttributesModel.clear();
    if (m_Sheet != null) {
      oldIDIndex      = -1;
      oldSortingIndex = -1;
      m_TableData.setModel(new SpreadSheetTableModel(m_Sheet));
      for (i = 0; i < m_Sheet.getColumnCount(); i++) {
	m_ComboBoxIDModel.addElement((i+1) + ": " + m_Sheet.getColumnName(i));
	m_ComboBoxSortingModel.addElement((i+1) + ": " + m_Sheet.getColumnName(i));
	m_ListAdditionalAttributesModel.addElement((i+1) + ": " + m_Sheet.getColumnName(i));
	if (oldID != null) {
	  if (m_Sheet.getColumnName(i).equals(oldID)) {
	    oldIDIndex = i;
	  }
	}
	if (oldSorting != null) {
	  if (m_Sheet.getColumnName(i).equals(oldSorting)) {
	    oldSortingIndex = i;
	  }
	}
      }
      // ID index
      if (oldIDIndex != -1) {
	m_ComboBoxID.setSelectedIndex(oldIDIndex + 1);  // +1 because of NO_ID at index 0
      }
      else if (!reload && m_DefaultIDIndex.hasIndex()) {
	m_DefaultIDIndex.setMax(m_Sheet.getColumnCount());
	if (m_DefaultIDIndex.getIntIndex() != -1)
	  m_ComboBoxID.setSelectedIndex(m_DefaultIDIndex.getIntIndex() + 1);  // +1 because of NO_ID at index 0
      }
      // sort index
      if (oldSortingIndex != -1) {
	m_ComboBoxSorting.setSelectedIndex(oldSortingIndex + 1);  // +1 because of NO_SORTING at index 0
      }
      else if (!reload && m_DefaultSortIndex.hasIndex()) {
	m_DefaultSortIndex.setMax(m_Sheet.getColumnCount());
	if (m_DefaultSortIndex.getIntIndex() != -1)
	  m_ComboBoxSorting.setSelectedIndex(m_DefaultSortIndex.getIntIndex() + 1);  // +1 because of NO_SORTING at index 0
      }
      // additional attributes
      m_ListAdditionalAttributes.setSelectedIndices(oldAdditional);
    }
    else {
      m_ButtonLoad.setEnabled(false);
    }
  }

  /**
   * Generates the indices.
   */
  protected void acceptSelection() {
    int		i;
    int[]	indices;

    if (m_TableData.getSelectedRowCount() == 0) {
      indices = new int[m_Sheet.getRowCount()];
      for (i = 0; i < indices.length; i++)
	indices[i] = i;
    }
    else {
      indices = m_TableData.getSelectedRows();
    }

    m_Indices = new int[indices.length];
    for (i = 0; i < indices.length; i++)
      m_Indices[i] = ((Integer) m_TableData.getValueAt(indices[i], 0)) - 2;

    setVisible(false);

    if (m_AcceptListener != null)
      m_AcceptListener.stateChanged(new ChangeEvent(this));
  }

  /**
   * Performs a search in the fields.
   */
  protected void search() {
    String	search;

    search = m_SearchPanel.getSearchText().trim();
    if (search.length() == 0)
      search = null;
    m_TableData.search(search, m_SearchPanel.isRegularExpression());
    m_SearchPanel.grabFocus();
  }

  /**
   * Closes the dialog.
   */
  protected void close() {
    m_Indices = null;

    setVisible(false);

    if (m_CancelListener != null)
      m_CancelListener.stateChanged(new ChangeEvent(this));
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    super.beforeShow();

    m_Indices = null;
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(File value) {
    m_FilePanel.setCurrentDirectory(value);
  }

  /**
   * Returns the current directory in use by the file chooser.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_FilePanel.getCurrentDirectory();
  }

  /**
   * Sets the current file.
   *
   * @param value	the file to set
   */
  public void setCurrent(File value) {
    m_FilePanel.setCurrent(value);
    m_TableData.clearSelection();
    m_FilePanel.fireCurrentValueChanged();
  }

  /**
   * Returns the current file.
   *
   * @return		the current file
   */
  public File getCurrent() {
    return m_FilePanel.getCurrent();
  }

  /**
   * Sets the listener for the event that the user accepts the input.
   *
   * @param l		the listener to use
   */
  public void setAcceptListener(ChangeListener l) {
    m_AcceptListener = l;
  }

  /**
   * Returns the listener for the event that the user accepts the input.
   *
   * @return		the listener in use, null if none set
   */
  public ChangeListener getAcceptListener() {
    return m_AcceptListener;
  }

  /**
   * Sets the listener for the event that the user discarded the input.
   *
   * @param l		the listener to use
   */
  public void setCancelListener(ChangeListener l) {
    m_CancelListener = l;
  }

  /**
   * Returns the listener for the event that the user discarded the input.
   *
   * @return		the listener in use, null if none set
   */
  public ChangeListener getCancelListener() {
    return m_CancelListener;
  }
}
