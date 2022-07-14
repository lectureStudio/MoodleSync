@echo off
setlocal EnableExtensions EnableDelayedExpansion

set PRODUCT_NAME="${package.full.name}"
set VERSION="${package.version}"
set VENDOR="${package.vendor}"
set COPYRIGHT="${package.copyright}"
set APP_NAME=${package.name}
set APP_MAIN_CLASS=moodle.sync.javafx.SyncApplication
set APP_JAR=moodle-sync-fx.jar

:: Start with modules not discovered with jdeps.
set MODULES="jdk.localedata,java.security.jgss,java.security.sasl,jdk.crypto.cryptoki,jdk.crypto.ec,jdk.zipfs"

:: Retrieve modules.
echo Get %APP_NAME% modules
for /F %%i in ('jdeps ^
				--class-path "${package.input.dir}\lib\*" ^
				--multi-release 15 ^
				--ignore-missing-deps ^
				--print-module-deps ^
				-R -q ^
				"${package.input.dir}\%APP_JAR%"') do (
	echo "%%i"
	if [!MODULES!] == [] (
		set "MODULES=%%i"
	) else (
		set "MODULES=!MODULES!,%%i"
	)
)

:: Create the Runtime.
echo Create Runtime

jlink ^
	--no-header-files --no-man-pages ^
	--compress=1 ^
	--strip-debug ^
	--strip-native-commands ^
	--include-locales=de,en ^
	--add-modules="%MODULES%" ^
	--output "%PRODUCT_NAME%\runtime"


echo Packaging %APP_NAME%

set app_path="%PRODUCT_NAME%\%APP_NAME%"
set exe_path=!app_path!.exe

REM Create the self-contained Java application package.
jpackage ^
	--type app-image ^
	--input "${package.input.dir}" ^
	--runtime-image "%PRODUCT_NAME%\runtime" ^
	--dest "%PRODUCT_NAME%" ^
	--java-options -Xmx2048m ^
	--app-version %VERSION% ^
	--name %APP_NAME% ^
	--main-jar %APP_JAR% ^
	--main-class %APP_MAIN_CLASS% ^
	--vendor %VENDOR% ^
	--copyright %COPYRIGHT%

REM Remove Runtime since there is a shared one.
rmdir /Q/S "!app_path!\runtime"

REM Copy all files of the generated application package to the common bundle folder.
robocopy !app_path! %PRODUCT_NAME%\ /NFL /NDL /NJH /NJS /nc /ns /np /MOV /E

REM Remove the individual application package.
del /F/Q/S !app_path! > NUL
rmdir /Q/S !app_path!

:: Remove unnecessary resources from the final bundle package.
del /F/Q/S "%PRODUCT_NAME%\api-ms-win-*.dll" > NUL
del /F/Q/S "%PRODUCT_NAME%\msvcp140.dll" > NUL
del /F/Q/S "%PRODUCT_NAME%\ucrtbase.dll" > NUL
del /F/Q/S "%PRODUCT_NAME%\vcruntime140.dll" > NUL
del /F/Q/S "%PRODUCT_NAME%\*.ico" > NUL
del /F/Q/S "%PRODUCT_NAME%\.jpackage.xml" > NUL

REM Copy bundle folder to parent target.
robocopy %PRODUCT_NAME% ${package.output.dir}/%PRODUCT_NAME%\ /NFL /NDL /NJH /NJS /nc /ns /np /MOV /E

endlocal