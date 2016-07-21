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
 * TextEditorPanel.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Patrick Chan and Addison Wesley, Java Developers Almanac 2000 (undo/redo)
 */
package adams.gui.core;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.io.FileUtils;
import adams.flow.sink.TextSupplier;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * A panel that allows the editing of text, including undo/redo support.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  copyright = "Patrick Chan and Addision Wesley, Java Developers Almanac 2000",
  license = License.BSD3,
  url = "http://java.sun.com/developer/codesamples/examplets/javax.swing.undo/236.html",
  note = "Undo/redo"
)
public class TextEditorPanel
  extends BasePanel
  implements TextSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 3594108882868668611L;

  /**
   * Specialized JTextArea. Only used to react to setText(...) and append(...)
   * events.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TextEditorArea
    extends BaseTextArea {

    /** for serialization. */
    private static final long serialVersionUID = -7941638500983219814L;

    /** the listeners for setText events. */
    protected HashSet<ChangeListener> m_SetTextListeners;

    /** the listeners for append events. */
    protected HashSet<ChangeListener> m_AppendListeners;

    /**
     * Initializes the text area.
     */
    public TextEditorArea() {
      super();

      m_SetTextListeners = new HashSet<>();
      m_AppendListeners  = new HashSet<>();
    }

    /**
     * Sets the text to display.
     *
     * @param value	the text to display, null clears the display
     */
    @Override
    public void setText(String value) {
      super.setText(value);
      notifySetTextListeners();
    }

    /**
     * Appends the text at the end.
     *
     * @param value	the text to append
     */
    @Override
    public void append(String value) {
      super.append(value);
      notifyAppendListeners();
    }

    /**
     * Adds the listener to the internal list of change listeners for setText
     * events.
     *
     * @param l		the listener to add
     */
    public void addSetTextListener(ChangeListener l) {
      m_SetTextListeners.add(l);
    }

    /**
     * Removes the listener from the internal list of change listeners for setText
     * events.
     *
     * @param l		the listener to remove
     */
    public void removeSetTextListener(ChangeListener l) {
      m_SetTextListeners.remove(l);
    }

    /**
     * Notifies all the setText change listeners.
     */
    protected void notifySetTextListeners() {
      ChangeEvent	e;

      e = new ChangeEvent(this);
      for (ChangeListener l: m_SetTextListeners)
	l.stateChanged(e);
    }

    /**
     * Adds the listener to the internal list of change listeners for append
     * events.
     *
     * @param l		the listener to add
     */
    public void addAppendListener(ChangeListener l) {
      m_AppendListeners.add(l);
    }

    /**
     * Removes the listener from the internal list of change listeners for append
     * events.
     *
     * @param l		the listener to remove
     */
    public void removeAppendListener(ChangeListener l) {
      m_AppendListeners.remove(l);
    }

    /**
     * Notifies all the append change listeners.
     */
    protected void notifyAppendListeners() {
      ChangeEvent	e;

      e = new ChangeEvent(this);
      for (ChangeListener l: m_AppendListeners)
	l.stateChanged(e);
    }
  }

  /** for displaying the text. */
  protected TextEditorArea m_TextArea;

  /** whether the content was modified. */
  protected boolean m_Modified;

  /** whether to ignore changes. */
  protected boolean m_IgnoreChanges;

  /** for saving the content. */
  protected transient TextFileChooser m_FileChooser;

  /** for managing undo/redo. */
  protected UndoManager m_Undo;

  /** the last search string used. */
  protected String m_LastFind;

  /** the listeners for modification events. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** the current file. */
  protected File m_CurrentFile;

  /** the current file encoding. */
  protected String m_CurrentEncoding;

  /** for customizing the popup menu. */
  protected PopupMenuCustomizer<TextEditorPanel> m_PopupMenuCustomizer;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentFile         = null;
    m_CurrentEncoding     = null;
    m_ChangeListeners     = new HashSet<>();
    m_Undo                = new UndoManager();
    m_PopupMenuCustomizer = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    // text
    m_TextArea = new TextEditorArea();
    m_TextArea.setWrapStyleWord(true);
    m_TextArea.addSetTextListener((ChangeEvent e) -> {
      m_TextArea.setCaretPosition(0);
      setModified(false);
    });
    m_TextArea.addAppendListener((ChangeEvent e) -> notifyChangeListeners());
    m_TextArea.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  e.consume();
	  showPopupMenu(e);
	}
	if (!e.isConsumed())
	  super.mouseClicked(e);
      }
    });
    m_TextArea.setFont(Fonts.getMonospacedFont());
    add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);

    // Listen for undo and redo events
    m_TextArea.getDocument().addUndoableEditListener((UndoableEditEvent evt) -> {
      m_Modified = true;
      m_Undo.addEdit(evt.getEdit());
      notifyChangeListeners();
    });

    // Create an undo action and add it to the text component
    m_TextArea.getActionMap().put("Undo", new AbstractAction("Undo") {
      private static final long serialVersionUID = -3023997491519283074L;
      public void actionPerformed(ActionEvent evt) {
	undo();
      }
    });

    // Bind the undo action to ctl-Z
    m_TextArea.getInputMap().put(GUIHelper.getKeyStroke("control Z"), "Undo");

    // Create a redo action and add it to the text component
    m_TextArea.getActionMap().put("Redo", new AbstractAction("Redo") {
      private static final long serialVersionUID = -3579642465298206034L;
      public void actionPerformed(ActionEvent evt) {
	redo();
      }
    });

    // Bind the redo action to ctl-Y
    m_TextArea.getInputMap().put(GUIHelper.getKeyStroke("control Y"), "Redo");

    setSize(600, 800);
  }

  /**
   * Returns the file chooser and creates it if necessary.
   *
   * @return		the file chooser
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
    if (!m_Modified)
      m_Undo.discardAllEdits();
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
    m_CurrentFile     = null;
    m_CurrentEncoding = null;
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
   * Appends the text at the end.
   *
   * @param str		the text to append
   */
  public void append(String str) {
    m_TextArea.append(str);
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
   * Returns the underlying JTextArea element.
   *
   * @return		the component
   */
  public JTextArea getTextArea() {
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
   * Sets the tab size, i.e., the number of maximum width characters.
   *
   * @param value	the number of maximum width chars
   */
  public void setTabSize(int value) {
    m_TextArea.setTabSize(value);
  }

  /**
   * Returns the tab size, i.e., the number of maximum width characters.
   *
   * @return		the number of maximum width chars
   */
  public int getTabSize() {
    return m_TextArea.getTabSize();
  }

  /**
   * Enables/disables line wrap.
   *
   * @param value	if true line wrap gets enabled
   */
  public void setLineWrap(boolean value) {
    m_TextArea.setLineWrap(value);
  }

  /**
   * Returns whether line wrap is enabled.
   *
   * @return		true if line wrap enabled
   */
  public boolean getLineWrap() {
    return m_TextArea.getLineWrap();
  }

  /**
   * Sets the style of wrapping used if the text area is wrapping
   * lines.  If set to true the lines will be wrapped at word
   * boundaries (whitespace) if they are too long
   * to fit within the allocated width.  If set to false,
   * the lines will be wrapped at character boundaries.
   * By default this property is false.
   *
   * @param word indicates if word boundaries should be used
   *   for line wrapping
   * @see #getWrapStyleWord
   */
  public void setWrapStyleWord(boolean word) {
    m_TextArea.setWrapStyleWord(word);
  }

  /**
   * Gets the style of wrapping used if the text area is wrapping
   * lines.  If set to true the lines will be wrapped at word
   * boundaries (ie whitespace) if they are too long
   * to fit within the allocated width.  If set to false,
   * the lines will be wrapped at character boundaries.
   *
   * @return if the wrap style should be word boundaries
   *  instead of character boundaries
   * @see #setWrapStyleWord
   */
  public boolean getWrapStyleWord() {
    return m_TextArea.getWrapStyleWord();
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
   *
   * @return		true if successfully opened
   */
  public boolean open() {
    int		retVal;

    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return false;

    return open(getFileChooser().getSelectedFile(), getFileChooser().getEncoding());
  }

  /**
   * Opens the specified file and loads/displays the content.
   *
   * @param file	the file to load
   * @return		true if successfully opened
   */
  public boolean  open(File file) {
    return open(file, null);
  }

  /**
   * Opens the specified file and loads/displays the content.
   *
   * @param file	the file to load
   * @param encoding	the encoding to use, use null or empty string for default UTF-8
   * @return		true if successfully opened
   */
  public boolean open(File file, String encoding) {
    boolean		result;
    List<String>	content;

    if ((encoding == null) || encoding.isEmpty())
      encoding = "UTF-8";

    content = FileUtils.loadFromFile(file, encoding);
    result  = (content != null);
    if (result) {
      setContent(Utils.flatten(content, "\n"));
      setModified(false);
      m_CurrentFile     = file;
      m_CurrentEncoding = encoding;
    }

    notifyChangeListeners();

    return result;
  }

  /**
   * Pops up dialog to save the content in a file if no filename provided, 
   * otherwise saves the .
   */
  public void save() {
    if (m_CurrentFile == null)
      saveAs();
    else
      save(m_CurrentFile, m_CurrentEncoding);
  }

  /**
   * Pops up dialog to save the content in a file.
   */
  public void saveAs() {
    int		retVal;

    retVal = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    save(getFileChooser().getSelectedFile(), getFileChooser().getEncoding());
  }

  /**
   * Saves the content under the specified file.
   *
   * @param file	the file to save the content in
   * @param encoding	the file encoding to use
   */
  protected void save(File file, String encoding) {
    String	msg;

    msg = FileUtils.writeToFileMsg(file.getAbsolutePath(), m_TextArea.getText(), false, encoding);
    if (msg != null) {
      GUIHelper.showErrorMessage(
	this, "Error saving content to file '" + file + "':\n" + msg);
    }
    else {
      m_CurrentFile     = file;
      m_CurrentEncoding = encoding;
      m_Modified        = false;
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
      return m_Undo.canUndo();
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
      if (m_Undo.canUndo())
	m_Undo.undo();

      // last change undone?
      if (!m_Undo.canUndo())
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
      return m_Undo.canRedo();
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
      if (m_Undo.canRedo()) {
	m_Undo.redo();
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
    return (isEditable() && (m_TextArea.getSelectedText() != null));
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
    return (isEditable() && ClipboardHelper.canPasteStringFromClipboard());
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
    m_TextArea.printText();
  }

  /**
   * Pops up a dialog for selecting the font.
   */
  public void selectFont() {
    m_TextArea.selectFont();
  }

  /**
   * Returns the currently loaded file.
   *
   * @return		the current file, null if none loaded
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * Returns the current file encoding.
   *
   * @return		the current encoding, null if no file loaded
   */
  public String getCurrentEncoding() {
    return m_CurrentEncoding;
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
  protected void notifyChangeListeners() {
    ChangeEvent 	e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Sets the customizer to use.
   *
   * @param value	the customizer, null to unset
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer<TextEditorPanel> value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the customizer in use.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer<TextEditorPanel> getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Shows the popup menu for the text area.
   *
   * @param e		the event that triggered the action
   */
  protected void showPopupMenu(MouseEvent e) {
    BasePopupMenu	menu;
    JMenuItem		menuitem;

    menu = new BasePopupMenu();

    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(m_TextArea.getSelectedText() != null);
    menuitem.addActionListener((ActionEvent ae) -> {
      m_TextArea.requestFocus();
      ClipboardHelper.copyToClipboard(m_TextArea.getSelectedText());
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Select all", GUIHelper.getEmptyIcon());
    menuitem.setEnabled(m_TextArea.getText().length() > 0);
    menuitem.addActionListener((ActionEvent ae) -> {
      m_TextArea.requestFocus();
      m_TextArea.setSelectionStart(0);
      m_TextArea.setSelectionEnd(m_TextArea.getText().length());
    });
    menu.add(menuitem);

    menuitem = new JCheckBoxMenuItem("Line wrap", GUIHelper.getEmptyIcon());
    menuitem.setSelected(getLineWrap());
    menuitem.addActionListener((ActionEvent ae) -> setLineWrap(!getLineWrap()));
    menu.addSeparator();
    menu.add(menuitem);

    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, menu);

    menu.showAbsolute(m_TextArea, e);
  }

  /**
   * Returns a custom file filter for the file chooser.
   *
   * @return		the file filter, null if to use default one
   */
  @Override
  public ExtensionFileFilter getCustomTextFileFilter() {
    return null;
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  @Override
  public String supplyText() {
    return getContent();
  }

  /**
   * Sets the text of this <code>TextComponent</code>
   * to the specified text.  If the text is <code>null</code>
   * or empty, has the effect of simply deleting the old text.
   * When text has been inserted, the resulting caret location
   * is determined by the implementation of the caret class.
   *
   * <p>
   * Note that text is not a bound property, so no <code>PropertyChangeEvent
   * </code> is fired when it changes. To listen for changes to the text,
   * use <code>DocumentListener</code>.
   *
   * @param t the new text to be set
   * @see #getText
   */
  public void setText(String t) {
    m_TextArea.setText(t);
  }

  /**
   * Returns the text contained in this <code>TextComponent</code>.
   * If the underlying document is <code>null</code>,
   * will give a <code>NullPointerException</code>.
   *
   * Note that text is not a bound property, so no <code>PropertyChangeEvent
   * </code> is fired when it changes. To listen for changes to the text,
   * use <code>DocumentListener</code>.
   *
   * @return the text
   * @throws NullPointerException if the document is <code>null</code>
   * @see #setText
   */
  public String getText() {
    return m_TextArea.getText();
  }

  /**
   * Returns the selected text contained in this
   * <code>TextComponent</code>.  If the selection is
   * <code>null</code> or the document empty, returns <code>null</code>.
   *
   * @return the text
   * @throws IllegalArgumentException if the selection doesn't
   *  have a valid mapping into the document for some reason
   */
  public String getSelectedText() {
    return m_TextArea.getSelectedText();
  }

  /**
   * Returns the selected text's start position.  Return 0 for an
   * empty document, or the value of dot if no selection.
   *
   * @return the start position &ge; 0
   */
  @Transient
  public int getSelectionStart() {
    return m_TextArea.getSelectionStart();
  }

  /**
   * Sets the selection start to the specified position.  The new
   * starting point is constrained to be before or at the current
   * selection end.
   * <p>
   * This is available for backward compatibility to code
   * that called this method on <code>java.awt.TextComponent</code>.
   * This is implemented to forward to the <code>Caret</code>
   * implementation which is where the actual selection is maintained.
   *
   * @param selectionStart the start position of the text &ge; 0
   */
  public void setSelectionStart(int selectionStart) {
    m_TextArea.setSelectionStart(selectionStart);
  }

  /**
   * Returns the selected text's end position.  Return 0 if the document
   * is empty, or the value of dot if there is no selection.
   *
   * @return the end position &ge; 0
   */
  @Transient
  public int getSelectionEnd() {
    return m_TextArea.getSelectionEnd();
  }

  /**
   * Sets the selection end to the specified position.  The new
   * end point is constrained to be at or after the current
   * selection start.
   * <p>
   * This is available for backward compatibility to code
   * that called this method on <code>java.awt.TextComponent</code>.
   * This is implemented to forward to the <code>Caret</code>
   * implementation which is where the actual selection is maintained.
   *
   * @param selectionEnd the end position of the text &ge; 0
   */
  public void setSelectionEnd(int selectionEnd) {
    m_TextArea.setSelectionEnd(selectionEnd);
  }

  /**
   * Fetches the caret that allows text-oriented navigation over
   * the view.
   *
   * @return the caret
   */
  @Transient
  public Caret getCaret() {
    return m_TextArea.getCaret();
  }

  /**
   * Sets the caret to be used.  By default this will be set
   * by the UI that gets installed.  This can be changed to
   * a custom caret if desired.  Setting the caret results in a
   * PropertyChange event ("caret") being fired.
   *
   * @param c the caret
   * @see #getCaret
   */
  public void setCaret(Caret c) {
    m_TextArea.setCaret(c);
  }
}
