from sqlalchemy import *
from data_base import User

db = User()
db.init_data_base()
db.set_user_ip("897", "10.0.0.15")