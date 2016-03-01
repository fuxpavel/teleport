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

'''
print dbb.send_friend_request("ben", "alex")
dbb.print_connection()
'''
x = requests.post("http://127.0.0.1:8000/api/friendship", json={"sender":"ben", "reply":"alex"})
print x.json()

