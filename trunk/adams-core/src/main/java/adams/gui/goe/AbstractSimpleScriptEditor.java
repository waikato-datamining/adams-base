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
 * AbstractSimpleScriptEditor.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import adams.core.AdditionalInformationHandler;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.option.AbstractOption;
import adams.gui.core.AbstractSimpleScript;
import adams.gui.core.GUIHelper;
import adams.gui.core.StyledTextEditorPanel;
import adams.gui.dialog.TextDialog;

/**
 * A PropertyEditor for AbstractScript-derived objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.gui.core.AbstractSimpleScript
 */
public class AbstractSimpleScriptEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, InlineEditorSupport {

  /** The text area with the script. */
  protected StyledTextEditorPanel m_TextStatement;

  /**
   * Returns the script as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((AbstractSimpleScript) object).stringValue();
  }

  /**
   * Returns a script object generated from the string.
   *
   * @param cls		the script class
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(Class cls, String str) {
    AbstractSimpleScript	result;

    try {
      if (cls.isArray())
	cls = cls.getComponentType();
      result = (AbstractSimpleScript) cls.newInstance();
      result.setValue(Utils.unbackQuoteChars(str));
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Returns a script object generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return valueOf(option.getDefaultValue().getClass(), str);
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
    return valueOf(getValue().getClass(), str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
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
  @Override
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
   * Returns the default size to use for the panels.
   * 
   * @return		the size
   */
  protected Dimension getDefaultSize() {
    return new Dimension(600, 500);
  }
  
  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    JPanel		panelBottom;
    JPanel 		panelButtonsRight;
    JPanel		panelButtonsLeft;
    final JButton	buttonOptions;
    JButton		buttonHelp;
    JButton 		buttonOK;
    JButton 		buttonClose;

    panelAll = new JPanel(new BorderLayout());
    panelAll.setSize(getDefaultSize());
    panelAll.setMinimumSize(getDefaultSize());
    panelAll.setPreferredSize(getDefaultSize());
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    if (getValue() == null)
      m_TextStatement = new StyledTextEditorPanel();
    else
      m_TextStatement = ((AbstractSimpleScript) getValue()).getTextEditorPanel();
    m_TextStatement.setWordWrap(true);
    panelAll.add(m_TextStatement, BorderLayout.CENTER);

    panelBottom = new JPanel(new BorderLayout());
    panelAll.add(panelBottom, BorderLayout.SOUTH);
    
    panelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBottom.add(panelButtonsLeft, BorderLayout.WEST);
    
    buttonOptions = new JButton("...");
    buttonOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	JPopupMenu menu = createPopupMenu();
	menu.show(buttonOptions, 0, buttonOptions.getHeight());
      }
    });
    panelButtonsLeft.add(buttonOptions);
    
    panelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBottom.add(panelButtonsRight, BorderLayout.EAST);

    if (m_TextStatement instanceof AdditionalInformationHandler) {
      buttonHelp = new JButton("Help");
      buttonHelp.setMnemonic('H');
      buttonHelp.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  String help = ((AdditionalInformationHandler) m_TextStatement).getAdditionalInformation();
	  TextDialog dlg = new TextDialog();
	  dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
	  dlg.setDialogTitle("Help");
	  dlg.setContent(help);
	  dlg.setLineWrap(true);
	  dlg.setEditable(false);
	  dlg.setVisible(true);
	}
      });
      panelButtonsRight.add(buttonHelp);
    }
    
    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = m_TextStatement.getContent();
	if (((AbstractSimpleScript) getValue()).isValid(s) && !s.equals(((AbstractSimpleScript) getValue()).getValue())) {
	  try {
	    AbstractSimpleScript newValue = (AbstractSimpleScript) getValue().getClass().newInstance();
	    newValue.setValue(s);
	    setValue(newValue);
	  }
	  catch (Exception ex) {
	    System.err.println("Failed to create AbstractScript-derived object from '" + s + "':");
	    ex.printStackTrace();
	  }
	}
	closeDialog(APPROVE_OPTION);
      }
    });
    panelButtonsRight.add(buttonOK);

    buttonClose = new JButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panelButtonsRight.add(buttonClose);

    return panelAll;
  }

  /**
   * Creates the popup menu for "..." button.
   * 
   * @return		the menu
   */
  protected JPopupMenu createPopupMenu() {
    JPopupMenu 	result;
    JMenuItem 	menuitem;

    result = new JPopupMenu();
    
    // cut
    menuitem = new JMenuItem("Cut");
    menuitem.setIcon(GUIHelper.getIcon("cut.gif"));
    menuitem.setEnabled(m_TextStatement.canCut());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextStatement.cut();
      }
    });
    result.add(menuitem);
    
    // copy
    menuitem = new JMenuItem("Copy");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(m_TextStatement.canCopy());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextStatement.copy();
      }
    });
    result.add(menuitem);
    
    // paste
    menuitem = new JMenuItem("Paste");
    menuitem.setIcon(GUIHelper.getIcon("paste.gif"));
    menuitem.setEnabled(m_TextStatement.canPaste());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextStatement.paste();
      }
    });
    result.add(menuitem);
    
    // undo
    menuitem = new JMenuItem("Undo");
    menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
    menuitem.setEnabled(m_TextStatement.canUndo());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextStatement.undo();;
      }
    });
    result.addSeparator();
    result.add(menuitem);
    
    // redo
    menuitem = new JMenuItem("Redo");
    menuitem.setIcon(GUIHelper.getIcon("redo.gif"));
    menuitem.setEnabled(m_TextStatement.canRedo());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextStatement.redo();
      }
    });
    result.add(menuitem);
    
    // line wrap
    menuitem = new JCheckBoxMenuItem("Line wrap");
    menuitem.setIcon(GUIHelper.getIcon("linewrap.png"));
    menuitem.setSelected(m_TextStatement.getWordWrap());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextStatement.setWordWrap(((JMenuItem) e.getSource()).isSelected());
      }
    });
    result.addSeparator();
    result.add(menuitem);

    addAdditionalMenuItems(result);

    return result;
  }
  
  /**
   * Hook-method to add further menu items to the menu of the "..." button.
   * <p/>
   * Default implementation does nothing.
   * 
   * @param menu	the popup menu for the button
   */
  protected void addAdditionalMenuItems(JPopupMenu menu) {
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();
    if (!m_TextStatement.getContent().equals("" + getValue()))
      m_TextStatement.setContent("" + getValue());
    m_TextStatement.setToolTipText(((BaseObject) getValue()).getTipText());
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
      setValue(valueOf(getValue().getClass(), value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return Utils.backQuoteChars(((BaseObject) getValue()).getValue());
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    return ((BaseObject) getValue()).isValid(Utils.unbackQuoteChars(value));
  }
}
