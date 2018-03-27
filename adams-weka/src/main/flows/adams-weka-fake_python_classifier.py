# Fake classifier: performs fake train and generates fake predictions
# (classification or regression)
#
# Run from command-line with "--help" to see all the options.
#
# based on code taken from here:
# https://stackoverflow.com/a/18297623

import sys
import argparse
import socket
import json
import datetime
import random
import traceback


def run(port=8000, max_conn=5, buf_size=1024, logging_level=0, seed=1):
    """
    Starts the

    :param port: the port to listen on
    :type port: int
    :param max_conn: the maximum number of connections to accept
    :type max_conn: int
    :param buf_size: the buffer size for receiving data
    :type buf_size: int
    :param logging_level: whether to output logging information (0=off), the higher the more output
    :type logging_level: int
    :param seed: the seed value for the random number generator
    :type seed: int
    """

    rand = random.Random()
    rand.seed = seed

    # start server socket
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind(('localhost', port))
    serversocket.listen(max_conn)

    while True:
        try:
            connection, address = serversocket.accept()
            if logging_level > 0:
                print("------", datetime.datetime.now(), "------")
            data = bytearray()
            while True:
                buf = connection.recv(buf_size)
                if len(buf) > 0:
                    data.extend(buf)

                if len(buf) < buf_size:
                    if logging_level >= 3:
                        print("raw:", data)

                    # turn into json
                    s = data.decode("UTF-8")
                    j = json.loads(s)
                    if logging_level >= 2:
                        print("json:", j)

                    # interpret
                    addr = j['address']
                    return_host = addr[0:addr.find(':')]
                    return_port = int(addr[addr.find(':') + 1:len(addr)])
                    if logging_level >= 1:
                        print("send results back to: %s:%i" % (return_host, return_port))

                    if logging_level > 0:
                        print("type:", j['type'])
                        print("class:", j['class'])
                        print("class_type:", j['class_type'])

                    num_classes = 1
                    classes = None
                    if 'class_labels' in j:
                        classes = j['class_labels']
                        num_classes = len(classes)
                    if logging_level > 0:
                        print("# classes:", num_classes)
                        if classes is not None:
                            print("class labels:", classes)

                    if j['type'] == 'train':
                        result = {'message': None}
                    elif j['type'] == 'classify':
                        if j['class_type'] == 'numeric':
                            result = {'classification': rand.random()}
                        else:
                            result = {'classification': float(rand.randint(0, num_classes))}
                    elif j['type'] == 'distribution':
                        if j['class_type'] == 'numeric':
                            result = {'distribution': [rand.random()]}
                        else:
                            dist = []
                            for i in range(num_classes):
                                dist.append(rand.random())
                            result = {'distribution': [float(i)/sum(dist) for i in dist]}

                    if logging_level >= 1:
                        print("generated results:", result)

                    # send result back
                    clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    clientsocket.connect((return_host, return_port))
                    clientsocket.sendall(bytearray(json.dumps(result), "UTF-8"))
                    clientsocket.close()
                    break
        except Exception:
            traceback.print_exc()


def main():
    """
    Command-line parsing.
    """

    parser = argparse.ArgumentParser(
        description='Executes a fake Python classifier that listens on the specified port and sends results back to caller.')
    parser.add_argument("--port", dest="port", help="port to listen on", default=8000)
    parser.add_argument("--max_conn", dest="max_conn", help="maximum number of connections to accept", default=5)
    parser.add_argument("--buf_size", dest="buf_size", help="size of buffer to use (in bytes)", default=1024)
    parser.add_argument("--logging_level", dest="logging_level", help="whether to output some logging information (0 is off)", default=0)
    parser.add_argument("--seed", dest="seed", help="seed for the random number generator", default=1)
    parsed = parser.parse_args()
    run(port=int(parsed.port), max_conn=int(parsed.max_conn),
        buf_size=int(parsed.buf_size), logging_level=int(parsed.logging_level),
        seed=int(parsed.seed))

if __name__ == '__main__':
    main()
