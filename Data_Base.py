from sqlalchemy import Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

Base = declarative_base()
DB_URL = 'sqlite:///Teleport_DB.db'

def get_engine():
    return create_engine(DB_URL)
    
class User(Base):

    __tablename__ = 'User'
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True, nullable=False)
    password = Column(String, nullable=False)

    def init_data_base(self, engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.create_all(engine)

    def register_user(self, username, password, engine=None):
        engine = engine if engine else get_engine()
        Base.metadata.bind = engine
        DBSession = sessionmaker(bind=engine)
        session = DBSession()
        new_user = User(username=username, password=password)
        session.add(new_user)
        try:
            session.commit()
        except:
            return False
        return True

    def login_user(self, username, password, engine=None):
        engine = engine if engine else get_engine()
        DBSession = sessionmaker()
        DBSession.bind = engine
        session = DBSession()
        info = session.query(User).filter_by(username=username).all()
        if info and info[0].password == password:
            return True
        else:
            return False

    def print_DB(self, engine=None):
        engine = engine if engine else get_engine()
        DBSession = sessionmaker()
        DBSession.bind = self.engine
        session = DBSession()
        info = session.query(User).all()
        for i in info:
            print i.username, i.password
