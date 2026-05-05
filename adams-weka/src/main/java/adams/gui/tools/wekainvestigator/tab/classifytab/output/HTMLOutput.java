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
 * HTMLOutput.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.core.Shortening;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.actualvspredictedprocessor.ActualVsPredictedHtmlProcessor;
import adams.flow.transformer.actualvspredictedprocessor.ClassifierErrors;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BrowserHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MultiPagePane;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import com.github.fracpete.javautils.enumerate.Enumerated;
import weka.classifiers.Evaluation;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Generates a self-contained HTML file from actual vs predicted data using the specified processor.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class HTMLOutput
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = 3326496263530951885L;

  /** the HTML processor. */
  protected ActualVsPredictedHtmlProcessor m_Processor;

  /** the output file to generate. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a self-contained HTML file from actual vs predicted data using the specified processor.\n"
      + "When not explicitly specifying a file name, one will get generated automatically using the "
      + "users temp directory. These files will get automatically deleted when application/JVM shut "
      + "down in a controlled manner.\n"
      + "Additional attributes can be associated with the data points and are displayed when hovering "
      + "over a data point in the plot. The format for the hover display is:\n"
      + "Actual: ...\n"
      + "Predicted: ...\n"
      + "Data: <InstIndex>: att1,att2,...";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "processor", "processor",
      new ClassifierErrors());

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile("."));
  }

  /**
   * Sets the HTML processor to use.
   *
   * @param value	the processor
   */
  public void setProcessor(ActualVsPredictedHtmlProcessor value) {
    m_Processor = value;
    reset();
  }

  /**
   * Returns the HTML processor to use.
   *
   * @return		the processor
   */
  public ActualVsPredictedHtmlProcessor getProcessor() {
    return m_Processor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processorTipText() {
    return "The HTML processor to apply to the data.";
  }

  /**
   * Sets the output file to generate.
   *
   * @param value	the output, automatically generated if pointing to a dir
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file to generate.
   *
   * @return		the output, automatically generated if pointing to a dir
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
    return "The output file to generate, gets automatically determined if pointing to a directory.";
  }

  /**
   * The title to use for the tab.
   *
   * @return the title
   */
  @Override
  public String getTitle() {
    return "HTML output";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item the item to check
   * @return true if output can be generated
   */
  @Override
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation()
	     && (item.getEvaluation().predictions() != null)
	     && (item.getEvaluation().getHeader() != null)
	     && (item.getEvaluation().getHeader().classAttribute().isNumeric());
  }


  /**
   * Generates a plot actor from the evaluation.
   *
   * @param index 		the 0-based fold index, -1 if not for fold
   * @param eval		the evaluation to use
   * @param originalIndices 	the original indices, can be null
   * @param additionalAttributes 	the additional attribute to use, can be null
   * @param errors 		for collecting errors
   * @return			the generated panel, null if failed to generate
   */
  protected ComponentContentPanel createOutput(int index, ResultItem item, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, MessageCollection errors) {
    SpreadSheet		sheet;
    ParameterPanel	paramPanel;
    JPanel		panel;
    FileChooserPanel	fileChooser;
    BaseButton		buttonOpen;
    String		title;
    String		html;
    File 		outputFile;
    final String	url;

    sheet = PredictionHelper.toSpreadSheet(this, errors, eval, originalIndices, additionalAttributes, false);
    if (sheet == null) {
      if (errors.isEmpty())
	errors.add("Failed to generate prediction!");
      return null;
    }

    if (m_Processor.getTitle().isEmpty()) {
      title = Shortening.shortenEnd(item.getEvaluation().getHeader().relationName(), 40);
      if (index > -1)
	title += " (" + (index + 1) + ")";
      m_Processor.setTitle(title);
    }
    html = m_Processor.process(sheet, errors);
    if (html == null)
      return null;

    if (m_OutputFile.isDirectory()) {
      outputFile = TempUtils.createTempFile("adams-", ".html");
      outputFile.deleteOnExit();
    }
    else {
      outputFile = m_OutputFile;
    }
    if (index > -1)
      outputFile = FileUtils.replaceExtension(outputFile, "-" + (index+1) + ".html");

    if (!FileUtils.writeToFile(outputFile.getAbsolutePath(), html))
      return null;

    paramPanel = new ParameterPanel();

    fileChooser = new FileChooserPanel();
    fileChooser.setCurrent(outputFile);
    fileChooser.setEnabled(false);
    paramPanel.addParameter("Output file", fileChooser);

    url = outputFile.toURI().toString();
    buttonOpen = new BaseButton(ImageManager.getIcon("browser"));
    buttonOpen.addActionListener((ActionEvent e) -> BrowserHelper.openURL(url));
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.add(buttonOpen);
    paramPanel.addParameter("Open in browser", panel);

    return new ComponentContentPanel(paramPanel, true);
  }

  /**
   * Generates output from the item.
   *
   * @param item   the item to generate output for
   * @param errors for collecting error messages
   * @return the output component, null if failed to generate
   */
  @Override
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    MultiPagePane multiPage;

    if (item.hasFoldEvaluations()) {
      multiPage = newMultiPagePane(item);
      addPage(multiPage, "Full", createOutput(-1, item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors), 0);
      for (Enumerated<Evaluation> eval: enumerate(item.getFoldEvaluations()))
	addPage(multiPage, "Fold " + (eval.index + 1), createOutput(eval.index, item, item.getFoldEvaluation(eval.index), item.getFoldOriginalIndices(eval.index), item.getAdditionalAttributes(), errors), eval.index + 1);
      if (multiPage.getPageCount() > 0)
	multiPage.setSelectedIndex(0);
      return multiPage;
    }
    else {
      return createOutput(-1, item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors);
    }
  }
}
