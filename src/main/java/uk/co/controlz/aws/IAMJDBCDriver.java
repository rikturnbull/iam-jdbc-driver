/**
  * MIT License
  *
  * Copyright (c) 2018 Rik Turnbull
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  */
package uk.co.controlz.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IAMJDBCDriver implements java.sql.Driver {

  private static final Logger LOGGER = Logger.getLogger(IAMJDBCDriver.class.getName());

  static {
    try {
      DriverManager.registerDriver(new IAMJDBCDriver());
    } catch(Exception e) {
      throw new RuntimeException("Can't register driver!", e);
    }
  }

  private Driver _mysqlDriver;

  public IAMJDBCDriver() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    _mysqlDriver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
  }

  public static String generateAuthToken(String region, String hostName, String port, String username) {
	    final RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
		    .credentials(new DefaultAWSCredentialsProviderChain())
		    .region(region)
		    .build();

	    return(generator.getAuthToken(
		    GetIamAuthTokenRequest.builder()
		    .hostname(hostName)
		    .port(Integer.parseInt(port))
		    .userName(username)
		    .build()));
  }

  public boolean acceptsURL(String url) throws SQLException {
    return _mysqlDriver.acceptsURL(url);
  }

  public Connection connect(String url, Properties info) throws SQLException {
    URI uri = URI.create(url.substring(5));

    String password = generateAuthToken(
      info.getProperty("awsRegion"),
      uri.getHost(),
      String.valueOf(uri.getPort()),
      getUsernameFromUrlOrProperties(uri, info)
    );

    info.setProperty("password", password);

    return _mysqlDriver.connect(url, info);
  }

  private String getUsernameFromUrlOrProperties(URI uri, Properties info) {
    String username = info.getProperty("user");

    if(username == null) {
      final String userInfo = uri.getUserInfo();
      if(userInfo != null) {
        username = userInfo.split(":")[0];
      }
    }

    return username;
  }

  public int getMajorVersion() {
    return _mysqlDriver.getMajorVersion();
  }

  public int getMinorVersion() {
    return _mysqlDriver.getMinorVersion();
  }

  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return _mysqlDriver.getParentLogger();
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    return _mysqlDriver.getPropertyInfo(url, info);
  }

  public boolean jdbcCompliant() {
    return _mysqlDriver.jdbcCompliant();
  }
}
