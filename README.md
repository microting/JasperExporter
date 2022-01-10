# JasperExporter

[![Java CI with Gradle](https://github.com/akarabelnikov/JasperExporter/actions/workflows/gradle.yml/badge.svg)](https://github.com/akarabelnikov/JasperExporter/actions/workflows/gradle.yml)

#### Prerequisites

1. Java 11 (JRE) should be installed for running tool.
2. All required certificates for accessing resources (images) via HTTPS should be installed for Java. The installation procedure described below.

#### Building tool from source code

In the root folder with source code run command in console.

For *Windows*:
```bash
gradlew.bat clean build
 ```
For *Linux*:
```bash
./gradlew clean build
```

As the result the [fat][1] jar will be generated at ***./build/libs*** folder.   

#### Running utility

The example of command for running utility on Windows:
```bash
C:\tmp>c:\jdk-11.0.8.10-hotspot\bin\java -jar c:\JasperExporter\build\libs\JasperExporter.jar -template="c:\test\1316.jrxml" -type="pdf" -uri="c:/test/201805281421570431_5491.xml" -outputFile="c:\JasperExporter\results\5491.pdf"
```

At the folder where the utility was started (at current directory) the log files will be generated according logging settings (see below)


*Parameters:*
 - **template** - the full qualified name (with full path) of report's template to run
 - **type** - the output format for generated report. Supported types/formats are: pdf, xls, xlsx, doc, docx, rtf, odt, ppt, pptx. 
 - **uri** - the full qualified name of report's datasource
 - **outputFile** - the full qualified name of output file. Should contains extension.
 - **loggingEnabled** - the flag for enabling/disabling logging. Supported values: true, false. Not required parameter. By default is disabled. 

All above parameters are required unless otherwise indicated.

***Notes:*** In the **uri** parameter for setting path for datasource the common slash (/) should be used

#### Using resources like images and compiled subreports at templates

The relative path can be used in expressions. The root will be the folder contains main report (passed via **template** utility parameter)

Example of valid image expression:
```xml
<imageExpression><![CDATA["logo2.png"]]></imageExpression>
```

Example of valid subreport expression:
```xml
<subreportExpression><![CDATA["3789_extra_picture.jasper"]]></subreportExpression>
```


#### Logging 

The [log4j2][3] library is using for logging purpose.
The configuration can be set via file ***src\main\resources\log4j2.xml***

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <File name="MainFile" fileName="logs/reportexporter.log">
            <PatternLayout>
                <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
            </PatternLayout>
        </File>
    </Appenders>
    <Loggers>
        <Root level="WARN">
            <AppenderRef ref="MainFile"/>
        </Root>
        <Logger name="com.microting" level="DEBUG" additivity="false">
            <AppenderRef ref="MainFile"/>
        </Logger>
    </Loggers>
</Configuration>
```

##### Disable logging 

* The logging can be disabled with help of **loggingEnabled** utility argument.

The example of command for running utility with disabled logging. *Windows* version:
```bash
C:\tmp>c:\jdk-11.0.8.10-hotspot\bin\java -jar c:\JasperExporter\build\libs\JasperExporter.jar -loggingEnabled=false -template="c:\test\1316.jrxml" -type="pdf" -uri="c:/test/201805281421570431_5491.xml" -outputFile="c:\JasperExporter\results\5491.pdf"
```
or just: 

```bash
C:\tmp>c:\jdk-11.0.8.10-hotspot\bin\java -jar c:\JasperExporter\build\libs\JasperExporter.jar -template="c:\test\1316.jrxml" -type="pdf" -uri="c:/test/201805281421570431_5491.xml" -outputFile="c:\JasperExporter\results\5491.pdf"
```

* The logging can be disabled with help of custom configuration file (log4j2.xml). To apply this custom config the ***log4j2.configurationFile*** property should be used.

The example of command for running utility with custom logging configuration file. *Windows* version:
```bash
C:\tmp>c:\jdk-11.0.8.10-hotspot\bin\java -Dlog4j2.configurationFile=c:\tmp\log4j2.xml -jar c:\JasperExporter\build\libs\JasperExporter.jar -template="c:\test\1316.jrxml" -type="pdf" -uri="c:/test/201805281421570431_5491.xml" -outputFile="c:\JasperExporter\results\5491.pdf"
```

The content of *log4j2.xml* with disabled loggers (appenders) looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="STDOUT"/>
    </Appenders>
    <Loggers>
        <Root level="OFF">
            <AppenderRef ref="STDOUT"/>
        </Root>
    </Loggers>
</Configuration>
```

As alternate, the *Console* appender for redirecting output to console can be used. The sample of such configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="STDOUT">
            <PatternLayout>
                <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
            </PatternLayout>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="WARN">
            <AppenderRef ref="STDOUT"/>
        </Root>
        <Logger name="com.microting"  level="DEBUG" additivity="false">
            <AppenderRef ref="STDOUT"/>
        </Logger>
    </Loggers>
</Configuration>
```

##### Adding certificate to JRE

1. Export certificate with help of browser
2. Import (add) certificate to keystore via [keytool][2] 

```bash
keytool -import -trustcacerts -keystore ..\jre\lib\security\cacerts -storepass changeit -noprompt -alias microting1eform.com -file microting.cer
```


------
##### References

[1]: Fat (uber) jar - is a jar file that contains not only a compiled code of program, but also embeds its all dependencies as well.
More info: https://stackoverflow.com/questions/11947037/what-is-an-uber-jar

[2]: Keytool - Manages a keystore (database) of cryptographic keys, X.509 certificate chains, and trusted certificates. 
More info: https://docs.oracle.com/javase/7/docs/technotes/tools/solaris/keytool.html, https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html, 
https://docs.oracle.com/cd/E19830-01/819-4712/ablqw/index.html

[3]: Introduction: https://logging.apache.org/log4j/2.x/manual/index.html 
Tutorials: https://logging.apache.org/log4j/2.x/articles.html
