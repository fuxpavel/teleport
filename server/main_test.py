from sqlalchemy import *
from data_base import *

dbb = User()
dbb.register_user("45","45")
a = dbb.login_user("45","45")
db = FriendRequest()
db.send_friend_request(a,"username1")
