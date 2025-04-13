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
 * BaseStatusBar.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.StatusMessageHandler;
import adams.gui.dialog.TextDialog;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A bar for displaying status messages (left and right).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseStatusBar
  extends BasePanel
  implements StatusMessageHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4700122818727471957L;

  /**
   * Interface for classes that process the status string to bring it into
   * a displayable format.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static interface StatusProcessor {

    /**
     * Processes the status string.
     *
     * @param msg	the status string to process
     * @return		the processed string
     */
    public String process(String msg);
  }

  /**
   * Interface for classes that modify the statusbar's popup menu.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static interface PopupMenuCustomizer {

    /**
     * For customizing the popup menu.
     *
     * @param source	the source statusbar
     * @param left 	whether left or right status
     * @param menu	the menu to customize
     */
    public void customizePopupMenu(BaseStatusBar source, boolean left, JPopupMenu menu);
  }


  /** the empty status. */
  public final static String EMPTY_STATUS = " ";

  /** the default title. */
  public final static String DEFAULT_TITLE = "Status";

  /** a label for displaying a status. */
  protected JLabel m_LabelStatusLeft;

  /** a mouse listener for displaying the message in a dialog. */
  protected MouseListener m_MouseListener;

  /** the title for the status dialog. */
  protected String m_StatusDialogTitle;

  /** for custom processing the status message before displaying it. */
  protected StatusProcessor m_StatusProcessor;

  /** the popup menu customizer to use. */
  protected PopupMenuCustomizer m_PopupMenuCustomizer;

  /** the default dimension for displaying the status. */
  protected Dimension m_DialogSize;

  /** the current status (left). */
  protected String m_StatusLeft;

  /** the current status (right). */
  protected String m_StatusRight;

  /** the label for the right status. */
  protected JLabel m_LabelStatusRight;

  /** the expiry time in seconds for clearing the left status automatically. */
  protected int m_ExpiryTimeLeft;

  /** the expiry time in seconds for clearing the right status automatically. */
  protected int m_ExpiryTimeRight;

  /** the timer for the left message. */
  protected Timer m_TimerLeft;

  /** the timer for the right message. */
  protected Timer m_TimerRight;

  /** the current timer for the left message. */
  protected TimerTask m_ClearTaskLeft;

  /** the current timer for the right message. */
  protected TimerTask m_ClearTaskRight;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_MouseListener       = null;
    m_StatusDialogTitle   = DEFAULT_TITLE;
    m_StatusProcessor     = null;
    m_DialogSize          = GUIHelper.getDefaultTinyDialogDimension();
    m_PopupMenuCustomizer = null;
    m_StatusLeft          = EMPTY_STATUS;
    m_StatusRight         = EMPTY_STATUS;
    m_ExpiryTimeLeft      = 0;
    m_ExpiryTimeRight     = 0;
    m_TimerLeft           = null;
    m_TimerRight          = null;
    m_ClearTaskLeft       = null;
    m_ClearTaskRight      = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	inner;

    super.initGUI();

    setLayout(new BorderLayout());

    inner = new JPanel(new BorderLayout());
    inner.setBorder(BorderHelper.createBevelBorder(BevelBorder.LOWERED));
    add(inner, BorderLayout.CENTER);

    // left
    panel             = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_LabelStatusLeft = new JLabel(EMPTY_STATUS);
    panel.add(m_LabelStatusLeft, BorderLayout.WEST);
    inner.add(panel, BorderLayout.WEST);

    // right
    panel              = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_LabelStatusRight = new JLabel("");
    panel.add(m_LabelStatusRight);
    inner.add(panel, BorderLayout.EAST);
  }

  /**
   * Removes any status message currently being displayed (left only).
   */
  public void clearStatus() {
    clearStatus(true);
  }

  /**
   * Removes any status message currently being displayed.
   *
   * @param left	whether to clear left or right
   */
  public void clearStatus(boolean left) {
    if (left) {
      m_StatusLeft = EMPTY_STATUS;
      m_LabelStatusLeft.setText(EMPTY_STATUS);
      if (m_ClearTaskLeft != null) {
	m_ClearTaskLeft.cancel();
	m_ClearTaskLeft = null;
      }
    }
    else {
      m_StatusRight = EMPTY_STATUS;
      m_LabelStatusRight.setText(EMPTY_STATUS);
      if (m_ClearTaskRight != null) {
	m_ClearTaskRight.cancel();
	m_ClearTaskRight = null;
      }
    }
  }

  /**
   * Returns whether a status is currently being displayed (left).
   *
   * @return		true if a status message is being displayed
   */
  public boolean hasStatus() {
    return hasStatus(true);
  }

  /**
   * Returns whether a status is currently being displayed.
   *
   * @param left	whether to query left or right status
   * @return		true if a status message is being displayed
   */
  public boolean hasStatus(boolean left) {
    if (left)
      return !m_StatusLeft.equals(EMPTY_STATUS);
    else
      return !m_StatusRight.equals(EMPTY_STATUS);
  }

  /**
   * Displays a message (left).
   *
   * @param msg		the message to display
   */
  public void setStatus(final String msg) {
    setStatus(true, msg);
  }

  /**
   * Displays a message.
   *
   * @param left	whether to update left or right status
   * @param msg		the message to display
   */
  public synchronized void setStatus(final boolean left, final String msg) {
    if (left) {
      m_StatusLeft = msg;
      SwingUtilities.invokeLater(() -> {
	if ((msg == null) || (msg.length() == 0))
	  clearStatus(true);
	else
	  m_LabelStatusLeft.setText(msg.replace("\r\n", "|").replace("\n", "|"));
      });
      if (m_TimerLeft != null) {
	if (m_ClearTaskLeft != null)
	  m_ClearTaskLeft.cancel();
	m_ClearTaskLeft = new TimerTask() {
	  @Override
	  public void run() {
	    clearStatus(true);
	  }
	};
	m_TimerLeft.schedule(m_ClearTaskLeft, m_ExpiryTimeLeft * 1000);
      }
    }
    else {
      m_StatusRight = msg;
      SwingUtilities.invokeLater(() -> {
	if ((msg == null) || (msg.length() == 0))
	  clearStatus(false);
	else
	  m_LabelStatusRight.setText(msg.replace("\r\n", "|").replace("\n", "|"));
      });
      if (m_TimerRight != null) {
	if (m_ClearTaskRight != null)
	  m_ClearTaskRight.cancel();
	m_ClearTaskRight = new TimerTask() {
	  @Override
	  public void run() {
	    clearStatus(false);
	  }
	};
	m_TimerRight.schedule(m_ClearTaskRight, m_ExpiryTimeRight * 1000);
      }
    }
  }

  /**
   * Returns the currently displayed status (left).
   *
   * @return		the status, null if none displayed
   */
  public String getStatus() {
    return getStatus(true);
  }

  /**
   * Returns the currently displayed status.
   *
   * @param left	whether to query left or right status
   * @return		the status, null if none displayed
   */
  public String getStatus(boolean left) {
    String	result;

    result = null;

    if (left) {
      if (!m_StatusLeft.equals(EMPTY_STATUS))
	result = m_StatusLeft;
    }
    else {
      if (!m_StatusRight.equals(EMPTY_STATUS))
	result = m_StatusRight;
    }

    return result;
  }

  /**
   * Displays a message (left). Just an alias for setStatus(String).
   *
   * @param msg		the message to display
   * @see		#setStatus(String)
   */
  public void showStatus(String msg) {
    setStatus(true, msg);
  }

  /**
   * Displays a message. Just an alias for setStatus(boolean, String)
   *
   * @param left	whether to update left or right status
   * @param msg		the message to display
   * @see		#setStatus(boolean, String)
   */
  public void showStatus(boolean left, String msg) {
    setStatus(left, msg);
  }

  /**
   * Displays the status in a dialog.
   *
   * @param left	whether to display left or right status
   */
  protected void displayStatus(boolean left) {
    String 	status;
    Component 	parent;
    TextDialog	dialog;

    if (left)
      status = m_StatusLeft;
    else
      status = m_StatusRight;
    if (m_StatusProcessor != null)
      status = m_StatusProcessor.process(status);

    if (getParentDialog() != null) {
      parent = getParentDialog();
      dialog = new TextDialog((Dialog) parent);
    }
    else {
      parent = getParentFrame();
      dialog = new TextDialog((Frame) parent);
    }
    dialog.setDialogTitle(m_StatusDialogTitle);
    dialog.setContent(status);
    dialog.setEditable(false);
    dialog.setSize(m_DialogSize);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  /**
   * Adds the mouse listener to the status bar (the JLabel, actually).
   *
   * @param l		the listener to add
   */
  @Override
  public void addMouseListener(MouseListener l) {
    m_LabelStatusLeft.addMouseListener(l);
    m_LabelStatusRight.addMouseListener(l);
  }

  /**
   * Removes the mouse listener from the status bar (the JLabel, actually).
   *
   * @param l		the listener to removes
   */
  @Override
  public void removeMouseListener(MouseListener l) {
    m_LabelStatusLeft.removeMouseListener(l);
    m_LabelStatusRight.removeMouseListener(l);
  }

  /**
   * Sets whether to turn mouse listener on (left only).
   *
   * @param value	if true then the mouse listener is active
   */
  public void setMouseListenerActive(boolean value) {
    if (value != (m_MouseListener != null)) {
      if (value) {
	m_MouseListener = new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {
	    if (e.getComponent() == m_LabelStatusLeft) {
	      if (MouseUtils.isDoubleClick(e) && (m_StatusLeft.length() > 0)) {
		e.consume();
		displayStatus(true);
	      }
	      else if (MouseUtils.isRightClick(e)) {
		e.consume();
		BasePopupMenu menu = getPopup(true);
		menu.showAbsolute(m_LabelStatusLeft, e);
	      }
	      else {
		super.mouseClicked(e);
	      }
	    }
	    else if (e.getComponent() == m_LabelStatusRight) {
	      if (MouseUtils.isDoubleClick(e) && (m_StatusRight.length() > 0)) {
		e.consume();
		displayStatus(false);
	      }
	      else if (MouseUtils.isRightClick(e)) {
		e.consume();
		BasePopupMenu menu = getPopup(false);
		menu.showAbsolute(m_LabelStatusRight, e);
	      }
	      else {
		super.mouseClicked(e);
	      }
	    }
	    else {
	      super.mouseClicked(e);
	    }
	  }
	};
	addMouseListener(m_MouseListener);
      }
      else {
	removeMouseListener(m_MouseListener);
      }
    }
  }

  /**
   * Returns whether the mouse listener is active.
   *
   * @return		true if the mouse listener is active
   */
  public boolean isMouseListenerActive() {
    return (m_MouseListener != null);
  }

  /**
   * Returns the popup menu for the status (left only).
   *
   * @param left	whether for left or right label
   * @return		the popup menu
   */
  protected BasePopupMenu getPopup(final boolean left) {
    BasePopupMenu	result;
    JMenuItem		menuitem;

    result = new BasePopupMenu();

    menuitem = new JMenuItem("Show status", ImageManager.getIcon("editor.gif"));
    menuitem.setEnabled(m_StatusLeft.length() > 0);
    menuitem.addActionListener((ActionEvent e) -> displayStatus(left));
    result.add(menuitem);

    result.addSeparator();

    menuitem = new JMenuItem("Clear status", ImageManager.getIcon("new.gif"));
    menuitem.setEnabled(m_StatusLeft.length() > 0);
    menuitem.addActionListener((ActionEvent e) -> clearStatus(left));
    result.add(menuitem);

    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, left, result);

    return result;
  }

  /**
   * Sets the title to use for the dialog when displaying the status bar in a
   * dialog.
   *
   * @param value	the title, use null for default
   */
  public void setStatusDialogTitle(String value) {
    if (value == null)
      m_StatusDialogTitle = DEFAULT_TITLE;
    else
      m_StatusDialogTitle = value;
  }

  /**
   * Returns the currently set title for the status dialog.
   *
   * @return		the title
   */
  public String getStatusDialogTitle() {
    return m_StatusDialogTitle;
  }

  /**
   * Sets the code for processing the status message before displaying it.
   *
   * @param value	the processor to use, null to turn off
   */
  public void setStatusProcessor(StatusProcessor value) {
    m_StatusProcessor = value;
  }

  /**
   * Returns the code for processing the status message before displaying it.
   *
   * @return		the processor in use, null if none set
   */
  public StatusProcessor getStatusProcessor() {
    return m_StatusProcessor;
  }

  /**
   * Sets the size for the dialog.
   *
   * @param value	the size to use
   */
  public void setDialogSize(Dimension value) {
    m_DialogSize = new Dimension(value);
  }

  /**
   * Returns the size for the dialog.
   *
   * @return		the size in use
   */
  public Dimension getDialogSize() {
    return m_DialogSize;
  }

  /**
   * Sets the popup menu customizer to use.
   *
   * @param value	the customizer, null to unset
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the current popup customizer in use.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Sets the expiry time in seconds.
   *
   * @param left	whether to set the time for the left or right message
   * @param seconds	expiry time in seconds, 0 to turn off expiry
   */
  public void setExpiryTime(boolean left, int seconds) {
    if (seconds < 0)
      seconds = 0;

    if (left) {
      if (m_TimerLeft != null) {
	m_TimerLeft.cancel();
	m_TimerLeft = null;
      }
      m_ExpiryTimeLeft = seconds;
      if (m_ExpiryTimeLeft > 0)
	m_TimerLeft = new Timer();
    }
    else {
      if (m_TimerRight != null) {
	m_TimerRight.cancel();
	m_TimerRight = null;
      }
      m_ExpiryTimeRight = seconds;
      if (m_ExpiryTimeRight > 0)
	m_TimerRight = new Timer();
    }
  }

  /**
   * Returns the expiry time in seconds.
   *
   * @param left	whether to return left or right expiry
   * @return		the time in seconds, 0 for no expiry
   */
  public int getExpiryTime(boolean left) {
    if (left)
      return m_ExpiryTimeLeft;
    else
      return m_ExpiryTimeRight;
  }
}
