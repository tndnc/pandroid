from PyQt5.QtWidgets import (
	QWidget, 
	QPushButton, 
	QHBoxLayout,
	QVBoxLayout,
	QSlider,
	QGridLayout,
	QLabel
)
from PyQt5.QtCore import pyqtSlot, Qt
from package.modules.generation import generate_random_instance
from package.modules.backtrack_solve import solve_by_backtrack
from package.modules.heuristics import h1
from package.ui.widgets import InstanceView

class MainWindow(QWidget):

	def __init__(self):
		super().__init__()
		self.resize(600, 350)
		self.setWindowTitle("LEF Solver / Analysis tool")

		self.leftPane = self.createLeftPane()
		self.rightPane = self.createRightPane()
		app_layout = QGridLayout()
		app_layout.addWidget(self.leftPane)
		app_layout.addWidget(self.rightPane)
		self.setLayout(app_layout)

		# init app variables
		self.nb_actors = 3
		self.generate_new_instance()

		self.show()

	def createLeftPane(self):
		widget = QWidget()
		layout = QVBoxLayout()

		self.instanceView = InstanceView()
		layout.addWidget(self.instanceView)

		widget.setLayout(layout)
		return widget

	def createRightPane(self):
		widget = QWidget()
		layout = QVBoxLayout()

		gbutton = QPushButton('Generate new instance', self)
		gbutton.clicked.connect(self.generate_new_instance)
		layout.addWidget(gbutton)

		slider = QSlider(Qt.Horizontal, self)
		slider.setMinimum(3)
		slider.setMaximum(7)
		slider.setTickInterval(1)
		slider.valueChanged.connect(lambda: self.__setattr__('nb_actors', slider.value()))
		layout.addWidget(slider)

		self.niterLabel = QLabel()
		self.niterLabel.setText("Number of iterations: 0")
		layout.addWidget(self.niterLabel)

		self.heuristicValueLabel1 = QLabel()
		self.heuristicValueLabel1.setText("h1 = Infeasible")
		layout.addWidget(self.heuristicValueLabel1)

		widget.setLayout(layout)
		return widget

	# Slots
	@pyqtSlot()
	def generate_new_instance(self):
		self.instance = generate_random_instance(self.nb_actors)
		alloc, niter, hotspots = solve_by_backtrack(self.instance)
		self.instanceView.setInstance(self.instance, allocation=alloc)
		self.niterLabel.setText("Number of iterations: {}".format(niter))
		if alloc:
			h = h1(self.instance, alloc, niter)
			self.heuristicValueLabel1.setText("h1 = {}".format(h))
		else:
			self.heuristicValueLabel1.setText("h1 = Infeasible")
