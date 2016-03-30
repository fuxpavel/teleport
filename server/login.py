import falcon
import json
from db_accessor import get_user_db


class Login(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        ip = req.env['REMOTE_ADDR']
        print ip
        username = userdata['username']
        password = userdata['password']

        token = self.db.login_user(username, password)
        if token:
            if self.db.set_user_ip(token, ip):
                status = 'success'
            else:
                status = 'failure'
        else:
            status = 'failure'

        resp.body = '{"username": "%s", "password": "%s", "token": "%s", "status": "%s"}' % (
        username, password, token, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class Register(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        username = userdata['username']
        password = userdata['password']

        if self.db.register_user(username, password):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"username": "%s", "password": "%s", "status": "%s"}' % (username, password, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201
