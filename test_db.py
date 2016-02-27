from data_base import User, Friendship
import unittest
from sqlalchemy import create_engine

class TestDataBase(unittest.TestCase):

    def setUp(self):
        self.engine = create_engine('sqlite:///:memory:')
        db = User()
        db.init_data_base(self.engine)

    def test_register(self):
        db = User()
        self.assertTrue(db.register_user("ben", "123", self.engine))

    def test_login(self):
        db = User()
        db.register_user("ben1", "1234", self.engine)
        self.assertTrue(db.login_user("ben1", "1234", self.engine))

    def test_update_ip(self):
        db = User()
        db.register_user("ben", "1234", self.engine)
        self.assertTrue(db.update_user_ip("ben", "127.0.0.1", self.engine))
        self.assertFalse(db.update_user_ip("alex", "127.0.0.1", self.engine))

    def test_create_friendship(self):
        db = User()
        f = Friendship()
        db.register_user("alex", "123", self.engine)
        db.register_user("ben", "123", self.engine)
        self.assertTrue(f.create_friendship("ben", "alex", self.engine))
        self.assertFalse(f.create_friendship("alex", "pavel", self.engine))

if __name__ == '__main__':
    unittest.main()
