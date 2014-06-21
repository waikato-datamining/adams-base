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
 * AbstractSelectedSheetsViewPlugin.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.tools.SpreadSheetViewerPanel;

/**
 * Ancestor for plugins that operate on multiple panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectedSheetsViewPlugin
  extends AbstractViewPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 7647402907873370803L;

  /** for panels to work on. */
  protected SpreadSheetPanel[] m_SelectedPanels;

  /**
   * Returns all the available panels in the image viewer.
   * 
   * @return		the available panels
   */
  protected SpreadSheetPanel[] getAllPanels() {
    List<SpreadSheetPanel>	result;
    SpreadSheetViewerPanel	owner;
    
    result = new ArrayList<SpreadSheetPanel>();
    
    owner = (SpreadSheetViewerPanel) GUIHelper.getParent(m_CurrentPanel, SpreadSheetViewerPanel.class);
    if (owner != null)
      result.addAll(Arrays.asList(owner.getAllPanels()));
    
    return result.toArray(new SpreadSheetPanel[result.size()]);
  }
  
  /**
   * Creates the panel containing the list of all images. Also updates
   * the {@link #m_SelectedPanels} property.
   * 
   * @param dialog	the dialog that is being created
   * @return		the generated panel
   */
  protected JPanel createListPanel(final ApprovalDialog dialog) {
    JPanel		result;
    final JList		list;
    DefaultListModel	model;
    final SpreadSheetPanel[]	panels;
    int			index;
    BaseScrollPane	scrollPane;
    
    result  = new JPanel(new BorderLayout());
    panels = getAllPanels();
    model  = new DefaultListModel();
    index  = -1;
    for (SpreadSheetPanel p: panels) {
      if (p == m_CurrentPanel)
	index = model.getSize();
      model.addElement((model.getSize() + 1) + ": " + p.getTabTitle());
    }
    list = new JList(model);
    list.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	int[] indices = list.getSelectedIndices();
	m_SelectedPanels = new SpreadSheetPanel[indices.length];
	for (int i = 0; i < indices.length; i++)
	  m_SelectedPanels[i] = panels[indices[i]];
      }
    });
    list.setSelectedIndex(index);
    scrollPane = new BaseScrollPane(list);
    scrollPane.setPreferredSize(new Dimension(200, 1));
    result.add(scrollPane, BorderLayout.CENTER);
    
    return result;
  }
  
  /**
   * Creates the panel with the configuration (return null to suppress display).
   * 
   * @param dialog	the dialog that is being created
   * @return		the generated panel, null to suppress
   */
  protected abstract JPanel createConfigurationPanel(final ApprovalDialog dialog);
  
  /**
   * Returns the size of the dialog.
   * 
   * @return		the size
   */
  protected Dimension getDialogSize() {
    return new Dimension(600, 400);
  }
  
  /**
   * Returns whether the dialog has an approval button.
   * 
   * @return		true if approval button visible
   */
  protected boolean hasApprovalButton() {
    return true;
  }
  
  /**
   * Returns whether the dialog has a cancel button.
   * 
   * @return		true if cancel button visible
   */
  protected boolean hasCancelButton() {
    return true;
  }
  
  /**
   * Creates the dialog to display.
   * 
   * @return		the dialog
   */
  protected ApprovalDialog createDialog() {
    ApprovalDialog	result;
    JPanel		panel;
    JPanel		panelList;
    JPanel		panelConfig;
    
    if (m_CurrentPanel.getParentDialog() != null)
      result = new ApprovalDialog(m_CurrentPanel.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      result = new ApprovalDialog(m_CurrentPanel.getParentFrame(), true);
    result.setTitle(getMenuText());
    result.setApproveVisible(hasApprovalButton());
    result.setCancelVisible(hasCancelButton());
    result.setDiscardVisible(false);
    result.setSize(getDialogSize());
    
    panel = new JPanel(new BorderLayout());
    result.getContentPane().add(panel, BorderLayout.CENTER);
    
    panelList   = createListPanel(result);
    panelConfig = createConfigurationPanel(result);
    if (panelConfig == null) {
      panel.add(panelList, BorderLayout.CENTER);
    }
    else {
      panel.add(panelList,   BorderLayout.WEST);
      panel.add(panelConfig, BorderLayout.CENTER);
    }
    
    return result;
  }

  /**
   * Processes all the selected panels.
   * 
   * @return		spreadsheet if successful, otherwise null
   */
  protected abstract BasePanel process();
  
  /**
   * Performs the actual generation of the information.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  @Override
  protected BasePanel doGenerate(SpreadSheet sheet) {
    BasePanel		result;
    ApprovalDialog	dialog;
    
    result = null;
    
    dialog = createDialog();
    if (dialog != null) {
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
      if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
	result = process();
      else if (dialog.getOption() == ApprovalDialog.CANCEL_OPTION)
	m_CanceledByUser = true;
    }
    
    return result;
  }
}
