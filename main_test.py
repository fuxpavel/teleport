from sqlalchemy import Column, Integer, String
from sqlalchemy import *
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy import update
import data_base
from data_base import *
from db_accessor import get_user_db
import requests
import platform
db = User()
db.init_data_base()
db.register_user("ben", "1234")
db.register_user("alex", "1234")
db.register_user("pavel", "1234")
a = db.login_user('ben', '1234')
b = db.login_user('alex', '1234')
db.set_user_ip(a,'1.2.3.4')
db.set_user_ip(b,'8.8.8.8')
'''
print dbb.send_friend_request("ben", "alex")
dbb.print_connection()
'''
c = requests.post("http://127.0.0.1:8000/api/register", json={"username": '123', "password": '123'})
print c.json()
'''
x = requests.get("http://127.0.0.1:8000/api/friendship", headers={"reply": b})
print x.json()
d = Friendship()





x = requests.get("http://127.0.0.1:8000/api/switch-ip", headers={"sender": a, 'receiver': b})
print x.json()
'''

print platform.system()