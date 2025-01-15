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
 * BaseTextArea.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.logging.LoggingHelper;
import adams.event.AnyChangeListenerSupporter;
import adams.gui.chooser.FontChooser;

import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.Frame;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * A customized JTextArea. Adds functionality for printing and selecting fonts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseTextArea
  extends JTextArea
  implements AppendableTextAreaComponent, AnyChangeListenerSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7970608693979989912L;

  /** the listeners for any changes to the text. */
  protected Set<ChangeListener> m_AnyChangeListeners;

  /**
   * Constructs a new TextArea.  A default model is set, the initial string
   * is null, and rows/columns are set to 0.
   */
  public BaseTextArea() {
    super();
    initialize();
  }

  /**
   * Constructs a new TextArea with the specified text displayed.
   * A default model is created and rows/columns are set to 0.
   *
   * @param text the text to be displayed, or null
   */
  public BaseTextArea(String text) {
    super(text);
    initialize();
  }

  /**
   * Constructs a new empty TextArea with the specified number of
   * rows and columns.  A default model is created, and the initial
   * string is null.
   *
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public BaseTextArea(int rows, int columns) {
    super(rows, columns);
    initialize();
  }

  /**
   * Constructs a new TextArea with the specified text and number
   * of rows and columns.  A default model is created.
   *
   * @param text the text to be displayed, or null
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public BaseTextArea(String text, int rows, int columns) {
    super(text, rows, columns);
    initialize();
  }

  /**
   * Constructs a new BaseTextArea with the given document model, and defaults
   * for all of the other arguments (null, 0, 0).
   *
   * @param doc  the model to use
   */
  public BaseTextArea(Document doc) {
    super(doc);
    initialize();
  }

  /**
   * Constructs a new BaseTextArea with the specified number of rows
   * and columns, and the given model.  All of the constructors
   * feed through this constructor.
   *
   * @param doc the model to use, or create a default one if null
   * @param text the text to be displayed, null if none
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public BaseTextArea(Document doc, String text, int rows, int columns) {
    super(doc, text, rows, columns);
    initialize();
  }

  /**
   * Initializes the text area.
   */
  protected void initialize() {
    m_AnyChangeListeners = new HashSet<>();
    getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	notifyAnyChangeListeners();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	notifyAnyChangeListeners();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	notifyAnyChangeListeners();
      }
    });
  }

  /**
   * Tries to determine the frame the container is part of.
   *
   * @return		the parent frame if one exists or null if not
   */
  public Frame getParentFrame() {
    return GUIHelper.getParentFrame(this);
  }

  /**
   * Tries to determine the dialog this panel is part of.
   *
   * @return		the parent dialog if one exists or null if not
   */
  public Dialog getParentDialog() {
    return GUIHelper.getParentDialog(this);
  }

  /**
   * Tries to determine the internal frame this panel is part of.
   *
   * @return		the parent internal frame if one exists or null if not
   */
  public JInternalFrame getParentInternalFrame() {
    return GUIHelper.getParentInternalFrame(this);
  }

  /**
   * Pops up a print dialog.
   */
  public void printText() {
    String 	msg;

    try {
      print(null, null, true, null, null, true);
    }
    catch (Exception ex) {
      msg = "Failed to print:" + ex;
      LoggingHelper.global().log(Level.SEVERE, msg, ex);
      if (getParentDialog() != null)
	GUIHelper.showErrorMessage(getParentDialog(), msg, ex, "Error");
      else
	GUIHelper.showErrorMessage(getParentFrame(), msg, ex, "Error");
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
    setFont(chooser.getCurrent());
  }
  
  /**
   * Returns the number of lines in the document (= # of elements).
   * 
   * @return		the number of lines
   */
  @Override
  public int getLineCount() {
    return getDocument().getDefaultRootElement().getElementCount();
  }
  
  /**
   * Jumps to the specified line.
   * 
   * @param index	the 0-based index for the line
   * @return		true if successfully jumped
   */
  public boolean gotoLine(int index) {
    Element	root;
    
    if (index < 0)
      return false;
    
    synchronized(getDocument()) {
      root = getDocument().getDefaultRootElement();
      if (root.getElementCount() <= index)
	return false;
      setCaretPosition(root.getElement(index).getStartOffset());
    }

    return true;
  }
  
  /**
   * Determines the line number for the caret position.
   * 
   * @param position	the caret position
   * @return		the line number, -1 if failed to determine
   */
  public int caretToLine(int position) {
    int		result;
    Element	root;
    Element	child;
    int		i;
    
    result = -1;
    
    synchronized(getDocument()) {
      root = getDocument().getDefaultRootElement();
      for (i = 0; i < root.getElementCount(); i++) {
	child = root.getElement(i);
	if ((child.getStartOffset() >= position) && (position <= child.getEndOffset())) {
	  result = i;
	  break;
	}
      }
    }
    
    return result;
  }

  /**
   * Removes all lines before the specified one.
   *
   * @param index	the 0-based index of the line to become the new first line
   * @return		true if successful removed
   */
  public boolean removeBeforeLine(int index) {
    Element	root;

    if (index >= getLineCount())
      return false;

    synchronized(getDocument()) {
      root = getDocument().getDefaultRootElement();
      if (root.getElementCount() <= index)
	return false;
      root.getElement(index).getStartOffset();
      try {
	getDocument().remove(0, root.getElement(index).getStartOffset());
      }
      catch (Exception e) {
	LoggingHelper.global().log(Level.SEVERE, "Failed to remove lines before " + index, e);
	return false;
      }
    }

    return true;
  }

  /**
   * Sets the text font.
   *
   * @param value the font
   */
  @Override
  public void setTextFont(Font value) {
    setFont(value);
  }

  /**
   * Returns the text font in use.
   *
   * @return the font
   */
  @Override
  public Font getTextFont() {
    return getFont();
  }

  /**
   * Sets the position of the cursor at the end.
   */
  @Override
  public void setCaretPositionLast() {
    setCaretPosition(getDocument().getLength());
  }

  /**
   * Appends the string at the end and keeps the number of lines to the
   * given limit (discarding from the top).
   *
   * @param str		the line to append
   * @param limit	the maximum number of lines to keep, ignored if <= 0
   */
  public void append(String str, int limit) {
    int 	offset;

    append(str);

    if ((limit > 0) && (getLineCount() > limit)) {
      try {
	offset = getLineEndOffset(getLineCount() - limit);
	replaceRange("", 0, offset);
      }
      catch (Exception e) {
	LoggingHelper.global().log(Level.SEVERE, "append(String,int) generated exception:", e);
      }
    }
  }

  /**
   * Adds the listener for listening to any text changes.
   *
   * @param l		the listener to add
   */
  @Override
  public void addAnyChangeListener(ChangeListener l) {
    m_AnyChangeListeners.add(l);
  }

  /**
   * Removes the listener from listening to any text changes.
   *
   * @param l		the listener to remove
   */
  @Override
  public void removeAnyChangeListener(ChangeListener l) {
    m_AnyChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners that some change to the text occurred.
   */
  protected void notifyAnyChangeListeners() {
    ChangeEvent e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_AnyChangeListeners.toArray(new ChangeListener[0]))
      l.stateChanged(e);
  }
}
