# DuckCentral

DuckCentral is an Android app for communicating with the [WifiDuck (https://github.com/SpacehuhnTech/WiFiDuck)](https://github.com/SpacehuhnTech/WiFiDuck). This removes the need for a web app which can free up some space
on the duck as well as removing the need to remember the IP address of the web interface.

## Modified Firmware

DuckCentral has a modified firmware which removes the web interface. You don't have to use this and it is optional.
This app is backwards compatible with the standard firmware. The firmware provided is a modified version of
[https://github.com/SpacehuhnTech/WiFiDuck](https://github.com/SpacehuhnTech/WiFiDuck)

The firmware source is stored under ./firmware and if you have the arduino-cli in your PATH you can use the
gradle task "buildFirmware" to run the build

Make sure you add "https://raw.githubusercontent.com/SpacehuhnTech/arduino/main/package_spacehuhn_index.json" to the additional
urls section in your arduino-cli config file othewrise you will be missing the required board. You will also need to run
`arduino-cli core update-index` and `arduino-cli core install wifiduck:esp8266`
