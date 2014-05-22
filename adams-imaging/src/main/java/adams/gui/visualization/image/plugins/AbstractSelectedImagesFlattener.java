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
 * AbstractImageFlattener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import adams.data.conversion.WekaInstanceToAdamsInstance;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.instance.InstanceContainer;
import adams.gui.visualization.instance.InstanceContainerManager;
import adams.gui.visualization.instance.InstanceExplorer;

/**
 * Ancestor for image flattener plugins that generate Weka {@link weka.core.Instance} 
 * objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectedImagesFlattener
  extends AbstractSelectedImagesViewerPlugin {

  /** for serialization. */
  private static final long serialVersionUID = -3111612432359476318L;
  
  /** for storing filtering errors. */
  protected String m_FilterError;

  /** the collected data. */
  protected List<weka.core.Instance> m_Collected;

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   * <p/>
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
   * Returns the title for the dialog.
   * 
   * @return		the title
   */
  protected String getDialogTitle() {
    String	result;
    
    result = getLastSetup().getClass().getSimpleName();
    if (m_CurrentPanel.getCurrentFile() != null)
      result += " [" + m_CurrentPanel.getCurrentFile().getName() + " -- " + m_CurrentPanel.getCurrentFile().getParent() + "]";
    
    return result;
  }
  
  /**
   * Returns the default size of the dialog.
   * 
   * @return		the dimension of the dialog
   */
  @Override
  protected Dimension getDialogSize() {
    return new Dimension(800, 600);
  }

  /**
   * Initializes the processing.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processInit() {
    String	result;
    
    result = super.processInit();
    
    if (result == null)
      m_Collected = new ArrayList<weka.core.Instance>();
    
    return result;
  }
  
  /**
   * Flattens the image.
   *
   * @param image	the image to filter
   * @return		the generated instance(s)
   */
  protected abstract weka.core.Instance[] flatten(BufferedImage image);
  
  /**
   * Processes the specified panel.
   * 
   * @param panel	the panel to process
   * @return		null if successful, error message otherwise
   */
  @Override
  protected String process(ImagePanel panel) {
    String			result;
    BufferedImage		input;
    weka.core.Instance[]	output;

    result = null;
    input  = panel.getCurrentImage();
    try {
      output = flatten(input);
      m_Collected.addAll(Arrays.asList(output));

      // did user abort filtering?
      if (m_CanceledByUser)
	return result;

      if (output == null) {
	result = "Failed to flatten image: ";
	if (m_FilterError == null)
	  result += "unknown reason";
	else
	  result += m_FilterError;
      }
    }
    catch (Exception e) {
      m_FilterError = e.toString();
      result = "Failed to flatten image: ";
      getLogger().log(Level.SEVERE, result, e);
      result += m_FilterError;
    }

    return result;
  }

  /**
   * Finishes up the processing.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processFinish() {
    String				result;
    ApprovalDialog			dlg;
    InstanceExplorer			explorer;
    InstanceContainerManager		manager;
    InstanceContainer			cont;
    adams.data.instance.Instance	ainst;
    WekaInstanceToAdamsInstance		conv;
    
    result = super.processFinish();
    
    if (result == null) {
      if (m_CurrentPanel.getParentDialog() != null)
	dlg = new ApprovalDialog(m_CurrentPanel.getParentDialog(), ModalityType.MODELESS);
      else
	dlg = new ApprovalDialog(m_CurrentPanel.getParentFrame(), false);
      m_CurrentPanel.addDependentDialog(dlg);
      explorer = new InstanceExplorer();
      manager  = explorer.getContainerManager();
      manager.startUpdate();
      conv     = new WekaInstanceToAdamsInstance();
      for (weka.core.Instance inst: m_Collected) {
	conv.setInput(inst);
	if (conv.convert() == null) {
	  ainst = (adams.data.instance.Instance) conv.getOutput();
	  cont  = manager.newContainer(ainst);
	  if (m_CurrentPanel.getCurrentFile() != null) {
	    cont.setID(m_CurrentPanel.getCurrentFile().getName());
	    cont.getData().getReport().setStringValue("File", m_CurrentPanel.getCurrentFile().getName());
	    cont.getData().getReport().setStringValue("Path", m_CurrentPanel.getCurrentFile().getParent());
	    cont.getData().getReport().setStringValue("Full", m_CurrentPanel.getCurrentFile().getAbsolutePath());
	  }
	  manager.add(cont);
	}
      }
      conv.cleanUp();
      manager.finishUpdate();
      dlg.setTitle(getDialogTitle());
      dlg.setApproveVisible(true);
      dlg.setCancelVisible(false);
      dlg.setDiscardVisible(false);
      dlg.getContentPane().add(explorer, BorderLayout.CENTER);
      dlg.setSize(getDialogSize());
      dlg.setLocationRelativeTo(null);
      dlg.setVisible(true);
    }
    
    return result;
  }
}
