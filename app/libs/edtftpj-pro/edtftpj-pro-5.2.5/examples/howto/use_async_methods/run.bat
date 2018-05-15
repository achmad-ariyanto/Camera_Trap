@call ..\env.bat
IF NOT ERRORLEVEL 1 (
  "%JAVA_HOME%\bin\javac" -classpath %CP% AsyncMethods.java
  "%JAVA_HOME%\bin\java" -cp %CP% AsyncMethods %1 %2 %3
)