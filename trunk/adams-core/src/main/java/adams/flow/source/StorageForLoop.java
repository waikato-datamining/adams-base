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
 * StorageForLoop.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Emulates the following for-loop:<br/>
 * - positive step size:<br/>
 *   for (int i = lower; i &lt;= upper; i += step)<br/>
 * - negative step size:<br/>
 *   for (int i = upper; i &gt;= lower; i += step)<br/>
 * In each iteration, the specified variable gets updated with the current value of the for-loop variable. The output of the for-loop is the specified storage value (in each iteration).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: StorageForLoop
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-lower &lt;int&gt; (property: loopLower)
 * &nbsp;&nbsp;&nbsp;The lower bound of the loop (= the first value).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-upper &lt;int&gt; (property: loopUpper)
 * &nbsp;&nbsp;&nbsp;The upper bound of the loop.
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 *
 * <pre>-step &lt;int&gt; (property: loopStep)
 * &nbsp;&nbsp;&nbsp;The step size of the loop.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable that gets updated with the for-loop value.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-storage &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage value to forward in each iteration of the loop.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StorageForLoop
  extends AbstractForLoop 
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5940182514084428795L;

  /** the variable to update with for-loop value. */
  protected VariableName m_VariableName;

  /** the storage value to output. */
  protected StorageName m_StorageName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Emulates the following for-loop:\n"
      + "- positive step size:\n"
      + "  for (int i = lower; i <= upper; i += step)\n"
      + "- negative step size:\n"
      + "  for (int i = upper; i >= lower; i += step)\n"
      + "In each iteration, the specified variable gets updated with the "
      + "current value of the for-loop variable. The output of the for-loop "
      + "is the specified storage value (in each iteration).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "variable", "variableName",
	    new VariableName());

    m_OptionManager.add(
	    "storage", "storageName",
	    new StorageName());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "variableName", m_VariableName, ", var: ");
    result += QuickInfoHelper.toString(this, "storagename", m_StorageName, ", storage: ");

    return result;
  }

  /**
   * Sets the variable to update with the for-loop value.
   *
   * @param value	the variable
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the variable to update with the for-loop value.
   *
   * @return		the variable
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The variable that gets updated with the for-loop value.";
  }

  /**
   * Sets the name of the storage value to forward.
   *
   * @param value	the storage name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage value to forward.
   *
   * @return		the storage name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage value to forward in each iteration of the loop.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (isLoggingEnabled())
      getLogger().info("i=" + m_Current);

    getVariables().set(m_VariableName.getValue(), "" + m_Current);
    if (isLoggingEnabled())
      getLogger().info(m_VariableName + "=" + m_Current);

    result     = new Token(getStorageHandler().getStorage().get(m_StorageName));
    m_Current += m_LoopStep;

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, ((Token) cont).getPayload().getClass()));
  }
}
