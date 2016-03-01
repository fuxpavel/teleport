import falcon
import json
from db_accessor import get_friend_request_db

class Friendship(object):
    def __init__(self):
        self.db = get_friend_request_db()

    def on_post(self, req, resp):
        userdata = json.loads(req.stream.read())
        sender = userdata['sender']
        reply = userdata['reply']

        if self.db.send_friend_request(sender, reply):
            status = 'success'
        else:
            status = 'failure'

        resp.body = '{"sender": "%s","reply":"%s" ,"status": "%s"}' % (sender, reply, status)
        resp.content_type = 'application/json'
        resp.status = falcon.HTTP_200


