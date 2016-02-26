from data_base import User
import unittest
from sqlalchemy import create_engine

class TestDataBase(unittest.TestCase):

    def setUp(self):
        self.engine = create_engine('sqlite:///:memory:')
        db = User()
        db.init_data_base(self.engine)
        de = User()

    def test_register(self):
        db = User()
        self.assertTrue(db.register_user("ben", "123", self.engine))

    def test_login(self):
        db = User()
        db.register_user("ben1", "1234", self.engine)
        self.assertTrue(db.login_user("ben1", "1234", self.engine))


if __name__ == '__main__':
    unittest.main()
