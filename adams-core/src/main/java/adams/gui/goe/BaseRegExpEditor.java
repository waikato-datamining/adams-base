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
 * BaseRegExpEditor.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseRegExp;
import adams.core.option.AbstractOption;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.BrowserHelper;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

/**
 * Editor specifically designed for entering regular expression. In order to
 * enter line feeds, tabs, etc. the user can enable "escaped input".
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseRegExpEditor
  extends BaseObjectEditor
  implements MultiSelectionEditor {

  /** the checkbox for "escaped input". */
  protected JCheckBox m_CheckBoxEscapedInput;

  /** the help button for bringing up a browser with the Java Pattern class. */
  protected JButton m_ButtonHelp;

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((BaseRegExp) object).stringValue();
  }

  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new BaseRegExp(Utils.unbackQuoteChars(str));
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel	panelAll;
    JPanel	panelCheck;
    JPanel	panelInput;
    JPanel	panel;
    JLabel	label;
    JPanel 	panelButtons;
    JButton 	buttonOK;
    JButton 	buttonClose;

    panelAll   = new JPanel(new BorderLayout());
    panelInput = new JPanel(new BorderLayout());
    panelAll.add(panelInput, BorderLayout.CENTER);
    m_TextValue = new BaseTextArea(1, 20);
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
    label = new JLabel("Value");
    label.setDisplayedMnemonic('V');
    label.setLabelFor(m_TextValue);
    m_ButtonHelp = new JButton(getHelpTitle(), GUIHelper.getIcon(getHelpIcon()));
    m_ButtonHelp.setToolTipText(getHelpDescription());
    m_ButtonHelp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	BrowserHelper.openURL(getHelpURL());
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelInput.add(panel, BorderLayout.WEST);
    panel.add(label);
    panelInput.add(new BaseScrollPane(m_TextValue), BorderLayout.CENTER);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelInput.add(panel, BorderLayout.EAST);
    panel.add(m_ButtonHelp);

    panelCheck = new JPanel(new BorderLayout());
    panelInput.add(panelCheck, BorderLayout.SOUTH);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelCheck.add(panel, BorderLayout.NORTH);
    m_CheckBoxEscapedInput = new JCheckBox("Escaped input");
    m_CheckBoxEscapedInput.setSelected(true);
    m_CheckBoxEscapedInput.setMnemonic('E');
    m_CheckBoxEscapedInput.setToolTipText("If checked, you can eg enter new lines like '\\n' or tabs like '\\t' (without the quotes)");
    m_CheckBoxEscapedInput.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_CheckBoxEscapedInput.isSelected())
	  m_TextValue.setText(Utils.backQuoteChars(m_TextValue.getText()));
	else
	  m_TextValue.setText(Utils.unbackQuoteChars(m_TextValue.getText()));
      }
    });
    panelCheck.add(m_CheckBoxEscapedInput);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	acceptInput();
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new JButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	discardInput();
      }
    });
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Unescapes the string if necessary.
   *
   * @param s		the string to unescape
   * @return		the processed string
   */
  protected String unEscape(String s) {
    if (m_CheckBoxEscapedInput.isSelected())
      return Utils.unbackQuoteChars(s);
    else
      return s;
  }

  /**
   * Checks whether the string is valid.
   *
   * @param s		the string to check
   * @return		true if the string is valid
   */
  @Override
  protected boolean isValid(String s) {
    return super.isValid(unEscape(s));
  }

  /**
   * Checks whether the string is the same as the currently used one.
   *
   * @param s		the string to check
   * @return		true if the strings are the same
   */
  @Override
  protected boolean isUnchanged(String s) {
    return super.isUnchanged(unEscape(s));
  }

  /**
   * Parses the given string and returns the generated object. The string
   * has to be a valid one, i.e., the isValid(String) check has been
   * performed already and succeeded.
   *
   * @param s		the string to parse
   * @return		the generated object, or null in case of an error
   */
  @Override
  protected BaseObject parse(String s) {
    return super.parse(unEscape(s));
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    String	value;

    resetChosenOption();
    
    if (m_CheckBoxEscapedInput.isSelected())
      value = Utils.backQuoteChars(((BaseRegExp) getValue()).getValue());
    else
      value = ((BaseRegExp) getValue()).getValue();

    if (!m_TextValue.getText().equals(value))
      m_TextValue.setText(value);
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextValue.grabFocus();
  }

  /**
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  @Override
  protected String getStringToPaint() {
    return ((BaseRegExp) getValue()).stringValue();
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    BaseRegExp[]		result;
    MultiLineValueDialog	dialog;
    Vector<String>		lines;
    int				i;

    dialog = new MultiLineValueDialog();
    dialog.setInfoText("Enter the regular expressions, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = new Vector<String>(Arrays.asList(dialog.getContent().split("\n")));
      Utils.removeEmptyLines(lines);
      result = new BaseRegExp[lines.size()];
      for (i = 0; i < lines.size(); i++)
	result[i] = (BaseRegExp) parse(lines.get(i));
    }
    else {
      result = new BaseRegExp[0];
    }

    return result;
  }
  
  /**
   * Returns a URL with additional information.
   * 
   * @return		the URL, null if not available
   */
  @Override
  public String getHelpURL() {
    return "http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html";
  }
  
  /**
   * Returns a long help description, e.g., used in tiptexts.
   * 
   * @return		the help text, null if not available
   */
  @Override
  public String getHelpDescription() {
    return "More info on regular expressions";
  }
  
  /**
   * Returns a short title for the help, e.g., used for buttons.
   * 
   * @return		the short title, null if not available
   */
  @Override
  public String getHelpTitle() {
    return null;
  }
  
  /**
   * Returns the name of a help icon, e.g., used for buttons.
   * 
   * @return		the icon name, null if not available
   */
  @Override
  public String getHelpIcon() {
    return "help2.png";
  }
}
