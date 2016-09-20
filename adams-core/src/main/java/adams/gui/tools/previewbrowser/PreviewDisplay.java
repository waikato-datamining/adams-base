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
 * PreviewDisplay.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import adams.gui.core.BasePanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

/**
 * Displays a {@link PreviewPanel} and a combobox to switch views.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreviewDisplay
  extends BasePanel {

  private static final long serialVersionUID = -6515079548026850756L;

  /** the panel for the view. */
  protected BasePanel m_PanelView;

  /** the panel with the content handlers. */
  protected BasePanel m_PanelContentHandlers;

  /** the combobox with the content handlers (if more than one available). */
  protected JComboBox m_ComboBoxContentHandlers;

  /** the model of the combobox. */
  protected DefaultComboBoxModel<String> m_ModelContentHandlers;

  /** whether to ignore selections of the content handler combobox temporarily. */
  protected boolean m_IgnoreContentHandlerChanges;

  /** the currently selected files. */
  protected File[] m_CurrentFiles;

  /** the last search panel that was encountered. */
  protected SearchPanel m_LastSearch;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentFiles = new File[0];
    m_IgnoreContentHandlerChanges = false;
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
    m_ComboBoxContentHandlers = new JComboBox<>(m_ModelContentHandlers);
    m_ComboBoxContentHandlers.addActionListener((ActionEvent e) -> {
      if (m_IgnoreContentHandlerChanges)
	return;
      updatePreferredContentHandler();
      m_LastSearch = null;
      if (m_CurrentFiles != null)
	display(m_CurrentFiles, false);
    });
    m_PanelContentHandlers.add(new JLabel("Preferred handler"));
    m_PanelContentHandlers.add(m_ComboBoxContentHandlers);
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

    if (m_PanelView.getComponentCount() > 0) {
      if (m_PanelView.getComponent(0) instanceof CleanUpHandler)
	((CleanUpHandler) m_PanelView.getComponent(0)).cleanUp();
    }
    m_PanelView.removeAll();
    m_PanelView.add(panel, BorderLayout.CENTER);
    getParent().validate();

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
    JPanel 			result;
    List<Class> 		handlers;
    AbstractContentHandler 	preferred;
    int				i;
    AbstractContentHandler 	contentHandler;
    Class 			cls;
    int 			prefIndex;

    result = new NoPreviewAvailablePanel();
    if (AbstractContentHandler.hasHandler(localFiles[0])) {
      handlers = AbstractContentHandler.getHandlersForFile(localFiles[0]);
      // update combobox
      m_IgnoreContentHandlerChanges = true;
      m_ModelContentHandlers.removeAllElements();
      for (Class handler: handlers)
	m_ModelContentHandlers.addElement(handler.getName());
      m_PanelContentHandlers.setVisible(m_ModelContentHandlers.getSize() > 1);
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
      m_ComboBoxContentHandlers.setSelectedIndex(prefIndex);
      m_IgnoreContentHandlerChanges = false;
      if (prefIndex == -1)
	return null;
      // get preferred handler
      try {
	cls            = Class.forName((String) m_ComboBoxContentHandlers.getSelectedItem());
	contentHandler = (AbstractContentHandler) cls.newInstance();
	if (contentHandler instanceof MultipleFileContentHandler)
	  result = ((MultipleFileContentHandler) contentHandler).getPreview(localFiles);
	else
	  result = contentHandler.getPreview(localFiles[0]);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  LoggingLevel.SEVERE,
	  "Failed to obtain content handler for '" + Utils.arrayToString(localFiles) + "':",
	  e);
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

    if (localFiles.length < 1)
      return;

    m_CurrentFiles = localFiles;

    // notify user
    displayCreatingView();

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
	  super.done();
	}
      };
      worker.execute();
    }
    else {
      displayView(createPreview(localFiles));
    }
  }

  /**
   * Updates the preferred handler.
   */
  protected void updatePreferredContentHandler() {
    String	ext;
    String	handler;
    Properties props;
    String	filename;

    if (m_CurrentFiles == null)
      return;

    props = PropertiesManager.getProperties();
    for (File file: m_CurrentFiles) {
      ext = FileUtils.getExtension(file);
      if (ext == null)
	continue;
      ext = ext.toLowerCase();

      if (m_ComboBoxContentHandlers.getSelectedIndex() < 0)
	handler = (String) m_ComboBoxContentHandlers.getItemAt(0);
      else
	handler = (String) m_ComboBoxContentHandlers.getSelectedItem();

      // update props
      if (handler != null)
	props.setProperty(PropertiesManager.PREFIX_PREFERRED_CONTENT_HANDLER + ext, handler);
    }

    // save props
    filename = Environment.getInstance().getHome() + File.separator + PropertiesManager.FILENAME;
    if (!props.save(filename)) {
      ConsolePanel.getSingleton().append(
	LoggingLevel.SEVERE,
	"Failed to save properties to '" + filename + "'!");
    }
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
   * Clears the view.
   */
  public void clear() {
    m_PanelView.removeAll();
  }
}
