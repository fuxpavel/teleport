import falcon
import json
from db_accessor import get_friend_request_db
from werkzeug.wrappers import Request, Response

class Friendship(object):
    def __init__(self):
        self.db = get_friend_request_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())

        if req.path == '/api/friendship':
            sender = userdata['sender']
            reply = userdata['reply']

            if self.db.send_friend_request(sender, reply):
                status = 'success'
            else:
                status = 'failure'

            resp.body = '{"sender": "%s","reply":"%s" ,"status": "%s"}' % (sender, reply, status)

    def on_get(self, req, resp):
        userdata = json.loads(req.stream.read())
        reply = userdata['reply']

        waiting = self.db.check_waiting_request(reply)
        resp.body = json.dumps(waiting)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


class FriendshipResponse:

    def __init__(self):
        self.db = get_friend_request_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = userdata['sender']
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

        resp.body = '{"sender": "%s","reply":"%s" ,"status": "%s"}' % (sender, reply, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


