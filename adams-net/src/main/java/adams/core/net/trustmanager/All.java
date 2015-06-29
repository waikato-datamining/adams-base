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
 * All.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.net.trustmanager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Trusts all certificates.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class All
  extends AbstractTrustManager {

  private static final long serialVersionUID = -4917206278778908512L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, trusts all certificates.";
  }

  /**
   * Returns the issuers.
   *
   * @return		always null
   */
  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return null;
  }

  /**
   * Checks the certificates. Does nothing.
   *
   * @param x509Certificates	the certificates
   * @param s
   * @throws CertificateException	if certification fails
   */
  @Override
  public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    // does nothing
  }

  /**
   * Checks the certificates. Does nothing.
   *
   * @param x509Certificates	the certificates
   * @param s
   * @throws CertificateException	if certification fails
   */
  @Override
  public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    // does nothing
  }
}
