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
 * DatabaseConnectionDialog.java
 * Copyright (C) 2011-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.ImageManager;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A standalone dialog for managing all the connections to the databases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DatabaseConnectionsDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 3625063329591593840L;

  /** the dialog itself. */
  protected DatabaseConnectionsDialog m_Self;

  /** database connection panel. */
  protected DatabaseConnectionsPanel m_Panel;

  /** the Close button. */
  protected BaseButton m_ButtonClose;

  /** the Disconnect button. */
  protected BaseButton m_ButtonDisconnect;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public DatabaseConnectionsDialog(Dialog owner) {
    this(owner, "Connect to databases");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public DatabaseConnectionsDialog(Dialog owner, String title) {
    super(owner, title, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public DatabaseConnectionsDialog(Frame owner) {
    this(owner, "Connect to databases");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public DatabaseConnectionsDialog(Frame owner, String title) {
    super(owner, title, true);
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_Self = this;
  }

  /**
   * initializes the GUI elements.
   */
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());

    // DB connection
    m_Panel = new DatabaseConnectionsPanel();
    add(m_Panel, BorderLayout.CENTER);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonDisconnect = new BaseButton("Disconnect");
    m_ButtonDisconnect.setMnemonic('D');
    m_ButtonDisconnect.addActionListener((ActionEvent e) -> m_Panel.disconnectConnections());
    panel.add(m_ButtonDisconnect);

    m_ButtonClose = new BaseButton("Close", ImageManager.getIcon("exit.png"));
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addActionListener((ActionEvent e) -> m_Self.setVisible(false));
    panel.add(m_ButtonClose);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	beforeHide();
        super.windowClosing(e);
      }
    });

    pack();
  }
}
