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
 * DiffPanel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug;

import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.core.Utils;
import adams.gui.core.BaseTextPaneWithWordWrap;
import adams.gui.core.ColorHelper;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.util.List;

/**
 * Displays one side of a side-by-side diff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DiffTextPane
  extends BaseTextPaneWithWordWrap {
  
  /** for serialization. */
  private static final long serialVersionUID = -8695413301813792099L;

  /** whether we display the left or right side. */
  boolean m_Left;

  /** the list to display. */
  protected SideBySideDiff m_Diff;
  
  /** the color for deleted content. */
  protected Color m_ColorDeleted;
  
  /** the color for added content. */
  protected Color m_ColorAdded;
  
  /** the color for right content. */
  protected Color m_ColorRight;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Left = true;
    m_Diff = new SideBySideDiff();
    
    m_ColorDeleted  = GUIHelper.getColor(getClass(), "Deleted.Color",  ColorHelper.valueOf("#9999FF"));
    m_ColorAdded    = GUIHelper.getColor(getClass(), "Added.Color",    ColorHelper.valueOf("#CCFFFF"));
    m_ColorRight    = GUIHelper.getColor(getClass(), "Right.Color", ColorHelper.valueOf("#FFCCCC"));
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    getTextPane().setFont(Fonts.getMonospacedFont());
    getTextPane().setEditable(false);
    setWordWrap(false);
  }
  
  /**
   * Sets whether this diff represents the left or right content.
   * 
   * @param value	if true then it represents the left view
   */
  public void setLeft(boolean value) {
    m_Left = value;
    update();
  }
  
  /**
   * Returns whether this diff represents the left or right content.
   * 
   * @return		true if it represents the left content
   */
  public boolean isLeft() {
    return m_Left;
  }
  
  /**
   * Sets the diff data to display.
   * 
   * @param value	the diff data
   */
  public void setDiff(SideBySideDiff value) {
    m_Diff = value.clone();
    update();
  }
  
  /**
   * Returns the current diff data on display.
   * 
   * @return		the diff data
   */
  public SideBySideDiff getDiff() {
    return m_Diff;
  }
  
  /**
   * Generates the diff view.
   */
  protected void update() {
    int			width;
    int			i;
    List		diff;
    Object		obj;
    Character		ind;
    StyledDocument	doc;
    SimpleAttributeSet	set;
    SimpleAttributeSet	lines;
    SimpleAttributeSet	normal;
    int			lineNo;
    String		line;
    
    width = Integer.toString(m_Diff.getLeft().size()).length();

    if (m_Left)
      diff = m_Diff.getLeft();
    else
      diff = m_Diff.getRight();

    getTextPane().setText("");
    lineNo = 0;
    doc    = new DefaultStyledDocument();
    lines  = new SimpleAttributeSet();
    StyleConstants.setBold(lines, true);
    StyleConstants.setFontFamily(lines, "monospaced");
    normal = new SimpleAttributeSet();
    StyleConstants.setFontFamily(normal, "monospaced");
    for (i = 0; i < diff.size(); i++) {
      obj = diff.get(i);
      ind = (Character) m_Diff.getIndicator().get(i);
      // color depending on indicator
      set = new SimpleAttributeSet();
      if (ind.equals(DiffUtils.INDICATOR_CHANGED))
	StyleConstants.setBackground(set, m_ColorRight);
      else if (m_Left && ind.equals(DiffUtils.INDICATOR_DELETED))
	StyleConstants.setBackground(set, m_ColorDeleted);
      else if (!m_Left && ind.equals(DiffUtils.INDICATOR_ADDED))
	StyleConstants.setBackground(set, m_ColorAdded);
      StyleConstants.setFontFamily(set, "monospaced");
      line = null;
      if (obj instanceof String) {
	lineNo++;
	line = obj.toString();
      }
      try {
	doc.insertString(doc.getLength(), (line == null ? Utils.padLeft("", ' ', width) : Utils.padLeft("" + lineNo, ' ', width)), lines);
	doc.insertString(doc.getLength(), " ", normal);
	doc.insertString(doc.getLength(), (line == null ? "" : line), set);
	doc.insertString(doc.getLength(), "\n", normal);
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }
    
    getTextPane().setStyledDocument(doc);
    getTextPane().setCaretPosition(0);
    invalidate();
    revalidate();
    doLayout();
  }
}
