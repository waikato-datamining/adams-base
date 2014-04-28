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
 * AbstractDocumentationProducer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adams.core.ClassLocator;
import adams.flow.core.Actor;

/**
 * Ancestor for producers that generate documentation that is split into
 * two parts: 1. structure of flow and 2. details of individual actors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data that gets generated
 * @param <I> the internal type used while nesting
 */
public abstract class AbstractDocumentationProducer<O>
  extends AbstractRecursiveOptionProducerWithOptionHandling<O,StringBuilder> {

  /** for serialization. */
  private static final long serialVersionUID = 4204278551512690989L;
  
  /**
   * Container object for generating the overview structure.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ActorPosition {
    /** the actor. */
    public Actor actor;
    
    /** the nesting level. */
    public int nesting;
    
    /** the index. */
    public int index;
    
    /**
     * Initializes the container.
     * 
     * @param actor	the actor
     * @param nesting	the nesting level
     * @param index	the sequence index of the actor
     */
    public ActorPosition(Actor actor, int nesting, int index) {
      this.actor   = actor;
      this.nesting = nesting;
      this.index   = index;
    }
  }
  
  /** the index in the structure. */
  protected int m_Index;
  
  /** the flat list of actors. */
  protected List<ActorPosition> m_Positions;

  /** the relation between full actor name and position container. */
  protected HashMap<String,ActorPosition> m_NamePosition;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Positions    = new ArrayList<ActorPosition>();
    m_NamePosition = new HashMap<String,ActorPosition>();
  }
  
  /**
   * Does nothing.
   *
   * @param option	the boolean option
   * @return		always null
   */
  @Override
  public StringBuilder processOption(BooleanOption option) {
    return null;
  }

  /**
   * Adds the position to the internal list/map.
   * 
   * @param actor	the actor to record the position for
   * @param nesting	the current nesting
   * @param index	the index of the actor
   */
  protected void addPosition(Actor actor, int nesting, int index) {
    ActorPosition	pos;
    
    pos = new ActorPosition(actor, nesting, index);
    
    m_Positions.add(pos);
    m_NamePosition.put(actor.getFullName(), pos);
  }
  
  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		always null
   */
  @Override
  public StringBuilder processOption(ClassOption option) {
    Object			currValue;
    Object			currValues;
    Object			value;
    int				i;
    boolean			isActor;

    isActor = (ClassLocator.hasInterface(Actor.class, option.getBaseClass()));
    if (!isActor)
      return null;

    currValue  = getCurrentValue(option);
    currValues = null;

    if (currValue != null) {
      if (!option.isMultiple()) {
	value = currValue;
	m_Index++;
	addPosition((Actor) value, m_Nesting.size(), m_Index);
	doProduce(((OptionHandler) value).getOptionManager());
      }
      else {
	currValues = currValue;
	m_Nesting.push(new StringBuilder());
	for (i = 0; i < Array.getLength(currValues); i++) {
	  value = Array.get(currValues, i);
	  m_Index++;
	  addPosition((Actor) value, m_Nesting.size(), m_Index);
	  doProduce(((OptionHandler) value).getOptionManager());
	}
	m_Nesting.pop();
      }
    }

    return null;
  }

  /**
   * Does nothing.
   *
   * @param option	the argument option
   * @return		always null
   */
  @Override
  public StringBuilder processOption(AbstractArgumentOption option) {
    return null;
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    super.preProduce();

    m_Index = 0;
    m_Positions.clear();
    m_NamePosition.clear();
  }
}
