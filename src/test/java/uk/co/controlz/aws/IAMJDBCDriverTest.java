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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;

import java.util.Map;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for IAM JDBC Driver wrapper for MySQL.
 *
 * @author Rik Turnbull
 *
 */
public class IAMJDBCDriverTest extends TestCase {
  private final static String IAMJDBCDRIVER_JKS_URL = "file:src/test/resources/rds-ca-2015-root.jks";
  private final static String IAMJDBCDRIVER_JKS_PASSWORD = "changeme";
  private final static String IAMJDBCDRIVER_REGION = "IAMJDBCDRIVER_REGION";
  private final static String IAMJDBCDRIVER_URL = "IAMJDBCDRIVER_URL";
  private final static String IAMJDBCDRIVER_USER = "IAMJDBCDRIVER_USER";



  /**
   * Fetches all the tests.
   *
   * @returns test suite
   */
  public static Test suite() {
    return new TestSuite(IAMJDBCDriverTest.class);
  }

  /**
   * Tests the connect method.
   */
  public void testConnect() {
    Map<String, String> env = getEnv();

    try {
      if(env != null) {
        Connection connection = DriverManager.getConnection(env.get(IAMJDBCDRIVER_URL), getProperties(env));
        assertNotNull(connection);
        connection.close();
      }
    } catch(Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * Tests the acceptUrl method.
   */
  public void testAcceptsUrl() {
    try {
      Driver driver = new IAMJDBCDriver();
      assertTrue("jdbc:mysqliam", driver.acceptsURL("jdbc:mysqliam://cluster.eu-west-1.rds.amazonaws.com:3306/mydb"));
      assertFalse("jdbc:mysql", driver.acceptsURL("jdbc:mysql://cluster.eu-west-1.rds.amazonaws.com:3306/mydb"));
    } catch(Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * Tests the getPropertyInfo method.
   */
  public void testGetPropertyInfo() {
    Map<String, String> env = getEnv();

    try {
      if(env != null) {
        Driver driver = new IAMJDBCDriver();
        boolean found = false;
        for(DriverPropertyInfo info : driver.getPropertyInfo(env.get(IAMJDBCDRIVER_URL), getProperties(env))) {
          if("awsRegion".equals(info.name)) {
            found = true;
          }
        }
        assertEquals("awsRegion", true, found);
      }
    } catch(Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * Fetches the environment, if any JDBC settings are configured.
   *
   * @returns the system environment or null if there are no JDBC settings
   */
  private Map<String, String> getEnv() {
    final Map<String, String> env = System.getenv();
    if(env.containsKey(IAMJDBCDRIVER_REGION) && env.containsKey(IAMJDBCDRIVER_USER) && env.containsKey(IAMJDBCDRIVER_URL)) {
      return System.getenv();
    } else {
      return null;
    }
  }

  /**
   * Fetches the JDBC driver properties.
   *
   * @param env the system environment containing JDBC settings
   * @return driver properties
   */
  private Properties getProperties(Map<String, String> env) {
    Properties properties = null;

    properties = new Properties();
    properties.setProperty("awsRegion", env.get(IAMJDBCDRIVER_REGION));
    properties.setProperty("requireSSL", "true");
    properties.setProperty("user", env.get(IAMJDBCDRIVER_USER));
    properties.setProperty("useSSL", "true");
    properties.setProperty("trustCertificateKeyStoreUrl", IAMJDBCDRIVER_JKS_URL);
    properties.setProperty("trustCertificateKeyStorePassword", IAMJDBCDRIVER_JKS_PASSWORD);

    return properties;
  }
}
