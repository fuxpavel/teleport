from data_base import *


_dbu = None
_dbfr = None
_dbf = None
_dbt = None

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

def get_transfers_db():
    global _dbt
    if not _dbt:
        _dbt = Tranfsers()
        _dbt.init_data_base()
    return _dbt