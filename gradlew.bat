@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
setlocal
set DEFAULT_JVM_OPTS=-Xmx2048m -Xms256m
set CLASSPATH=gradle\wrapper\gradle-wrapper.jar
java %DEFAULT_JVM_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
