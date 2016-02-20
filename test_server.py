import requests
import json
import unittest
import subprocess
import socket
from retrying import retry


class Server:
    def start(self):
        self.server = subprocess.Popen(['gunicorn', 'server'])

    @staticmethod
    def shutdown(self):
        self.server.kill()
        subprocess.call(['pkill', 'gunicorn'])


@retry(stop_max_delay=60000, wait_fixed=2000)
def check_server(sock):
    # attempt to connect to localhost/8000
    ip = '127.0.0.1'
    port = 8000
    sock.connect((ip, port))
    return True


def post(payload):
    url = 'http://localhost:8000/login'
    r = requests.post(url, json=payload)
    return r.text


class TestPostRequest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        # setting up the server
        Server.start()
        sock = socket.socket()
        if not check_server(sock):
            sock.close()
            raise Exception('Server could not be started')
        sock.close()

        cls.send_data = {'username': 'alex',
                         'password': '1234'}
        # ret = '{"username": "alex", "password": "1234"}'
        ret = post(cls.send_data)
        cls.ret_data = json.loads(ret)

    @classmethod
    def tearDownClass(cls):
        # shutting down the server
        cls.server.kill()
        subprocess.call(['pkill', 'gunicorn'])  # getting rid of additional processes

        del cls.send_data
        del cls.ret_data

    def test_username(self):
        self.assertEqual(self.send_data['username'], self.ret_data['username'], "usernames don't match")

    def test_passwords(self):
        self.assertEqual(self.send_data['password'], self.ret_data['password'], "passwords don't match")


if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(TestPostRequest)
    unittest.TextTestRunner(verbosity=2).run(suite)
