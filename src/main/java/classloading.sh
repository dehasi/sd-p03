#!/bin/bash

# compile
javac hw/classloading/command/Command.java
jar cf command.jar hw/classloading/command/Command.class

javac hw/classloading/plugin/Find.java
jar cf plugin.jar hw/classloading/plugin/Find.class

# actual run
java  -cp 'command.jar:plugin.jar:.' hw/classloading/FilePrinter.java  'hw.classloading.plugin.Find'

# clean up
rm *.jar hw/classloading/**/*.class