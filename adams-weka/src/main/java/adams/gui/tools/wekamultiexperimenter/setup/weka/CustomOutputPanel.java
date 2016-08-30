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
 * CustomOutputPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup.weka;

import adams.gui.core.ParameterPanel;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import weka.experiment.InstancesResultListener;
import weka.experiment.ResultListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;

/**
 * Allows the user to configure any {@link ResultListener}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CustomOutputPanel
  extends AbstractOutputPanel {

  /** for serialization. */
  private static final long serialVersionUID = 6294601080342288801L;
  
  /** the GOE panel. */
  protected WekaGenericObjectEditorPanel m_PanelGOE;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    ParameterPanel	panel;
    
    super.initGUI();
    
    panel = new ParameterPanel();
    m_PanelGOE = new WekaGenericObjectEditorPanel(ResultListener.class, new InstancesResultListener(), true);
    m_PanelGOE.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	m_Owner.setModified(true);
      }
    });
    panel.addParameter("Results", m_PanelGOE);
    add(panel, BorderLayout.CENTER);
  }

  /**
   * Returns the name to display in the GUI.
   * 
   * @return		the name
   */
  @Override
  public String getOutputName() {
    return "Custom";
  }
  
  /**
   * Returns whether this panel handles the specified {@link ResultListener}.
   * 
   * @param listener	the listener to check
   * @return		true if the panel handles this listener
   */
  @Override
  public boolean handlesResultListener(ResultListener listener) {
    return true;
  }

  /**
   * Sets the {@link ResultListener}.
   * 
   * @param value	the listener
   */
  @Override
  public void setResultListener(ResultListener value) {
    m_PanelGOE.setCurrent(value);
  }

  /**
   * Returns the configured {@link ResultListener}.
   * 
   * @return		the listener, null if failed to set up
   */
  @Override
  public ResultListener getResultListener() {
    return (ResultListener) m_PanelGOE.getCurrent();
  }
}
