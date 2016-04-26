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

    def register_user(self, username, password, confirm, engine=None):
        session = get_session(engine)
        if confirm == password:
            new_user = User(username=username, password=password)
            session.add(new_user)
            try:
                session.commit()
            except:
                return False
            return True
        else:
            return False

    def username_like(self, name, reply, engine=None):
        username = []
        session = get_session(engine)
        names = session.query(User).filter(User.username.like(name + '%')).all()
        for name in names:
            username.append(name.username)
        lst = set(username) - set(Friendship().get_friends_list(reply))
        lst = lst - {User().get_username_by_token(reply)}
        return list(lst)

    def login_user(self, username, password, engine=None):
        info = self.check_exist_user_username(username, engine)
        if info and info[0].password == password:
            token = str(random.randint(1, 1000))
            return self.set_token(username, token, engine)
        else:
            return False

    def logout_user(self, token, engine=None):
        if self.check_exist_user_token(token):
            if self.set_user_ip(token, None, engine):
                if not self.set_token(self.get_username_by_token(token), None, engine):
                    return True
                else:
                    return False
            else:
                return False
        else:
            return False

    def get_username_by_token(self, token, engine=None):
        session = get_session(engine)
        if self.check_exist_user_token(token, engine):
            return session.query(User).filter_by(token=token).all()[0].username
        else:
            return False

    def get_token_by_username(self, username, engine=None):
        session = get_session(engine)
        if self.check_exist_user_username(username, engine):
            return session.query(User).filter_by(username=username).all()[0].token
        else:
            return False

    def set_token(self, username, token, engine=None):
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

    def set_user_ip(self, token, ip_address, engine=None):
        session = get_session(engine)
        if session.query(User).filter_by(token=token).update({"ip": ip_address}):
            try:
                session.commit()
            except:
                return False
            return True
        else:
            return False

    def get_user_ip(self, token, engine=None):
        session = get_session(engine)
        if self.check_exist_user_token(token, engine):
            return session.query(User).filter_by(token=token).all()[0].ip
        else:
            return False


