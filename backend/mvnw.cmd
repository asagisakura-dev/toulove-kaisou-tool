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
@REM Apache Maven Wrapper startup batch script, version 3.2.0
@REM
@REM Optional ENV vars
@REM   JAVA_HOME - location of a JDK home dir, required when download maven via java source
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SETLOCAL

SET __MVNW_CMD__=
SET __MVNW_ERROR__=
SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
SET PSModulePath=
IF NOT "%JAVA_HOME%"=="" GOTO valid_java_home

FOR /F "tokens=*" %%i IN ('powershell -noprofile "$ErrorActionPreference='SilentlyContinue';(get-command java).Path"') DO (
  IF "%%i" NEQ "" SET __MVNW_CMD__=%%i
)
GOTO exec_java

:valid_java_home
SET __MVNW_CMD__=%JAVA_HOME%\bin\java.exe

:exec_java
IF "%__MVNW_CMD__%"=="" (
  ECHO Error: JAVA_HOME is not set and 'java' could not be found in your PATH. 1>&2
  SET __MVNW_ERROR__=1
  GOTO error
)

SET __MVNW_BASE_DIR__=%~dp0
SET __MVNW_BASE_DIR_NOSLASH__=%__MVNW_BASE_DIR__:~0,-1%
SET WRAPPER_JAR="%__MVNW_BASE_DIR__%.mvn\wrapper\maven-wrapper.jar"
SET WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%__MVNW_BASE_DIR__%.mvn\wrapper\maven-wrapper.properties") DO (
  IF "%%A"=="wrapperUrl" SET WRAPPER_URL="%%B"
)

IF EXIST %WRAPPER_JAR% GOTO run

ECHO Downloading Maven Wrapper... 1>&2
powershell -noprofile "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest %WRAPPER_URL% -OutFile %WRAPPER_JAR%"

:run
"%__MVNW_CMD__%" ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%__MVNW_BASE_DIR_NOSLASH__%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*
IF ERRORLEVEL 1 GOTO error
GOTO end

:error
SET __MVNW_ERROR__=1

:end
SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@ENDLOCAL & (
  IF "%__MVNW_ERROR__%"=="1" (
    EXIT /B 1
  ) ELSE (
    EXIT /B %ERRORLEVEL%
  )
)
