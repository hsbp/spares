SpaReS: Spark.io Remote Serial port
===================================

Connects the serial ports of the Spark Core board to a TCP channel.

Protocol design
---------------

### Announcement ###

The IP address of the device is stored in hexadecimal format in the
variable called `local_ip` and can be queries in the following way.

	$ curl "https://api.spark.io/v1/devices/XXX/local_ip/?access_token=YYY"
	{
	  "cmd": "VarReturn",
	  "name": "local_ip",
	  "result": "C0A80169",
	  "coreInfo": {
		"last_app": "",
		"last_heard": "2014-12-28T10:18:03.802Z",
		"connected": true,
		"deviceID": "XXX"
	  }
	}
	$ python
	>>> from binascii import unhexlify
	>>> map(ord, unhexlify('C0A80169'))
	[192, 168, 1, 105]

### Connecting ###

The default port is 42232 (the meaning of life + 232 for RS-232).
