import falcon
import json
from db_accessor import *


class Logout(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        user = req.get_header('Authorization')
        if self.db.logout_user(user):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"status": "%s"}' % status
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201


class Login(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        ip = req.env['REMOTE_ADDR']
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

        resp.body = '{"token": "%s", "status": "%s"}' % (token, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201


class Register(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        username = userdata['username']
        password = userdata['password']
        confirm = userdata['confirm']

        if self.db.register_user(username, password, confirm):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"status": "%s"}' % status
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201
