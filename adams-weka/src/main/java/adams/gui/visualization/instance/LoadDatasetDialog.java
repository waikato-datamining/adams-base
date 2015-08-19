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
 * LoadDatasetDialog.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.core.Index;
import adams.core.Range;
import adams.gui.chooser.DatasetFileChooserPanel;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * A dialog for loading datasets from disk.
 *
 * @author  fracete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LoadDatasetDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 3881690262061461134L;

  /** the "no class" constant. */
  public final static String NO_CLASS = "-no class-";

  /** the "no sorting" constant. */
  public final static String NO_SORTING = "-no sorting-";

  /** the "no id" constant. */
  public final static String NO_ID = "-no ID-";

  /** the dialog itself. */
  protected LoadDatasetDialog m_Self;

  /** for selecting the dataset file. */
  protected DatasetFileChooserPanel m_FilePanel;

  /** the button for reloading an existing file. */
  protected JButton m_ButtonReload;

  /** the table for displaying the instances. */
  protected InstanceTable m_TableData;

  /** the Load button. */
  protected JButton m_ButtonLoad;

  /** the Close button. */
  protected JButton m_ButtonClose;

  /** the search panel. */
  protected SearchPanel m_SearchPanel;

  /** the class index. */
  protected JComboBox m_ComboBoxClass;

  /** the class index model. */
  protected DefaultComboBoxModel m_ComboBoxClassModel;

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

  /** whether to include numeric attributes. */
  protected JCheckBox m_CheckBoxIncludeNumericAttributes;

  /** whether to include date attributes. */
  protected JCheckBox m_CheckBoxIncludeDateAttributes;

  /** whether to include numeric nominal. */
  protected JCheckBox m_CheckBoxIncludeNominalAttributes;

  /** whether to include string attributes. */
  protected JCheckBox m_CheckBoxIncludeStringAttributes;

  /** whether to include relational attributes. */
  protected JCheckBox m_CheckBoxIncludeRelationalAttributes;

  /** the full dataset. */
  protected Instances m_Instances;

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

  /** the default for numeric attributes. */
  protected boolean m_DefaultIncludeNumericAttributes;

  /** the default for date attributes. */
  protected boolean m_DefaultIncludeDateAttributes;

  /** the default for nominal attributes. */
  protected boolean m_DefaultIncludeNominalAttributes;

  /** the default for string attributes. */
  protected boolean m_DefaultIncludeStringAttributes;

  /** the default for relational attributes. */
  protected boolean m_DefaultIncludeRelationalAttributes;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public LoadDatasetDialog(Dialog owner) {
    this(owner, "Load dataset");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public LoadDatasetDialog(Dialog owner, String title) {
    super(owner, title, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public LoadDatasetDialog(Frame owner) {
    this(owner, "Load dataset");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public LoadDatasetDialog(Frame owner, String title) {
    super(owner, title, true);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Self                               = this;
    m_Instances                          = null;
    m_ComboBoxClassModel                 = new DefaultComboBoxModel();
    m_ComboBoxClassModel.addElement(NO_CLASS);
    m_ComboBoxSortingModel               = new DefaultComboBoxModel();
    m_ComboBoxSortingModel.addElement(NO_SORTING);
    m_ComboBoxIDModel                    = new DefaultComboBoxModel();
    m_ComboBoxIDModel.addElement(NO_ID);
    m_ListAdditionalAttributesModel      = new DefaultListModel();
    m_DefaultClassIndex                  = new Index();
    m_DefaultIDIndex                     = new Index();
    m_DefaultSortIndex                   = new Index();
    m_DefaultIncludeNumericAttributes    = false;
    m_DefaultIncludeDateAttributes       = false;
    m_DefaultIncludeNominalAttributes    = false;
    m_DefaultIncludeStringAttributes     = false;
    m_DefaultIncludeRelationalAttributes = false;
    m_DefaultAttributeRange              = new Range(Range.ALL);
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
    m_FilePanel = new DatasetFileChooserPanel();
    m_FilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    m_FilePanel.setPrefix("File");
    m_FilePanel.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        m_ButtonLoad.setEnabled(getFilename().length() > 0);
        if (m_ButtonLoad.isEnabled())
          loadFile(false);
        m_ButtonReload.setEnabled(m_ButtonLoad.isEnabled());
      }
    });
    panel.add(m_FilePanel, BorderLayout.CENTER);

    m_ButtonReload = new JButton(GUIHelper.getIcon("refresh.gif"));
    m_ButtonReload.setEnabled(false);
    m_ButtonReload.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	loadFile(true);
      }
    });
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
    m_TableData = new InstanceTable(null);
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
    panel.add(new BaseScrollPane(m_TableData), BorderLayout.CENTER);
    panelInstances.add(panel, BorderLayout.CENTER);

    // search
    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_SearchPanel.setTextColumns(15);
    m_SearchPanel.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	search();
      }
    });
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

    // class
    m_ComboBoxClass = new JComboBox(m_ComboBoxClassModel);
    m_ComboBoxClass.setSelectedIndex(0);
    m_ComboBoxClass.setToolTipText("The selected attribute gets omitted from display and added to the report instead");
    label = new JLabel("Class");
    label.setDisplayedMnemonic('s');
    label.setLabelFor(m_ComboBoxClass);
    panel.add(label);
    panel.add(m_ComboBoxClass);

    // attribute range
    m_TextAttributeRange = new JTextField(10);
    m_TextAttributeRange.setText(Range.ALL);
    m_TextAttributeRange.setToolTipText("For limiting the attributes being displayed");
    label = new JLabel("Attribute range");
    label.setDisplayedMnemonic('r');
    label.setLabelFor(m_TextAttributeRange);
    panel.add(label);
    panel.add(m_TextAttributeRange);

    // attribute types
    label = new JLabel("Attribute types");
    m_CheckBoxIncludeNumericAttributes = new JCheckBox("Numeric");
    m_CheckBoxIncludeDateAttributes = new JCheckBox("Date");
    m_CheckBoxIncludeNominalAttributes = new JCheckBox("Nominal");
    m_CheckBoxIncludeStringAttributes = new JCheckBox("String");
    m_CheckBoxIncludeRelationalAttributes = new JCheckBox("Relational");
    panel.add(label);
    panel.add(m_CheckBoxIncludeNumericAttributes);
    panel.add(m_CheckBoxIncludeDateAttributes);
    panel.add(m_CheckBoxIncludeNominalAttributes);
    panel.add(m_CheckBoxIncludeNominalAttributes);
    panel.add(m_CheckBoxIncludeStringAttributes);
    panel.add(m_CheckBoxIncludeRelationalAttributes);

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
    m_ButtonLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        acceptSelection();
      }
    });
    panelAll.add(m_ButtonLoad);

    m_ButtonClose = new JButton("Cancel");
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        close();
      }
    });
    panelAll.add(m_ButtonClose);

    pack();

    // adjust sizes
    m_ComboBoxClass.setPreferredSize(new Dimension(150, m_ComboBoxClass.getHeight()));

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
   * Sets the default class index to use.
   *
   * @param value	the class index, 1-based integer or 'first'/'last',
   * 			use empty string for none
   * @see		Index
   */
  public void setDefaultClassIndex(String value) {
    m_DefaultClassIndex.setIndex(value);
  }

  /**
   * Returns the default class index in use.
   *
   * @return		the class index, 1-based integer or 'first'/'last',
   * 			empty string for none
   * @see		Index
   */
  public String getDefaultClassIndex() {
    return m_DefaultClassIndex.getIndex();
  }

  /**
   * Returns the currently selected class index.
   *
   * @return		the class index, -1 if none selected
   */
  public int getCurrentClassIndex() {
    if (m_ComboBoxClass.getSelectedIndex() > -1)
      return m_ComboBoxClass.getSelectedIndex() - 1;
    else
      return -1;
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
   * Returns the full dataset, can be null if none loaded.
   *
   * @return		the full dataset
   */
  public Instances getDataset() {
    int		index;
    Instances	result;

    result = new Instances(m_Instances);
    if (m_ComboBoxSorting.getSelectedIndex() > 0)
      result.sort(m_ComboBoxSorting.getSelectedIndex() - 1);

    index = m_ComboBoxClass.getSelectedIndex();
    if (index > -1)
      index--;
    result.setClassIndex(index);

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
   * Sets the default for the specified type of attribute.
   *
   * @param attType	the attribute type
   * @param value	true if to restrict to attribute type by default
   * @see		Attribute
   */
  public void setDefaultIncludeAttributes(int attType, boolean value) {
    switch (attType) {
      case Attribute.NUMERIC:
	m_DefaultIncludeNumericAttributes = value;
	break;
      case Attribute.DATE:
	m_DefaultIncludeDateAttributes = value;
	break;
      case Attribute.NOMINAL:
	m_DefaultIncludeNominalAttributes = value;
	break;
      case Attribute.STRING:
	m_DefaultIncludeStringAttributes = value;
	break;
      case Attribute.RELATIONAL:
	m_DefaultIncludeDateAttributes = value;
	break;
      default:
	System.err.println(getClass().getName() + ": unhandled attribute type " + attType);
    }
  }

  /**
   * Returns the default for the specified attribute type.
   *
   * @param attType	the attribute type
   * @return		true if to include to attribute type by default
   * @see		Attribute
   */
  public boolean getDefaultInclueAttributes(int attType) {
    switch (attType) {
      case Attribute.NUMERIC:
	return m_DefaultIncludeNumericAttributes;
      case Attribute.DATE:
	return m_DefaultIncludeDateAttributes;
      case Attribute.NOMINAL:
	return m_DefaultIncludeNominalAttributes;
      case Attribute.STRING:
	return m_DefaultIncludeStringAttributes;
      case Attribute.RELATIONAL:
	return m_DefaultIncludeDateAttributes;
      default:
	System.err.println(getClass().getName() + ": unhandled attribute type " + attType);
	return false;
    }
  }

  /**
   * Sets whether only numeric attributes should be used.
   *
   * @param attType	the attribute type
   * @param value	if true then only numeric attributes will be used
   * @see		Attribute
   */
  public void setIncludeAttributes(int attType, boolean value) {
    switch (attType) {
      case Attribute.NUMERIC:
	m_CheckBoxIncludeNumericAttributes.setSelected(value);
	break;
      case Attribute.DATE:
	m_CheckBoxIncludeDateAttributes.setSelected(value);
	break;
      case Attribute.NOMINAL:
	m_CheckBoxIncludeNominalAttributes.setSelected(value);
	break;
      case Attribute.STRING:
	m_CheckBoxIncludeStringAttributes.setSelected(value);
	break;
      case Attribute.RELATIONAL:
	m_CheckBoxIncludeRelationalAttributes.setSelected(value);
	break;
      default:
	System.err.println(getClass().getName() + ": unhandled attribute type " + attType);
    }
  }

  /**
   * Returns whether only numeric attributes should be used.
   *
   * @param attType	the attribute type
   * @return		true if only numeric attributes to be used
   * @see		Attribute
   */
  public boolean getIncludeAttributes(int attType) {
    switch (attType) {
      case Attribute.NUMERIC:
	return m_CheckBoxIncludeNumericAttributes.isSelected();
      case Attribute.DATE:
	return m_CheckBoxIncludeDateAttributes.isSelected();
      case Attribute.NOMINAL:
	return m_CheckBoxIncludeNominalAttributes.isSelected();
      case Attribute.STRING:
	return m_CheckBoxIncludeStringAttributes.isSelected();
      case Attribute.RELATIONAL:
	return m_CheckBoxIncludeRelationalAttributes.isSelected();
      default:
	System.err.println(getClass().getName() + ": unhandled attribute type " + attType);
	return false;
    }
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
    String		oldClass;
    int			oldClassIndex;
    String		oldID;
    int			oldIDIndex;
    String		oldSorting;
    int			oldSortingIndex;
    int[]		oldAdditional;

    if (!m_FilePanel.getCurrent().isFile())
      return;

    // class
    oldClass = null;
    if (reload && (m_Instances != null)) {
      oldClassIndex = m_ComboBoxClass.getSelectedIndex() - 1;
      if (oldClassIndex > -1)
	oldClass = m_Instances.attribute(oldClassIndex).name();
    }

    // ID
    oldID = null;
    if (reload && (m_Instances != null)) {
      oldIDIndex = m_ComboBoxID.getSelectedIndex() - 1;
      if (oldIDIndex > -1)
	oldID = m_Instances.attribute(oldIDIndex).name();
    }

    // sorting
    oldSorting = null;
    if (reload && (m_Instances != null)) {
      oldSortingIndex = m_ComboBoxSorting.getSelectedIndex() - 1;
      if (oldSortingIndex > -1)
	oldSorting = m_Instances.attribute(oldSortingIndex).name();
    }

    // additional attributes
    oldAdditional = new int[0];
    if (reload && (m_Instances != null)) {
      oldAdditional = m_ListAdditionalAttributes.getSelectedIndices();
    }

    try {
      if (m_FilePanel.getLoader() == null)
	throw new IllegalStateException("Cannot determine loader for file!");
      m_Instances = m_FilePanel.getLoader().getDataSet();
      m_TextAttributeRange.setToolTipText("#attributes: " + m_Instances.numAttributes());
    }
    catch (Exception e) {
      m_TextAttributeRange.setToolTipText(null);
      m_Instances = null;
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  this, "Error loading file '" + getFilename() + "' - exception:\n" + e);
    }

    if (!reload)
      m_TextAttributeRange.setText(m_DefaultAttributeRange.getRange());
    m_ComboBoxClassModel.removeAllElements();
    m_ComboBoxClassModel.addElement(NO_CLASS);
    m_ComboBoxClass.setSelectedIndex(0);
    m_ComboBoxIDModel.removeAllElements();
    m_ComboBoxIDModel.addElement(NO_ID);
    m_ComboBoxID.setSelectedIndex(0);
    m_ComboBoxSortingModel.removeAllElements();
    m_ComboBoxSortingModel.addElement(NO_SORTING);
    m_ComboBoxSorting.setSelectedIndex(0);
    m_ListAdditionalAttributesModel.clear();
    if (m_Instances != null) {
      oldClassIndex   = -1;
      oldIDIndex      = -1;
      oldSortingIndex = -1;
      m_TableData.setModel(new InstanceTableModel(m_Instances));
      for (i = 0; i < m_Instances.numAttributes(); i++) {
	m_ComboBoxClassModel.addElement((i+1) + ": " + m_Instances.attribute(i).name());
	m_ComboBoxIDModel.addElement((i+1) + ": " + m_Instances.attribute(i).name());
	m_ComboBoxSortingModel.addElement((i+1) + ": " + m_Instances.attribute(i).name());
	m_ListAdditionalAttributesModel.addElement((i+1) + ": " + m_Instances.attribute(i).name());
	if (oldClass != null) {
	  if (m_Instances.attribute(i).name().equals(oldClass)) {
	    oldClassIndex = i;
	  }
	}
	if (oldID != null) {
	  if (m_Instances.attribute(i).name().equals(oldID)) {
	    oldIDIndex = i;
	  }
	}
	if (oldSorting != null) {
	  if (m_Instances.attribute(i).name().equals(oldSorting)) {
	    oldSortingIndex = i;
	  }
	}
      }
      // class index
      if (oldClassIndex != -1) {
	m_ComboBoxClass.setSelectedIndex(oldClassIndex + 1);  // +1 because of NO_CLASS at index 0
      }
      else if (!reload && m_DefaultClassIndex.hasIndex()) {
	m_DefaultClassIndex.setMax(m_Instances.numAttributes());
	if (m_DefaultClassIndex.getIntIndex() != -1)
	  m_ComboBoxClass.setSelectedIndex(m_DefaultClassIndex.getIntIndex() + 1);  // +1 because of NO_CLASS at index 0
      }
      // ID index
      if (oldIDIndex != -1) {
	m_ComboBoxID.setSelectedIndex(oldIDIndex + 1);  // +1 because of NO_ID at index 0
      }
      else if (!reload && m_DefaultIDIndex.hasIndex()) {
	m_DefaultIDIndex.setMax(m_Instances.numAttributes());
	if (m_DefaultIDIndex.getIntIndex() != -1)
	  m_ComboBoxID.setSelectedIndex(m_DefaultIDIndex.getIntIndex() + 1);  // +1 because of NO_ID at index 0
      }
      // only numeric attributes
      if (!reload)
	m_CheckBoxIncludeNumericAttributes.setSelected(m_DefaultIncludeNumericAttributes);
      // sort index
      if (oldSortingIndex != -1) {
	m_ComboBoxSorting.setSelectedIndex(oldSortingIndex + 1);  // +1 because of NO_SORTING at index 0
      }
      else if (!reload && m_DefaultSortIndex.hasIndex()) {
	m_DefaultSortIndex.setMax(m_Instances.numAttributes());
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
      indices = new int[m_Instances.numInstances()];
      for (i = 0; i < indices.length; i++)
	indices[i] = i;
    }
    else {
      indices = m_TableData.getSelectedRows();
    }

    m_Indices = new int[indices.length];
    for (i = 0; i < indices.length; i++)
      m_Indices[i] = ((Integer) m_TableData.getValueAt(indices[i], 0)) - 1;

    setVisible(false);
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
}
