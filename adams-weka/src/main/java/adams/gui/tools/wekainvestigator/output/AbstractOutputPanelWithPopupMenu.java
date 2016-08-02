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
 * AbstractOutputPanelWithPopupMenu.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.core.CleanUpHandler;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PopupMenuCustomizer;
import adams.gui.core.PopupMenuProvider;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Ancestor for output panels that can save the displayed output to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOutputPanelWithPopupMenu<T extends BaseFileChooser>
  extends BasePanel
  implements PopupMenuProvider, CleanUpHandler {

  private static final long serialVersionUID = -2818808520522758309L;

  /** the content panel. */
  protected BasePanel m_ContentPanel;

  /** the filechooser. */
  protected T m_FileChooser;

  /** the optional popup menu customizer. */
  protected PopupMenuCustomizer<AbstractOutputPanelWithPopupMenu<T>> m_PopupMenuCustomizer;

  /** the frame for detaching the component. */
  protected BaseFrame m_DetachedFrame;

  /** the panel with a "reattach" frame. */
  protected BasePanel m_PanelReattach;

  /** the button for reattaching. */
  protected JButton m_ButtonReattach;

  /** the frame title to use. */
  protected String m_FrameTitle;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser         = createFileChooser();
    m_PopupMenuCustomizer = null;
    m_DetachedFrame      = null;
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

    m_ButtonReattach = new JButton("Reattach");
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
   * Creates the filechooser to use.
   *
   * @return		the filechooser
   */
  protected abstract T createFileChooser();

  /**
   * Saves the content to the specified file.
   *
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  protected abstract String save(File file);

  /**
   * Pops up the filechooser.
   */
  protected void save() {
    int		retVal;
    String	msg;

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    msg = save(m_FileChooser.getSelectedFile());
    if (msg != null)
      GUIHelper.showErrorMessage(this, "Failed to save content to: " + m_FileChooser.getSelectedFile() + "\n" + msg);
  }

  /**
   * Detaches the content into a separate frame.
   */
  protected void detach() {
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
    m_DetachedFrame.setSize(GUIHelper.getDefaultDialogDimension());
    m_DetachedFrame.setLocationRelativeTo(this);
    m_DetachedFrame.setVisible(true);

    add(m_PanelReattach, BorderLayout.CENTER);
    invalidate();
    revalidate();
    doLayout();
    repaint();
  }

  /**
   * Reattaches the content from a separate frame.
   */
  protected void reattach() {
    if (!isDetached())
      return;
    remove(m_PanelReattach);
    add(m_ContentPanel, BorderLayout.CENTER);
    invalidate();
    revalidate();
    doLayout();
    repaint();

    m_DetachedFrame.dispose();
    m_DetachedFrame = null;
  }

  /**
   * Sets the popup customizer to use.
   *
   * @param value	the customizer, can be null
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer<AbstractOutputPanelWithPopupMenu<T>> value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the current popup customizer in use.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer<AbstractOutputPanelWithPopupMenu<T>> getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Returns the popup menu.
   */
  @Override
  public JPopupMenu getPopupMenu() {
    JPopupMenu		result;
    JMenuItem		menuitem;

    result = new JPopupMenu();

    if (isDetached()) {
      menuitem = new JMenuItem("Reattach", GUIHelper.getIcon("minimize.png"));
      menuitem.addActionListener((ActionEvent e) -> reattach());
      result.add(menuitem);
    }
    else {
      menuitem = new JMenuItem("Detach", GUIHelper.getIcon("maximize.png"));
      menuitem.addActionListener((ActionEvent e) -> detach());
      result.add(menuitem);
    }

    menuitem = new JMenuItem("Save...", GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener((ActionEvent e) -> save());
    result.add(menuitem);

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
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (isDetached())
      reattach();
  }
}
