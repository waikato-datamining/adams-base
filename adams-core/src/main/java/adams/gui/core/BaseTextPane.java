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
 * BaseTextPane.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2003-2007 Philip Isenhour (setting font)
 */
package adams.gui.core;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.gui.chooser.FontChooser;

import javax.swing.JInternalFrame;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.Frame;

/**
 * A customized JTextPane. Adds functionality for wordwrap, printing and
 * selecting fonts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTextPane
  extends JTextPane {

  /** for serialization. */
  private static final long serialVersionUID = 5053144101104728014L;

  /**
   * Creates a new <code>BaseTextPane</code>.  A new instance of
   * <code>StyledEditorKit</code> is
   * created and set, and the document model set to <code>null</code>.
   */
  public BaseTextPane() {
    super();
    initialize();
  }

  /**
   * Creates a new <code>BaseTextPane</code>, with a specified document model.
   * A new instance of <code>javax.swing.text.StyledEditorKit</code>
   *  is created and set.
   *
   * @param doc the document model
   */
  public BaseTextPane(StyledDocument doc) {
    super(doc);
    initialize();
  }

  /**
   * Initializes the member variables.
   */
  protected void initialize() {
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
    setTextFont(chooser.getCurrent());
  }

  /**
   * Sets the font of the text pane.
   *
   * @param font	the font to use
   */
  @MixedCopyright(
      author = "Philip Isenhour",
      copyright = "2003-2007 Philip Isenhour",
      license = License.PUBLIC_DOMAIN,
      url = "http://javatechniques.com/blog/setting-jtextpane-font-and-color/"
  )
  public void setTextFont(Font font) {
    // Start with the current input attributes for the JTextPane. This
    // should ensure that we do not wipe out any existing attributes
    // (such as alignment or other paragraph attributes) currently
    // set on the text area.
    MutableAttributeSet attrs = getInputAttributes();

    // Set the font family, size, and style, based on properties of
    // the Font object. Note that JTextPane supports a number of
    // character attributes beyond those supported by the Font class.
    // For example, underline, strike-through, super- and sub-script.
    StyleConstants.setFontFamily(attrs, font.getFamily());
    StyleConstants.setFontSize(attrs, font.getSize());
    StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
    StyleConstants.setBold(attrs, (font.getStyle() & Font.BOLD) != 0);

    // Retrieve the pane's document object
    StyledDocument doc = getStyledDocument();

    // Replace the style for the entire document. We exceed the length
    // of the document by 1 so that text entered at the end of the
    // document uses the attributes.
    doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
  }
  
  /**
   * Returns the number of lines in the document (= # of elements).
   * 
   * @return		the number of lines
   */
  public int getLineCount() {
    return getDocument().getDefaultRootElement().getElementCount();
  }
  
  /**
   * Jumps to the specified line.
   * 
   * @param line	the 0-based index for the line
   * @return		true if successfully jumped
   */
  public boolean gotoLine(int line) {
    boolean	result;
    int 	currLine;
    int 	currSel;
    String 	text;
    String 	sep;
    int 	sepLen;
    int 	next;
    
    result   = false;
    currLine = 0;
    currSel  = 0;
    text     = getText();
    sep      = System.getProperty("line.separator");
    sepLen   = sep.length();
    while (currLine < line) {
      next = text.indexOf(sep, currSel);
      if (next > -1) {
	currSel = next + sepLen;
	currLine++;
      } 
      else {
	// set to the end of doc
	currSel  = text.length();
	result   = (currLine == line);
	currLine = line;
      }
    }
    
    setCaretPosition(currSel);
    
    return result;
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
   * Appends the text at the end.
   *
   * @param text	the text to append
   */
  public synchronized void append(String text) {
    append(text, null);
  }

  /**
   * Appends the text at the end.
   *
   * @param text	the text to append
   * @param a		the attribute set, null if to use current
   */
  public synchronized void append(String text, AttributeSet a) {
    StyledDocument	doc;

    doc = getStyledDocument();
    try {
      doc.insertString(doc.getLength(), text, a);
    }
    catch (Exception e) {
      System.err.println("Failed to insert text: " + text);
      e.printStackTrace();
    }
    setCaretPosition(doc.getLength());
  }

  /**
   * Sets the position of the cursor at the end.
   */
  public void setCaretPositionLast() {
    setCaretPosition(getDocument().getLength());
  }
}
