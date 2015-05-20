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
 * FixDeprecatedCommandlineTransformers.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.data.conversion.AnyToCommandline;
import adams.data.conversion.CommandlineToAny;
import adams.flow.core.AbstractActor;
import adams.flow.transformer.Convert;

/**
 <!-- globalinfo-start -->
 * Replaces the deprecated CommandlineToAny and AnyToCommandline transformers with the appropriate Convert transfomer set ups.
 * <br><br>
 <!-- globalinfo-end -->
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@SuppressWarnings("deprecation")
public class FixDeprecatedCommandlineTransformers
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 5428735399970480088L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Replaces the deprecated CommandlineToAny and AnyToCommandline "
      + "transformers with the appropriate Convert transfomer set ups.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(AbstractActor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void update(AbstractOption option, Object newValue) {
	Method method = option.getDescriptor().getWriteMethod();
	try {
	  method.invoke(option.getOptionHandler(), new Object[]{newValue});
	  m_Modified = true;
	}
	catch (Exception e) {
	  System.err.println("Failed to update " + option + ": ");
	  e.printStackTrace();
	}
      }
      protected Convert setupConvert(Object current) {
	Convert	result = null;
	if (current instanceof adams.flow.transformer.AnyToCommandline) {
	  result = new Convert();
	  result.setConversion(new AnyToCommandline());
	}
	else if (current instanceof adams.flow.transformer.CommandlineToAny) {
	  result = new Convert();
	  result.setConversion(new CommandlineToAny());
	}
	return result;
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	Convert convert;
	Object element;
	boolean updated;
	Object current = option.getCurrentValue();
	if (option.isMultiple()) {
	  updated = false;
	  for (int i = 0; i < Array.getLength(current); i++) {
	    element = Array.get(current, i);
	    convert = setupConvert(element);
	    if (convert != null) {
	      convert.setName(((AbstractActor) element).getName());
	      Array.set(current, i, convert);
	      updated = true;
	    }
	  }
	  if (updated)
	    update(option, current);
	}
	else {
	  convert = setupConvert(current);
	  if (convert != null) {
	    convert.setName(((AbstractActor) current).getName());
	    update(option, convert);
	  }
	}
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// ignored
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return true;
      }
      public boolean canRecurse(Object obj) {
	return canRecurse(obj.getClass());
      }
    });
  }
}
