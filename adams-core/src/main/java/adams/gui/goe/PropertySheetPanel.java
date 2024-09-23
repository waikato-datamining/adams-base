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
 *    PropertySheet.java
 *    Copyright (C) 1999-2024 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.ExampleProvider;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.IntrospectionHelper.IntrospectionContainer;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractNumericOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;
import adams.core.option.UserMode;
import adams.core.option.UserModeSupporter;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BaseHtmlEditorPane;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;
import adams.gui.help.AbstractHelpGenerator;
import adams.gui.help.HelpContainer;
import adams.gui.help.HelpFrame;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.Beans;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Displays a property sheet where (supported) properties of the target
 * object may be edited.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertySheetPanel
  extends BasePanel
  implements PropertyChangeListener, UserModeSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -557854258929870536L;

  /** whether to show an extra button for variable popup menu. */
  protected static Boolean SHOW_VARIABLE_POPUP_BUTTON;

  /** The target object being edited. */
  protected Object m_Target;

  /** Holds the options of this object, if it implements OptionHandler. */
  protected List<AbstractOption> m_Options;

  /** Holds properties of the target. */
  protected PropertyDescriptor[] m_Properties;

  /** Holds the methods of the target. */
  protected MethodDescriptor[] m_Methods;

  /** Holds property editors of the object. */
  protected PropertyEditor[] m_Editors;

  /** Holds current object values for each property. */
  protected Object[] m_Values;

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;

  /** the panel with the content. */
  protected BaseSplitPane m_SplitPaneContent;

  /** the editor pane. */
  protected BaseHtmlEditorPane m_PanelHelp;

  /** the panel for the parameters. */
  protected ParameterPanel m_ParameterPanel;

  /** Stores GUI components containing each editing component. */
  protected JComponent[] m_Views;

  /** the buttons for the variable popup menu. */
  protected BaseFlatButton[] m_VarButtons;

  /** The tool tip text for each property. */
  protected String[] m_TipTexts;

  /** the text from the globalInfo method, if any. */
  protected String m_GlobalInfo;

  /** The panel holding global info and help, if provided by
      the object being editied. */
  protected BaseTextAreaWithButtons m_PanelAbout;

  /** A support object for handling property change listeners. */
  protected PropertyChangeSupport m_Support;

  /** whether to show/suppress the about box. */
  protected boolean m_ShowAboutBox;

  /** the user mode to use. */
  protected UserMode m_UserMode;

  /**
   * Default constructor.
   */
  public PropertySheetPanel(UserMode userMode) {
    super();
    m_UserMode = userMode;
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ShowAboutBox = true;
    m_UserMode     = UserMode.LOWEST;
    m_Support      = new PropertyChangeSupport(this);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    m_SplitPaneContent = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPaneContent.setDividerLocation(100);
    m_SplitPaneContent.setUISettingsParameters(getClass(), "GlobalInfoPropertiesDivider");

    m_PanelAbout = new BaseTextAreaWithButtons();
    m_PanelAbout.setEditable(false);
    m_PanelAbout.setLineWrap(true);
    m_PanelAbout.setWrapStyleWord(true);
    m_PanelAbout.setPreferredSize(new Dimension(20, 20));
    m_SplitPaneContent.setTopComponent(m_PanelAbout);

    m_PanelHelp = new BaseHtmlEditorPane();
    m_PanelHelp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_PanelHelp.setFont(Fonts.getMonospacedFont());
    m_PanelHelp.addHyperlinkListener(new HelpFrame.HelpHyperlinkListener());
    m_PanelHelp.setPreferredSize(new Dimension(200, 20));
    m_PanelHelp.setFont(Fonts.getMonospacedFont());

    m_TabbedPane = new BaseTabbedPane(BaseTabbedPane.BOTTOM);
    m_TabbedPane.addTab("Options", m_SplitPaneContent);
    m_TabbedPane.addTab("Help", new BaseScrollPane(m_PanelHelp));
    add(m_TabbedPane, BorderLayout.CENTER);
  }

  /**
   * Sets whether to show the about-box or not.
   * Must happen before calling {@link #setTarget(Object)}.
   *
   * @param value	true if to show
   */
  public void setShowAboutBox(boolean value) {
    m_ShowAboutBox = value;
  }

  /**
   * Returns whether the about box is displayed.
   *
   * @return		true if shown
   */
  public boolean getShowAboutBox() {
    return m_ShowAboutBox;
  }

  /**
   * Return the panel containing global info and help for
   * the object being edited. May return null if the edited
   * object provides no global info or tip text.
   *
   * @return the about-panel.
   */
  public JPanel getAboutPanel() {
    return m_PanelAbout;
  }

  /**
   * Returns the underlying PropertyChangeSupport object.
   *
   * @return		the support object
   */
  public PropertyChangeSupport getPropertyChangeSupport() {
    return m_Support;
  }

  /**
   * Updates the property sheet panel with a changed property and also passed
   * the event along.
   *
   * @param evt a value of type 'PropertyChangeEvent'
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    wasModified(evt); // Let our panel update before guys downstream
    m_Support.firePropertyChange("", null, null);
  }

  /**
   * Adds a PropertyChangeListener.
   *
   * @param l a value of type 'PropertyChangeListener'
   */
  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    if (m_Support != null)
      m_Support.addPropertyChangeListener(l);
  }

  /**
   * Removes a PropertyChangeListener.
   *
   * @param l a value of type 'PropertyChangeListener'
   */
  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    if (m_Support != null)
      m_Support.removePropertyChangeListener(l);
  }

  /**
   * Extracts the first sentence from the given text.
   *
   * @param text	the text to process
   * @param indicator	whether to add an indicator that more text is available
   * @return		the first sentence
   */
  protected String extractFirstSentence(String text, boolean indicator) {
    String	result;
    int		pos;
    int		newPos;

    pos = text.length() - 1;

    newPos = text.indexOf(". ");
    if ((newPos > -1) && (newPos < pos))
      pos = newPos;

    newPos = text.indexOf(".\n");
    if ((newPos > -1) && (newPos < pos))
      pos = newPos;

    result = text.substring(0, pos + 1);
    if (indicator && (result.length() < text.length()))
      result += " ...";

    return result;
  }

  /**
   * Initializes the options/methods to display.
   *
   * @see		#m_Options
   * @see		#m_Methods
   */
  protected void initSheet() {
    IntrospectionContainer  cont;

    m_Options    = null;
    m_Properties = new PropertyDescriptor[0];
    m_Methods    = new MethodDescriptor[0];
    try {
      cont = IntrospectionHelper.introspect(m_Target, m_UserMode);
      if (cont.options != null)
	m_Options = new ArrayList<>(Arrays.asList(cont.options));
      m_Properties = cont.properties;
      m_Methods    = cont.methods;
    }
    catch (Exception ex) {
      printException("Couldn't introspect!", ex);
    }

    m_Editors    = new PropertyEditor[m_Properties.length];
    m_Values     = new Object[m_Properties.length];
    m_Views      = new JComponent[m_Properties.length];
    m_TipTexts   = new String[m_Properties.length];
    m_VarButtons = new BaseFlatButton[m_Properties.length];
  }

  /**
   * Initializes the help text for the object.
   *
   * @see		#m_GlobalInfo
   * @see		#m_TipTexts
   */
  protected void initHelp() {
    Method 	method;
    int		i;
    int		j;
    String 	name;
    String 	tipName;
    String 	displayName;
    String 	example;

    m_GlobalInfo = null;

    // Look for a globalInfo method that returns a string
    // describing the target
    try {
      method = m_Target.getClass().getMethod("globalInfo");
      if (method != null)
	m_GlobalInfo = (String) method.invoke(m_Target);
    }
    catch (Exception e) {
      // ignored
    }

    for (i = 0; i < m_Editors.length; i++) {
      name    = m_Properties[i].getDisplayName();
      tipName = name + "TipText";
      for (j = 0; j < m_Methods.length; j++) {
	displayName = m_Methods[j].getDisplayName();
	method      = m_Methods[j].getMethod();
	if (displayName.equals(tipName)) {
	  if (method.getReturnType().equals(String.class)) {
	    try {
	      m_TipTexts[i] = ((String) (method.invoke(m_Target))).trim();
              if (m_Options.get(i).getCurrentValue() instanceof ExampleProvider) {
                example = ((ExampleProvider) m_Options.get(i).getCurrentValue()).getExample();
                if (!m_TipTexts[i].contains(example)) {
                  if (m_TipTexts[i].endsWith("."))
                    m_TipTexts[i] = m_TipTexts[i].substring(0, m_TipTexts[i].length() - 1) + ";";
                  m_TipTexts[i] += " " + example;
                }
              }
	    }
	    catch (Exception ex) {
	      // ignored
	    }
	    break;
	  }
	}
      }
    }
  }

  /**
   * Sets a new target object for customisation.
   *
   * @param value a value of type 'Object'
   */
  public synchronized void setTarget(Object value) {
    int 				i;
    String 				name;
    Class 				type;
    Method 				getter;
    Method 				setter;
    Class 				pec;
    JLabel 				empty;
    AbstractGenericObjectEditorHandler	handler;
    boolean				canChangeClass;
    JComponent				view;

    m_Target = value;

    initSheet();
    initHelp();

    m_SplitPaneContent.setTopComponentHidden(true);
    m_SplitPaneContent.setBottomComponentHidden(true);

    JPanel scrollablePanel = new JPanel(new BorderLayout());
    BaseScrollPane scrollPane = new BaseScrollPane(scrollablePanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    m_SplitPaneContent.setBottomComponent(scrollPane);

    m_PanelAbout.setText("");
    if ((m_GlobalInfo != null) && m_ShowAboutBox) {
      m_PanelAbout.setText(m_GlobalInfo);
      m_PanelAbout.setCaretPosition(0);
      m_SplitPaneContent.setTopComponentHidden(false);
    }

    m_ParameterPanel = null;
    for (i = 0; i < m_Properties.length; i++) {
      // Don't display hidden or expert properties.
      if (m_Properties[i].isHidden() || m_Properties[i].isExpert())
	continue;

      name   = m_Properties[i].getDisplayName();
      type   = m_Properties[i].getPropertyType();
      getter = m_Properties[i].getReadMethod();
      setter = m_Properties[i].getWriteMethod();

      // Only display read/write properties.
      if ((getter == null) || (setter == null))
	continue;

      try {
	canChangeClass = true;

	// value
	m_Values[i] = getter.invoke(m_Target);

	// editor
	pec = m_Properties[i].getPropertyEditorClass();
	if (pec != null) {
	  try {
	    m_Editors[i] = (PropertyEditor) pec.getDeclaredConstructor().newInstance();
	  }
	  catch (Exception ex) {
	    // Drop through.
	  }
	}
	if (m_Editors[i] == null)
	  m_Editors[i] = PropertyEditorManager.findEditor(type);

	// for classes implementing OptionHandler, we can always display
	// the GenericObjectEditor
	if ((m_Editors[i] == null) && (m_Target instanceof OptionHandler)) {
	  m_Editors[i]   = new GenericObjectEditor();
	  canChangeClass = false;
	}

	// If we can't edit this component, skip it.
	if (m_Editors[i] == null)
	  continue;

	if (m_Editors[i] instanceof GenericObjectEditor)
	  ((GenericObjectEditor) m_Editors[i]).setUserMode(m_UserMode);

	handler = AbstractGenericObjectEditorHandler.getHandler(m_Editors[i]);
	handler.setClassType(m_Editors[i], type);
	handler.setCanChangeClassInDialog(m_Editors[i], canChangeClass);

	// Don't try to set null values:
	if (m_Values[i] == null)
	  continue;

	m_Editors[i].setValue(m_Values[i]);

	// Now figure out how to display it...
	if ((m_Editors[i] instanceof InlineEditorSupport) && ((InlineEditorSupport) m_Editors[i]).isInlineEditingAvailable()) {
	  m_Views[i]   = new InlineEditor(m_Editors[i], this);
	  m_Editors[i] = (PropertyEditor) m_Views[i];
	}
	else {
	  view = EditorHelper.findView(m_Editors[i]);
	  if (view != null) {
	    m_Views[i] = view;
	  }
	  else {
	    printErrorMessage("Warning: Property \"" + name
		+ "\" has non-displayabale editor.  Skipping.");
	    continue;
	  }
	}

	// set sensible dimensions for widgets
	if (m_Views[i] instanceof JSpinner) {
	  m_Views[i].setSize(new Dimension(100, GUIHelper.getInteger("GOESpinnerHeight", 20)));
	  m_Views[i].setPreferredSize(new Dimension(100, GUIHelper.getInteger("GOESpinnerHeight", 20)));
	  m_Views[i].setMaximumSize(new Dimension(100, GUIHelper.getInteger("GOESpinnerHeight", 20) * 2));
	}
	else if (m_Views[i] instanceof JCheckBox) {
	  m_Views[i].setSize(new Dimension(25, GUIHelper.getInteger("GOECheckBoxHeight", 20)));
	  m_Views[i].setPreferredSize(new Dimension(25, GUIHelper.getInteger("GOECheckBoxHeight", 20)));
	  m_Views[i].setMaximumSize(new Dimension(25, (int) (GUIHelper.getInteger("GOECheckBoxHeight", 20) * 1.2)));
	}
	else {
	  m_Views[i].setSize(new Dimension(300, GUIHelper.getInteger("GOEDefaultHeight", 20)));
	  m_Views[i].setPreferredSize(new Dimension(300, GUIHelper.getInteger("GOEDefaultHeight", 20)));
	  m_Views[i].setMaximumSize(new Dimension(300, GUIHelper.getInteger("GOEDefaultHeight", 20) * 2));
	}

	m_Editors[i].addPropertyChangeListener(this);
      }
      catch (Exception ex) {
	printException("Skipping property " + name + " (" + m_Target.getClass().getName() + ")!", ex);
	continue;
      }

      if (m_ParameterPanel == null) {
	m_ParameterPanel = new ParameterPanel(3, 1);
	scrollablePanel.add(m_ParameterPanel, BorderLayout.CENTER);
      }

      final PropertyEditor editor = m_Editors[i];
      final AbstractOption option;
      if (m_Options != null)
	option = m_Options.get(i);
      else
	option = null;

      if ((m_Options != null) && getShowVariablePopupButton()) {
	final BaseFlatButton buttonVars;
	if ((option instanceof AbstractArgumentOption) && ((AbstractArgumentOption) option).isVariableAttached())
	  buttonVars = new BaseFlatButton(ImageManager.getIcon("variable_present.gif"));
	else
	  buttonVars = new BaseFlatButton(ImageManager.getIcon("variable_notpresent.gif"));
	buttonVars.setFocusable(false);
	buttonVars.addActionListener((ActionEvent e) -> {
	  BasePopupMenu menu = new BasePopupMenu();
	  VariableSupport.updatePopup(PropertySheetPanel.this, editor, menu);
	  menu.show(buttonVars, 0, buttonVars.getHeight());
	});
	JPanel panelView = new JPanel(new BorderLayout(0, 0));
	panelView.add(buttonVars, BorderLayout.WEST);
	panelView.add(m_Views[i], BorderLayout.CENTER);
	m_ParameterPanel.addParameter(name, m_Views[i], panelView);
	m_VarButtons[i] = buttonVars;
      }
      else {
	m_ParameterPanel.addParameter(name, m_Views[i]);
      }

      final JLabel label = m_ParameterPanel.getLabel(m_ParameterPanel.getParameterCount() - 1);
      label.addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent evt) {
	  if (MouseUtils.isRightClick(evt)) {
	    evt.consume();
	    BasePopupMenu menu = new BasePopupMenu();
	    VariableSupport.updatePopup(PropertySheetPanel.this, editor, menu);
	    menu.showAbsolute(label, evt);
	  }
	  else {
	    super.mouseClicked(evt);
	  }
	}
      });
      if (m_Options != null) {
	label.setToolTipText("Command-line option: -" + m_Options.get(i).getCommandline());
	if (m_Options.get(i) instanceof AbstractArgumentOption) {
	  VariableSupport.updateLabel(label, ((AbstractArgumentOption) m_Options.get(i)).getVariableName());
	  if (m_Editors[i] instanceof AbstractNumberEditor) {
	    ((AbstractNumberEditor) m_Editors[i]).setDefaultValue((Number) m_Options.get(i).getDefaultValue());
	    ((AbstractNumberEditor) m_Editors[i]).setLowerBound(((AbstractNumericOption) m_Options.get(i)).getLowerBound());
	    ((AbstractNumberEditor) m_Editors[i]).setUpperBound(((AbstractNumericOption) m_Options.get(i)).getUpperBound());
	  }
	}
      }
      if (m_TipTexts[i] != null)
	m_Views[i].setToolTipText(GUIHelper.processTipText(m_TipTexts[i], GUIHelper.getMaxTooltipWidth()));
    }

    if (m_ParameterPanel == null) {
      empty = new JLabel("No editable properties", SwingConstants.CENTER);
      scrollablePanel.add(empty);
    }

    m_SplitPaneContent.validate();
    m_SplitPaneContent.setBottomComponentHidden(false);
    updateHelpPanel();

    // tabbedpane doesn't properly update itself, need to switch between pages to achieve that...
    if (m_TabbedPane.getSelectedIndex() == 1) {
      m_TabbedPane.setSelectedIndex(0);
      m_TabbedPane.setSelectedIndex(1);
    }
  }

  /**
   * Returns the current target object.
   *
   * @return		the target
   */
  public Object getTarget() {
    return m_Target;
  }

  /**
   * Sets the user mode to use for displaying the properties.
   *
   * @param value	the mode
   */
  @Override
  public void setUserMode(UserMode value) {
    if (value != m_UserMode) {
      m_UserMode = value;
      if (getTarget() != null)
	setTarget(getTarget());
    }
  }

  /**
   * Returns the user mode to use for displaying the properties.
   *
   * @return		the mode
   */
  @Override
  public UserMode getUserMode() {
    return m_UserMode;
  }

  /**
   * Updates the content in the help panel.
   */
  protected void updateHelpPanel() {
    HelpContainer 	cont;

    cont = AbstractHelpGenerator.generateHelp(getTarget(), m_UserMode);
    m_PanelHelp.setContentType(cont.isHtml() ? "text/html" : "text/plain");
    m_PanelHelp.setText(cont.getHelp());
    m_PanelHelp.setCaretPosition(0);
  }

  /**
   * Gets the number of editable properties for the current target.
   *
   * @return the number of editable properties.
   */
  public int editableProperties() {
    if (m_ParameterPanel == null)
      return 0;
    else
      return m_ParameterPanel.getParameterCount();
  }

  /**
   * Updates the propertysheet when a value has been changed (from outside
   * the propertysheet?).
   *
   * @param evt a value of type 'PropertyChangeEvent'
   */
  synchronized void wasModified(PropertyChangeEvent evt) {
    PropertyEditor 	editor;
    int			i;
    PropertyDescriptor 	property;
    Object 		value;
    Method 		setter;
    Method 		getter;
    String 		message;
    Component 		jf;
    Object 		o;

    if (evt.getSource() instanceof PropertyEditor) {
      editor = (PropertyEditor) evt.getSource();
      for (i = 0 ; i < m_Editors.length; i++) {
	if (m_Editors[i] == editor) {
	  property    = m_Properties[i];
	  value       = editor.getValue();
	  m_Values[i] = value;
	  setter      = property.getWriteMethod();
	  try {
	    setter.invoke(m_Target, value);
	  }
	  catch (InvocationTargetException ex) {
	    jf = null;
	    if (evt.getSource() instanceof JPanel)
	      jf = ((JPanel) evt.getSource()).getParent();

	    if (ex.getTargetException() instanceof PropertyVetoException) {
	      message = "WARNING: Vetoed; reason is: " + ex.getTargetException().getMessage();
	      printErrorMessage(message);
	      GUIHelper.showErrorMessage(jf, message, "Error");
	    }
	    else {
	      printException(ex.getTargetException().getClass().getName() + " while updating "+ property.getName() +":", ex);
	      GUIHelper.showErrorMessage(jf,
		ex.getTargetException().getClass().getName() + " while updating " + property.getName() + ":\n" +
		  LoggingHelper.throwableToString(ex), "Error");
	    }
	  }
	  catch (Exception ex) {
	    printException("Unexpected exception while updating " + property.getName(), ex);
	  }
	  if (m_Views[i] != null && m_Views[i] instanceof PropertyPanel) {
	    m_Views[i].repaint();
	    revalidate();
	  }
	  break;
	}
      }
    }

    // Now re-read all the properties and update the editors
    // for any other properties that have changed.
    for (i = 0; i < m_Properties.length; i++) {
      try {
	getter = m_Properties[i].getReadMethod();
	setter = m_Properties[i].getWriteMethod();

	// ignore set/get only properties
	if (getter == null || setter == null)
	  continue;

	o = getter.invoke(m_Target);
      }
      catch (Exception ex) {
	o = null;
      }

      // The property is equal to its old value.
      if (Objects.equals(o, m_Values[i]))
	continue;

      m_Values[i] = o;

      // Make sure we have an editor for this property...
      if (m_Editors[i] == null)
	continue;

      // The property has changed!  Update the editor.
      m_Editors[i].removePropertyChangeListener(this);
      m_Editors[i].setValue(o);
      m_Editors[i].addPropertyChangeListener(this);
      if (m_Views[i] != null)
	m_Views[i].repaint();
    }

    // Make sure the target bean gets repainted.
    if (Beans.isInstanceOf(m_Target, Component.class))
      ((Component)(Beans.getInstanceOf(m_Target, Component.class))).repaint();
  }

  /**
   * Determines the index of the editor in this sheet panel.
   *
   * @param editor	the editor to look for
   * @return		the index, or -1 if not found
   */
  protected int findEditor(PropertyEditor editor) {
    int		result;
    int		i;

    result = -1;

    if (m_Options != null) {
      for (i = 0; i < m_Editors.length; i++) {
	if (m_Editors[i] == editor) {
	  result = i;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Tries to find the option for the specified editor.
   *
   * @param editor	the editor to find the option for
   * @return		the option, or null if none found
   */
  public AbstractOption findOption(PropertyEditor editor) {
    AbstractOption	result;
    int			index;

    index = findEditor(editor);
    if (index > -1)
      result = m_Options.get(index);
    else
      result = null;

    return result;
  }

  /**
   * Tries to find the JComponent for the specified editor.
   *
   * @param editor	the editor to find the option for
   * @return		the GUI component, or null if none found
   */
  public JComponent findView(PropertyEditor editor) {
    JComponent	result;
    int		index;

    index = findEditor(editor);
    if (index > -1)
      result = m_Views[index];
    else
      result = null;

    return result;
  }

  /**
   * Tries to find the label for the specified editor.
   *
   * @param editor	the editor to find the option for
   * @return		the label, or null if none found
   */
  public JLabel findLabel(PropertyEditor editor) {
    JLabel	result;
    int		index;

    index = findEditor(editor);
    if (index > -1)
      result = m_ParameterPanel.getLabel(index);
    else
      result = null;

    return result;
  }

  /**
   * Returns whether variable popup button should be displayed.
   *
   * @return		true if button displayed
   */
  public static synchronized boolean getShowVariablePopupButton() {
    if (SHOW_VARIABLE_POPUP_BUTTON == null)
      SHOW_VARIABLE_POPUP_BUTTON = GUIHelper.getBoolean("GenericObjectEditorShowVariablePopupMenuButton", false);
    return SHOW_VARIABLE_POPUP_BUTTON;
  }

  /**
   * Tries to find the variable button for the specified editor.
   *
   * @param editor	the editor to find the option for
   * @return		the button, or null if none found
   */
  public BaseFlatButton findVarButton(PropertyEditor editor) {
    BaseFlatButton	result;
    int			index;

    if (!getShowVariablePopupButton())
      return null;

    index = findEditor(editor);
    if (index > -1)
      result = m_VarButtons[index];
    else
      result = null;

    return result;
  }
}
