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
 * BaseStatusBar.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.StatusMessageHandler;
import adams.gui.dialog.TextDialog;

import javax.swing.BorderFactory;
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
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A bar for displaying a status message.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * @version $Revision$
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
   * @version $Revision$
   */
  public static interface PopupMenuCustomizer
    extends adams.gui.core.PopupMenuCustomizer<BaseStatusBar> {
    
    /**
     * For customizing the popup menu.
     *
     * @param source	the source statusbar
     * @param menu	the menu to customize
     */
    public void customizePopupMenu(BaseStatusBar source, JPopupMenu menu);
  }


  /** the empty status. */
  public final static String EMPTY_STATUS = " ";

  /** the default title. */
  public final static String DEFAULT_TITLE = "Status";

  /** a label for displaying a status. */
  protected JLabel m_LabelStatus;

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
  
  /** the current status. */
  protected String m_Status;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_MouseListener       = null;
    m_StatusDialogTitle   = DEFAULT_TITLE;
    m_StatusProcessor     = null;
    m_DialogSize          = new Dimension(400, 300);
    m_PopupMenuCustomizer = null;
    m_Status              = EMPTY_STATUS;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    panel         = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_LabelStatus = new JLabel(EMPTY_STATUS);
    panel.add(m_LabelStatus, BorderLayout.WEST);
    add(panel, BorderLayout.WEST);
  }

  /**
   * Removes any status message currently being displayed.
   */
  protected void clearStatus() {
    m_Status = EMPTY_STATUS;
    m_LabelStatus.setText(EMPTY_STATUS);
  }

  /**
   * Returns whether a status is currently being displayed.
   *
   * @return		true if a status message is being displayed
   */
  public boolean hasStatus() {
    return !m_Status.equals(EMPTY_STATUS);
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void setStatus(final String msg) {
    Runnable	run;

    m_Status = msg;
    run = new Runnable() {
      public void run() {
	if ((msg == null) || (msg.length() == 0))
	  clearStatus();
	else
	  m_LabelStatus.setText(msg.replace("\r\n", "|").replace("\n", "|"));
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Returns the currently displayed status.
   *
   * @return		the status, null if none displayed
   */
  public String getStatus() {
    String	result;

    result = null;

    if (!m_Status.equals(EMPTY_STATUS))
      result = m_Status;

    return result;
  }

  /**
   * Displays a message. Just an alias for setStatus(String)
   *
   * @param msg		the message to display
   * @see		#setStatus(String)
   */
  public void showStatus(String msg) {
    setStatus(msg);
  }

  /**
   * Displays the status in a dialog.
   */
  protected void displayStatus() {
    String 	status;
    Component 	parent;
    TextDialog	dialog;

    status = m_Status;
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
    m_LabelStatus.addMouseListener(l);
  }

  /**
   * Removes the mouse listener from the status bar (the JLabel, actually).
   *
   * @param l		the listener to removes
   */
  @Override
  public void removeMouseListener(MouseListener l) {
    m_LabelStatus.removeMouseListener(l);
  }

  /**
   * Sets whether to turn mouse listener on.
   *
   * @param value	if true then the mouse listener is active
   */
  public void setMouseListenerActive(boolean value) {
    if (value != (m_MouseListener != null)) {
      if (value) {
	m_MouseListener = new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {
	    if (MouseUtils.isDoubleClick(e) && (m_Status.length() > 0)) {
	      e.consume();
	      displayStatus();
	    }
	    else if (MouseUtils.isRightClick(e)) {
	      e.consume();
	      BasePopupMenu menu = getPopup();
	      menu.showAbsolute(m_LabelStatus, e);
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
   * Returns the popup menu for the status.
   *
   * @return		the popup menu
   */
  protected BasePopupMenu getPopup() {
    BasePopupMenu	result;
    JMenuItem		menuitem;

    result = new BasePopupMenu();

    menuitem = new JMenuItem("Show status");
    menuitem.setEnabled(m_Status.length() > 0);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	displayStatus();
      }
    });
    result.add(menuitem);

    result.addSeparator();

    menuitem = new JMenuItem("Clear status");
    menuitem.setEnabled(m_Status.length() > 0);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	clearStatus();
      }
    });
    result.add(menuitem);
    
    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, result);

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
}
