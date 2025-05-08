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
 * CronScheduleEditor.java
 * Copyright (C) 2012-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.base.BaseObject;
import adams.core.base.CronSchedule;
import adams.core.option.parsing.CronScheduleParsing;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.BaseTextField;
import adams.gui.core.BrowserHelper;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import org.quartz.CronExpression;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A PropertyEditor for CronSchedule objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.core.base.CronSchedule
 */
public class CronScheduleEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, InlineEditorSupport {

  /** for the parts of the expression. */
  protected ParameterPanel m_PanelParts;

  /** the text field for the seconds. */
  protected BaseTextField m_TextSeconds;

  /** the text field for the minutes. */
  protected BaseTextField m_TextMinutes;

  /** the text field for the hours. */
  protected BaseTextField m_TextHours;

  /** the text field for the day-of-month. */
  protected BaseTextField m_TextDayOfMonth;

  /** the text field for the month. */
  protected BaseTextField m_TextMonth;

  /** the text field for the day-of-week. */
  protected BaseTextField m_TextDayOfWeek;

  /** the text field for the year. */
  protected BaseTextField m_TextYear;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return CronScheduleParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return CronScheduleParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + CronScheduleParsing.toString(null, getValue()) + "\")";

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
      val = "null";
    else
      val = CronScheduleParsing.toString(null, getValue());
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
  }

  /**
   * Returns the current expression in the GUI.
   *
   * @return		the expression
   */
  protected String getCurrentExpression() {
    String 	result;

    result =   m_TextSeconds.getText() + " "
             + m_TextMinutes.getText() + " "
             + m_TextHours.getText() + " "
             + m_TextDayOfMonth.getText() + " "
             + m_TextMonth.getText() + " "
             + m_TextDayOfWeek.getText() + " "
             + m_TextYear.getText();

    result = result.trim();

    return result;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel	panelAll;
    JPanel 	panelButtons;
    JPanel	panelBottom;
    BaseButton 	buttonClose;
    BaseButton 	buttonOK;
    BaseButton	buttonHelp;
    BaseButtonWithDropDownMenu	buttonTemplates;
    BaseButton 	buttonValidate;

    panelAll    = new JPanel(new BorderLayout());

    m_TextSeconds = new BaseTextField(5);
    m_TextSeconds.setToolTipText("0-59 and , - * /");
    m_TextMinutes = new BaseTextField(5);
    m_TextMinutes.setToolTipText("0-59 and , - * /");
    m_TextHours = new BaseTextField(5);
    m_TextHours.setToolTipText("0-23 and , - * /");
    m_TextDayOfMonth = new BaseTextField(5);
    m_TextDayOfMonth.setToolTipText("1-31 and , - * ? / L W");
    m_TextMonth = new BaseTextField(5);
    m_TextMonth.setToolTipText("1-12 or JAN-DEC and , - * /");
    m_TextDayOfWeek = new BaseTextField(5);
    m_TextDayOfWeek.setToolTipText("1-7 and , - * ? / L #");
    m_TextYear = new BaseTextField(5);
    m_TextYear.setToolTipText("empty, 1970-2099 and , - * /");

    m_PanelParts = new ParameterPanel();
    m_PanelParts.addParameter("_Seconds",          m_TextSeconds);
    m_PanelParts.addParameter("_Minutes",          m_TextMinutes);
    m_PanelParts.addParameter("_Hours",            m_TextHours);
    m_PanelParts.addParameter("_Day of month",     m_TextDayOfMonth);
    m_PanelParts.addParameter("M_onth",            m_TextMonth);
    m_PanelParts.addParameter("Day of _week",      m_TextDayOfWeek);
    m_PanelParts.addParameter("_Year (optional)",  m_TextYear);

    panelAll.add(m_PanelParts, BorderLayout.CENTER);

    panelBottom = new JPanel(new BorderLayout());
    panelAll.add(panelBottom, BorderLayout.SOUTH);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBottom.add(panelButtons, BorderLayout.WEST);

    buttonHelp = new BaseButton(ImageManager.getIcon(getHelpIcon()));
    buttonHelp.setToolTipText(getHelpDescription());
    buttonHelp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	BrowserHelper.openURL(getHelpURL());
      }
    });
    panelButtons.add(buttonHelp);

    buttonTemplates = new BaseButtonWithDropDownMenu(ImageManager.getIcon("template.gif"));
    buttonTemplates.setToolTipText("Templates for various schedules");
    Map<String,CronSchedule> templates = BaseObject.getTemplates(CronSchedule.class);
    List<String> items = new ArrayList<>(templates.keySet());
    Collections.sort(items);
    for (String item: items) {
      JMenuItem menuitem = new JMenuItem(item);
      menuitem.addActionListener((ActionEvent ae) -> setValue(templates.get(item)));
      buttonTemplates.addToMenu(menuitem);
    }
    panelButtons.add(buttonTemplates);

    buttonValidate = new BaseButton(ImageManager.getIcon("validate.png"));
    buttonValidate.setToolTipText("Checks the validity of the expressions");
    buttonValidate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = getCurrentExpression();
	CronSchedule cs = new CronSchedule();
	if (cs.isValid(s))
	  GUIHelper.showInformationMessage(
	      GUIHelper.getParentComponent(m_CustomEditor),
	      "Expression '" + s + "' is valid!");
	else
	  GUIHelper.showErrorMessage(
	      GUIHelper.getParentComponent(m_CustomEditor),
	      "Expression '" + s + "' is not valid!");
      }
    });
    panelButtons.add(buttonValidate);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBottom.add(panelButtons, BorderLayout.EAST);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = getCurrentExpression();
	if (((CronSchedule) getValue()).isValid(s) && !s.equals(((BaseObject) getValue()).getValue()))
	  setValue(new CronSchedule(s));
	closeDialog(APPROVE_OPTION);
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
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
      setValue(new CronSchedule(value));
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
    return ((CronSchedule) getValue()).isValid(value);
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    String	value;
    String	expr;
    String[]	parts;

    super.initForDisplay();

    value = "" + getValue();
    if (!getCurrentExpression().equals(value)) {
      try {
	expr = new CronExpression(value).toString();
      }
      catch (Exception e) {
	expr = CronSchedule.DEFAULT;
      }
      parts = expr.toString().split(" ");
      if (parts.length > 0)
	m_TextSeconds.setText(parts[0]);
      if (parts.length > 1)
	m_TextMinutes.setText(parts[1]);
      if (parts.length > 2)
	m_TextHours.setText(parts[2]);
      if (parts.length > 3)
	m_TextDayOfMonth.setText(parts[3]);
      if (parts.length > 4)
	m_TextMonth.setText(parts[4]);
      if (parts.length > 5)
	m_TextDayOfWeek.setText(parts[5]);
      if (parts.length > 6)
	m_TextYear.setText(parts[6]);
    }
  }
}
