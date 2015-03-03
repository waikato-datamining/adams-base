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
 * WekaPropertySheetPanelPage.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import weka.filters.supervised.attribute.AddClassification;
import weka.gui.PropertySheetPanel;
import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Wizard page that use a {@link PropertySheetPanel} for displaying
 * the properties of an object.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPropertySheetPanelPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** the identifier for the commandline of the object. */
  public final static String PROPERTY_CMDLINE = "Commandline";
  
  /**
   * Allowing better access to property sheet panel.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class CustomPropertySheetPanel
    extends PropertySheetPanel {

    /** for serialization. */
    private static final long serialVersionUID = 1083770659191190864L;

    /**
     * Sets whether to show the about box or not.
     * Must happen before calling {@link #setTarget(Object)}.
     * 
     * @param value	true if to show
     */
    public void setShowAboutBox(boolean value) {
      if (getAboutPanel() != null)
	getAboutPanel().setVisible(value);
    }
    
    /**
     * Returns whether the about box is displayed.
     * 
     * @return		true if shown
     */
    public boolean getShowAboutBox() {
      if (getAboutPanel() == null)
	return true;
      else
	return getAboutPanel().isVisible();
    }
  }
  
  /** the parameter panel for displaying the parameters. */
  protected CustomPropertySheetPanel m_PanelSheet;
  
  /** the current target. */
  protected transient Object m_Target;
  
  /**
   * Default constructor.
   */
  public WekaPropertySheetPanelPage() {
    super();
  }
  
  /**
   * Initializes the page with the given page name.
   * 
   * @param pageName	the page name to use
   */
  public WekaPropertySheetPanelPage(String pageName) {
    this();
    setPageName(pageName);
  }
  
  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_PanelSheet = new CustomPropertySheetPanel();
    add(m_PanelSheet, BorderLayout.CENTER);
  }
  
  /**
   * Returns the underlying property sheet panel.
   * 
   * @return		the property sheet panel
   */
  public PropertySheetPanel getParameterPanel() {
    return m_PanelSheet;
  }
  
  /**
   * Sets the object to display the properties for.
   * 
   * @param value	the object
   */
  public void setTarget(Object value) {
    m_Target = value;
    m_PanelSheet.setTarget(m_Target);
    m_PanelSheet.setShowAboutBox(false);
  }
  
  /**
   * Returns the current object.
   * 
   * @return		the object
   */
  public Object getTarget() {
    return m_Target;
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    Properties	result;
    
    result = new Properties();
    result.setProperty(PROPERTY_CMDLINE, OptionUtils.getCommandLine(m_Target));
    
    return result;
  }
  
  /**
   * For testing only.
   * 
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    final WizardPane wizard = new WizardPane();
    wizard.addPage(new StartPage());
    ParameterPanelPage page = new ParameterPanelPage();
    page.setPageName("Parameters 1");
    page.setDescription("Here is the description of the first parameter page.\n\nAnother line.");
    page.getParameterPanel().addPropertyType("doublevalue", PropertyType.DOUBLE);
    page.getParameterPanel().setLabel("doublevalue", "Double value");
    page.getParameterPanel().addPropertyType("booleanvalue", PropertyType.BOOLEAN);
    page.getParameterPanel().setLabel("booleanvalue", "Boolean value");
    page.getParameterPanel().addPropertyType("stringlist", PropertyType.LIST);
    page.getParameterPanel().setLabel("stringlist", "String list");
    page.getParameterPanel().setList("stringlist", new String[]{"A", "B", "C"});
    Properties props = new Properties();
    props.setDouble("doublevalue", 1.234);
    props.setBoolean("booleanvalue", true);
    props.setProperty("stringlist", "B");
    page.setProperties(props);
    page.setDescription("Here is the description of the first parameter page.\n\nAnother line.");
    wizard.addPage(page);
    page = new ParameterPanelPage();
    page.setDescription("<html><h3>More parameters</h3>Here is the description of the 2nd parameter page.<br><br>Another line.");
    page.setPageName("Parameters 2");
    wizard.addPage(page);
    page = new ParameterPanelPage();
    page.setPageName("Parameters 3");
    page.setDescription("Nothing here.");
    wizard.addPage(page);
    WekaPropertySheetPanelPage shpage = new WekaPropertySheetPanelPage("Filter");
    shpage.setDescription("<html><h3>Object properties</h3>Here you can change all properties of the " + AddClassification.class.getName() + " filter.");
    shpage.setTarget(new AddClassification());
    wizard.addPage(shpage);
    wizard.addPage(new FinalPage());
    final BaseFrame frame = new BaseFrame("Example Wizard");
    wizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	System.out.println(e.getActionCommand());
	System.out.println(wizard.getProperties(true));
	frame.setVisible(false);
	frame.dispose();
      }
    });
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(wizard, BorderLayout.CENTER);
    frame.setSize(800, 600);
    frame.setVisible(true);
  }
}
