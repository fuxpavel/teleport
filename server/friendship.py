import json
import falcon
from db_accessor import *


class RemoveFriendship(object):
    def __init__(self):
        self.db = get_friendship_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        user = req.get_header('Authorization')
        # update ip address in DB
        ip = req.env['REMOTE_ADDR']
        if self.db.get_user_ip(user) != ip:
            self.db.set_user_ip(user, ip)

        remove = userdata['remove']
        if self.db.remove_friendship(user, remove):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"status": "%s"}' % status
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201


class Friendship(object):
    def __init__(self):
        self.db = get_friend_request_db()
        self.dbf = get_friendship_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = req.get_header('Authorization')
        # update ip address in DB
        ip = req.env['REMOTE_ADDR']
        if self.db.get_user_ip(sender) != ip:
            self.db.set_user_ip(sender, ip)

        reply = userdata['reply']

        if self.db.send_friend_request(sender, reply):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"status": "%s"}' % status
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201

    def on_get(self, req, resp):
        reply = req.get_header('Authorization')
        # update ip address in DB
        ip = req.env['REMOTE_ADDR']
        if self.db.get_user_ip(reply) != ip:
            self.db.set_user_ip(reply, ip)

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
        # update ip address in DB
        ip = req.env['REMOTE_ADDR']
        if self.db.get_user_ip(sender) != ip:
            self.db.set_user_ip(sender, ip)

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

        resp.body = '{"status": "%s"}' % status
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201

    def on_get(self, req, resp):
        reply = req.get_header('Authorization')
        # update ip address in DB
        ip = req.env['REMOTE_ADDR']
        if self.db.get_user_ip(reply) != ip:
            self.db.set_user_ip(reply, ip)

        incoming = self.db.check_incoming_request(reply)
        outgoing = self.db.check_outgoing_request(reply)
        resp.body = json.dumps({"incoming": incoming, "outgoing": outgoing})
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class Username(object):
    def __init__(self):
        self.db = get_user_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        reply = req.get_header('Authorization')
        # update ip address in DB
        ip = req.env['REMOTE_ADDR']
        if self.db.get_user_ip(reply) != ip:
            self.db.set_user_ip(reply, ip)

        usernames = self.db.username_like(userdata['name'], reply)
        resp.body = json.dumps(usernames)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_201
