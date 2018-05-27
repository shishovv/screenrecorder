# screenrecorder

## Description
Screen recorder is a java library that allows you to record the screen from java code.

The library currently supports h.264 (.mov format) and TSCC (.avi format) video codecs.

External libraries used:

- jcodec (http://jcodec.org)
- monte media library (http://www.randelshofer.ch/monte/)

## Building project
[Gradle](http://www.gradle.org) is used to build. JDK 1.8 is required.

To build jar file, run:

    ./gradlew recorder:jar
    
Run sample application (30sec recording):

    ./gradlew run
