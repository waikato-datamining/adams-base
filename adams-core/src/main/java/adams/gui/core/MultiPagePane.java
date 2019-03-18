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
 * MultiPagePane.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.CleanUpHandler;
import adams.gui.event.RemoveItemsListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Manages multiple pages, like JTabbedPane manages multiple tabs.
 * Uses a JList for listing the page titles.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiPagePane
  extends BasePanel
  implements CleanUpHandler, UISettingsSupporter {

  private static final long serialVersionUID = -2108092957035381345L;

  public static class DetachablePage
    extends DetachablePanel {

    private static final long serialVersionUID = 1968992223273451733L;

    /** the component to wrap. */
    protected Component m_Component;

    /**
     * Initializes the detachable panel with the component.
     *
     * @param comp	the component to allow to detach
     */
    public DetachablePage(Component comp) {
      super();
      m_Component = comp;
      getContentPanel().add(m_Component, BorderLayout.CENTER);
    }

    /**
     * Returns the wrapped component.
     *
     * @return		the actual component
     */
    public Component getComponent() {
      return m_Component;
    }
  }

  /**
   * Container for page component and title.
   */
  public static class PageContainer
    implements Serializable {

    private static final long serialVersionUID = -7918640108273902031L;

    /** the title. */
    protected String m_Title;

    /** the page. */
    protected Component m_Page;

    /** the detachable panel. */
    protected DetachablePage m_DetachablePage;

    /** the current icon (can be null). */
    protected ImageIcon m_Icon;

    /**
     * Initializes the container with no icon.
     *
     * @param title	the title
     * @param page	the page
     */
    public PageContainer(String title, Component page) {
      this(title, page, null);
    }

    /**
     * Initializes the container.
     *
     * @param title	the title
     * @param page	the page
     * @param icon	the icon
     */
    public PageContainer(String title, Component page, ImageIcon icon) {
      super();
      m_Title = title;
      m_Page  = page;
      m_Icon  = icon;
      m_DetachablePage = new DetachablePage(page);
    }

    /**
     * Returns the title.
     *
     * @return		the title
     */
    public String getTitle() {
      return m_Title;
    }

    /**
     * Sets the title.
     *
     * @param value	the title
     */
    public void setTitle(String value) {
      m_Title = value;
    }

    /**
     * Returns the page.
     *
     * @return		the page
     */
    public Component getPage() {
      return m_Page;
    }

    /**
     * Sets the page.
     *
     * @param value	the page
     */
    public void setPage(Component value) {
      m_Page = value;
      m_DetachablePage.getContentPanel().removeAll();
      m_DetachablePage.getContentPanel().add(value, BorderLayout.CENTER);
      m_DetachablePage.getContentPanel().invalidate();
      m_DetachablePage.getContentPanel().revalidate();
      m_DetachablePage.getContentPanel().repaint();
    }

    /**
     * Returns the detachable page.
     *
     * @return		the page
     */
    public DetachablePage getDetachablePage() {
      return m_DetachablePage;
    }

    /**
     * Just returns the title.
     *
     * @return		the title
     */
    public String toString() {
      return m_Title;
    }

    /**
     * Returns the icon.
     *
     * @return		the icon
     */
    public ImageIcon getIcon() {
      return m_Icon;
    }

    /**
     * Sets the icon.
     *
     * @param value	the icon
     */
    public void setIcon(ImageIcon value) {
      m_Icon = value;
    }
  }

  /**
   * Interface for classes that can hook into the closing of pages and stop it.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public interface PageCloseApprover {

    /**
     * Method gets called when having to approve a close operation of
     * multiple pages.
     *
     * @param source 	the origin of the closing operation
     * @param index	the page that is to be closed
     */
    public boolean approvePageClosing(MultiPagePane source, int index);
  }

  /**
   * Interface for classes that want to customize the popup menu for entry.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public interface PopupCustomizer {

    /**
     * Gets called before the popup for the entry is displayed.
     *
     * @param index	the index this menu is for
     * @param menu	the menu so far
     */
    public void customizePopup(int index, JPopupMenu menu);
  }

  /**
   * Interface for classes that want to customize the tool tip of an entry.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public interface ToolTipCustomizer {

    /**
     * Gets called before the tool tip is set.
     *
     * @param index	the index this menu is for
     * @param toolTip	the tool tip at the moment, can be null
     * @return		the updated tool tip
     */
    public String customizeToolTip(int index, String toolTip);
  }

  /**
   * The cell renderer.
   */
  public static class TitleRenderer
    extends DefaultListCellRenderer {

    private static final long serialVersionUID = 662711521384106051L;

    /** the owner. */
    protected MultiPagePane m_Owner;

    /** the border for no focus. */
    protected Border m_BorderNoFocus;

    /** the border for focused. */
    protected Border m_BorderFocused;

    /**
     * Initializes the renderer with the owner.
     *
     * @param owner		the owner
     */
    public TitleRenderer(MultiPagePane owner) {
      super();
      m_Owner = owner;
    }

    /**
     * Returns the owner.
     *
     * @return		the pane this renderer belongs to
     */
    public MultiPagePane getOwner() {
      return m_Owner;
    }

    /**
     * Returns the rendering component.
     *
     * @param list		the list this renderer is for
     * @param value		the current list value
     * @param index		the index of the value
     * @param isSelected	whether the item is selected
     * @param cellHasFocus	whether the cell has the focus
     * @return			the rendering component
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component		result;
      JLabel 		label;
      PageContainer	cont;

      if (m_BorderNoFocus == null) {
        m_BorderNoFocus = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        m_BorderFocused = BorderFactory.createLineBorder(list.getSelectionBackground().darker(), 1);
      }

      result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      label  = (JLabel) result;
      if (cellHasFocus)
        label.setBorder(m_BorderFocused);
      else
        label.setBorder(m_BorderNoFocus);
      cont   = (PageContainer) list.getModel().getElementAt(index);
      label.setIcon(cont.getIcon());

      return result;
    }
  }

  /**
   * Container for the page undo list.
   */
  public static class PageUndo {
    /** the component that made up the tab. */
    public Component component;

    /** the title. */
    public String title;

    /** the position. */
    public int index;

    /** tiptext. */
    public String tiptext;

    /** the icon. */
    public ImageIcon icon;

    /** the tab component. */
    public Component tabComponent;

    /** whether the tab was selected. */
    public boolean selected;
  }

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the panel with the list and buttons. */
  protected BasePanel m_LeftPanel;

  /** the page list. */
  protected BaseList m_PageList;

  /** the list model. */
  protected DefaultListModel<PageContainer> m_PageListModel;

  /** the panel for the list buttons. */
  protected BasePanel m_PanelListButtons;

  /** the move up button. */
  protected BaseFlatButton m_ButtonUp;

  /** the move down button. */
  protected BaseFlatButton m_ButtonDown;

  /** the remove button. */
  protected BaseFlatButton m_ButtonRemove;

  /** the remove all button. */
  protected BaseFlatButton m_ButtonRemoveAll;

  /** the undo button. */
  protected BaseFlatButton m_ButtonUndo;

  /** the action button. */
  protected BaseSplitButton m_ButtonAction;

  /** the content pane for the pages. */
  protected BasePanel m_PanelContent;

  /** the listeners when pages get selected. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** whether to ignore updates. */
  protected boolean m_IgnoreUpdates;

  /** for approving page closing. */
  protected PageCloseApprover m_PageCloseApprover;

  /** an optional customizer for the popup on the JList. */
  protected PopupCustomizer m_PopupCustomizer;

  /** an optional customizer for the tooltips of the JList. */
  protected ToolTipCustomizer m_ToolTipCustomizer;

  /** whether the page list is readonly. */
  protected boolean m_ReadOnly;

  /** the maximum number of tabs to keep for undo. */
  protected int m_MaxPageCloseUndo;

  /** the list of tabs to undo. */
  protected transient List<PageUndo> m_PageUndoList;

  /** whether to skip tab undo. */
  protected boolean m_SkipPageUndo;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PageListModel     = new DefaultListModel<>();
    m_ChangeListeners   = new HashSet<>();
    m_IgnoreUpdates     = false;
    m_PageCloseApprover = null;
    m_PopupCustomizer   = null;
    m_ToolTipCustomizer = null;
    m_ReadOnly          = false;
    m_MaxPageCloseUndo  = 0;
    m_PageUndoList      = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setDividerLocation(200);
    m_SplitPane.setResizeWeight(0.0);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);

    m_LeftPanel = new BasePanel(new BorderLayout());
    m_SplitPane.setLeftComponent(m_LeftPanel);

    m_PageList = new BaseList(m_PageListModel);
    m_PageList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_PageList.addListSelectionListener((ListSelectionEvent e) -> update());
    m_PageList.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (!processListKey(e))
	  super.keyPressed(e);
      }
    });
    m_PageList.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
	int index = m_PageList.locationToIndex(e.getPoint());
	m_PageList.setToolTipText(generateToolTip(index));
      }
    });
    m_PageList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isRightClick(e)) {
          showPopup(e);
          e.consume();
        }
        else {
          super.mouseClicked(e);
        }
      }
    });
    m_LeftPanel.add(new BaseScrollPane(m_PageList), BorderLayout.CENTER);

    m_PanelListButtons = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_LeftPanel.add(m_PanelListButtons, BorderLayout.SOUTH);

    m_ButtonUp = new BaseFlatButton(GUIHelper.getIcon("arrow_up.gif"));
    m_ButtonUp.setToolTipText("Moves up selected");
    m_ButtonUp.addActionListener((ActionEvent e) -> moveUp());
    m_PanelListButtons.add(m_ButtonUp);

    m_ButtonDown = new BaseFlatButton(GUIHelper.getIcon("arrow_down.gif"));
    m_ButtonDown.setToolTipText("Moves down selected");
    m_ButtonDown.addActionListener((ActionEvent e) -> moveDown());
    m_PanelListButtons.add(m_ButtonDown);

    m_ButtonRemove = new BaseFlatButton(GUIHelper.getIcon("delete.gif"));
    m_ButtonRemove.setToolTipText("Removes currently selected");
    m_ButtonRemove.addActionListener((ActionEvent e) -> checkedRemoveSelectedPages());
    m_PanelListButtons.add(m_ButtonRemove);

    m_ButtonRemoveAll = new BaseFlatButton(GUIHelper.getIcon("delete_all.gif"));
    m_ButtonRemoveAll.setToolTipText("Removes all");
    m_ButtonRemoveAll.addActionListener((ActionEvent e) -> checkedRemoveAllPages());
    m_PanelListButtons.add(m_ButtonRemoveAll);

    m_ButtonAction = new BaseSplitButton();
    m_ButtonAction.setAlwaysDropdown(false);
    m_ButtonAction.setToolTipText("Additional actions");
    m_ButtonAction.setVisible(false);
    m_PanelListButtons.add(m_ButtonAction);

    m_ButtonUndo = new BaseFlatButton(GUIHelper.getIcon("undo.gif"));
    m_ButtonUndo.setToolTipText("Undo removal");
    m_ButtonUndo.addActionListener((ActionEvent e) -> undoPageClose());

    m_PanelContent = new BasePanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PanelContent);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setTitleRenderer(newRenderer());
    updateButtons();
  }

  /**
   * Returns the renderer to use.
   *
   * @return		the renderer
   */
  protected TitleRenderer newRenderer() {
    return new TitleRenderer(this);
  }

  /**
   * Sets whether the pages are readonly or can be manipulated.
   *
   * @param value	true if no manipulation possible
   */
  public void setReadOnly(boolean value) {
    m_ReadOnly = value;
    m_PanelListButtons.setVisible(!m_ReadOnly);
  }

  /**
   * Returns whether the pages are readonly or can be manipulated.
   *
   * @return		true if no manipulation possible
   */
  public boolean isReadOnly() {
    return m_ReadOnly;
  }

  /**
   * Sets the parameters for storing the divider location.
   *
   * @param cls		the class
   * @param property	the property
   */
  @Override
  public void setUISettingsParameters(Class cls, String property) {
    m_SplitPane.setUISettingsParameters(cls, property);
  }

  /**
   * Clears the para meters for storing the divider location.
   */
  @Override
  public void clearUISettingsParameters() {
    m_SplitPane.clearUISettingsParameters();
  }

  /**
   * Sets the location for the divider between page titles and content.
   *
   * @param value	the location in pixels
   */
  public void setDividerLocation(int value) {
    m_SplitPane.setDividerLocation(value);
  }

  /**
   * Returns the current location of the divider between page titles and
   * content.
   *
   * @return		the location in pixels
   */
  public int getDividerLocation() {
    return m_SplitPane.getDividerLocation();
  }

  /**
   * Sets the renderer for the titles.
   *
   * @param renderer	the renderer to use
   */
  public void setTitleRenderer(ListCellRenderer renderer) {
    m_PageList.setCellRenderer(renderer);
  }

  /**
   * Returns the renderer for the titles.
   *
   * @return		the renderer to use
   */
  public ListCellRenderer getTitleRenderer() {
    return m_PageList.getCellRenderer();
  }

  /**
   * Returns the number of pages.
   *
   * @return		the number of pages
   */
  public int getPageCount() {
    return m_PageList.getModel().getSize();
  }

  /**
   * Returns the currently selected page index.
   *
   * @return		the index, -1 if none selected
   */
  public int getSelectedIndex() {
    return m_PageList.getSelectedIndex();
  }

  /**
   * Selects the specified page index.
   *
   * @param index	the index of the page to select
   */
  public void setSelectedIndex(int index) {
    m_PageList.setSelectedIndex(index);
  }

  /**
   * Returns the currently selected page indices.
   *
   * @return		the indices, 0-length array if none selected
   */
  public int[] getSelectedIndices() {
    return m_PageList.getSelectedIndices();
  }

  /**
   * Sets the currently selected page indices.
   *
   * @param indices	the indices
   */
  public void setSelectedIndices(int[] indices) {
    m_PageList.setSelectedIndices(indices);
  }

  /**
   * Selects the specified page component as active page.
   *
   * @param page	the component to select
   */
  public void setSelectedPage(Component page) {
    int		i;

    for (i = 0; i < m_PageListModel.getSize(); i++) {
      if (m_PageListModel.get(i).getPage() == page) {
	setSelectedIndex(i);
	break;
      }
    }
  }

  /**
   * Sets the container at the specified index.
   *
   * @param index	the page index
   * @param cont	the new container
   */
  public void setPageAt(int index, PageContainer cont) {
    m_PageListModel.set(index, cont);
    update();
  }

  /**
   * Returns the page container at the specified index.
   *
   * @param index	the page index
   * @return		the associated page container
   */
  public PageContainer getPageContainerAt(int index) {
    return (PageContainer) m_PageList.getModel().getElementAt(index);
  }

  /**
   * Returns the currently selected page container.
   *
   * @return		the page container, null if none selected
   */
  public PageContainer getSelectedPageContainer() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getPageContainerAt(getSelectedIndex());
  }

  /**
   * Sets the page at the specified index.
   *
   * @param index	the page index
   * @param page	the new page
   */
  public void setPageAt(int index, Component page) {
    getPageContainerAt(index).setPage(page);
    update();
  }

  /**
   * Returns the page component at the specified index.
   *
   * @param index	the page index
   * @return		the associated page component
   */
  public Component getPageAt(int index) {
    return getPageContainerAt(index).getPage();
  }

  /**
   * Returns the detachable page component at the specified index.
   *
   * @param index	the page index
   * @return		the associated detachable page component
   */
  public DetachablePage getDetachablePageAt(int index) {
    return getPageContainerAt(index).getDetachablePage();
  }

  /**
   * Returns the page index for the page component.
   *
   * @param page	the page component to look up
   * @return		the associated page index, -1 if not found
   */
  public int indexOfPage(Component page) {
    int		result;
    int		i;

    result = -1;
    for (i = 0; i < getPageCount(); i++) {
      if (getPageAt(i) == page) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the page index for the page container.
   *
   * @param cont	the page component to look up
   * @return		the associated page index, -1 if not found
   */
  public int indexOfPage(PageContainer cont) {
    int		result;
    int		i;

    result = -1;
    for (i = 0; i < getPageCount(); i++) {
      if (getPageContainerAt(i) == cont) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the currently selected page.
   *
   * @return		the page, null if none selected
   */
  public Component getSelectedPage() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getPageAt(getSelectedIndex());
  }

  /**
   * Returns the currently selected detachable page.
   *
   * @return		the detachable page, null if none selected
   */
  public DetachablePage getSelectedDetachablePage() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getDetachablePageAt(getSelectedIndex());
  }

  /**
   * Sets the title at the specified index.
   *
   * @param index	the page index
   * @param title	the new title
   */
  public void setTitleAt(int index, String title) {
    getPageContainerAt(index).setTitle(title);
    update();
  }

  /**
   * Returns the title at the specified index.
   *
   * @param index	the page index
   * @return		the associated title
   */
  public String getTitleAt(int index) {
    return getPageContainerAt(index).getTitle();
  }

  /**
   * Returns the title of the currently selected page.
   *
   * @return		the title, null if none selected
   */
  public String getSelectedTitle() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getTitleAt(getSelectedIndex());
  }

  /**
   * Sets the icon at the specified index.
   *
   * @param index	the page index
   * @param icon	the new icon
   */
  public void setIconAt(int index, ImageIcon icon) {
    getPageContainerAt(index).setIcon(icon);
    update();
  }

  /**
   * Returns the icon at the specified index.
   *
   * @param index	the page index
   * @return		the associated icon
   */
  public ImageIcon getIconAt(int index) {
    return getPageContainerAt(index).getIcon();
  }

  /**
   * Returns the icon of the currently selected page.
   *
   * @return		the icon, null if none selected
   */
  public ImageIcon getSelectedIcon() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getIconAt(getSelectedIndex());
  }

  /**
   * Approves the closing of the specified page.
   *
   * @param index	the page index
   * @return		true if can be closed
   * @see		#getPageCloseApprover()
   */
  protected boolean isPageClosingApproved(int index) {
    if (m_PageCloseApprover == null)
      return true;
    return m_PageCloseApprover.approvePageClosing(this, index);
  }

  /**
   * Approves the closing of the specified pages.
   *
   * @param indices	the page indices
   * @return		true if can be closed
   * @see		#getPageCloseApprover()
   */
  protected boolean isPageClosingApproved(int[] indices) {
    boolean	result;

    result = true;
    for (int index: indices) {
      result = m_PageCloseApprover.approvePageClosing(this, index);
      if (!result)
        break;
    }

    return result;
  }

  /**
   * Removes the currently selected page container, if approved.
   *
   * @return		the removed container, if any
   */
  public PageContainer checkedRemoveSelectedPage() {
    if (getSelectedIndex() == -1)
      return null;
    if (isPageClosingApproved(getSelectedIndex()))
      return removeSelectedPage();
    else
      return null;
  }

  /**
   * Removes the currently selected page container.
   *
   * @return		the removed container
   */
  public PageContainer removeSelectedPage() {
    if (getSelectedIndex() > -1)
      return removePageAt(getSelectedIndex());
    else
      return null;
  }

  /**
   * Removes the currently selected page containers, if approved.
   */
  public void checkedRemoveSelectedPages() {
    if (getSelectedIndex() == -1)
      return;
    if (isPageClosingApproved(getSelectedIndices()))
      removeSelectedPages();
  }

  /**
   * Removes the currently selected page containers.
   */
  public void removeSelectedPages() {
    int[]	indices;
    int		i;

    indices = getSelectedIndices();
    for (i = indices.length - 1; i >= 0; i--)
      removePageAt(indices[i]);
  }

  /**
   * Removes the page container at the specified index, if approved.
   *
   * @param index	the page index
   * @return		the removed container, null if not removed
   * @see		#m_PageCloseApprover
   */
  public PageContainer checkedRemovePageAt(int index) {
    if (isPageClosingApproved(index))
      return removePageAt(index);
    else
      return null;
  }

  /**
   * Removes the page container at the specified index.
   *
   * @param index	the page index
   * @return		the removed container
   */
  public PageContainer removePageAt(int index) {
    PageContainer	result;

    addPageUndo(index);
    result = m_PageListModel.remove(index);

    // detached?
    if (result.getDetachablePage().isDetached())
      result.getDetachablePage().reattach();

    // clean up?
    if (result.getPage() instanceof CleanUpHandler)
      ((CleanUpHandler) result.getPage()).cleanUp();

    if (index < getPageCount())
      setSelectedIndex(index);
    else if (index > 0)
      setSelectedIndex(index - 1);

    return result;
  }

  /**
   * Removes all pages. Does not stored them in undo.
   */
  public void checkedRemoveAllPages() {
    PageContainer	removed;

    m_IgnoreUpdates = true;
    m_SkipPageUndo  = true;

    while (getPageCount() > 0) {
      removed = checkedRemovePageAt(0);
      if (removed == null)
        break;
    }

    m_IgnoreUpdates = false;
    m_SkipPageUndo  = false;
    update();
  }

  /**
   * Removes all pages. Does not stored them in undo.
   */
  public void removeAllPages() {
    m_IgnoreUpdates = true;
    m_SkipPageUndo  = true;

    while (getPageCount() > 0)
      removePageAt(0);

    m_IgnoreUpdates = false;
    m_SkipPageUndo  = false;
    clearPageUndo();
    update();
  }

  /**
   * Adds the page at the end.
   *
   * @param title	the title
   * @param page	the page component
   */
  public void addPage(String title, Component page) {
    addPage(new PageContainer(title, page));
  }

  /**
   * Adds the page at the end.
   *
   * @param cont	the page container
   */
  public void addPage(PageContainer cont) {
    m_PageListModel.addElement(cont);
    setSelectedIndex(getPageCount() - 1);
  }

  /**
   * Adds the page at the specified index.
   *
   * @param index	the page index to insert the page at
   * @param title	the title
   * @param page	the page component
   */
  public void addPage(int index, String title, Component page) {
    addPage(index, new PageContainer(title, page));
  }

  /**
   * Adds the page at the specified index.
   *
   * @param index	the page index to insert the page at
   * @param cont	the page container
   */
  public void addPage(int index, PageContainer cont) {
    m_PageListModel.add(index, cont);
    setSelectedIndex(index);
  }

  /**
   * moves the selected items up by 1.
   */
  public void moveUp() {
    m_PageList.moveUp();
  }

  /**
   * moves the selected item down by 1.
   */
  public void moveDown() {
    m_PageList.moveDown();
  }

  /**
   * moves the selected items to the top.
   */
  public void moveTop() {
    m_PageList.moveTop();
  }

  /**
   * moves the selected items to the end.
   */
  public void moveBottom() {
    m_PageList.moveBottom();
  }

  /**
   * checks whether the selected items can be moved up.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveUp() {
    return !m_ReadOnly && m_PageList.canMoveUp();
  }

  /**
   * checks whether the selected items can be moved down.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveDown() {
    return !m_ReadOnly && m_PageList.canMoveDown();
  }

  /**
   * Adds the remove items listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addRemoveItemsListener(RemoveItemsListener l) {
    m_PageList.addRemoveItemsListener(l);
  }

  /**
   * Removes the remove items listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeRemoveItemsListener(RemoveItemsListener l) {
    m_PageList.removeRemoveItemsListener(l);
  }

  /**
   * Adds the change listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the change listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies the change listeners.
   */
  protected void notifyChangeListeners() {
    ChangeEvent event;

    event = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(event);
  }

  /**
   * Updates the content panel.
   */
  protected void update() {
    Component	comp;

    if (m_IgnoreUpdates)
      return;

    m_PanelContent.removeAll();

    comp = getSelectedDetachablePage();
    if (comp != null)
      m_PanelContent.add(comp, BorderLayout.CENTER);

    m_PanelContent.invalidate();
    m_PanelContent.revalidate();
    m_PanelContent.repaint();

    m_PageList.repaint();

    updateButtons();
    notifyChangeListeners();
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    int		numSelected;

    numSelected = m_PageList.getSelectedIndices().length;

    m_ButtonUp.setEnabled((numSelected > 0) && canMoveUp());
    m_ButtonDown.setEnabled((numSelected > 0) && canMoveDown());
    m_ButtonRemove.setEnabled(numSelected > 0);
    m_ButtonRemoveAll.setEnabled(getPageCount() > 0);
    m_ButtonUndo.setEnabled(canUndoPageClose());
  }

  /**
   * Adds the action to the action button.
   *
   * @param action	the action to add
   */
  protected void addAction(Action action) {
    if (m_ButtonAction.getAction() == null)
      m_ButtonAction.setAction(action);
    else
      m_ButtonAction.add(action);
    m_ButtonAction.setVisible(true);
  }

  /**
   * Adds the menu item to the action button.
   *
   * @param action	the item to add
   */
  protected void addAction(JMenuItem action) {
    m_ButtonAction.add(action);
    m_ButtonAction.setVisible(true);
  }

  /**
   * Handles the key event from the page list.
   *
   * @param e		the event
   * @return		true if processed
   */
  protected boolean processListKey(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
      if (getSelectedIndex() > -1) {
	checkedRemoveSelectedPages();
	return true;
      }
    }
    return false;
  }

  /**
   * Sets the approver for closing pages.
   *
   * @param value	the approver, null to remove
   */
  public void setPageCloseApprover(PageCloseApprover value) {
    m_PageCloseApprover = value;
  }

  /**
   * Returns the approver for closing pages.
   *
   * @return		the approver, null if none set
   */
  public PageCloseApprover getPageCloseApprover() {
    return m_PageCloseApprover;
  }

  /**
   * Generates the right-click menu for the JList.
   * <br><br>
   * Derived classes should override this method instead of making use
   * of the PopupCustomizer.
   *
   * @param e		the event that triggered the popup
   * @return		the generated menu
   * @see		#showPopup(MouseEvent)
   */
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu		result;
    JMenuItem	  		menuitem;
    final int			index;
    final DetachablePage	detach;
    final String		title;

    result = new BasePopupMenu();
    index  = getSelectedIndex();

    // remove
    menuitem = new JMenuItem("Remove");
    menuitem.setIcon(GUIHelper.getIcon("delete.gif"));
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    menuitem.setEnabled(index > -1);
    menuitem.addActionListener((ActionEvent ae) -> checkedRemoveSelectedPages());
    result.add(menuitem);

    // remove all
    menuitem = new JMenuItem("Remove all");
    menuitem.setIcon(GUIHelper.getIcon("delete_all.gif"));
    menuitem.setEnabled(m_PageListModel.getSize() > 0);
    menuitem.addActionListener((ActionEvent ae) -> checkedRemoveAllPages());
    result.add(menuitem);

    // detach/reattach
    detach = getSelectedDetachablePage();
    if (detach != null) {
      title = getSelectedTitle();
      result.addSeparator();
      if (detach.isDetached()) {
	menuitem = new JMenuItem("Reattach");
	menuitem.setIcon(GUIHelper.getIcon("minimize.png"));
	menuitem.addActionListener((ActionEvent ae) -> detach.reattach());
	result.add(menuitem);
      }
      else {
	menuitem = new JMenuItem("Detach");
	menuitem.setIcon(GUIHelper.getIcon("maximize.png"));
	menuitem.addActionListener((ActionEvent ae) -> {
	  updateTitle(title, detach);
	  detach.detach();
	});
	result.add(menuitem);
      }
    }

    return result;
  }

  /**
   * Hook method for updating the title.
   *
   * @param title	the entry title
   * @param detach	the detachable page
   */
  protected void updateTitle(String title, DetachablePage detach) {
    detach.setFrameTitle(title);
  }

  /**
   * Generates and pops up the right-click menu on the JList.
   *
   * @param e		the event that triggered the popup
   * @see		#createPopup(MouseEvent)
   */
  protected void showPopup(MouseEvent e) {
    BasePopupMenu	menu;

    menu = createPopup(e);

    // customizer available?
    if (m_PopupCustomizer != null)
      m_PopupCustomizer.customizePopup(getSelectedIndex(), menu);

    menu.showAbsolute(this, e);
  }

  /**
   * Sets the popup customizer to use.
   *
   * @param value	the customizer, use null to turn off
   */
  public void setPopupCustomizer(PopupCustomizer value) {
    m_PopupCustomizer = value;
  }

  /**
   * Returns the currently set popup customizer.
   *
   * @return		the customizer, can be null if none set
   */
  public PopupCustomizer getPopupCustomizer() {
    return m_PopupCustomizer;
  }

  /**
   * Sets the tool tip customizer to use.
   *
   * @param value	the customizer, use null to turn off
   */
  public void setToolTipCustomizer(ToolTipCustomizer value) {
    m_ToolTipCustomizer = value;
  }

  /**
   * Returns the currently set tool tip customizer.
   *
   * @return		the customizer, can be null if none set
   */
  public ToolTipCustomizer getToolTipCustomizer() {
    return m_ToolTipCustomizer;
  }

  /**
   * Generates the tool tip for the index.
   *
   * @param index	the index, can be outside range
   * @return		the tool tip, null if none available
   */
  protected String generateToolTip(int index) {
    String result;

    result = null;
    if ((index >= 0) && (index < m_PageListModel.getSize()))
      result = m_PageListModel.getElementAt(index).toString();

    if (m_ToolTipCustomizer != null)
      result = m_ToolTipCustomizer.customizeToolTip(index, result);

    return result;
  }

  /**
   * Sets the maximum pages to keep around for undoing closing.
   *
   * @param value	the maximum, <1 turned off
   */
  public void setMaxPageCloseUndo(int value) {
    m_MaxPageCloseUndo = value;
    m_PanelListButtons.remove(m_ButtonUndo);
    if (m_MaxPageCloseUndo > 0)
      m_PanelListButtons.add(m_ButtonUndo);
  }

  /**
   * Returns the maximum pages to keep around for undoing closing.
   *
   * @return		the maximum, <1 turned off
   */
  public int getMaxPageCloseUndo() {
    return m_MaxPageCloseUndo;
  }

  /**
   * Returns the page undo list.
   *
   * @return		the list
   */
  protected List<PageUndo> getPageUndoList() {
    if (m_PageUndoList == null)
      m_PageUndoList = new ArrayList<>();
    return m_PageUndoList;
  }

  /**
   * Adds the page to its undo list, if enabled.
   *
   * @param index	the position of the tab
   */
  protected void addPageUndo(int index) {
    PageUndo 	undo;

    if ((m_MaxPageCloseUndo < 1) || m_SkipPageUndo)
      return;

    undo              = new PageUndo();
    undo.component    = getPageAt(index);
    undo.title        = getTitleAt(index);
    undo.index        = index;
    undo.icon         = getIconAt(index);
    undo.selected     = (index == getSelectedIndex());

    getPageUndoList().add(undo);

    while (getPageUndoList().size() > m_MaxPageCloseUndo)
      getPageUndoList().remove(0);
  }

  /**
   * Returns whether a tab close can be undone.
   *
   * @return		true if possible
   */
  public boolean canUndoPageClose() {
    return (getPageUndoList().size() > 0);
  }

  /**
   * Performs an undo of a page close.
   *
   * @return		true if successfully restored
   */
  public boolean undoPageClose() {
    PageUndo undo;
    int			size;
    PageContainer	cont;

    size = getPageUndoList().size();
    if (size < 1)
      return false;

    undo = getPageUndoList().get(size - 1);
    getPageUndoList().remove(size - 1);
    cont = new PageContainer(undo.title, undo.component, undo.icon);
    addPage(undo.index, cont);
    if (undo.selected)
      setSelectedIndex(undo.index);

    return true;
  }

  /**
   * Clears the page undo list.
   */
  protected void clearPageUndo() {
    if (m_PageUndoList != null) {
      for (PageUndo undo: m_PageUndoList)
	if (undo.component instanceof CleanUpHandler)
	  ((CleanUpHandler) undo.component).cleanUp();
      m_PageUndoList.clear();
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    removeAllPages();
    clearPageUndo();
    m_PageUndoList = null;
  }
}
