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
 * SpreadSheetQueryPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import adams.core.AdditionalInformationHandler;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetQueryEditorPanel;
import adams.parser.SpreadSheetQueryText;

/**
 * Panel with spreadsheet query editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetQueryPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -4419661519749458767L;

  /** the panel with the query. */
  protected SpreadSheetQueryEditorPanel m_PanelQuery;

  /** the button for the options. */
  protected JButton m_ButtonOptions;

  /** the button for displaying the help. */
  protected JButton m_ButtonHelp;

  /** the panel for the buttons at the bottom. */
  protected JPanel m_PanelBottom;

  /** the panel for the buttons on the right. */
  protected JPanel m_PanelButtonsRight;

  /** the panel for the buttons on the left. */
  protected JPanel m_PanelButtonsLeft;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    m_PanelQuery = new SpreadSheetQueryEditorPanel();
    m_PanelQuery.setWordWrap(true);
    add(m_PanelQuery, BorderLayout.CENTER);

    m_PanelBottom = new JPanel(new BorderLayout());
    add(m_PanelBottom, BorderLayout.SOUTH);
    
    m_PanelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelBottom.add(m_PanelButtonsLeft, BorderLayout.WEST);
    
    m_ButtonOptions = new JButton("...");
    m_ButtonOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	JPopupMenu menu = new JPopupMenu();
	JMenuItem menuitem;
	// cut
	menuitem = new JMenuItem("Cut");
	menuitem.setIcon(GUIHelper.getIcon("cut.gif"));
	menuitem.setEnabled(m_PanelQuery.canCut());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.cut();
	  }
	});
	menu.add(menuitem);
	// copy
	menuitem = new JMenuItem("Copy");
	menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
	menuitem.setEnabled(m_PanelQuery.canCopy());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.copy();
	  }
	});
	menu.add(menuitem);
	// paste
	menuitem = new JMenuItem("Paste");
	menuitem.setIcon(GUIHelper.getIcon("paste.gif"));
	menuitem.setEnabled(m_PanelQuery.canPaste());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.paste();
	  }
	});
	menu.add(menuitem);
	// line wrap
	menuitem = new JCheckBoxMenuItem("Line wrap");
	menuitem.setIcon(GUIHelper.getEmptyIcon());
	menuitem.setSelected(m_PanelQuery.getWordWrap());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.setWordWrap(((JMenuItem) e.getSource()).isSelected());
	  }
	});
	menu.addSeparator();
	menu.add(menuitem);
	
	menu.show(m_ButtonOptions, 0, m_ButtonOptions.getHeight());
      }
    });
    m_PanelButtonsLeft.add(m_ButtonOptions);
    
    m_PanelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelBottom.add(m_PanelButtonsRight, BorderLayout.EAST);

    if (m_PanelQuery instanceof AdditionalInformationHandler) {
      m_ButtonHelp = new JButton("Help");
      m_ButtonHelp.setMnemonic('H');
      m_ButtonHelp.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  String help = ((AdditionalInformationHandler) m_PanelQuery).getAdditionalInformation();
	  TextDialog dlg = new TextDialog();
	  dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
	  dlg.setDialogTitle("Help");
	  dlg.setContent(help);
	  dlg.setLineWrap(true);
	  dlg.setEditable(false);
	  dlg.setVisible(true);
	}
      });
      m_PanelButtonsRight.add(m_ButtonHelp);
    }
  }
  
  /**
   * Sets the query.
   * 
   * @param value	the query to use
   */
  public void setQuery(SpreadSheetQueryText value) {
    m_PanelQuery.setContent(value.getValue());
  }
  
  /**
   * Returns the current query.
   * 
   * @return		the current query
   */
  public SpreadSheetQueryText getQuery() {
    return new SpreadSheetQueryText(m_PanelQuery.getContent());
  }
}
