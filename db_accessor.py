from data_base import *

_dbu = None
_dbfr = None
_dbf = None

def get_user_db():
    global _dbu
    if not _dbu:
        _dbu = User()
        _dbu.init_data_base()
    return _dbu


def get_friend_request_db():
    global _dbfr
    if not _dbfr:
        _dbfr = FriendRequest()
        _dbfr.init_data_base()
    return _dbfr

def get_friendship_db():
    global _dbf
    if not _dbf:
        _dbf = Friendship()
        _dbf.init_data_base()
    return _dbf