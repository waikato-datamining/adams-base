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
 * ContainerDisplayTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for ContainerDisplay actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ContainerDisplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ContainerDisplayTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ContainerDisplayTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.StringConstants tmp2 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp3 = new adams.core.base.BaseString[1];
      tmp3[0] = (adams.core.base.BaseString) argOption.valueOf("1.1");
      tmp2.setStrings(tmp3);

      tmp1[0] = tmp2;
      adams.flow.transformer.Convert tmp4 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToDouble tmp6 = new adams.data.conversion.StringToDouble();
      tmp4.setConversion(tmp6);

      tmp1[1] = tmp4;
      adams.flow.transformer.MakePlotContainer tmp7 = new adams.flow.transformer.MakePlotContainer();
      tmp1[2] = tmp7;
      adams.flow.sink.ContainerDisplay tmp8 = new adams.flow.sink.ContainerDisplay();
      tmp1[3] = tmp8;
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

