@call ..\env.bat
IF NOT ERRORLEVEL 1 (
  "%JAVA_HOME%\bin\javac" -classpath %CP% MultiProtocolClient.java
  "%JAVA_HOME%\bin\java" -cp %CP% MultiProtocolClient %1 %2 %3
)