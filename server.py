import falcon
import login

api = application = falcon.API()

login_res = login.Login()
register_res = login.Register()

api.add_route('/api/login', login_res)
api.add_route('/api/register', register_res)
