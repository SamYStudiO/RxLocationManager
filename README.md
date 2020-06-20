RxLocationManager
=================
Android [Reactive](https://github.com/ReactiveX/RxJava) [LocationManager](https://developer.android.com/reference/android/location/LocationManager) callbacks.

This library also includes some helper observables to get altitude using GPS, barometric sensor or a remote service (build in Google elevation API implementation included).
GPS altitude may returned both ellipsoidal and geoidal (mean sea level), android [Location.getAltitude()](https://developer.android.com/reference/android/location/Location.html#getAltitude()) returns ellipsoidal altitude but in most case you want to get geoidal one.

An extra standalone library is available to parse NMEA messages that may be returned from `observeNmea()`. Actual available parsers are:
- GGA
- GLL
- GSA
- RMC (timing and navigational)
- more to come... and PR welcome!

You can easily add you own parser, just inherit from `net.samystudio.rxlocationmanager.nmea.Nmea` class and use `net.samystudio.rxlocationmanager.nmea.TokenValidator` to validate your messages.

Download
--------
```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager:0.5.0'
```
If you need altitude helpers observables add this as well:
```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager-altitude:0.5.0'
```
If you want to easily parse nmea messages you can use this standalone artifact (note this is already include if you added rxlocationmanager-altitude dependency):
```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager-nmea:0.5.0'
```

Snapshots are available from [Sonatype's snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/).
If you want to run latest snapshot add its repository from your root `build.gradle`:
```groovy
allprojects {
    repositories {
        google()
        jcenter()
        ...
        maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    }
}
```
and change versions:
```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager:0.6.0-SNAPSHOT'
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager-altitude:0.6.0-SNAPSHOT'
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager-nmea:0.6.0-SNAPSHOT'
```

Usage
-----
Check `xxx-sample` directories. A lot of samples showcase [RxPermissions](https://github.com/tbruyelle/RxPermissions) library as well to easily request location permission for callbacks that require it.

Publishing
-----

 1. Change the version in `gradle.properties` to a non-SNAPSHOT version.
 2. Update the `CHANGELOG.md` for the impending release.
 3. Update the `README.md` with the new version.
 4. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
 5. `./gradlew uploadArchives --no-daemon --no-parallel`
 6. `git tag -a X.Y.Z -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 7. Update the `gradle.properties` to the next SNAPSHOT version.
 8. `git commit -am "Prepare next development version."`
 9. `git push && git push --tags`
 10.`./gradlew closeAndReleaseRepository` or visit [Sonatype Nexus](https://oss.sonatype.org/) and promote the artifact.

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
