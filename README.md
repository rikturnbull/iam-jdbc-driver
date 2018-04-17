# iam-jdbc-driver
A JDBC Driver wrapped around the standard MySQL JDBC Driver that provides IAM authentication for connecting to AWS Aurora MySQL or AWS RDS for MySQL, as described in [IAM Database Authentication for MySQL and Amazon Aurora](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAMDBAuth.html).

## Properties

This JDBC driver supports all the MySQL JDBC Driver properties and an additional `awsRegion` driver property. The following driver properties are required:

|Property               |Description                      |Example  |
|-----------------------|---------------------------------|---------|
|awsRegion              |AWS region of target RDS instance|eu-west-1|
|requireSSL             |Demand that SSL is used          |true     |
|useSSL                 |Prefer that SSL is used          |true     |
|verifyServerCertificate|Validate the database certificate|false    |

## Example

```java
Driver driver = (Driver) Class.forName("uk.co.controlz.aws.IAMJDBCDriver").newInstance();
Properties properties = new Properties();
properties.setProperty("awsRegion", "eu-west-1";
properties.setProperty("requireSSL", "true");
properties.setProperty("user", "mydbuser");
properties.setProperty("useSSL", "true");
properties.setProperty("verifyServerCertificate", "false");
Connection connection = driver.connect(url, properties);
```
