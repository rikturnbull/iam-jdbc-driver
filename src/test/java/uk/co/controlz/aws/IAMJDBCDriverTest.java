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

import java.util.Map;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IAMJDBCDriverTest extends TestCase {
  public IAMJDBCDriverTest(String testName)  {
      super(testName);
  }

  public static Test suite() {
    return new TestSuite(IAMJDBCDriverTest.class);
  }

  public void testConnect() {
    try {
      Driver driver = (Driver) Class.forName("uk.co.controlz.aws.IAMJDBCDriver").newInstance();
      Map<String, String> env = System.getenv();
      if(env.containsKey("IAMJDBCDRIVER_REGION") && env.containsKey("IAMJDBCDRIVER_USER") && env.containsKey("IAMJDBCDRIVER_URL")) {
        Properties info = new Properties();
        info.setProperty("awsRegion", env.get("IAMJDBCDRIVER_REGION"));
        info.setProperty("requireSSL", "true");
        info.setProperty("user", env.get("IAMJDBCDRIVER_USER"));
        info.setProperty("useSSL", "true");
        info.setProperty("verifyServerCertificate", "false");
        Connection connection = driver.connect(env.get("IAMJDBCDRIVER_URL"), info);
        System.out.println("CONNECTED!");
        connection.close();
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    assertTrue( true );
  }
}
