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
 * SelectOptionPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.chooser;

import adams.core.option.OptionUtils;
import adams.gui.core.BaseList;
import adams.gui.core.ConsolePanel;
import adams.gui.dialog.ApprovalDialog;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Allows the user to select either a single or multiple options from a list
 * of available options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectOptionPanel
  extends AbstractChooserPanel<String[]> {

  private static final long serialVersionUID = -4122866764098538057L;

  /** the title for the dialog. */
  protected String m_DialogTitle;

  /** whether to use multi-select. */
  protected boolean m_MultiSelect;

  /** the available options. */
  protected String[] m_Options;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Options     = new String[0];
    m_MultiSelect = false;
    m_DialogTitle = "Select option";
  }

  /**
   * Sets the options to choose from.
   *
   * @param value	the options
   */
  public void setOptions(String[] value) {
    String[]		current;
    List<String>	present;

    current   = getCurrent();
    m_Options = value;
    present   = new ArrayList<>();
    for (String c: current) {
      if (Arrays.binarySearch(m_Options, c) > -1)
	present.add(c);
    }
    setCurrent(present.toArray(new String[present.size()]));
  }

  /**
   * Returns the options to choose from.
   *
   * @return		the options
   */
  public String[] getOptions() {
    return m_Options;
  }

  /**
   * Selects the option specified by the index.
   *
   * @param value	the index to select
   */
  public void setCurrentIndex(int value) {
    if (value < 0)
      setCurrent(new String[0]);
    else if (value < m_Options.length)
      setCurrent(new String[]{m_Options[value]});
  }

  /**
   * Returns the index of the currently selected option.
   *
   * @return		the index, -1 if none selected
   */
  public int getCurrentIndex() {
    int[]	indices;

    indices = getCurrentIndices();
    if (indices.length == 0)
      return -1;
    else
      return indices[0];
  }

  /**
   * Selects the options specified by the indices.
   *
   * @param value	the indices to select
   */
  public void setCurrentIndices(int[] value) {
    List<String>	current;

    if (value.length == 0) {
      setCurrent(new String[0]);
    }
    else {
      current = new ArrayList<>();
      for (int index: value) {
	if ((index >= 0) && (index < m_Options.length))
	  current.add(m_Options[index]);
      }
      setCurrent(current.toArray(new String[current.size()]));
    }
  }

  /**
   * Returns the indices of the currently selected options.
   *
   * @return		the indices
   */
  public int[] getCurrentIndices() {
    TIntList	result;
    String[]	current;
    int		index;

    result = new TIntArrayList();
    current = getCurrent();

    for (String c: current) {
      index = Arrays.binarySearch(m_Options, c);
      result.add(index);
    }

    return result.toArray();
  }

  /**
   * Sets the title for the dialog.
   *
   * @param value	the title
   */
  public void setDialogTitle(String value) {
    m_DialogTitle = value;
  }

  /**
   * Returns the title to use for the dialog.
   *
   * @return		the title
   */
  public String getDialogTitle() {
    return m_DialogTitle;
  }

  /**
   * Sets whether to allow selection of multiple options.
   *
   * @param value	true if multi-select enabled
   */
  public void setMultiSelect(boolean value) {
    m_MultiSelect = value;
  }

  /**
   * Returns whether multiple options can be selected.
   *
   * @return		true if multi-select enabled
   */
  public boolean isMultiSelect() {
    return m_MultiSelect;
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(String[] value) {
    return OptionUtils.joinOptions(value);
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected String[] fromString(String value) {
    String[]	result;

    try {
      result = OptionUtils.splitOptions(value);
    }
    catch (Exception e) {
      result = new String[0];
      ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to parse options for selection: " + value, e);
    }

    return result;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected String[] doChoose() {
    List<String> 		result;
    ApprovalDialog		dialog;
    DefaultListModel<String>	model;
    BaseList 			list;
    int				i;
    TIntList			selected;
    Set<String> 		current;

    // populate list
    model    = new DefaultListModel<>();
    selected = new TIntArrayList();
    current  = new HashSet<>(Arrays.asList(getCurrent()));
    for (i = 0; i < m_Options.length; i++) {
      model.addElement(m_Options[i]);
      if (current.contains(m_Options[i])) {
        selected.add(i);
        if (!isMultiSelect())
          break;
      }
    }
    list = new BaseList(model);
    list.setSelectionMode(isMultiSelect() ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
    list.setSelectedIndices(selected.toArray());

    // create dialog
    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle(m_DialogTitle);
    dialog.getContentPane().add(new BaseScrollPane(list));
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return null;

    result = new ArrayList<>();
    for (int index: list.getSelectedIndices())
      result.add(model.get(index));

    return result.toArray(new String[result.size()]);
  }
}
