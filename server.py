import falcon
import login

api = application = falcon.API()

login_res = login.Login()
api.add_route('/login', login_res)
