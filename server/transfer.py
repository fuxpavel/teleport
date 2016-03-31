import falcon
import json
from db_accessor import *


class Transfer(object):
    def __init__(self):
        self.dbt = get_transfers_db()

    def on_post(self, req, resp):
        sender = req.get_header('Authorization')
        data = action = json.loads(req.stream.read())
        receiver = data['receiver']
        action = data['action']
        if action == 'begin':
            if self.dbt.add_transfer(sender, receiver):
                status = 'success'
            else:
                status = 'failure'
        elif action == 'end':
            if self.dbt.end_transfer(sender, receiver):
                status = 'success'
            else:
                status = 'failure'
        else:
            status = 'failure'

        resp.body = '{"status": "%s"}' % status
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201


    def on_get(self, req, resp):
        user = req.get_header('Authorization')
        incoming = self.dbt.get_incoming_transfers(user)
        outgoing = self.dbt.get_outgoing_transfers(user)
        resp.body = json.dumps({"incoming": incoming, "outgoing": outgoing})
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200

class SwitchIP(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        receiver = req.get_header('Authorization')
        sender = userdata['sender']
        f = get_friendship_db()
        if f.check_friendship(sender, self.db.get_username_by_token(receiver)):
            ip = self.db.get_user_ip(self.db.get_token_by_username(sender))
            resp.body = json.dumps({'msg': ip})
        else:
            resp.body = json.dumps({'msg': 'not friends'})

        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201
