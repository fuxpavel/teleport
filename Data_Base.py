from sqlalchemy import Column, Integer, String, Table, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy import update

Base = declarative_base()
DB_URL = 'sqlite:///Teleport_DB.db'


def get_engine():
    return create_engine(DB_URL)


def get_session(engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.bind = engine
        DBSession = sessionmaker(bind=engine)
        session = DBSession()
        return session


class User(Base):
    __tablename__ = 'User'
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True, nullable=False)
    password = Column(String, nullable=False)
    ip = Column(String)

    def init_data_base(self, engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.create_all(engine)

    def register_user(self, username, password, engine=None):
        session = get_session(engine)
        new_user = User(username=username, password=password)
        session.add(new_user)
        try:
            session.commit()
        except:
            return False
        return True

    def login_user(self, username, password, engine=None):
        info = self.check_exist_user(username, engine)
        if info and info[0].password == password:
            return True
        else:
            return False

    def check_exist_user(self, username, engine=None):
        session = get_session(engine)
        return session.query(User).filter_by(username=username).all()

    def update_user_ip(self, username, ip_address, engine=None):
        session = get_session(engine)
        if session.query(User).filter_by(username=username).update({"ip": ip_address}):
            try:
                session.commit()
            except:
                return False
            return True
        else:
            return False

    def print_db(self, engine=None):
        session = get_session(engine)
        info = session.query(User).all()
        for i in info:
            print str(i.id)+")", i.username, i.password, i.ip


class Friendship(Base):
    __tablename__ = 'Friendship'
    id = Column(Integer, primary_key=True)
    friend1 = Column(String, ForeignKey(User.username))
    friend2 = Column(String, ForeignKey(User.username))

    def create_friendship(self, friend1, friend2, engine=None):
        session = get_session(engine)
        db = User()
        if db.check_exist_user(friend1, engine) and db.check_exist_user(friend2, engine):
            if not self.check_friendship(friend1, friend2, engine):
                new_friendship = Friendship(friend1=friend1, friend2=friend2)
                session.add(new_friendship)
                try:
                    session.commit()
                except:
                    return False
                return True
            else:
                return False
        else:
            return False

    def check_friendship(self, friend1, friend2, engine=None):
        session = get_session(engine)
        if session.query(Friendship).filter_by(friend1=friend1, friend2=friend2).all() or session.query(Friendship).filter_by(friend1=friend2, friend2=friend1).all():
            return True
        else:
            return False

    def print_connection(self, engine=None):
        session = get_session(engine)
        info = session.query(Friendship).all()
        for i in info:
            print i.friend1, "<-->", i.friend2
