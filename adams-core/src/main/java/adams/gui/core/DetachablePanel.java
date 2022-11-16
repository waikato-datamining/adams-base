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
 * DetachablePanel.java
 * Copyright (C) 2016-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.CleanUpHandler;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Ancestor for panels that can be detached in a frame and also reattached again.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DetachablePanel
  extends BasePanel
  implements PopupMenuProvider, CleanUpHandler {

  private static final long serialVersionUID = -2818808520522758309L;

  public final static String DETACH_ICON = "maximize.png";

  public final static String REATTACH_ICON = "minimize.png";

  /** the content panel. */
  protected BasePanel m_ContentPanel;

  /** the optional popup menu customizer. */
  protected PopupMenuCustomizer<DetachablePanel> m_PopupMenuCustomizer;

  /** the frame for detaching the component. */
  protected BaseFrame m_DetachedFrame;

  /** the panel with a "reattach" frame. */
  protected BasePanel m_PanelReattach;

  /** the button for reattaching. */
  protected BaseButton m_ButtonReattach;

  /** the frame title to use. */
  protected String m_FrameTitle;

  /** the last size of the frame. */
  protected Dimension m_LastFrameSize;

  /** the last position of the frame. */
  protected Point m_LastFramePosition;

  /** the detach listeners. */
  protected Set<ChangeListener> m_DetachListeners;

  /** the reattach listeners. */
  protected Set<ChangeListener> m_ReattachListeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PopupMenuCustomizer = null;
    m_DetachedFrame       = null;
    m_LastFrameSize       = null;
    m_LastFramePosition   = null;
    m_DetachListeners     = new HashSet<>();
    m_ReattachListeners   = new HashSet<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());
    m_ContentPanel = new BasePanel(new BorderLayout());
    add(m_ContentPanel, BorderLayout.CENTER);

    m_PanelReattach = new BasePanel(new BorderLayout());
    panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    m_PanelReattach.add(panel, BorderLayout.CENTER);

    m_ButtonReattach = new BaseButton("Reattach", ImageManager.getIcon(REATTACH_ICON));
    m_ButtonReattach.addActionListener((ActionEvent e) -> reattach());
    panel.add(m_ButtonReattach);
  }

  /**
   * Returns the content panel where to add the other widgets.
   *
   * @return		the panel for the content
   */
  public BasePanel getContentPanel() {
    return m_ContentPanel;
  }

  /**
   * Returns whether the content is currently detached and displayed in a frame.
   *
   * @return		true if detached
   */
  public boolean isDetached() {
    return (m_DetachedFrame != null);
  }

  /**
   * Detaches the content into a separate frame.
   */
  public void detach() {
    if (isDetached())
      return;

    m_DetachedFrame = new BaseFrame(getFrameTitle());
    m_DetachedFrame.setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);
    m_DetachedFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
	super.windowClosed(e);
	reattach();
      }
    });
    m_DetachedFrame.getContentPane().setLayout(new BorderLayout());
    m_DetachedFrame.getContentPane().add(m_ContentPanel, BorderLayout.CENTER);
    m_DetachedFrame.pack();
    m_DetachedFrame.setSize(m_LastFrameSize == null ? GUIHelper.getDefaultDialogDimension() : m_LastFrameSize);
    if (m_LastFramePosition == null)
      m_DetachedFrame.setLocationRelativeTo(this);
    else
      m_DetachedFrame.setLocation(m_LastFramePosition);
    m_DetachedFrame.setVisible(true);

    add(m_PanelReattach, BorderLayout.CENTER);
    invalidate();
    revalidate();
    doLayout();
    repaint();

    notifyDetachListeners();
  }

  /**
   * Reattaches the content from a separate frame.
   */
  public void reattach() {
    if (!isDetached())
      return;
    remove(m_PanelReattach);
    add(m_ContentPanel, BorderLayout.CENTER);
    invalidate();
    revalidate();
    doLayout();
    repaint();

    m_LastFrameSize = m_DetachedFrame.getSize();
    m_LastFramePosition = m_DetachedFrame.getLocation();

    m_DetachedFrame.dispose();
    m_DetachedFrame = null;

    notifyReattachListeners();
  }

  /**
   * Sets the popup customizer to use.
   *
   * @param value	the customizer, can be null
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer<DetachablePanel> value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the current popup customizer in use.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer<DetachablePanel> getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Creates and returns the popup menu.
   *
   * @return		the menu
   */
  protected JPopupMenu createPopupMenu() {
    JPopupMenu		result;
    JMenuItem		menuitem;

    result = new JPopupMenu();

    if (isDetached()) {
      menuitem = new JMenuItem("Reattach", ImageManager.getIcon(REATTACH_ICON));
      menuitem.addActionListener((ActionEvent e) -> reattach());
      result.add(menuitem);
    }
    else {
      menuitem = new JMenuItem("Detach", ImageManager.getIcon(DETACH_ICON));
      menuitem.addActionListener((ActionEvent e) -> detach());
      result.add(menuitem);
    }

    return result;
  }

  /**
   * Returns the popup menu.
   */
  @Override
  public JPopupMenu getPopupMenu() {
    JPopupMenu		result;

    result = createPopupMenu();

    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, result);

    return result;
  }

  /**
   * Sets the frame title to use.
   *
   * @param value	the title for the detached frame
   */
  public void setFrameTitle(String value) {
    m_FrameTitle = value;
  }

  /**
   * Returns the frame title to use.
   *
   * @return		the title fro the detached frame
   */
  public String getFrameTitle() {
    return m_FrameTitle;
  }

  /**
   * Adds the listener for detach events.
   *
   * @param l		the listener to add
   */
  public void addDetachListener(ChangeListener l) {
    m_DetachListeners.add(l);
  }

  /**
   * Removes the listener for detach events.
   *
   * @param l		the listener to remove
   */
  public void removeDetachListener(ChangeListener l) {
    m_DetachListeners.remove(l);
  }

  /**
   * Removes all detach event listeners.
   */
  public void clearDetachListeners() {
    m_DetachListeners.clear();
  }

  /**
   * Notifies all detach listeners.
   */
  protected void notifyDetachListeners() {
    ChangeEvent		e;
    
    e = new ChangeEvent(this);
    for (ChangeListener l: m_DetachListeners.toArray(new ChangeListener[0]))
      l.stateChanged(e);
  }

  /**
   * Adds the listener for reattach events.
   *
   * @param l		the listener to add
   */
  public void addReattachListener(ChangeListener l) {
    m_ReattachListeners.add(l);
  }

  /**
   * Removes the listener for reattach events.
   *
   * @param l		the listener to remove
   */
  public void removeReattachListener(ChangeListener l) {
    m_ReattachListeners.remove(l);
  }

  /**
   * Removes all reattach event listeners.
   */
  public void clearReattachListeners() {
    m_ReattachListeners.clear();
  }

  /**
   * Notifies all reattach listeners.
   */
  protected void notifyReattachListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ReattachListeners.toArray(new ChangeListener[0]))
      l.stateChanged(e);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (isDetached())
      reattach();
    m_DetachListeners.clear();
    m_ReattachListeners.clear();
  }
}
