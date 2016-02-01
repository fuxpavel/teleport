import requests
import json
import unittest

class Tests(unittest.TestCase):

    def setUp(self):
        self.send_data = { 'username': 'alex',
                           'password': '1234' }
        ret = post(self.send_data)
        self.ret_data = json.loads(ret)

    def tearDown(self):
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
    unittest.main()
