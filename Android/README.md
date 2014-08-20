# [Version 1.0.0]
## Feature:
1. Maps Feature.
2. Image Feature.
3. List Feature.
4. Chat Feature.
5. Settings Feature.

## IDE:
Android Studio (version 0.8.2 Build 135.1267975).

## Unit Testing:
Added testing for Android.

## SDK:
Android SDK (API level 19) latest update and Google Play Services (with SDK minimum version Android 3.0 (Honeycomb (API level 11)).

## Google APIs Key:
NB: Google APIs Key needed to change when the environment for project is change, including on Jenkins.

* http://www.androidhive.info/2013/08/android-working-with-google-maps-v2/
* https://developers.google.com/maps/documentation/android/start#installing_the_google_maps_android_v2_api

##Jenkins:
For now, Jenkins can build android application, because Gradle Android projects (particularly if you are using Maps, USB or Compatibility Libraries) have library dependencies that cannot be resolved with the local Android SDK and are not available from Maven Central.
To run testing in Jenkins, open "app.gradle" file located in ".\MobileMAD\app" and uncomment some line with comment remark.
Compile the project and commit into git.
MobileMAD configuration in Jenkins can be seen on: http://140.109.17.55:8080/job/MobileMAD

For reference:
* http://gitlab.iis.sinica.edu.tw/gitlab/sp4_vr/mobileapplication-git/tree/master/Android/jenkins.txt
* https://developer.cloudbees.com/bin/view/Mobile/Android
* https://github.com/makinacorpus/android-archetypes/wiki/Getting-started:-Configure-your-environment
* https://github.com/mosabua/maven-android-sdk-deployer

## Documents:
* http://gitlab.iis.sinica.edu.tw/gitlab/sp4_vr/mobileapplication-git/tree/master/documents
