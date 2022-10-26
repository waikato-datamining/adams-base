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
 * ActorLocationsPanel.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.gui.core.BaseButton;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tree.Tree;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Panel for listing actor locations. If a parent component (FlowPanel/Tree)
 * is available, jumping to the locations is possible, too.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActorLocationsPanel
  extends BasePanel {

  private static final long serialVersionUID = 5481630762689053835L;

  /** the parent component. */
  protected Component m_ParentComponent;

  /** the locations. */
  protected List<String> m_Locations;

  /** the list. */
  protected BaseListWithButtons m_List;

  /** the button for copying the location. */
  protected BaseButton m_ButtonCopy;

  /** the button for jumping to the location. */
  protected BaseButton m_ButtonJumpTo;

  /**
   * Initializes the panel.
   *
   * @param parentComponent	the parent component to use, can be null
   * @param locations 		the list of locations
   */
  public ActorLocationsPanel(Component parentComponent, List<String> locations) {
    super();

    m_ParentComponent = parentComponent;
    m_Locations       = locations;

    initGUI();
    finishInit();
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    DefaultListModel		model;

    if (m_ParentComponent == null)
      return;

    super.initGUI();

    setLayout(new BorderLayout());

    m_List = new BaseListWithButtons();
    model = new DefaultListModel<>();
    for (String item: m_Locations)
      model.addElement(item);
    m_List.setModel(model);

    m_ButtonCopy = new BaseButton("Copy");
    m_ButtonCopy.setEnabled(false);
    m_ButtonCopy.addActionListener((ActionEvent e) -> {
      Object[] values = m_List.getSelectedValuesList().toArray();
      StringBuilder content = new StringBuilder();
      for (Object value: values) {
	if (content.length() > 0)
	  content.append("\n");
	content.append("" + value);
      }
      ClipboardHelper.copyToClipboard(content.toString());
    });
    m_List.addToButtonsPanel(m_ButtonCopy);

    if (m_ParentComponent != null) {
      m_ButtonJumpTo = new BaseButton("Jump to");
      m_ButtonJumpTo.setEnabled(false);
      m_ButtonJumpTo.addActionListener((ActionEvent e) -> {
	if (m_List.getSelectedIndex() > -1) {
	  if (m_ParentComponent instanceof FlowPanel) {
	    ((FlowPanel) m_ParentComponent).getTree().locateAndDisplay(
	      "" + m_List.getSelectedValue(), true);
	  }
	  else if (m_ParentComponent instanceof Tree) {
	    ((Tree) m_ParentComponent).locateAndDisplay(
	      "" + m_List.getSelectedValue(), true);
	  }
	}
      });
      m_List.addToButtonsPanel(m_ButtonJumpTo);
      m_List.setDoubleClickButton(m_ButtonJumpTo);
    }
    else {
      m_ButtonJumpTo = null;
    }

    m_List.addListSelectionListener((ListSelectionEvent e) -> {
      m_ButtonCopy.setEnabled(m_List.getSelectedIndices().length > 0);
      if (m_ButtonJumpTo != null)
	m_ButtonJumpTo.setEnabled(m_List.getSelectedIndices().length == 1);
    });

    add(m_List, BorderLayout.CENTER);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    if (m_ParentComponent == null)
      return;

    super.finishInit();
  }
}
