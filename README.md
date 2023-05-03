## About this app

[Bitbuckler](https://play.google.com/store/apps/details?id=com.akvelon.bitbuckler&hl=en&gl=US) – Android Native app for Bitbucket which has more than 20k downloads in Play Market. It’s built for easy access to pull requests and source code.

![Play market](https://raw.githubusercontent.com/akvelon/Bitbuckler-Android/main/images/Bitbuckler-screenshot.png)

This app use [Bitbucket REST API](https://developer.atlassian.com/server/bitbucket/rest/v810/intro/#about) for implementing all the features.

## Features

* Authorization
* Pull requests
* Repositories
* Tracking repositories
* Code reviewing
* Issue tracker
* Commenting
* Premium 
* Donations

## Installation

1. Download 'google-services.json' from Firebase console and replace './app/YOUR_GOOGLE_SERVICES.json' with it.
2. Update 'gms.ads.APPLICATION_ID' metadata at 'AndroidManifest.xml' with your Admob app id.
3. Update 'GOOGLE_ADMOB_ID' field with your Admob id in 'app/build.gradle'.
4. Update Bitbucket API 'CLIENT_ID' and 'CLIENT_SECRET' fields in 'app/build.gradle'.
5. Update 'EMAIL' and 'GOOGLE_PLAY_LINK' fields with relative data in 'app/build.gradle'.
6. Update 'privacy_policy.html' in '/assets' dir.
7. Change root packaging and application id.

## Code style

Our project has a strict code style and uses [detekt](https://detekt.dev/) to ensure code quality. Code style can be found [here](https://kotlinlang.org/docs/coding-conventions.html#names-for-backing-properties). Also you could detekt shows pretty good description for all code style issue wich it found. To check whether your code violates any code rules you could run 

```
gradlew detekt
```

No issues should be shown. If you see some code style issues please fix them, or if you think configuration changes are required, contact lead developer.

The configuration file for detekt is located in the git root folder (```../detekt-config.yml```). Also here you could find file for the Android studio code styling. We are trying to keep it as close to detekt rules as possible, but some rules can be done by detekt only, and another by IDE only. So please add an attached file to your Android studio.

