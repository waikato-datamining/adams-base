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
 * SocketFacade.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.core.option.OptionUtils;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.meta.socketfacade.AbstractDataPreparation;
import weka.classifiers.meta.socketfacade.Simple;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Uses sockets to communicate with a process for training and
 * making predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SocketFacade
  extends AbstractClassifier {

  private static final long serialVersionUID = -7557824847573090857L;

  /** the address of the remote process. */
  protected BaseHostname m_Remote = getDefaultRemote();

  /** the return address for the remote process to use. */
  protected BaseHostname m_Local = getDefaultLocal();

  /** the timeout for the socket. */
  protected int m_Timeout = getDefaultTimeout();

  /** the data preparation to use. */
  protected AbstractDataPreparation m_Preparation = getDefaultPreparation();

  /** whether to skip training. */
  protected boolean m_SkipTrain;

  /** the server socket for receiving the replies. */
  protected transient ServerSocket m_Server;

  /**
   * Returns a string describing this classifier.
   *
   * @return 		a description of the classifier suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Uses sockets to communicate with a process for training and "
	+ "making predictions.\n"
      + "NB: This classifier cannot be evaluated in parallel, as the "
      + "local port, which receives the results, can only be bound once.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    result.addElement(new Option(
      "\tThe address of the remote host.\n"
	+ "\t(default: " + getDefaultRemote() + ")",
      "remote", 1, "-remote <host:port>"));

    result.addElement(new Option(
      "\tThe return address for the remote host to use.\n"
	+ "\t(default: " + getDefaultLocal() + ")",
      "local", 1, "-local <host:port>"));

    result.addElement(new Option(
      "\tThe timeout for sockets in milli-second.\n"
	+ "\t(default: " + getDefaultTimeout() + ")",
      "timeout", 1, "-timeout <int>"));

    result.addElement(new Option(
      "\tThe scheme for preparing and parsing the data.\n"
	+ "\t(default: " + Utils.classToString(getDefaultPreparation()) + ")",
      "preparation", 1, "-preparation <classname + options>"));

    result.addElement(new Option(
      "\tWhether to skip the training process (eg pre-built model).\n"
	+ "\t(default: train not skipped)",
      "skip-train", 0, "-skip-train"));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	value;

    value = weka.core.Utils.getOption("remote", options);
    if (!value.isEmpty())
      setRemote(new BaseHostname(value));
    else
      setRemote(getDefaultRemote());

    value = weka.core.Utils.getOption("local", options);
    if (!value.isEmpty())
      setLocal(new BaseHostname(value));
    else
      setLocal(getDefaultLocal());

    value = weka.core.Utils.getOption("timeout", options);
    if (!value.isEmpty())
      setTimeout(Integer.parseInt(value));
    else
      setTimeout(getDefaultTimeout());

    value = weka.core.Utils.getOption("preparation", options);
    if (!value.isEmpty())
      setPreparation((AbstractDataPreparation) OptionUtils.forCommandLine(AbstractDataPreparation.class, value));
    else
      setPreparation(getDefaultPreparation());

    setSkipTrain(weka.core.Utils.getFlag("skip-train", options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result;

    result = new ArrayList<>();

    result.add("-remote");
    result.add("" + getRemote());

    result.add("-local");
    result.add("" + getLocal());

    result.add("-timeout");
    result.add("" + getTimeout());

    result.add("-preparation");
    result.add(OptionUtils.getCommandLine(getPreparation()));

    if (getSkipTrain())
      result.add("-skip-train");

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the default address of the remote process.
   *
   * @return		the default
   */
  protected BaseHostname getDefaultRemote() {
    return new BaseHostname("127.0.0.1:8000");
  }

  /**
   * Sets address of the remote process.
   *
   * @param value 	the address
   */
  public void setRemote(BaseHostname value) {
    m_Remote = value;
  }

  /**
   * Returns the address of the remote process.
   *
   * @return 		the address
   */
  public BaseHostname getRemote() {
    return m_Remote;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String remoteTipText() {
    return "The address of the remote process.";
  }

  /**
   * Returns the default address of the return address.
   *
   * @return		the default
   */
  protected BaseHostname getDefaultLocal() {
    return new BaseHostname("127.0.0.1:8001");
  }

  /**
   * Sets the return address for the remote process to use.
   *
   * @param value 	the address
   */
  public void setLocal(BaseHostname value) {
    m_Local = value;
  }

  /**
   * Returns the return address for the remote process to use.
   *
   * @return 		the address
   */
  public BaseHostname getLocal() {
    return m_Local;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String localTipText() {
    return "The return address for the remote process to use.";
  }

  /**
   * The default timeout in milli-second.
   *
   * @return		the default
   */
  protected int getDefaultTimeout() {
    return 3000;
  }

  /**
   * Sets the timeout in milli-second to wait for new connections.
   *
   * @param value	the timeout in msec
   */
  public void setTimeout(int value) {
    m_Timeout = value;
  }

  /**
   * Returns the timeout in milli-second to wait for new connections.
   *
   * @return		the timeout in msec
   */
  public int getTimeout() {
    return m_Timeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeoutTipText() {
    return "The timeout in milli-second for waiting on responses from the process.";
  }

  /**
   * Returns the default data preparation scheme to use.
   *
   * @return		the default
   */
  protected AbstractDataPreparation getDefaultPreparation() {
    return new Simple();
  }

  /**
   * Sets the data preparation scheme to use.
   *
   * @param value 	the scheme
   */
  public void setPreparation(AbstractDataPreparation value) {
    m_Preparation = value;
  }

  /**
   * Returns the data preparation scheme to use.
   *
   * @return 		the scheme
   */
  public AbstractDataPreparation getPreparation() {
    return m_Preparation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String preparationTipText() {
    return "The data preparation scheme to use for sending/receiving the data.";
  }

  /**
   * Sets whether to skip training, eg when using a pre-built model.
   *
   * @param value 	true if to skip training
   */
  public void setSkipTrain(boolean value) {
    m_SkipTrain = value;
  }

  /**
   * Returns whether to skip training, eg when using a pre-built model.
   *
   * @return 		true if to skip training
   */
  public boolean getSkipTrain() {
    return m_SkipTrain;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String skipTrainTipText() {
    return "If enabled, the training is skipped; useful when using a pre-built model.";
  }

  /**
   * Initializes the server socket if necessary.
   *
   * @throws Exception		if initialization fails
   */
  protected synchronized void initServer() throws Exception {
    if (m_Server == null) {
      m_Server = new ServerSocket(m_Local.portValue());
      m_Server.setSoTimeout(m_Timeout);
    }
  }

  /**
   * Closes the server socket if necessary.
   */
  protected synchronized void closeServer() {
    if (m_Server != null) {
      try {
        m_Server.close();
        m_Server = null;
      }
      catch (Exception e) {
        // ignored
      }
    }
  }

  /**
   * Returns the server socket, instantiates it if necessary.
   *
   * @return		the socket
   * @throws Exception	if binding of socket fails
   */
  protected ServerSocket getServer() throws Exception {
    initServer();
    return m_Server;
  }

  /**
   * Receives the response data.
   *
   * @return		the received data
   * @throws Exception	if fails to receive data
   */
  protected byte[] receive() throws Exception {
    Socket 	client;
    TByteList 	result;
    InputStream	in;
    int		b;

    initServer();

    client = m_Server.accept();
    in     = client.getInputStream();
    result = new TByteArrayList();
    while ((b = in.read()) != -1)
      result.add((byte) b);
    client.close();

    closeServer();

    return result.toArray();
  }

  /**
   * Sends the data to the remote host.
   *
   * @param data	the data to send
   * @return		the response data
   * @throws Exception	if sending fails
   */
  protected byte[] send(byte[] data) throws Exception {
    Socket	socket;

    initServer();
    socket = new Socket(m_Remote.hostnameValue(), m_Remote.portValue());
    socket.setSoTimeout(m_Timeout);
    socket.getOutputStream().write(data);
    socket.getOutputStream().flush();
    socket.close();

    return receive();
  }

  /**
   * Returns the Capabilities of this classifier.
   *
   * @return the capabilities of this object
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enableAll();

    return result;
  }

  /**
   * Generates a classifier.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    String	response;

    if (m_SkipTrain)
      return;

    try {
      response = m_Preparation.parseTrain(send(m_Preparation.prepareTrain(data, this)));
      if (response != null)
	throw new Exception("Failed to perform remote build:\n" + response);
    }
    finally {
      closeServer();
    }
  }

  /**
   * Generates a classification for the instance.
   *
   * @param instance	the instance to classify
   * @return		the classification
   * @throws Exception	if classification failed
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    try {
      return m_Preparation.parseClassify(send(m_Preparation.prepareClassify(instance, this)));
    }
    finally {
      closeServer();
    }
  }

  /**
   * Generates a class distribution for the instance.
   *
   * @param instance	the instance to get the class distribution for
   * @return		the class distribution
   * @throws Exception	if class distribution fails
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    try {
      return m_Preparation.parseDistribution(send(m_Preparation.prepareDistribution(instance, this)), instance.numClasses());
    }
    finally {
      closeServer();
    }
  }

  /**
   * Just returns the commandline options.
   *
   * @return		the commandline options
   */
  @Override
  public String toString() {
    return OptionUtils.getCommandLine(this);
  }
}
