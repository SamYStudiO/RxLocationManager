0.7.0-SNAPSHOT
--------------
- Add synchronized when adding nmea listener as it may lead to ConcurrentException
- Add FUSED_PROVIDER from Provider enum
- Fix parsing nmea with some locale not using dot as decimal separator + get rid of android Location class dependency (better for testing)
- Fix tests
- Fix publish
- Use Looper instead of Handler/Executor as context for every method
- Replace getLastKnownLocation with getCurrentLocation
- Remove GnssMeasurements/GnssNavigationMessage status callbacks as they are not working anymore with latest android version
- Make time validator less restrictive
- Use LocationManagerCompat for requestLocationUpdates + spotless
- Use LocationManagerCompat for registerGnssStatusCallback
- Bump libraries


0.6.0 (2021-12-06)
------------------
- Update nmea validators to be more compliant with v2.3+ specification
- Remove nmea RMCTiming and rename RMCNavigational to RMC

0.5.0 (2020-06-19)
------------------
- Migrate to **rxjava3**.
- Add handler/looper arguments when possible to match Android API arguments.
- Add getLastKnownLocation as Maybe observable.
- Add GSA satelliteCount property to easily get satellite count.
- Add nmea constructor for building our own message more easily.
- Add RMCTiming and RMCNavigational nmea.
- Fix nmea parsing with trailing spaces.
- Fix GSA modes should be mandatory.
- Fix maximum satellite count from GGA
- Fix GGA quality enum list.
- Change EnumValidator now uses CharArray insteadof array of char.
- Rename observeGnssStatusChanged to observeGnssStatusOnChanged for consistency.

0.3.0 (2018-11-07)
------------------
- Add Nmea rxlocationmanager-nmea module library to easily parse name sentence.
- Migrate altitude module nmea parsing with new nmea parser module library.
- Improve doc.

0.2.0 (2018-11-03)
------------------
- Make provider arguments easier with enum.
- Make locationManager property public.
- Make GnssStatus compatible pre N.
- Add doc about LocationManager.requestSingleUpdate.
- Move public inner class outside.
- Doc.

0.1.0 (2018-10-31)
------------------
Initial release.

