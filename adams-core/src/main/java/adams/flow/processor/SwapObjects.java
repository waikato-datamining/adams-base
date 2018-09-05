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
 * SwapObjects.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.data.conversion.AbstractSwapObject;
import adams.flow.core.Actor;

import java.lang.reflect.Array;

/**
 * Allows replacing of objects using the specified object swap conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SwapObjects
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;

  /** the swap conversion. */
  protected AbstractSwapObject m_Conversion;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows replacing of objects using the specified object swap conversion.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "conversion", "conversion",
      new adams.data.conversion.SwapObjects());
  }

  /**
   * Sets the conversion to use.
   *
   * @param value	the conversion
   */
  public void setConversion(AbstractSwapObject value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion to use.
   *
   * @return		the conversion
   */
  public AbstractSwapObject getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String conversionTipText() {
    return "The conversion to use for swapping the objects.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(Actor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected Object process(Object obj) {
        m_Conversion.setInput(obj);
        String msg = m_Conversion.convert();
        if (msg == null) {
          m_Modified = true;
	  return m_Conversion.getOutput();
	}
        else {
          getLogger().warning(msg);
	  return obj;
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	Object current = option.getCurrentValue();
	if (option.isMultiple()) {
	  for (int i = 0; i < Array.getLength(current); i++)
	    Array.set(current, i, process(Array.get(current, i)));
	  option.setCurrentValue(current);
	}
	else {
	  option.setCurrentValue(process(current));
	}
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return true;
      }
      public boolean canRecurse(Object obj) {
	return true;
      }
    });
  }
}
