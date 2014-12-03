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
 * FlowPanelNotificationArea.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextPanel;

/**
 * Shows textual notifications. 
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowPanelNotificationArea
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -6807606526180616742L;

  /** the owner. */
  protected FlowPanel m_Owner;
  
  /** for displaying the text. */
  protected TextPanel m_TextNotification;
  
  /** the close button. */
  protected JButton m_ButtonClose;
  
  /** the copy button. */
  protected JButton m_ButtonCopy;
  
  /** the close listeners. */
  protected HashSet<ActionListener> m_CloseListeners;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Owner          = null;
    m_CloseListeners = new HashSet<ActionListener>();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelRight;
    JPanel	panelButtons;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TextNotification = new TextPanel();
    m_TextNotification.setUpdateParentTitle(false);
    m_TextNotification.setEditable(false);
    m_TextNotification.setLineWrap(true);
    add(m_TextNotification, BorderLayout.CENTER);
    
    panelRight = new JPanel(new BorderLayout());
    panelRight.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(panelRight, BorderLayout.EAST);
    
    panelButtons = new JPanel(new GridLayout(2, 1, 5, 5));
    panelRight.add(panelButtons, BorderLayout.NORTH);
    
    m_ButtonClose = new JButton("Close");
    m_ButtonClose.setIcon(GUIHelper.getIcon("delete.gif"));
    m_ButtonClose.addActionListener(new ActionListener() {      
      @Override
      public void actionPerformed(ActionEvent e) {
        clearNotification();
        notifyCloseListeners();
      }
    });
    panelButtons.add(m_ButtonClose);
    
    m_ButtonCopy = new JButton("Copy");
    m_ButtonCopy.setIcon(GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.addActionListener(new ActionListener() {      
      @Override
      public void actionPerformed(ActionEvent e) {
	GUIHelper.copyToClipboard(m_TextNotification.getContent());
      }
    });
    panelButtons.add(m_ButtonCopy);
  }
  
  /**
   * Sets the owning panel.
   * 
   * @param value	the owner
   */
  public void setOwner(FlowPanel value) {
    m_Owner = value;
  }
  
  /**
   * Returns the current owner.
   * 
   * @return		the owner, null if none set
   */
  public FlowPanel getOwner() {
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
  protected synchronized void notifyCloseListeners() {
    ActionEvent	event;
    
    event = new ActionEvent(this, ActionEvent.ACTION_FIRST, "close");
    for (ActionListener l: m_CloseListeners)
      l.actionPerformed(event);
  }
  
  /**
   * Displays the notification text.
   * 
   * @param msg		the text to display
   * @param error	true if an error message
   */
  public void showNotification(String msg, boolean error) {
    String[]	lines;
    
    lines = msg.split("\n");
    setPreferredSize(new Dimension(0, Math.min(300, (lines.length + 1) * 20) + 25));
    m_TextNotification.setContent(msg);
    if (getOwner() != null) {
      getOwner().setTabIcon(error ? "stop.gif" : "validate_blue.png");
      getOwner().getSplitPane().setBottomComponentHidden(false);
    }
  }
  
  /**
   * Removes the notification.
   */
  public void clearNotification() {
    m_TextNotification.setContent("");
    if (getOwner() != null) {
      getOwner().setTabIcon(null);
      getOwner().getSplitPane().setBottomComponentHidden(true);
    }
  }
}
