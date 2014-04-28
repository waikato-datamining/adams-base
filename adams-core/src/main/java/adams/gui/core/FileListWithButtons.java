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
 * FileListWithButtons.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import adams.core.io.PlaceholderFile;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.event.RemoveItemsEvent;
import adams.gui.event.RemoveItemsListener;

/**
 * A specialized list that allows the addition/removal of files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileListWithButtons
  extends BaseListWithButtons {

  /** for serialization. */
  private static final long serialVersionUID = 4722609868753213745L;

  /** the button for adding a file. */
  protected JButton m_ButtonAdd;

  /** the button for removing file(s). */
  protected JButton m_ButtonRemove;

  /** the button for removing all files. */
  protected JButton m_ButtonRemoveAll;

  /** the file chooser for selecting files. */
  protected BaseFileChooser m_FileChooser;

  /** the button for moving an entry up. */
  protected JButton m_ButtonMoveUp;

  /** the button for moving an entry down. */
  protected JButton m_ButtonMoveDown;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new BaseFileChooser();
    m_FileChooser.setMultiSelectionEnabled(true);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setModel(new DefaultListModel());
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	update();
      }
    });

    m_ButtonAdd = new JButton("Add");
    m_ButtonAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addFile();
      }
    });
    addToButtonsPanel(m_ButtonAdd);

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeFile(getSelectedIndices());
      }
    });
    addToButtonsPanel(m_ButtonRemove);

    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeAllFiles();
      }
    });
    addToButtonsPanel(m_ButtonRemoveAll);

    m_ButtonMoveUp = new JButton("Up");
    m_ButtonMoveUp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	moveUp();
      }
    });
    addToButtonsPanel(new JLabel());
    addToButtonsPanel(m_ButtonMoveUp);

    m_ButtonMoveDown = new JButton("Down");
    m_ButtonMoveDown.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	moveDown();
      }
    });
    addToButtonsPanel(m_ButtonMoveDown);

    addRemoveItemsListener(new RemoveItemsListener() {
      public void removeItems(RemoveItemsEvent e) {
	if (e.hasIndices())
	  removeFile(e.getIndices());
      }
    });
  }

  /**
   * Adds the specified filter to the file dialog.
   *
   * @param filter	the filter to add
   */
  public void addChoosableFileFilter(FileFilter filter) {
    m_FileChooser.addChoosableFileFilter(filter);
  }

  /**
   * Removes the specified filter from the file dialog.
   *
   * @param filter	the filter to remove
   */
  public void removeChoosableFileFilter(FileFilter filter) {
    m_FileChooser.removeChoosableFileFilter(filter);
  }

  /**
   * Sets the title for the file dialog.
   *
   * @param value	the title
   */
  public void setDialogTitle(String value) {
    m_FileChooser.setDialogTitle(value);
  }

  /**
   * Retruns the current title of the file dialog.
   *
   * @return		the title
   */
  public String getDialogTitle() {
    return m_FileChooser.getDialogTitle();
  }

  /**
   * Sets the current directory for the file dialog.
   *
   * @param value	the new current directory
   */
  public void setCurrentDirectory(File value) {
    m_FileChooser.setCurrentDirectory(value);
  }

  /**
   * Returns the current directory of the file dialog.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_FileChooser.getCurrentDirectory();
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    update();
  }

  /**
   * Updates buttons etc.
   */
  public void update() {
    updateButtons();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonAdd.setEnabled(true);
    m_ButtonRemove.setEnabled(getSelectedIndices().length > 0);
    m_ButtonRemoveAll.setEnabled(getModel().getSize() > 0);
    m_ButtonMoveUp.setEnabled(canMoveUp());
    m_ButtonMoveDown.setEnabled(canMoveDown());
  }

  /**
   * Adds a file to the list.
   */
  protected void addFile() {
    DefaultListModel	model;
    int			retVal;
    PlaceholderFile[]	files;

    model  = (DefaultListModel) getModel();
    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    files = m_FileChooser.getSelectedPlaceholderFiles();
    for (File file: files)
      model.addElement(file.toString());

    update();
  }

  /**
   * Removes the selected files.
   *
   * @param indices	the indices to remove
   */
  protected void removeFile(int[] indices) {
    DefaultListModel	model;
    int			i;

    model = (DefaultListModel) getModel();
    for (i = indices.length - 1; i >= 0; i--)
      model.remove(indices[i]);

    update();
  }

  /**
   * Removes the all files.
   */
  protected void removeAllFiles() {
    ((DefaultListModel) getModel()).clear();
    update();
  }

  /**
   * Sets the files to display.
   *
   * @param value	the files to display
   */
  public void setFiles(File[] value) {
    DefaultListModel	model;

    model = (DefaultListModel) getModel();
    model.clear();
    for (File file: value)
      model.addElement(file.toString());

    update();
  }

  /**
   * Returns all the specified files.
   *
   * @param indices	the indices to retrieve, all if null
   * @return		the files
   */
  protected File[] getFiles(int[] indices) {
    File[]		result;
    DefaultListModel	model;
    int			i;

    model = (DefaultListModel) getModel();
    if (indices != null) {
      result = new File[indices.length];
      for (i = 0; i < indices.length; i++)
	result[i] = new PlaceholderFile((String) model.get(i));
    }
    else {
      result = new File[model.getSize()];
      for (i = 0; i < model.size(); i++)
	result[i] = new PlaceholderFile((String) model.get(i));
    }

    return result;
  }

  /**
   * Returns all currently displayed files.
   *
   * @return		all files
   */
  public File[] getFiles() {
    return getFiles(null);
  }

  /**
   * Retruns all currently selected files.
   *
   * @return		the selected files
   */
  public File[] getSelectedFiles() {
    return getFiles(getSelectedIndices());
  }
}
