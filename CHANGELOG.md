0.5.0-SNAPSHOT (rxjava3)
------------------
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
- Migrate to rxjava3.

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

