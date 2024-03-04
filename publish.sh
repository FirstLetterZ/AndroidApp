#!/bin/sh
modelName="gsonParse"
#modelName="binding"
#modelName="network"
#modelName="webview"
#modelName="support"
#modelName="glideutil"
#modelName="toolexpand"
#modelName="rvexpand"

./gradlew :$modelName:clean --info
./gradlew :$modelName:build --info
./gradlew :$modelName:publish --info