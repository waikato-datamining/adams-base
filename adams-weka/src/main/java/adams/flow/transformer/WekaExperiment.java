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
 * WekaExperiment.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.WekaExperimentFile;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Represents a Weka experiment, stored in a file. Can be setup from inside the flow.<br/>
 * Via the input port, additional datasets can be inserted into the experiment. This allows one to define a template experiment and then just run it over various datasets without every having to change the experiment setup itself.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.experiment.Experiment<br/>
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
 * &nbsp;&nbsp;&nbsp;default: Experiment
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
 * <pre>-file &lt;adams.data.ExperimentFile&gt; (property: experimentFile)
 * &nbsp;&nbsp;&nbsp;The file the experiment is stored in.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-no-check (property: noCheck)
 * &nbsp;&nbsp;&nbsp;If set to true, the experiment file must not be present at setUp-time; necessary
 * &nbsp;&nbsp;&nbsp;if file gets generated on-the-fly.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperiment
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -1127504846960163422L;

  /** the experiment file. */
  protected WekaExperimentFile m_ExperimentFile;

  /** whether not to check for experiment file to exist (e.g., when it generated on the fly). */
  protected boolean m_NoCheck;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Represents a Weka experiment, stored in a file. Can be setup from "
      + "inside the flow.\n"
      + "Via the input port, additional datasets can be inserted into the "
      + "experiment. This allows one to define a template experiment and "
      + "then just run it over various datasets without every having to "
      + "change the experiment setup itself.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "experimentFile",
	    new WekaExperimentFile("."));

    m_OptionManager.add(
	    "no-check", "noCheck",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "experimentFile", m_ExperimentFile);
  }

  /**
   * Sets the file the experiment is stored in.
   *
   * @param value	the file
   */
  public void setExperimentFile(WekaExperimentFile value) {
    m_ExperimentFile = value;
    reset();
  }

  /**
   * Returns the file the experiment is stored in.
   *
   * @return		the file
   */
  public WekaExperimentFile getExperimentFile() {
    return m_ExperimentFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String experimentFileTipText() {
    return "The file the experiment is stored in.";
  }

  /**
   * Sets whether to avoid the check at setUp time whether the experiment file
   * is present or not.
   *
   * @param value	true if the file need not be there
   */
  public void setNoCheck(boolean value) {
    m_NoCheck = value;
    reset();
  }

  /**
   * Returns whether to avoid the check at setUp time whether the experiment file
   * is present or not.
   *
   * @return		true if the file need not be there
   */
  public boolean getNoCheck() {
    return m_NoCheck;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCheckTipText() {
    return
        "If set to true, the experiment file must not be present at setUp-time; "
      + "necessary if file gets generated on-the-fly.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class, java.io.File.class, java.io.File[].class, java.lang.String.class, java.lang.String[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class, File.class, File[].class, String.class, String[].class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!m_NoCheck) {
	if (!m_ExperimentFile.exists())
	  result = "Experiment file '" + m_ExperimentFile.getAbsolutePath() + "' does not exist!";
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    weka.experiment.Experiment	exp;
    File[]			tmpFiles;
    List<File>			files;
    String[]			tmpStr;
    int				i;

    result = null;

    // load experiment
    try {
      exp = weka.experiment.Experiment.read(m_ExperimentFile.getAbsolutePath());
    }
    catch (Exception e) {
      exp    = null;
      result = handleException("Failed to read experiment: " + m_ExperimentFile, e);
    }

    // more datasets to add?
    if (m_InputToken != null) {
      // get files
      files = new ArrayList<File>();
      if (m_InputToken.getPayload() instanceof File) {
	files.add(((File) m_InputToken.getPayload()).getAbsoluteFile());
      }
      else if (m_InputToken.getPayload() instanceof File[]) {
	tmpFiles = (File[]) m_InputToken.getPayload();
	for (i = 0; i < tmpFiles.length; i++)
	  files.add(tmpFiles[i].getAbsoluteFile());
      }
      else if (m_InputToken.getPayload() instanceof String) {
	files.add(new PlaceholderFile((String) m_InputToken.getPayload()).getAbsoluteFile());
      }
      else if (m_InputToken.getPayload() instanceof String[]) {
	tmpStr = (String[]) m_InputToken.getPayload();
	for (i = 0; i < tmpStr.length; i++)
	  files.add(new PlaceholderFile(tmpStr[i]).getAbsoluteFile());
      }

      // add files
      if (isLoggingEnabled())
	getLogger().info("Adding files: " + files);
      for (i = 0; i < files.size(); i++)
	exp.getDatasets().addElement(files.get(i));
    }

    // run experiment
    if (result == null) {
      try {
	if (isLoggingEnabled())
	  getLogger().info("Initializing experiment...");
	exp.initialize();

	if (isLoggingEnabled())
	  getLogger().info("Running experiment...");
	exp.runExperiment();

	if (isLoggingEnabled())
	  getLogger().info("Post-processing experiment...");
	exp.postProcess();

	if (isLoggingEnabled())
	  getLogger().info("Finished experiment!");

	m_OutputToken = new Token(exp);
      }
      catch (Exception e) {
	result = handleException("Failed execute experiment: " + m_ExperimentFile, e);
      }
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.experiment.Experiment.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{weka.experiment.Experiment.class};
  }
}
