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
 * PreviewDisplay.java
 * Copyright (C) 2016-2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.CleanUpHandler;
import adams.core.LRUCache;
import adams.core.io.FileUtils;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.SearchPanel;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a {@link PreviewPanel} and a combobox to switch views.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PreviewDisplay
  extends BasePanel
  implements CleanUpHandler {

  private static final long serialVersionUID = -6515079548026850756L;

  /** placeholder string when a favorite was selected. */
  public final static String DISPLAY_FAVORITE = "Favorite";

  /** placeholder string when no favorites are available. */
  public final static String DISPLAY_NO_FAVORITES = "[No favorites]";

  /** the panel for the view. */
  protected BasePanel m_PanelView;

  /** the panel with the content handlers. */
  protected BasePanel m_PanelContentHandlers;

  /** the combobox with the content handlers (if more than one available). */
  protected BaseComboBox m_ComboBoxContentHandlers;

  /** the model of the combobox. */
  protected DefaultComboBoxModel<String> m_ModelContentHandlers;

  /** the list of content handler objects (aligned with combobox). */
  protected List<ContentHandler> m_ListContentHandlers;

  /** the button with the favorites. */
  protected BaseButtonWithDropDownMenu m_ButtonFavorites;

  /** the cached previews (content handler -> preview). */
  protected LRUCache<String,PreviewPanel> m_PreviewCache;

  /** the button for displaying the options for the content handler. */
  protected BaseButton m_ButtonContentHandler;

  /** whether to ignore selections of the content handler combobox temporarily. */
  protected boolean m_IgnoreContentHandlerChanges;

  /** the currently selected files. */
  protected File[] m_CurrentFiles;

  /** the current extension. */
  protected String m_CurrentExtension;

  /** the last search panel that was encountered. */
  protected SearchPanel m_LastSearch;

  /** whether a display is currently in progress. */
  protected boolean m_DisplayInProgress;

  /** whether to reuse previews. */
  protected boolean m_ReusePreviews;

  /** the favorites per extension. */
  protected Map<String, ContentHandlerFavorites.ContentHandlerFavorite> m_Favorites;

  /** the currently selected favorite. */
  protected ContentHandlerFavorites.ContentHandlerFavorite m_CurrentFavorite;

  /** whether to fix the handler. */
  protected boolean m_UseFixedContentHandler;

  /** the last content handler. */
  protected ContentHandler m_LastContentHandler;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentFiles                = new File[0];
    m_CurrentExtension            = null;
    m_IgnoreContentHandlerChanges = false;
    m_DisplayInProgress           = false;
    m_ListContentHandlers         = new ArrayList<>();
    m_PreviewCache                = new LRUCache<>(100);
    m_ReusePreviews               = true;
    m_Favorites                   = new HashMap<>();
    m_CurrentFavorite             = null;
    m_UseFixedContentHandler      = false;
    m_LastContentHandler          = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelView = new BasePanel(new BorderLayout());
    add(m_PanelView, BorderLayout.CENTER);

    m_PanelContentHandlers = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelContentHandlers.setVisible(false);
    add(m_PanelContentHandlers, BorderLayout.SOUTH);

    m_ModelContentHandlers    = new DefaultComboBoxModel<>();
    m_ComboBoxContentHandlers = new BaseComboBox<>(m_ModelContentHandlers);
    m_ComboBoxContentHandlers.addActionListener((ActionEvent e) -> {
      if (m_IgnoreContentHandlerChanges)
	return;
      if (m_ComboBoxContentHandlers.getSelectedIndex() > 0)
	m_CurrentFavorite = null;
      updatePreferredContentHandler();
      m_LastSearch = null;
      if (m_CurrentFiles != null)
	display(m_CurrentFiles, false);
    });

    m_ButtonContentHandler = new BaseButton("...");
    m_ButtonContentHandler.addActionListener((ActionEvent e) -> editContentHandler());
    m_ButtonContentHandler.setToolTipText("Open dialog to configure content handler");

    m_ButtonFavorites = new BaseButtonWithDropDownMenu(ImageManager.getIcon("favorite.gif"));
    m_ButtonFavorites.setToolTipText("Select or manage favorite content handlers");

    m_PanelContentHandlers.add(new JLabel("Preferred handler"));
    m_PanelContentHandlers.add(m_ComboBoxContentHandlers);
    m_PanelContentHandlers.add(m_ButtonContentHandler);
    m_PanelContentHandlers.add(m_ButtonFavorites);
  }

  /**
   * Updates the favorite to use.
   *
   * @param ext		the extension this favorite is to be used with
   * @param favorite	the favorite itself
   */
  public void updateFavorite(String ext, ContentHandlerFavorites.ContentHandlerFavorite favorite) {
    m_Favorites.put(ext, favorite);
    m_CurrentFavorite = favorite;
  }

  /**
   * Selects the favorite instead of the selected content handler
   *
   * @param favorite	the favorite instance
   */
  public void selectFavorite(ContentHandlerFavorites.ContentHandlerFavorite favorite) {
    updateFavorite(m_CurrentExtension, favorite);

    // we have favorites available now
    if (m_ModelContentHandlers.getElementAt(0).equalsIgnoreCase(DISPLAY_NO_FAVORITES)) {
      m_ModelContentHandlers.insertElementAt(DISPLAY_FAVORITE, 0);
      m_ModelContentHandlers.removeElementAt(1);
    }

    m_IgnoreContentHandlerChanges = true;
    m_ComboBoxContentHandlers.setSelectedIndex(0);
    m_IgnoreContentHandlerChanges = false;
    display(m_CurrentFiles, true);
  }

  /**
   * Returns the actual content handler in use.
   *
   * @return		the handler in use
   */
  public ContentHandler getActualContentHandler() {
    if (m_CurrentFavorite != null)
      return m_CurrentFavorite.getHandler();
    else
      return getContentHandler();
  }

  /**
   * Returns the current handler.
   *
   * @return    the handler, null if a favorite was selected
   */
  public ContentHandler getContentHandler() {
    int				index;

    if (m_ComboBoxContentHandlers.getSelectedIndex() < 1)
      index = 1;
    else
      index = m_ComboBoxContentHandlers.getSelectedIndex();

    return m_ListContentHandlers.get(index);
  }

  /**
   * Displays GOE for current content handler.
   */
  protected void editContentHandler() {
    GenericObjectEditorDialog				dialog;
    int							index;
    ContentHandler					handler;
    ContentHandlerFavorites.ContentHandlerFavorite  	favorite;
    int							i;

    handler = null;
    index   = -1;
    if (m_ComboBoxContentHandlers.getSelectedIndex() == 0) {
      if (m_CurrentFavorite != null)
	handler = m_CurrentFavorite.getHandler();
    }
    else {
      if (m_ComboBoxContentHandlers.getSelectedIndex() < 1)
	index = 1;
      else
	index = m_ComboBoxContentHandlers.getSelectedIndex();
      handler = m_ListContentHandlers.get(index);
    }
    if (handler == null)
      return;

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setUISettingsPrefix(ContentHandler.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(ContentHandler.class);
    dialog.setCurrent(handler);
    dialog.setLocationRelativeTo(getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    m_PreviewCache.remove(handler.toCommandLine());

    if (index == -1) {
      favorite = new ContentHandlerFavorites.ContentHandlerFavorite(m_CurrentExtension, m_CurrentFavorite.m_Name, (ContentHandler) dialog.getCurrent());
      updateFavorite(m_CurrentExtension, favorite);
    }
    else {
      handler = (ContentHandler) dialog.getCurrent();
      PropertiesManager.setCustomContentHandler(handler);
      index = -1;
      for (i = 0; i < m_ListContentHandlers.size(); i++) {
        if (m_ListContentHandlers.get(i) == null)
          continue;
        if (m_ListContentHandlers.get(i).getClass().equals(handler.getClass())) {
          index = i;
          break;
	}
      }
      if (index > -1) {
	m_ListContentHandlers.set(index, handler);
	m_ComboBoxContentHandlers.setSelectedIndex(index);
      }
    }

    m_LastContentHandler = handler;

    if (m_CurrentFiles != null)
      display(m_CurrentFiles, false);
  }

  /**
   * Displays the given view in the content panel.
   *
   * @param panel	the view to display
   */
  public void displayView(final JPanel panel) {
    final SearchPanel		currentSearch;

    // transfer search
    currentSearch = (SearchPanel) GUIHelper.findFirstComponent(panel, SearchPanel.class, true, true);
    if ((m_LastSearch != null) && !m_LastSearch.getSearchText().isEmpty() && (currentSearch != null)) {
      currentSearch.setSearchText(m_LastSearch.getSearchText());
      currentSearch.setRegularExpression(m_LastSearch.isRegularExpression());
      SwingUtilities.invokeLater(() -> currentSearch.search());
    }

    if ((m_PanelView.getComponentCount() > 0) && (m_PanelView.getComponent(0) != panel))
      m_PanelView.removeAll();

    if (m_PanelView.getComponentCount() == 0) {
      m_PanelView.add(panel, BorderLayout.CENTER);
      getParent().invalidate();
      getParent().validate();
      getParent().doLayout();
      getParent().repaint();
    }

    if (currentSearch != null)
      m_LastSearch = currentSearch;
  }

  /**
   * Displays "Creating view...".
   */
  public void displayCreatingView() {
    displayView(new CreatingPreviewPanel());
  }

  /**
   * Creates a preview for the files.
   *
   * @param localFiles	the files to create the preview for
   * @return		the preview
   */
  protected JPanel createPreview(final File[] localFiles) {
    PreviewPanel 	result;
    List<Class> 	handlers;
    ContentHandler 	preferred;
    int			i;
    ContentHandler 	contentHandler;
    int 		prefIndex;
    String		ext;

    result = null;
    ext    = determineExtension(localFiles);

    if (AbstractContentHandler.hasHandler(localFiles[0])) {
      if (m_UseFixedContentHandler && (m_LastContentHandler != null)) {
	contentHandler = m_LastContentHandler;
      }
      else {
	handlers = AbstractContentHandler.getHandlersForFile(localFiles[0]);
	// update combobox
	m_IgnoreContentHandlerChanges = true;
	m_ModelContentHandlers.removeAllElements();
	m_ListContentHandlers.clear();
	if (m_Favorites.containsKey(ext)) {
	  if (m_CurrentFavorite != null)
	    m_ModelContentHandlers.addElement("[" + DISPLAY_FAVORITE + ": " + m_CurrentFavorite.getName() + "]");
	  else
	    m_ModelContentHandlers.addElement("[" + DISPLAY_FAVORITE + "]");
	}
	else {
	  m_ModelContentHandlers.addElement(DISPLAY_NO_FAVORITES);
	}
	m_ListContentHandlers.add(null);
	for (Class handler : handlers) {
	  m_ModelContentHandlers.addElement(handler.getName());
	  m_ListContentHandlers.add(PropertiesManager.getCustomContentHandler(handler));
	}
	m_PanelContentHandlers.setVisible(m_ModelContentHandlers.getSize() > 1);

	contentHandler = null;
	if (m_CurrentFavorite != null) {
	  if (m_CurrentFavorite.getExtension().equalsIgnoreCase(ext))
	    contentHandler = m_CurrentFavorite.getHandler();
	  else
	    m_CurrentFavorite = null;
	}
	if (contentHandler == null) {
	  // set preferred one
	  preferred = PropertiesManager.getPreferredContentHandler(localFiles[0]);
	  prefIndex = -1;
	  if (preferred != null) {
	    for (i = 0; i < handlers.size(); i++) {
	      if (preferred.getClass() == handlers.get(i)) {
		prefIndex = i;
		break;
	      }
	    }
	  }
	  if ((prefIndex == -1) && (m_ModelContentHandlers.getSize() > 0))
	    prefIndex = 0;
	  if (prefIndex > -1) {
	    m_ComboBoxContentHandlers.setSelectedIndex(prefIndex + 1);
	    // get preferred handler
	    contentHandler = m_ListContentHandlers.get(prefIndex + 1);
	  }
	}
      }

      if (contentHandler != null) {
	m_LastContentHandler = contentHandler;

	// cached?
	if (m_ReusePreviews && m_PreviewCache.contains(contentHandler.toCommandLine())) {
	  result = m_PreviewCache.get(contentHandler.toCommandLine());
	  if (contentHandler instanceof MultipleFileContentHandler)
	    result = ((MultipleFileContentHandler) contentHandler).reusePreview(localFiles, result);
	  else
	    result = contentHandler.reusePreview(localFiles[0], result);
	}
	else {
	  if (contentHandler instanceof MultipleFileContentHandler)
	    result = ((MultipleFileContentHandler) contentHandler).getPreview(localFiles);
	  else
	    result = contentHandler.getPreview(localFiles[0]);
	}
	// cache preview
	if (m_ReusePreviews && !(result instanceof NoPreviewAvailablePanel))
	  m_PreviewCache.put(contentHandler.toCommandLine(), result);
      }

      SwingUtilities.invokeLater(() -> m_IgnoreContentHandlerChanges = false);
    }

    if (result == null)
      result = new NoPreviewAvailablePanel();

    return result;
  }

  /**
   * Returns the current extension.
   *
   * @return		the extension, null if none determined
   */
  public String getCurrentExtension() {
    return m_CurrentExtension;
  }

  /**
   * Determines the extension from the file(s) for the favorites.
   *
   * @param localFiles	the files to inspect
   * @return		the extension, null if failed to determine
   */
  protected String determineExtension(File[] localFiles) {
    String	result;

    result = null;

    if (localFiles != null) {
      for (File localFile: localFiles) {
	result = FileUtils.getExtension(localFile);
	if (result != null)
	  break;
      }
    }

    return result;
  }

  /**
   * Displays the content from the specified files.
   *
   * @param localFiles	the files to display
   * @param wait	wait for worker thread to finish
   */
  public void display(final File[] localFiles, boolean wait) {
    SwingWorker worker;

    if (m_DisplayInProgress)
      return;
    if (localFiles.length < 1)
      return;

    m_CurrentFiles      = localFiles;
    m_DisplayInProgress = true;
    m_CurrentExtension  = determineExtension(localFiles);

    if (!wait) {
      worker = new SwingWorker() {
	JPanel contentPanel;
	@Override
	protected Object doInBackground() throws Exception {
	  contentPanel = createPreview(localFiles);
	  return null;
	}
	@Override
	protected void done() {
	  if (contentPanel != null)
	    displayView(contentPanel);
	  m_DisplayInProgress = false;
	  ContentHandlerFavorites.getSingleton().customizeDropDownButton(m_ButtonFavorites, m_CurrentExtension, PreviewDisplay.this);
	  super.done();
	}
      };
      worker.execute();
    }
    else {
      displayView(createPreview(localFiles));
      ContentHandlerFavorites.getSingleton().customizeDropDownButton(m_ButtonFavorites, m_CurrentExtension, PreviewDisplay.this);
      m_DisplayInProgress = false;
    }
  }

  /**
   * Updates the preferred handler.
   */
  protected void updatePreferredContentHandler() {
    String		ext;
    ContentHandler 	handler;
    int			index;
    List<String>	exts;

    if (m_CurrentFiles == null)
      return;

    if (m_ComboBoxContentHandlers.getSelectedIndex() < 0)
      index = 1;
    else
      index = m_ComboBoxContentHandlers.getSelectedIndex();

    handler = m_ListContentHandlers.get(index);

    exts = new ArrayList<>();
    for (File file: m_CurrentFiles) {
      ext = FileUtils.getExtension(file);
      if (ext == null)
	continue;
      ext = ext.toLowerCase();
      exts.add(ext);
    }

    m_LastContentHandler = handler;

    PropertiesManager.updatePreferredContentHandler(exts.toArray(new String[0]), handler);
  }

  /**
   * Returns the component for sendto.
   *
   * @return		the component
   */
  public JComponent getComponent() {
    if ((m_PanelView.getComponentCount() > 0) && (m_PanelView.getComponent(0) instanceof PreviewPanel))
      return ((PreviewPanel) m_PanelView.getComponent(0)).getContent();
    else
      return m_PanelView;
  }

  /**
   * Sets whether to reuse previews.
   *
   * @param value	true if to reuse
   */
  public void setReusePreviews(boolean value) {
    m_ReusePreviews = value;
    if (!m_ReusePreviews)
      m_PreviewCache.clear();
  }

  /**
   * Returns whether to reuse previews.
   *
   * @return		true if to reuse
   */
  public boolean getReusePreviews() {
    return m_ReusePreviews;
  }

  /**
   * Sets the size of the preview cache.
   *
   * @param value	the size of the cache
   */
  public void setPreviewCacheSize(int value) {
    m_PreviewCache = new LRUCache<>(value);
  }

  /**
   * Returns the size of the preview cache.
   *
   * @return		the size of the cache
   */
  public int getPreviewCacheSize() {
    return m_PreviewCache.size();
  }

  /**
   * Sets whether to use a fixed content handler.
   *
   * @param value	true if to use fixed handler
   */
  public void setUseFixedContentHandler(boolean value) {
    m_UseFixedContentHandler = value;
  }

  /**
   * Returns whether to use a fixed content handler.
   *
   * @return		true if to use fixed handler
   */
  public boolean getUseFixedContentHandler() {
    return m_UseFixedContentHandler;
  }

  /**
   * Clears the view.
   */
  public void clear() {
    m_PanelView.removeAll();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    for (Map.Entry<String,PreviewPanel> entry: m_PreviewCache.getAll())
      entry.getValue().cleanUp();
    m_PreviewCache.clear();
  }
}
