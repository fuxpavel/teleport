import falcon
import json
from db_accessor import get_user_db

class Login(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        body = req.stream.read()
        userdata = json.loads(body)
        username = userdata['username']
        password = userdata['password']

        if self.db.login_user(username, password):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"username": "%s", "password": "%s", "status": "%s"}' % (username, password, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class Register(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        body = req.stream.read()
        userdata = json.loads(body)
        username = userdata['username']
        password = userdata['password']

        if self.db.register_user(username, password):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"username": "%s", "password": "%s", "status": "%s"}' % (username, password, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200



