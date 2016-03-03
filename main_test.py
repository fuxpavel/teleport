from sqlalchemy import Column, Integer, String
from sqlalchemy import *
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy import update
import data_base
from data_base import *
from db_accessor import get_user_db
import requests
db = User()
db.init_data_base()
db.register_user("ben","1234")
db.register_user("alex","1234")
db.register_user("pavel","1234")
dbb = FriendRequest()
dbb.send_friend_request("alex", "ben")
dbb.send_friend_request("pavel", "ben")
waiting = dbb.check_waiting_request("pavel")
'''
print dbb.send_friend_request("ben", "alex")
dbb.print_connection()
'''
dbb.print_connection()
x = requests.post("http://127.0.0.1:8000/api/friendship/response", json={"sender":"alex","reply":"ben","status":"confirm"})
print x.json()
dbb.print_connection()
d = Friendship()
d.print_connection()

