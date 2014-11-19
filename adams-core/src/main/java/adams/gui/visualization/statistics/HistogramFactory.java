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
 * HistogramFactory.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.statistics;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.StatUtils;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.core.TranslucentColorProvider;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.BarPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;

/**
 * A factory for histogram related objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistogramFactory {

  /** whether to use JMathPlot's BarPlot instead of HistogramPlot. */
  public final static boolean USE_BARPLOT = false;

  /**
   * A panel for displaying a histogram based on the GC data of a chromatogram.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Panel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = -1990327944906647098L;

    /** the plot panel. */
    protected SequencePlotterPanel m_PlotPanel;

    /**
     * For initializing the GUI.
     */
    @Override
    protected void initGUI() {
      BarPaintlet	paintlet;
      
      super.initGUI();

      setLayout(new BorderLayout());
      paintlet = new BarPaintlet();
      paintlet.setWidth(10);
      m_PlotPanel = new SequencePlotterPanel("Histogram");
      m_PlotPanel.setColorProvider(new TranslucentColorProvider());
      m_PlotPanel.setPaintlet(paintlet);
      m_PlotPanel.getPlot().getAxis(Axis.BOTTOM).setTickGenerator(new FancyTickGenerator());
      m_PlotPanel.getPlot().getAxis(Axis.BOTTOM).setNumberFormat("0");
      m_PlotPanel.getPlot().getAxis(Axis.LEFT).setTickGenerator(new FancyTickGenerator());
      m_PlotPanel.getPlot().getAxis(Axis.LEFT).setNumberFormat("0.0");
      add(m_PlotPanel, BorderLayout.CENTER);
    }

    /**
     * Generates from the specified data using the configured ArrayHistogram
     * object and adds the plot.
     *
     * @param hist	for generating the histogram data
     * @param data	the data to generate the histogram from
     * @param name	the name of the tab
     */
    public void add(ArrayHistogram hist, double[] data, String name) {
      add(hist, StatUtils.toNumberArray(data), name);
    }

    /**
     * Generates from the specified data using the configured ArrayHistogram
     * object and adds the plot.
     *
     * @param hist	for generating the histogram data
     * @param data	the data to generate the histogram from
     * @param name	the name of the tab
     */
    public void add(ArrayHistogram hist, Number[] data, String name) {
      ArrayHistogram			histogram;
      StatisticContainer 		cont;
      int				numBins;
      int				i;
      XYSequenceContainerManager	manager;
      XYSequenceContainer		seqcont;
      XYSequence			seq;

      // generate the histogram data
      histogram = (ArrayHistogram) hist.shallowCopy(true);
      histogram.add(data);
      cont = histogram.calculate();

      // generate data for plot
      numBins = (Integer) cont.getMetaData(ArrayHistogram.METADATA_NUMBINS);
      manager = m_PlotPanel.getContainerManager();
      manager.startUpdate();
      seq = new XYSequence();
      seq.setID((manager.count()+1) + ": " + numBins + " bins");
      seq.setComparison(Comparison.X);
      for (i = 0; i < numBins; i++)
	seq.add(new XYSequencePoint((double) i, (Double) cont.getCell(0, i)));
      seqcont = manager.newContainer(seq);
      manager.add(seqcont);
      manager.finishUpdate();

      histogram.destroy();
    }
  }

  /**
   * A dialog that queries the user about parameters for displaying histograms.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SetupDialog
    extends GenericObjectEditorDialog {

    /** for serialization. */
    private static final long serialVersionUID = 4215632076348292959L;

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
   * @param modality	the type of modality
     */
    public SetupDialog(java.awt.Dialog owner, ModalityType modality) {
      super(owner, modality);
    }

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modal	if true then the dialog will be modal
     */
    public SetupDialog(java.awt.Frame owner, boolean modal) {
      super(owner, modal);
    }

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      getGOEEditor().setClassType(AbstractArrayStatistic.class);
      getGOEEditor().setCanChangeClassInDialog(false);
      setCurrent(new ArrayHistogram());
    }
  }

  /**
   * Dialog for displaying histograms.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Dialog
    extends BaseDialog {

    /** for serialization. */
    private static final long serialVersionUID = 2464157048335973279L;

    /** the tabbed pane for displaying the histograms. */
    protected BaseTabbedPane m_TabbedPane;

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
   * @param modality	the type of modality
     */
    public Dialog(java.awt.Dialog owner, ModalityType modality) {
      super(owner, modality);
    }

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modal	if true then the dialog will be modal
     */
    public Dialog(java.awt.Frame owner, boolean modal) {
      super(owner, modal);
    }

    /**
     * initializes the GUI.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setTitle("Histogram");
      getContentPane().setLayout(new BorderLayout());
      m_TabbedPane = new BaseTabbedPane();
      getContentPane().add(m_TabbedPane, BorderLayout.CENTER);

      setSize(new Dimension(800, 600));
    }

    /**
     * Removes all the tabs.
     */
    public void clear() {
      m_TabbedPane.removeAll();
    }

    /**
     * Generates from the specified data using the configured ArrayHistogram
     * object and adds the plot.
     *
     * @param hist	for generating the histogram data
     * @param data	the data to generate the histogram from
     * @param name	the name of the tab
     */
    public void add(ArrayHistogram hist, double[] data, String name) {
      add(hist, StatUtils.toNumberArray(data), name);
    }

    /**
     * Generates from the specified data using the configured ArrayHistogram
     * object and adds the plot.
     *
     * @param hist	for generating the histogram data
     * @param data	the data to generate the histogram from
     * @param name	the name of the tab
     */
    public void add(ArrayHistogram hist, Number[] data, String name) {
      Panel	panel;

      panel = new HistogramFactory.Panel();
      panel.add(hist, data, name);
      m_TabbedPane.addTab(name, panel);
    }
  }

  /**
   * Returns an instance of a new panel for displaying histograms.
   *
   * @return		the panel
   */
  public static Panel getPanel() {
    return new Panel();
  }

  /**
   * Returns an instance of a setup dialog for displaying histograms.
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static SetupDialog getSetupDialog(java.awt.Dialog owner, ModalityType modality) {
    return new SetupDialog(owner, modality);
  }

  /**
   * Returns an instance of a setup dialog for displaying histograms.
   *
   * @param owner	the owning component
   * @param modal	if true then the dialog will be modal
   * @return		the dialog
   */
  public static SetupDialog getSetupDialog(java.awt.Frame owner, boolean modal) {
    return new SetupDialog(owner, modal);
  }

  /**
   * Returns a new dialog for displaying histograms.
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Dialog owner, ModalityType modality) {
    return new Dialog(owner, modality);
  }

  /**
   * Returns a new dialog for displaying displaying histograms.
   *
   * @param owner	the owning component
   * @param modal	if true then the dialog will be modal
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Frame owner, boolean modal) {
    return new Dialog(owner, modal);
  }
}
