import falcon
import json
from db_accessor import *


class Transfer(object):
    def __init__(self):
        self.dbt = get_transfers_db()

    def on_post(self, req, resp):
        user = req.get_header('Authorization')
        data = json.loads(req.stream.read())
        action = data['action']
        if action == 'begin':
            other_user = data['user']
            id = self.dbt.add_transfer(user, other_user)
            if id != -1:
                status = 'success'
            else:
                status = 'failure'
            resp.body = '{"status": "%s", "id": "%s"}' % (status, id)

        elif action == 'not_pass':
            id_connection = data['id']
            if self.dbt.transfer_not_pass(id_connection):
                status = 'success'
            else:
                status = 'failure'
            resp.body = '{"status": "%s"}' % status

        elif action == 'end':
            id_connection = data['id']
            if self.dbt.end_transfer(id_connection):
                status = 'success'
            else:
                status = 'failure'
            resp.body = '{"status": "%s"}' % status

        else:
            status = 'failure'
            resp.body = '{"status": "%s"}' % status

        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201

    def on_get(self, req, resp):
        user = req.get_header('Authorization')
        incoming = self.dbt.get_incoming_transfers(user)
        outgoing = self.dbt.get_outgoing_transfers(user)
        not_pass = self.dbt.get_transfers_not_pass(user)
        resp.body = json.dumps({"incoming": incoming, "outgoing": outgoing, "not_pass": not_pass})
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
            if ip:
                resp.body = json.dumps({'ip': ip})
            else:
                resp.body = json.dumps({'ip': 'failure'})
        else:
            resp.body = json.dumps({'ip': 'failure'})

        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201
