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
 * Screenshot.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.io.File;
import java.util.Hashtable;

import javax.swing.JComponent;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.InputConsumer;
import adams.gui.print.NullWriter;

/**
 <!-- globalinfo-start -->
 * Actor that takes screenshots of graphical components.
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Screenshot
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
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title of the dialog.
 * &nbsp;&nbsp;&nbsp;default: Screenshot
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;An optional suffix for the filename, inserted before the extension.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The output directory.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-writer &lt;adams.gui.print.JComponentWriter [options]&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 *
 * <pre>-provider &lt;adams.flow.sink.DisplayPanelProvider [options]&gt; (property: panelProvider)
 * &nbsp;&nbsp;&nbsp;The actor for generating the display panels to take a screenshot of.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.ClassifierErrors -writer adams.gui.print.NullWriter
 * </pre>
 *
 * <pre>-filename-prefix &lt;java.lang.String&gt; (property: filenamePrefix)
 * &nbsp;&nbsp;&nbsp;The prefix for the filename in case of auto-generation (no path, just name
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: screenshot
 * </pre>
 *
 * <pre>-filename-var &lt;java.lang.String&gt; (property: filenameVariable)
 * &nbsp;&nbsp;&nbsp;The variable to use for the filename instead of the auto-generated one.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Screenshot
  extends AbstractGraphicsGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 4613925594824175758L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_COUNTER = "counter";

  /** the actor to use for generating panels. */
  protected DisplayPanelProvider m_PanelProvider;

  /** the prefix for the auto-generated filename. */
  protected String m_FilenamePrefix;

  /** the variable to use as filename. */
  protected VariableName m_FilenameVariable;

  /** the counter for the screenshots. */
  protected int m_Counter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor that takes screenshots of graphical components.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "provider", "panelProvider",
	    new ImageViewer());

    m_OptionManager.add(
	    "filename-prefix", "filenamePrefix",
	    "screenshot");

    m_OptionManager.add(
	    "filename-var", "filenameVariable",
	    new VariableName());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	variable;
    String	output;
    String	prefix;
    String	result;

    variable = QuickInfoHelper.getVariable(this, "filenameVariable");

    if (variable != null) {
      result = variable;
    }
    else if ((m_FilenameVariable != null) && (m_FilenameVariable.getValue().length() > 0)) {
      result = m_FilenameVariable.paddedValue();
    }
    else {
      output = QuickInfoHelper.toString(this, "output", m_Output);
      prefix = QuickInfoHelper.toString(this, "filenamePrefix", m_FilenamePrefix);

      if (!(getWriter() instanceof NullWriter)) {
	result = output + File.separator + prefix + "XYZ";
        if (getWriter().getExtensions().length > 0)
          result += getWriter().getExtensions()[0];
      }
      else {
	result = output;
      }
    }

    return result;
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

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Counter = 0;
  }

  /**
   * Returns the default title for the dialog.
   *
   * @return		the default title
   */
  @Override
  protected String getDefaultTitle() {
    return "Screenshot";
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputTipText() {
    return "The output directory.";
  }

  /**
   * Sets the panel provider to use for generating the panels.
   *
   * @param value	the panel provider to use
   */
  public void setPanelProvider(DisplayPanelProvider value) {
    m_PanelProvider = value;
    reset();
  }

  /**
   * Returns the panel provider in use for generating the panels.
   *
   * @return		the panel provider in use
   */
  public DisplayPanelProvider getPanelProvider() {
    return m_PanelProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String panelProviderTipText() {
    return "The actor for generating the display panels to take a screenshot of.";
  }

  /**
   * Sets the prefix for the filename in case of auto-generation.
   *
   * @param value	the prefix (just name, no path)
   */
  public void setFilenamePrefix(String value) {
    m_FilenamePrefix = value;
    reset();
  }

  /**
   * Returns the prefix for the filename in case of auto-generation.
   *
   * @return		the panel provider in use
   */
  public String getFilenamePrefix() {
    return m_FilenamePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenamePrefixTipText() {
    return "The prefix for the filename in case of auto-generation (no path, just name).";
  }

  /**
   * Sets the variable to use for generating the filename instead of the
   * auto-generated one.
   *
   * @param value	the variable name (without the @{ and })
   */
  public void setFilenameVariable(VariableName value) {
    m_FilenameVariable = value;
    reset();
  }

  /**
   * Returns the variable to use for generating the filename instead of the
   * auto-generated one.
   *
   * @return		the panel provider in use
   */
  public VariableName getFilenameVariable() {
    return m_FilenameVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenameVariableTipText() {
    return "The variable to use for the filename instead of the auto-generated one.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Evaluation.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    if ((m_PanelProvider != null) && (m_PanelProvider instanceof InputConsumer))
      return ((InputConsumer) m_PanelProvider).accepts();
    else
      return new Class[]{Object.class};
  }

  /**
   * Generates the component to display in the frame.
   *
   * @return		the component
   */
  @Override
  protected JComponent generateComponent() {
    return (JComponent) m_PanelProvider.createDisplayPanel(m_InputToken);
  }

  /**
   * Generates the filename for the output.
   *
   * @return		the file
   */
  @Override
  protected PlaceholderFile generateFilename() {
    PlaceholderFile	result;

    m_Counter++;

    if (getVariables().has(m_FilenameVariable.getValue())) {
      result = new PlaceholderFile(
	  getVariables().get(m_FilenameVariable.getValue()));
    }
    else {
      result = new PlaceholderFile(
	  m_Output.getAbsolutePath() + File.separator
	  + FileUtils.createFilename(m_FilenamePrefix + m_Counter + m_Writer.getExtensions()[0], "_"));
    }

    return result;
  }
}
