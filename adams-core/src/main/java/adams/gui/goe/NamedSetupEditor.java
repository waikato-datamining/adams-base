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
 * NamedSetupEditor.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.NamedSetup;
import adams.core.Utils;
import adams.core.option.AbstractOption;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.MouseUtils;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.tools.NamedSetupManagementPanel.Model;

/**
 * A PropertyEditor for NamedSetup objects that lets the user select a
 * named setup.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedSetupEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler {

  /** The table used for selecting the named setup. */
  protected SortableAndSearchableTable m_Table;

  /** the underlying model. */
  protected Model m_Model;

  /** the OK button. */
  protected JButton m_ButtonOK;

  /** the close button. */
  protected JButton m_ButtonClose;

  /**
   * Returns the NamedSetup as string.
   *
   * @param option	the current option
   * @param object	the NamedSetup object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((NamedSetup) object).getName();
  }

  /**
   * Returns a NamedSetup generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a field
   * @return		the generated NamedSetup object
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new NamedSetup(str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  public String getJavaInitializationString() {
    String	result;
    NamedSetup 	setup;

    setup = (NamedSetup) getValue();

    if (setup == null)
      result = "null";
    else
      result = "new " + NamedSetup.class.getName() + "(\"" + Utils.backQuoteChars(setup.getName()) + "\")";

    return result;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  protected JComponent createCustomEditor() {
    JPanel 	panel;

    panel   = new JPanel(new BorderLayout());
    m_Model = new Model();
    m_Table = new SortableAndSearchableTable(m_Model);
    m_Table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_Table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	m_ButtonOK.setEnabled(m_Table.getSelectedRowCount() == 1);
      }
    });
    m_Table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
	if (m_Table.getSelectedRow() != -1) {
	  if (MouseUtils.isDoubleClick(e))
	    m_ButtonOK.doClick();
	}
      }
    });
    panel.add(new BaseScrollPane(m_Table), BorderLayout.CENTER);

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_Table.getSelectedRowCount() == 1) {
	  NamedSetup setup = new NamedSetup((String) m_Table.getValueAt(m_Table.getSelectedRow(), 0));
	  if (!getValue().equals(setup))
	    setValue(setup);
	}
	closeDialog(APPROVE_OPTION);
      }
    });
    panelButtons.add(m_ButtonOK);

    m_ButtonClose = new JButton("Cancel");
    m_ButtonClose.setMnemonic('C');
    m_ButtonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panelButtons.add(m_ButtonClose);

    return panel;
  }

  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    int		index;

    super.initForDisplay();

    index = m_Model.indexOf((NamedSetup) getValue());
    m_Table.clearSelection();
    if (index != -1) {
      m_Table.getSelectionModel().addSelectionInterval(
	  m_Table.getDisplayRow(index), m_Table.getDisplayRow(index));
    }
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  public void paintValue(Graphics gfx, Rectangle box) {
    FontMetrics 	fm;
    int 		vpad;
    String 		val;

    fm   = gfx.getFontMetrics();
    vpad = (box.height - fm.getHeight()) / 2 ;
    val  = ((NamedSetup) getValue()).getName();
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }
}

