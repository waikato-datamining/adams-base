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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JPanel;

import adams.gui.core.BasePanel;
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
    JPanel	panel;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TextNotification = new TextPanel();
    m_TextNotification.setUpdateParentTitle(false);
    m_TextNotification.setEditable(false);
    m_TextNotification.setLineWrap(true);
    add(m_TextNotification, BorderLayout.CENTER);
    
    panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    add(panel, BorderLayout.EAST);
    
    m_ButtonClose = new JButton("Close");
    m_ButtonClose.addActionListener(new ActionListener() {      
      @Override
      public void actionPerformed(ActionEvent e) {
        clearNotification();
        notifyCloseListeners();
      }
    });
    panel.add(m_ButtonClose);
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
    setPreferredSize(new Dimension(0, Math.min(300, (lines.length + 1) * 20)));
    m_TextNotification.setContent(msg);
    if (getOwner() != null)
      getOwner().setTabIcon(error ? "stop.gif" : "validate_blue.png");
    setVisible(true);
  }
  
  /**
   * Removes the notification.
   */
  public void clearNotification() {
    setVisible(false);
    m_TextNotification.setContent("");
    if (getOwner() != null)
      getOwner().setTabIcon(null);
  }
}
