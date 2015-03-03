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
 * SetupManagerPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import adams.gui.core.BaseMultiPagePane;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;

/**
 * Panel that combines all the preference panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreferencesManagerPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 8245611221697036772L;

  /** the multi-page pane with all the setups. */
  protected BaseMultiPagePane m_MultiPagePanel;

  /** the setup panels. */
  protected ArrayList<PreferencesPanel> m_Panels;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Panels = new ArrayList<PreferencesPanel>();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    String[]	classes;
    JPanel	wrapper;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    
    m_MultiPagePanel = new BaseMultiPagePane();
    add(m_MultiPagePanel, BorderLayout.CENTER);
    
    classes = AbstractPreferencesPanel.getPanels();
    for (String cls: classes) {
      try {
	m_Panels.add((PreferencesPanel) Class.forName(cls).newInstance());
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate preferences panel: " + cls);
	e.printStackTrace();
      }
    }
    
    Collections.sort(m_Panels);
    for (PreferencesPanel panel: m_Panels) {
      if (panel.requiresWrapper()) {
	wrapper = new JPanel(new BorderLayout());
	wrapper.add(new BaseScrollPane((JComponent) panel), BorderLayout.NORTH);
	m_MultiPagePanel.addPage(panel.getTitle(), wrapper);
      }
      else {
	m_MultiPagePanel.addPage(panel.getTitle(), (JComponent) panel);
      }
    }
  }
  
  /**
   * Activates all the settings.
   */
  public void activate() {
    String	msg;
    
    for (PreferencesPanel panel: m_Panels) {
      msg = panel.activate();
      if (msg != null)
	GUIHelper.showErrorMessage(this, msg);
    }
    
    GUIHelper.showInformationMessage(
	this, 
	"For the changes to take effect, you need to restart the application now.");
  }
}
