if __name__ == '__main__':
    import sys

    if len(sys.argv) > 1 and sys.argv[1] == '-g':
    	from package import app
    	sys.exit(app.run(sys.argv))
    else:
    	from package.modules.tests import *
    	# test_h1Distribution()
    	test_generation()