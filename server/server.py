import login
import friendship
import transfer
import falcon
from wsgiref import simple_server

api = application = falcon.API()

login_res = login.Login()
logout_res = login.Logout()
register_res = login.Register()
friendship_res = friendship.Friendship()
friendship_response_res = friendship.FriendshipResponse()
switch_ip_res = transfer.SwitchIP()
username_list_res = friendship.Username()
transfer_res = transfer.Transfer()

api.add_route('/api/login', login_res)
api.add_route('/api/logout', logout_res)
api.add_route('/api/register', register_res)
api.add_route('/api/friendship', friendship_res)
api.add_route('/api/friendship/response', friendship_response_res)
api.add_route('/api/switch-ip', switch_ip_res)
api.add_route('/api/username', username_list_res)
api.add_route('/api/transfer', transfer_res)

if __name__ == '__main__':
    httpd = simple_server.make_server('', 8000, api)
    httpd.serve_forever()
