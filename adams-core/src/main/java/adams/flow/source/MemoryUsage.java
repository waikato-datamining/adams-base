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
 * MemoryUsage.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.flow.core.Token;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Outputs the memory usage.<br>
 * The generated map has the following keys:<br>
 * - used_heap<br>
 * - committed_heap<br>
 * - used_non_heap<br>
 * - committed_non_heap
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MemoryUsage
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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MemoryUsage
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 6241869472082918544L;

  public static final String KEY_USED_HEAP = "used_heap";

  public static final String KEY_COMMITTED_HEAP = "committed_heap";

  public static final String KEY_USED_NON_HEAP = "used_non_heap";

  public static final String KEY_COMMITTED_NON_HEAP = "committed_non_heap";

  /** the memory MX bean. */
  protected transient MemoryMXBean m_Memory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the memory usage.\n"
      + "The generated map has the following keys:\n"
      + "- " + KEY_USED_HEAP + "\n"
      + "- " + KEY_COMMITTED_HEAP + "\n"
      + "- " + KEY_USED_NON_HEAP + "\n"
      + "- " + KEY_COMMITTED_NON_HEAP;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Map.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    java.lang.management.MemoryUsage 	usage;
    Map<String,Long> 			map;

    if (m_Memory == null)
      m_Memory = ManagementFactory.getMemoryMXBean();

    map = new HashMap<>();

    // heap
    usage = m_Memory.getHeapMemoryUsage();
    map.put(KEY_USED_HEAP, usage.getUsed());
    map.put(KEY_COMMITTED_HEAP, usage.getCommitted());

    // non-heap
    usage = m_Memory.getNonHeapMemoryUsage();
    map.put(KEY_USED_NON_HEAP, usage.getUsed());
    map.put(KEY_COMMITTED_NON_HEAP, usage.getCommitted());

    m_OutputToken = new Token(map);

    return null;
  }
}
