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
 * AbstractCommandProcessor.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.logging.LoggingObject;
import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.gui.core.BasePanel;
import adams.gui.core.Undo;
import adams.gui.core.UndoHandler;
import adams.gui.visualization.container.DataContainerPanel;

/**
 * Abstract command processor for the scripting engine.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCommandProcessor
  extends LoggingObject
  implements UndoHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5363881783406430165L;

  /** the scripting engine this processor belongs to. */
  protected AbstractScriptingEngine m_Owner;

  /** the base panel to use. */
  protected BasePanel m_BasePanel;

  /** the action &lt;-&gt; scriptlet relation. */
  protected Hashtable<String,AbstractScriptlet> m_Actions;

  /**
   * Initializes the processor. Still needs to set the owner.
   *
   * @see	#setOwner(AbstractScriptingEngine)
   */
  public AbstractCommandProcessor() {
    this(null);
  }

  /**
   * Initializes the processor.
   *
   * @param owner	the owning scripting engine
   */
  public AbstractCommandProcessor(AbstractScriptingEngine owner) {
    super();

    m_Owner = owner;

    initScriptlets();
  }

  /**
   * Initializes all the available scriptlets.
   */
  protected void initScriptlets() {
    String[]		scriptlets;
    AbstractScriptlet	scriptlet;
    int			i;

    m_Actions = new Hashtable<String,AbstractScriptlet>();
    scriptlets = AbstractScriptlet.getScriptlets();
    for (i = 0; i < scriptlets.length; i++) {
      scriptlet = AbstractScriptlet.forName(scriptlets[i]);
      scriptlet.setOwner(this);
      if (!m_Actions.containsKey(scriptlet.getAction()))
	m_Actions.put(scriptlet.getAction(), scriptlet);
      else
	getLogger().severe(
	    "Duplicate actions:\n"
	    + scriptlet.getClass().getName() + "\n"
	    + m_Actions.get(scriptlet.getAction()).getClass().getName());
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    StringBuilder				result;
    Hashtable<String,Vector<AbstractScriptlet>>	sorted;
    int						i;
    int						n;
    int						m;
    AbstractScriptlet				scriptlet;
    Enumeration<AbstractScriptlet>		enm;
    String[]					required;
    Vector<AbstractScriptlet>			scriptlets;
    Vector<String>				types;
    String[]					desc;
    Class[]					requiredClasses;

    result = new StringBuilder();

    // sort scriptlets according to required class
    sorted = new Hashtable<String,Vector<AbstractScriptlet>>();
    enm    = (Enumeration<AbstractScriptlet>) m_Actions.elements();
    while (enm.hasMoreElements()) {
      scriptlet = enm.nextElement();

      requiredClasses = scriptlet.getRequirements();
      if (requiredClasses == null) {
	required = new String[]{""};
      }
      else {
	required = new String[requiredClasses.length];
	for (i = 0; i < requiredClasses.length; i++)
	  required[i] = requiredClasses[i].getName();
      }

      for (i = 0; i < required.length; i++) {
	if (!sorted.containsKey(required[i])) {
	  scriptlets = new Vector<AbstractScriptlet>();
	  sorted.put(required[i], scriptlets);
	}
	else {
	  scriptlets = sorted.get(required[i]);
	}
	scriptlets.add(scriptlet);
      }
    }

    // assemble information
    types = new Vector<String>(sorted.keySet());
    Collections.sort(types);
    for (i = 0; i < types.size(); i++) {
      scriptlets = sorted.get(types.get(i));
      Collections.sort(scriptlets);

      // heading
      if (types.get(i).length() == 0)
	result.append("General actions:\n\n");
      else
	result.append("Actions for " + types.get(i) + ":\n\n");

      // actions
      for (n = 0; n < scriptlets.size(); n++) {
	scriptlet = scriptlets.get(n);
	result.append(scriptlet.getParameterDescription() + "\n");
	desc = Utils.breakUp(scriptlet.getDescription(), 72);
	for (m = 0; m < desc.length; m++)
	  result.append("\t" + desc[m] + "\n");
	result.append("\n");
      }

      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Sets the owner of this processor.
   *
   * @param value	the owner
   */
  public void setOwner(AbstractScriptingEngine value) {
    m_Owner = value;
  }

  /**
   * Returns the owner of this processor.
   *
   * @return		the owner
   */
  public AbstractScriptingEngine getOwner() {
    return m_Owner;
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public abstract AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Sets the base panel.
   *
   * @param value	the panel to use
   */
  public void setBasePanel(BasePanel value) {
    m_BasePanel = value;
  }

  /**
   * Returns the base panel.
   *
   * @return		the base panel
   */
  public BasePanel getBasePanel() {
    return m_BasePanel;
  }

  /**
   * Returns the DataContainer panel.
   *
   * @return		the panel or null
   */
  public DataContainerPanel getDataContainerPanel() {
    if ((m_BasePanel != null) && (m_BasePanel instanceof DataContainerPanel))
      return (DataContainerPanel) m_BasePanel;
    else
      return null;
  }

  /**
   * Returns the post-processor to use.
   *
   * @return		the post-processor
   */
  public AbstractDataContainerUpdatingPostProcessor getDataContainerUpdatingPostProcessor() {
    return null;
  }

  /**
   * Returns the class that is required in the flow.
   *
   * @return		the required class
   */
  protected Class getRequiredFlowClass() {
    return DataContainer.class;
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value) {
    if (isUndoSupported())
      ((UndoHandler) getBasePanel()).setUndo(value);
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  public Undo getUndo() {
    if (isUndoSupported())
      return ((UndoHandler) getBasePanel()).getUndo();
    else
      return null;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported() {
    return (    (getBasePanel() instanceof UndoHandler)
	     && ((UndoHandler) getBasePanel()).isUndoSupported());
  }

  /**
   * Returns the status message handler, if available.
   *
   * @return		the handler
   */
  public StatusMessageHandler getStatusMessageHandler() {
    if (m_BasePanel instanceof StatusMessageHandler)
      return (StatusMessageHandler) m_BasePanel;
    else
      return null;
  }

  /**
   * Returns the object that is to be used for the undo point.
   *
   * @return		the object to store as undo point
   * @see		#addUndoPoint(String, String)
   */
  protected abstract Object getUndoObject();

  /**
   * Adds an undo point, if possible.
   *
   * @param statusMsg	the status message to display while adding the undo point
   * @param undoComment	the comment for the undo point
   */
  protected void addUndoPoint(String statusMsg, String undoComment) {
    Object	undo;

    if (isUndoSupported() && getUndo().isEnabled()) {
      undo = getUndoObject();
      if (undo != null) {
	showStatus(statusMsg);
	getUndo().addUndo(undo, undoComment);
	showStatus("");
      }
    }
  }

  /**
   * Displays the status, if the owner is a StatusMessageHandler, otherwise it
   * just prints the status on the commandline.
   *
   * @param msg		the message to display
   * @see		StatusMessageHandler
   */
  protected void showStatus(String msg) {
    if (getStatusMessageHandler() != null) {
      getStatusMessageHandler().showStatus(msg);
    }
    else {
      getLogger().info(msg);
      System.out.println(msg);
    }
  }

  /**
   * Returns the scriptlet associated with the action.
   *
   * @param action 	the action to get the scriptlet for
   * @return		the scriptlet or null if none found
   */
  protected AbstractScriptlet findScriptlet(String action) {
    return m_Actions.get(action);
  }

  /**
   * Creates a default error message for the given requirement class.
   *
   * @param requirement	the class to create the error message for
   * @return		the error message
   */
  protected String createRequirementError(Class requirement) {
    return "No " + requirement.getName() + " available!";
  }

  /**
   * Checks the following requirement.
   * <br><br>
   * Needs to be extended in derived classes.
   *
   * @param requirement	the requirement class that needs to be present
   * @return		"" if met, error message if not met, null if not processed
   */
  protected String checkRequirement(Class requirement) {
    String	result;

    result = null;

    if (requirement == BasePanel.class) {
      if (getBasePanel() == null)
	result = createRequirementError(requirement);
      else
	result = "";
    }
    else if (requirement == DataContainerPanel.class) {
      if (getDataContainerPanel() == null)
	result = createRequirementError(requirement);
      else
	result = "";
    }
    else if (requirement == UndoHandler.class) {
      if ((getBasePanel() == null) || (!(getBasePanel() instanceof UndoHandler)))
	result = createRequirementError(requirement);
      else
	result = "";
    }

    return result;
  }

  /**
   * Checks whether all the requirements for the scriptlet have been met.
   * <br><br>
   * Needs to be extended in derived classes.
   *
   * @param scriplet	the scriplet to check
   * @return		"" if all requirements met, error message if not met,
   * 			null if at least one requirement not processed
   */
  protected String checkRequirements(AbstractScriptlet scriplet) {
    String	result;
    Class[]	requirements;
    int		i;

    result = "";

    requirements = scriplet.getRequirements();
    if (requirements != null) {
      for (i = 0; i < requirements.length; i++) {
	result = checkRequirement(requirements[i]);
	if (result != null)
	  break;
      }
    }

    return result;
  }

  /**
   * Performs further setups of the scriptlet.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param scriptlet	the scriptlet to work on
   */
  protected void setupScriptlet(AbstractScriptlet scriptlet) {
  }

  /**
   * processes the given command.
   *
   * @param command	the command to execute
   * @return		null if no error generated, otherwise the error output
   * @throws Exception	if something goes wrong
   */
  public String process(ScriptingCommand command) throws Exception {
    String		action;
    String		options;
    String		result;
    String		reqResult;
    AbstractScriptlet	scriptlet;

    result = null;

    setBasePanel(command.getBasePanel());

    // obtain action
    action    = command.getCommand().trim().replaceAll(" .*", "");
    options   = command.getCommand().trim().replaceAll("^" + action + " ", "").trim();
    scriptlet = findScriptlet(action);

    // action registered?
    if (scriptlet == null)
      result = getClass().getName() + ": Unknown action '" + action + "'!";

    // all requirements met?
    if (result == null) {
      reqResult = checkRequirements(scriptlet);
      setupScriptlet(scriptlet);
      if (reqResult == null)
	getLogger().severe(
	    "WARNING: Action '" + action + "'/" + scriptlet.getClass().getName()
	    + " has unmet requirement(s)!");
      else if (reqResult.length() > 0)
	result = reqResult;
    }

    // execute action
    if (result == null)
      result = scriptlet.process(options);

    setBasePanel(null);

    return result;
  }
}
