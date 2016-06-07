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
 * PDFRenderPagesTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DisplayPanelManager;
import adams.flow.sink.ImageViewer;
import adams.flow.source.FileSupplier;
import adams.gui.print.NullWriter;
import adams.gui.visualization.image.NullOverlay;
import adams.gui.visualization.image.selection.NullProcessor;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for PDFRenderPages actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class PDFRenderPagesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PDFRenderPagesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("multiple_images.pdf");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("multiple_images.pdf");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PDFRenderPagesTest.class);
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
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/multiple_images.pdf"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.PDFRenderPages
      PDFRenderPages pdfrenderpages = new PDFRenderPages();
      actors.add(pdfrenderpages);

      // Flow.DisplayPanelManager
      DisplayPanelManager displaypanelmanager = new DisplayPanelManager();
      argOption = (AbstractArgumentOption) displaypanelmanager.getOptionManager().findByProperty("width");
      displaypanelmanager.setWidth((Integer) argOption.valueOf("1200"));
      argOption = (AbstractArgumentOption) displaypanelmanager.getOptionManager().findByProperty("height");
      displaypanelmanager.setHeight((Integer) argOption.valueOf("600"));

      // 
      ImageViewer imageviewer = new ImageViewer();
      NullWriter nullwriter = new NullWriter();
      imageviewer.setWriter(nullwriter);

      imageviewer.setShowProperties(true);

      argOption = (AbstractArgumentOption) imageviewer.getOptionManager().findByProperty("propertiesWidth");
      imageviewer.setPropertiesWidth((Integer) argOption.valueOf("100"));
      NullProcessor nullprocessor = new NullProcessor();
      imageviewer.setSelectionProcessor(nullprocessor);

      NullOverlay nulloverlay = new NullOverlay();
      imageviewer.setImageOverlay(nulloverlay);

      displaypanelmanager.setPanelProvider(imageviewer);

      actors.add(displaypanelmanager);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

