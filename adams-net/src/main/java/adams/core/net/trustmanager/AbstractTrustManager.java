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
 * AbstractTrustManager.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.net.trustmanager;

import adams.core.option.AbstractOptionHandler;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Ancestor for SSL trust managers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrustManager
  extends AbstractOptionHandler
  implements X509TrustManager {

  /**
   * Returns the issuers.
   *
   * @return		the issuers
   */
  public abstract X509Certificate[] getAcceptedIssuers();

  /**
   * Checks the certificates.
   *
   * @param x509Certificates	the certificates
   * @param s
   * @throws CertificateException	if certification fails
   */
  @Override
  public abstract void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException;

  /**
   * Checks the certificates.
   *
   * @param x509Certificates	the certificates
   * @param s
   * @throws CertificateException	if certification fails
   */
  @Override
  public abstract void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException;
}
