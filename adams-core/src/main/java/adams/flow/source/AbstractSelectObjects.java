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
 * AbstractSelectObjects.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.flow.core.AutomatableInteractiveActor;

/**
 * Ancestor for sources that promp the user to select a nuber of objects to
 * be broadcasted as tokens.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectObjects
  extends AbstractInteractiveArrayProvider 
  implements AutomatableInteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = 8791403891812271704L;

  /** the superclass. */
  protected String m_SuperClass;
  
  /** the initial objects. */
  protected BaseString[] m_InitialObjects;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;

  /** whether to use just the actor name or the full name as title. */
  protected boolean m_ShortTitle;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "super-class", "superClass",
	    getDefaultSuperClass());

    m_OptionManager.add(
	    "initial-objects", "initialObjects",
	    getDefaultInitialObjects());

    m_OptionManager.add(
	    "non-interactive", "nonInteractive",
	    false);

    m_OptionManager.add(
	    "short-title", "shortTitle",
	    false);
  }

  /**
   * Returns the default superclass to use.
   * 
   * @return		the default superclass
   */
  protected String getDefaultSuperClass() {
    return Object.class.getName();
  }
  
  /**
   * Sets the superclass for the class hierarchy to use.
   *
   * @param value	the superclass
   */
  public void setSuperClass(String value) {
    m_SuperClass = value;
    reset();
  }

  /**
   * Returns the superclass for the class hierarchy in use.
   *
   * @return 		the superclass
   */
  public String getSuperClass() {
    return m_SuperClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String superClassTipText() {
    return "The superclass for the class hierarchy.";
  }

  /**
   * Returns the default set of objects (in their commandline representation)
   * to use.
   */
  public BaseString[] getDefaultInitialObjects() {
    return new BaseString[0];
  }
  
  /**
   * Sets the initial objects (commandlines).
   *
   * @param value	the initial objects
   */
  public void setInitialObjects(BaseString[] value) {
    m_InitialObjects = value;
    reset();
  }

  /**
   * Returns the initial objects.
   *
   * @return 		the initial objects
   */
  public BaseString[] getInitialObjects() {
    return m_InitialObjects;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialObjectsTipText() {
    return "The initial objects to populate the dialog with.";
  }

  /**
   * Sets whether to enable/disable interactiveness.
   *
   * @param value	if true actor is not interactive, but automated
   */
  public void setNonInteractive(boolean value) {
    m_NonInteractive = value;
    reset();
  }

  /**
   * Returns whether interactiveness is enabled/disabled.
   *
   * @return 		true if actor is not interactive i.e., automated
   */
  public boolean isNonInteractive() {
    return m_NonInteractive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String nonInteractiveTipText() {
    return "If enabled, the initial objects are forwarded without user interaction.";
  }

  /**
   * Sets whether to use just the name of the actor or the full name.
   *
   * @param value 	if true just the name will get used, otherwise the full name
   */
  public void setShortTitle(boolean value) {
    m_ShortTitle = value;
    reset();
  }

  /**
   * Returns whether to use just the name of the actor or the full name.
   *
   * @return 		true if just the name used, otherwise full name
   */
  public boolean getShortTitle() {
    return m_ShortTitle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shortTitleTipText() {
    return "If enabled uses just the name for the title instead of the actor's full name.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "superClass", m_SuperClass, "super: ");
    result += QuickInfoHelper.toString(this, "initialObjects", (m_InitialObjects.length == 1 ? m_InitialObjects[0].getValue() : m_InitialObjects.length + " objects"), ", initial: ");

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "as array"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stops flow if canceled"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "nonInteractive", m_NonInteractive, "non-interactive"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "shortTitle", m_ShortTitle, "short title"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Turns a commandline into an object.
   * 
   * @param cmdline	the commandline to convert
   * @return		the generated object, null if failed to convert
   */
  protected abstract Object commandlineToObject(String cmdline);

  /**
   * Initializes the interactive dialog with the 
   */
  protected abstract void initializeDialog();
  
  /**
   * Displays the dialog, prompting the user to select classes.
   * 
   * @return		the selected objects, null in case of user cancelling the dialog
   */
  protected abstract Object[] showDialog();
  
  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    Object[]	selected;
    
    m_Queue.clear();
    
    initializeDialog();
    selected = showDialog();
    if (selected == null)
      return false;
    if (m_Stopped)
      return true;
    for (Object sel: selected)
      m_Queue.add(sel);
    
    return true;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	obj;
    
    result = null;
    
    if (isHeadless() || m_NonInteractive) {
      for (BaseString initial: m_InitialObjects) {
	obj = commandlineToObject(initial.getValue());
	if (obj == null)
	  getLogger().warning("Failed to convert commandline into object: " + initial);
	else
	  m_Queue.add(obj);
      }
    }
    else {
      result = super.doExecute();
    }
    
    return result;
  }
}
