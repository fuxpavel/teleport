all: server test shutdown

shutdown:
	@echo "\n\n\nShutting down the server\n"
	@pkill gunicorn
	@sleep 1
	@echo

test:
	@echo "\n\n\nRunning test:\n"
	@python test.py
	@sleep 1

server:
	@echo "\nSetting up the server\n"
	@gunicorn server &
	@sleep 1
