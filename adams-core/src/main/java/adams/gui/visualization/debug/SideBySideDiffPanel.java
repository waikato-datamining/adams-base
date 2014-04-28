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
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.core.io.PlaceholderFile;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;

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

  /** the left content. */
  protected DiffTextPane m_TextLeft;
  
  /** the scroll pane for the left content. */
  protected BaseScrollPane m_ScrollPaneLeft;
  
  /** the panel for the left content. */
  protected BasePanel m_PanelLeft;
  
  /** the label for the left content. */
  protected JLabel m_LabelLeft;

  /** the right content. */
  protected DiffTextPane m_TextRight;
  
  /** the scroll pane for the right content. */
  protected BaseScrollPane m_ScrollPaneRight;
  
  /** the panel for the right content. */
  protected BasePanel m_PanelRight;
  
  /** the label for the right content. */
  protected JLabel m_LabelRight;
  
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
    m_LabelLeft = new JLabel("Left");
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
    m_LabelRight = new JLabel("Right");
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_LabelRight);
    m_PanelRight = new BasePanel(new BorderLayout());
    m_PanelRight.add(panel, BorderLayout.NORTH);
    m_PanelRight.add(m_ScrollPaneRight, BorderLayout.CENTER);
    m_PanelAll.add(m_PanelRight);
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
    if (left)
      m_LabelLeft.setText(text);
    else
      m_LabelRight.setText(text);
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
      return m_LabelLeft.getText();
    else
      return m_LabelRight.getText();
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
   * Updates the left file.
   * 
   * @param file	the new left file
   */
  public void updateLeft(File file) {
    m_LastFileLeft = file;
    display(DiffUtils.sideBySide(m_LastFileLeft, m_LastFileRight));
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
