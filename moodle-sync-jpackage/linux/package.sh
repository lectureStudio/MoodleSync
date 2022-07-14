#!/bin/bash

PRODUCT_NAME="${package.full.name}"
VERSION="${package.version}"
VENDOR="${package.vendor}"
COPYRIGHT="${package.copyright}"
APP_NAME=${package.name}
APP_MAIN_CLASS=moodle.sync.javafx.SyncApplication
APP_JAR=moodle-sync-fx.jar

mkdir "$PRODUCT_NAME"

# Start with modules not discovered with jdeps.
MODULES="jdk.localedata,java.security.jgss,java.security.sasl,jdk.crypto.cryptoki,jdk.crypto.ec,jdk.zipfs"

# Retrieve modules.
echo "Get $APP_NAME modules"

modules=$(jdeps \
	--class-path "${package.input.dir}/lib/*" \
	--multi-release 15 \
	--ignore-missing-deps \
	--print-module-deps \
	-R -q \
	"${package.input.dir}/$APP_JAR")

echo "$modules"

if [ -z "$MODULES" ]
then
	MODULES="$modules"
else
	MODULES="$MODULES,$modules"
fi


# Create the Runtime.
echo "Create Runtime"

jlink \
	--no-header-files --no-man-pages \
	--compress=1 \
	--strip-debug \
	--strip-native-commands \
	--include-locales=de,en \
	--add-modules="$MODULES" \
	--output "runtime"

app_name=$APP_NAME

echo "Packaging $APP_NAME";

if [ -n "${icon[$value]}" ]; then
	app_icon="--icon ${icon[$value]}"
fi

# Create the self-contained Java application package.
jpackage \
	--type app-image \
	--input "${package.input.dir}" \
	--runtime-image "runtime" \
	--java-options -Xmx2048m \
	--app-version "$VERSION" \
	--name $APP_NAME \
	--main-jar $APP_JAR \
	--main-class $APP_MAIN_CLASS \
	--vendor "$VENDOR" \
	--copyright "$COPYRIGHT" \
	${app_icon}

# Copy all files of the generated application package to the common bundle folder.
cp -npR $APP_NAME/* "$PRODUCT_NAME/"

# Remove the individual application package.
rm -Rf $APP_NAME
