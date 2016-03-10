import falcon
import json
from db_accessor import *


class Friendship(object):
    def __init__(self):
        self.db = get_friend_request_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = req.get_header('Authorization')
        reply = userdata['reply']
        u = User()

        if self.db.send_friend_request(sender, reply):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"sender": "%s", "reply":"%s", "status": "%s"}' % (sender, reply, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200

    def on_get(self, req, resp):
        reply = req.get_header('Authorization')
        f = Friendship()
        friends = f.get_friends_list(reply)
        resp.body = json.dumps(friends)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class FriendshipResponse(object):
    def __init__(self):
        self.db = get_friend_request_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = req.get_header('Authorization')
        u = User()
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
        #userdata = json.loads(req.stream.read())
        reply = req.get_header('Authorization')
        waiting = self.db.check_waiting_request(reply)
        resp.body = json.dumps(waiting)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class SwitchIP(object):
    def __init__(self):
        self.db = get_user_db()

    def on_get(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = req.get_header('Authorization')
        receiver = userdata['receiver']
        #request_ip = req.env['HTTP_X_FORWARDED_FOR']
        #if the current request come from the sender client
        #if self.db.get_user_ip(sender) == request_ip:
        ip = self.db.get_user_ip(self.db.get_token_by_username(receiver))
        resp.body = json.dumps({'msg': ip})
        '''
        # if the current request come from the receiver client
        elif self.db.get_user_ip(receiver) == request_ip:
            user_ip = self.db.get_user_ip(receiver)
            #check if the ip in the DB is updated
            if user_ip == request_ip:
                self.db.set_user_ip(receiver, request_ip)
                resp.body = json.dumps({'msg': 'ip updated'})
            else:
                resp.body = json.dumps({'msg': 'ip no change'})
        else:
            resp.body = json.dumps({'msg': 'WTF'})
            '''
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200