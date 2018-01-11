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
 * FourInOnePlot.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.transformer.WekaPredictionsToSpreadSheet;
import adams.gui.core.BaseTabbedPane;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.visualization.stats.fourinone.VersusFit;
import adams.gui.visualization.stats.fourinone.VersusFitOptions;
import adams.gui.visualization.stats.fourinone.VersusOrder;
import adams.gui.visualization.stats.fourinone.VersusOrderOptions;
import adams.gui.visualization.stats.histogram.Histogram;
import adams.gui.visualization.stats.histogram.HistogramOptions;
import adams.gui.visualization.stats.probabilityplot.NormalPlot;
import adams.gui.visualization.stats.probabilityplot.NormalPlotOptions;

import javax.swing.JComponent;

/**
 * Generates the 4-in-1 plot: normal plot, histogram, residuals vs fit and vs order.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FourInOnePlot
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** whether to use absolute errors. */
  protected boolean m_UseAbsoluteError;

  /**options for the normal plot */
  protected NormalPlotOptions m_NormalPlotOptions;

  /**Options for the histogram */
  protected HistogramOptions m_HistogramOptions;

  /**Options for the vs fit */
  protected VersusFitOptions m_VersusFitOptions;

  /**Options for the vs order */
  protected VersusOrderOptions m_VersusOrderOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the 4-in-1 plot: normal plot, histogram, residuals vs fit and vs order.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "absolute-error", "useAbsoluteError",
      false);

    m_OptionManager.add(
      "normal-plot-options", "normalPlotOptions",
      new NormalPlotOptions());

    m_OptionManager.add(
      "versus-fit-options", "versusFitOptions",
      new VersusFitOptions());

    m_OptionManager.add(
      "histogram-options", "histogramOptions",
      new HistogramOptions());

    m_OptionManager.add(
      "versus-order-options", "versusOrderOptions",
      new VersusOrderOptions());
  }

  /**
   * Sets whether to use an absolute error (ie no direction).
   *
   * @param value	true if to use absolute error
   */
  public void setUseAbsoluteError(boolean value) {
    m_UseAbsoluteError = value;
    reset();
  }

  /**
   * Returns whether to use an absolute error (ie no direction).
   *
   * @return		true if to use absolute error
   */
  public boolean getUseAbsoluteError() {
    return m_UseAbsoluteError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAbsoluteErrorTipText() {
    return "If set to true, then the error will be absolute (no direction).";
  }

  /**
   * Set the options for the histogram.
   *
   * @param value			Histogramoptions object containing the options
   */
  public void setHistogramOptions(HistogramOptions value) {
    m_HistogramOptions = value;
    reset();
  }

  /**
   * Get the options for the histogram.
   *
   * @return			Histogramoptions object containing the options for the histogram
   */
  public HistogramOptions getHistogramOptions() {
    return m_HistogramOptions;
  }

  /**
   * Tip text for the histogram options property.
   *
   * @return			String describing the property
   */
  public String histogramOptionsTipText() {
    return "options for the histogram graph";
  }

  /**
   * Set the options for the vs order plot.
   *
   * @param value			Versusorderoptions object containing the options
   */
  public void setVersusOrderOptions(VersusOrderOptions value) {
    m_VersusOrderOptions = value;
    reset();
  }

  /**
   * Get the options for the vsorder plot.
   *
   * @return				Versusorder object containing the options
   */
  public VersusOrderOptions getVersusOrderOptions() {
    return m_VersusOrderOptions;
  }

  /**
   * Tip text for the vsorder options property.
   *
   * @return				String describing the property
   */
  public String versusOrderOptionsTipText() {
    return "options for the versus order graph";
  }

  /**
   * Set the options for the vsfit plot.
   *
   * @param value			Vsfiroptions object containing the options
   */
  public void setVersusFitOptions(VersusFitOptions value) {
    m_VersusFitOptions = value;
    reset();
  }

  /**
   * Get the options for the vsfit plot.
   *
   * @return			vsfitoptions object containing the options
   */
  public VersusFitOptions getVersusFitOptions() {
    return m_VersusFitOptions;
  }

  /**
   * Tip text for the vs fit options property.
   *
   * @return			String describing the property
   */
  public String versusFitOptionsTipText() {
    return "options for the versus fit graph";
  }

  /**
   * Set the options for the normal plot.
   *
   * @param value			Normalplotoptions object containing the options
   */
  public void setNormalPlotOptions(NormalPlotOptions value) {
    m_NormalPlotOptions = value;
    reset();
  }

  /**
   * get the options for the normal plot.
   *
   * @return			Normalplotoptions object containing the options
   */
  public NormalPlotOptions getNormalPlotOptions() {
    return m_NormalPlotOptions;
  }

  /**
   * tip text for the normalplot options property.
   *
   * @return			String describing the property
   */
  public String normalPlotOptionsTipText() {
    return "options for normal plot";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "4-in-1 plot";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation() && (item.getEvaluation().predictions() != null);
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    BaseTabbedPane			tabbedPane;
    WekaPredictionsToSpreadSheet	p2s;
    Token				token;
    SpreadSheet				sheet;
    NormalPlot				normalPlot;
    Histogram				histogram;
    VersusFit				vsFit;
    VersusOrder				vsOrder;

    tabbedPane = new BaseTabbedPane();

    p2s = new WekaPredictionsToSpreadSheet();
    p2s.setShowError(true);
    p2s.setUseAbsoluteError(m_UseAbsoluteError);
    p2s.input(new Token(item.getEvaluation()));
    try {
      p2s.execute();
    }
    catch (Exception e) {
      errors.add("Failed to assemble predictions!", e);
      return null;
    }
    token = p2s.output();
    sheet = (SpreadSheet) token.getPayload();

    normalPlot = new NormalPlot();
    normalPlot.setData(sheet);
    normalPlot.setIndex(2);  // resid
    normalPlot.setOptions(m_NormalPlotOptions);
    tabbedPane.addTab("Normal plot", normalPlot);

    histogram = new Histogram();
    histogram.setData(sheet);
    histogram.setIndex(2);  // resid
    histogram.setOptions(m_HistogramOptions);
    tabbedPane.addTab("Histogram", histogram);

    vsFit = new VersusFit();
    vsFit.setData(sheet);
    vsFit.setIndices(2, 1);  // resid, pred
    vsFit.setOptions(m_VersusFitOptions);
    tabbedPane.addTab("Versus Fit", vsFit);

    vsOrder = new VersusOrder();
    vsOrder.setData(sheet);
    vsOrder.setIndex(2);  // resid
    vsOrder.setOptions(m_VersusOrderOptions);
    tabbedPane.addTab("Versus Order", vsOrder);

    return new ComponentContentPanel(tabbedPane, true);
  }
}
