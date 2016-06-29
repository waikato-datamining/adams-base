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
 * AbstractBaseDateTypeEditor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.DateValueSupporter;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.gui.chooser.DateProvider;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.HelpDialog;
import adams.parser.GrammarSupplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 * Ancestor property editors that handle base date types.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <B> the base object type in use
 * @param <P> the panel in use for selecting the date type
 */
public abstract class AbstractBaseDateTypeEditor<B extends BaseObject & DateValueSupporter & GrammarSupplier,P extends DateProvider>
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, InlineEditorSupport {

  /**
   * The supported placeholders.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Placeholder {
    INF_PAST,
    NOW,
    INF_FUTURE
  }

  /** The calendar component for editing the date. */
  protected P m_Calendar;
  
  /** For specific date placeholders. */
  protected JComboBox<String> m_ComboBoxPlaceholders;

  /** For entering a custom date string. */
  protected JTextField m_TextCustom;

  /** the button for the bringing up the help for the custom format. */
  protected JButton m_ButtonCustomHelp;

  /** The NOW button. */
  protected JButton m_ButtonNow;

  /** The OK button. */
  protected JButton m_ButtonOK;

  /** for parsing the date type. */
  protected B m_Date;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Date = newDateType();
  }

  /**
   * Returns a new instance of the date type used for parsing.
   *
   * @return		the instance
   */
  protected B newDateType() {
    return newDateType((String) null);
  }

  /**
   * Returns a new instance of the date type.
   *
   * @param s		the string to instantiate with, can be null
   * @return		the instance
   */
  protected abstract B newDateType(String s);

  /**
   * Returns a new instance of the date type.
   *
   * @param d		the date to initialize with
   * @return		the instance
   */
  protected abstract B newDateType(Date d);

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return ((B) obj).getValue();
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return newDateType(str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + toCustomStringRepresentation(getValue()) + "\")";

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
      val = AbstractPropertyEditorSupport.NULL;
    else
      val = toCustomStringRepresentation(getValue());
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }

  /**
   * Instantiates a new panel for picking the date type.
   *
   * @return		the panel
   */
  protected abstract P newPanel();

  /**
   * Returns the string equivalent of the placeholder.
   *
   * @param ph		the placeholder to get the string representation for
   * @return		the string representation
   */
  protected abstract String getPlaceholder(Placeholder ph);

  /**
   * Returns the text to use for the "now" button.
   *
   * @return		the button text
   */
  protected abstract String getNowButtonText();

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

    m_Calendar  = newPanel();
    panelValues.add((JPanel) m_Calendar, BorderLayout.CENTER);

    m_ComboBoxPlaceholders = new JComboBox<String>(new String[]{
	"Custom",
	"Selected",
	getPlaceholder(Placeholder.INF_PAST),
	getPlaceholder(Placeholder.NOW),
	getPlaceholder(Placeholder.INF_FUTURE)
    });
    m_ComboBoxPlaceholders.addActionListener((ActionEvent e) -> {
      int index = m_ComboBoxPlaceholders.getSelectedIndex();
      if (index > 1) {
	B date = newDateType((String) m_ComboBoxPlaceholders.getSelectedItem());
	m_Calendar.setDate(date.dateValue());
      }
      m_TextCustom.setEnabled(index == 0);
      m_ButtonNow.setEnabled(index == 1);
      ((JPanel) m_Calendar).setEnabled(index == 1);
    });
    m_ButtonNow = new JButton(getNowButtonText());
    m_ButtonNow.addActionListener((ActionEvent e) -> m_Calendar.setDate(newDateType(getPlaceholder(Placeholder.NOW)).dateValue()));
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    panel.add(new JLabel("Type"));
    panel.add(m_ComboBoxPlaceholders);
    panel.add(m_ButtonNow);
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
    m_ButtonCustomHelp = new JButton(GUIHelper.getIcon("help2.png"));
    m_ButtonCustomHelp.addActionListener(e1 -> showHelp());
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    panel.add(new JLabel("Custom date"));
    panel.add(m_TextCustom);
    panel.add(m_ButtonCustomHelp);
    panelCustom.add(panel, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener((ActionEvent e) -> {
      int index = m_ComboBoxPlaceholders.getSelectedIndex();
      if (index == 0)
	setValue(newDateType(m_TextCustom.getText()));
      else if (index == 1)
	setValue(newDateType(m_Calendar.getDate()));
      else
	setValue(newDateType((String) m_ComboBoxPlaceholders.getSelectedItem()));
      closeDialog(APPROVE_OPTION);
    });
    panelButtons.add(m_ButtonOK);

    buttonClose = new JButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener((ActionEvent e) -> closeDialog(CANCEL_OPTION));
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
   * Displays the help for the
   */
  protected void showHelp() {
    HelpDialog	dialog;

    if (getParentDialog() != null)
      dialog = new HelpDialog(getParentDialog());
    else
      dialog = new HelpDialog(getParentFrame());
    dialog.setDefaultCloseOperation(HelpDialog.DISPOSE_ON_CLOSE);
    dialog.setHelp(m_Date.getGrammar(), false);
    dialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
  }

  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    super.initForDisplay();
    m_TextCustom.setText(((BaseObject) getValue()).getValue());
    m_TextCustom.setToolTipText(((BaseObject) getValue()).getTipText());
    m_Calendar.setDate(((B) getValue()).dateValue());
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
    Object			result;
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
      result = Array.newInstance(m_Date.getClass(), lines.size());
      for (i = 0; i < lines.size(); i++)
	Array.set(result, i, newDateType(lines.get(i)));
    }
    else {
      result = Array.newInstance(m_Date.getClass(), 0);
    }

    return (B[]) result;
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
      setValue(newDateType(value));
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
