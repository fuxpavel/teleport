import requests
import json
import unittest
import subprocess
import socket
import time


class Server:
    def start(self):
        self.server = subprocess.Popen(['gunicorn', 'server'], stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                       stderr=subprocess.PIPE)

    def shutdown(self):
        self.server.kill()
        subprocess.call(['pkill', 'gunicorn'], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    def check_server(self):
        # attempt to connect to localhost/8000
        ip = '127.0.0.1'
        port = 8000
        timeout = 6  # in seconds
        delay = 0.5  # in seconds
        sock = socket.socket()
        start_time = time.time()
        status = False
        while time.time() - start_time < timeout and not status:
            try:
                sock.connect((ip, port))
            except socket.error as e:
                status = False
            else:
                status = True
            time.sleep(delay)
        sock.close()
        return status


class ServerCommunication:
    @staticmethod
    def login(payload):
        url = 'http://localhost:8000/api/login'
        r = requests.post(url, json=payload)
        return r.text

    @staticmethod
    def register(payload):
        url = 'http://localhost:8000/api/register'
        r = requests.post(url, json=payload)
        return r.text


class TestPostRequest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        # setting up the server
        cls.server = Server()
        cls.server.start()
        if not cls.server.check_server():
            raise Exception('Server could not be started')

        # communicating
        cls.register_send_data = {'username': 'alex', 'password': '1234'}
        cls.login_send_data = {'username': 'alex', 'password': '1234'}
        cls.register_ret_data = json.loads(ServerCommunication.login(cls.register_send_data))
        cls.login_ret_data = json.loads(ServerCommunication.login(cls.login_send_data))

    @classmethod
    def tearDownClass(cls):
        # shutting down the server
        cls.server.shutdown()

    def testRegisterUsername(self):
        self.assertEquals(self.register_send_data['username'], self.register_ret_data['username'])

    def testRegisterPassword(self):
        self.assertEquals(self.register_send_data['password'], self.register_ret_data['password'])

    def testLoginUsername(self):
        self.assertEquals(self.login_send_data['username'], self.login_ret_data['username'])

    def testLoginPassword(self):
        self.assertEquals(self.login_send_data['password'], self.login_ret_data['password'])


if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(TestPostRequest)
    unittest.TextTestRunner(verbosity=2).run(suite)
