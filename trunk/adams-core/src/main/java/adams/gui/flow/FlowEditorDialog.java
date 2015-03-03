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
 * FlowEditorDialog.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import adams.gui.core.BaseDialog;

/**
 * Dialog that displays a flow editor panel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowEditorDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 545478782839955844L;

  /** the underlying Flow panel. */
  protected FlowEditorPanel m_FlowEditorPanel;

  /**
   * Creates a modal dialog with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public FlowEditorDialog(Dialog owner) {
    super(owner, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Creates a modal dialog with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public FlowEditorDialog(Frame owner) {
    super(owner, true);
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    String	classname;

    super.initGUI();

    setTitle("Flow editor");

    m_FlowEditorPanel = new FlowEditorPanel();
    classname = FlowEditorPanel.getPropertiesEditor().getPath(
	"FlowEditorClass", FlowEditorPanel.class.getName());
    try {
      m_FlowEditorPanel = (FlowEditorPanel) Class.forName(classname).newInstance();
    }
    catch (Exception ex) {
      m_FlowEditorPanel = new FlowEditorPanel();
    }
    setTitle("Flow editor");
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(m_FlowEditorPanel, BorderLayout.CENTER);
    setJMenuBar(m_FlowEditorPanel.getMenuBar());
    setSize(800, 600);
    setLocationRelativeTo(this);
  }

  /**
   * Returns the underlying flow editor panel.
   *
   * @return		the panel
   */
  public FlowEditorPanel getFlowEditorPanel() {
    return m_FlowEditorPanel;
  }
}
