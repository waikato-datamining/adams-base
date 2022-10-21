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

/*
 * PreferencesManagerPanel.java
 * Copyright (C) 2013-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingLevel;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MultiPagePane;
import adams.gui.core.UISettings;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Panel that combines all the preference panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PreferencesManagerPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 8245611221697036772L;

  /** the multi-page pane with all the setups. */
  protected MultiPagePane m_MultiPagePanel;

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

    m_MultiPagePanel = new MultiPagePane();
    m_MultiPagePanel.setReadOnly(true);
    m_MultiPagePanel.setUISettingsParameters(getClass(), "Divider");
    if (UISettings.has(getClass(), "Divider"))
      m_MultiPagePanel.setDividerLocation(UISettings.get(getClass(), "Divider", 200));
    add(m_MultiPagePanel, BorderLayout.CENTER);

    classes = AbstractPreferencesPanel.getPanels();
    for (String cls: classes) {
      try {
	m_Panels.add((PreferencesPanel) ClassManager.getSingleton().forName(cls).getDeclaredConstructor().newInstance());
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
      final BaseButton buttonReset = new BaseButton("Default");
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
      final BaseButton buttonApply = new BaseButton("Apply");
      buttonApply.addActionListener((ActionEvent e) -> {
	SwingWorker worker = new SwingWorker() {
	  String msg = null;
	  @Override
	  protected Object doInBackground() throws Exception {
	    buttonReset.setEnabled(false);
	    buttonApply.setEnabled(false);
	    msg = panel.activate();
	    return null;
	  }
	  @Override
	  protected void done() {
	    buttonApply.setEnabled(true);
	    buttonReset.setEnabled(panel.canReset());
	    if (msg != null)
	      GUIHelper.showErrorMessage(
		  PreferencesManagerPanel.this,
		  "Failed to activate preferences for " + panel.getTitle() + ":\n" + msg);
	    else
	      showRestartNotification();
	    super.done();
	  }
	};
	worker.execute();
      });
      panelButtons.add(buttonApply);
      if (panel.requiresWrapper())
	panelPage.add(new BaseScrollPane((JComponent) panel), BorderLayout.CENTER);
      else
	panelPage.add((JComponent) panel, BorderLayout.CENTER);
      m_MultiPagePanel.addPage(panel.getTitle(), panelPage);
    }
    if (m_MultiPagePanel.getPageCount() > 0)
      m_MultiPagePanel.setSelectedIndex(0);
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
