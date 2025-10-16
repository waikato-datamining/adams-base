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
 * StringEditor.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.option.constraint.AbstractOptionConstraint;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.BrowserHelper;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.List;

/**
 * A PropertyEditor for strings.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.core.base.BaseObject
 */
public class StringEditor
  extends AbstractPropertyEditorSupport
  implements MultiSelectionEditor, InlineEditorSupport, PropertyEditorWithConstraint {

  /** The text field with the value. */
  protected JTextComponent m_TextValue;

  /** the help button for bringing up a browser with the Java Pattern class. */
  protected BaseButton m_ButtonHelp;

  /** the constraint to use. */
  protected AbstractOptionConstraint m_Constraint;

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new String(\"" + getValue() + "\")";

    return result;
  }

  /**
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  protected String getStringToPaint() {
    return "" + getValue();
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   * @see		#getStringToPaint()
   */
  @Override
  public void paintValue(Graphics gfx, Rectangle box) {
    int[] 		offset;
    String 		val;

    val  = getStringToPaint();
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
  }

  /**
   * Parses the given string and returns the generated object. The string
   * has to be a valid one, i.e., the isValid(String) check has been
   * performed already and succeeded.
   *
   * @param s		the string to parse
   * @return		the generated object, or null in case of an error
   */
  protected String parse(String s) {
    return s;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		always null
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    JPanel		panel;
    JLabel		label;
    JPanel 		panelButtons;
    BaseButton 		buttonOK;
    BaseButton 		buttonClose;

    m_TextValue = new BaseTextArea(2, 30);
    ((BaseTextArea) m_TextValue).setLineWrap(true);
    m_TextValue.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  acceptInput();
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  discardInput();
	}
	else {
	  super.keyPressed(e);
	}
      }
    });

    panelAll = new JPanel(new BorderLayout());

    label = new JLabel("Value");
    label.setDisplayedMnemonic('V');
    label.setLabelFor(m_TextValue);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panelAll.add(panel, BorderLayout.WEST);

    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    panel.add(new BaseScrollPane(m_TextValue), BorderLayout.CENTER);
    panelAll.add(panel, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonHelp = new BaseButton();
    m_ButtonHelp.setVisible(false);
    m_ButtonHelp.setToolTipText(getHelpDescription());
    m_ButtonHelp.addActionListener((ActionEvent e) -> BrowserHelper.openURL(getHelpURL()));
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panel, BorderLayout.EAST);
    panel.add(m_ButtonHelp);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener((ActionEvent e) -> acceptInput());
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener((ActionEvent e) -> discardInput());
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Checks whether the string is valid.
   *
   * @param s		the string to check
   * @return		true if the string is valid
   */
  protected boolean isValid(String s) {
    return ((m_Constraint == null) || m_Constraint.isValid(s));
  }

  /**
   * Checks whether the string is the same as the currently used one.
   *
   * @param s		the string to check
   * @return		true if the strings are the same
   */
  protected boolean isUnchanged(String s) {
    return s.equals((String) getValue());
  }

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String 	s;

    s = m_TextValue.getText();
    if (isValid(s) && !isUnchanged(s))
      setValue(parse(s));
    closeDialog(APPROVE_OPTION);
  }

  /**
   * Discards the input and closes the dialog.
   */
  protected void discardInput() {
    closeDialog(CANCEL_OPTION);
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();

    if (!m_TextValue.getText().equals("" + getValue()))
      m_TextValue.setText("" + getValue());
    m_TextValue.setCaretPosition(0);
    m_TextValue.grabFocus();
    // update help button
    if (m_ButtonHelp != null) {
      m_ButtonHelp.setVisible(getHelpURL() != null);
      m_ButtonHelp.setToolTipText("<html>" + GUIHelper.processTipText(getHelpDescription(), 120) + "</html>");
      if (getHelpIcon() != null)
	m_ButtonHelp.setIcon(ImageManager.getIcon(getHelpIcon()));
      else
	m_ButtonHelp.setIcon(null);
      m_ButtonHelp.setText(getHelpTitle());
    }
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    Object[]			result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the strings, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = (Object[]) Array.newInstance(String.class, lines.size());
    for (i = 0; i < lines.size(); i++)
      Array.set(result, i, parse(lines.get(i)));

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
    if (isValid(value))
      setValue(parse(value));
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  public String getInlineValue() {
    return (String) getValue();
  }

  /**
   * Checks whether the value id valid.
   *
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    return isValid(value);
  }

  /**
   * The constraint to use.
   *
   * @param value the constraint, null to remove
   */
  @Override
  public void setConstraint(AbstractOptionConstraint value) {
    m_Constraint = value;
  }

  /**
   * Return the constraint in use.
   *
   * @return the constraint, null if none set
   */
  @Override
  public AbstractOptionConstraint getConstraint() {
    return m_Constraint;
  }
}
