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
 * MOAStream.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import moa.options.ClassOption;
import moa.streams.InstanceStream;
import moa.streams.generators.AgrawalGenerator;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.MOAUtils;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Generates artificial data using a MOA stream generator.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MOAStream
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stream &lt;moa.options.ClassOption&gt; (property: streamGenerator)
 * &nbsp;&nbsp;&nbsp;The stream generator to use for generating the weka.core.Instance objects.
 * &nbsp;&nbsp;&nbsp;default: moa.streams.generators.AgrawalGenerator
 * </pre>
 * 
 * <pre>-num-examples &lt;int&gt; (property: numExamples)
 * &nbsp;&nbsp;&nbsp;The number of examples to generate; -1 means infinite.
 * &nbsp;&nbsp;&nbsp;default: 10000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-chunk-size &lt;int&gt; (property: chunkSize)
 * &nbsp;&nbsp;&nbsp;The size of the chunks to create: if 1 then Instance by Instance otherwise 
 * &nbsp;&nbsp;&nbsp;an Instances object.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAStream
  extends AbstractSource
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1862828539481494711L;

  /** the actual stream generator to use. */
  protected InstanceStream m_ActualStreamGenerator;

  /** the stream generator object. */
  protected ClassOption m_StreamGenerator;

  /** the maximum number of instance objects to generate. */
  protected int m_NumExamples;

  /** the number of examples generated so far. */
  protected int m_CountExamples;

  /** the chunk size, if 1 then Instance by Instance. */
  protected int m_ChunkSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates artificial data using a MOA stream generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "stream", "streamGenerator",
	    getDefaultOption());

    m_OptionManager.add(
	    "num-examples", "numExamples",
	    10000, -1, null);

    m_OptionManager.add(
	    "chunk-size", "chunkSize",
	    1, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualStreamGenerator = null;
    m_CountExamples         = 0;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "evaluator", getCurrentStreamGenerator().getClass());
    result += QuickInfoHelper.toString(this, "numExamples", ((m_NumExamples == -1) ? "infinite" : m_NumExamples), "/");
    result += QuickInfoHelper.toString(this, "chunkSize", ((m_ChunkSize == 1) ? "one-by-one" : "chunks of " + m_ChunkSize), ", ");

    return result;
  }

  /**
   * Returns the default stream generator.
   *
   * @return		the generator
   */
  protected InstanceStream getDefaultGenerator() {
    return new AgrawalGenerator();
  }

  /**
   * Returns the default class option.
   *
   * @return		the option
   */
  protected ClassOption getDefaultOption() {
    return new ClassOption(
	"stream",
	's',
	"The MOA stream generator to use from within ADAMS.",
	InstanceStream.class,
	getDefaultGenerator().getClass().getName().replace("moa.streams.", ""),
	getDefaultGenerator().getClass().getName());
  }

  /**
   * Sets the stream generator to use.
   *
   * @param value	the stream generator
   */
  public void setStreamGenerator(ClassOption value) {
    m_StreamGenerator = (ClassOption) value.copy();
    reset();
  }

  /**
   * Returns the data generator in use.
   *
   * @return		the data generator
   */
  public ClassOption getStreamGenerator() {
    return m_StreamGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String streamGeneratorTipText() {
    return "The stream generator to use for generating the weka.core.Instance objects.";
  }

  /**
   * Returns the current stream generator, based on the class option.
   *
   * @return		the stream generator
   * @see		#getStreamGenerator()
   */
  protected InstanceStream getCurrentStreamGenerator() {
    return (moa.streams.InstanceStream) MOAUtils.fromOption(m_StreamGenerator);
  }

  /**
   * Sets the maximum number of examples to generate. Less or equal to 0
   * means infinite.
   *
   * @param value	the maximum number
   */
  public void setNumExamples(int value) {
    if ((value == -1) || (value > 0)) {
      m_NumExamples = value;
      reset();
    }
    else {
      getLogger().warning("Number of examples must be -1 for infinite or >0, provided: " + value);
    }
  }

  /**
   * Returns the number of examples to generate. Less or equal to 0 means
   * infinite.
   *
   * @return		the maximum number
   */
  public int getNumExamples() {
    return m_NumExamples;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numExamplesTipText() {
    return "The number of examples to generate; -1 means infinite.";
  }

  /**
   * Sets the chunk size of data to generate; if 1 then Instance by Instance
   * otherwise an Instances object.
   *
   * @param value	the chunk size
   */
  public void setChunkSize(int value) {
    if (value >= 1) {
      m_ChunkSize = value;
      reset();
    }
    else {
      getLogger().warning("Chunk size must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the chunk size of data to generate; if 1 then Instance by Instance
   * otherwise an Instances object.
   *
   * @return		the chunk size
   */
  public int getChunkSize() {
    return m_ChunkSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String chunkSizeTipText() {
    return "The size of the chunks to create: if 1 then Instance by Instance otherwise an Instances object.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instance.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    if (m_ChunkSize == 1)
      return new Class[]{Instance.class};
    else
      return new Class[]{Instances.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instances	data;

    result = null;

    try {
      m_ActualStreamGenerator = getCurrentStreamGenerator();
      m_CountExamples         = 0;
      data                    = m_ActualStreamGenerator.getHeader();
      if (data == null)
	result = "No header obtained from stream generator!";
    }
    catch (Exception e) {
      result = handleException("Failed to generate stream: ", e);
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;
    Instance	inst;
    Instances	data;
    int		count;

    if (m_ChunkSize == 1) {
      result = new Token(m_ActualStreamGenerator.nextInstance());
      if (m_NumExamples > -1)
	m_CountExamples++;
    }
    else {
      data  = null;
      count = 0;
      while ((count < m_ChunkSize) && m_ActualStreamGenerator.hasMoreInstances()) {
	count++;
	inst = m_ActualStreamGenerator.nextInstance();
	if (data == null)
	  data = new Instances(inst.dataset(), m_ChunkSize);
	data.add(inst);
	if (m_NumExamples > -1) {
	  m_CountExamples++;
	  if (m_CountExamples == m_NumExamples)
	    break;
	}
      }
      data.compactify();
      result = new Token(data);
    }

    updateProvenance(result);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    boolean	maxReached;

    maxReached = ((m_NumExamples > -1) && (m_CountExamples >= m_NumExamples));

    return (m_ActualStreamGenerator != null) && !maxReached && m_ActualStreamGenerator.hasMoreInstances();
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, ((Token) cont).getPayload().getClass()));
  }
}
