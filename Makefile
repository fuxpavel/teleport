all: server test clean

server:
	@echo "\nSetting up the server\n"
	@gunicorn server &
	@sleep 1

test:
	@echo "\n\n\nRunning test:\n"
	@python test.py
	@sleep 1

clean:
	@echo "\n\n\nShutting down the server\n"
	@pkill gunicorn
	@sleep 1
