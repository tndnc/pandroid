from PyQt5.QtWidgets import QApplication
from package.ui.mainwindow import MainWindow

def run(args):
	app = QApplication(args)
	window = MainWindow()
	app.exec_()