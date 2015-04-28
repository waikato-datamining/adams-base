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
 * WekaSelectMultipleDatasetsPage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseListWithButtons;
import weka.gui.ConverterFileChooser;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Wizard page that allows the user to select multiple datasets. File filters can
 * be defined as well. Stores the selected files as blank-separated list.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class WekaSelectMultipleDatasetsPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** key in the properties that contains the file name. */
  public static final String KEY_FILES = "files";

  /** the list for the file names. */
  protected BaseListWithButtons m_ListFiles;

  /** the filechooser for selecting the files. */
  protected ConverterFileChooser m_FileChooser;

  /** the button for bringing up the filechooser. */
  protected JButton m_ButtonAdd;

  /** the button for removing the selected files. */
  protected JButton m_ButtonRemove;

  /** the button for removing all files. */
  protected JButton m_ButtonRemoveAll;

  /**
   * Default constructor.
   */
  public WekaSelectMultipleDatasetsPage() {
    super();
  }

  /**
   * Initializes the page with the given page name.
   *
   * @param pageName	the page name to use
   */
  public WekaSelectMultipleDatasetsPage(String pageName) {
    this();
    setPageName(pageName);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new ConverterFileChooser();
    m_FileChooser.setMultiSelectionEnabled(true);
  }

  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ListFiles = new BaseListWithButtons(new DefaultListModel());
    m_ListFiles.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        updateListButtons();
      }
    });
    add(m_ListFiles, BorderLayout.CENTER);

    m_ButtonAdd = new JButton("Add...");
    m_ButtonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int retVal = m_FileChooser.showOpenDialog(WekaSelectMultipleDatasetsPage.this);
        if (retVal != BaseFileChooser.APPROVE_OPTION)
          return;
        File[] selected = m_FileChooser.getSelectedFiles();
        DefaultListModel model = (DefaultListModel) m_ListFiles.getModel();
        for (File file: selected)
          model.addElement(file.getAbsolutePath());
        updateListButtons();
      }
    });
    m_ListFiles.addToButtonsPanel(m_ButtonAdd);

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int[] indices = m_ListFiles.getSelectedIndices();
        DefaultListModel model = (DefaultListModel) m_ListFiles.getModel();
        for (int i = indices.length - 1; i >= 0; i--)
          model.remove(indices[i]);
        updateListButtons();
      }
    });
    m_ListFiles.addToButtonsPanel(m_ButtonRemove);

    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DefaultListModel model = (DefaultListModel) m_ListFiles.getModel();
        model.removeAllElements();
        updateListButtons();
      }
    });
    m_ListFiles.addToButtonsPanel(m_ButtonRemoveAll);
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
    m_ButtonRemove.setEnabled(m_ListFiles.getSelectedIndices().length > 0);
    m_ButtonRemoveAll.setEnabled(m_ListFiles.getModel().getSize() > 0);
    updateButtons();
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(File value) {
    m_FileChooser.setCurrentDirectory(new PlaceholderFile(value));
  }

  /**
   * Returns the current directory in use by the file chooser.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_FileChooser.getCurrentDirectory();
  }

  /**
   * Sets the current files.
   *
   * @param value	the files
   */
  public void setCurrent(File[] value) {
    DefaultListModel    model;

    model = new DefaultListModel();
    for (File file: value)
      model.addElement(file.getAbsolutePath());
    m_ListFiles.setModel(model);
  }

  /**
   * Returns the current files.
   *
   * @return		the current files
   */
  public File[] getCurrent() {
    List<File>      result;
    int             i;

    result = new ArrayList<>();
    for (i = 0; i < m_ListFiles.getModel().getSize(); i++)
      result.add(new File("" + m_ListFiles.getModel().getElementAt(i)));

    return result.toArray(new File[result.size()]);
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
    for (i = 0; i < m_ListFiles.getModel().getSize(); i++)
      files.add("" + m_ListFiles.getModel().getElementAt(i));
    result.setProperty(KEY_FILES, OptionUtils.joinOptions(files.toArray(new String[files.size()])));
    
    return result;
  }
}
