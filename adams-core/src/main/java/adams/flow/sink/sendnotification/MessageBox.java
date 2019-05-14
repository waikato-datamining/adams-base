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
 * MessageBox.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sendnotification;

import adams.core.QuickInfoHelper;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;
import adams.gui.dialog.TextPanel;

import java.awt.BorderLayout;

/**
 * Simply pops up a message box with the notification.
 * Outputs to stdout if in headless environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MessageBox
  extends AbstractNotification {

  private static final long serialVersionUID = 4577706017089540470L;

  /** whether to block the flow execution. */
  protected boolean m_Block;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply pops up a message box with the notification.\n"
      + "Outputs to stdout if in headless environment.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "block", "block",
      false);
  }

  /**
   * Sets whether to block flow execution.
   *
   * @param value 	true if to block
   */
  public void setBlock(boolean value) {
    m_Block = value;
    reset();
  }

  /**
   * Returns whether to block flow execution.
   *
   * @return 		true if to block
   */
  public boolean getBlock() {
    return m_Block;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String blockTipText() {
    return "If enabled, blocks the flow execution till the dialog has been closed.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "block", m_Block, "block");
  }

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  @Override
  protected String doSendNotification(String msg) {
    final ApprovalDialog 	dlg;
    String[]			lines;
    int				height;
    TextPanel 			editor;

    if (GUIHelper.isHeadless()) {
      System.out.println(msg);
    }
    else {
      lines  = msg.split("\n");
      height = Math.min(350, (lines.length + 1) * 20);
      dlg = ApprovalDialog.getDialog(null, m_Block);
      dlg.setTitle("Notification");
      dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
      dlg.setIconImage(GUIHelper.getIcon("information.png").getImage());
      editor = new TextPanel();
      editor.setTitle("Notification");
      editor.setEditable(false);
      editor.setLineWrap(true);
      dlg.getContentPane().add(editor, BorderLayout.CENTER);
      dlg.pack();
      dlg.setSize(
	GUIHelper.getInteger("DefaultSmallDialog.Width", 600),
	Math.min(dlg.getHeight() + height, (int) (GUIHelper.getScreenBounds(dlg).height * 0.5)));
      dlg.setLocationRelativeTo(null);
      editor.setContent(msg);
      dlg.getApproveButton().requestFocusInWindow();
      dlg.setVisible(true);
    }
    return null;
  }
}
