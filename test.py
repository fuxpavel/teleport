import requests

url = 'http://localhost:8000/login'
username = raw_input('username: ')
password = raw_input('password: ')
payload = {'username': username, 'password': password}
r = requests.post(url, json=payload)
print 'response:'
print r.text
