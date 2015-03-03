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
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;

/**
 * A standalone dialog for connecting to a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseConnectionDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = -1846927534390634983L;

  /** the dialog itself. */
  protected DatabaseConnectionDialog m_Self;

  /** database connection panel. */
  protected DatabaseConnectionPanel m_Panel;

  /** the Close button. */
  protected JButton m_ButtonClose;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public DatabaseConnectionDialog(Dialog owner) {
    this(owner, "Connect to database");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public DatabaseConnectionDialog(Dialog owner, String title) {
    super(owner, title, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public DatabaseConnectionDialog(Frame owner) {
    this(owner, "Connect to database");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public DatabaseConnectionDialog(Frame owner, String title) {
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
   * Returns the database connection panel.
   *
   * @return		the panel
   */
  protected DatabaseConnectionPanel newDatabaseConnectionPanel() {
    return new DatabaseConnectionPanel();
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
    m_Panel = new DatabaseConnectionPanel();
    add(m_Panel, BorderLayout.CENTER);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonClose = new JButton("Close", GUIHelper.getIcon("exit.png"));
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_Self.setVisible(false);
      }
    });
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
