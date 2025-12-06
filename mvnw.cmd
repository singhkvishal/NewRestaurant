@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with @REM to avoid issues with the "for" command
@setlocal
@set MAVEN_OPTS=-Xms256m -Xmx512m
@set ERROR_CODE=0

@REM ==== START VALIDATION ====
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM check for mavenrc in current directory
if exist .mavenrc_pre.cmd call .mavenrc_pre.cmd
:skipRcPre

@REM ==== END VALIDATION ====

@set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
@set MAVEN_CMD_LINE_ARGS=%*

@set MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM 3300d is a workaround for MNG-7009, a regression in Maven 3.8.1 and later
set MAVEN_CMD_LINE_ARGS=-Xmx512m -Dmaven.wagon.http.retryHandler.count=3 %MAVEN_CMD_LINE_ARGS% %MAVEN_OPTS%

@REM Execute the JVM in the background, piping the output to a temporary file.
@REM This allows us to display the output in the main thread.
set OUTPUT_FILE="%MAVEN_PROJECTBASEDIR%\target\mvn-output.txt"

if exist %OUTPUT_FILE% del %OUTPUT_FILE%

%MAVEN_JAVA_EXE% %MAVEN_OPTS% ^
  -classpath %WRAPPER_JAR% ^
  -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %* ^
  > %OUTPUT_FILE% 2>&1

@REM Capture the exit code from the Maven execution
set M2_EXECUTION_STATUS=%ERRORLEVEL%

@REM Display the output from the Maven execution
type %OUTPUT_FILE%

@REM If the exit code is not 0, exit with that code
exit /b %M2_EXECUTION_STATUS%
