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
 * VariablesTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tab;

import adams.core.Utils;
import adams.flow.processor.ListAllVariables;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Allows user to list variables in flow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariablesTab
  extends AbstractTabChangeAwareEditorTab {

  private static final long serialVersionUID = 1745841596971673114L;

  /** the button for refreshing the variable list. */
  protected JButton m_ButtonRefresh;

  /** for listing all the variables. */
  protected BaseTextArea m_TextAreaVariables;

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Variables";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);
    m_ButtonRefresh = new JButton("Refresh", GUIHelper.getIcon("refresh.gif"));
    m_ButtonRefresh.addActionListener(e -> refreshVariables());
    panel.add(m_ButtonRefresh);

    m_TextAreaVariables = new BaseTextArea();
    m_TextAreaVariables.setTextFont(Fonts.getMonospacedFont());
    m_TextAreaVariables.setEditable(false);
    add(new BaseScrollPane(m_TextAreaVariables), BorderLayout.CENTER);
  }

  /**
   * Refreshes the variables.
   */
  protected void refreshVariables() {
    ListAllVariables	list;

    list = new ListAllVariables();
    list.process(getCurrentPanel().getCurrentFlow());
    m_TextAreaVariables.setText(Utils.flatten(list.getVariables(), "\n"));
  }

  /**
   * Notifies the tab of the currently selected flow panel.
   *
   * @param panel	the new panel
   */
  @Override
  public void flowPanelChanged(FlowPanel panel) {
    m_TextAreaVariables.setText("");
  }
}
