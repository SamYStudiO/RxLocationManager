RxLocationManager
=================
[Reactive](https://github.com/ReactiveX/RxJava) [LocationManager](https://developer.android.com/reference/android/location/LocationManager) callbacks.

This library also includes some helper observables to get altitude using GPS, barometric sensor or a remote service (build in Google elevation API implementation included).
GPS altitude may returned both ellipsoidal and geoidal (mean sea level), android [Location.getAltitude()](https://developer.android.com/reference/android/location/Location.html#getAltitude()) returns ellipsoidal altitude but in most case you want to get geoidal one.

Download
--------
```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager:0.1.0'
```
If you need altitude helpers observables add this as well:
```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager-altitude:0.1.0'
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
and change library version:

```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager:0.2.0-SNAPSHOT'
```
If you need altitude helpers observables add this as well:
```groovy
implementation 'net.samystudio.rxlocationmanager:rxlocationmanager-altitude:0.2.0-SNAPSHOT'
```

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
