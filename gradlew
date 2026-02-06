#!/bin/bash
export ANDROID_SDK_ROOT=/home/ubuntu/android-sdk
export ANDROID_HOME=/home/ubuntu/android-sdk
exec /tmp/gradle-8.1/bin/gradle "$@"
