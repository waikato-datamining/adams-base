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
 * BaseObjectEditor.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.HelpProvider;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.logging.LoggingHelper;
import adams.core.option.parsing.BaseObjectParsing;
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
import java.util.logging.Level;

/**
 * A PropertyEditor for BaseObject-derived objects.
 * <br><br>
 * If the 
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.core.base.BaseObject
 */
public class BaseObjectEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, 
             InlineEditorSupportWithFavorites {

  /** The text field with the value. */
  protected JTextComponent m_TextValue;

  /** the help button for bringing up a browser with the Java Pattern class. */
  protected BaseButton m_ButtonHelp;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return BaseObjectParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    BaseObject	result;
    Class	cls;

    try {
      cls = getValue().getClass();
      if (cls.isArray())
	cls = cls.getComponentType();
      if (cls == BaseObject.class) {
	System.err.println("Falling back to BaseString class!");
	cls = BaseString.class;
      }
      result = (BaseObject) cls.getDeclaredConstructor().newInstance();
      result.setValue(str);
    }
    catch (Exception e) {
      result = null;
      LoggingHelper.global().log(Level.SEVERE, "Failed to instantiate from custom string representation: " + str, e);
    }

    return result;
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + Utils.backQuoteChars(getValue().toString()) + "\")";

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
  protected BaseObject parse(String s) {
    BaseObject	result;

    try {
      result = (BaseObject) getValue().getClass().getDeclaredConstructor().newInstance();
      result.setValue(s);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to parse: " + s, e);
      result = null;
    }

    return result;
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
    return ((BaseObject) getValue()).isValid(s);
  }

  /**
   * Checks whether the string is the same as the currently used one.
   *
   * @param s		the string to check
   * @return		true if the strings are the same
   */
  protected boolean isUnchanged(String s) {
    return s.equals(((BaseObject) getValue()).getValue());
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
    String	toolTip;

    super.initForDisplay();

    if (!m_TextValue.getText().equals("" + getValue()))
      m_TextValue.setText("" + getValue());
    m_TextValue.setCaretPosition(0);
    toolTip = ((BaseObject) getValue()).getTipText();
    if (toolTip != null)
      toolTip = "<html>" + GUIHelper.processTipText(toolTip, 120) + "</html>";
    m_TextValue.setToolTipText(toolTip);
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
    Class			cls;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the string representations, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    cls    = BaseObjectParsing.determineClass(getValue());
    lines  = dialog.getValues();
    result = (Object[]) Array.newInstance(cls, lines.size());
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
    return ((BaseObject) getValue()).getValue();
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
   * Checks whether favorites support is available.
   *
   * @return		true if enabled
   */
  public boolean isInlineFavoritesEnabled() {
    return ((BaseObject) getValue()).hasFavoritesSupport();
  }

  /**
   * Returns the class to use for favorites.
   *
   * @return		the class to use
   */
  public Class getInlineFavoritesClass() {
    return getValue().getClass();
  }

  /**
   * Returns a long help description, e.g., used in tiptexts.
   * <br><br>
   * If current value is an instance of {@link HelpProvider}, then its description
   * is returned, otherwise the value's tip text.
   * 
   * @return		the help text, null if not available
   */
  @Override
  public String getHelpDescription() {
    if (getValue() instanceof HelpProvider)
      return ((HelpProvider) getValue()).getHelpDescription();
    else if (getValue() != null)
      return ((BaseObject) getValue()).getTipText();
    else
      return null;
  }
}
