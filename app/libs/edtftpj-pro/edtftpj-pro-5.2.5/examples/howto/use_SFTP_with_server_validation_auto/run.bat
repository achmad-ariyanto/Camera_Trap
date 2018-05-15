@call ..\env.bat
IF NOT ERRORLEVEL 1 (
  "%JAVA_HOME%\bin\javac" -classpath %CP% UseSFTPWithServerValidationAuto.java
  "%JAVA_HOME%\bin\java" -cp %CP% UseSFTPWithServerValidationAuto %1 %2 %3 %4
)