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
 * JavaExec.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.SystemInfo;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.management.ClassPathAugmenter;
import adams.core.management.Launcher;
import adams.core.management.LoggingObjectOutputPrinter;
import adams.core.management.RecordingOutputPrinter;
import adams.flow.control.AbstractControlActor;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.FixedNameActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.sink.Null;

/**
 <!-- globalinfo-start -->
 * Forks off a new JVM with the same classpath by default. The classpath can be extended using classpath augmenters. Additional JVM options can be supplied as well, apart from the heap size.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: JavaExec
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
 * <pre>-java-class &lt;java.lang.String&gt; (property: javaClass)
 * &nbsp;&nbsp;&nbsp;The java class to execute.
 * &nbsp;&nbsp;&nbsp;default: adams.core.SystemInfo
 * </pre>
 * 
 * <pre>-memory &lt;java.lang.String&gt; (property: memory)
 * &nbsp;&nbsp;&nbsp;The amount of memory to start the JVM with.
 * &nbsp;&nbsp;&nbsp;default: 256m
 * </pre>
 * 
 * <pre>-jvm &lt;adams.core.base.BaseString&gt; [-jvm ...] (property: JVM)
 * &nbsp;&nbsp;&nbsp;The additional options for the JVM.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-option &lt;adams.core.base.BaseString&gt; [-option ...] (property: options)
 * &nbsp;&nbsp;&nbsp;The options for the Java class.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-classpath-augmenter &lt;adams.core.management.ClassPathAugmenter&gt; [-classpath-augmenter ...] (property: classPathAugmenters)
 * &nbsp;&nbsp;&nbsp;The classpath augmenters to use when launching the Java process.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-record-output (property: recordOutput)
 * &nbsp;&nbsp;&nbsp;If enabled, the output of stdout&#47;stderr gets recorded and forwarded (as 
 * &nbsp;&nbsp;&nbsp;string) to the appropriate sub-actor for further processing.
 * </pre>
 * 
 * <pre>-stdout &lt;adams.flow.core.AbstractActor&gt; (property: stdOut)
 * &nbsp;&nbsp;&nbsp;The actor for further processing the stdout output (string).
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Null -name stdout
 * </pre>
 * 
 * <pre>-stderr &lt;adams.flow.core.AbstractActor&gt; (property: stdErr)
 * &nbsp;&nbsp;&nbsp;The actor for further processing the stderr output (string).
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Null -name stderr
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JavaExec
  extends AbstractControlActor 
  implements FixedNameActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6073217549744299278L;

  /** the name for stdout. */
  public final static String NAME_STDOUT = "stdout";
  
  /** the name for stderr. */
  public final static String NAME_STDERR = "stderr";
  
  /** the class to run. */
  protected String m_JavaClass;
  
  /** the amount of memory to use. */
  protected String m_Memory;
  
  /** additional options for the jvm. */
  protected BaseString[] m_JVM;
  
  /** additional options for the java class. */
  protected BaseString[] m_Options;
  
  /** classpath augmenters. */
  protected ClassPathAugmenter[] m_ClassPathAugmenters;
  
  /** whether to record the output of the process. */
  protected boolean m_RecordOutput;

  /** the stdout actor. */
  protected AbstractActor m_StdOut;

  /** the stderr actor. */
  protected AbstractActor m_StdErr;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Forks off a new JVM with the same classpath by default. The "
	+ "classpath can be extended using classpath augmenters. "
	+ "Additional JVM options can be supplied as well, apart from the "
	+ "heap size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "java-class", "javaClass",
	    SystemInfo.class.getName());

    m_OptionManager.add(
	    "memory", "memory",
	    "256m");

    m_OptionManager.add(
	    "jvm", "JVM",
	    new BaseString[0]);

    m_OptionManager.add(
	    "option", "options",
	    new BaseString[0]);

    m_OptionManager.add(
	    "classpath-augmenter", "classPathAugmenters",
	    new ClassPathAugmenter[0]);

    m_OptionManager.add(
	    "record-output", "recordOutput",
	    false);

    m_OptionManager.add(
	    "stdout", "stdOut",
	    new Null());

    m_OptionManager.add(
	    "stderr", "stdErr",
	    new Null());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_StdOut = new Null();
    m_StdErr = new Null();
  }
  
  /**
   * Sets the java class to execute.
   *
   * @param value	the class name
   */
  public void setJavaClass(String value) {
    m_JavaClass = value;
    reset();
  }

  /**
   * Returns the java class to execute.
   *
   * @return 		the class name
   */
  public String getJavaClass() {
    return m_JavaClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String javaClassTipText() {
    return "The java class to execute.";
  }

  /**
   * Sets the amount of memory to use for the JVM.
   *
   * @param value	the amount
   */
  public void setMemory(String value) {
    m_Memory = value;
    reset();
  }

  /**
   * Returns the amount of memory to use for the JVM.
   *
   * @return 		the amount
   */
  public String getMemory() {
    return m_Memory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String memoryTipText() {
    return "The amount of memory to start the JVM with.";
  }

  /**
   * Sets the additional options for the JVM.
   *
   * @param value	the options
   */
  public void setJVM(BaseString[] value) {
    m_JVM = value;
    reset();
  }

  /**
   * Returns the additional options for the JVM.
   *
   * @return 		the options
   */
  public BaseString[] getJVM() {
    return m_JVM;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String JVMTipText() {
    return "The additional options for the JVM.";
  }

  /**
   * Sets the options for the Java class.
   *
   * @param value	the options
   */
  public void setOptions(BaseString[] value) {
    m_Options = value;
    reset();
  }

  /**
   * Returns the options for the Java class.
   *
   * @return 		the options
   */
  public BaseString[] getOptions() {
    return m_Options;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String optionsTipText() {
    return "The options for the Java class.";
  }

  /**
   * Sets the classpath augmenters.
   *
   * @param value	the augmenters
   */
  public void setClassPathAugmenters(ClassPathAugmenter[] value) {
    m_ClassPathAugmenters = value;
    reset();
  }

  /**
   * Returns the classpath augmenters.
   *
   * @return 		the augmenters
   */
  public ClassPathAugmenter[] getClassPathAugmenters() {
    return m_ClassPathAugmenters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String classPathAugmentersTipText() {
    return "The classpath augmenters to use when launching the Java process.";
  }

  /**
   * Sets whether to record stdout/stderr output of process.
   *
   * @param value	true if to record
   */
  public void setRecordOutput(boolean value) {
    m_RecordOutput = value;
    reset();
  }

  /**
   * Returns whether to record stdout/stderr output of process.
   *
   * @return 		true if recorded
   */
  public boolean getRecordOutput() {
    return m_RecordOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String recordOutputTipText() {
    return "If enabled, the output of stdout/stderr gets recorded and forwarded (as string) to the appropriate sub-actor for further processing.";
  }

  /**
   * Sets the actor for further processing the stdout output (string).
   *
   * @param value	the actor
   */
  public void setStdOut(AbstractActor value) {
    if (value instanceof InputConsumer) {
      m_StdOut = value;
      m_StdOut.setName(NAME_STDOUT);
      updateParent();
      reset();
    }
    else {
      getLogger().severe("stdout actor must consume input, " + value.getClass().getName() + " doesn't!");
    }
  }

  /**
   * Returns the actor for further processing the stdout output (string).
   *
   * @return 		the actor
   */
  public AbstractActor getStdOut() {
    return m_StdOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String stdOutTipText() {
    return "The actor for further processing the stdout output (string).";
  }

  /**
   * Sets the actor for further processing the stderr output (string).
   *
   * @param value	the actor
   */
  public void setStdErr(AbstractActor value) {
    if (value instanceof InputConsumer) {
      m_StdErr = value;
      m_StdErr.setName(NAME_STDERR);
      updateParent();
      reset();
    }
    else {
      getLogger().severe("stderr actor must consume input, " + value.getClass().getName() + " doesn't!");
    }
  }

  /**
   * Returns the actor for further processing the stderr output (string).
   *
   * @return 		the actor
   */
  public AbstractActor getStdErr() {
    return m_StdErr;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String stdErrTipText() {
    return "The actor for further processing the stderr output (string).";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, false, ActorExecution.PARALLEL, true, new Class[]{InputConsumer.class}, false);
  }

  /**
   * Returns the size of the group.
   *
   * @return		the size, always 2
   */
  @Override
  public int size() {
    return 2;
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public AbstractActor get(int index) {
    if (index == 0)
      return m_StdOut;
    else if (index == 1)
      return m_StdErr;
    else
      throw new IllegalArgumentException("Invalid index: " + index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    if (index == 0)
      setStdOut(actor);
    else if (index == 1)
      setStdErr(actor);
    else
      throw new IllegalArgumentException("Invalid index: " + index);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    if (m_StdOut.getName().equals(actor))
      return 0;
    else if (m_StdErr.getName().equals(actor))
      return 1;
    else
      return -1;
  }

  /**
   * Returns the name for the sub-actor at this position.
   * 
   * @param index	the position of the sub-actor
   * @return		the name to use
   */
  public String getFixedName(int index) {
    if (index == 0)
      return NAME_STDOUT;
    else if (index == 1)
      return NAME_STDERR;
    else
      throw new IllegalArgumentException("Invalid index: " + index);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result = QuickInfoHelper.toString(this, "javaClass", m_JavaClass);
    
    value = QuickInfoHelper.toString(this, "memory", m_Memory, " -Xmx");
    if (value != null)
      result += value;
    
    value = QuickInfoHelper.toString(this, "JVM", Utils.flatten(m_JVM, " "), " ");
    if (value != null)
      result += value;
    
    value = QuickInfoHelper.toString(this, "options", Utils.flatten(m_Options, " "), " ");
    if (value != null)
      result += value;
    
    value = QuickInfoHelper.toString(this, "recordOutput", m_RecordOutput, "record output", ", ");
    if (value != null)
      result += value;
    
    return result;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Launcher	launcher;
    String[]	options;
    int		i;
    Token	input;
    
    launcher = new Launcher();
    launcher.ignoreEnvironmentOptions();
    launcher.suppressErrorDialog();
    launcher.setMainClass(m_JavaClass);
    launcher.setMemory(m_Memory);
    for (BaseString opt: m_JVM)
      launcher.addJVMOption(opt.getValue());
    for (ClassPathAugmenter cpa: m_ClassPathAugmenters)
      launcher.addClassPathAugmentations(cpa);
    options = new String[m_Options.length];
    for (i = 0; i < m_Options.length; i++)
      options[i] = m_Options[i].getValue();
    launcher.setArguments(options);
    launcher.setConsoleObject(this);
    if (m_RecordOutput)
      launcher.setOutputPrinter(RecordingOutputPrinter.class);
    else
      launcher.setOutputPrinter(LoggingObjectOutputPrinter.class);
    result = launcher.execute();
    
    // further process output?
    if (m_RecordOutput) {
      // stdout
      if (result == null) {
	try {
	  input = new Token(((RecordingOutputPrinter) launcher.getStdOut().getPrinter()).getRecording());
	  ((InputConsumer) m_StdOut).input(input);
	  result = m_StdOut.execute();
	}
	catch (Exception e) {
	  result = handleException("Failed to process data with stdout sub-actor!", e);
	}
      }

      // stderr
      if (result == null) {
	try {
	  input = new Token(((RecordingOutputPrinter) launcher.getStdErr().getPrinter()).getRecording());
	  ((InputConsumer) m_StdErr).input(input);
	  result = m_StdErr.execute();
	}
	catch (Exception e) {
	  result = handleException("Failed to process data with stderr sub-actor!", e);
	}
      }
    }

    return result;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_StdOut instanceof ActorHandler)
      ((ActorHandler) m_StdOut).flushExecution();
    if (m_StdErr instanceof ActorHandler)
      ((ActorHandler) m_StdErr).flushExecution();
  }
}
