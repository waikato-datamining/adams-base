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
 * IfStorageValue.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.sink.Null;
import adams.flow.source.StringConstants;

/**
 <!-- globalinfo-start -->
 * An If-Then-Else source actor for storage values. If a storage value is available, the 'Then' branch gets executed, otherwise the 'Else' branch.<br>
 * Whereas the 'Then' branch receives the storage value (in case this branch accepts input), the 'Else' branch needs to produce its own data, i.e., having a source actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: IfStorageValue
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the cache to retrieve the value from; uses the regular storage
 * &nbsp;&nbsp;&nbsp;if left empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the stored value to retrieve.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-then &lt;adams.flow.core.Actor&gt; (property: thenActor)
 * &nbsp;&nbsp;&nbsp;The actor of the 'then' branch.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.Sequence -name then -actor adams.flow.sink.Null
 * </pre>
 *
 * <pre>-else &lt;adams.flow.core.Actor&gt; (property: elseActor)
 * &nbsp;&nbsp;&nbsp;The actor of the 'else' branch.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.Sequence -name else -actor adams.flow.source.StringConstants
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IfStorageValue
  extends AbstractDirectedControlActor {

  /** for serialization. */
  private static final long serialVersionUID = 185561131623293880L;

  /**
   * A specialized director for an IfStorageValue control actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class IfStorageValueDirector
    extends AbstractDirector {

    /** for serialization. */
    private static final long serialVersionUID = 8414511259688024553L;

    /** the stored value. */
    protected Object m_StoredValue;

    /**
     * Calls the super implementation of setControlActor.
     *
     * @param value	the control actor to set
     */
    protected void setIfThenElseActor(AbstractDirectedControlActor value) {
      super.setControlActor(value);
    }

    /**
     * Sets the group to execute.
     *
     * @param value 	the group
     */
    public void setControlActor(AbstractDirectedControlActor value) {
      if ((value instanceof IfStorageValue) || (value == null))
	setIfThenElseActor(value);
      else
	System.err.println(
	    "Control actor must be a IfStorageValue actor (provided: "
	    + ((value != null) ? value.getClass().getName() : "-null-") + ")!");
    }

    /**
     * Determines whether to execute the 'then' branch.
     *
     * @return		true if the 'then' branch should get executed
     */
    protected boolean doThen() {
      boolean		result;
      IfStorageValue	owner;
      Storage		storage;

      owner         = (IfStorageValue) m_ControlActor;
      storage       = owner.getStorageHandler().getStorage();
      m_StoredValue = null;

      if (owner.getCache().length() == 0) {
	if (storage.has(owner.getStorageName()))
	  m_StoredValue = storage.get(owner.getStorageName());
      }
      else {
	if (storage.has(owner.getCache(), owner.getStorageName()))
	  m_StoredValue = storage.get(owner.getCache(), owner.getStorageName());
      }

      result = (m_StoredValue != null);

      if (isLoggingEnabled()) {
	getLogger().info(
	      "doThen:\n"
	    + "- cache name: " + owner.getCache() + "\n"
	    + "- storage name: " + owner.getStorageName() + "\n"
	    + "  --> " + result + " (" + m_StoredValue + ")");
      }

      return result;
    }

    /**
     * Executes the group of actors.
     *
     * @return		null if everything went smooth
     */
    @Override
    public String execute() {
      String	result;
      Actor	branch;
      Token	token;

      if (doThen())
	branch = ((IfStorageValue) m_ControlActor).getThenActor();
      else
	branch = ((IfStorageValue) m_ControlActor).getElseActor();

      try {
	// input
	if ((m_StoredValue != null) && (branch instanceof InputConsumer)) {
	  token = new Token(m_StoredValue);
	  if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	    branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(branch, token);
	  ((InputConsumer) branch).input(token);
	  if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	    branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(branch);
	}
	// execute
	if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(branch);
	result = branch.execute();
	if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(branch);
      }
      catch (Throwable t) {
	result = handleException(branch.getFullName() + " generated the following exception: ", t);
      }

      if (result != null)
	result = branch.getErrorHandler().handleError(branch, "execute", result);

      m_StoredValue = null;

      return result;
    }

    /**
     * Cleans up data structures, frees up memory.
     */
    @Override
    public void cleanUp() {
      m_StoredValue = null;

      super.cleanUp();
    }
  }

  /** the name of the LRU cache. */
  protected String m_Cache;

  /** the name of the stored value. */
  protected StorageName m_StorageName;

  /** the actor to execute in the "then" branch. */
  protected Actor m_ThenActor;

  /** the actor to execute in the "else" branch. */
  protected Actor m_ElseActor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "An If-Then-Else source actor for storage values. If a storage "
      + "value is available, the 'Then' branch gets executed, otherwise "
      + "the 'Else' branch.\n"
      + "Whereas the 'Then' branch receives the storage value (in case this "
      + "branch accepts input), the 'Else' branch needs to produce its own "
      + "data, i.e., having a source actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cache", "cache",
	    "");

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName());

    m_OptionManager.add(
	    "then", "thenActor",
	    getDefaultThen());

    m_OptionManager.add(
	    "else", "elseActor",
	    getDefaultElse());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    // need to be initialized because of "updateParent()" call
    m_ThenActor  = getDefaultThen();
    m_ElseActor  = getDefaultElse();
  }

  /**
   * Returns the default 'Then' actor.
   *
   * @return		the default actor
   */
  protected Actor getDefaultThen() {
    Sequence	result;
    Actor	actor;

    result = new Sequence();
    result.setName("then");
    actor = new Null();
    result.setActors(new Actor[]{
	actor
    });

    return result;
  }

  /**
   * Returns the default 'Else' actor.
   *
   * @return		the default actor
   */
  protected Actor getDefaultElse() {
    Sequence	result;
    Actor	actor;

    result = new Sequence();
    result.setName("else");
    actor = new StringConstants();
    result.setActors(new Actor[]{
	actor
    });

    return result;
  }

  /**
   * Returns an instance of a director.
   *
   * @return		the director
   */
  @Override
  protected AbstractDirector newDirector() {
    return new IfStorageValueDirector();
  }

  /**
   * Sets the name of the LRU cache to use, regular storage if left empty.
   *
   * @param value	the cache
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the LRU cache to use, regular storage if left empty.
   *
   * @return		the cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the cache to retrieve the value from; uses the regular storage if left empty.";
  }

  /**
   * Sets the name of the stored value.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored value.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the stored value to retrieve.";
  }

  /**
   * Sets the actor of the 'then' branch.
   *
   * @param value	the actor
   */
  public void setThenActor(Actor value) {
    ActorUtils.uniqueName(value, this, 0);
    m_ThenActor = value;
    reset();
    updateParent();
  }

  /**
   * Returns the actor of the 'then' branch.
   *
   * @return		the actor
   */
  public Actor getThenActor() {
    return m_ThenActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thenActorTipText() {
    return "The actor of the 'then' branch.";
  }

  /**
   * Sets the actor of the 'else' branch.
   *
   * @param value	the actor
   */
  public void setElseActor(Actor value) {
    ActorUtils.uniqueName(value, this, 1);
    m_ElseActor = value;
    reset();
    updateParent();
  }

  /**
   * Returns the actor of the 'else' branch.
   *
   * @return		the actor
   */
  public Actor getElseActor() {
    return m_ElseActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String elseActorTipText() {
    return "The actor of the 'else' branch.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName.getValue());
    value  = QuickInfoHelper.toString(this, "cache", (m_Cache.length() > 0 ? m_Cache : ""), " cache: ");
    if (value != null)
      result += value;
    
    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 2
   */
  @Override
  public int size() {
    return 2;
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public Actor get(int index) {
    if (index == 0)
      return m_ThenActor;
    else if (index == 1)
      return m_ElseActor;
    else
      throw new IndexOutOfBoundsException("Only two items available, requested index: " + index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, Actor actor) {
    if (index == 0)
      setThenActor(actor);
    else if (index == 1)
      setElseActor(actor);
    else
      getLogger().severe("Index out of range: " + index);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    if (m_ThenActor.getName().equals(actor))
      return 0;
    else if (m_ElseActor.getName().equals(actor))
      return 1;
    else
      return -1;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.PARALLEL, true);
  }

  /**
   * Performs checks on the "sub-actors". Default implementation does nothing.
   *
   * @return		null
   */
  @Override
  public String check() {
    ActorHandler	handler;
    Actor[]		actors;
    int			i;
    
    if (m_ElseActor instanceof ActorHandler) {
      handler = (ActorHandler) m_ElseActor;
      actors  = new Actor[handler.size()];
      for (i = 0; i < handler.size(); i++)
	actors[i] = handler.get(i);
      return ActorUtils.checkForSource(actors);
    }
    else  {
      return ActorUtils.checkForSource(m_ElseActor);
    }
  }
}
