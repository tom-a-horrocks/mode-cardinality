@ECHO OFF
MODE con:cols=82 lines=82

SET rootdir=%~dp0
SET datadir="D:\\PhD Data\\processed"

CD /d %rootdir%
call ant clean
call ant compile
call ant jar

If "%computername%" NEQ "CLEMENTINE" (
	cd /d %datadir%
)
java -ea -jar %rootdir%\\build\\jar\\cluster.jar
pause