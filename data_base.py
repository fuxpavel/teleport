from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import random

Base = declarative_base()
DB_URL = 'sqlite:///Teleport_DB.db'


def get_engine():
    return create_engine(DB_URL)


def get_session(engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.bind = engine
        #DBSession = sessionmaker(bind=engine)
        session = sessionmaker(bind=engine)()
        return session


class User(Base):
    __tablename__ = 'User'
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True, nullable=False)
    password = Column(String, nullable=False)
    ip = Column(String)
    token = Column(String, unique=True)

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
        info = self.check_exist_user_username(username, engine)
        if info and info[0].password == password:
            return self.set_token(username, engine)
        else:
            return False

    def get_username_by_token(self, token, engine=None):
        session = get_session(engine)
        if self.check_exist_user_token(token):
            return session.query(User).filter_by(token=token).all()[0].username
        else:
            return False

    def get_token_by_username(self, username, engine=None):
        session = get_session(engine)
        if self.check_exist_user_username(username):
            return session.query(User).filter_by(username=username).all()[0].token
        else:
            return False

    def set_token(self, username, engine=None):
        token = str(random.randint(1, 1000))
        session = get_session(engine)
        if session.query(User).filter_by(username=username).update({"token": token}):
            try:
                session.commit()
            except:
                return False
            return token
        else:
            return False

    def check_exist_user_token(self, token, engine=None):
        session = get_session(engine)
        return session.query(User).filter_by(token=token).all()

    def check_exist_user_username(self, username, engine=None):
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
            print str(i.id)+")", i.username, i.password, i.ip, i.token


class FriendRequest(Base):
    __tablename__ = 'Friend_request'
    id = Column(Integer, primary_key=True)
    sender = Column(String, ForeignKey(User.token))
    reply = Column(String, ForeignKey(User.token))
    status = Column(String)

    def init_data_base(self, engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.create_all(engine)

    def check_waiting_request(self, reply, engine=None):
        session = get_session(engine)
        waiting = session.query(FriendRequest).filter_by(reply=reply).all()
        lst = []
        for i in waiting:
            lst.append(str(i.sender))
        return lst

    def confirm_request(self, sender, reply, engine=None):
        session = get_session(engine)
        db = Friendship()
        if db.create_friendship(sender, reply, engine):
            session.query(FriendRequest).filter_by(sender=sender, reply=reply).delete()
            try:
                session.commit()
            except:
                return False
            return True
        else:
            return False

    def denial_request(self, sender, reply, engine=None):
        session = get_session(engine)
        session.query(FriendRequest).filter_by(sender=sender, reply=reply).delete()
        try:
            session.commit()
        except:
            return False
        return True

    def send_friend_request(self, sender, reply, engine=None):
        session = get_session(engine)
        db = User()
        f = Friendship()
        if db.check_exist_user_token(sender, engine) and db.check_exist_user_token(reply, engine):
            if not f.check_friendship(sender, reply, engine) and not self.check_friend_request(sender, reply, engine):
                new_request = FriendRequest(sender=sender, reply=reply)
                session.add(new_request)
                try:
                    session.commit()
                except:
                    return False
                return True
            else:
                return False
        else:
            return False

    def check_friend_request(self, sender, reply, engine=None):
        session = get_session(engine)
        if session.query(FriendRequest).filter_by(sender=sender, reply=reply).all():
            return True
        else:
            return False

    def print_connection(self, engine=None):
        session = get_session(engine)
        info = session.query(FriendRequest).all()
        for i in info:
            print i.sender, "<-->", i.reply


class Friendship(Base):
    __tablename__ = 'Friendship'
    id = Column(Integer, primary_key=True)
    friend1 = Column(String, ForeignKey(User.token))
    friend2 = Column(String, ForeignKey(User.token))

    def create_friendship(self, friend1, friend2, engine=None):
        session = get_session(engine)
        db = User()
        if db.check_exist_user_token(friend1, engine) and db.check_exist_user_token(friend2, engine):
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
