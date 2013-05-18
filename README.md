Android Thermostat
=================

An Android based home thermostat project.  For full details see AndroidThermostat.com


## Components

* com.androidthermostat.server - Run on the thermostat itself and is responsible for all communication with the relays that turn on/off the heater, air condition and fan.  It monitors the inside conditions and executes the schedule.  It has no real interface of it's own, but exposes a JSON interface that a client can connect to in order to check the state and send commands.

* com.androidthermostat.client - Provides the interface for the thermostat.  This program runs directly on the thermostat itself and connects to the JSON API to interface with the server component.  It can also be run an another Android device to provide a remote interface to the thermostat.

## Configuring Your Development Environment

*These instructions assume a Windows environment using the Eclipse IDE.*

<dl>
  <dt>1. Download and install the [JRE]  [1] </dt>
  <dt>2. Download and install the [Android SDK]  [2] </dt>
  <dd>In the SDK Manager, install Android Tools, Version 2.2 (8) and 4.03 (15), Google USB driver and Android Support Library.</dd>
  <dt>3. Download and extract [Eclipse Classic]  [3] </dt>
  <dt>4. Install the [Android Development Tools]  [4] Eclipse Plugin </dt>
  <dt>5. Install [GitHub for Windows]  [5] </dt>
  <dd>Create a new folder (c:\github) for your Github repositories and set it as the default storage directory</dd>
  <dd>Clone the Android Thermostat repository to this folder</dd>
  <dd>Copy the server and client apps to your development directory (c:\sourcecode\android)</dd>
  <dt>6. Download the necessary libraries</dt>
  <dd>In Eclipse, choose File -> New -> Project -> Android -> From existing code</dd>
  <dd>For [ActionBarSherlock]  [6] choose the library project</dd>
  <dd>For [HoloEverywhere]  [7], choose the library project</dd>
  <dd>For [IOIO]  [8] choose the IOIOLib project</dd>
  <dd>Make sure each of these compile. If you have problems, make sure the java version is 1.6 in the properties and check for broken references</dd>
  <dt>7. Load the Android Thermostat client and server</dt>
  <dd>In Eclipse, choose File -> New -> Project -> Android -> From existing code</dd>
  <dd>Browse to the com.androidthermostat.client folder and repeat for server</dd>
  <dd>If Eclipse names the project com.androidthermostat.client.MainActivity, right click, choose refactor and rename it to com.androidthermostat.client</dd>
  <dd>If there are compile errors, make sure the java version is set to 1.6 on both projects and fix any broken references</dd>
  <dt>8. Set up the emulator</dt>
  <dd>Click the Run button and in the popup add a new virtual machine.</dd>
  <dd>Name it Froyo and set api version to 2.2 (8)</dd>
  <dd>Repeat to create one called ICS and set API to 4.0 (15)</dd>
  <dd>Run the app on one of them!</dd>
  <dt>9. Set up your phone</dt>
  <dd>Enable USB debugging on your phone from the advanced settings</dd>
  <dd>Download the ADB driver for your phone and follow the instructions [here]  [9] to install it.</dd>
  <dd>Run the app again and it should prompt you to choose a device</dd>
  <dd>If it doesn't prompt, click the down arrow next to run and choose Run Configuration</dd>
</dl>

  [1]: http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1637588.html        "JRE"
  [2]: http://developer.android.com/sdk/index.html                                                "Android SDK"
  [3]: http://www.eclipse.org/downloads/                                                          "Eclipse Classic"
  [4]: http://developer.android.com/sdk/installing/installing-adt.html                            "Android Development Tools"
  [5]: http://windows.github.com/                                                                 "GitHub for Windows"
  [6]: http://actionbarsherlock.com/                                                              "ActionBarSherlock"
  [7]: http://www.holoeverywhere.com/                                                             "HoloEverywhere"
  [8]: https://github.com/ytai/ioio/wiki/Downloads                                                "IOIO"
  [9]: http://developer.android.com/tools/extras/oem-usb.html                                     "Driver Instructions"



## License

Copyright (C) 2012 Trilitech, LLC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.