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
 * BaseAnnotationEditor.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.core.base.BaseAnnotation;
import adams.core.base.BaseObject;
import adams.core.option.parsing.BaseAnnotationParsing;
import adams.flow.processor.ListAnnotationTags;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.TextAreaComponent;
import adams.gui.core.UISettings;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A PropertyEditor for BaseAnnotation objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.core.base.BaseAnnotation
 */
public class BaseAnnotationEditor
    extends AbstractPropertyEditorSupport
    implements CustomStringRepresentationHandler, InlineEditorSupport {

  /** The text area with the value. */
  protected TextAreaComponent m_TextValue;

  /** The checkbox for the line wrap. */
  protected BaseCheckBox m_CheckLineWrap;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return BaseAnnotationParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return BaseAnnotationParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + BaseAnnotationParsing.toString(null, getValue()) + "\")";

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
    int[] 		offset;
    String 		val;

    if (getValue() == null)
      val = AbstractPropertyEditorSupport.NULL;
    else
      val = BaseAnnotationParsing.toString(null, getValue());
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    JPanel 		panelButtons;
    BaseButton 		buttonClose;
    BaseButton 		buttonOK;
    BaseButton		buttonClear;
    JPanel		panel;
    JPanel		panelTabbedPane;
    BaseTabbedPane 	tabbedPane;
    BaseTextArea	textHelp;
    JPanel		panelHelp;

    panelAll    = new JPanel(new BorderLayout());
    m_TextValue = new BaseTextArea();
    m_TextValue.setRows(30);
    m_TextValue.setColumns(80);
    panelAll.add(new BaseScrollPane((JComponent) m_TextValue), BorderLayout.CENTER);
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panelButtons = new JPanel(new BorderLayout());
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.WEST);
    buttonClear = new BaseButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.setIcon(ImageManager.getIcon("new.gif"));
    buttonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_TextValue.setText("");
      }
    });
    panel.add(buttonClear);

    m_CheckLineWrap = new BaseCheckBox("Line wrap");
    m_CheckLineWrap.setSelected(UISettings.get(getClass(), "LineWrap", false));
    m_CheckLineWrap.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextValue.setLineWrap(m_CheckLineWrap.isSelected());
        UISettings.set(BaseAnnotationEditor.this.getClass(), "LineWrap", m_TextValue.getLineWrap());
      }
    });
    panel.add(m_CheckLineWrap);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.EAST);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = m_TextValue.getText();
	if (((BaseAnnotation) getValue()).isValid(s) && !s.equals(((BaseObject) getValue()).getValue()))
	  setValue(new BaseAnnotation(s));
	closeDialog(APPROVE_OPTION);
      }
    });
    panel.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panel.add(buttonClose);

    // help available?
    panelTabbedPane = new JPanel(new BorderLayout());
    panelTabbedPane.setPreferredSize(GUIHelper.getDefaultSmallDialogDimension());
    tabbedPane = new BaseTabbedPane(BaseTabbedPane.TOP);
    tabbedPane.addTab("Annotations", panelAll);
    panelTabbedPane.add(tabbedPane, BorderLayout.CENTER);
    textHelp   = new BaseTextArea(getHelpDescription());
    textHelp.setLineWrap(true);
    textHelp.setWrapStyleWord(true);
    textHelp.setFont(Fonts.getMonospacedFont());
    textHelp.setEditable(false);
    panelHelp  = new JPanel(new BorderLayout());
    panelHelp.add(new BaseScrollPane(textHelp), BorderLayout.CENTER);
    panelHelp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    tabbedPane.addTab("Help", panelHelp);

    return panelTabbedPane;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();
    if (!m_TextValue.getText().equals("" + getValue()))
      m_TextValue.setText("" + getValue());
    m_TextValue.setLineWrap(m_CheckLineWrap.isSelected());
  }

  /**
   * Checks whether inline editing is available.
   *
   * @return		always true
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
    setValue(new BaseAnnotation(Utils.unbackQuoteChars(value)));
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  public String getInlineValue() {
    return Utils.backQuoteChars(((BaseAnnotation) getValue()).getValue());
  }

  /**
   * Checks whether the value id valid.
   *
   * @param value	the value to check
   * @return		always true
   */
  public boolean isInlineValueValid(String value) {
    return true;
  }

  /**
   * Returns a long help description, e.g., used in tiptexts.
   *
   * @return		the help text, null if not available
   */
  @Override
  public String getHelpDescription() {
    return
	"How to write annotations:\n"
	    + "1. Annotations can be spread over several lines.\n"
	    + "2. You can use custom tags, using the following format:\n"
	    + BaseAnnotation.TAG_START + "tagname[:key=value[,key=value...]]" + BaseAnnotation.TAG_END + "\n"
	    + "3. The 'color' and 'size' keys are automatically interpreted, "
	    + "with 'color' either being a hex string '#ff0000' or a word like 'red' "
	    + "and 'size' being an HTML size ranging from 1 to 7.\n"
	    + "4. You can use the " + ListAnnotationTags.class.getName() + " actor "
	    + "processor to list tags in a flow.";
  }
}
