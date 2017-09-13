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
 * FileTailer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Monitors a text file for data being appended, e.g., log files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: FileTailer
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
 * <pre>-end &lt;boolean&gt; (property: end)
 * &nbsp;&nbsp;&nbsp;If enabled, will start monitoring from the end of the file, otherwise the 
 * &nbsp;&nbsp;&nbsp;start.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-delay &lt;int&gt; (property: delay)
 * &nbsp;&nbsp;&nbsp;The delay in milliseconds.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileTailer
  extends AbstractTransformer {

  private static final long serialVersionUID = 8148843753177357097L;

  public static class Listener
    extends TailerListenerAdapter {

    /** the owner. */
    protected FileTailer m_Owner;

    /** the monitored file. */
    protected String m_File;

    /**
     * Initializes the owner.
     *
     * @param owner	the owning actor
     */
    public Listener(FileTailer owner, String file) {
      m_Owner = owner;
      m_File  = file;
    }

    /**
     * Handles a line from a Tailer.
     * @param line the line.
     */
    @Override
    public void handle(String line) {
      m_Owner.addLine(line);
    }

    /**
     * Handles an Exception .
     * @param ex the exception.
     */
    @Override
    public void handle(Exception ex) {
      m_Owner.getLogger().log(Level.SEVERE, "Error tailing file: " + m_File, ex);
    }
  }

  /** whether to tail from the end or beginning of file. */
  protected boolean m_End;

  /** the delay in msec. */
  protected int m_Delay;

  /** the tailer instance. */
  protected transient Tailer m_Tailer;

  /** the queue with the data. */
  protected List<String> m_Queue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Monitors a text file for data being appended, e.g., log files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "end", "end",
      true);

    m_OptionManager.add(
      "delay", "delay",
      100, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Queue = new ArrayList<>();
  }

  /**
   * Sets whether to start from the end or beginning.
   *
   * @param value	true if from end
   */
  public void setEnd(boolean value) {
    m_End = value;
    reset();
  }

  /**
   * Returns whether to start from the end or beginning.
   *
   * @return		true if from end
   */
  public boolean getEnd() {
    return m_End;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTipText() {
    return "If enabled, will start monitoring from the end of the file, otherwise the start.";
  }

  /**
   * Sets the delay in milliseconds.
   *
   * @param value	the delay
   */
  public void setDelay(int value) {
    m_Delay = value;
    reset();
  }

  /**
   * Returns the delay in milliseconds.
   *
   * @return		the delay
   */
  public int getDelay() {
    return m_Delay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delayTipText() {
    return "The delay in milliseconds.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "end", (m_End ? "from end" : "from start"));
    result += QuickInfoHelper.toString(this, "delay", m_Delay, ", delay: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile 	file;
    Listener		listener;

    result = null;

    file = null;
    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else if (m_InputToken.getPayload() instanceof File)
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    else
      result = "Unhandled input type: " + Utils.classToString(m_InputToken.getPayload());

    if (file != null) {
      listener = new Listener(this, file.getAbsolutePath());
      m_Tailer = Tailer.create(file.getAbsoluteFile(), listener, m_Delay, m_End);
      new Thread(m_Tailer).start();
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Tailer != null) {
      m_Tailer.stop();
      m_Tailer = null;
    }
    super.stopExecution();
  }

  /**
   * Adds the line to the output.
   *
   * @param line	the line to add
   */
  public void addLine(String line) {
    m_Queue.add(line);
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Tailer != null);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    while (!isStopped() && m_Queue.isEmpty())
      Utils.wait(this, 1000, 100);
    if (m_Queue.isEmpty())
      return null;
    else
      return new Token(m_Queue.remove(0));
  }
}
