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
 * Histogram.java
 * Copyright (C) 2012-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.core.Index;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.visualization.stats.histogram.HistogramOptions;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Generates a histogram based on the incoming data.
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
 * &nbsp;&nbsp;&nbsp;default: Histogram
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
 * <pre>-short-title (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
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
 * <pre>-index &lt;adams.core.Index&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The attribute index in case of spreadsheet being displayed.
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 *
 * <pre>-options &lt;adams.gui.visualization.stats.histogram.HistogramOptions&gt; (property: options)
 * &nbsp;&nbsp;&nbsp;The display options for the histogram.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.histogram.HistogramOptions -paintlet adams.gui.visualization.stats.paintlet.HistogramPaintlet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Histogram
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider, TextSupplier, SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 8145699633341614296L;

  /** the attribute index, in case of Instances. */
  protected Index m_Index;

  /** the name for the data. */
  protected String m_DataName;

  /** the histogram options themselves. */
  protected HistogramOptions m_Options;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a histogram based on the incoming data.";
  }

  /**
   * Adds options to the internal list of operations
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "index", "index",
      new Index(Index.FIRST));

    m_OptionManager.add(
      "data-name", "dataName",
      "");

    m_OptionManager.add(
      "options", "options",
      new HistogramOptions());
  }

  /**
   * Sets the attribute index in case of Instances data.
   *
   * @param value	the index
   */
  public void setIndex(Index value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the attirbute index in case of Instances data.
   *
   * @return		the index
   */
  public Index getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property
   */
  public String indexTipText() {
    return "The attribute index in case of spreadsheet being displayed.";
  }

  /**
   * Sets the attribute index in case of Instances data.
   *
   * @param value	the index
   */
  public void setDataName(String value) {
    m_DataName = value;
    reset();
  }

  /**
   * Returns the attirbute index in case of Instances data.
   *
   * @return		the index
   */
  public String getDataName() {
    return m_DataName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property
   */
  public String dataNameTipText() {
    return "The name to display on the x-axis, overrides the attribute name obtained from the Instances.";
  }

  /**
   * Sets the display options.
   *
   * @param value	the options
   */
  public void setOptions(HistogramOptions value) {
    m_Options = value;
    reset();
  }

  /**
   * Returns the display options
   *
   * @return		the options
   */
  public HistogramOptions getOptions() {
    return m_Options;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property
   */
  public String optionsTipText() {
    return "The display options for the histogram.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Double[].class, SpreadSheet.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    adams.gui.visualization.stats.histogram.Histogram	hist;
    SpreadSheet 					data;

    hist = (adams.gui.visualization.stats.histogram.Histogram) m_Panel;

    if (token.getPayload() instanceof SpreadSheet) {
      data = (SpreadSheet) token.getPayload();
      m_Index.setMax(data.getColumnCount());
      hist.setIndex(m_Index.getIntIndex());
      hist.setData(data);
    }
    else {
      hist.setArray((Double[]) token.getPayload());
    }
    hist.setDataName(m_DataName);
    hist.update();
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return true;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    adams.gui.visualization.stats.histogram.Histogram	hist;

    if (m_Panel != null) {
      hist = (adams.gui.visualization.stats.histogram.Histogram) m_Panel;
      hist.setData(null);
      hist.setArray(null);
      hist.update();
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    adams.gui.visualization.stats.histogram.Histogram	result;

    result = new adams.gui.visualization.stats.histogram.Histogram();
    result.setOptions(m_Options);

    return result;
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractTextAndComponentDisplayPanel("Histogram") {
      private static final long serialVersionUID = 4360182045245637304L;
      protected adams.gui.visualization.stats.histogram.Histogram m_Histogram;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_Histogram = new adams.gui.visualization.stats.histogram.Histogram();
	m_Histogram.setOptions(m_Options);
	add(m_Histogram, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	if (token.getPayload() instanceof SpreadSheet) {
	  SpreadSheet data = (SpreadSheet) token.getPayload();
	  m_Index.setMax(data.getColumnCount());
	  m_Histogram.setData(data);
	  m_Histogram.setIndex(m_Index.getIntIndex());
	}
	else {
	  m_Histogram.setArray((Double[]) token.getPayload());
	}
	m_Histogram.setDataName(m_DataName);
	m_Histogram.update();
      }
      @Override
      public void clearPanel() {
	m_Histogram.setData(null);
	m_Histogram.setArray(null);
	m_Histogram.update();
      }
      @Override
      public JComponent supplyComponent() {
	return m_Histogram;
      }
      @Override
      public String getCustomSupplyTextMenuItemCaption() {
	return ((adams.gui.visualization.stats.histogram.Histogram) m_Panel).getCustomSupplyTextMenuItemCaption();
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
	return ((adams.gui.visualization.stats.histogram.Histogram) m_Panel).getCustomTextFileFilter();
      }
      @Override
      public String supplyText() {
	return ((adams.gui.visualization.stats.histogram.Histogram) m_Panel).supplyText();
      }
      @Override
      public void cleanUp() {
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  @Override
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    if (m_Panel == null)
      return null;
    else
      return ((adams.gui.visualization.stats.histogram.Histogram) m_Panel).toSpreadSheet();
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption() {
    if (m_Panel == null)
      return "Save histogram as...";
    else
      return ((adams.gui.visualization.stats.histogram.Histogram) m_Panel).getCustomSupplyTextMenuItemCaption();
  }

  /**
   * Returns a custom file filter for the file chooser.
   *
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter() {
    if (m_Panel == null)
      return new ExtensionFileFilter("CSV files", "csv");
    else
      return ((adams.gui.visualization.stats.histogram.Histogram) m_Panel).getCustomTextFileFilter();
  }

  /**
   * Supplies the text. May get called even if actor hasn't been executed yet.
   *
   * @return		the text, null if none available
   */
  public String supplyText() {
    if (m_Panel == null)
      return null;
    else
      return ((adams.gui.visualization.stats.histogram.Histogram) m_Panel).supplyText();
  }
}
