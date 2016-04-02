import requests
import json
import unittest
import subprocess
import socket
import time
import platform


class Server(object):
    def start(self):
        if platform.system() == 'Windows':
            self.server = subprocess.Popen('python server.py', shell=True, stdout=subprocess.PIPE)
        else:
            self.server = subprocess.Popen(['gunicorn', 'server'], stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                           stderr=subprocess.PIPE)

    def shutdown(self):
        if platform.system() == 'Windows':
            subprocess.call(['taskkill', '/F', '/T', '/PID', str(self.server.pid)])
        else:
            self.server.kill()
            subprocess.call(['pkill', 'gunicorn'], stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                            stderr=subprocess.PIPE)

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


class ServerCommunication(object):
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

    @staticmethod
    def friendship(header, payload=None):
        url = 'http://localhost:8000/api/friendship'
        if payload is not None:
            r = requests.post(url, json=payload, headers=header)
        else:
            r = requests.get(url, headers=header)
        return r.text

    @staticmethod
    def friendship_respones(header, payload):
        url = 'http://localhost:8000/api/friendship/response'
        r = requests.post(url, json=payload, headers=header)
        return r.text

    @staticmethod
    def switch_ip(header, payload):
        url = 'http://localhost:8000/api/switch-ip'
        r = requests.get(url, json=payload, headers=header)
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
        cls.register_send_data = {'username': 'ben', 'password': '1234'}
        cls.login_send_data = {'username': 'ben', 'password': '1234'}
        cls.register_ret_data = json.loads(ServerCommunication.register(cls.register_send_data))
        cls.login_ret_data = json.loads(ServerCommunication.login(cls.login_send_data))

        cls.friendship_send_data = {'reply': 'alex'}
        cls.friendship_response_send_data = {'reply': 'alex', 'status': 'confirm'}
        cls.switch_ip_send_data = {'receiver': 'alex'}
        cls.friendship_ret_data_post = json.loads(
            ServerCommunication.friendship({'Authorization': cls.login_ret_data['token'].encode('ascii')},
                                           cls.friendship_send_data))
        cls.friendship_ret_data_get = json.loads(
            ServerCommunication.friendship({'Authorization': cls.login_ret_data['token'].encode('ascii')}))
        cls.friendship_response_ret_data = json.loads(
            ServerCommunication.friendship_respones({'Authorization': cls.login_ret_data['token'].encode('ascii')},
                                                    cls.friendship_response_send_data))
        cls.switch_ip_ret_data = json.loads(
            ServerCommunication.switch_ip({'Authorization': cls.login_ret_data['token'].encode('ascii')},
                                          cls.switch_ip_send_data))

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()

    def test_register_username(self):
        self.assertEquals(self.register_send_data['username'], self.register_ret_data['username'])

    def test_register_password(self):
        self.assertEquals(self.register_send_data['password'], self.register_ret_data['password'])

    def test_login_username(self):
        self.assertEquals(self.login_send_data['username'], self.login_ret_data['username'])

    def test_login_password(self):
        self.assertEquals(self.login_send_data['password'], self.login_ret_data['password'])

    def test_friendship(self):
        self.assertEqual(self.friendship_ret_data_post['sender'], self.login_ret_data['token'])
        self.assertEqual(self.friendship_send_data['reply'], self.friendship_send_data['reply'])

    def test_friendship_request(self):
        self.assertEqual(self.friendship_response_ret_data['sender'], self.login_ret_data['token'])
        self.assertEqual(self.friendship_response_send_data['reply'], self.friendship_response_send_data['reply'])


if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(TestPostRequest)
    unittest.TextTestRunner(verbosity=2).run(suite)
