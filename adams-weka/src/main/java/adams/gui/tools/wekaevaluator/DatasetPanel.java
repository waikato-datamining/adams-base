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
 * DatasetPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekaevaluator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import weka.gui.AdamsHelper;
import weka.gui.ConverterFileChooser;
import adams.core.io.PlaceholderFile;
import adams.gui.core.BaseListWithButtons;

/**
 * Panel for listing datasets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetPanel
  extends AbstractSetupOptionPanel {

  /** for serialization. */
  private static final long serialVersionUID = -832431512063524253L;

  /** the file chooser for selecting files. */
  protected ConverterFileChooser m_FileChooser;
  
  /** the button for adding files. */
  protected JButton m_ButtonAdd;
  
  /** the button for removing files. */
  protected JButton m_ButtonRemove;
  
  /** the button for removing all files. */
  protected JButton m_ButtonRemoveAll;
  
  /** the button for moving files up. */
  protected JButton m_ButtonUp;
  
  /** the button for moving files down. */
  protected JButton m_ButtonDown;
  
  /** for listing the files. */
  protected BaseListWithButtons m_List;

  /** the model. */
  protected DefaultListModel<File> m_Model;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_FileChooser = new ConverterFileChooser();
    AdamsHelper.updateFileChooserAccessory(m_FileChooser);
    m_FileChooser.setMultiSelectionEnabled(true);
    m_Model = new DefaultListModel<File>();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_List = new BaseListWithButtons(m_Model);
    add(m_List, BorderLayout.CENTER);
    
    m_ButtonAdd = new JButton("Add...");
    m_ButtonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int retVal = m_FileChooser.showOpenDialog(DatasetPanel.this);
	if (retVal != ConverterFileChooser.APPROVE_OPTION)
	  return;
	if (m_FileChooser.getSelectedFiles().length == 0)
	  return;
	for (File file: m_FileChooser.getSelectedFiles())
	  m_Model.addElement(file);
	modified();
      }
    });
    
    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int[] indices = m_List.getSelectedIndices();
	if (indices.length == 0)
	  return;
	Arrays.sort(indices);
	for (int i = indices.length; i >= 0; i--)
	  m_Model.remove(indices[i]);
	modified();
      }
    });

    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Model.clear();
	modified();
      }
    });

    m_ButtonUp = new JButton("Up");
    m_ButtonUp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_List.moveUp();
	modified();
      }
    });

    m_ButtonDown = new JButton("Down");
    m_ButtonDown.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_List.moveDown();
	modified();
      }
    });

    m_List.addToButtonsPanel(m_ButtonAdd);
    m_List.addToButtonsPanel(m_ButtonRemove);
    m_List.addToButtonsPanel(m_ButtonRemoveAll);
    m_List.addToButtonsPanel(new JLabel(""));
    m_List.addToButtonsPanel(m_ButtonUp);
    m_List.addToButtonsPanel(m_ButtonDown);
    
    m_List.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	m_ButtonRemove.setEnabled(m_List.getSelectedIndices().length > 0);
	m_ButtonRemoveAll.setEnabled(m_Model.getSize() > 0);
	m_ButtonUp.setEnabled(m_List.canMoveUp());
	m_ButtonDown.setEnabled(m_List.canMoveDown());
      }
    });
  }
  
  /**
   * Gets called when the owner changes.
   */
  @Override
  protected void ownerChanged() {
    super.ownerChanged();
    
    if (getOwner() != null) {
      m_FileChooser.setCurrentDirectory(
	  new PlaceholderFile(
	      ExperimenterPanel.getProperties().getPath(
		  "DatasetsInitialDir", "%h")).getAbsoluteFile());
    }
  }

  /**
   * Sets the files to use.
   * 
   * @param value	the files
   */
  public void setFiles(File[] value) {
    m_IgnoreChanges = true;
    
    m_Model.clear();
    for (File file: value)
      m_Model.addElement(file);
    
    m_IgnoreChanges = false;
  }
  
  /**
   * Returns the current files.
   * 
   * @return		the files
   */
  public File[] getFiles() {
    File[]	result;
    int		i;
    
    result = new File[m_Model.getSize()];
    for (i = 0; i < m_Model.getSize(); i++)
      result[i] = m_Model.getElementAt(i);
    
    return result;
  }
  
  /**
   * Updates the buttons.
   */
  @Override
  protected void update() {
    super.update();
    m_ButtonRemove.setEnabled(m_List.getSelectedIndices().length > 0);
    m_ButtonRemoveAll.setEnabled(m_Model.getSize() > 0);
    m_ButtonUp.setEnabled(m_List.canMoveUp());
    m_ButtonDown.setEnabled(m_List.canMoveDown());
  }
}
