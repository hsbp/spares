#define LISTEN_PORT 42232

void byte2hex(const byte src, char *dst) {
	const static char hexchars[17] = "0123456789ABCDEF";

	dst[0] = hexchars[src >> 4];
	dst[1] = hexchars[src & 0x0F];
}

void setup() {
	IPAddress ip = WiFi.localIP();
	static char local_ip[9] = "";

	for (byte i = 0; i < 4; i++) byte2hex(ip[i], local_ip + (i * 2));
	local_ip[8] = '\0';

	Spark.variable("local_ip", local_ip, STRING);
}

void loop() {}
