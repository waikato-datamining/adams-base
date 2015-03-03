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
 * FixDeprecatedCommandlineTransformersTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.CallableSink;
import adams.flow.sink.Display;
import adams.flow.source.StringConstants;
import adams.flow.standalone.CallableActors;
import adams.flow.standalone.DeleteFile;
import adams.flow.transformer.AnyToCommandline;
import adams.flow.transformer.CommandlineToAny;
import adams.test.TmpDirectory;

/**
 * Tests the FixDeprecatedCommandlineTransformers processor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixDeprecatedCommandlineTransformersTest
  extends AbstractActorProcessorTestCase {

  /**
   * Constructs the test.
   *
   * @param name 	the name of the test
   */
  public FixDeprecatedCommandlineTransformersTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    DeleteFile df = new DeleteFile();
    df.setDirectory(new TmpDirectory());
    df.setRegExp(new BaseRegExp("dumpfile.txt"));
    df.setSkip(true);

    Display d1 = new Display();
    d1.setName("dis");

    CallableActors ga = new CallableActors();
    ga.setActors(new AbstractActor[]{
	d1
    });

    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("1"),
	new BaseString("2"),
	new BaseString("3"),
	new BaseString("4")
    });

    CommandlineToAny con1 = new CommandlineToAny();
    con1.setSkip(true);

    CallableSink gs1 = new CallableSink();
    gs1.setCallableName(new CallableActorReference("dis"));

    Sequence seq1 = new Sequence();
    seq1.setActors(new AbstractActor[]{
	con1,
	gs1
    });

    AnyToCommandline con2 = new AnyToCommandline();

    CallableSink gs2 = new CallableSink();
    gs2.setCallableName(new CallableActorReference("dis"));

    Sequence seq2 = new Sequence();
    seq2.setActors(new AbstractActor[]{
	con2,
	gs2
    });

    Branch br = new Branch();
    br.setBranches(new AbstractActor[]{
	seq1,
	seq2
    });

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{
	df,
	ga,
	sc,
	br
    });

    return flow;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractActorProcessor[] getRegressionSetups() {
    return new AbstractActorProcessor[]{
	new FixDeprecatedCommandlineTransformers()
    };
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(FixDeprecatedCommandlineTransformersTest.class);
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
