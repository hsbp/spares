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

The default port is 42232 (the meaning of life + 232 for RS-232). The first
byte describes which port to use (USB, USART 1/2) along with the baudrate.
The available baud rates and bit patterns can be seen below.

	MSB   LSB
	|       |
	...0 ....  USB   (/dev/ttyACMx under Linux, Serial in Core Firmware)
	...1 ....  USART (RX/TX pins, Serial1 in Core Firmware)
	.... ....
	.... 0000     300 bps
	.... 0001     600 bps
	.... 0010    1200 bps
	.... 0011    2400 bps
	.... 0100    4800 bps
	.... 0101    9600 bps
	.... 0110   14400 bps
	.... 0111   19200 bps
	.... 1000   28800 bps
	.... 1001   38400 bps
	.... 1010   57600 bps
	.... 1011  115200 bps

For example, 9600 bps over USB is 0x05, and since the current versions
ignore the three most significant bits, ASCII 0x45 (`E`) can be used for
testing; the same applies to 115200 over USART, with ASCII 0x3b (`;`).

License
-------

The whole project is available under MIT license, see `LICENSE.txt`.
