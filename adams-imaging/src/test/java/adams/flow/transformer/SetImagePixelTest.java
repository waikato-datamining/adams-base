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
 * SetImagePixelTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for SetImagePixel actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetImagePixelTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetImagePixelTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("adams_logo.png");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("adams_logo.png");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SetImagePixelTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[7];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp3 = new adams.flow.core.AbstractActor[1];
      adams.flow.sink.DisplayPanelManager tmp4 = new adams.flow.sink.DisplayPanelManager();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("panelProvider");
      adams.flow.sink.ImageViewer tmp6 = new adams.flow.sink.ImageViewer();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter tmp8 = new adams.gui.print.NullWriter();
      tmp6.setWriter(tmp8);

      tmp4.setPanelProvider(tmp6);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.FileSupplier tmp9 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("files");
      tmp9.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/adams_logo.png")});

      tmp1[1] = tmp9;
      adams.flow.transformer.ImageMagickReader tmp11 = new adams.flow.transformer.ImageMagickReader();
      tmp1[2] = tmp11;
      adams.flow.control.Tee tmp12 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("name");
      tmp12.setName((java.lang.String) argOption.valueOf("display original image"));

      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp14 = new adams.flow.core.AbstractActor[2];
      adams.flow.transformer.Copy tmp15 = new adams.flow.transformer.Copy();
      tmp14[0] = tmp15;
      adams.flow.sink.CallableSink tmp16 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("callableName");
      tmp16.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DisplayPanelManager"));

      tmp14[1] = tmp16;
      tmp12.setActors(tmp14);

      tmp1[3] = tmp12;
      adams.flow.transformer.SetStorageValue tmp18 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("storageName");
      tmp18.setStorageName((adams.flow.control.StorageName) argOption.valueOf("image"));

      tmp1[4] = tmp18;
      adams.flow.control.Trigger tmp20 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("name");
      tmp20.setName((java.lang.String) argOption.valueOf("loop x coord"));

      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp22 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.ForLoop tmp23 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("loopLower");
      tmp23.setLoopLower((Integer) argOption.valueOf("20"));

      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("loopUpper");
      tmp23.setLoopUpper((Integer) argOption.valueOf("100"));

      tmp22[0] = tmp23;
      adams.flow.transformer.SetVariable tmp26 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("variableName");
      tmp26.setVariableName((adams.core.VariableName) argOption.valueOf("X"));

      tmp22[1] = tmp26;
      adams.flow.control.Trigger tmp28 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("name");
      tmp28.setName((java.lang.String) argOption.valueOf("loop y coord"));

      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp30 = new adams.flow.core.AbstractActor[7];
      adams.flow.source.ForLoop tmp31 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("loopLower");
      tmp31.setLoopLower((Integer) argOption.valueOf("20"));

      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("loopUpper");
      tmp31.setLoopUpper((Integer) argOption.valueOf("100"));

      tmp30[0] = tmp31;
      adams.flow.transformer.SetVariable tmp34 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("variableName");
      tmp34.setVariableName((adams.core.VariableName) argOption.valueOf("Y"));

      tmp30[1] = tmp34;
      adams.flow.transformer.MathExpression tmp36 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp36.getOptionManager().findByProperty("expression");
      tmp36.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X*@{X}"));

      tmp30[2] = tmp36;
      adams.flow.transformer.Convert tmp38 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp38.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToInt tmp40 = new adams.data.conversion.DoubleToInt();
      tmp38.setConversion(tmp40);

      tmp30[3] = tmp38;
      adams.flow.transformer.SetVariable tmp41 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp41.getOptionManager().findByProperty("name");
      tmp41.setName((java.lang.String) argOption.valueOf("SetVariable-1"));

      argOption = (AbstractArgumentOption) tmp41.getOptionManager().findByProperty("variableName");
      tmp41.setVariableName((adams.core.VariableName) argOption.valueOf("seed"));

      tmp30[4] = tmp41;
      adams.flow.control.Trigger tmp44 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp44.getOptionManager().findByProperty("name");
      tmp44.setName((java.lang.String) argOption.valueOf("calc RGBA value"));

      argOption = (AbstractArgumentOption) tmp44.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp46 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.RandomNumberGenerator tmp47 = new adams.flow.source.RandomNumberGenerator();
      argOption = (AbstractArgumentOption) tmp47.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt tmp49 = new adams.data.random.JavaRandomInt();
      argOption = (AbstractArgumentOption) tmp49.getOptionManager().findByProperty("seed");
      argOption.setVariable("@{seed}");

      argOption = (AbstractArgumentOption) tmp49.getOptionManager().findByProperty("maxValue");
      tmp49.setMaxValue((Integer) argOption.valueOf("1000000"));

      tmp47.setGenerator(tmp49);

      argOption = (AbstractArgumentOption) tmp47.getOptionManager().findByProperty("maxNum");
      tmp47.setMaxNum((Integer) argOption.valueOf("1"));

      tmp46[0] = tmp47;
      adams.flow.transformer.Convert tmp52 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp52.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToInt tmp54 = new adams.data.conversion.DoubleToInt();
      tmp52.setConversion(tmp54);

      tmp46[1] = tmp52;
      adams.flow.transformer.SetVariable tmp55 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp55.getOptionManager().findByProperty("variableName");
      tmp55.setVariableName((adams.core.VariableName) argOption.valueOf("RGBA"));

      tmp46[2] = tmp55;
      tmp44.setActors(tmp46);

      tmp30[5] = tmp44;
      adams.flow.control.Trigger tmp57 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp57.getOptionManager().findByProperty("name");
      tmp57.setName((java.lang.String) argOption.valueOf("update image"));

      argOption = (AbstractArgumentOption) tmp57.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp59 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.StorageValue tmp60 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp60.getOptionManager().findByProperty("storageName");
      tmp60.setStorageName((adams.flow.control.StorageName) argOption.valueOf("image"));

      tmp59[0] = tmp60;
      adams.flow.transformer.SetImagePixel tmp62 = new adams.flow.transformer.SetImagePixel();
      argOption = (AbstractArgumentOption) tmp62.getOptionManager().findByProperty("X");
      argOption.setVariable("@{X}");

      argOption = (AbstractArgumentOption) tmp62.getOptionManager().findByProperty("Y");
      argOption.setVariable("@{Y}");

      argOption = (AbstractArgumentOption) tmp62.getOptionManager().findByProperty("RGBA");
      argOption.setVariable("@{RGBA}");

      tmp59[1] = tmp62;
      tmp57.setActors(tmp59);

      tmp30[6] = tmp57;
      tmp28.setActors(tmp30);

      tmp22[2] = tmp28;
      tmp20.setActors(tmp22);

      tmp1[5] = tmp20;
      adams.flow.control.Trigger tmp63 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp63.getOptionManager().findByProperty("name");
      tmp63.setName((java.lang.String) argOption.valueOf("display modified image"));

      argOption = (AbstractArgumentOption) tmp63.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp65 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.StorageValue tmp66 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp66.getOptionManager().findByProperty("storageName");
      tmp66.setStorageName((adams.flow.control.StorageName) argOption.valueOf("image"));

      tmp65[0] = tmp66;
      adams.flow.sink.CallableSink tmp68 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp68.getOptionManager().findByProperty("callableName");
      tmp68.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DisplayPanelManager"));

      tmp65[1] = tmp68;
      tmp63.setActors(tmp65);

      tmp1[6] = tmp63;
      flow.setActors(tmp1);

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

