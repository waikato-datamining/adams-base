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
 * EncapsulateActors.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.VariableName;
import adams.core.Variables;
import adams.flow.container.EncapsulatedActorsContainer;
import adams.flow.control.Flow;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.source.StorageValue;
import adams.flow.transformer.SetStorageValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for encapsulating actor(s).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EncapsulateActors {

  /**
   * Extracts the specified variables and their values and returns the subset.
   *
   * @param context 	the flow context to get the current variables from
   * @param vars	the variables to extract
   * @return		the subset
   */
  public static Variables getVariables(Actor context, VariableName[] vars) {
    Variables	result;
    Variables	source;

    result = new Variables();
    source = context.getVariables();
    for (VariableName var: vars) {
      if (source.has(var.getValue()))
        result.set(var.getValue(), source.get(var.getValue()));
    }

    return result;
  }

  /**
   * Extracts the specified storage items and returns the subset.
   *
   * @param context 	the flow context to get the current storage items from
   * @param items	the items to extract
   * @return		the subset
   */
  public static Storage getStorage(Actor context, StorageName[] items) {
    Storage	result;
    Storage	source;

    result = new Storage();
    source = context.getStorageHandler().getStorage();
    for (StorageName item : items) {
      if (source.has(item))
        result.put(item, source.get(item));
    }

    return result;
  }

  /**
   * Generates a storage name, ensuring that it doesn't interfere with storage
   * items to keep.
   *
   * @param items	the storage items to keep
   * @param prefix 	the prefix to use
   * @return		the name for the input token
   */
  protected static StorageName createStorageName(StorageName[] items, String prefix) {
    StorageName 	result;
    Set<StorageName>	names;
    int			count;

    names = new HashSet<>(Arrays.asList(items));
    result = new StorageName(prefix);
    count = 0;
    while (names.contains(result)) {
      count++;
      result = new StorageName(prefix + count);
    }

    return result;
  }

  /**
   * Generates a storage name for the input token, ensuring that it doesn't
   * interfere with storage items to keep.
   *
   * @param items	the storage items to keep
   * @return		the name for the input token
   */
  public static StorageName createInputStorageName(StorageName[] items) {
    return createStorageName(items, "input");
  }

  /**
   * Generates a storage name for the output token, ensuring that it doesn't
   * interfere with storage items to keep.
   *
   * @param items	the storage items to keep
   * @return		the name for the output token
   */
  public static StorageName createOutputStorageName(StorageName[] items) {
    return createStorageName(items, "output");
  }

  /**
   * Encapsulates the actor as is.
   *
   * @param actor	the actor to encapsulate
   * @param vars	the variables to migrate
   * @param items	the storage items to migrate
   * @return		the generated container
   */
  public static EncapsulatedActorsContainer encapsulate(Actor actor, VariableName[] vars, StorageName[] items) {
    return encapsulate(actor, vars, items, null);
  }

  /**
   * Encapsulates the actor as is.
   *
   * @param actor	the actor to encapsulate
   * @param vars	the variables to migrate
   * @param items	the storage items to migrate
   * @param input 	the input token, can be null
   * @return		the generated container
   */
  public static EncapsulatedActorsContainer encapsulate(Actor actor, VariableName[] vars, StorageName[] items, Object input) {
    return new EncapsulatedActorsContainer(
      actor,
      getVariables(actor, vars),
      getStorage(actor, items),
      input,
      null);
  }

  /**
   * Wraps the actor to obtain a fully self-container flow.
   *
   * @param actor	the actors to wrap
   * @param vars	the variables to migrate
   * @param items	the storage items to migrate
   * @return		the generated container
   */
  public static EncapsulatedActorsContainer wrap(Actor actor, VariableName[] vars, StorageName[] items) {
    return wrap(actor, vars, items, null);
  }

  /**
   * Wraps the actor to obtain a fully self-container flow.
   *
   * @param actor	the actor to wrap
   * @param vars	the variables to migrate
   * @param items	the storage items to migrate
   * @param input 	the input token payload, can be null; gets ignored if actor is a Flow actor
   * @return		the generated container
   */
  public static EncapsulatedActorsContainer wrap(Actor actor, VariableName[] vars, StorageName[] items, Object input) {
    Variables		variables;
    Storage		storage;
    StorageName		inputName;
    StorageName		outputName;

    // already a proper flow?
    if (actor instanceof Flow)
      return encapsulate(actor.shallowCopy(false), vars, items);

    variables = getVariables(actor, vars);
    storage   = getStorage(actor, items);

    // input?
    inputName  = null;
    outputName = null;
    if (input != null) {
      inputName  = createInputStorageName(items);
      storage.put(inputName, input);

      if (actor instanceof InputConsumer) {
        Flow flow = new Flow();
        StorageValue sv = new StorageValue();
        sv.setStorageName(inputName);
        flow.add(sv);
	flow.add(actor);
        if (actor instanceof InputConsumer) {
	  outputName = createOutputStorageName(items);
	  SetStorageValue ssv = new SetStorageValue();
	  ssv.setStorageName(outputName);
	  flow.add(ssv);
	}
        actor = flow;
      }
      else {
        Flow flow = new Flow();
	flow.add(actor);
        if (actor instanceof InputConsumer) {
	  outputName = createOutputStorageName(items);
	  SetStorageValue ssv = new SetStorageValue();
	  ssv.setStorageName(outputName);
	  flow.add(ssv);
	}
        actor = flow;
      }
    }

    return new EncapsulatedActorsContainer(actor.shallowCopy(false), variables, storage, input, inputName, null, outputName);
  }
}
