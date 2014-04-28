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
 * ProvenanceInformation.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.provenance;

import java.io.Serializable;

import adams.core.CloneHandler;
import adams.core.option.MaterializedArrayProducer;
import adams.flow.core.AbstractActor;

/**
 * Container used for storing provenance information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProvenanceInformation
  implements Serializable, CloneHandler<ProvenanceInformation> {

  /** for serialiation. */
  private static final long serialVersionUID = -8879950520072626172L;

  /** the type of actor. */
  protected ActorType m_ActorType;

  /** the type of input data, if any. */
  protected Class m_InputDataType;

  /** the type of output data, if any. */
  protected Class m_OutputDataType;

  /** the (materialized) options of the actor. */
  protected String[] m_Options;

  /**
   * Initializes the container (singletons).
   *
   * @param actorType	the type of actor
   * @param actor	the actor
   */
  public ProvenanceInformation(ActorType actorType, AbstractActor actor) {
    this(actorType, toOptions(actor));
  }

  /**
   * Initializes the container (singletons).
   *
   * @param actorType	the type of actor
   * @param options	the options of the actor
   */
  public ProvenanceInformation(ActorType actorType, String[] options) {
    this(actorType, null, options, null);
  }

  /**
   * Initializes the container (sinks).
   *
   * @param actorType	the type of actor
   * @param input	the type of input, can be null
   * @param actor	the actor
   */
  public ProvenanceInformation(ActorType actorType, Class input, AbstractActor actor) {
    this(actorType, input, toOptions(actor));
  }

  /**
   * Initializes the container (sinks).
   *
   * @param actorType	the type of actor
   * @param input	the type of input, can be null
   * @param options	the options of the actor
   */
  public ProvenanceInformation(ActorType actorType, Class input, String[] options) {
    this(actorType, input, options, null);
  }

  /**
   * Initializes the container (sources).
   *
   * @param actorType	the type of actor
   * @param actor	the actor
   * @param output	the type of output, can be null
   */
  public ProvenanceInformation(ActorType actorType, AbstractActor actor, Class output) {
    this(actorType, toOptions(actor), output);
  }

  /**
   * Initializes the container (sources).
   *
   * @param actorType	the type of actor
   * @param options	the options of the actor
   * @param output	the type of output, can be null
   */
  public ProvenanceInformation(ActorType actorType, String[] options, Class output) {
    this(actorType, null, options, output);
  }

  /**
   * Initializes the container (transformers).
   *
   * @param actorType	the type of actor
   * @param input	the type of input, can be null
   * @param actor	the actor
   * @param output	the type of output, can be null
   */
  public ProvenanceInformation(ActorType actorType, Class input, AbstractActor actor, Class output) {
    this(actorType, input, toOptions(actor), output);
  }

  /**
   * Initializes the container (transformers).
   *
   * @param actorType	the type of actor
   * @param input	the type of input, can be null
   * @param options	the options of the actor
   * @param output	the type of output, can be null
   */
  public ProvenanceInformation(ActorType actorType, Class input, String[] options, Class output) {
    super();

    m_ActorType      = actorType;
    m_InputDataType  = input;
    m_OutputDataType = output;
    m_Options        = options.clone();
  }

  /**
   * Returns the actor type.
   *
   * @return		the type
   */
  public ActorType getActorType() {
    return m_ActorType;
  }

  /**
   * Checks whether the container has information on the input data type.
   *
   * @return		true if input data type available
   */
  public boolean hasInputDataType() {
    return (m_InputDataType != null);
  }

  /**
   * Returns the input data type of this container.
   *
   * @return		the data type, null if not available
   */
  public Class getInputDataType() {
    return m_InputDataType;
  }

  /**
   * Checks whether the container has information on the output data type.
   *
   * @return		true if output data type available
   */
  public boolean hasOutputDataType() {
    return (m_OutputDataType != null);
  }

  /**
   * Returns the output data type of this container.
   *
   * @return		the data type, null if not available
   */
  public Class getOutputDataType() {
    return m_OutputDataType;
  }

  /**
   * Returns the options of the actor.
   *
   * @return		the options
   */
  public String[] getOptions() {
    return m_Options;
  }

  /**
   * Returns the classname from the options (convenience method).
   *
   * @return		the classname
   */
  public String getClassname() {
    return m_Options[0];
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public ProvenanceInformation getClone() {
    return new ProvenanceInformation(m_ActorType, m_InputDataType, m_Options, m_OutputDataType);
  }

  /**
   * Returns a short string representation of the container.
   *
   * @return		the string representation
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();

    result.append("actorType=" + m_ActorType);
    if (hasInputDataType())
      result.append(", input=" + m_InputDataType);
    result.append(", actor=" + m_Options[0]);
    if (hasOutputDataType())
      result.append(", output=" + m_OutputDataType);

    return result.toString();
  }

  /**
   * Turns the actor into an options array.
   *
   * @param actor	the actor to get the options for
   * @return		the options array
   */
  protected static String[] toOptions(AbstractActor actor) {
    MaterializedArrayProducer	producer;

    producer = new MaterializedArrayProducer();
    producer.produce(actor);

    return producer.getOutput();
  }
}
