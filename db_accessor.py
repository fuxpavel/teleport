from  data_base import User

_db = None


def get_db():
    global _db
    if not _db:
        _db = User()
        _db.init_data_base()
    return _db
