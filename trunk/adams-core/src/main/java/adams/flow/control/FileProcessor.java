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
 * FileProcessor.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.io.File;

import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.Compatibility;
import adams.flow.core.ControlActor;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.transformer.AbstractDataProcessor;

/**
 * TODO: what this class does
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileProcessor
  extends AbstractDataProcessor
  implements MutableActorHandler, ControlActor {

  /** for serialization. */
  private static final long serialVersionUID = 296261057990241918L;
  
  /** the flow items. */
  protected Sequence m_Actors;
  
  /** whether to use files or strings as input for the actors. */
  protected boolean m_UseFiles;
  
  /** the actor to use for input. */
  protected transient AbstractActor m_Input;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Processes arriving files one by one, placing them in the 'processing'"
	+ "directory while working on them. If successfully processed, they get "
	+ "placed in 'processed', otherwise in 'failed'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "actor", "actors",
	    new AbstractActor[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Input  = null;
    m_Actors = new Sequence();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Input = null;
  }
  
  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Actors.setLoggingLevel(value);
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    m_Actors.setName(getName());
    m_Actors.setParent(null);
    m_Actors.setParent(getParent());
  }

  /**
   * Checks the tee actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActor(int index, AbstractActor actor) {
    Compatibility	comp;
    
    if ((index == 0) && !actor.getSkip()) {
      if (!(actor instanceof InputConsumer))
	return "Actor at position #" + (index+1) + " does not accept input!";
      comp = new Compatibility();
      if (!comp.isCompatible(accepts(), ((InputConsumer) actor).accepts()))
	return "Actor at #" + (index+1) + " must accept: " + Utils.classesToString(accepts()) + "!";
    }
    
    return null;
  }

  /**
   * Checks the tee actors before they are set via the setTeeActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActors(AbstractActor[] actors) {
    Compatibility	comp;
    int			i;
    int			index;
    
    index  = -1;
    for (i = 0; i < actors.length; i++) {
      if (!actors[i].getSkip()) {
	index = i;
	break;
      }
    }
    
    // no active actors? ignore!
    if (index == -1)
      return null;

    if (!(actors[index] instanceof InputConsumer))
      return "Actor at position #" + (index+1) + " does not accept input!";
    comp = new Compatibility();
    if (!comp.isCompatible(accepts(), ((InputConsumer) actors[index]).accepts()))
      return "Actor at #" + (index+1) + " must accept: " + Utils.classesToString(accepts()) + "!";
    
    return null;
  }

  /**
   * Sets the actor to tee-off to.
   *
   * @param value	the actor
   */
  public void setActors(AbstractActor[] value) {
    String	msg;

    msg = checkSubActors(value);
    if (msg == null) {
      m_Actors.setActors(value);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Returns the actors to tee-off to.
   *
   * @return		the actors
   */
  public AbstractActor[] getActors() {
    return m_Actors.getActors();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorsTipText() {
    return "The actors to siphon-off the tokens to.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return m_Actors.getActorHandlerInfo();
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String check() {
    return m_Actors.check();
  }

  /* (non-Javadoc)
   * @see adams.flow.core.ActorHandler#size()
   */
  @Override
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public AbstractActor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    m_Actors.set(index, actor);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    return m_Actors.indexOf(actor);
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  @Override
  public AbstractActor firstActive() {
    return m_Actors.firstActive();
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  @Override
  public AbstractActor lastActive() {
    return m_Actors.lastActive();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (lastActive() instanceof OutputProducer)
      return ((OutputProducer) lastActive()).generates();
    else
      return new Class[0];
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  @Override
  public void add(AbstractActor actor) {
    add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  @Override
  public void add(int index, AbstractActor actor) {
    String	msg;

    msg = checkSubActor(index, actor);
    if (msg == null) {
      m_Actors.add(index, actor);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  @Override
  public AbstractActor remove(int index) {
    AbstractActor	result;

    result = m_Actors.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  @Override
  public void removeAll() {
    m_Actors.removeAll();
    reset();
  }

  /**
   * Performs the setUp of the sub-actors.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String setUpSubActors() {
    String	result;

    result = null;

    if ((result == null) && (!getSkip())) {
      updateParent();
      if (result == null)
	result = m_Actors.setUp();
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    Compatibility	comp;
    
    result = super.setUp();
    
    if (result == null) {
      result = setUpSubActors();

      if (result == null) {
	m_Input = firstActive();
	if (m_Input == null)
	  result = "No active sub-actor available!";

	if (result == null) {
	  comp       = new Compatibility();
	  m_UseFiles = comp.isCompatible(new Class[]{File.class}, accepts());
	}
      }
    }
    
    return result;
  }
  
  /**
   * Processes the given data.
   *
   * @param file	the file/dir to process
   * @return		true if everything went alright
   * @see		#m_ProcessError
   */
  @Override
  protected boolean processData(File file) {
    boolean	result;
    
    try {
      if (m_UseFiles)
	((InputConsumer) m_Input).input(new Token(file.getAbsoluteFile()));
      else
	((InputConsumer) m_Input).input(new Token(file.getAbsolutePath()));
      m_ProcessError = m_Actors.execute();
      result = (m_ProcessError == null);
      if (result)
	m_OutputToken = new Token(m_DestinationFile);
    }
    catch (Exception e) {
      result = false;
      m_ProcessError = handleException("Failed to process: " + file, e);
    }
    
    return result;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    m_Actors.flushExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    m_Actors.stopExecution();
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    m_Actors.wrapUp();
    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_Actors.cleanUp();
    super.cleanUp();
  }
}
