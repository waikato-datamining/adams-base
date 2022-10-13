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
 * AbstractAdvancedScriptEditor.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.AdditionalInformationHandler;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.option.parsing.AdvancedScriptParsing;
import adams.gui.core.AbstractAdvancedScript;
import adams.gui.core.AbstractTextAreaPanelWithAdvancedSyntaxHighlighting;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.DefaultTextAreaPanelWithAdvancedSyntaxHighlighting;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A PropertyEditor for AbstractAdvancedScript-derived objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.gui.core.AbstractAdvancedScript
 */
public class AbstractAdvancedScriptEditor
    extends AbstractPropertyEditorSupport
    implements CustomStringRepresentationHandler, InlineEditorSupport {

  /** The text area with the script. */
  protected AbstractTextAreaPanelWithAdvancedSyntaxHighlighting m_TextStatement;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return AdvancedScriptParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return AdvancedScriptParsing.valueOf(getValue().getClass(), str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + AdvancedScriptParsing.toString(null, getValue()) + "\")";

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
      val = AbstractPropertyEditorSupport.NULL;
    else
      val = AdvancedScriptParsing.toString(null, getValue());
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }

  /**
   * Returns the default size to use for the panels.
   *
   * @return		the size
   */
  protected Dimension getDefaultSize() {
    return GUIHelper.makeTaller(GUIHelper.getDefaultSmallDialogDimension());
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel			panelAll;
    JPanel			panelBottom;
    JPanel 			panelButtonsRight;
    JPanel			panelButtonsLeft;
    BaseButtonWithDropDownMenu	buttonOptions;
    BaseButton			buttonHelp;
    BaseButton 			buttonOK;
    BaseButton 			buttonClose;
    JPanel			panelTabbedPane;
    BaseTabbedPane 		tabbedPane;
    BaseTextArea 		textHelp;
    JPanel			panelHelp;

    panelAll = new JPanel(new BorderLayout());
    panelAll.setSize(getDefaultSize());
    panelAll.setMinimumSize(getDefaultSize());
    panelAll.setPreferredSize(getDefaultSize());

    if (getValue() == null)
      m_TextStatement = new DefaultTextAreaPanelWithAdvancedSyntaxHighlighting();
    else
      m_TextStatement = ((AbstractAdvancedScript) getValue()).getTextAreaPanel();
    panelAll.add(m_TextStatement, BorderLayout.CENTER);

    panelBottom = new JPanel(new BorderLayout());
    panelAll.add(panelBottom, BorderLayout.SOUTH);

    panelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBottom.add(panelButtonsLeft, BorderLayout.WEST);

    buttonOptions = new BaseButtonWithDropDownMenu();
    buttonOptions.setDropDownMenu(createPopupMenu());
    panelButtonsLeft.add(buttonOptions);

    panelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBottom.add(panelButtonsRight, BorderLayout.EAST);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = m_TextStatement.getContent();
	if (((AbstractAdvancedScript) getValue()).isValid(s) && !s.equals(((AbstractAdvancedScript) getValue()).getValue())) {
	  try {
	    AbstractAdvancedScript newValue = (AbstractAdvancedScript) getValue().getClass().newInstance();
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

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panelButtonsRight.add(buttonClose);

    // help available?
    panelTabbedPane = null;
    if (m_TextStatement instanceof AdditionalInformationHandler) {
      panelTabbedPane = new JPanel(new BorderLayout());
      panelTabbedPane.setPreferredSize(GUIHelper.getDefaultSmallDialogDimension());
      tabbedPane = new BaseTabbedPane(BaseTabbedPane.TOP);
      tabbedPane.addTab("Script", panelAll);
      panelTabbedPane.add(tabbedPane, BorderLayout.CENTER);
      textHelp   = new BaseTextArea(((AdditionalInformationHandler) m_TextStatement).getAdditionalInformation());
      textHelp.setLineWrap(true);
      textHelp.setWrapStyleWord(true);
      textHelp.setFont(Fonts.getMonospacedFont());
      textHelp.setEditable(false);
      panelHelp  = new JPanel(new BorderLayout());
      panelHelp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      panelHelp.add(new BaseScrollPane(textHelp), BorderLayout.CENTER);
      tabbedPane.addTab("Help", panelHelp);
    }
    else {
      panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    if (panelTabbedPane != null)
      return panelTabbedPane;
    else
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
    menuitem.setIcon(ImageManager.getIcon("cut.gif"));
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
    menuitem.setIcon(ImageManager.getIcon("copy.gif"));
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
    menuitem.setIcon(ImageManager.getIcon("paste.gif"));
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
    menuitem.setIcon(ImageManager.getIcon("undo.gif"));
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
    menuitem.setIcon(ImageManager.getIcon("redo.gif"));
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
    menuitem.setIcon(ImageManager.getIcon("linewrap.png"));
    menuitem.setSelected(m_TextStatement.getLineWrap());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextStatement.setLineWrap(((JMenuItem) e.getSource()).isSelected());
      }
    });
    result.addSeparator();
    result.add(menuitem);

    addAdditionalMenuItems(result);

    return result;
  }

  /**
   * Hook-method to add further menu items to the menu of the "..." button.
   * <br><br>
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
      setValue(AdvancedScriptParsing.valueOf(getValue().getClass(), value));
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
