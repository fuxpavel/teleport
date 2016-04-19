from sqlalchemy import *
from data_base import *

dbb = User()
dbb.register_user("45","45")
a = dbb.login_user("ben","1234")
db = Friendship()
db.create_friendship(a,"alex")
new_transfer = Tranfsers(sender="alex", receiver="gal")
session = get_session()
session.add(new_transfer)
session.commit()
a = session.query(Tranfsers).filter_by(sender="alex", receiver="gal").all()
print a[len(a)-1].id