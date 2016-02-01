import requests
import json
import unittest
import subprocess
import time

class TestPostRequest(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        # setting up the server
        self.server = subprocess.Popen(('gunicorn', 'server'), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        time.sleep(1) # let server start

        self.send_data = {'username': 'alex',
                          'password': '1234'}
        # ret = '{"username": "alex", "password": "1234"}'
        ret = post(self.send_data)
        self.ret_data = json.loads(ret)

    @classmethod
    def tearDownClass(self):
        # shutting down the server
        self.server.kill()
        subprocess.call(['pkill', 'gunicorn']) # getting rid of additional processes

        del self.send_data
        del self.ret_data

    def test_username(self):
        self.assertEqual(self.send_data['username'], self.ret_data['username'], "usernames don't match")

    def test_passwords(self):
        self.assertEqual(self.send_data['password'], self.ret_data['password'], "passwords don't match")


def post(payload):
    url = 'http://localhost:8000/login'
    r = requests.post(url, json=payload)
    return r.text


if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(TestPostRequest)
    unittest.TextTestRunner(verbosity=2).run(suite)