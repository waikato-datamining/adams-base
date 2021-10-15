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
 * Copyright (C) 2016-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.CleanUpHandler;
import adams.core.io.FileUtils;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
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
  protected List<AbstractContentHandler> m_ListContentHandlers;

  /** the button with the favorites. */
  protected BaseButtonWithDropDownMenu m_ButtonFavorites;

  /** the cached previews (content handler -> preview). */
  protected Map<Class,PreviewPanel> m_PreviewCache;

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
    m_PreviewCache                = new HashMap<>();
    m_ReusePreviews               = true;
    m_Favorites                   = new HashMap<>();
    m_CurrentFavorite             = null;
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

    m_ButtonFavorites = new BaseButtonWithDropDownMenu(GUIHelper.getIcon("favorite.gif"));
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
  }

  /**
   * Selects the favorite instead of the selected content handler
   *
   * @param favorite	the favorite instance
   */
  public void selectFavorite(ContentHandlerFavorites.ContentHandlerFavorite favorite) {
    m_CurrentFavorite = favorite;

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
   * Returns the current handler.
   *
   * @return    the handler, null if a favorite was selected
   */
  public AbstractContentHandler getContentHandler() {
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
    AbstractContentHandler				handler;
    ContentHandlerFavorites.ContentHandlerFavorite  	favorite;

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
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.getGOEEditor().setClassType(AbstractContentHandler.class);
    dialog.setCurrent(handler);
    dialog.setLocationRelativeTo(getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    if (index == -1) {
      favorite = new ContentHandlerFavorites.ContentHandlerFavorite(m_CurrentExtension, m_CurrentFavorite.m_Name, (AbstractContentHandler) dialog.getCurrent());
      updateFavorite(m_CurrentExtension, favorite);
    }
    else {
      handler = (AbstractContentHandler) dialog.getCurrent();
      PropertiesManager.setCustomContentHandler(handler);
      m_ListContentHandlers.set(index, handler);
    }
    m_PreviewCache.remove(handler.getClass());

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
    if ((m_LastSearch != null) && (currentSearch != null)) {
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
    PreviewPanel 		result;
    List<Class> 		handlers;
    AbstractContentHandler 	preferred;
    int				i;
    AbstractContentHandler 	contentHandler;
    int 			prefIndex;
    String			ext;

    result = new NoPreviewAvailablePanel();
    ext    = determineExtension(localFiles);
    if (AbstractContentHandler.hasHandler(localFiles[0])) {
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
      for (Class handler: handlers) {
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

      if (contentHandler != null) {
	// cached?
	if (m_ReusePreviews && m_PreviewCache.containsKey(contentHandler.getClass())) {
	  result = m_PreviewCache.get(contentHandler.getClass());
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
	if (m_ReusePreviews)
	  m_PreviewCache.put(contentHandler.getClass(), result);
      }

      SwingUtilities.invokeLater(() -> m_IgnoreContentHandlerChanges = false);
    }

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
    String			ext;
    AbstractContentHandler	handlerObj;
    int				index;
    List<String>		exts;

    if (m_CurrentFiles == null)
      return;

    if (m_ComboBoxContentHandlers.getSelectedIndex() < 0)
      index = 1;
    else
      index = m_ComboBoxContentHandlers.getSelectedIndex();

    handlerObj = m_ListContentHandlers.get(index);

    exts = new ArrayList<>();
    for (File file: m_CurrentFiles) {
      ext = FileUtils.getExtension(file);
      if (ext == null)
	continue;
      ext = ext.toLowerCase();
      exts.add(ext);
    }

    PropertiesManager.updatePreferredContentHandler(exts.toArray(new String[0]), handlerObj);
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
   * Clears the view.
   */
  public void clear() {
    m_PanelView.removeAll();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    for (PreviewPanel panel: m_PreviewCache.values())
      panel.cleanUp();
    m_PreviewCache.clear();
  }
}
