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
 * FlowPanelNotificationArea.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import adams.core.logging.LoggingLevel;
import adams.flow.core.ActorUtils;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BaseFlatSplitButton;
import adams.gui.core.BaseMenu;
import adams.gui.core.BasePanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.PanelType;
import adams.gui.core.ImageManager;
import adams.gui.core.MultiPageIconSupporter;
import adams.gui.core.PopupMenuCustomizer;
import adams.gui.core.TabIconSupporter;
import adams.gui.core.TextEditorPanel;
import adams.gui.dialog.TextPanel;
import adams.gui.flow.notificationareaaction.AbstractNotificationAreaAction;
import adams.gui.flow.notificationareaaction.CloseAndCleanUp;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Shows textual notifications. 
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowPanelNotificationArea
  extends BasePanel
  implements PopupMenuCustomizer<TextEditorPanel>  {

  /** for serialization. */
  private static final long serialVersionUID = -6807606526180616742L;

  /**
   * The type of notification.
   */
  public enum NotificationType {
    PLAIN(null),
    INFO("flow_ok.png"),
    WARNING("flow_warning.png"),
    ERROR("flow_error.png"),
    QUESTION("flow_question.png");

    /** the icon to use. */
    private String m_Icon;

    /**
     * Initializes the enumeration item.
     *
     * @param icon	the icon name to associate, null for none
     */
    private NotificationType(String icon) {
      m_Icon = icon;
    }

    /**
     * Returns the icon associated with the type.
     *
     * @return		the icon name, null if none
     */
    public String getIcon() {
      return m_Icon;
    }
  }

  /** the owner. */
  protected FlowWorkerHandler m_Owner;

  /** for displaying the text. */
  protected TextPanel m_TextNotification;

  /** the panel for the buttons. */
  protected JPanel m_PanelButtons;

  /** the close button. */
  protected BaseFlatButton m_ButtonClose;

  /** the action button. */
  protected BaseFlatSplitButton m_ButtonAction;

  /** the checkbox for including the console output. */
  protected BaseCheckBox m_CheckBoxConsole;

  /** the close listeners. */
  protected HashSet<ActionListener> m_CloseListeners;

  /** the notification string. */
  protected String m_Notification;

  /** the type of notification. */
  protected NotificationType m_Type;

  /** the available actions. */
  protected List<AbstractNotificationAreaAction> m_Actions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Class[]				classes;
    AbstractNotificationAreaAction	action;

    super.initialize();

    m_Owner          = null;
    m_CloseListeners = new HashSet<>();
    m_Notification   = null;
    m_Type           = NotificationType.PLAIN;
    m_Actions        = new ArrayList<>();
    classes          = AbstractNotificationAreaAction.getActions();
    for (Class cls: classes) {
      try {
	action = (AbstractNotificationAreaAction) cls.getDeclaredConstructor().newInstance();
	action.setOwner(this);
	m_Actions.add(action);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to instantiate action: " + cls.getName(), e);
      }
    }
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panelRight;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TextNotification = new TextPanel();
    m_TextNotification.setUpdateParentTitle(false);
    m_TextNotification.setEditable(false);
    m_TextNotification.setLineWrap(true);
    m_TextNotification.setPopupMenuCustomizer(this);
    add(m_TextNotification, BorderLayout.CENTER);

    panelRight = new JPanel(new BorderLayout());
    panelRight.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(panelRight, BorderLayout.EAST);

    m_PanelButtons = new JPanel(new GridLayout(0, 1, 5, 5));
    panelRight.add(m_PanelButtons, BorderLayout.NORTH);

    m_ButtonAction = new BaseFlatSplitButton();
    m_ButtonAction.setBorderPainted(true);
    m_ButtonAction.setButtonEnabled(true);
    for (AbstractNotificationAreaAction action: m_Actions) {
      if (action instanceof CloseAndCleanUp)
	m_ButtonAction.setAction(action);
      else
	m_ButtonAction.add(action);
    }
    m_PanelButtons.add(m_ButtonAction);

    m_ButtonClose = new BaseFlatButton("Close");
    m_ButtonClose.setBorderPainted(true);
    m_ButtonClose.setIcon(ImageManager.getIcon("delete.gif"));
    m_ButtonClose.addActionListener((ActionEvent e) -> {
      clearNotification();
      notifyCloseListeners();
    });
    m_PanelButtons.add(m_ButtonClose);

    m_CheckBoxConsole = new BaseCheckBox("Console output");
    m_CheckBoxConsole.setSelected(false);
    m_CheckBoxConsole.addActionListener((ActionEvent e) -> update());
    m_PanelButtons.add(m_CheckBoxConsole);
  }

  /**
   * Sets the owning panel.
   *
   * @param value	the owner
   */
  public void setOwner(FlowWorkerHandler value) {
    m_Owner = value;
  }

  /**
   * Returns the current owner.
   *
   * @return		the owner, null if none set
   */
  public FlowWorkerHandler getOwner() {
    return m_Owner;
  }

  /**
   * Adds the listener to the list of listeners waiting for the "Close"
   * button to be pressed.
   *
   * @param l		the listener to add
   */
  public void addCloseListener(ActionListener l) {
    m_CloseListeners.add(l);
  }

  /**
   * Removes the listener from the list of listeners waiting for the "Close"
   * button to be pressed.
   *
   * @param l		the listener to remove
   */
  public void removeCloseListener(ActionListener l) {
    m_CloseListeners.remove(l);
  }

  /**
   * Notifies all the listeners that the close button was pressed.
   */
  public synchronized void notifyCloseListeners() {
    ActionEvent	event;

    event = new ActionEvent(this, ActionEvent.ACTION_FIRST, "close");
    for (ActionListener l: m_CloseListeners)
      l.actionPerformed(event);
  }

  /**
   * Updates the notification area.
   */
  protected void update() {
    Runnable    run;

    run = () -> {
      if (m_Notification == null) {
	m_TextNotification.setContent("");
      }
      else {
	String msg = m_Notification;
	if (m_CheckBoxConsole.isSelected()) {
	  msg += "\n\n--- Console output ---\n\n"
	    + ConsolePanel.getSingleton().getPanel(PanelType.ALL).getContent();
	}
	String[] lines = msg.split("\n");
	setPreferredSize(new Dimension(0, Math.min(300, (lines.length + 1) * 20) + 25));
	m_TextNotification.setContent(msg);
      }

      if (getOwner() != null) {
        displayIcon(getOwner(), m_Type);
	getOwner().getSplitPane().setBottomComponentHidden(m_Notification == null);
	// ensure that the notification panel is visible
	if (m_Notification != null) {
	  int minHeight = (int) getPreferredSize().getHeight();
	  if (getOwner().getSplitPane().getDividerLocation() + minHeight >= getOwner().getSplitPane().getMaximumDividerLocation()) {
	    int newLocation = getOwner().getSplitPane().getMaximumDividerLocation() - minHeight;
	    if (newLocation < 0)
	      newLocation = getOwner().getSplitPane().getMaximumDividerLocation() / 2;
	    getOwner().getSplitPane().setDividerLocation(newLocation);
	  }
	}
      }

      for (AbstractNotificationAreaAction action: m_Actions)
	action.update();
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Displays the notification text.
   *
   * @param msg		the text to display
   * @param type	the type of notification (info/warning/error)
   */
  public void showNotification(String msg, NotificationType type) {
    m_Notification = msg;
    m_Type         = type;
    update();
  }

  /**
   * Removes the notification.
   */
  public void clearNotification() {
    m_Notification = null;
    m_Type         = NotificationType.PLAIN;
    update();
  }

  /**
   * For customizing the popup menu.
   *
   * @param source	the source, e.g., event
   * @param menu	the menu to customize
   */
  @Override
  public void customizePopupMenu(TextEditorPanel source, JPopupMenu menu) {
    List<String> 	paths;
    BaseMenu 		submenu;
    JMenuItem 		menuitem;
    FlowPanel		flowpanel;

    if (getOwner() instanceof FlowPanel) {
      flowpanel = (FlowPanel) getOwner();
      paths = ActorUtils.extractActorNames(flowpanel.getCurrentFlow(), source.getContent());
      if (paths.size() > 0) {
	submenu = new BaseMenu("Jump to");
	menu.add(submenu);
	for (final String path : paths) {
	  menuitem = new JMenuItem(path);
	  menuitem.addActionListener((ActionEvent e) -> flowpanel.getTree().locateAndDisplay(path, true));
	  submenu.add(menuitem);
	}

	submenu = new BaseMenu("Copy location");
	menu.add(submenu);
	for (final String path : paths) {
	  menuitem = new JMenuItem(path);
	  menuitem.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(path));
	  submenu.add(menuitem);
	}
      }
    }
  }

  /**
   * Returns the textual content of the notification area.
   *
   * @return		the text
   */
  public String getContent() {
    return m_TextNotification.getContent();
  }

  /**
   * Pops up a print dialog.
   */
  public void printText() {
    m_TextNotification.printText();
  }

  /**
   * Displays the icon associated with the notification type.
   *
   * @param handler	the handler to display the icon with
   * @param type	the type of icon to display
   */
  public static void displayIcon(FlowWorkerHandler handler, NotificationType type) {
    displayIcon(handler, type.getIcon());
  }

  /**
   * Displays the icon.
   *
   * @param handler	the handler to display the icon with
   * @param icon	the name of the icon to display
   */
  public static void displayIcon(FlowWorkerHandler handler, String icon) {
    if (handler instanceof MultiPageIconSupporter)
      ((MultiPageIconSupporter) handler).setPageIcon(icon);
    if (handler instanceof TabIconSupporter)
      ((TabIconSupporter) handler).setTabIcon(icon);
  }

  /**
   * Clears any icon.
   *
   * @param handler	the handler to display the icon with
   */
  public static void clearIcon(FlowWorkerHandler handler) {
    displayIcon(handler, (String) null);
  }
}
