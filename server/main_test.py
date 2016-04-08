from sqlalchemy import *
from data_base import *

dbb = User()
dbb.register_user("45","45")
a = dbb.login_user("ben","1234")
db = Friendship()
db.create_friendship(a,"alex")
