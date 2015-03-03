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
 * BaseDateEditor.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import adams.core.Utils;
import adams.core.base.BaseDate;
import adams.core.base.BaseObject;
import adams.core.base.BaseTime;
import adams.core.option.AbstractOption;
import adams.gui.chooser.DatePanel;
import adams.gui.dialog.ApprovalDialog;

/**
 * A PropertyEditor for BaseDate objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.base.BaseDate
 */
public class BaseDateEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, InlineEditorSupport {

  /** The calendar component for editing the date. */
  protected DatePanel m_Calendar;
  
  /** For specific date placeholders. */
  protected JComboBox m_ComboBoxPlaceholders;

  /** For entering a custom date string. */
  protected JTextField m_TextCustom;

  /** The NOW button. */
  protected JButton m_ButtonToday;

  /** The OK button. */
  protected JButton m_ButtonOK;

  /** for parsing the date. */
  protected BaseDate m_Date;

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((BaseDate) object).getValue();
  }

  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new BaseDate(str);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Date = new BaseDate();
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

    result = "new " + getValue().getClass().getName() + "(\"" + toString(null, getValue()) + "\")";

    return result;
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
    vpad = (box.height - fm.getHeight()) / 2;
    if (getValue() == null)
      val = "null";
    else
      val = toString(null, getValue());
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  protected JComponent createCustomEditor() {
    JPanel	panelAll;
    JPanel	panel;
    JPanel	panelValues;
    JPanel	panelCustom;
    JPanel 	panelButtons;
    JButton 	buttonClose;

    panelAll = new JPanel(new BorderLayout());
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panelValues = new JPanel(new BorderLayout());
    panelAll.add(panelValues, BorderLayout.CENTER);
    panelCustom = new JPanel(new BorderLayout());
    panelValues.add(panelCustom, BorderLayout.SOUTH);

    m_Calendar  = new DatePanel();
    panelValues.add(m_Calendar, BorderLayout.CENTER);

    m_ComboBoxPlaceholders = new JComboBox(new String[]{
	"Custom",
	"Selected",
	BaseDate.INF_PAST,
	BaseDate.NOW,
	BaseDate.INF_FUTURE
    });
    m_ComboBoxPlaceholders.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	int index = m_ComboBoxPlaceholders.getSelectedIndex();
	if (index > 1) {
	  BaseDate date = new BaseDate((String) m_ComboBoxPlaceholders.getSelectedItem());
	  m_Calendar.setDate(date.dateValue());
	}
	m_TextCustom.setEnabled(index == 0);
	m_ButtonToday.setEnabled(index == 1);
	m_Calendar.setEnabled(index == 1);
      }
    });
    m_ButtonToday = new JButton("Today");
    m_ButtonToday.setMnemonic('T');
    m_ButtonToday.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Calendar.setDate(new BaseTime(BaseTime.NOW).dateValue());
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    panel.add(new JLabel("Type"));
    panel.add(m_ComboBoxPlaceholders);
    panel.add(m_ButtonToday);
    panelCustom.add(panel, BorderLayout.NORTH);

    m_TextCustom = new JTextField(12);
    m_TextCustom.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
	update();
      }
      public void insertUpdate(DocumentEvent e) {
	update();
      }
      public void changedUpdate(DocumentEvent e) {
	update();
      }
      protected void update() {
	updateButtons();
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    panel.add(new JLabel("Custom date"));
    panel.add(m_TextCustom);
    panelCustom.add(panel, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	int index = m_ComboBoxPlaceholders.getSelectedIndex();
	if (index == 0)
	  setValue(new BaseDate(m_TextCustom.getText()));
	else if (index == 1)
	  setValue(new BaseDate(m_Calendar.getDate()));
	else
	  setValue(new BaseDate((String) m_ComboBoxPlaceholders.getSelectedItem()));
	closeDialog(APPROVE_OPTION);
      }
    });
    panelButtons.add(m_ButtonOK);

    buttonClose = new JButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Sets the enabled state of the buttons.
   */
  protected void updateButtons() {
    int		index;

    index = m_ComboBoxPlaceholders.getSelectedIndex();

    if (index == 0)
      m_ButtonOK.setEnabled(m_Date.isValid(m_TextCustom.getText()));
    else
      m_ButtonOK.setEnabled(true);
  }

  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    super.initForDisplay();
    m_TextCustom.setText(((BaseDate) getValue()).getValue());
    m_TextCustom.setToolTipText(((BaseObject) getValue()).getTipText());
    m_Calendar.setDate(((BaseDate) getValue()).dateValue());
    m_ComboBoxPlaceholders.setSelectedIndex(m_ComboBoxPlaceholders.getSelectedIndex());
    updateButtons();
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    BaseDate[]			result;
    MultiLineValueDialog	dialog;
    Vector<String>		lines;
    int				i;

    initForDisplay();

    dialog = new MultiLineValueDialog();
    dialog.setInfoText("Enter the date values, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = new Vector<String>(Arrays.asList(dialog.getContent().split("\n")));
      Utils.removeEmptyLines(lines);
      result = new BaseDate[lines.size()];
      for (i = 0; i < lines.size(); i++)
	result[i] = new BaseDate(lines.get(i));
    }
    else {
      result = new BaseDate[0];
    }

    return result;
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		true if editing available
   */
  public boolean isInlineEditingAvailable() {
    return true;
  }

  /**
   * Sets the value to use.
   * 
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    if (isInlineValueValid(value))
      setValue(new BaseDate(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((BaseObject) getValue()).getValue();
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    return ((BaseObject) getValue()).isValid(value);
  }
}
