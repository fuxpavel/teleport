import falcon
import json
from db_accessor import get_db

class Login(object):
    def __init__(self):
        self.db = get_db()

    def on_post(self, req, resp):
        body = req.stream.read()
        body = '{"'+body.split('"')[3]+'":"'+body.split('"')[7]+'","'+body.split('"')[11]+'":"'+body.split('"')[15]+'"}'
        print body
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
        self.db = get_db()

    def on_post(self, req, resp):
        body = req.stream.read()
        body = '{"'+body.split('"')[3]+'":"'+body.split('"')[7]+'","'+body.split('"')[11]+'":"'+body.split('"')[15]+'","'+body.split('"')[19]+'":"'+body.split('"')[23]+'"}'
        print body
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



