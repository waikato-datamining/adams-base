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
 * ExperimenterEntryPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

/**
 * Allows the display of multiple Experimenter panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6682 $
 */
public class ExperimenterEntryPanel
  extends AbstractNamedHistoryPanel<ExperimenterPanel> {

  /** for serialization. */
  private static final long serialVersionUID = 1704390033157269580L;
  
  /** the panel to display the results in. */
  protected BasePanel m_Panel;

  /** the parent's default title. */
  protected String m_Title;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Panel = null;
    m_Title = null;
  }

  /**
   * Sets the panel to display the results in.
   *
   * @param value	the panel to display
   */
  public void setPanel(BasePanel value) {
    m_Panel = value;
  }

  /**
   * Revalidates the panel to make changes visible.
   */
  protected void updatePanel() {
    m_Panel.getParent().invalidate();
    m_Panel.getParent().validate();
    m_Panel.getParent().repaint();
  }
  
  /**
   * Updates the menu bar.
   * 
   * @param menu	the menubar to use, can be null to remove menu
   */
  protected void updateMenu(final JMenuBar menu) {
    Runnable	run;

    run = new Runnable() {
      @Override
      public void run() {
	if (m_Panel.getParentFrame() != null) {
	  ((JFrame) m_Panel.getParentFrame()).setJMenuBar(menu);
	  m_Panel.getParentFrame().setVisible(true);  // necessary, otherwise menu is either blocked or not properly removed
	}
	else if (m_Panel.getParentDialog() != null) {
	  ((JDialog) m_Panel.getParentDialog()).setJMenuBar(menu);
	  m_Panel.getParentDialog().setVisible(true);  // necessary, otherwise menu is either blocked or not properly removed
	}
      }
    };
    SwingUtilities.invokeLater(run);
  }
  
  /**
   * Displays the specified entry.
   *
   * @param name	the name of the entry, can be null to clear display
   */
  @Override
  protected void updateEntry(String name) {
    ExperimenterPanel	experimenter;
    String		title;
    
    m_Panel.removeAll();

    if (name != null) {
      // update panel
      if (hasEntry(name)) {
	experimenter = getEntry(name);
	if (m_Title == null)
	  m_Title = experimenter.getTitle();
        m_Panel.add(experimenter);
        updatePanel();
        updateMenu(experimenter.getMenuBar());
	experimenter.updateTitle();
      }
    }
    else {
      updatePanel();
      updateMenu(null);
      if (m_Title != null)
	title = m_Title;
      else
	title = "Experimenter";
      if (GUIHelper.getParentDialog(this) != null)
	GUIHelper.getParentDialog(this).setTitle(title);
      else if (GUIHelper.getParentFrame(this) != null)
	GUIHelper.getParentFrame(this).setTitle(title);
    }
  }
}
