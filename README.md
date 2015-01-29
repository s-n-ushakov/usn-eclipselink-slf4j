<!--
This document is authored using GitHub Flavored Markdown:
https://help.github.com/articles/github-flavored-markdown/
-->

# usn-eclipselink-slf4j

An [EclipseLink](http://eclipse.org/eclipselink/)
<code>[SessionLog](http://eclipse.org/eclipselink/api/2.5/org/eclipse/persistence/logging/SessionLog.html)</code>
implementation for logging messages through [SLF4J](http://www.slf4j.org/).

## Background

[EclipseLink](http://eclipse.org/eclipselink/) is a recognized Java persistence
framework and the reference implementation for JPA 2.0.

[SLF4J](http://www.slf4j.org/) is a recognized Java logging API and a framework
for bridging different Java logging systems in one application if necessary.

EclipseLink makes use of its own logging platform and has a
[built-in bridge](http://eclipse.org/eclipselink/api/2.5/org/eclipse/persistence/logging/JavaLog.html)
to
[java.util.logging](http://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html)
system. It is possible to arrange logging from EclipseLink via SLF4J using
[jul-to-slf4j bridge](http://www.slf4j.org/api/org/slf4j/bridge/SLF4JBridgeHandler.html),
but this solution is known to be CPU expensive (see the bridge description).

This project allows direct logging from EclipseLink via SLF4J with necessary
precautions taken regarding performance and is implemented after EclipseLink
<code>[JavaLog](http://eclipse.org/eclipselink/api/2.5/org/eclipse/persistence/logging/JavaLog.html)</code>.

Other implementations for the same task are also known:

- [EclipseLinkSessionLogger with SLF4J](http://adfinmunich.blogspot.ru/2012/03/eclipselinksessionlogger-with-slf4j.html) -
  a short implementation at blogger.com
- [PE-INTERNATIONAL / org.eclipse.persistence.logging.slf4j](https://github.com/PE-INTERNATIONAL/org.eclipse.persistence.logging.slf4j) - a project at GitHub

## Building

Both [pom.xml](pom.xml) for Maven and [build.xml](build.xml) for Ant are
available. Ant buildfile automatically labels every build with current date.
Maven builds are labeled with project version from the .pom file, but a warning
is issued if this version does not match current date. Artifact/build versions
are currently formatted like "YYYYMMDD" rather than "v1.0", as no distinct
versioning policy has evolved so far.

## Usage – HOW-TO

- download or build the latest 'usn-eclipselink-slf4j-YYYYMMDD.jar' file and add
  it to your application class path;
- register this logger with your `persistence.xml` as per
  [EclipseLink logger documentation](http://eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_logging_logger.htm)
  and
  [EclipseLink wiki on JPA logging](http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging):

```xml
<property name="eclipselink.logging.logger" value="usn.eclipse.persistence.logging.SLF4JLog" />
```

- arrange your logging via SLF4J as per
  [SLF4J user manual](http://www.slf4j.org/manual.html);
- enjoy :)

## Licenses

The project is issued and distributed under the following licenses as per the
original license model by EclipseLink:

- [Eclipse Public License - v 1.0](LICENSE.EPL.html);
- [Eclipse Distribution License - v 1.0](LICENSE.EDL.html).

## Distributions

The following distribution types are available:

- plain JAR:
  - usn-eclipselink-slf4j-YYYYMMDD.jar
- Maven style javadoc and sources JARs:
  - usn-eclipselink-slf4j-YYYYMMDD-javadoc.jar
  - usn-eclipselink-slf4j-YYYYMMDD-sources.jar
- full distribution that contains everything:
  - usn-eclipselink-slf4j-YYYYMMDD-full.jar

<!-- NOTE '../../' below compensate 'blob/master/' that is otherwise added by
          GitHub
          -->
The latest release can be found [here](../../releases/latest).

## TODO

- make the Maven artifact for the project available from some public repository.
