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
 * SpreadSheetPlotGeneratorTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.core.Actor;
import adams.flow.sink.SequencePlotter;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.plotgenerator.SimplePlotGenerator;
import adams.flow.transformer.plotgenerator.XYPlotGenerator;
import adams.gui.visualization.sequence.DotPaintlet;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.test.TmpFile;

/**
 * Tests the SpreadSheetReader actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetPlotGeneratorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetPlotGeneratorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("bolts.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.csv")});

    SpreadSheetFileReader ssr = new SpreadSheetFileReader();

    SpreadSheetPlotGenerator plot1 = new SpreadSheetPlotGenerator();
    SimplePlotGenerator simple = new SimplePlotGenerator();
    simple.setPlotColumns("first,last");
    plot1.setGenerator(simple);

    SequencePlotter sp1 = new SequencePlotter();
    sp1.setPaintlet(new LinePaintlet());

    Sequence seq1 = new Sequence();
    seq1.setActors(new Actor[]{
	plot1,
	sp1
    });

    SpreadSheetPlotGenerator plot2 = new SpreadSheetPlotGenerator();
    simple = new SimplePlotGenerator();
    simple.setPlotColumns("first,last");
    plot2.setGenerator(simple);

    SequencePlotter sp2 = new SequencePlotter();
    sp2.setPaintlet(new LinePaintlet());

    Sequence seq2 = new Sequence();
    seq2.setActors(new Actor[]{
	plot2,
	sp2
    });

    SpreadSheetPlotGenerator plot3 = new SpreadSheetPlotGenerator();
    XYPlotGenerator generator = new XYPlotGenerator();
    generator.setPlotColumns("last_1,last");
    generator.setXColumn("last");
    plot3.setGenerator(generator);

    SequencePlotter sp3 = new SequencePlotter();
    DotPaintlet dot = new DotPaintlet();
    dot.setStrokeThickness(3.0f);
    sp3.setPaintlet(dot);

    Sequence seq3 = new Sequence();
    seq3.setActors(new Actor[]{
	plot3,
	sp3
    });

    Branch br = new Branch();
    br.setNumThreads(0);
    br.setBranches(new Actor[]{
	seq1,
	seq2,
	seq3
    });

    Flow flow = new Flow();
    flow.setActors(new Actor[]{sfs, ssr, br});

    return flow;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetPlotGeneratorTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
