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
 * EncapsulatedActorsContainer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.core.Variables;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for encapsulating actors alongside variables and storage items.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.flow.processor.ActorProcessor
 */
public class EncapsulatedActorsContainer
  extends AbstractContainer {

  private static final long serialVersionUID = -418617166464706249L;

  /** the key for the actor. */
  public final static String VALUE_ACTOR = "Actor";

  /** the key for the variables. */
  public final static String VALUE_VARIABLES = "Variables";

  /** the key for the storage. */
  public final static String VALUE_STORAGE = "Storage";

  /** the key for the input. */
  public final static String VALUE_INPUT = "Input";

  /** the key for the input name. */
  public final static String VALUE_INPUTNAME = "Input name";

  /** the key for the output. */
  public final static String VALUE_OUTPUT = "Output";

  /** the key for the output name. */
  public final static String VALUE_OUTPUTNAME = "Output name";

  /**
   * Default constructor.
   */
  public EncapsulatedActorsContainer() {
    this(null, null, null);
  }

  /**
   * Initializes the container with the actors, variables and storage.
   *
   * @param actor	the actor to store
   * @param variables   the variables to store
   * @param storage	the storage items to store
   */
  public EncapsulatedActorsContainer(Actor actor, Variables variables, Storage storage) {
    this(actor, variables, storage, null, null);
  }

  /**
   * Initializes the container with the actors, variables and storage.
   *
   * @param actor	the actor to store
   * @param variables   the variables to store
   * @param storage	the storage items to store
   * @param input 	the input token payload, can be null
   * @param inputName 	the input name in storage, can be null
   */
  public EncapsulatedActorsContainer(Actor actor, Variables variables, Storage storage, Object input, StorageName inputName) {
    this(actor, variables, storage, input, inputName, null, null);
  }

  /**
   * Initializes the container with the actors, variables and storage.
   *
   * @param actor	the actor to store
   * @param variables   the variables to store
   * @param storage	the storage items to store
   * @param input 	the input token payload, can be null
   * @param inputName 	the input name in storage, can be null
   * @param output 	the output, can be null
   * @param outputName 	the output name in storage, can be null
   */
  public EncapsulatedActorsContainer(Actor actor, Variables variables, Storage storage, Object input, StorageName inputName, Object output, StorageName outputName) {
    super();

    store(VALUE_ACTOR,      actor);
    store(VALUE_VARIABLES,  variables);
    store(VALUE_STORAGE,    storage);
    store(VALUE_INPUT,      input);
    store(VALUE_INPUTNAME,  inputName);
    store(VALUE_OUTPUT,     output);
    store(VALUE_OUTPUTNAME, outputName);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> 	result;

    result = new ArrayList<>();
    result.add(VALUE_ACTOR);
    result.add(VALUE_VARIABLES);
    result.add(VALUE_STORAGE);
    result.add(VALUE_INPUT);
    result.add(VALUE_INPUTNAME);
    result.add(VALUE_OUTPUT);
    result.add(VALUE_OUTPUTNAME);

    return result.iterator();
  }

  /**
   * Initializes the help strings.
   */
  @Override
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_ACTOR, "actor to encapsulate", Actor.class);
    addHelp(VALUE_VARIABLES, "the variables", Variables.class);
    addHelp(VALUE_STORAGE, "the storage items", Storage.class);
    addHelp(VALUE_INPUT, "the optional input", Object.class);
    addHelp(VALUE_INPUTNAME, "the storage name of the optional input", StorageName.class);
    addHelp(VALUE_OUTPUT, "the optional generated output", Object.class);
    addHelp(VALUE_OUTPUTNAME, "the storage name of the optional generated output", StorageName.class);
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_ACTOR) && hasValue(VALUE_VARIABLES) && hasValue(VALUE_STORAGE);
  }
}
