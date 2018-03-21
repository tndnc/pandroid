from PyQt5.QtWidgets import (
	QMainWindow,
	QWidget, 
	QPushButton, 
	QHBoxLayout,
	QVBoxLayout,
	QSlider,
	QGridLayout,
	QLabel
)
from PyQt5.QtCore import pyqtSlot, Qt
from package.modules.generation import generate_random_instance, generate_solvable
from package.modules.backtrack_solve import backtrack_from_agent, compute_optimal_solutions
from package.modules.heuristics import h1
from package.ui.widgets import InstanceView, AllSolutionsView
from package.modules.export import export_toLATEX, export_toXML

class MainWindow(QMainWindow):

	def __init__(self, app):
		super().__init__()
		self.resize(600, 450)
		self.setWindowTitle("LEF Solver / Analysis tool")
		self.clipboard = app.clipboard()

		self.firstPane = self.createFirstPane()
		self.secondPane = self.createSecondPane()
		self.centralWidget = QWidget()
		app_layout = QGridLayout()
		app_layout.setRowStretch(0, 5)
		app_layout.addWidget(self.firstPane)
		app_layout.addWidget(self.secondPane)
		self.centralWidget.setLayout(app_layout)
		self.setCentralWidget(self.centralWidget)

		# init app variables
		self.set_nbActors(3)
		self.generate_new_instance()

		self.show()

	def createFirstPane(self):
		widget = QWidget()
		layout = QVBoxLayout()

		self.instanceView = InstanceView()
		# self.instanceView.resize(550, 400)
		# self.instanceView.setStyleSheet('background-color: red;')
		layout.addWidget(self.instanceView)

		widget.setLayout(layout)
		return widget

	def createSecondPane(self):
		widget = QWidget()
		layout = QGridLayout()
		layout.setColumnStretch(0, 1)
		layout.setColumnStretch(1, 2)

		slider = QSlider(Qt.Horizontal, self)
		slider.setMinimum(3)
		slider.setMaximum(10)
		slider.setTickInterval(1)
		slider.valueChanged.connect(lambda: self.set_nbActors(slider.value()))
		layout.addWidget(slider, 0, 0)

		gbutton = QPushButton('Generate new instance', self)
		gbutton.clicked.connect(self.generate_new_instance)
		layout.addWidget(gbutton, 1, 0)

		solbutton = QPushButton('Show all solutions', self)
		solbutton.clicked.connect(self.show_all_solutions)
		layout.addWidget(solbutton, 2, 0)

		exportLATEXbutton = QPushButton('Export to LATEX', self)
		exportLATEXbutton.clicked.connect(self.export_to_clipboard_LATEX)
		layout.addWidget(exportLATEXbutton, 3, 0)

		exportXMLbtn = QPushButton('Export to XML', self)
		exportXMLbtn.clicked.connect(self.export_to_clipboard_XML)
		layout.addWidget(exportXMLbtn, 4, 0)

		self.nbActorsLabel = QLabel()
		self.nbActorsLabel.setText("Number of actors: 3")
		layout.addWidget(self.nbActorsLabel, 0, 1)

		self.niterLabel = QLabel()
		self.niterLabel.setText("Number of iterations: 0")
		layout.addWidget(self.niterLabel, 1, 1)

		self.heuristicValueLabel1 = QLabel()
		self.heuristicValueLabel1.setText("h1 = Infeasible")
		layout.addWidget(self.heuristicValueLabel1, 2, 1)

		hotbutton = QPushButton('Show heatmap', self)
		hotbutton.clicked.connect(self.show_heatmap)
		layout.addWidget(hotbutton, 3, 1)

		widget.setLayout(layout)
		return widget

	# Slots
	@pyqtSlot()
	def generate_new_instance(self):
		# self.instance = generate_random_instance(self.nb_actors)
		self.instance = generate_solvable(self.nb_actors)
		self.solutions, self.metadata = compute_optimal_solutions(self.instance)
		# show first solutions in mainwindow
		sol = self.solutions[0] if len(self.solutions) > 0 else None
		niter = self.metadata[0]['niter'] if len(self.solutions) > 0 else None
		self.instanceView.setInstance(self.instance, allocation=sol)
		self.niterLabel.setText("Number of iterations: {}".format(niter))

		if len(self.solutions) > 0:
			self.h = h1(self.instance, self.solutions, self.metadata)
			self.heuristicValueLabel1.setText("h1 = {}".format(self.h))
		else:
			self.heuristicValueLabel1.setText("h1 = Infeasible")

	@pyqtSlot()
	def set_nbActors(self, value):
		self.nb_actors = value
		self.nbActorsLabel.setText("Number of actors: {}".format(value))

	@pyqtSlot()
	def show_all_solutions(self):
		self.popup = AllSolutionsView([{'instance': self.instance, 'alloc': s} for s in self.solutions])

	@pyqtSlot()
	def export_to_clipboard_LATEX(self):
		latexstr = export_toLATEX(instance=self.instance, 
			allocation=self.solutions[0], niter=self.metadata[0]['niter'], h=self.h, env='subfigure')
		self.clipboard.setText(latexstr)
		print("exported to clipboard")

	@pyqtSlot()
	def export_to_clipboard_XML(self):
		xmlstr = export_toXML(instance=self.instance)
		self.clipboard.setText(xmlstr)
		print("exported to clipboard")

	@pyqtSlot()
	def show_heatmap(self):
		from package.modules.utils import show_average_heatmap
		hotspots_list = [m['hotspots'] for m in self.metadata]
		show_average_heatmap(hotspots_list, self.instance)