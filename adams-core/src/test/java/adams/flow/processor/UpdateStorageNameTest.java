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
 * UpdateStorageNameTest.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for UpdateStorageName processor.
 *
 * @author fracpete
 * @version $Revision$
 */
public class UpdateStorageNameTest
  extends AbstractActorProcessorTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UpdateStorageNameTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(UpdateStorageNameTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("annotations");
      flow.setAnnotations((adams.core.base.BaseAnnotation) argOption.valueOf("This flow demonstrates how to process the same storage value\nmultiple times with Triggers, but still output and forward in the\nflow. The actual processing is happening below the \n\"StorageValueSequence\" control actor, which also forwards the\nstorage value then.\nThis flow only uses a simple string to show the functionality, but\nit could be any complex data structure that the flow manipulates."));

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp2 = new adams.flow.core.Actor[6];
      adams.flow.standalone.SetVariable tmp3 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("variableName");
      tmp3.setVariableName((adams.core.VariableName) argOption.valueOf("count"));

      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("variableValue");
      tmp3.setVariableValue((BaseText) argOption.valueOf("0"));

      tmp2[0] = tmp3;
      adams.flow.source.StringConstants tmp6 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp7 = new adams.core.base.BaseString[4];
      tmp7[0] = (adams.core.base.BaseString) argOption.valueOf("blah");
      tmp7[1] = (adams.core.base.BaseString) argOption.valueOf("bloerk");
      tmp7[2] = (adams.core.base.BaseString) argOption.valueOf("some");
      tmp7[3] = (adams.core.base.BaseString) argOption.valueOf("thing");
      tmp6.setStrings(tmp7);

      tmp2[1] = tmp6;
      adams.flow.transformer.IncVariable tmp8 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("variableName");
      tmp8.setVariableName((adams.core.VariableName) argOption.valueOf("count"));

      tmp2[2] = tmp8;
      adams.flow.transformer.SetStorageValue tmp10 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("storageName");
      tmp10.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp2[3] = tmp10;
      adams.flow.control.StorageValueSequence tmp12 = new adams.flow.control.StorageValueSequence();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp13 = new adams.flow.core.Actor[2];
      adams.flow.control.Trigger tmp14 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("name");
      tmp14.setName((java.lang.String) argOption.valueOf("first processing step"));

      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp16 = new adams.flow.core.Actor[3];
      adams.flow.source.StorageValue tmp17 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("storageName");
      tmp17.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp16[0] = tmp17;
      adams.flow.transformer.StringInsert tmp19 = new adams.flow.transformer.StringInsert();
      tmp19.setAfter(true);

      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("value");
      argOption.setVariable("@{count}");

      tmp16[1] = tmp19;
      adams.flow.transformer.SetStorageValue tmp20 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("storageName");
      tmp20.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp16[2] = tmp20;
      tmp14.setActors(tmp16);

      tmp13[0] = tmp14;
      adams.flow.control.Trigger tmp22 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("name");
      tmp22.setName((java.lang.String) argOption.valueOf("second processing step"));

      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp24 = new adams.flow.core.Actor[3];
      adams.flow.source.StorageValue tmp25 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("storageName");
      tmp25.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp24[0] = tmp25;
      adams.flow.transformer.StringInsert tmp27 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) tmp27.getOptionManager().findByProperty("position");
      tmp27.setPosition((adams.core.Index) argOption.valueOf("first"));

      argOption = (AbstractArgumentOption) tmp27.getOptionManager().findByProperty("value");
      argOption.setVariable("@{count}");

      tmp24[1] = tmp27;
      adams.flow.transformer.SetStorageValue tmp29 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp29.getOptionManager().findByProperty("storageName");
      tmp29.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp24[2] = tmp29;
      tmp22.setActors(tmp24);

      tmp13[1] = tmp22;
      tmp12.setActors(tmp13);

      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("storageName");
      tmp12.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp2[4] = tmp12;
      adams.flow.sink.Display tmp32 = new adams.flow.sink.Display();
      tmp2[5] = tmp32;
      flow.setActors(tmp2);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractActorProcessor[] getRegressionSetups() {
    UpdateStorageName[]	result;

    result    = new UpdateStorageName[1];
    result[0] = new UpdateStorageName();
    result[0].setOldName("content");
    result[0].setNewName("funky");

    return result;
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

