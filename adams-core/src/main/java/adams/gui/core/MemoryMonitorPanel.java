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
 * MemoryMonitorPanel.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.NamedCounter;
import adams.core.Properties;
import adams.core.option.AbstractOption;
import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.env.Environment;
import adams.env.MemoryMonitorDefinition;
import adams.flow.sink.sequenceplotter.AbstractSequencePostProcessor;
import adams.flow.sink.sequenceplotter.AligningSequences;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.PaintletWithMarkers;
import adams.gui.visualization.core.axis.AbstractTickGenerator;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.PaintletWithFixedYRange;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Displays the memory consumption.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MemoryMonitorPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 649755316726537053L;

  /** The name of the properties file. */
  public final static String FILENAME = "MemoryMonitor.props";

  /** Contains the properties. */
  protected static Properties m_Properties;
  
  /** for plotting the data. */
  protected SequencePlotterPanel m_PlotPanel;
  
  /** the post-processor. */
  protected AbstractSequencePostProcessor m_PostProcessor;
  
  /** whether the monitoring is running. */
  protected boolean m_Running;
  
  /** performs the actual monitoring. */
  protected Runnable m_Monitor;
  
  /** the memory MX bean. */
  protected MemoryMXBean m_Memory;
  
  /** for keeping track of the tokens. */
  protected NamedCounter m_Counter;
  
  /** the interval for refresh (in msec). */
  protected int m_Interval;
  
  /** the divisor for the bytes. */
  protected double m_Divisor;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Properties	props;
    
    super.initialize();
    
    props           = getProperties();
    m_Memory        = ManagementFactory.getMemoryMXBean();
    m_Counter       = new NamedCounter();
    m_PostProcessor = new AligningSequences();
    ((AligningSequences) m_PostProcessor).setLimit(props.getInteger("Max", 200));
    m_Interval      = props.getInteger("Interval", 1000);
    m_Divisor       = props.getDouble("Divisor", 1024.0*1024.0);
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties				props;
    PaintletWithFixedYRange	paintlet;
    AxisPanelOptions			options;
  
    super.initGUI();
    
    setLayout(new BorderLayout());

    props       = getProperties();
    m_PlotPanel = new SequencePlotterPanel(null);
    m_PlotPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    // bottom
    options = new AxisPanelOptions();
    options.setLabel(props.getPath("Axis.Bottom.Title", "Time"));
    options.setType(Type.valueOf((AbstractOption) null, props.getPath("Axis.Bottom.Type", Type.TIME.toString())));
    options.setTickGenerator(AbstractTickGenerator.forCommandLine(props.getProperty("Axis.Bottom.TickGenerator", new FancyTickGenerator().toString())));
    options.setWidth(props.getInteger("Axis.Bottom.Width", 40));
    options.setNthValueToShow(props.getInteger("Axis.Bottom.NthValueToShow", 2));
    options.setCustomFormat(new DecimalFormatString(props.getPath("Axis.Bottom.Format", "HH:mm")));
    options.configure(m_PlotPanel.getPlot(), Axis.BOTTOM);
    // left
    options = new AxisPanelOptions();
    options.setLabel(props.getPath("Axis.Left.Title", "MB"));
    options.setType(Type.valueOf((AbstractOption) null, props.getPath("Axis.Left.Type", Type.ABSOLUTE.toString())));
    options.setTickGenerator(AbstractTickGenerator.forCommandLine(props.getProperty("Axis.Left.TickGenerator", new FancyTickGenerator().toString())));
    options.setWidth(props.getInteger("Axis.Left.Width", 80));
    options.setNthValueToShow(props.getInteger("Axis.Left.NthValueToShow", 2));
    options.setCustomFormat(new DecimalFormatString(props.getPath("Axis.Left.Format", "0")));
    options.configure(m_PlotPanel.getPlot(), Axis.LEFT);
    m_PlotPanel.getPlot().setZoomingEnabled(false);
    // paintlet
    paintlet = new PaintletWithFixedYRange();
    if (paintlet.getPaintlet() instanceof PaintletWithMarkers)
      ((PaintletWithMarkers) paintlet.getPaintlet()).setMarkersDisabled(true);
    paintlet.setMinY(0.0);
    paintlet.setMaxY(scale(m_Memory.getHeapMemoryUsage().getMax()));
    m_PlotPanel.setDataPaintlet(paintlet);
    // color provider
    m_PlotPanel.setColorProvider(AbstractColorProvider.forCommandLine(props.getProperty("ColorProvider", new DefaultColorProvider().toCommandLine())));
    
    add(m_PlotPanel, BorderLayout.CENTER);
  }
  
  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    m_Monitor = new Runnable() {
      public void run() {
	m_Running = true;
	while (m_Running) {
	  SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
	      update();
	    }
	  });
	  try {
	    synchronized(this) {
	      wait(m_Interval);
	    }
	  }
	  catch (Exception e) {
	    e.printStackTrace();
	    // ignored
	  }
	}
      }
    };
    new Thread(m_Monitor).start();
  }

  /**
   * Scales the value to MB.
   * 
   * @param value	the value to scale
   * @return		the scaled value
   */
  protected double scale(double value) {
    return value / m_Divisor;
  }

  /**
   * Adds to the specified plot.
   */
  protected void add(XYSequenceContainerManager manager, String name, long value) {
    XYSequence		seq;
    XYSequenceContainer	cont;
    XYSequencePoint	point;

    // find or create new plot
    if (manager.indexOf(name) == -1) {
      seq  = new XYSequence();
      seq.setComparison(Comparison.X);
      seq.setID(name);
      cont = manager.newContainer(seq);
      manager.add(cont);
    }
    else {
      cont = manager.get(manager.indexOf(name));
      seq  = cont.getData();
    }

    // create and add new point
    point = new XYSequencePoint("" + System.currentTimeMillis(), new Double(System.currentTimeMillis()), new Double(scale(value)));
    seq.add(point);

    if (manager.indexOf(name) > -1)
      m_PostProcessor.postProcess(manager, name);
  }
  
  /**
   * Updates the plot.
   */
  protected void update() {
    MemoryUsage			usage;
    XYSequenceContainerManager	manager;
    
    usage   = m_Memory.getHeapMemoryUsage();
    manager = m_PlotPanel.getContainerManager();
    
    manager.startUpdate();
    
    add(manager, "Used",      usage.getUsed());
    add(manager, "Committed", usage.getCommitted());
    
    manager.finishUpdate();
  }
  
  /**
   * Stops the monitoring.
   */
  public void stop() {
    m_Running = false;
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(MemoryMonitorDefinition.KEY);

    return m_Properties;
  }
}
