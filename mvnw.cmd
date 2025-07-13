@REM Maven Wrapper Script for Windows
@REM This script allows running Maven without having it installed locally

@echo off
setlocal enabledelayedexpansion

set MAVEN_WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set MAVEN_WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties

REM Check if wrapper jar exists
if not exist "%MAVEN_WRAPPER_JAR%" (
    echo Maven Wrapper JAR not found. Downloading...
    if not exist ".mvn\wrapper" mkdir .mvn\wrapper
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%MAVEN_WRAPPER_JAR%'"
)

REM Execute Maven with wrapper
java -cp "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
