import falcon
import json
from db_accessor import *


class Friendship(object):
    def __init__(self):
        self.db = get_friend_request_db()
        self.dbf  = get_friendship_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = req.get_header('Authorization')
        reply = userdata['reply']

        if self.db.send_friend_request(sender, reply):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"sender": "%s", "reply":"%s", "status": "%s"}' % (sender, reply, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200

    def on_get(self, req, resp):
        reply = req.get_header('Authorization')
        friends = self.dbf.get_friends_list(reply)
        resp.body = json.dumps(friends)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class FriendshipResponse(object):
    def __init__(self):
        self.db = get_friend_request_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = req.get_header('Authorization')
        reply = userdata['reply']
        request_status = userdata['status']

        if request_status == 'confirm':
            if self.db.confirm_request(sender, reply):
                status = 'success'
            else:
                status = 'failure'

        elif request_status == 'denial':
            if self.db.denial_request(sender, reply):
                status = 'success'
            else:
                status = 'failure'
        else:
            status = 'failure'

        resp.body = '{"sender": "%s", "reply":"%s", "status": "%s"}' % (sender, reply, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200

    def on_get(self, req, resp):
        reply = req.get_header('Authorization')
        waiting = self.db.check_waiting_request(reply)
        resp.body = json.dumps(waiting)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class SwitchIP(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        receiver = req.get_header('Authorization')
        sender = userdata['sender']
        ip = self.db.get_user_ip(self.db.get_token_by_username(sender))
        resp.body = json.dumps({'ip': ip})
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200
