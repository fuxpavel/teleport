from Data_Base import User

db = User()
print db.register_user("ben", "123")
print db.login_user("ben", "123")
db.print_DB()