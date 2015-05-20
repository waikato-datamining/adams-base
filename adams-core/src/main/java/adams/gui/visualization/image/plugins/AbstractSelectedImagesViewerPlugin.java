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
 * AbstractSelectedImagesViewerPlugin.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImageViewerPanel;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for plugins that work on selected images.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7171 $
 */
public abstract class AbstractSelectedImagesViewerPlugin
  extends AbstractImageViewerPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 869121794905442017L;

  /** for panels to work on. */
  protected ImagePanel[] m_SelectedPanels;

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   * <br><br>
   * Panel must be non-null and must contain an image.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(ImagePanel panel) {
    return (panel != null) && (panel.getCurrentImage() != null);
  }

  /**
   * Returns all the available panels in the image viewer.
   * 
   * @return		the available panels
   */
  protected ImagePanel[] getAllPanels() {
    List<ImagePanel>	result;
    ImageViewerPanel	owner;
    
    result = new ArrayList<ImagePanel>();
    
    owner = (ImageViewerPanel) GUIHelper.getParent(m_CurrentPanel, ImageViewerPanel.class);
    if (owner != null)
      result.addAll(Arrays.asList(owner.getAllPanels()));
    
    return result.toArray(new ImagePanel[result.size()]);
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
    final ImagePanel[]	panels;
    int			index;
    BaseScrollPane	scrollPane;
    
    result  = new JPanel(new BorderLayout());
    panels = getAllPanels();
    model  = new DefaultListModel();
    index  = -1;
    for (ImagePanel p: panels) {
      if (p == m_CurrentPanel)
	index = model.getSize();
      model.addElement((model.getSize() + 1) + ": " + (p.getCurrentFile() == null ? "---" : p.getCurrentFile().getName()));
    }
    list = new JList(model);
    list.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	int[] indices = list.getSelectedIndices();
	m_SelectedPanels = new ImagePanel[indices.length];
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
    result.setTitle(getCaption());
    result.setApproveVisible(hasApprovalButton());
    result.setCancelVisible(hasCancelButton());
    result.setDiscardVisible(false);
    result.setSize(getDialogSize());
    
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(new JLabel("Please select:"));
    result.getContentPane().add(panel, BorderLayout.NORTH);
    panel = new JPanel(new BorderLayout());
    result.getContentPane().add(panel, BorderLayout.CENTER);
    
    panelList   = createListPanel(result);
    panelList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
   * Initializes the processing.
   * <br><br>
   * Default implementation returns null.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String processInit() {
    return null;
  }
  
  /**
   * Processes the specified panel.
   * 
   * @param panel	the panel to process
   * @return		null if successful, error message otherwise
   */
  protected abstract String process(ImagePanel panel);
  
  /**
   * Finishes up the processing.
   * <br><br>
   * Default implementation returns null.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String processFinish() {
    return null;
  }

  /**
   * Processes all the selected panels.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String process() {
    String	result;
    
    result = processInit();
    
    if (result == null) {
      for (ImagePanel panel: m_SelectedPanels) {
	result = process(panel);
	if (result != null)
	  break;
      }
    }
    
    if (result == null)
      result = processFinish();
    
    return result;
  }
  
  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    ApprovalDialog	dialog;
    
    result = null;
    
    dialog = createDialog();
    if (dialog == null) {
      result = "Failed to create dialog!";
    }
    else {
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
