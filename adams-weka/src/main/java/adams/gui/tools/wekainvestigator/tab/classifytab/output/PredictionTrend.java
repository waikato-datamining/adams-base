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
 * PredictionTrend.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.Token;
import adams.flow.sink.AbstractDisplayPanel;
import adams.flow.sink.SimplePlot;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.CustomColorProvider;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import weka.classifiers.Evaluation;

import java.awt.Color;
import java.util.HashMap;

/**
 * Generates a 'prediction trend' for classifier errors: sorts the
 * predictions on the actual value and plots actual and predicted side-by-side.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PredictionTrend
  extends AbstractOutputGeneratorWithSeparateFoldsSupport<ComponentContentPanel> {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the paintlet. */
  protected XYSequencePaintlet m_Paintlet;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a 'prediction trend' for classifier errors: sorts the "
      + "predictions on the actual value and plots actual and predicted side-by-side.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "paintlet", "paintlet",
      getDefaultPaintlet());

    m_OptionManager.add(
      "color-provider", "colorProvider",
      getDefaultColorProvider());
  }

  /**
   * Returns the default paintlet to use.
   *
   * @return		the paintlet
   */
  protected XYSequencePaintlet getDefaultPaintlet() {
    LinePaintlet	result;

    result = new LinePaintlet();
    result.setPaintAll(true);

    return result;
  }

  /**
   * Sets the paintlet to use for the plot.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(XYSequencePaintlet value) {
    m_Paintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use for the plot.
   *
   * @return		the paintlet
   */
  public XYSequencePaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The paintlet to use for the plot.";
  }

  /**
   * Returns the default color provider to use.
   *
   * @return		the default
   */
  protected ColorProvider getDefaultColorProvider() {
    CustomColorProvider 	result;

    result = new CustomColorProvider();
    result.setColors(new Color[]{Color.BLUE, Color.RED});

    return result;
  }

  /**
   * Sets the color provider for the plots.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider for the plots.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider to use for the plot colors.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Prediction trend";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation()
      && (item.getEvaluation().getHeader().classAttribute().isNumeric())
      && (item.getEvaluation().predictions() != null);
  }

  /**
   * Generates the output for the evaluation.
   *
   * @param eval	the evaluation to use
   * @param errors	for collecting errors
   * @return		the generated output
   */
  protected ComponentContentPanel createOutput(Evaluation eval, MessageCollection errors) {
    SpreadSheet			sheet;
    SimplePlot			plot;
    AbstractDisplayPanel	panel;
    SequencePlotterContainer[] 	conts;
    Row				row;
    int				i;
    int				n;

    sheet = PredictionHelper.toSpreadSheet(
      this, errors, eval, null, null, false, false, false, false, false);
    if (sheet == null) {
      if (errors.isEmpty())
	errors.add("Failed to generate predictions!");
      return null;
    }

    // sort by actual
    sheet.sort(0, true);

    plot = new SimplePlot();
    plot.setTitle("Trend");
    plot.setPaintlet((XYSequencePaintlet) m_Paintlet.shallowCopy());
    plot.setColorProvider(m_ColorProvider.shallowCopy());
    plot.setMouseClickAction(new ViewDataClickAction());
    plot.getAxisX().setLabel("index");
    plot.getAxisY().setLabel("class value");

    panel  = plot.createDisplayPanel(null);
    conts = new SequencePlotterContainer[2];
    for (i = 0; i < sheet.getRowCount(); i++) {
      row      = sheet.getRow(i);
      conts[0] = new SequencePlotterContainer("actual", i+1.0, row.getCell(0).toDouble());
      conts[1] = new SequencePlotterContainer("predicted", i+1.0, row.getCell(1).toDouble());
      for (SequencePlotterContainer cont : conts) {
        // add meta-data
	if (sheet.getColumnCount() > 2) {
	  cont.setValue(SequencePlotterContainer.VALUE_METADATA, new HashMap<String,Object>());
	  for (n = 2; n < sheet.getColumnCount(); n++)
	    cont.addMetaData(sheet.getColumnName(n), row.getCell(n).getNative());
	}
	panel.display(new Token(cont));
      }
    }
    panel.wrapUp();

    return new ComponentContentPanel(panel, false);
  }
}
