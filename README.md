# iam-jdbc-driver
A JDBC Driver wrapped around the standard MySQL JDBC Driver that provides IAM authentication for connecting to AWS Aurora MySQL or AWS RDS for MySQL, as described in [IAM Database Authentication for MySQL and Amazon Aurora](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAMDBAuth.html).

## Properties

This JDBC driver supports all the MySQL JDBC Driver properties and an additional, required `awsRegion` driver property.

Note that for RDS, the MySQL SSL properties must be set:

|Property               |Description                      |Example  |
|-----------------------|---------------------------------|---------|
|awsRegion              |AWS region of target RDS instance|eu-west-1|
|requireSSL             |Demand that SSL is used          |true     |
|useSSL                 |Prefer that SSL is used          |true     |

Amazon use their own CA for signing RDS certificates. Therefore, you may choose to skip validation:

|Property               |Description                      |Example  |
|-----------------------|---------------------------------|---------|
|verifyServerCertificate|Validate the database certificate|false    |

or generate a JKS keystore:

```bash
wget https://s3.amazonaws.com/rds-downloads/rds-ca-2015-root.pem
keytool -import -file rds-ca-2015-root.pem -alias rds-ca-2015-root -keystore rds-ca-2015-root.jks
```

then add your jks keystore properties:

|Property                        |Description         |Example                       |
|--------------------------------|--------------------|------------------------------|
|trustCertificateKeyStoreUrl     |Trust store URL     |file:path/rds-ca-2015-root.jks|
|trustCertificateKeyStorePassword|Trust store password|changeme                      |

## Building

If you build the driver (recommended), then run maven with:

```mvn package -Passembly```

This way you will have a single JAR file containing all the dependencies, including the MySQL driver and AWS SDK:

```target/iam-jdbc-driver-1.1.1-SNAPSHOT-jar-with-dependencies.jar```

If you download the ZIP file (`iam-jdbc-driver-1.1.0.zip`) from the release page, you must unpack it first and you must source and add
the dependencies to your classpath. These are:

```
+- com.amazonaws:aws-java-sdk-core:jar:1.11.310
  +- commons-logging:commons-logging:jar:1.1.3
  +- org.apache.httpcomponents:httpclient:jar:4.5.5
  |  +- org.apache.httpcomponents:httpcore:jar:4.4.9
  |  \- commons-codec:commons-codec:jar:1.10
  +- software.amazon.ion:ion-java:jar:1.0.2
  +- com.fasterxml.jackson.core:jackson-databind:jar:2.6.7.1
  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.6.0
  |  \- com.fasterxml.jackson.core:jackson-core:jar:2.6.7
  +- com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:jar:2.6.7
  \- joda-time:joda-time:jar:2.8.1
- com.amazonaws:aws-java-sdk-rds:jar:1.11.310
  \- com.amazonaws:jmespath-java:jar:1.11.310
- mysql:mysql-connector-java:jar:5.1.46
```

## Driver URL

Use `jdbc:mysqliam:` in place of `jdbc:mysql:` in the JDBC URL.

For example: `jdbc:mysqliam://host.cluster.region.rds.amazonaws.com:3306/database`

## Example

```java
Properties properties = new Properties();
properties.setProperty("awsRegion", "eu-west-1");
properties.setProperty("requireSSL", "true");
properties.setProperty("user", "mydbuser");
properties.setProperty("useSSL", "true");
properties.setProperty("verifyServerCertificate", "false");
Connection connection = DriverManager.getConnection(JDBCDRIVER_URL, properties);
```
