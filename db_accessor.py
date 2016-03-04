from data_base import User
from data_base import FriendRequest
_db = None
_dbb = None

def get_user_db():
    global _db
    if not _db:
        _db = User()
        _db.init_data_base()
    return _db


def get_friend_request_db():
    global _dbb
    if not _dbb:
        _dbb = FriendRequest()
        _dbb.init_data_base()
    return _dbb