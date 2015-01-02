#!/usr/bin/env python

from __future__ import print_function
from argparse import ArgumentParser
from sys import stderr, stdout, stdin
from socket import socket, SHUT_RDWR
from select import select
from termios import tcgetattr, tcsetattr, TCSADRAIN
from tty import setraw
import re

BAUDRATES = [300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 57600, 115200]
SERIAL_PORTS = ['usb', 'usart']
PORT = 42232

def main():
    parser = ArgumentParser(
            description='Connects to a Spark.io Remote Serial port')
    parser.add_argument('host', help='IP address (can be in hex) or host name')
    parser.add_argument('baudrate', type=int, help='baud rate (300 .. 115200)')
    parser.add_argument('port', choices=SERIAL_PORTS,
            help='serial port on the device (usart: RX/TX pins)')
    args = parser.parse_args()
    host = hex2ip(args.host) if re.match('[0-9a-fA-F]{8}', args.host) else args.host
    try:
        handshake = chr(BAUDRATES.index(args.baudrate) | (SERIAL_PORTS.index(args.port) << 4))
    except ValueError:
        print('Invalid baud rate; choose from ' + repr(BAUDRATES), file=stderr)
        raise SystemExit(1)
    connect(host, handshake)

def connect(host, handshake):
    s = socket()
    s.connect((host, PORT))
    s.send(handshake)
    print('Connected, press ^C or ^D to terminate the connection.')
    try:
        fd = stdin.fileno()
        old_settings = tcgetattr(fd)
        setraw(fd)
        while True:
            r, _, _ = select([stdin, s], [], [])
            for ready in r:
                if ready is s:
                    r = s.recv(4096)
                    if not r:
                        print('Connection closed by remote peer', file=stderr)
                        return
                    stdout.write(r)
                    stdout.flush()
                elif ready is stdin:
                    r = stdin.read(1)
                    if not r or chr(3) in r or chr(4) in r:
                        s.shutdown(SHUT_RDWR)
                        return
                    s.send(r)
    finally:
        tcsetattr(fd, TCSADRAIN, old_settings)

def hex2ip(host):
    from binascii import unhexlify
    return '.'.join(str(ord(c)) for c in unhexlify(host))

if __name__ == '__main__':
    main()
