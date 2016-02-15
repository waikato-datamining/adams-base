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
 * FileOpenRemoteFlow.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.Utils;
import adams.core.option.NestedConsumer;
import adams.flow.core.Actor;
import adams.flow.execution.RemoteFlowListener;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.flow.FlowPanel;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Lets user connect to a remote flow and retrieve the setup.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileOpenRemoteFlow
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Open remote flow...";
  }

  /**
   * Attempts to connect to the remote flow and display it.
   *
   * @param server      the server to connect to
   * @param port        the port to connect to
   */
  protected void openRemote(String server, int port) {
    Socket            	socket;
    BufferedReader    	in;
    StringBuilder     	buffer;
    String            	line;
    NestedConsumer    	consumer;
    Actor 		actor;
    FlowPanel         	panel;

    buffer = new StringBuilder();
    socket = null;
    try {
      socket = new Socket(server, port);
      in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      while ((line = in.readLine()) != null) {
        buffer.append(line);
        buffer.append("\n");
      }
      in.close();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
        m_State.getParentDialog(),
        "Failed to connect to " + server + ":" + port + "\n" + Utils.throwableToString(e));
      return;
    }
    finally {
      if (socket != null) {
        try {
          socket.close();
        }
        catch (Exception e) {
          // ignored
        }
      }
    }

    consumer = new NestedConsumer();
    actor    = (Actor) consumer.fromString(buffer.toString());
    if (actor == null) {
      GUIHelper.showErrorMessage(
        m_State.getParentDialog(),
        "Failed to instantiate flow from:\n" + buffer);
    }
    else {
      panel = m_State.getFlowPanels().newPanel();
      panel.setCurrentFlow(actor);
      panel.setTitle(server + ":" + port);
      panel.updateTitle();
    }
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ParameterPanel    panel;
    JTextField        textServer;
    JSpinner          spinnerPort;
    ApprovalDialog dialog;

    panel = new ParameterPanel();
    textServer = new JTextField(20);
    textServer.setText("127.0.0.1");
    panel.addParameter("Server", textServer);

    spinnerPort = new JSpinner();
    ((SpinnerNumberModel) spinnerPort.getModel()).setMinimum(1);
    ((SpinnerNumberModel) spinnerPort.getModel()).setMaximum(65535);
    spinnerPort.setValue(RemoteFlowListener.DEFAULT_PORT);
    panel.addParameter("Port", spinnerPort);

    dialog = new ApprovalDialog(m_State.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Open remote flow");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(m_State.getParentDialog());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    openRemote(textServer.getText(), (Integer) spinnerPort.getValue());
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(true);
  }
}
