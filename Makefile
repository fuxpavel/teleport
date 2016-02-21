all: test

test:
	@echo "\nRunning test:\n"
	@python -m unittest discover
