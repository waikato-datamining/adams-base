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
 * JComponentWriterFileChooser.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.print;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import adams.core.io.PlaceholderFile;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.ImagePreview;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * A specialized JFileChooser that lists all available file Writers
 * for screenshots.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JComponentWriterFileChooser
  extends BaseFileChooser {

  /** for serialization. */
  private static final long serialVersionUID = -7671035763970202570L;

  /** unhandled type of dialog. */
  public final static int UNHANDLED_DIALOG = 0;

  /** the writer dialog. */
  public final static int WRITER_DIALOG = 2;

  /** the file filters for the writers. */
  protected static List<JComponentWriterFileFilter> m_WriterFileFilters;

  /** the type of dialog to display. */
  protected int m_DialogType;

  /** the converter that was chosen by the user. */
  protected Object m_CurrentConverter;

  /** the propertychangelistener. */
  protected PropertyChangeListener m_Listener;

  /** the last filter that was used for opening/saving. */
  protected FileFilter m_LastFilter;

  /** the preferred filter. */
  protected JComponentWriter m_PreferredWriter;

  /** the checkbox for bringing up the GenericObjectEditor. */
  protected JCheckBox m_CheckBoxOptions;

  /** the image preview. */
  protected ImagePreview m_ImagePreview;

  /** the GOE for displaying the options of a reader/writer. */
  protected GenericObjectEditor m_Editor;

  /**
   * onstructs a FileChooser pointing to the user's default directory.
   */
  public JComponentWriterFileChooser() {
    super();
    initialize();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public JComponentWriterFileChooser(File currentDirectory) {
    super(currentDirectory);
    initialize();
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public JComponentWriterFileChooser(String currentDirectory) {
    super(currentDirectory);
    initialize();
  }

  /**
   * Further initializations.
   */
  @Override
  protected void initialize() {
    super.initialize();

    initFilters();

    super.setAcceptAllFileFilterUsed(false);

    setAutoAppendExtension(true);
    setPromptOverwriteFile(true);

    m_PreferredWriter = JComponentWriter.forCommandLine(GUIHelper.getString("PreferredJComponentWriter", new PNGWriter().toCommandLine()));
    m_Editor          = new GenericObjectEditor(false);
  }

  /**
   * Creates an accessory panel displayed next to the files.
   * 
   * @return		the panel or null if none available
   */
  @Override
  protected JComponent createAccessoryPanel() {
    JPanel	result;
    JPanel	panel;
    Dimension	dim;

    super.createAccessoryPanel();
    
    m_CheckBoxOptions = new JCheckBox("Edit options");
    m_CheckBoxOptions.setMnemonic('E');
    result = new JPanel(new BorderLayout());
    result.add(m_CheckBoxOptions, BorderLayout.NORTH);
    
    panel = new JPanel(new BorderLayout());
    result.add(panel, BorderLayout.CENTER);

    m_ImagePreview = new ImagePreview(this);
    panel.add(m_PanelBookmarks, BorderLayout.CENTER);
    panel.add(m_ImagePreview, BorderLayout.SOUTH);
    
    dim = getDefaultAccessoryDimension();
    if (dim != null) {
      result.setSize(dim);
      result.setMinimumSize(dim);
      result.setPreferredSize(dim);
    }

    return result;
  }

  /**
   * initializes the JComponentWriterFileFilters.
   */
  protected synchronized static void initFilters() {
    JComponentWriterFileFilter[]	list;
    int					i;

    if (m_WriterFileFilters == null) {
      list                = JComponentWriter.getFileFilters();
      m_WriterFileFilters = new ArrayList<JComponentWriterFileFilter>();
      for (i = 0; i < list.length; i++)
	m_WriterFileFilters.add(list[i]);
    }
  }

  /**
   * initializes the GUI.
   *
   * @param dialogType		the type of dialog to setup the GUI for
   */
  protected void initGUI(int dialogType) {
    List<JComponentWriterFileFilter>	list;
    int					i;
    JComponentWriterFileFilter		preferred;
    FileFilter		active;

    // setup filters
    resetChoosableFileFilters();
    list      = m_WriterFileFilters;
    preferred = null;
    for (i = 0; i < list.size(); i++) {
      addChoosableFileFilter(list.get(i));
      if (m_PreferredWriter != null) {
	if (list.get(i).getWriter().getClass().equals(m_PreferredWriter.getClass())) {
	  preferred = list.get(i);
	}
      }
    }

    // select current one
    if (list.size() > 0) {
      active = list.get(0);
      if (preferred != null)
	active = preferred;
      if (m_LastFilter != null)
	active = m_LastFilter;
      setFileFilter(active);
    }

    // listener
    if (m_Listener != null)
      removePropertyChangeListener(m_Listener);
    m_Listener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
	// filter changed
	if (evt.getPropertyName().equals(FILE_FILTER_CHANGED_PROPERTY)) {
	  updateCurrentConverter();
	}
      }
    };
    addPropertyChangeListener(m_Listener);

    // initial setup
    if (dialogType == WRITER_DIALOG) {
      m_Editor.setClassType(JComponentWriter.class);
      m_Editor.setValue(list.get(0).getWriter());
    }

    updateCurrentConverter();
  }

  /**
   * Pops a custom file chooser dialog with a custom approve button. Throws
   * an exception, if the dialog type is UNHANDLED_DIALOG.
   *
   * @param parent		the parent of this dialog
   * @param approveButtonText	the text for the OK button
   * @return			the user's action
   */
  @Override
  public int showDialog(Component parent, String approveButtonText) {
    if (m_DialogType == UNHANDLED_DIALOG)
      throw new IllegalStateException("Either use showOpenDialog or showSaveDialog!");
    else
      return super.showDialog(parent, approveButtonText);
  }

  /**
   * Throws an exception.
   *
   * @param parent		the parent of this file chooser
   * @return			throws an exception
   */
  @Override
  public int showOpenDialog(Component parent) {
    throw new IllegalStateException("Cannot load images!");
  }

  /**
   * Pops up an "Save File" file chooser dialog.
   *
   * @param parent		the parent of this file chooser
   * @return			the result of the user's action
   */
  @Override
  public int showSaveDialog(Component parent) {
    m_DialogType       = WRITER_DIALOG;
    m_CurrentConverter = null;

    initGUI(WRITER_DIALOG);

    // using "setAcceptAllFileFilterUsed" messes up the currently selected
    // file filter/file, hence backup/restore of currently selected
    // file filter/file
    FileFilter currentFilter = getFileFilter();
    File currentFile = getSelectedFile();
    setAcceptAllFileFilterUsed(false);
    setFileFilter(currentFilter);
    setSelectedFile(currentFile);

    int result = super.showSaveDialog(parent);

    // do we have to add the extension?
    if (result == APPROVE_OPTION) {
      if (getFileFilter() instanceof JComponentWriterFileFilter) {
	String filename = getSelectedFile().getAbsolutePath();
	String[] extensions = ((JComponentWriterFileFilter) getFileFilter()).getExtensions();
	if (!filename.endsWith(extensions[0])) {
	  filename += extensions[0];
	  setSelectedFile(new File(filename));
	}
      }
    }

    // using "setAcceptAllFileFilterUsed" messes up the currently selected
    // file filter/file, hence backup/restore of currently selected
    // file filter/file
    currentFilter = getFileFilter();
    currentFile = getSelectedFile();
    setFileFilter(currentFilter);
    setSelectedFile(currentFile);

    m_DialogType = UNHANDLED_DIALOG;
    removePropertyChangeListener(m_Listener);

    if (result == APPROVE_OPTION) {
      m_LastFilter = getFileFilter();
      configureCurrentConverter(WRITER_DIALOG);

      // bring up options dialog?
      if (m_CheckBoxOptions.isSelected()) {
	m_Editor.setValue(m_CurrentConverter);
	GenericObjectEditorDialog dialog = GenericObjectEditorDialog.createDialog(this, m_Editor);
	dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
	dialog.setVisible(true);
	result = dialog.getResultType();
	if (result == APPROVE_OPTION)
	  m_CurrentConverter = m_Editor.getValue();
      }
    }

    return result;
  }

  /**
   * returns the writer that was chosen by the user, can be null in case the
   * user aborted the dialog or the open dialog was shown.
   *
   * @return		the chosen writer, if any
   */
  public JComponentWriter getWriter() {
    configureCurrentConverter(WRITER_DIALOG);
    return (JComponentWriter) m_CurrentConverter;
  }

  /**
   * sets the current converter according to the current filefilter.
   */
  protected void updateCurrentConverter() {
    Object	newConverter;

    if (getFileFilter() == null)
      return;

    try {
      // determine current converter
      newConverter = ((JComponentWriterFileFilter) getFileFilter()).getWriter();

      if (m_CurrentConverter == null) {
	m_CurrentConverter = newConverter;
      }
      else {
	if (!m_CurrentConverter.getClass().equals(newConverter.getClass()))
	  m_CurrentConverter = newConverter;
      }
    }
    catch (Exception e) {
      m_CurrentConverter = null;
      e.printStackTrace();
    }
  }

  /**
   * configures the current converter.
   *
   * @param dialogType		the type of dialog to configure for
   */
  protected void configureCurrentConverter(int dialogType) {
    PlaceholderFile	selFile;
    File		currFile;

    if (getSelectedPlaceholderFile() == null)
      return;

    selFile = getSelectedPlaceholderFile();

    if (m_CurrentConverter == null) {
      m_CurrentConverter = ((JComponentWriterFileFilter) getFileFilter()).getWriter();

      // none found?
      if (m_CurrentConverter == null)
	return;
    }

    try {
      currFile = ((JComponentWriter) m_CurrentConverter).getFile();
      if ((currFile == null) || (!currFile.getAbsolutePath().equals(selFile.getAbsolutePath())))
	((JComponentWriter) m_CurrentConverter).setFile(selFile);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Is always using false, since we can't determine the reader/writer based
   * on the extension.
   *
   * @param b		ignored
   */
  @Override
  public void setAcceptAllFileFilterUsed(boolean b) {
    super.setAcceptAllFileFilterUsed(false);
  }
}
