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
 * StorageJFreeChartAddSeriesTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.UnknownToUnknown;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.displaytype.Default;
import adams.flow.execution.NullListener;
import adams.flow.sink.JFreeChartPlot;
import adams.flow.source.FileSupplier;
import adams.flow.source.StorageValue;
import adams.gui.print.NullWriter;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.jfreechart.chart.ScatterPlot;
import adams.gui.visualization.jfreechart.dataset.DefaultXY;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for StorageJFreeChartAddSeries actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class StorageJFreeChartAddSeriesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StorageJFreeChartAddSeriesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("bolts.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StorageJFreeChartAddSeriesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.SpreadSheetFileReader
      SpreadSheetFileReader spreadsheetfilereader = new SpreadSheetFileReader();
      CsvSpreadSheetReader csvspreadsheetreader = new CsvSpreadSheetReader();
      DenseDataRow densedatarow = new DenseDataRow();
      csvspreadsheetreader.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      csvspreadsheetreader.setSpreadSheetType(defaultspreadsheet);

      spreadsheetfilereader.setReader(csvspreadsheetreader);

      actors.add(spreadsheetfilereader);

      // Flow.1st series
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("1st series"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.1st series.MakeJFreeChartDataset
      MakeJFreeChartDataset makejfreechartdataset = new MakeJFreeChartDataset();
      DefaultXY defaultxy = new DefaultXY();
      argOption = (AbstractArgumentOption) defaultxy.getOptionManager().findByProperty("Y");
      defaultxy.setY((SpreadSheetColumnRange) argOption.valueOf("8"));
      makejfreechartdataset.setDataset(defaultxy);

      actors2.add(makejfreechartdataset);

      // Flow.1st series.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("dataset"));
      actors2.add(setstoragevalue);
      tee.setActors(actors2.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.2nd series
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("2nd series"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.2nd series.StorageJFreeChartAddSeries
      StorageJFreeChartAddSeries storagejfreechartaddseries = new StorageJFreeChartAddSeries();
      argOption = (AbstractArgumentOption) storagejfreechartaddseries.getOptionManager().findByProperty("storageName");
      storagejfreechartaddseries.setStorageName((StorageName) argOption.valueOf("dataset"));
      DefaultXY defaultxy2 = new DefaultXY();
      argOption = (AbstractArgumentOption) defaultxy2.getOptionManager().findByProperty("Y");
      defaultxy2.setY((SpreadSheetColumnRange) argOption.valueOf("3"));
      storagejfreechartaddseries.setDataset(defaultxy2);

      actors3.add(storagejfreechartaddseries);
      tee2.setActors(actors3.toArray(new Actor[0]));

      actors.add(tee2);

      // Flow.plot
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("plot"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.plot.StorageValue
      StorageValue storagevalue = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue.getOptionManager().findByProperty("storageName");
      storagevalue.setStorageName((StorageName) argOption.valueOf("dataset"));
      UnknownToUnknown unknowntounknown = new UnknownToUnknown();
      storagevalue.setConversion(unknowntounknown);

      actors4.add(storagevalue);

      // Flow.plot.JFreeChartPlot
      JFreeChartPlot jfreechartplot = new JFreeChartPlot();
      Default default_ = new Default();
      jfreechartplot.setDisplayType(default_);

      NullWriter nullwriter = new NullWriter();
      jfreechartplot.setWriter(nullwriter);

      DefaultXY defaultxy3 = new DefaultXY();
      jfreechartplot.setDataset(defaultxy3);

      ScatterPlot scatterplot = new ScatterPlot();
      jfreechartplot.setChart(scatterplot);

      adams.gui.visualization.jfreechart.shape.Default default_2 = new adams.gui.visualization.jfreechart.shape.Default();
      jfreechartplot.setShape(default_2);

      DefaultColorProvider defaultcolorprovider = new DefaultColorProvider();
      jfreechartplot.setColorProvider(defaultcolorprovider);

      actors4.add(jfreechartplot);
      trigger.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

