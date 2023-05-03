# Bitbuckler Android client

[Bitbuckler](https://play.google.com/store/apps/details?id=com.akvelon.bitbuckler&hl=en&gl=US) – a convenient and user-friendly mobile client for Bitbucket. 

## Installation

1. Download 'google-services.json' from Firebase console and replace './app/YOUR_GOOGLE_SERVICES.json' with it.
2. Update 'gms.ads.APPLICATION_ID' metadata at 'AndroidManifest.xml' with your Admob app id.
3. Update 'GOOGLE_ADMOB_ID' field with your Admob id in 'app/build.gradle'.
4. Update Bitbucket API 'CLIENT_ID' and 'CLIENT_SECRET' fields in 'app/build.gradle'.
5. Update 'EMAIL' and 'GOOGLE_PLAY_LINK' fields with relative data in 'app/build.gradle'.
6. Update 'privacy_policy.html' in '/assets' dir.
7. Change root packaging and application id.