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
 * PreferencesManagerPanel.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.logging.LoggingLevel;
import adams.gui.core.BaseMultiPagePane;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;

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

    m_Panels = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    String[]	classes;
    JPanel 	panelButtons;
    JPanel	panelPage;

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
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to instantiate preferences panel: " + cls, e);
      }
    }

    Collections.sort(m_Panels);
    for (PreferencesPanel panel: m_Panels) {
      panelPage = new JPanel(new BorderLayout());
      panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      panelPage.add(panelButtons, BorderLayout.SOUTH);
      final JButton buttonReset = new JButton("Default");
      buttonReset.setEnabled(panel.canReset());
      buttonReset.addActionListener((ActionEvent e) -> {
	String msg = panel.reset();
	if (msg != null)
	  GUIHelper.showErrorMessage(
	    PreferencesManagerPanel.this,
	    "Failed to reset preferences for " + panel.getTitle() + ":\n" + msg);
	else
	  showRestartNotification();
	buttonReset.setEnabled(panel.canReset());
      });
      panelButtons.add(buttonReset);
      final JButton buttonApply = new JButton("Apply");
      buttonApply.addActionListener((ActionEvent e) -> {
	String msg = panel.activate();
	if (msg != null)
	  GUIHelper.showErrorMessage(
	    PreferencesManagerPanel.this,
	    "Failed to activate preferences for " + panel.getTitle() + ":\n" + msg);
	else
	  showRestartNotification();
	buttonReset.setEnabled(panel.canReset());
      });
      panelButtons.add(buttonApply);
      if (panel.requiresWrapper())
	panelPage.add(new BaseScrollPane((JComponent) panel), BorderLayout.CENTER);
      else
	panelPage.add((JComponent) panel, BorderLayout.CENTER);
      m_MultiPagePanel.addPage(panel.getTitle(), panelPage);
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
	GUIHelper.showErrorMessage(
	  this, "Failed to activate preferences for " + panel.getTitle() + ":\n" + msg);
    }

    showRestartNotification();
  }

  /**
   * Resets all the settings.
   */
  public void reset() {
    String	msg;

    for (PreferencesPanel panel: m_Panels) {
      msg = panel.reset();
      if (msg != null)
	GUIHelper.showErrorMessage(
	  this, "Failed to reset preferences for " + panel.getTitle() + ":\n" + msg);
    }

    showRestartNotification();
  }

  /**
   * Shows a dialog telling the user to restart the applications for the
   * changes to take effect.
   */
  protected void showRestartNotification() {
    GUIHelper.showInformationMessage(
      this,
      "For the changes to take effect, you need to restart the application now.");
  }
}
