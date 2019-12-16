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
 * CallableActorScreenshot.java
 * Copyright (C) 2011-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.io.AbstractFilenameGenerator;
import adams.core.io.DefaultFilenameGenerator;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.ControlActor;
import adams.flow.core.DataPlotUpdaterSupporter;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.sink.ComponentSupplier;
import adams.gui.print.JComponentWriter;
import adams.gui.print.NullWriter;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Takes a screenshot of a callable actor whenever a token passes through.<br>
 * The screenshot can either be written to disk (and the input token is forwarded), or the screenshot is forwarded as adams.data.image.BufferedImageContainer (dropping the input token).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: CallableActorScreenshot
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
 * <pre>-callable &lt;adams.flow.core.CallableActorReference&gt; (property: callableName)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor to use.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-output-type &lt;FILE|BUFFEREDIMAGE_CONTAINER&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;The type of output to generate.
 * &nbsp;&nbsp;&nbsp;default: FILE
 * </pre>
 * 
 * <pre>-filename-generator &lt;adams.core.io.AbstractFilenameGenerator&gt; (property: filenameGenerator)
 * &nbsp;&nbsp;&nbsp;The filename generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.DefaultFilenameGenerator
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CallableActorScreenshot
  extends AbstractActor
  implements ControlActor, InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = -7346814880631564292L;

  /**
   * Determines how to output the screenshot.
   */
  public enum OutputType {
    FILE,
    BUFFEREDIMAGE_CONTAINER,
  }

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_COUNTER = "counter";

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the input token. */
  protected Token m_InputToken;

  /** the output token. */
  protected Token m_OutputToken;

  /** the output type. */
  protected OutputType m_OutputType;

  /** the writer to use. */
  protected JComponentWriter m_Writer;

  /** the filename generator to use. */
  protected AbstractFilenameGenerator m_FilenameGenerator;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** the counter for the screenshots. */
  protected int m_Counter;

  /** for storing any exceptions while trying to create a screenshot. */
  protected String m_ScreenshotResult;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Takes a screenshot of a callable actor whenever a token passes through.\n"
	+ "The screenshot can either be written to disk (and the input token is forwarded), "
	+ "or the screenshot is forwarded as " + BufferedImageContainer.class.getName() + " (dropping the input token).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "callable", "callableName",
	    new CallableActorReference(CallableActorReference.UNKNOWN));

    m_OptionManager.add(
	    "output-type", "outputType",
	    OutputType.FILE);

    m_OptionManager.add(
	    "filename-generator", "filenameGenerator",
	    new DefaultFilenameGenerator());

    m_OptionManager.add(
	    "writer", "writer",
	    new NullWriter());
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

    m_Counter     = 0;
    m_InputToken  = null;
    m_OutputToken = null;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_COUNTER);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_COUNTER, m_Counter);

    if (m_InputToken != null)
      result.put(BACKUP_INPUT, m_InputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_COUNTER)) {
      m_Counter = (Integer) state.get(BACKUP_COUNTER);
      state.remove(BACKUP_COUNTER);
    }

    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "callableName", m_CallableName, "callable: ");
    result += QuickInfoHelper.toString(this, "outputType", m_OutputType, ", output: ");
    result += QuickInfoHelper.toString(this, "filenameGenerator", m_FilenameGenerator, ", generator: ");
    
    return result;
  }

  /**
   * Sets the name of the callable actor to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText() {
    return "The name of the callable actor to use.";
  }

  /**
   * Sets how to output the screenshot.
   *
   * @param value 	the type
   */
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns how to output the screenshot.
   *
   * @return 		the type
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "The type of output to generate.";
  }

  /**
   * Sets the prefix for the filename in case of auto-generation.
   *
   * @param value	the prefix (just name, no path)
   */
  public void setFilenameGenerator(AbstractFilenameGenerator value) {
    m_FilenameGenerator = value;
    reset();
  }

  /**
   * Returns the prefix for the filename in case of auto-generation.
   *
   * @return		the panel provider in use
   */
  public AbstractFilenameGenerator getFilenameGenerator() {
    return m_FilenameGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenameGeneratorTipText() {
    return "The filename generator to use.";
  }

  /**
   * Sets the writer.
   *
   * @param value 	the writer
   */
  public void setWriter(JComponentWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer.
   *
   * @return 		the writer
   */
  public JComponentWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for generating the graphics output.";
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getCallableName());
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    HashSet<String>	variables;

    result = super.setUp();

    if (result == null) {
      m_CallableActor = findCallableActor();
      if (m_CallableActor == null) {
        result = "Couldn't find callable actor '" + getCallableName() + "'!";
      }
      else {
	if (!(m_CallableActor instanceof ComponentSupplier)) {
	  result = "Callable actor '" + getCallableName() + "' is not a " + ComponentSupplier.class.getName() + "!";
	}
	else {
	  variables = findVariables(m_CallableActor);
	  m_DetectedVariables.addAll(variables);
	  if (m_DetectedVariables.size() > 0)
	    getVariables().addVariableChangeListener(this);
	}
      }
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Does nothing.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_InputToken  = token;
    m_OutputToken = null;
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_InputToken;
  }

  /**
   * Generates the filename for the output.
   *
   * @return		the file
   */
  protected PlaceholderFile generateFilename() {
    PlaceholderFile	result;

    m_Counter++;
    result = new PlaceholderFile(m_FilenameGenerator.generate(m_Counter));

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Runnable		run;

    if ((m_Writer instanceof NullWriter) && (m_OutputType == OutputType.FILE)) {
      m_OutputToken = m_InputToken;
      return "No writer defined, cannot generate screenshot!";
    }
    
    m_ScreenshotResult = null;

    if (!isHeadless()) {
      run = () -> {
	synchronized(m_CallableActor) {
	  // force update
	  if (m_CallableActor instanceof DataPlotUpdaterSupporter)
	    ((DataPlotUpdaterSupporter) m_CallableActor).updatePlot();
	  JComponent comp = ((ComponentSupplier) m_CallableActor).supplyComponent();
	  if ((comp != null) && (comp.getWidth() > 0) && (comp.getHeight() > 0)) {
	    switch (m_OutputType) {
	      case FILE:
		PlaceholderFile filename = generateFilename();
		getLogger().info("Saving to: " + filename);
		m_Writer.setComponent(comp);
		m_Writer.setFile(filename);
		try {
		  m_Writer.toOutput();
		}
		catch (Exception e) {
		  m_ScreenshotResult = handleException("Failed to generate screenshot ('" + filename + "'): ", e);
		}
		m_Writer.setComponent(null);
		break;

	      case BUFFEREDIMAGE_CONTAINER:
		// take screenshot
		BufferedImage img = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setPaintMode();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, comp.getWidth(), comp.getHeight());
		comp.printAll(g);
		// create token
		BufferedImageContainer cont = new BufferedImageContainer();
		cont.setContent(img);
		cont.getReport().setStringValue("Callable actor", m_CallableName.toString());
		cont.getReport().setStringValue("Timestamp", DateUtils.getTimeFormatterMsecs().format(new Date()));
		cont.getReport().setNumericValue("Width", comp.getWidth());
		cont.getReport().setNumericValue("Height", comp.getHeight());
		m_OutputToken = new Token(cont);
		break;

	      default:
		getLogger().severe("Unhandled output type: " + m_OutputType);
	    }
	  }
	  synchronized(m_Self) {
	    try {
	      m_Self.notifyAll();
	    }
	    catch (Exception e) {
	      handleException("Failed to notify all", e);
	    }
	  }
	}
      };

      synchronized(m_Self) {
	SwingUtilities.invokeLater(run);
	try {
	  m_Self.wait();
	}
	catch (Exception e) {
	  handleException("Failed to wait", e);
	}
      }
    }

    result             = m_ScreenshotResult;
    m_ScreenshotResult = null;
    if (m_OutputType == OutputType.FILE)
      m_OutputToken = m_InputToken;

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String execute() {
    String	result;

    result = super.execute();
    
    if (m_Skip)
      m_OutputToken = m_InputToken;
    
    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result        = m_OutputToken;
    m_OutputToken = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <br><br>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_InputToken  = null;
    m_OutputToken = null;

    super.wrapUp();
  }
}