class FriendRequest(Base):
    __tablename__ = 'FriendRequest'
    id = Column(Integer, primary_key=True)
    sender = Column(String, ForeignKey(User.token))
    reply = Column(String, ForeignKey(User.token))

    def init_data_base(self, engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.create_all(engine)

    def check_incoming_request(self, reply, engine=None):
        session = get_session(engine)
        db = User()
        reply = db.get_username_by_token(reply, engine)
        incoming = session.query(FriendRequest).filter_by(reply=reply).all()
        lst = []
        for i in incoming:
            lst.append(i.sender)
        return lst

    def check_outgoing_request(self, sender, engine=None):
        session = get_session(engine)
        db = User()
        sender = db.get_username_by_token(sender, engine)
        outgoing = session.query(FriendRequest).filter_by(sender=sender).all()
        lst = []
        for i in outgoing:
            lst.append(i.reply)
        return lst

    def confirm_request(self, sender, reply, engine=None):
        session = get_session(engine)
        db = Friendship()
        u = User()
        if db.create_friendship(sender, reply, engine):
            session.query(FriendRequest).filter_by(sender=reply, reply=u.get_username_by_token(sender, engine)).delete()
            try:
                session.commit()
            except:
                return False
            return True
        else:
            return False

    def denial_request(self, sender, reply, engine=None):
        session = get_session(engine)
        u = User()
        sender = u.get_username_by_token(sender, engine)
        session.query(FriendRequest).filter_by(sender=reply, reply=sender).delete()
        try:
            session.commit()
        except:
            return False
        return True

    def send_friend_request(self, sender, reply, engine=None):
        session = get_session(engine)
        db = User()
        f = Friendship()
        if db.check_exist_user_token(sender, engine) and db.check_exist_user_username(reply, engine) and \
                        db.get_username_by_token(sender) != reply:
            sender = db.get_username_by_token(sender, engine)
            if not f.check_friendship(sender, reply, engine) and not (
                self.check_friend_request(sender, reply, engine) or self.check_friend_request(reply, sender, engine)):
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


class Friendship(Base):
    __tablename__ = 'Friendship'
    id = Column(Integer, primary_key=True)
    friend1 = Column(String, ForeignKey(User.token))
    friend2 = Column(String, ForeignKey(User.token))

    def init_data_base(self, engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.create_all(engine)

    def remove_friendship(self, user, remove, engine=None):
        session = get_session(engine)
        username = User().get_username_by_token(user)
        if session.query(Friendship).filter_by(friend1=username, friend2=remove).all():
            session.query(Friendship).filter_by(friend1=username, friend2=remove).delete()

        elif session.query(Friendship).filter_by(friend1=remove, friend2=username).all():
            session.query(Friendship).filter_by(friend1=remove, friend2=username).delete()
        try:
            session.commit()
        except:
            return False
        return True

    def create_friendship(self, friend1, friend2, engine=None):
        session = get_session(engine)
        db = User()
        if db.check_exist_user_token(friend1, engine) and db.check_exist_user_username(friend2, engine) and \
                        db.get_username_by_token(friend1) != friend2:
            friend1 = db.get_username_by_token(friend1)
            if not (self.check_friendship(friend1, friend2, engine) or self.check_friendship(friend2, friend1, engine)):
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
        if session.query(Friendship).filter_by(friend1=friend1, friend2=friend2).all() or session.query(
                Friendship).filter_by(friend1=friend2, friend2=friend1).all():
            return True
        else:
            return False

    def get_friends_list(self, token, engine=None):
        session = get_session(engine)
        db = User()
        reply = db.get_username_by_token(token, engine)
        list1 = session.query(Friendship).filter_by(friend1=reply).all()
        list2 = session.query(Friendship).filter_by(friend2=reply).all()
        lst = []
        for i in list1:
            lst.append(i.friend2)
        for i in list2:
            lst.append(i.friend1)
        return lst


class Tranfsers(Base):
    __tablename__ = 'Transfers'
    id = Column(Integer, primary_key=True)
    sender = Column(String, ForeignKey(User.token))
    receiver = Column(String, ForeignKey(User.token))
    file_name = Column(String)
    file_size = Column(String)
    status = Column(String)

    def init_data_base(self, engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.create_all(engine)

    def add_transfer(self, sender_token, receiver_name, file_name, file_size, engine=None):
        session = get_session(engine)
        users_table = User()
        friendships_table = Friendship()
        transfers_table = Tranfsers()

        if users_table.check_exist_user_token(sender_token, engine) and \
                users_table.check_exist_user_username(receiver_name, engine) and sender_token != receiver_name:
            sender_name = users_table.get_username_by_token(sender_token, engine)
            if friendships_table.check_friendship(sender_name, receiver_name, engine):
                if not transfers_table.check_transfer(sender_name, receiver_name):
                    new_transfer = Tranfsers(sender=sender_name, receiver=receiver_name, file_name=file_name,
                                             file_size=file_size, status='begin')
                    session.add(new_transfer)
                    try:
                        session.commit()
                    except:
                        return -1
                a = session.query(Tranfsers).filter_by(sender=sender_name, receiver=receiver_name).all()
                if a:
                    return a[len(a) - 1].id
                else:
                    return -1
            else:
                return -1
        else:
            return -1

    def get_transfers_not_pass(self, token, engine=None):
        session = get_session(engine)
        username = User().get_username_by_token(token)
        not_pass = session.query(Tranfsers).filter_by(receiver=username, status='not_pass').all()
        lst = []
        for i in not_pass:
            lst += [i.sender + ":" + i.file_size + ":" + i.file_name]
        return lst

    def transfer_not_pass(self, id_transfer, engine=None):
        session = get_session(engine)
        session.query(Tranfsers).filter_by(id=id_transfer).update({"status": "not_pass"})
        try:
            session.commit()
        except:
            return False
        return True

    def end_transfer(self, id_transfer, engine=None):
        session = get_session(engine)
        session.query(Tranfsers).filter_by(id=id_transfer).delete()
        try:
            session.commit()
        except:
            return False
        return True

    def get_incoming_transfers(self, user, engine=None):
        session = get_session(engine)
        users_table = User()
        user = users_table.get_username_by_token(user, engine)
        incoming = session.query(Tranfsers).filter_by(receiver=user).all()
        lst = []
        for i in incoming:
            lst += [i.sender + ":" + i.file_size + ":" + i.file_name]
        return lst

    def get_outgoing_transfers(self, user, engine=None):
        session = get_session(engine)
        users_table = User()
        user = users_table.get_username_by_token(user, engine)
        incoming = session.query(Tranfsers).filter_by(sender=user).all()
        lst = []
        for i in incoming:
            lst += [i.sender]
        return lst

    def check_transfer(self, sender, receiver, engine=None):
        session = get_session(engine)
        if session.query(Tranfsers).filter_by(sender=sender, receiver=receiver).all():
            return True
        else:
            return False
