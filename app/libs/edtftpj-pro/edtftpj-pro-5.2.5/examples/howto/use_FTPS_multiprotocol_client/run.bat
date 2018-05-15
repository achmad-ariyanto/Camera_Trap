@call ..\env.bat
IF NOT ERRORLEVEL 1 (
  "%JAVA_HOME%\bin\javac" -classpath %CP% MultiProtocolClientFTPS.java
  "%JAVA_HOME%\bin\java" -cp %CP% MultiProtocolClientFTPS %1 %2 %3
)