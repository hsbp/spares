#define LISTEN_PORT 42232

TCPServer server = TCPServer(LISTEN_PORT);
TCPClient client;

void setup() {
	IPAddress ip = WiFi.localIP();
	static char local_ip[9] = "";

	for (byte i = 0; i < 4; i++) byte2hex(ip[i], local_ip + (i * 2));
	local_ip[8] = '\0';

	Spark.variable("local_ip", local_ip, STRING);
	server.begin();
	RGB.control(true);
	RGB.color(255, 0, 0);
}

#define USB_SERIAL 0
#define USART 1

#define SERIAL_BEGIN_ASSIGN(PORT) do { (PORT).begin(baudrate); \
                                       usart = &(PORT); } while (0)

void loop() {
	static Stream *usart = NULL;
	static byte port;
	const static int baudrates[12] = {300, 600, 1200, 2400, 4800,
		9600, 14400, 19200, 28800, 38400, 57600, 115200};

	if (client.connected()) {
		if (usart == NULL) {
			if (!client.available()) {
				RGB.color(255, 127, 0);
				return;
			}
			const byte handshake = client.read();
			port = (handshake >> 4) & 0x01;
			const int baudrate = baudrates[handshake & 0x0F];
			switch (port) {
				case USB_SERIAL:
					SERIAL_BEGIN_ASSIGN(Serial);
					RGB.color(0, 255, 0);
					break;
				case USART:
					SERIAL_BEGIN_ASSIGN(Serial1);
					RGB.color(255, 255, 0);
					break;
				default: client.stop(); return;
			}
		}
		forward_bytes(&client, usart);
		forward_bytes(usart, &client);
	} else {
		if (usart != NULL) {
			usart = NULL;
			RGB.color(255, 0, 0);
		}
		client = server.available();
	}
}

void forward_bytes(Stream *from, Stream *to) {
	 while (from->available()) to->write(from->read());
}

void byte2hex(const byte src, char *dst) {
	const static char hexchars[17] = "0123456789ABCDEF";

	dst[0] = hexchars[src >> 4];
	dst[1] = hexchars[src & 0x0F];
}
