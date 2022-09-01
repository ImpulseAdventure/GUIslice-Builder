@echo off
set DIR="%~dp0"
set JAVA_EXEC="%DIR:"=%\bin\javaw"



pushd %DIR% & start "builder" %JAVA_EXEC% %CDS_JVM_OPTS%  -p "%~dp0/../app" -m builder/builder.Builder  %* & popd
