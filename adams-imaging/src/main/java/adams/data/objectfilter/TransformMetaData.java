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
 * TransformMetaData.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Transforms the specified meta-data using the referenced callable actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The key of the meta-data value to add.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-transformer &lt;adams.flow.core.CallableActorReference&gt; (property: transformer)
 * &nbsp;&nbsp;&nbsp;The callable transformer to apply to the located cells.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TransformMetaData
  extends AbstractObjectFilter {

  private static final long serialVersionUID = 5647107073729835067L;

  /** the key name. */
  protected String m_Key;

  /** the callable transformer to apply to the cells. */
  protected CallableActorReference m_Transformer;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Transforms the specified meta-data using the referenced callable actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "");

    m_OptionManager.add(
      "transformer", "transformer",
      new CallableActorReference(CallableActorReference.UNKNOWN));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
  }

  /**
   * Sets the key of the meta-data value to add.
   *
   * @param value	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the key of the meta-data value to add.
   *
   * @return		the name
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The key of the meta-data value to add.";
  }

  /**
   * Sets the reference to the callable transformer.
   *
   * @param value	the reference
   */
  public void setTransformer(CallableActorReference value) {
    m_Transformer = value;
    reset();
  }

  /**
   * Returns the reference to the callable transformer.
   *
   * @return		the reference
   */
  public CallableActorReference getTransformer() {
    return m_Transformer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transformerTipText() {
    return "The callable transformer to apply to the located cells.";
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(m_FlowContext, getTransformer());
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "key", m_Key, "key: ");
    result += QuickInfoHelper.toString(this, "transformer", m_Transformer, ", transformer: ");

    return result;
  }

  /**
   * Configures the callable actor.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;

    result = null;

    m_CallableActor = findCallableActor();
    if (m_CallableActor == null) {
      result = "Couldn't find callable transformer '" + getTransformer() + "'!";
    }
    else {
      if (!ActorUtils.isTransformer(m_CallableActor))
	result = "Callable actor '" + getTransformer() + "' is not a transformer!";
    }

    return result;
  }

  /**
   * Hook method for checking the object list before processing it.
   *
   * @param objects	the object list to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(LocatedObjects objects) {
    String	result;

    result = super.check(objects);

    if (result == null) {
      if (m_CallableActor == null)
        result = setUpCallableActor();
    }

    return result;
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the objects to filter
   * @return		the updated object list
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    Object  		val;
    String		msg;
    Token		output;

    result = new LocatedObjects();

    for (LocatedObject object: objects) {
      object = object.getClone();
      val    = object.getMetaData().get(m_Key);
      if (val != null) {
	((InputConsumer) m_CallableActor).input(new Token(val));
	msg = m_CallableActor.execute();
	if (msg == null) {
	  output = ((OutputProducer) m_CallableActor).output();
	  if (output != null)
	    object.getMetaData().put(m_Key, output.getPayload());
	  else
	    getLogger().warning("Callable transformer '" + m_Transformer +"' generated no output on input: " + val);
	}
	else {
	  getLogger().severe("Callable transformer '" + m_Transformer +"' failed to process input: " + val + "\n" + msg);
	}
      }
      result.add(object);
    }

    return result;
  }
}
