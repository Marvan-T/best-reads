#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

VARS_JS_PATH=/app/config.js

# Replace placeholders with environment variable values
sed -i "s|VUE_APP_RECOMMENDATIONS_FEATURE=\"undefined\"|VUE_APP_RECOMMENDATIONS_FEATURE=\"${VUE_APP_RECOMMENDATIONS_FEATURE}\"|g" $VARS_JS_PATH
