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
 * OpenStreetMapViewerTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for OpenStreetMapViewer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class OpenStreetMapViewerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public OpenStreetMapViewerTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(OpenStreetMapViewerTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[2];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      actors1[0] = start2;

      // Flow.OpenStreetMapViewer
      adams.flow.sink.OpenStreetMapViewer openstreetmapviewer3 = new adams.flow.sink.OpenStreetMapViewer();
      argOption = (AbstractArgumentOption) openstreetmapviewer3.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter5 = new adams.gui.print.NullWriter();
      openstreetmapviewer3.setWriter(nullwriter5);

      argOption = (AbstractArgumentOption) openstreetmapviewer3.getOptionManager().findByProperty("tileSource");
      adams.flow.sink.openstreetmapviewer.OpenStreetMapSource openstreetmap7 = new adams.flow.sink.openstreetmapviewer.OpenStreetMapSource();
      openstreetmapviewer3.setTileSource(openstreetmap7);

      argOption = (AbstractArgumentOption) openstreetmapviewer3.getOptionManager().findByProperty("tileLoader");
      adams.flow.sink.openstreetmapviewer.OpenStreetMapLoader openstreetmap9 = new adams.flow.sink.openstreetmapviewer.OpenStreetMapLoader();
      openstreetmapviewer3.setTileLoader(openstreetmap9);

      argOption = (AbstractArgumentOption) openstreetmapviewer3.getOptionManager().findByProperty("hitListener");
      adams.flow.sink.openstreetmapviewer.NullMapObjectHitListener nullmapobjecthitlistener11 = new adams.flow.sink.openstreetmapviewer.NullMapObjectHitListener();
      openstreetmapviewer3.setHitListener(nullmapobjecthitlistener11);

      argOption = (AbstractArgumentOption) openstreetmapviewer3.getOptionManager().findByProperty("mapObjectPruner");
      adams.flow.sink.openstreetmapviewer.NullPruner nullpruner13 = new adams.flow.sink.openstreetmapviewer.NullPruner();
      openstreetmapviewer3.setMapObjectPruner(nullpruner13);

      actors1[1] = openstreetmapviewer3;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener15 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener15);

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

