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
 * ContainerToStorage.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.flow.container.AbstractContainer;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Extracts the specified values from the container passing through and makes them available as storage items. A prefix for the storage names can be supplied.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.AbstractContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.AbstractContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.AbstractContainer:
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ContainerToStorage
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to match the names of the container values against.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;api&#47;java.base&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the storage names.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-container-storage-name-pair &lt;adams.core.base.BaseKeyValuePair&gt; [-container-storage-name-pair ...] (property: containerStorageNamePairs)
 * &nbsp;&nbsp;&nbsp;The pairs of container name and storage name, overrides the regular expression.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ContainerToStorage
  extends AbstractContainerValueExtractor {

  private static final long serialVersionUID = 8072844783869677669L;

  /** the container name/storage name pairs. */
  protected List<BaseKeyValuePair> m_ContainerStorageNamePairs;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Extracts the specified values from the container passing through and "
	+ "makes them available as storage items. A prefix for the storage names can "
	+ "be supplied.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "container-storage-name-pair", "containerStorageNamePairs",
      new BaseKeyValuePair[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ContainerStorageNamePairs = new ArrayList<>();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String prefixTipText() {
    return "The prefix to use for the storage names.";
  }

  /**
   * Adds the container name / storage name pair.
   *
   * @param value	the pair to add
   */
  public void addContainerStorageNamePair(BaseKeyValuePair value) {
    m_ContainerStorageNamePairs.add(value);
    reset();
  }

  /**
   * Sets the container name / storage name pairs.
   *
   * @param value	the pairs
   */
  public void setContainerStorageNamePairs(BaseKeyValuePair[] value) {
    m_ContainerStorageNamePairs.clear();
    m_ContainerStorageNamePairs.addAll(Arrays.asList(value));
    reset();
  }

  /**
   * Returns the pairs of container name / storage name.
   *
   * @return		the pairs
   */
  public BaseKeyValuePair[] getContainerStorageNamePairs() {
    return m_ContainerStorageNamePairs.toArray(new BaseKeyValuePair[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String containerStorageNamePairsTipText() {
    return "The pairs of container name and storage name, overrides the regular expression.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (m_ContainerStorageNamePairs.isEmpty())
      result = super.getQuickInfo();
    else
      result = QuickInfoHelper.toString(this, "containerStorageNamePairs", m_ContainerStorageNamePairs, "pairs: ");

    return result;
  }

  /**
   * Processes the container.
   *
   * @param cont	the container to process
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String processContainer(AbstractContainer cont) {
    Iterator<String> 		names;
    String			name;
    Object			value;
    StorageName			sname;
    Map<String,StorageName>	pairs;

    names = cont.names();

    pairs = new HashMap<>();
    for (BaseKeyValuePair pair: m_ContainerStorageNamePairs)
      pairs.put(pair.getPairKey(), new StorageName(pair.getPairValue()));

    while (names.hasNext()) {
      name  = names.next();
      value = cont.getValue(name);
      if (!pairs.isEmpty()) {
	if (pairs.containsKey(name))
	  getStorageHandler().getStorage().put(pairs.get(name), value);
      }
      else if (m_RegExp.isMatch(name) && cont.hasValue(name)) {
	sname = new StorageName(Storage.toValidName(m_Prefix + name));
	getStorageHandler().getStorage().put(sname, value);
      }
    }

    return null;
  }
}
