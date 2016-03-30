from sqlalchemy import *
from data_base import *

db = Friendship()
db.init_data_base()
db.create_friendship("9", "10")
db.create_friendship("9", "15")
dbb = FriendRequest()
dbb.send_friend_request("451", "9")
d = User()
print d.username("1")
