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
 * ReactivateActors.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Utils;
import adams.flow.control.Sequence;
import adams.flow.control.SubProcess;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.InactiveActor;
import adams.flow.source.SequenceSource;
import adams.flow.standalone.Standalones;

/**
 <!-- globalinfo-start -->
 * Activates all actors implementing the adams.flow.core.InactiveActor. If an actor cannot be removed, it gets disabled.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReactivateActors
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -8658024993875114313L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Activates all actors implementing the " + Utils.classToString(InactiveActor.class) + ". If an actor cannot be removed, it gets disabled.";
  }

  /**
   * Transfer the name of "source" to "target" and returns "target".
   *
   * @param source	the actor with the name to use
   * @param target	the actor to update
   * @return		the updated actor
   */
  protected Actor transferName(Actor source, Actor target) {
    target.setName(source.getName());
    return target;
  }

  /**
   * Reactivates the specified actor.
   *
   * @param handler	the current handler
   * @param index	the actor index in the handler
   */
  protected void activate(ActorHandler handler, int index) {
    Actor		actor;
    ActorHandler	subHandler;
    Standalones		standalones;
    SequenceSource	seqsource;
    SubProcess		subproc;
    Sequence 		sequence;
    Actor[]		actors;
    int			i;

    actor = handler.get(index);
    if (actor instanceof ActorHandler) {
      subHandler = (ActorHandler) actor;
      if (subHandler.size() == 0) {
        if (ActorUtils.isStandalone(subHandler)) {
          handler.set(index, transferName(subHandler, new adams.flow.standalone.Null()));
	}
	else if (ActorUtils.isSource(subHandler)) {
          handler.set(index, transferName(subHandler, new adams.flow.source.Null()));
	}
	else if (ActorUtils.isTransformer(subHandler)) {
          handler.set(index, transferName(subHandler, new adams.flow.transformer.PassThrough()));
	}
	else if (ActorUtils.isSink(subHandler)) {
          handler.set(index, transferName(subHandler, new adams.flow.sink.Null()));
	}
	else {
          getLogger().warning("Unknown actor type: " + Utils.classToString(subHandler));
	}
      }
      else if (subHandler.size() == 1) {
        handler.set(index, subHandler.get(0));
      }
      else {
        actors = new Actor[subHandler.size()];
        for (i = 0; i < subHandler.size(); i++)
          actors[i] = subHandler.get(i);

        if (ActorUtils.isStandalone(subHandler)) {
          standalones = new Standalones();
          standalones.setName(subHandler.getName());
          standalones.setActors(actors);
          handler.set(index, standalones);
	}
	else if (ActorUtils.isSource(subHandler)) {
          seqsource = new SequenceSource();
          seqsource.setName(subHandler.getName());
          seqsource.setActors(actors);
          handler.set(index, seqsource);
	}
	else if (ActorUtils.isTransformer(subHandler)) {
          subproc = new SubProcess();
          subproc.setName(subHandler.getName());
          subproc.setActors(actors);
          handler.set(index, subproc);
	}
	else if (ActorUtils.isSink(subHandler)) {
          sequence = new Sequence();
          sequence.setName(subHandler.getName());
          sequence.setActors(actors);
          handler.set(index, sequence);
	}
	else {
          getLogger().warning("Unknown actor type: " + Utils.classToString(subHandler));
	}
      }
    }
    else {
      handler.set(index, actor);
    }
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(Actor actor) {
    ActorHandler	handler;
    int			i;

    if (actor instanceof ActorHandler) {
      handler = (ActorHandler) actor;

      for (i = 0; i < handler.size(); i++) {
	if (handler.get(i) instanceof InactiveActor) {
	  activate(handler, i);
	  m_Modified = true;
	}
      }

      // recurse
      for (i = 0; i < handler.size(); i++)
	processActor(handler.get(i));
    }
  }
}
