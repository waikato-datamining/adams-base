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
 * FourInOneDisplay.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;
import adams.core.Index;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.fourinone.FourInOne;
import adams.gui.visualization.stats.fourinone.VersusFitOptions;
import adams.gui.visualization.stats.fourinone.VersusOrderOptions;
import adams.gui.visualization.stats.histogram.HistogramOptions;
import adams.gui.visualization.stats.probabilityplot.NormalPlotOptions;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a four-in-one plot. Contains a histogram, a normal probability plot, vs fit plot and vs order plot
 * <p/>
 <!-- globalinfo-end -->
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
 * &nbsp;&nbsp;&nbsp;default: FourInOneDisplay
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 1200
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 750
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 *
 * <pre>-actual-attribute &lt;java.lang.String&gt; (property: actualAttribute)
 * &nbsp;&nbsp;&nbsp;1-based index of the actual attribute
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-predicted-attribute &lt;java.lang.String&gt; (property: predictedAttribute)
 * &nbsp;&nbsp;&nbsp;1-based index of the predicted attribute
 * &nbsp;&nbsp;&nbsp;default: 2
 * </pre>
 *
 * <pre>-normal-plot-options &lt;adams.gui.visualization.stats.probabilityplot.NormalPlotOptions&gt; (property: normalPlotOptions)
 * &nbsp;&nbsp;&nbsp;options for normal plot
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.probabilityplot.NormalPlotOptions
 * </pre>
 *
 * <pre>-versus-fit-options &lt;adams.gui.visualization.stats.fourinone.VersusFitOptions&gt; (property: versusFitOptions)
 * &nbsp;&nbsp;&nbsp;options for the versus fit graph
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.fourinone.VersusFitOptions -paintlet adams.gui.visualization.stats.paintlet.VsFitPaintlet
 * </pre>
 *
 * <pre>-histogram-options &lt;adams.gui.visualization.stats.histogram.HistogramOptions&gt; (property: histogramOptions)
 * &nbsp;&nbsp;&nbsp;options for the histogram graph
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.histogram.HistogramOptions -paintlet adams.gui.visualization.stats.paintlet.HistogramPaintlet
 * </pre>
 *
 * <pre>-versus-order-options &lt;adams.gui.visualization.stats.fourinone.VersusOrderOptions&gt; (property: versusOrderOptions)
 * &nbsp;&nbsp;&nbsp;options for the versus order graph
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.fourinone.VersusOrderOptions -paintlet adams.gui.visualization.stats.paintlet.VsOrderPaintlet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class FourInOneDisplay
  extends AbstractGraphicalDisplay{

  /** for serialization */
  private static final long serialVersionUID = -5847391335658516849L;

  /** panel to display the actor */
  protected FourInOne m_Plot;

  /**options for the normal plot */
  protected NormalPlotOptions m_NormalPlotOptions;

  /**Options for the histogram */
  protected HistogramOptions m_HistogramOptions;

  /**Options for the vs fit */
  protected VersusFitOptions m_VersusFitOptions;

  /**Options for the vs order */
  protected VersusOrderOptions m_VersusOrderOptions;

  /** the 0-based index of the actual attribute. */
  protected String m_Act;

  /** the 0-based index of the predicted attribute. */
  protected String m_pred;

  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    //actual value attribute
    m_OptionManager.add(
	"actual-attribute", "actualAttribute", "1");

    //Predicted value attribute
    m_OptionManager.add(
	"predicted-attribute", "predictedAttribute", "2");

    //Options for normal plot
    m_OptionManager.add(
	"normal-plot-options", "normalPlotOptions", new NormalPlotOptions());

    //options for vs fit
    m_OptionManager.add(
	"versus-fit-options", "versusFitOptions", new VersusFitOptions());

    //Options for histogram
    m_OptionManager.add(
	"histogram-options", "histogramOptions", new HistogramOptions());

    //options for vs order
    m_OptionManager.add(
	"versus-order-options", "versusOrderOptions", new VersusOrderOptions());
  }

  /**
   * Set the string to use as an index for the actual attribute
   * @param val			String for the index
   */
  public void setActualAttribute(String val) {
    m_Act = val;
    reset();
  }

  /**
   * Get the string used for the index for the actual attribute
   * @return			String for the index
   */
  public String getActualAttribute() {
    return m_Act;
  }

  /**
   * Tip text for the actual attribute property
   * @return			String describing the property
   */
  public String actualAttributeTipText() {
    return "1-based index of the actual attribute";
  }

  /**
   * Set the string to use as an index for the predicted attribute
   * @param val			String for the index
   */
  public void setPredictedAttribute(String val) {
    m_pred = val;
    reset();
  }

  /**
   * Get the string used for the index for the predicted attribute
   * @return			String for the index
   */
  public String getPredictedAttribute() {
    return m_pred;
  }

  /**
   * Tip text for the predicted attribute property
   * @return			String describing the property
   */
  public String predictedAttributeTipText() {
    return "1-based index of the predicted attribute";
  }


  /**
   * Set the options for the histogram
   * @param val			Histogramoptions object containing the options
   */
  public void setHistogramOptions(HistogramOptions val) {
    m_HistogramOptions = val;
    reset();
  }

  /**
   * Get the options for the histogram
   * @return			Histogramoptions object containing the options for the histogram
   */
  public HistogramOptions getHistogramOptions() {
    return m_HistogramOptions;
  }

  /**
   * Tip text for the histogram options property
   * @return			String describing the property
   */
  public String histogramOptionsTipText() {
    return "options for the histogram graph";
  }

  /**
   * Set the options for the vs order plot
   * @param val			Versusorderoptions object containing the options
   */
  public void setVersusOrderOptions(VersusOrderOptions val) {
    m_VersusOrderOptions = val;
    reset();
  }

  /**
   * Get the options for the vsorder plot
   * @return				Versusorder object containing the options
   */
  public VersusOrderOptions getVersusOrderOptions() {
    return m_VersusOrderOptions;
  }

  /**
   * Tip text for the vsorder options property
   * @return				String describing the property
   */
  public String versusOrderOptionsTipText() {
    return "options for the versus order graph";
  }

  /**
   * Set the options for the vsfit plot
   * @param val			Vsfiroptions object containing the options
   */
  public void setVersusFitOptions(VersusFitOptions val) {
    m_VersusFitOptions = val;
    reset();
  }

  /**
   * Get the options for the vsfit plot
   * @return			vsfitoptions object containing the options
   */
  public VersusFitOptions getVersusFitOptions() {
    return m_VersusFitOptions;
  }

  /**
   * Tip text for the vs fit options property
   * @return			String describing the property
   */
  public String versusFitOptionsTipText() {
    return "options for the versus fit graph";
  }

  /**
   * Set the options for the normal plot
   * @param val			Normalplotoptions object containing the options
   */
  public void setNormalPlotOptions(NormalPlotOptions val) {
    m_NormalPlotOptions = val;
    reset();
  }

  /**
   * get the options for the normal plot
   * @return			Normalplotoptions object containing the options
   */
  public NormalPlotOptions getNormalPlotOptions() {
    return m_NormalPlotOptions;
  }

  /**
   * tip text for the normalplot options property
   * @return			String describing the property
   */
  public String normalPlotOptionsTipText() {
    return "options for normal plot";
  }

  @Override
  public void clearPanel() {
    if (m_Plot != null) {
      Instances temp = new Instances("Empty", new ArrayList<Attribute>(), 0);
      m_Plot.setInstances(temp);
    }
  }

  @Override
  protected BasePanel newPanel() {
    m_Plot = new FourInOne();
    return m_Plot;
  }

  @Override
  protected void display(Token token) {
    m_Plot.setInstances((Instances)token.getPayload());
    m_Plot.setAct(new Index(m_Act));
    m_Plot.setPred(new Index(m_pred));
    m_Plot.setOptions(m_HistogramOptions, m_VersusFitOptions, m_VersusOrderOptions, m_NormalPlotOptions);
    m_Plot.reset();
  }

  @Override
  protected int getDefaultWidth() {
    return 1200;
  }

  @Override
  protected int getDefaultHeight() {
    return 750;
  }


  @Override
  public String globalInfo() {
    return "Actor for displaying a four-in-one plot. Contains a histogram, a normal probability plot, vs fit plot and vs order plot";
  }
}