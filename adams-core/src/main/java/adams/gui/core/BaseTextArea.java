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
 * BaseTextArea.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.gui.chooser.FontChooser;

import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.Frame;

/**
 * A customized JTextArea. Adds functionality for printing and selecting fonts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTextArea
  extends JTextArea
  implements TextAreaComponent {

  /** for serialization. */
  private static final long serialVersionUID = 7970608693979989912L;

  /**
   * Constructs a new TextArea.  A default model is set, the initial string
   * is null, and rows/columns are set to 0.
   */
  public BaseTextArea() {
    super();
  }

  /**
   * Constructs a new TextArea with the specified text displayed.
   * A default model is created and rows/columns are set to 0.
   *
   * @param text the text to be displayed, or null
   */
  public BaseTextArea(String text) {
    super(text);
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
  }

  /**
   * Constructs a new JTextArea with the given document model, and defaults
   * for all of the other arguments (null, 0, 0).
   *
   * @param doc  the model to use
   */
  public BaseTextArea(Document doc) {
    super(doc);
  }

  /**
   * Constructs a new JTextArea with the specified number of rows
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
    setFont(chooser.getCurrent());
  }
  
  /**
   * Returns the number of lines in the document (= # of elements).
   * 
   * @return
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
}
