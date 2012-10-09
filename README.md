Android Thermostat
=================

An Android based home thermostat project.  For full details see AndroidThermostat.com


## Components

* com.androidthermostat.server - Run on the thermostat itself and is responsible for all communication with the relays that turn on/off the heater, air condition and fan.  It monitors the inside conditions and executes the schedule.  It has no real interface of it's own, but exposes a JSON interface that a client can connect to in order to check the state and send commands.

* com.androidthermostat.client - Provides the interface for the thermostat.  This program runs directly on the thermostat itself and connects to the JSON API to interface with the server component.  It can also be run an another Android device to provide a remote interface to the thermostat.

## License

Copyright (C) 2012 Trilitech, LLC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.