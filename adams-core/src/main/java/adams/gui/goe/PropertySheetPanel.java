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
 *    Copyright (C) 1999-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.AdditionalInformationHandler;
import adams.core.Utils;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.IntrospectionHelper.IntrospectionContainer;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractNumericOption;
import adams.core.option.AbstractOption;
import adams.core.option.HtmlHelpProducer;
import adams.core.option.OptionHandler;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.Introspector;
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

/**
 * Displays a property sheet where (supported) properties of the target
 * object may be edited.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 * @see weka.gui.PropertySheetPanel
 */
public class PropertySheetPanel extends BasePanel
  implements PropertyChangeListener {

  /** for serialization. */
  static final long serialVersionUID = -557854258929870536L;

  /** the maximum characters per line for a tool tip. */
  public final static int MAX_TOOLTIP_WIDTH = 40;

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

  /** the panel for the parameters. */
  protected ParameterPanel m_ParameterPanel;

  /** Stores GUI components containing each editing component. */
  protected JComponent[] m_Views;

  /** The tool tip text for each property. */
  protected String[] m_TipTexts;

  /** StringBuilder containing help text for the object being edited. */
  protected StringBuilder m_HelpText;

  /** StringBuilder containing help text for the object being edited in HTML format. */
  protected StringBuilder m_HelpTextHtml;

  /** the text from the globalInfo method, if any. */
  protected String m_GlobalInfo;

  /** Help frame. */
  protected GenericObjectEditorHelpDialog m_DialogHelp;

  /** Button to pop up the full help text in a separate frame. */
  protected JButton m_ButtonHelp;

  /** The panel holding global info and help, if provided by
      the object being editied. */
  protected BaseTextAreaWithButtons m_PanelAbout;

  /** A support object for handling property change listeners. */
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);

  /** whether to suppress the about box. */
  protected boolean m_ShowAboutBox;

  /**
   * Creates the property sheet panel.
   */
  public PropertySheetPanel() {
    setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    m_ShowAboutBox = true;
  }

  /**
   * Sets whether to show the about box or not.
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
   * @return the about panel.
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
    m_Support.addPropertyChangeListener(l);
  }

  /**
   * Removes a PropertyChangeListener.
   *
   * @param l a value of type 'PropertyChangeListener'
   */
  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
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
      cont         = IntrospectionHelper.introspect(m_Target);
      if (cont.options != null)
	m_Options = new ArrayList<>(Arrays.asList(cont.options));
      m_Properties = cont.properties;
      m_Methods    = cont.methods;
    }
    catch (Exception ex) {
      printException("Couldn't introspect!", ex);
    }

    m_Editors  = new PropertyEditor[m_Properties.length];
    m_Values   = new Object[m_Properties.length];
    m_Views    = new JComponent[m_Properties.length];
    m_TipTexts = new String[m_Properties.length];
  }

  /**
   * Initializes the help text for the object.
   *
   * @see		#m_HelpText
   * @see		#m_HelpTextHtml
   * @see		#m_GlobalInfo
   * @see		#m_TipTexts
   */
  protected void initHelp() {
    Method 	method;
    boolean 	firstTip;
    String 	className;
    String 	addInfo;
    int		i;
    int		j;
    String 	name;
    String 	tipName;
    String 	mname;
    String 	commandline;
    String 	tipText;

    m_HelpText     = null;
    m_HelpTextHtml = null;
    m_GlobalInfo   = null;
    if (m_Target instanceof OptionHandler) {
      m_HelpTextHtml = new StringBuilder();
      HtmlHelpProducer producer = new HtmlHelpProducer();
      producer.produce((OptionHandler) m_Target);
      m_HelpTextHtml.append(producer.toString());
    }
    else {
      m_HelpText = new StringBuilder();
    }

    // Look for a globalInfo method that returns a string
    // describing the target
    try {
      method = m_Target.getClass().getMethod("globalInfo", new Class[0]);
      if (method != null) {
	m_GlobalInfo = (String) method.invoke(m_Target, new Object[0]);
	className    = m_Target.getClass().getName();

	m_HelpText.append("NAME\n");
	m_HelpText.append(className).append("\n\n");
	m_HelpText.append("SYNOPSIS\n").append(m_GlobalInfo).append("\n\n");

	if (m_Target instanceof AdditionalInformationHandler) {
	  addInfo = ((AdditionalInformationHandler) m_Target).getAdditionalInformation();
	  if ((addInfo != null) && (addInfo.length() > 0)) {
	    m_HelpText.append("ADDITIONAL INFORMATION\n");
	    m_HelpText.append(addInfo + "\n\n");
	  }
	}
      }
    }
    catch (Exception e) {
      // ignored
    }

    firstTip = true;
    for (i = 0; i < m_Editors.length; i++) {
      name    = m_Properties[i].getDisplayName();
      tipName = name + "TipText";
      for (j = 0; j < m_Methods.length; j++) {
	mname       = m_Methods[j].getDisplayName();
	method      = m_Methods[j].getMethod();
	commandline = null;
	if (mname.equals(tipName)) {
	  if (method.getReturnType().equals(String.class)) {
	    if (m_Options != null)
	      commandline = m_Options.get(i).getCommandline();
	    try {
	      tipText       = (String) (method.invoke(m_Target, new Object[0]));
	      m_TipTexts[i] = extractFirstSentence(tipText, true);
	      if (m_HelpText != null) {
		if (firstTip) {
		  m_HelpText.append("OPTIONS\n");
		  firstTip = false;
		}
		m_HelpText.append(name);
		if (commandline != null)
		  m_HelpText.append("/-" + commandline);
		m_HelpText.append(":\n");
		m_HelpText.append(tipText).append("\n\n");
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
   * @param targ a value of type 'Object'
   */
  public synchronized void setTarget(Object targ) {
    String 				summary;
    int 				i;
    String 				name;
    Class 				type;
    Method 				getter;
    Method 				setter;
    Class 				pec;
    JLabel 				empty;
    AbstractGenericObjectEditorHandler	handler;
    boolean				canChangeClass;
    Dimension				dim;
    JComponent				view;

    m_Target = targ;

    initSheet();
    initHelp();

    // Close any child windows at this point
    removeAll();

    setLayout(new BorderLayout());
    JPanel scrollablePanel = new JPanel(new BorderLayout());
    BaseScrollPane scrollPane = new BaseScrollPane(scrollablePanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    add(scrollPane, BorderLayout.CENTER);

    setVisible(false);

    if (m_GlobalInfo != null) {
      summary = extractFirstSentence(m_GlobalInfo, true);
      m_ButtonHelp = new JButton(GUIHelper.getIcon("help.gif"));
      m_ButtonHelp.setToolTipText("Help on " + m_Target.getClass().getName());
      m_ButtonHelp.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent a) {
	  openHelpDialog();
	  m_ButtonHelp.setEnabled(false);
	}
      });

      m_PanelAbout = new BaseTextAreaWithButtons(summary);
      m_PanelAbout.setColumns(30);
      m_PanelAbout.setEditable(false);
      m_PanelAbout.setLineWrap(true);
      m_PanelAbout.setWrapStyleWord(true);
      m_PanelAbout.setTextFont(new Font("SansSerif", Font.PLAIN,12));
      m_PanelAbout.getScrollPane().setBorder(null);
      m_PanelAbout.getComponent().setBackground(getBackground());
      m_PanelAbout.setBorder(BorderFactory.createCompoundBorder(
	  BorderFactory.createTitledBorder("About"),
	  BorderFactory.createEmptyBorder(5, 5, 5, 5)));
      m_PanelAbout.addToButtonsPanel(m_ButtonHelp);
      if (m_ShowAboutBox)
	add(m_PanelAbout, BorderLayout.NORTH);
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
      if (getter == null || setter == null)
	continue;

      try {
	canChangeClass = true;

	// value
	m_Values[i] = getter.invoke(m_Target, new Object[0]);

	// editor
	pec = m_Properties[i].getPropertyEditorClass();
	if (pec != null) {
	  try {
	    m_Editors[i] = (PropertyEditor) pec.newInstance();
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
	  m_Views[i].setSize(new Dimension(100, 20));
	  m_Views[i].setPreferredSize(new Dimension(100, 20));
	  m_Views[i].setMaximumSize(new Dimension(100, 40));
	}
	else if (m_Views[i] instanceof JCheckBox) {
	  m_Views[i].setSize(new Dimension(25, 20));
	  m_Views[i].setPreferredSize(new Dimension(25, 20));
	  m_Views[i].setMaximumSize(new Dimension(25, 25));
	}
	else {
	  m_Views[i].setSize(new Dimension(300, 20));
	  m_Views[i].setPreferredSize(new Dimension(300, 20));
	  m_Views[i].setMaximumSize(new Dimension(300, 40));
	}

	m_Editors[i].addPropertyChangeListener(this);
      }
      catch (InvocationTargetException ex) {
	printException("Skipping property " + name + " (" + m_Target.getClass().getName() + ")!", ex);
	continue;
      }
      catch (Exception ex) {
	printException("Skipping property " + name + " (" + m_Target.getClass().getName() + ")!", ex);
	continue;
      }

      if (m_ParameterPanel == null) {
	m_ParameterPanel = new ParameterPanel(3, 1);
	scrollablePanel.add(m_ParameterPanel, BorderLayout.CENTER);
      }

      m_ParameterPanel.addParameter(name, m_Views[i]);
      m_ParameterPanel.getLabel(m_ParameterPanel.getParameterCount() - 1);
      final JLabel label = m_ParameterPanel.getLabel(m_ParameterPanel.getParameterCount() - 1);
      final PropertyEditor editor = m_Editors[i];
      final AbstractOption option;
      if (m_Options != null)
	option = m_Options.get(i);
      else
	option = null;
      label.addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent evt) {
	  if (MouseUtils.isRightClick(evt)) {
	    evt.consume();
	    JPopupMenu menu = new JPopupMenu();
	    VariableSupport.updatePopup(PropertySheetPanel.this, editor, menu);
	    // revert to default menu item
	    if (option != null) {
	      JMenuItem menuitem = new JMenuItem("Use default", GUIHelper.getIcon("undo.gif"));
	      menuitem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		  editor.setValue(option.getDefaultValue());
		  setTarget(m_Target);
		}
	      });
	      menu.addSeparator();
	      menu.add(menuitem);
	    }
	    menu.show(label, evt.getX(), evt.getY());
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
	m_Views[i].setToolTipText(GUIHelper.processTipText(m_TipTexts[i], MAX_TOOLTIP_WIDTH));
    }

    if (m_ParameterPanel == null) {
      empty = new JLabel("No editable properties", SwingConstants.CENTER);
      scrollablePanel.add(empty);
    }

    // Mnemonics don't seem to work here??
    //setMnemonics();

    validate();

    // sometimes, the calculated dimensions seem to be too small and the
    // scrollbars show up, though there is still plenty of space on the
    // screen. hence we increase the dimensions a bit to fix this.
    dim = scrollablePanel.getPreferredSize();
    dim.height += 5;
    dim.width  += 5;
    scrollPane.setPreferredSize(dim);
    validate();

    setVisible(true);
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
   * Sets the mnemonics of the labels.
   */
  protected void setMnemonics() {
    String[]	labels;
    char[]	mnemonics;
    int		i;

    labels = new String[editableProperties()];
    for (i = 0; i < editableProperties(); i++)
      labels[i] = m_ParameterPanel.getLabel(i).getText();

    mnemonics = GUIHelper.getMnemonics(labels);
    for (i = 0; i < editableProperties(); i++)
      m_ParameterPanel.getLabel(i).setDisplayedMnemonic(mnemonics[i]);
  }

  /**
   * opens the help frame.
   */
  protected void openHelpDialog() {
    boolean 	isHtml;

    initHelp();
    isHtml = (m_HelpTextHtml != null);
    if (GUIHelper.getParentDialog(this) != null)
      m_DialogHelp = new GenericObjectEditorHelpDialog(GUIHelper.getParentDialog(this), this);
    else
      m_DialogHelp = new GenericObjectEditorHelpDialog(GUIHelper.getParentFrame(this), this);
    if (isHtml)
      m_DialogHelp.setHelp(m_HelpTextHtml.toString(), true);
    else
      m_DialogHelp.setHelp(m_HelpText.toString(), false);
    m_DialogHelp.setLocation(
	m_PanelAbout.getTopLevelAncestor().getLocationOnScreen().x + m_PanelAbout.getTopLevelAncestor().getSize().width,
	m_PanelAbout.getTopLevelAncestor().getLocationOnScreen().y);
    if (isHtml)
      m_DialogHelp.setSize(800, 600);
    else
      m_DialogHelp.setSize(400, 350);
    m_DialogHelp.setVisible(true);
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
    if (evt.getSource() instanceof PropertyEditor) {
      PropertyEditor editor = (PropertyEditor) evt.getSource();
      for (int i = 0 ; i < m_Editors.length; i++) {
	if (m_Editors[i] == editor) {
	  PropertyDescriptor property = m_Properties[i];
	  Object value = editor.getValue();
	  m_Values[i] = value;
	  Method setter = property.getWriteMethod();
	  try {
	    Object args[] = { value };
	    args[0] = value;
	    setter.invoke(m_Target, args);
	  }
	  catch (InvocationTargetException ex) {
	    if (ex.getTargetException()
		instanceof PropertyVetoException) {
	      String message = "WARNING: Vetoed; reason is: "
		+ ex.getTargetException().getMessage();
	      printErrorMessage(message);

	      Component jf;
	      if(evt.getSource() instanceof JPanel)
		jf = ((JPanel)evt.getSource()).getParent();
	      else
		jf = new JFrame();
	      GUIHelper.showErrorMessage(jf, message, "Error");
	      if(jf instanceof JFrame)
		((JFrame)jf).dispose();
	    }
	    else {
	      printException(ex.getTargetException().getClass().getName()+
		  " while updating "+ property.getName() +":", ex);
	      Component jf;
	      if(evt.getSource() instanceof JPanel)
		jf = ((JPanel)evt.getSource()).getParent();
	      else
		jf = new JFrame();
	      GUIHelper.showErrorMessage(jf,
                ex.getTargetException().getClass().getName() +
                  " while updating " + property.getName() +
                  ":\n" +
                  Utils.throwableToString(ex),
		  "Error");
	      if (jf instanceof JFrame)
		((JFrame)jf).dispose();
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
    for (int i = 0; i < m_Properties.length; i++) {
      Object o;
      try {
	Method getter = m_Properties[i].getReadMethod();
	Method setter = m_Properties[i].getWriteMethod();

	// ignore set/get only properties
	if (getter == null || setter == null)
	  continue;

	Object args[] = { };
	o = getter.invoke(m_Target, args);
      }
      catch (Exception ex) {
	o = null;
      }

      // The property is equal to its old value.
      if (o == m_Values[i] || (o != null && o.equals(m_Values[i])))
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
    };

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
   * Returns the help button in use.
   *
   * @return		the help button
   */
  public JButton getHelpButton() {
    return m_ButtonHelp;
  }

  /**
   * Returns the help dialog.
   *
   * @return		the help dialog, can be null
   */
  public JDialog getHelpDialog() {
    return m_DialogHelp;
  }
}
