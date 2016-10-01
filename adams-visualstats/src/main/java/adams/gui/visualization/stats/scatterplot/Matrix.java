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
 * Matrix.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericArrayEditorPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.stats.core.SubSample;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCircle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Displays a grid of scatter plots with each attribute plotted against
 * each of the other attributes, also can choose overlays etc.
 *
 * @author msf8
 * @version $Revision$
 */
public class Matrix
extends BasePanel{

  /** for serialization */
  private static final long serialVersionUID = -7593836818545034592L;

  /**Instances for plotting */
  private SpreadSheet m_Data;

  /**Full set of of instances, used when the sub sample changes m_instances */
  private SpreadSheet m_DataOriginal;

  /**Size of each of the scatter plots */
  private int m_PlotSize;

  /** Panel for displaying the scatter plots */
  protected JPanel m_Centre;

  /**for choosing the size of each scatter plot */
  protected JSpinner m_Spin;

  /**for default value for overlays */
  protected AbstractScatterPlotOverlay[] m_Default;

  /** Displays a GAE for choosing overlays for the scatter plots */
  protected GenericArrayEditorPanel m_PanelOverlay;

  /** Displas a GOE for choosing the paintlet for the scatterplots*/
  protected GenericObjectEditorPanel m_PanelPaintlet;

  /**Paintlet for plotting, chosen using GOE */
  protected AbstractScatterPlotPaintlet m_Val;

  /**array list contatining all of the scatter plots displayed in this matrix plot*/
  protected ArrayList< ScatterPlotSimple> m_ScatterPlots;

  /**Overlays to be displayed */
  protected ArrayList<AbstractScatterPlotOverlay> m_Array;

  /**Commandline strings of overlays before new overlays chosen */
  protected HashSet<String> m_OldHash;

  /**Default paintlet for GOEpanel */
  protected AbstractScatterPlotPaintlet m_Def;

  /**panels displaying names of attributes */
  protected ArrayList<JPanel> m_Panels = new ArrayList<JPanel>();

  /**Percentage of sample to use for sum-sample */
  protected int m_Percent;

  /**Spinner for choosing percent of data to sample */
  protected JSpinner m_SpinPercent;

  /**Commandline strings of overlays to be deleted */
  protected HashSet<String> m_DeleteOverlay;

  /** Commandline strings of new overlays to be added */
  protected HashSet<String> m_NewOverlay;

  /**Progress bar for displaying update progress */
  protected JProgressBar m_Bar;

  /** Swing worker for displaying gui during update */
  protected progressWorker m_Work;

  /**Panel for displaying the progress bar */
  protected JPanel m_Progress;

  /**Whether an update is required, if an option has been changed */
  protected boolean m_NeedUpdate;

  /** Button to stop the thread updating the overlays */
  protected JButton m_Stop;

  /**Panel containing the matrix options */
  protected ParameterPanel m_OptionPanel;

  /** If the update was cancelled before it finished */
  boolean m_IsCancel;

  protected void initialize() {
    super.initialize();
    m_PlotSize = 100;
  }

  /**
   * Set the instances to be displayed
   * @param inst		Instances containing the data
   */
  public void setData(SpreadSheet inst) {
    m_DataOriginal = inst;
    m_Data = inst;
  }

  /**
   * Updates the display. Uses the swing worker and shows a progress bar during updating
   */
  public void updateOverlays() {
    m_Progress = new JPanel();
    add(m_Progress, BorderLayout.SOUTH);
    m_Stop = new JButton("Stop");
    m_Stop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
	m_IsCancel = true;
	m_Work.cancel(true);
      }
    });
    m_Progress.add(m_Stop, BorderLayout.WEST);
    m_Bar = new JProgressBar(0, 100);
    m_Bar.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
	if(m_Bar.getValue() >=98) {
	  remove(m_Progress);
	}
      }
    });
    m_Bar.setValue(0);
    m_Bar.setStringPainted(true);
    m_Progress.add(m_Bar, BorderLayout.EAST);
    Matrix.this.revalidate();
    Matrix.this.repaint();
    //can't change the options during updating
    m_OptionPanel.setEnabled(false);

    m_Work = new progressWorker();
    m_Work.addPropertyChangeListener(new prop());
    m_Work.execute();
  }

  /**
   * called when the overlays to display have been changed, sets
   * up the newoverlay hashset so the overlays can be updated
   */
  private void changeOverlay() {
    if(m_OldHash == null) {
      m_OldHash = new HashSet<String>();
    }

    int len = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent()).length;
    HashSet<String> m_NewHash = new HashSet<String>();
    for(int i = 0; i< len; i++) {
      m_NewHash.add(((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent())[i].toCommandLine());
    }
    m_NewOverlay = new HashSet<String>();
    for(String a: m_NewHash) {
      m_NewOverlay.add(a);
    }
    //only keep current overlays that are new
    m_NewOverlay.removeAll(m_OldHash);

    HashSet<String> stilOverlay = new HashSet<String>();
    for(String a: m_OldHash) {
      stilOverlay.add(a);
    }
    //only keep old overlays that are still current
    stilOverlay.retainAll(m_NewHash);

    m_DeleteOverlay = new HashSet<String>();
    for(String a: m_OldHash) {
      m_DeleteOverlay.add(a);
    }
    //only keep old overlays that aren't still current
    m_DeleteOverlay.removeAll(m_NewHash);

    repaint();
    revalidate();
    m_NeedUpdate = true;
  }

  /**
   * Change the paintlet for each scatterplot
   */
  public void redoPaintlets() {
    String newPaintlet;
    m_Val = (AbstractScatterPlotPaintlet)m_PanelPaintlet.getCurrent();
    newPaintlet = m_Val.toCommandLine();

    //for each of the scatterplots in the matrix
    for(int j = 0; j< m_ScatterPlots.size(); j++) {
      AbstractScatterPlotPaintlet temp;
      try {
	//make a new paintlet from the commandline
	temp = (AbstractScatterPlotPaintlet)OptionUtils.forCommandLine(AbstractScatterPlotPaintlet.class, newPaintlet);
      }
      catch(Exception e) {
	//make a circle paintlet if the forcommandline doesn't work
	temp = new ScatterPaintletCircle();
      }
      m_ScatterPlots.get(j).setPaintlet(temp);
      temp.setPanel(m_ScatterPlots.get(j));
      //set the indices of each paintlet i.e. what attributes
      temp.setXIndex(m_ScatterPlots.get(j).m_XIntIndex);
      temp.setYIndex(m_ScatterPlots.get(j).m_YIntIndex);
      temp.setColorIndex(-1);
      temp.setData(m_Data);
    }
    repaint();
    revalidate();
  }

  public void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());

    //GAE for overlays
    m_Default = new AbstractScatterPlotOverlay[]{};
    m_PanelOverlay = new GenericArrayEditorPanel(m_Default);
    m_PanelOverlay.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	changeOverlay();
      }});

    //GOE for choosing paintlet
    m_Def = new ScatterPaintletCircle();
    m_PanelPaintlet = new GenericObjectEditorPanel(AbstractScatterPlotPaintlet.class, m_Def, true);
    m_PanelPaintlet.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	m_NeedUpdate = true;
      }});

    //Panel for the right part of the splitpane for choosing options
    m_OptionPanel = new ParameterPanel();
    m_Centre = new JPanel(new BorderLayout());
    BaseSplitPane splitPane;
    splitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(m_Centre);
    JPanel hold = new JPanel(new BorderLayout());
    hold.add(m_OptionPanel, BorderLayout.NORTH);
    splitPane.setRightComponent(hold);
    //Displays all the right component
    splitPane.setResizeWeight(1.0);
    splitPane.setOneTouchExpandable(true);
    this.add(splitPane, BorderLayout.CENTER);

    //for choosing size of each scatterplot
    SpinnerModel spinSize = new SpinnerNumberModel(m_PlotSize, 20, 500, 4);
    m_Spin = new JSpinner(spinSize);
    m_Spin.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
	m_NeedUpdate = true;
      }});

    //for choosing sub sample percent
    SpinnerModel percentModel = new SpinnerNumberModel(
	m_Percent, 0, 100, 1);
    m_SpinPercent = new JSpinner(percentModel);
    m_SpinPercent.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
	m_NeedUpdate = true;
      }});

    //To update the display
    JButton updateBut = new JButton("update");
    updateBut.addActionListener(new action());

    //Add the options to the parameterpanel
    m_OptionPanel.addParameter("Overlays", m_PanelOverlay);
    m_OptionPanel.addParameter("Paintlet", m_PanelPaintlet);
    m_OptionPanel.addParameter("size of each plot", m_Spin);
    m_OptionPanel.addParameter("percent of sample", m_SpinPercent);
    m_OptionPanel.addParameter("Update matrix", updateBut);
  }

  /**
   * Listener for when the update button is pressed
   * @author msf8
   *
   */
  public class action implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      //if options have changed
      if(m_NeedUpdate) {
	//if the last update wasn't completed fully
	if(m_IsCancel) {
	  //this populates the hashset of overlays to delete
	  changeOverlay();
	  //remove all the overlays that were completed,
	  //will start fresh
	  for(ScatterPlotSimple s: m_ScatterPlots) {
	    s.removeOverlays(m_NewOverlay);
	  }
	  m_IsCancel = false;}
	//update size of each plot
	m_PlotSize = (Integer)m_Spin.getModel().getValue();

	//Take a subsample of the data
	int temp = (Integer)m_SpinPercent.getModel().getValue();
	//if a change in previous sample size
	if(temp != m_Percent) {
	  m_Percent = temp;
	  sample();
	}
	//Paintlets changed
	redoPaintlets();

	reDisplay();
	repaint();
	revalidate();

	updateOverlays();
      }

    }
  }

  /**
   * Updates the progress bar using the "progress" of the swing worker
   * @author msf8
   *
   */
  public class prop implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent evt) {
      if("progress" == evt.getPropertyName()) {
	int prog = (Integer)evt.getNewValue();
	//update progress bar
	m_Bar.setValue(prog);
	Matrix.this.revalidate();
	Matrix.this.repaint();
      }
    }
  }

  /**
   * Take a sample of the data using the provided percentage
   */
  private void sample() {
    //take subsample of the data using original instances
    SubSample subSam = new SubSample(m_DataOriginal, m_Percent);
    try {
      m_Data = subSam.sample();
      //need it to redo overlays now as well, populate the hashset
      //of overlays to add with all the current overlays
      m_NewOverlay = new HashSet<String>();
      int len = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent()).length;
      for(int i = 0; i< len; i++) {
	m_NewOverlay.add(((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent())[i].toCommandLine());
      }
      //empty oldhash so old overlays are removed
      m_OldHash = new HashSet<String>();
    }
    catch(Exception e) {}
    //if scatterplots havn't been created
    if(m_ScatterPlots != null) {
      for(ScatterPlotSimple s: m_ScatterPlots) {
	s.setData(m_Data);
      }
    }
  }


  /**
   * Display the scatterplots
   */
  private void reDisplay() {
    Dimension size;
    //if scatterplots havn't been created so this is first time calling
    //redisplay
    if(m_ScatterPlots == null) {
      m_Centre.removeAll();
      m_Spin.setValue(m_PlotSize);
      m_SpinPercent.setValue(m_Percent);
      m_ScatterPlots = new ArrayList<ScatterPlotSimple>();
      size = new Dimension(m_PlotSize, m_PlotSize);
      //grid for displaying scatterplots, as many rows as there are attributes
      JPanel fullGrid = new JPanel(new GridLayout(m_Data.getColumnCount(), 0));
      //for scrolling the grid
      BaseScrollPane scrollPane = new BaseScrollPane(fullGrid);
      m_Centre.add(scrollPane, BorderLayout.CENTER);
      for(int j = 0; j< m_Data.getColumnCount(); j++) {
	JPanel display = new JPanel();
	display.setBackground(Color.GRAY);
	for(int i = 0; i< m_Data.getColumnCount(); i++) {
	  //if a label, instead of plotting attribute against itself
	  if(j==i)
	  {
	    JPanel temp = new JPanel(new BorderLayout());
	    temp.setBackground(Color.BLACK);
	    JLabel label = new JLabel(m_Data.getColumnName(i));
	    label.setForeground(Color.WHITE);
	    //set maximum possible size for font for label
	    int siz = 22;
	    Font f = new Font("Dialog", Font.PLAIN, siz);
	    label.setFont(f);
	    temp.add(label, BorderLayout.CENTER);
	    temp.setPreferredSize(size);
	    display.add(temp);
	    m_Panels.add(temp);
	  }
	  //if a scatterplot, plotting attribute against another attribute
	  else {
	    ScatterPlotSimple scat = new ScatterPlotSimple();
	    scat.setData(m_Data);
	    scat.setXIntIndex(i);
	    scat.setYIntIndex(j);
	    scat.setPreferredSize(size);
	    scat.update();
	    display.add(scat);
	    m_ScatterPlots.add(scat);
	  }
	}
	//add to the grid of scatter plots
	fullGrid.add(display);
      }
      changeFontSize();
    }

    //if only changing the size, layout already created before
    else {
      size = new Dimension(m_PlotSize, m_PlotSize);
      for(int i = 0; i< m_ScatterPlots.size(); i++) {
	m_ScatterPlots.get(i).setPreferredSize(size);
      }
      for(int i = 0; i< m_Panels.size(); i++) {
	m_Panels.get(i).setPreferredSize(size);
      }
      changeFontSize();
    }
  }

  /**
   * Called by the class that creates this matrix panel once all he fields have been set
   */
  public void reset() {
    sample();
    reDisplay();
    changeOverlay();
    updateOverlays();
    redoPaintlets();
    repaint();
    revalidate();
  }

  /**
   * Change font size for each panel. The size will be such that the longest
   * attribute name will just fit in the panel
   */
  private void changeFontSize() {
    int minSize = 32;
    for(int i = 0; i< m_Panels.size(); i++) {
      //existing label
      JLabel temp = (JLabel)m_Panels.get(i).getComponent(0);
      //panel holding label
      JPanel panel = m_Panels.get(i);
      //set maximum possible size for font for label
      int siz = 32;
      Font f = new Font("Dialog", Font.PLAIN, siz);
      temp.setFont(f);
      while(temp.getPreferredSize().width > panel.getPreferredSize().width) {
	siz-=2;
	f = new Font("Dialog", Font.PLAIN, siz);
	temp.setFont(f);
	if(siz < minSize)
	  minSize = siz;
      }
    }
    //use the font size of the longest attribute name
    Font f = new Font("Dialog", Font.PLAIN, minSize);
    for(int i = 0; i< m_Panels.size(); i++) {
      JPanel panel = m_Panels.get(i);
      JLabel temp = (JLabel)m_Panels.get(i).getComponent(0);
      temp.setFont(f);
      //this part puts the name in the centre of the panel
      JPanel temp1 = new JPanel();
      temp1.setPreferredSize(new Dimension((panel.getPreferredSize().width- temp.getPreferredSize().width)/2,0));
      temp1.setBackground(Color.BLACK);
      panel.add(temp1, BorderLayout.WEST);
    }
  }

  /**
   * Set the paintlet used for plotting the data on each scatter plot
   * @param val			Paintlet used
   */
  public void setPaintlet(AbstractScatterPlotPaintlet val) {
    m_PanelPaintlet.setCurrent(val.shallowCopy());
  }

  /**
   * Set the overlays to be applied to each of the scatter plots
   * @param val			Array of overlays to be applied
   */
  public void setOverlays(AbstractScatterPlotOverlay[] val) {
    AbstractScatterPlotOverlay[]	overlays;
    int					i;
    
    overlays = new AbstractScatterPlotOverlay[val.length];
    for (i = 0; i < val.length; i++)
      overlays[i] = val[i].shallowCopy();
    m_PanelOverlay.setCurrent(overlays);
  }

  /**
   * Set the size of each of the scatter plots
   * @param val		Size in pixels
   */
  public void setPlotSize(int val) {
    m_PlotSize = val;
  }

  public void paint(Graphics g) {
    super.paint(g);
  }

  /**
   * Set the value to use for the percent subsample
   * @param val		Percent of original sample
   */
  public void setPercent(int val) {
    m_Percent = val;
  }

  /**
   * Swing worker for displaying a progress bar while updating overlays etc
   * Takes along time to calculate new overlays, particularly lowess
   * @author msf8
   *
   */
  class progressWorker extends SwingWorker<Void, Void> {

    protected Void doInBackground() throws Exception {
      for(int j = 0; j< m_ScatterPlots.size(); j++) {
	//if deleting all the overlays
	if(m_OldHash.size() == 0) {
	  m_ScatterPlots.get(j).removeAllOverlays();
	}
	//remove only the old overlays that are not wanted any more
	else {
	  m_ScatterPlots.get(j).removeOverlays(m_DeleteOverlay);
	}
	//Iterate through the new overlays to be added
	Iterator<String> it = m_NewOverlay.iterator();
	while(it.hasNext()) {
	  AbstractScatterPlotOverlay temp;
	  String str = it.next();
	  try {
	    temp = (AbstractScatterPlotOverlay)OptionUtils.forCommandLine(AbstractScatterPlotOverlay.class, str);
	  }
	  catch(Exception e) {
	    //just add the diagonal overlay if the forcommandline doesn't work
	    temp = new Diagonal();
	  }
	  temp.inst(m_Data);
	  temp.setParent(m_ScatterPlots.get(j));
	  temp.setUp();
	  m_ScatterPlots.get(j).addOverlay(temp);
	}
	//Setprogress so progress bar can use this info
	setProgress((int)((100 *j)/m_ScatterPlots.size()));
      }
      //repaint each of the scatterplots
      for(int i = 0; i< m_ScatterPlots.size(); i++) {
	m_ScatterPlots.get(i).repaint();
	m_ScatterPlots.get(i).revalidate();
      }
      return null;
    }

    public void done() {
      //remove the progress bar
      m_OptionPanel.setEnabled(true);
      Matrix.this.remove(m_Progress);
      //Fill the oldHash hash set here so each time the overlays
      //are changed, they are compared with this to see which
      //need to be plotted next time the update button is pressed

      if(m_IsCancel) {
	//don't change the oldhash as updating wasn't completed
	System.out.println("cancelled");
      }
      else {
	m_OldHash = new HashSet<String>();
	int len = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent()).length;
	for(int i = 0; i< len; i++) {
	  m_OldHash.add(((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent())[i].toCommandLine());
	}
	m_NeedUpdate = false;
	m_IsCancel = false;
      }
      //no new overlays to add
      m_NewOverlay = new HashSet<String>();
    }
  }
}