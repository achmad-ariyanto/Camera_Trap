@call ..\env.bat
IF NOT ERRORLEVEL 1 (
  "%JAVA_HOME%\bin\javac" -classpath %CP% TransferMultipleFilesDirectories.java
  "%JAVA_HOME%\bin\java" -cp %CP% TransferMultipleFilesDirectories %1 %2 %3 %4 %5
)