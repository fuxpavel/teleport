from data_base import User, Friendship, FriendRequest
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
        t = db.login_user("ben", "1234", self.engine)
        self.assertTrue(db.set_user_ip(t, "127.0.0.1", self.engine))

    def test_create_friendship(self):
        db = User()
        f = Friendship()
        db.register_user("alex", "123", self.engine)
        db.register_user("ben", "123", self.engine)
        f1 = db.login_user("ben", "123", self.engine)
        f2 = db.login_user("alex", "123", self.engine)
        self.assertTrue(f.create_friendship(f1, 'alex', self.engine))

    def test_send_friend_request(self):
        db = User()
        r = FriendRequest()
        db.init_data_base(self.engine)
        db.register_user("ben", "123", self.engine)
        db.register_user("alex", "123", self.engine)
        f1 = db.login_user("ben", "123", self.engine)
        f2 = db.login_user("alex", "123", self.engine)
        waiting = r.check_waiting_request(f1, self.engine)
        self.assertEqual(len(waiting), 0)
        self.assertTrue(r.send_friend_request(f2, 'ben', self.engine))
        waiting = r.check_waiting_request(f1, self.engine)
        self.assertEqual(len(waiting), 1)
        self.assertTrue(r.denial_request(f1, 'alex', self.engine))
        waiting = r.check_waiting_request(f1, self.engine)
        self.assertEqual(len(waiting), 0)
        self.assertTrue(r.send_friend_request(f2, 'ben', self.engine))
        self.assertTrue(r.confirm_request(f1, 'alex', self.engine))
        self.assertEqual(len(waiting), 0)


if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(TestDataBase)
    unittest.TextTestRunner(verbosity=2).run(suite)
