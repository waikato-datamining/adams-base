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
 * SideBySideDiffPanel.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug;

import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.core.Shortening;
import adams.core.io.PlaceholderFile;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

/**
 * Panel for displaying side-by-side diff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SideBySideDiffPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -7728182993462886323L;

  /** the max length for label text. */
  public static final int MAX_LABEL_LENGTH = 50;

  /** the left content. */
  protected DiffTextPane m_TextLeft;
  
  /** the scroll pane for the left content. */
  protected BaseScrollPane m_ScrollPaneLeft;
  
  /** the panel for the left content. */
  protected BasePanel m_PanelLeft;
  
  /** the label for the left content. */
  protected JLabel m_LabelLeft;

  /** the text for the left content. */
  protected String m_LabelTextLeft;

  /** the right content. */
  protected DiffTextPane m_TextRight;
  
  /** the scroll pane for the right content. */
  protected BaseScrollPane m_ScrollPaneRight;
  
  /** the panel for the right content. */
  protected BasePanel m_PanelRight;
  
  /** the label for the right content. */
  protected JLabel m_LabelRight;

  /** the text for the right content. */
  protected String m_LabelTextRight;

  /** for the two sides of a diff. */
  protected BasePanel m_PanelAll;
  
  /** whether to ignore viewport changes for the left panel. */
  protected boolean m_IgnoreViewportChangesLeft;
  
  /** whether to ignore viewport changes for the right panel. */
  protected boolean m_IgnoreViewportChangesRight;
  
  /** the last left file. */
  protected File m_LastFileLeft;
  
  /** the last right file. */
  protected File m_LastFileRight;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    resetLastFiles();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();
    
    m_PanelAll = new BasePanel(new GridLayout(1, 2));
    add(m_PanelAll, BorderLayout.CENTER);
    
    // left
    m_TextLeft = new DiffTextPane();
    m_TextLeft.setLeft(true);
    m_ScrollPaneLeft = new BaseScrollPane(m_TextLeft);
    m_ScrollPaneLeft.getViewport().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	if (m_IgnoreViewportChangesLeft)
	  return;
	m_IgnoreViewportChangesRight = true;
	m_ScrollPaneRight.getViewport().setViewPosition(
	    m_ScrollPaneLeft.getViewport().getViewPosition());
	m_IgnoreViewportChangesRight = false;
      }
    });
    m_LabelLeft = new JLabel();
    m_LabelLeft.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  e.consume();
	  showTextLabelPopupMenu(e, true);
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_LabelLeft);
    m_PanelLeft = new BasePanel(new BorderLayout());
    m_PanelLeft.add(panel, BorderLayout.NORTH);
    m_PanelLeft.add(m_ScrollPaneLeft, BorderLayout.CENTER);
    m_PanelAll.add(m_PanelLeft);
    
    // right
    m_TextRight = new DiffTextPane();
    m_TextRight.setLeft(false);
    m_ScrollPaneRight = new BaseScrollPane(m_TextRight);
    m_ScrollPaneRight.getViewport().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	if (m_IgnoreViewportChangesRight)
	  return;
	m_IgnoreViewportChangesLeft = true;
	m_ScrollPaneLeft.getViewport().setViewPosition(
	    m_ScrollPaneRight.getViewport().getViewPosition());
	m_IgnoreViewportChangesLeft = false;
      }
    });
    m_LabelRight = new JLabel();
    m_LabelRight.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  e.consume();
	  showTextLabelPopupMenu(e, false);
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_LabelRight);
    m_PanelRight = new BasePanel(new BorderLayout());
    m_PanelRight.add(panel, BorderLayout.NORTH);
    m_PanelRight.add(m_ScrollPaneRight, BorderLayout.CENTER);
    m_PanelAll.add(m_PanelRight);
  }

  @Override
  protected void finishInit() {
    super.finishInit();
    setLabelText(true, "Left");
    setLabelText(false, "right");
  }

  /**
   * Resets the files last used.
   */
  protected void resetLastFiles() {
    m_LastFileLeft  = new PlaceholderFile(".");
    m_LastFileRight = new PlaceholderFile(".");
  }
  
  /**
   * Returns the underlying panel.
   * 
   * @param left	if true the panel with the left content is returned
   * 			otherwise the right one
   */
  public BasePanel getPanel(boolean left) {
    if (left)
      return m_PanelLeft;
    else
      return m_PanelRight;
  }
  
  /**
   * Returns the underlying diff pane.
   * 
   * @param left	if true the pane with the left content is returned
   * 			otherwise the right one
   */
  public DiffTextPane getDiffTextPane(boolean left) {
    if (left)
      return m_TextLeft;
    else
      return m_TextRight;
  }
  
  /**
   * Sets the text of the label.
   * 
   * @param left	whether to set the label of the left content
   * 			or the right one
   * @param text	the text to set
   */
  public void setLabelText(boolean left, String text) {
    String	shortened;

    shortened = Shortening.shortenMiddle(text, MAX_LABEL_LENGTH);
    if (left) {
      m_LabelTextLeft = text;
      m_LabelLeft.setText(shortened);
      if (!text.equals(shortened))
	m_LabelLeft.setToolTipText(text);
      else
	m_LabelLeft.setToolTipText(null);
    }
    else {
      m_LabelTextRight = text;
      m_LabelRight.setText(shortened);
      if (!text.equals(shortened))
	m_LabelRight.setToolTipText(text);
      else
	m_LabelRight.setToolTipText(null);
    }
  }
  
  /**
   * Returns the text of the label.
   * 
   * @param left	whether to get the label of the left content
   * 			or the right one
   * @return		the current label text
   */
  public String getLabelText(boolean left) {
    if (left)
      return m_LabelTextLeft;
    else
      return m_LabelTextRight;
  }

  /**
   * Shows popup menu for text labels showing the filename.
   *
   * @param e		the mouse event
   * @param left	whether left or right label
   */
  protected void showTextLabelPopupMenu(MouseEvent e, final boolean left) {
    JPopupMenu	menu;
    JMenuItem	menuitem;

    menu = new JPopupMenu();

    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.addActionListener((ActionEvent ae) -> ClipboardHelper.copyToClipboard(getLabelText(left)));
    menu.add(menuitem);

    menuitem = new JMenuItem("Paste", GUIHelper.getIcon("paste.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      String s = ClipboardHelper.pasteStringFromClipboard();
      PlaceholderFile file = new PlaceholderFile(s);
      if (left)
	updateLeft(file);
      else
	updateRight(file);
    });
    menu.add(menuitem);

    menu.show(left ? m_LabelLeft : m_LabelRight, e.getX(), e.getY());
  }

  /**
   * Displays the diff of the two files.
   * 
   * @param file1	the left file
   * @param file2	the right file
   */
  public void compare(File file1, File file2) {
    m_LastFileLeft  = file1;
    m_LastFileRight = file2;
    display(DiffUtils.sideBySide(file1, file2));
  }
  
  /**
   * Displays the diff of the two lists.
   * 
   * @param list1	the left list
   * @param list2	the right list
   */
  public void compare(String[] list1, String[] list2) {
    resetLastFiles();
    display(DiffUtils.sideBySide(list1, list2));
  }
  
  /**
   * Displays the diff of the two lists.
   * 
   * @param list1	the left list
   * @param list2	the right list
   */
  public void compare(List<String> list1, List<String> list2) {
    resetLastFiles();
    display(DiffUtils.sideBySide(list1, list2));
  }

  /**
   * Sets the last left file - does not update.
   *
   * @param file	the new left file, null to reset
   */
  public void setLastFileLeft(File file) {
    if (file == null)
      file = new PlaceholderFile(".");
    m_LastFileLeft = file;
  }

  /**
   * Updates the left file.
   * 
   * @param file	the new left file
   */
  public void updateLeft(File file) {
    m_LastFileLeft = file;
    display(DiffUtils.sideBySide(m_LastFileLeft, m_LastFileRight));
  }

  /**
   * Sets the last right file - does not update.
   *
   * @param file	the new left file, null to reset
   */
  public void setLastFileRight(File file) {
    if (file == null)
      file = new PlaceholderFile(".");
    m_LastFileRight = file;
  }

  /**
   * Updates the right file.
   * 
   * @param file	the new right file
   */
  public void updateRight(File file) {
    m_LastFileRight = file;
    display(DiffUtils.sideBySide(m_LastFileLeft, m_LastFileRight));
  }

  /**
   * Clears the display.
   */
  public void clear() {
    resetLastFiles();
    display(new SideBySideDiff());
  }
  
  /**
   * Updates the display with the given diff data.
   * 
   * @param diff	the diff data to display
   */
  public void display(SideBySideDiff diff) {
    m_TextLeft.setDiff(diff);
    m_TextRight.setDiff(diff);
  }
  
  /**
   * Returns the currently displayed diff.
   * 
   * @return		the diff information
   */
  public SideBySideDiff getCurrent() {
    return m_TextLeft.getDiff();
  }
}
