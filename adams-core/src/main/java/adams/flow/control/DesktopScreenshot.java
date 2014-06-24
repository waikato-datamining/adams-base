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
 * DesktopScreenshot.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.flow.core.AbstractActor;
import adams.flow.core.ControlActor;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Takes a screenshot of the desktop whenever a token passes through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: DesktopScreenshot
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-file &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The output file; the extension determines the file format.
 * &nbsp;&nbsp;&nbsp;default: screenshot.png
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DesktopScreenshot
  extends AbstractActor
  implements ControlActor, InputConsumer, OutputProducer, FileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -7346814880631564292L;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the input token. */
  protected Token m_InputToken;

  /** the output token. */
  protected Token m_OutputToken;

  /** the output file to use. */
  protected PlaceholderFile m_OutputFile;

  /** for storing any exceptions while trying to create a screenshot. */
  protected String m_ScreenshotResult;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Takes a screenshot of the desktop whenever a token passes through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-file", "outputFile",
	    new PlaceholderFile("screenshot.png"));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_InputToken  = null;
    m_OutputToken = null;
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
    return QuickInfoHelper.toString(this, "outputFile", m_OutputFile);
  }

  /**
   * Sets the output file.
   *
   * @param value 	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return 		the file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The output file; the extension determines the file format.";
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
    m_InputToken = token;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    m_ScreenshotResult = null;

    if (!m_Headless) {
      try {
	Dimension size  = Toolkit.getDefaultToolkit().getScreenSize();
	Rectangle rect  = new Rectangle(size);
	Robot robot = new Robot();
	BufferedImage image = robot.createScreenCapture(rect);
	getLogger().info("Saving to: " + m_OutputFile);
	ImageWriter writer = null;
	Iterator iter = ImageIO.getImageWritersByFormatName(FileUtils.getExtension(m_OutputFile));
	if (iter.hasNext())
	  writer = (ImageWriter) iter.next();
	else
	  throw new Exception("No writer available for " + FileUtils.getExtension(m_OutputFile) + "!");
	ImageOutputStream ios = ImageIO.createImageOutputStream(m_OutputFile.getAbsoluteFile());
	writer.setOutput(ios);
	writer.write(null, new IIOImage(image, null, null), null);
	ios.flush();
	writer.dispose();
	ios.close();
      }
      catch (Exception e) {
	m_ScreenshotResult = handleException("Failed to save screenshot to: " + m_OutputFile, e);
      }
    }

    result             = m_ScreenshotResult;
    m_ScreenshotResult = null;
    m_OutputToken      = m_InputToken;

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
   * <p/>
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
