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
 * SelectMultipleDirectoriesPage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.gui.chooser.BaseDirectoryChooser;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseListWithButtons;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Wizard page that allows the user to select multiple directories.
 * Stores the selected files as blank-separated list.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class SelectMultipleDirectoriesPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** key in the properties that contains the file name. */
  public static final String KEY_DIRECTORIES = "directories";

  /** the list for the dir names. */
  protected BaseListWithButtons m_ListDirs;

  /** the chooser for selecting the directories. */
  protected BaseDirectoryChooser m_DirChooser;

  /** the button for bringing up the dirchooser. */
  protected JButton m_ButtonAdd;

  /** the button for removing the selected dirs. */
  protected JButton m_ButtonRemove;

  /** the button for removing all dirs. */
  protected JButton m_ButtonRemoveAll;

  /** the button for moving the selected dirs up. */
  protected JButton m_ButtonMoveUp;

  /** the button for moving the selected dirs down. */
  protected JButton m_ButtonMoveDown;

  /**
   * Default constructor.
   */
  public SelectMultipleDirectoriesPage() {
    super();
  }

  /**
   * Initializes the page with the given page name.
   *
   * @param pageName	the page name to use
   */
  public SelectMultipleDirectoriesPage(String pageName) {
    this();
    setPageName(pageName);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DirChooser = new BaseDirectoryChooser();
    m_DirChooser.setMultiSelectionEnabled(true);
  }

  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ListDirs = new BaseListWithButtons(new DefaultListModel());
    m_ListDirs.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        updateListButtons();
      }
    });
    add(m_ListDirs, BorderLayout.CENTER);

    m_ButtonAdd = new JButton("Add...");
    m_ButtonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int retVal = m_DirChooser.showOpenDialog(SelectMultipleDirectoriesPage.this);
        if (retVal != BaseFileChooser.APPROVE_OPTION)
          return;
        File[] selected = m_DirChooser.getSelectedFiles();
        DefaultListModel model = (DefaultListModel) m_ListDirs.getModel();
        for (File file: selected)
          model.addElement(file.getAbsolutePath());
        updateListButtons();
      }
    });
    m_ListDirs.addToButtonsPanel(m_ButtonAdd);

    m_ListDirs.addToButtonsPanel(new JLabel(""));

    m_ButtonMoveUp = new JButton("Up");
    m_ButtonMoveUp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        m_ListDirs.moveUp();
      }
    });
    m_ListDirs.addToButtonsPanel(m_ButtonMoveUp);

    m_ButtonMoveDown = new JButton("Down");
    m_ButtonMoveDown.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        m_ListDirs.moveDown();
      }
    });
    m_ListDirs.addToButtonsPanel(m_ButtonMoveDown);

    m_ListDirs.addToButtonsPanel(new JLabel(""));

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int[] indices = m_ListDirs.getSelectedIndices();
        DefaultListModel model = (DefaultListModel) m_ListDirs.getModel();
        for (int i = indices.length - 1; i >= 0; i--)
          model.remove(indices[i]);
        updateListButtons();
      }
    });
    m_ListDirs.addToButtonsPanel(m_ButtonRemove);

    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DefaultListModel model = (DefaultListModel) m_ListDirs.getModel();
        model.removeAllElements();
        updateListButtons();
      }
    });
    m_ListDirs.addToButtonsPanel(m_ButtonRemoveAll);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateListButtons();
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateListButtons() {
    m_ButtonAdd.setEnabled(true);
    m_ButtonMoveUp.setEnabled(m_ListDirs.canMoveUp());
    m_ButtonMoveDown.setEnabled(m_ListDirs.canMoveDown());
    m_ButtonRemove.setEnabled(m_ListDirs.getSelectedIndices().length > 0);
    m_ButtonRemoveAll.setEnabled(m_ListDirs.getModel().getSize() > 0);
    updateButtons();
  }

  /**
   * Sets the current directory to use for the directory chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(File value) {
    m_DirChooser.setCurrentDirectory(new PlaceholderFile(value));
  }

  /**
   * Returns the current directory in use by the directory chooser.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_DirChooser.getCurrentDirectory();
  }

  /**
   * Sets the current directories.
   *
   * @param value	the directories
   */
  public void setCurrent(File[] value) {
    DefaultListModel    model;

    model = new DefaultListModel();
    for (File file: value)
      model.addElement(file.getAbsolutePath());
    m_ListDirs.setModel(model);
  }

  /**
   * Returns the current directories.
   *
   * @return		the current directories
   */
  public File[] getCurrent() {
    List<File>      result;
    int             i;

    result = new ArrayList<>();
    for (i = 0; i < m_ListDirs.getModel().getSize(); i++)
      result.add(new File("" + m_ListDirs.getModel().getElementAt(i)));

    return result.toArray(new File[result.size()]);
  }

  /**
   * Sets the content of the page (ie parameters) as properties.
   *
   * @param value	the parameters as properties
   */
  public void setProperties(Properties value) {
    String[]	elements;
    File[]	dirs;
    int		i;

    dirs = new File[0];
    try {
      if (value.hasKey(KEY_DIRECTORIES)) {
	elements = OptionUtils.splitOptions(value.getProperty(KEY_DIRECTORIES));
	dirs     = new File[elements.length];
	for (i = 0; i < elements.length; i++)
	  dirs[i] = new PlaceholderFile(elements[i]).getAbsoluteFile();
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse directories: " + value.getProperty(KEY_DIRECTORIES), e);
    }
    setCurrent(dirs);
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    Properties	  result;
    List<String>  files;
    int           i;

    result = new Properties();

    files = new ArrayList<>();
    for (i = 0; i < m_ListDirs.getModel().getSize(); i++)
      files.add("" + m_ListDirs.getModel().getElementAt(i));
    result.setProperty(KEY_DIRECTORIES, OptionUtils.joinOptions(files.toArray(new String[files.size()])));
    
    return result;
  }
}
