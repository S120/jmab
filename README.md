# Java Macro Agent-Based (JMAB) toolkit

Copyright (c) 2016 Alessandro Caiani and Antoine Godin

##Overview

JMAB is a Java framework for building macro stock-flow consistent agent-based simulation models. A simulation model is constructed using <a href="http://martinfowler.com/articles/injection.html">dependency injection</a> by creating a <a href="https://blog.mafr.de/2007/11/01/configuration-with-spring-beans/">Spring beans</a> configuration file which specifies which classes to use in the simulation and the values of any attributes (parameters). The Spring configuration file is specified using the system property jabm.config.

The main application class is DesktopSimulationManager

##Prerequisites

JMAB requires Java version 6 or later. It has been tested against version 1.6.0_35 and 1.7.0_75.

Note that on Mac OS, you will need to use the Oracle version of Java instead of the default one shipped with the OS.

##Installation

The project archive can be imported directly into the Eclipse IDE as an existing project.

##Running the examples from the Eclipse IDE

The distribution archive can be imported directly into the Eclipse IDE by using the File/Import menu item. Create a launch configuration in the benchmark project with the main class benchmark.Main and specify which configuration file you want to use by setting the system property jabm.config using the JVM argument -D , for example

-Djabm.config=model/mainBaseline.xml

##Documentation

The folder documentation contains a user guide.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.