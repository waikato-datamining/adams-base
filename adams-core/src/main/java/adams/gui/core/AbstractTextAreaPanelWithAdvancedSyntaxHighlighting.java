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
 * AbstractTextAreaPanelWithAdvancedSyntaxHighlighting.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.io.File;
import java.util.HashSet;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;

import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.FontChooser;
import adams.gui.chooser.TextFileChooser;

/**
 * Panel with syntax highlighting using {@link RSyntaxTextArea}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTextAreaPanelWithAdvancedSyntaxHighlighting
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -6446220610454192959L;
  
  /** the text area. */
  protected RSyntaxTextArea m_TextArea;

  /** the scroll pane. */
  protected RTextScrollPane m_ScrollPane;
  
  /** whether the content was modified. */
  protected boolean m_Modified;

  /** whether to ignore changes. */
  protected boolean m_IgnoreChanges;

  /** for saving the content. */
  protected transient TextFileChooser m_FileChooser;

  /** the last search string used. */
  protected String m_LastFind;

  /** the listeners for modification events. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** the current file. */
  protected File m_CurrentFile;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentFile     = null;
    m_ChangeListeners = new HashSet<ChangeListener>();
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TextArea   = newTextArea();
    m_ScrollPane = newScrollPane(m_TextArea);
    add(m_ScrollPane, BorderLayout.CENTER);

    // listen for text changes
    m_TextArea.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
	m_Modified = true;
      }
      public void insertUpdate(DocumentEvent e) {
	m_Modified = true;
      }
      public void changedUpdate(DocumentEvent e) {
	m_Modified = true;
      }
    });

    // Listen for undo and redo events
    m_TextArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent evt) {
	m_Modified = true;
	notifyChangeListeners();
      }
    });

    setSize(600, 800);
  }
  
  /**
   * Returns the syntax style to use.
   * 
   * @return		style
   * @see		RSyntaxTextArea
   */
  protected abstract String getSyntaxStyle();
  
  /**
   * Creates a new {@link RSyntaxTextArea} instance.
   * 
   * @return		the text area
   */
  protected RSyntaxTextArea newTextArea() {
    RSyntaxTextArea	result;
    
    result = new RSyntaxTextArea();
    result.setLineWrap(false);
    result.setAutoIndentEnabled(true);
    result.setAntiAliasingEnabled(true);
    result.setCodeFoldingEnabled(true);
    result.setBracketMatchingEnabled(false);
    result.setSyntaxEditingStyle(getSyntaxStyle());
    
    return result;
  }
  
  /**
   * Creates a new {@link RSyntaxTextArea} instance.
   * 
   * @return		the text area
   */
  protected RTextScrollPane newScrollPane(RSyntaxTextArea textArea) {
    RTextScrollPane	result;
    
    result = new RTextScrollPane(textArea);
    result.setLineNumbersEnabled(true);
    result.setFoldIndicatorEnabled(true);
    
    return result;
  }

  /**
   * Returns the file chooser and creates it if necessary.
   */
  protected TextFileChooser getFileChooser() {
    if (m_FileChooser == null)
      m_FileChooser = new TextFileChooser();
    
    return m_FileChooser;
  }

  /**
   * Sets the modified state. If false, all edits are discarded and the
   * last search string reset as well.
   *
   * @param value 	if true then the content is flagged as modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
    notifyChangeListeners();
  }

  /**
   * Returns whether the content has been modified.
   *
   * @return		true if the content was modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Sets the content to display. Resets the modified state.
   *
   * @param value	the text
   */
  public void setContent(String value) {
    m_TextArea.setText(value);
  }

  /**
   * Returns the content to display.
   *
   * @return		the text
   */
  public String getContent() {
    return m_TextArea.getText();
  }

  /**
   * Sets whether the text area is editable or not.
   *
   * @param value	if true then the text will be editable
   */
  public void setEditable(boolean value) {
    m_TextArea.setEditable(value);
  }

  /**
   * Returns whether the text area is editable or not.
   *
   * @return		true if the text is editable
   */
  public boolean isEditable() {
    return m_TextArea.isEditable();
  }

  /**
   * Sets the font of the text area.
   *
   * @param value	the font to use
   */
  public void setTextFont(Font value) {
    m_TextArea.setFont(value);
  }

  /**
   * Returns the font currently in use by the text area.
   *
   * @return		the font in use
   */
  public Font getTextFont() {
    return m_TextArea.getFont();
  }

  /**
   * Returns the last search string.
   *
   * @return		the last search string, can be null if no search
   * 			performed yet
   */
  public String getLastFind() {
    return m_LastFind;
  }

  /**
   * Returns the underlying RSyntaxTextArea element.
   *
   * @return		the component
   */
  public RSyntaxTextArea getTextArea() {
    return m_TextArea;
  }

  /**
   * Returns the underlying document of the text area.
   *
   * @return		the document
   */
  public Document getDocument() {
    return m_TextArea.getDocument();
  }

  /**
   * Sets the position of the cursor.
   *
   * @param value	the position
   */
  public void setCaretPosition(int value) {
    m_TextArea.setCaretPosition(value);
  }

  /**
   * Returns the current position of the cursor.
   *
   * @return		the cursor position
   */
  public int getCaretPosition() {
    return m_TextArea.getCaretPosition();
  }

  /**
   * Sets the linewrap state.
   *
   * @param value	whether to wrap or not
   */
  public void setLineWrap(boolean value) {
    m_TextArea.setLineWrap(value);
  }

  /**
   * Returns the linewrap status.
   *
   * @return		true if linewrap is on
   */
  public boolean getLineWrap() {
    return m_TextArea.getLineWrap();
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the content or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  public boolean checkForModified() {
    boolean 	result;
    int		retVal;
    String	msg;

    result = !isModified();

    if (!result) {
      msg    = "Content not saved - save?";
      retVal = GUIHelper.showConfirmMessage(this, msg, "Content not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  saveAs();
	  result = !isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Pops up dialog to open a file.
   */
  public void open() {
    int		retVal;

    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    open(getFileChooser().getSelectedFile(), getFileChooser().getEncoding());
  }

  /**
   * Opens the specified file and loads/displays the content, using UTF-8.
   *
   * @param file	the file to load
   */
  public void open(File file) {
    open(file, null);
  }

  /**
   * Opens the specified file and loads/displays the content.
   *
   * @param file	the file to load
   * @param encoding	the encoding to use, use null or empty string for default UTF-8
   */
  public void open(File file, String encoding) {
    List<String>	content;

    if ((encoding == null) || encoding.isEmpty())
      encoding = "UTF-8";
    
    content = FileUtils.loadFromFile(file, encoding);
    setContent(Utils.flatten(content, "\n"));
    setModified(false);

    m_CurrentFile = file;

    notifyChangeListeners();
  }

  /**
   * Pops up dialog to save the content in a file.
   */
  public void save() {
    if (m_CurrentFile == null)
      saveAs();
    else
      save(m_CurrentFile);
  }

  /**
   * Pops up dialog to save the content in a file.
   */
  public void saveAs() {
    int		retVal;

    retVal = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    save(getFileChooser().getSelectedFile());
  }

  /**
   * Saves the content under the specified file.
   *
   * @param file	the file to save the content int
   */
  protected void save(File file) {
    if (!FileUtils.writeToFile(file.getAbsolutePath(), m_TextArea.getText(), false)) {
      GUIHelper.showErrorMessage(
	  this, "Error saving content to file '" + file + "'!");
    }
    else {
      m_CurrentFile = file;
      m_Modified    = false;
    }

    notifyChangeListeners();
  }

  /**
   * Removes all content. Does not reset the undos.
   */
  public void clear() {
    try {
      m_TextArea.getDocument().remove(0, m_TextArea.getDocument().getLength());
    }
    catch (Exception e) {
      // ignored
    }
    m_Modified = false;
    notifyChangeListeners();
  }

  /**
   * Checks whether an undo action is available.
   *
   * @return		true if an undo action is available
   */
  public boolean canUndo() {
    try {
      return m_TextArea.canUndo();
    }
    catch (Exception ex) {
      return false;
    }
  }

  /**
   * Performs an undo, if possible.
   */
  public void undo() {
    try {
      // perform undo
      if (m_TextArea.canUndo())
	m_TextArea.undoLastAction();

      // last change undone?
      if (!m_TextArea.canUndo())
	m_Modified = false;

      notifyChangeListeners();
    }
    catch (Exception ex) {
      // ignored
    }
  }

  /**
   * Checks whether a redo action is available.
   *
   * @return		true if a redo action is available
   */
  public boolean canRedo() {
    try {
      return m_TextArea.canRedo();
    }
    catch (Exception ex) {
      return false;
    }
  }

  /**
   * Performs a redo, if possible.
   */
  public void redo() {
    try {
      if (m_TextArea.canRedo()) {
	m_TextArea.redoLastAction();
	m_Modified = true;
	notifyChangeListeners();
      }
    }
    catch (Exception ex) {
      // ignored
    }
  }

  /**
   * Checks whether text can be cut at the moment.
   *
   * @return		true if text is available for cutting
   */
  public boolean canCut() {
    if (isEditable() && (m_TextArea.getSelectedText() != null))
      return true;
    else
      return false;
  }

  /**
   * Cuts the currently selected text and places it on the clipboard.
   */
  public void cut() {
    m_TextArea.cut();
    notifyChangeListeners();
  }

  /**
   * Checks whether text can be copied at the moment.
   *
   * @return		true if text is available for copying
   */
  public boolean canCopy() {
    return (m_TextArea.getSelectedText() != null);
  }

  /**
   * Copies the currently selected text to the clipboard.
   */
  public void copy() {
    if (m_TextArea.getSelectedText() == null)
      ClipboardHelper.copyToClipboard(m_TextArea.getText());
    else
      m_TextArea.copy();
  }

  /**
   * Checks whether text can be pasted at the moment.
   *
   * @return		true if text is available for pasting
   */
  public boolean canPaste() {
    if (isEditable() && ClipboardHelper.canPasteStringFromClipboard())
      return true;
    else
      return false;
  }

  /**
   * Pastes the text from the clipboard into the document.
   */
  public void paste() {
    m_TextArea.paste();
    notifyChangeListeners();
  }

  /**
   * Selects all the text.
   */
  public void selectAll() {
    m_TextArea.selectAll();
  }

  /**
   * Initiates a search.
   */
  public void find() {
    String	search;
    int		index;

    search = GUIHelper.showInputDialog(GUIHelper.getParentComponent(this), "Enter search string", m_LastFind);
    if (search == null)
      return;

    index = m_TextArea.getText().indexOf(search, m_TextArea.getCaretPosition());
    if (index > -1) {
      m_LastFind = search;
      m_TextArea.setCaretPosition(index + search.length());
      m_TextArea.setSelectionStart(index);
      m_TextArea.setSelectionEnd(index + search.length());
    }
    else {
      GUIHelper.showErrorMessage(this, "Search string '" + search + "' not found!");
    }

    notifyChangeListeners();
  }

  /**
   * Finds the next occurrence.
   */
  public void findNext() {
    int		index;

    index = m_TextArea.getText().indexOf(m_LastFind, m_TextArea.getCaretPosition());
    if (index > -1) {
      m_TextArea.setCaretPosition(index + m_LastFind.length());
      m_TextArea.setSelectionStart(index);
      m_TextArea.setSelectionEnd(index + m_LastFind.length());
    }
    else {
      GUIHelper.showErrorMessage(this, "Search string '" + m_LastFind + "' not found!");
    }

    notifyChangeListeners();
  }

  /**
   * Pops up a print dialog.
   */
  public void printText() {
    String 	msg;

    try {
      m_TextArea.print(null, null, true, null, null, true);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      msg = "Failed to print:\n" + ex;
      if (getParentDialog() != null)
	GUIHelper.showErrorMessage(getParentDialog(), msg);
      else
	GUIHelper.showErrorMessage(getParentFrame(), msg);
    }
  }

  /**
   * Pops up a dialog for selecting the font.
   */
  public void selectFont() {
    FontChooser 	chooser;

    if (getParentDialog() != null)
      chooser = new FontChooser(getParentDialog());
    else
      chooser = new FontChooser(getParentFrame());
    chooser.setCurrent(getFont());
    chooser.setModalityType(ModalityType.DOCUMENT_MODAL);
    chooser.setVisible(true);
    setTextFont(chooser.getCurrent());
  }

  /**
   * Adds the given change listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the given change listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Sends an event to all change listeners.
   */
  protected synchronized void notifyChangeListeners() {
    ChangeEvent 	e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }
}
