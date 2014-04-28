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
 * FixDeprecatedStringToSpreadSheet.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.util.List;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.MutableActorHandler;
import adams.flow.transformer.Convert;
import adams.flow.transformer.StringToSpreadSheet;

/**
 * Replaces the {@link StringToSpreadSheet} transformer with a {@link Convert} transformer
 * using the {@link adams.data.conversion.StringToSpreadSheet} conversion scheme.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@SuppressWarnings("deprecation")
public class FixDeprecatedStringToSpreadSheet
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -4170658262349662939L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Replaces the StringToSpreadSheet transformer with a Convert transformer "
	+ "using the StringToSpreadSheet conversion scheme.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(AbstractActor actor) {
    List<AbstractActor> 	actors;
    StringToSpreadSheet		stsactor;
    Convert			convert;
    
    actors = ActorUtils.enumerate(actor, new Class[]{StringToSpreadSheet.class});
    for (AbstractActor sts: actors) {
      stsactor = (StringToSpreadSheet) sts;
      convert  = new Convert();
      convert.setName(stsactor.getName());
      convert.setConversion(new adams.data.conversion.StringToSpreadSheet());
      ((MutableActorHandler) stsactor.getParent()).set(stsactor.index(), convert);
      m_Modified = true;
    }
  }
}
