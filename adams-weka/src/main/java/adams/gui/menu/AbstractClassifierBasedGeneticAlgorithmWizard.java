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
 * AbstractClassifierBasedGeneticAlgorithmWizard.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.event.FitnessChangeEvent;
import adams.event.FitnessChangeListener;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.genetic.AbstractClassifierBasedGeneticAlgorithm;
import adams.genetic.OutputPrefixType;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.CirclePaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.wizard.AbstractWizardPage;
import adams.gui.wizard.FinalPage;
import adams.gui.wizard.PageCheck;
import adams.gui.wizard.PropertySheetPanelPage;
import adams.gui.wizard.WekaSelectMultipleDatasetsPage;
import adams.gui.wizard.WizardPane;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

/**
 * Ancestor for optimizing datasets (attribute selection) using a genetic algorithm.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierBasedGeneticAlgorithmWizard
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 7586443345167287461L;

  /**
   * For plotting the performance of the genetic algorithm.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class PerformancePlot
    extends SequencePlotterPanel
    implements FitnessChangeListener {

    private static final long serialVersionUID = -4899150268392572586L;

    /** the owner. */
    protected AbstractClassifierBasedGeneticAlgorithm m_Owner;

    /**
     * Initializes plot.
     *
     * @param title	the title
     * @param owner	the dark lord setup
     */
    public PerformancePlot(String title, AbstractClassifierBasedGeneticAlgorithm owner) {
      super(title);
      setOwner(owner);
      setTitle(owner.getMeasure().toString());
    }

    /**
     * Sets the owner for the plot.
     *
     * @param value	the owner
     */
    public void setOwner(AbstractClassifierBasedGeneticAlgorithm value) {
      if (m_Owner != null)
	m_Owner.removeFitnessChangeListener(this);
      m_Owner = value;
      m_Owner.addFitnessChangeListener(this);
    }

    /**
     * Returns the current owner.
     *
     * @return		the owner, null if none set
     */
    public AbstractClassifierBasedGeneticAlgorithm getOwner() {
      return m_Owner;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      AxisPanelOptions	axis;

      super.initGUI();

      getPlot().setAxisVisibility(Axis.BOTTOM, Visibility.VISIBLE);
      axis = getDefaultAxisX();
      axis.configure(getPlot(), Axis.BOTTOM);

      getPlot().setAxisVisibility(Axis.LEFT, Visibility.VISIBLE);
      axis = getDefaultAxisY();
      axis.configure(getPlot(), Axis.LEFT);

      setPaintlet(new CirclePaintlet());
    }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisX() {
    AxisPanelOptions	result;
    FancyTickGenerator tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("iteration");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("0"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(20);
    result.setTickGenerator(tick);

    return result;
  }

  /**
   * Returns the setup for the Y axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisY() {
    AxisPanelOptions	result;
    FancyTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("measure");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(60);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("0.0"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(10);
    result.setTickGenerator(tick);

    return result;
  }

    /**
     * Adds the fitness measure to the plot.
     *
     * @param e                the event
     */
    @Override
    public void fitnessChanged(FitnessChangeEvent e) {
      AbstractClassifierBasedGeneticAlgorithm	algorithm;
      String					plotName;
      SequencePlotPoint				point;
      XYSequenceContainerManager		manager;
      XYSequence				seq;
      XYSequenceContainer 			cont;

      algorithm = (AbstractClassifierBasedGeneticAlgorithm) e.getGeneticAlgorithm();
      plotName  = algorithm.getMeasure().toString();
      switch (algorithm.getOutputPrefixType()) {
	case NONE:
	  break;
	case RELATION:
	  plotName = ((adams.genetic.DarkLord) e.getGeneticAlgorithm()).getInstances().relationName() + "-" + plotName;
	  break;
	case SUPPLIED:
	  plotName = ((adams.genetic.DarkLord) e.getGeneticAlgorithm()).getSuppliedPrefix() + "-" + plotName;
	  break;
	default:
	  throw new IllegalStateException("Unhandled output prefix type: " + algorithm.getOutputPrefixType());
      }
      manager  = getContainerManager();
      manager.startUpdate();
      if (manager.indexOf(plotName) == -1) {
	seq  = new SequencePlotSequence();
	seq.setComparison(Comparison.X_AND_Y);
	seq.setID(plotName);
	cont = manager.newContainer(seq);
	manager.add(cont);
      }
      else {
	cont = manager.get(manager.indexOf(plotName));
	seq  = cont.getData();
      }
      point = new SequencePlotPoint(seq.size() + 1, e.getFitness());
      seq.add(point);
      manager.finishUpdate();
    }
  }

  /**
   * Initializes the menu item with no owner.
   */
  public AbstractClassifierBasedGeneticAlgorithmWizard() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AbstractClassifierBasedGeneticAlgorithmWizard(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "genetic.png";
  }

  /**
   * Returns the genetic algorithm setup to use.
   *
   * @return		the setup
   */
  protected abstract AbstractClassifierBasedGeneticAlgorithm getSetup();

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    AbstractClassifierBasedGeneticAlgorithm	setup;
    final WizardPane				wizard;
    WekaSelectMultipleDatasetsPage 		datasets;
    PropertySheetPanelPage			algorithm;
    FinalPage					finalpage;
    final ChildFrame				frame;

    // wizard
    wizard = new WizardPane();
    wizard.setCustomFinishText("Execute");

    // files
    datasets = new WekaSelectMultipleDatasetsPage("Input");
    datasets.setDescription("Select all the files that you want to process, one after the other.");
    datasets.setPageCheck(new PageCheck() {
      @Override
      public boolean checkPage(AbstractWizardPage page) {
	Properties props = page.getProperties();
	try {
	  String[] files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
	  return (files.length >= 2);
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to obtain files:", e);
	}
	return false;
      }
    });
    wizard.addPage(datasets);

    // setup
    setup = getSetup();
    setup.setLoggingLevel(LoggingLevel.FINE);
    setup.setOutputPrefixType(OutputPrefixType.SUPPLIED);
    setup.setNotificationInterval(10);

    algorithm = new PropertySheetPanelPage("Setup");
    algorithm.setButtonPanelVisible(true);
    algorithm.setTarget(setup);
    algorithm.setDescription(
      "Configure the genetic algorithm setup.\n"
	+ "Select the dataset that you want to have optimized and the classifier to use for optimizing.\n"
	+ "The file name (without path and extension) is automatically set as the 'supplied prefix'.");
    algorithm.setPageCheck(new PageCheck() {
      @Override
      public boolean checkPage(AbstractWizardPage page) {
	Properties props = page.getProperties();
	try {
	  String cmdline = props.getProperty(PropertySheetPanelPage.PROPERTY_CMDLINE);
	  OptionUtils.forAnyCommandLine(adams.genetic.DarkLord.class, cmdline);
	  return true;
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to obtain genetic algorithm setup:", e);
	  return false;
	}
      }
    });
    wizard.addPage(algorithm);

    finalpage = new FinalPage();
    finalpage.setLogo(null);
    finalpage.setDescription("<html><h2>Ready</h2>Please click on <b>Execute</b> to start the optimization.</html>");
    wizard.addPage(finalpage);

    frame = createChildFrame(wizard, 800, 600);
    wizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!e.getActionCommand().equals(WizardPane.ACTION_FINISH)) {
          frame.dispose();
          return;
        }
        Properties props = wizard.getProperties(false);
	AbstractClassifierBasedGeneticAlgorithm genetic = null;
        String[] files = null;
	try {
	  String cmdline = props.getProperty(PropertySheetPanelPage.PROPERTY_CMDLINE);
	  genetic = (AbstractClassifierBasedGeneticAlgorithm) OptionUtils.forAnyCommandLine(AbstractClassifierBasedGeneticAlgorithm.class, cmdline);
          files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
	}
	catch (Exception ex) {
          GUIHelper.showErrorMessage(
            null, "Failed to obtain genetic algorithm setup from wizard!\n" + Utils.throwableToString(ex));
	  return;
	}
        doOptimize(frame, genetic, files);
      }
    });
  }

  /**
   * Performs the optimization.
   *
   * @param frame       the frame to close
   * @param genetic	the genetic algorithm setup
   * @param files	the files to process
   */
  protected void doOptimize(ChildFrame frame, final AbstractClassifierBasedGeneticAlgorithm genetic, final String[] files) {
    JPanel			panelAll;
    JPanel			panelButtons;
    final PerformancePlot	plot;
    final JButton		buttonPause;
    final JButton		buttonResume;
    final JButton		buttonStop;
    SwingWorker			worker;

    frame.dispose();

    panelAll = new JPanel(new BorderLayout());

    plot = new PerformancePlot(getTitle(), genetic);
    panelAll.add(plot, BorderLayout.CENTER);

    buttonPause  = new JButton(GUIHelper.getIcon("pause.gif"));
    buttonResume = new JButton(GUIHelper.getIcon("resume.gif"));
    buttonStop   = new JButton(GUIHelper.getIcon("stop_blue.gif"));
    buttonPause.setEnabled(true);
    buttonResume.setEnabled(false);
    buttonStop.setEnabled(true);
    buttonPause.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	genetic.pauseExecution();
	buttonPause.setEnabled(!genetic.isPaused());
	buttonResume.setEnabled(genetic.isPaused());
      }
    });
    buttonResume.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	genetic.resumeExecution();
	buttonPause.setEnabled(!genetic.isPaused());
	buttonResume.setEnabled(genetic.isPaused());
      }
    });
    buttonStop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	genetic.stopExecution();
	buttonPause.setEnabled(false);
	buttonResume.setEnabled(false);
	buttonStop.setEnabled(false);
      }
    });
    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);
    panelButtons.add(buttonPause);
    panelButtons.add(buttonResume);
    panelButtons.add(buttonStop);

    createChildFrame(panelAll, 800, 300);
    worker = new SwingWorker() {
      protected AbstractClassifierBasedGeneticAlgorithm m_Actual;
      @Override
      protected Object doInBackground() throws Exception {
	for (String file: files) {
	  Instances data = DataSource.read(file);
	  m_Actual = (AbstractClassifierBasedGeneticAlgorithm) genetic.shallowCopy();
	  plot.setOwner(m_Actual);
	  m_Actual.setSuppliedPrefix(FileUtils.replaceExtension(new File(file).getName(), ""));
	  m_Actual.setInstances(data);
	  m_Actual.run();
	  if (m_Actual.isStopped())
	    break;
	}
	return null;
      }
      @Override
      protected void done() {
	buttonPause.setEnabled(false);
	buttonResume.setEnabled(false);
	buttonStop.setEnabled(false);
	if (m_Actual.isStopped())
	  GUIHelper.showErrorMessage(null, getTitle() + " stopped!");
	else
	  GUIHelper.showInformationMessage(null, getTitle() + " finished!");
	super.done();
      }
    };
    worker.execute();
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.EXPERT;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_MACHINELEARNING;
  }
}