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
 * InteractionDisplayLocationHelper.java
 * Copyright (C) 2023-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.UniqueIDs;
import adams.core.Utils;
import adams.gui.core.BaseButton;
import adams.gui.core.GUIHelper.DialogCommunication;
import adams.gui.core.GUIHelper.InputPanelWithButtons;
import adams.gui.core.GUIHelper.InputPanelWithComboBox;
import adams.gui.core.GUIHelper.InputPanelWithTextArea;
import adams.gui.flow.FlowWorkerHandler;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Helper class for displaying panels in the notification area.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class InteractionDisplayLocationHelper {

  /**
   * Retrieves the FlowWorkerHandler from the context.
   *
   * @param context	the context to use
   * @return		the handler
   * @throws IllegalStateException	if no context or not a {@link FlowWorkerHandler}
   */
  public static FlowWorkerHandler getFlowWorkerHandler(Actor context) {
    if (context == null)
      throw new IllegalStateException("No owner!");
    if (!(context.getParentComponent() instanceof FlowWorkerHandler))
      throw new IllegalStateException("Parent component of owner is not of type " + Utils.classToString(FlowWorkerHandler.class));
    return (FlowWorkerHandler) context.getParentComponent();
  }

  /**
   * Displays the panel in the notification area.
   *
   * @param context	the flow context to use
   * @param comm	the interaction communication
   * @param panel	the panel to display
   * @return		the selected button, null if stopped
   */
  public static String display(Actor context, DialogCommunication comm, InputPanelWithButtons panel) {
    String		sync;
    final StringBuilder answer;

    getFlowWorkerHandler(context).showNotification(panel, panel.getIcon());

    answer = new StringBuilder();
    for (BaseButton button: panel.getButtons()) {
      button.addActionListener((ActionEvent e) -> {
	answer.append(button.getText());
      });
    }

    // wait till answer provided
    sync = UniqueIDs.next();
    while ((answer.length() == 0) && !comm.isCloseRequested()) {
      try {
	synchronized (sync) {
	  sync.wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    getFlowWorkerHandler(context).clearNotification();

    if (comm.isCloseRequested())
      return null;
    else
      return answer.toString();
  }

  /**
   * Displays the panel in the notification area.
   *
   * @param context		the flow context to use
   * @param comm		the interaction communication
   * @param panel		the panel to display
   * @param btnJustification 	the justification of the OK/Cancel buttons (see {@link FlowLayout})
   * @return			the selected button, null if stopped
   */
  public static String display(Actor context, DialogCommunication comm, InputPanelWithComboBox panel, int btnJustification) {
    String		sync;
    JPanel		panelTop;
    JPanel		panelAll;
    JPanel		panelButtons;
    final BaseButton	buttonOK;
    final BaseButton	buttonCancel;
    final StringBuilder	answer;

    answer = new StringBuilder();

    panelTop = new JPanel(new BorderLayout());
    panelTop.add(panel, BorderLayout.NORTH);

    panelAll = new JPanel(new BorderLayout());
    panelAll.add(panelTop, BorderLayout.NORTH);

    panelButtons = new JPanel(new FlowLayout(btnJustification));
    panelButtons.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    panelTop.add(panelButtons, BorderLayout.CENTER);
    buttonOK = new BaseButton("OK");
    buttonOK.addActionListener((ActionEvent e) -> answer.append(panel.getValue()));
    panelButtons.add(buttonOK);
    buttonCancel = new BaseButton("Cancel");
    buttonCancel.addActionListener((ActionEvent e) -> comm.requestClose());
    panelButtons.add(buttonCancel);

    getFlowWorkerHandler(context).showNotification(panelAll, panel.getIcon());

    // wait till answer provided
    sync = UniqueIDs.next();
    while ((answer.length() == 0) && !comm.isCloseRequested()) {
      try {
	synchronized (sync) {
	  sync.wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    getFlowWorkerHandler(context).clearNotification();

    if (comm.isCloseRequested())
      return null;
    else
      return answer.toString();
  }

  /**
   * Displays the panel in the notification area.
   *
   * @param context		the flow context to use
   * @param comm		the interaction communication
   * @param panel		the panel to display
   * @param btnJustification 	the justification of the OK/Cancel buttons (see {@link FlowLayout})
   * @return			the selected button, null if stopped
   */
  public static String display(Actor context, DialogCommunication comm, InputPanelWithTextArea panel, int btnJustification) {
    String		sync;
    JPanel		panelTop;
    JPanel		panelAll;
    JPanel		panelButtons;
    final BaseButton	buttonOK;
    final BaseButton	buttonCancel;
    final StringBuilder	answer;

    answer = new StringBuilder();

    panelTop = new JPanel(new BorderLayout());
    panelTop.add(panel, BorderLayout.NORTH);

    panelAll = new JPanel(new BorderLayout());
    panelAll.add(panelTop, BorderLayout.NORTH);

    panelButtons = new JPanel(new FlowLayout(btnJustification));
    panelButtons.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    panelTop.add(panelButtons, BorderLayout.CENTER);
    buttonOK = new BaseButton("OK");
    buttonOK.addActionListener((ActionEvent e) -> answer.append(panel.getValue()));
    panelButtons.add(buttonOK);
    buttonCancel = new BaseButton("Cancel");
    buttonCancel.addActionListener((ActionEvent e) -> comm.requestClose());
    panelButtons.add(buttonCancel);

    getFlowWorkerHandler(context).showNotification(panelAll, panel.getIcon());

    // wait till answer provided
    sync = UniqueIDs.next();
    while ((answer.length() == 0) && !comm.isCloseRequested()) {
      try {
	synchronized (sync) {
	  sync.wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    getFlowWorkerHandler(context).clearNotification();

    if (comm.isCloseRequested())
      return null;
    else
      return answer.toString();
  }

  /**
   * Displays the panel in the notification area.
   *
   * @param context		the flow context to use
   * @param comm		the interaction communication
   * @param panel		the panel to display
   * @param btnJustification 	the justification of the OK/Cancel buttons (see {@link FlowLayout})
   * @return			whether OK (true) or Cancel (false) was selected, null if stopped
   */
  public static Boolean display(Actor context, DialogCommunication comm, JPanel panel, int btnJustification) {
    return display(context, comm, panel, btnJustification, null);
  }

  /**
   * Displays the panel in the notification area.
   *
   * @param context		the flow context to use
   * @param comm		the interaction communication
   * @param panel		the panel to display
   * @param btnJustification 	the justification of the OK/Cancel buttons (see {@link FlowLayout})
   * @param icon		the icon to display, null for none
   * @return			whether OK (true) or Cancel (false) was selected, null if stopped
   */
  public static Boolean display(Actor context, DialogCommunication comm, JPanel panel, int btnJustification, String icon) {
    String		sync;
    JPanel		panelTop;
    JPanel		panelAll;
    JPanel		panelButtons;
    final BaseButton	buttonOK;
    final BaseButton	buttonCancel;
    final StringBuilder	answer;

    answer = new StringBuilder();

    panelTop = new JPanel(new BorderLayout());
    panelTop.add(panel, BorderLayout.NORTH);

    panelAll = new JPanel(new BorderLayout());
    panelAll.add(panelTop, BorderLayout.NORTH);

    panelButtons = new JPanel(new FlowLayout(btnJustification));
    panelButtons.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    panelTop.add(panelButtons, BorderLayout.CENTER);
    buttonOK = new BaseButton("OK");
    buttonOK.addActionListener((ActionEvent e) -> answer.append("OK"));
    panelButtons.add(buttonOK);
    buttonCancel = new BaseButton("Cancel");
    buttonCancel.addActionListener((ActionEvent e) -> comm.requestClose());
    panelButtons.add(buttonCancel);

    getFlowWorkerHandler(context).showNotification(panelAll, icon);

    // wait till answer provided
    sync = UniqueIDs.next();
    while ((answer.length() == 0) && !comm.isCloseRequested()) {
      try {
	synchronized (sync) {
	  sync.wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    getFlowWorkerHandler(context).clearNotification();

    if (comm.isCloseRequested())
      return null;
    else
      return answer.toString().equalsIgnoreCase("OK");
  }
}
