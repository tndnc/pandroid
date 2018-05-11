from PyQt5.QtWidgets import (
	QMainWindow,
	QWidget, 
	QPushButton, 
	QHBoxLayout,
	QVBoxLayout,
	QSlider,
	QGridLayout,
	QLabel,
	QShortcut,
)
from PyQt5.QtGui import QKeySequence
from PyQt5.QtCore import pyqtSlot, Qt
from package.modules.generation import generate_random_instance, generate_solvable
from package.modules.backtrack_solve import compute_optimal_solutions
from package.modules.asp_solve import solve as asp_solve
from package.ui.widgets import InstanceView, AllSolutionsView
from package.modules.export import export_toLATEX, export_toXML
from package.modules.utils import pprint_metadata
from package.modules.analysis import *

class MainWindow(QMainWindow):

	def __init__(self, app):
		super().__init__()
		self.resize(600, 500)
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

		# init shortcuts
		self.gen_shortcut = QShortcut(QKeySequence('Ctrl+R'), self)
		self.gen_shortcut.activated.connect(self.generate_new_instance)
		self.inc_shortcut = QShortcut(QKeySequence('Ctrl+Up'), self)
		self.inc_shortcut.activated.connect(lambda: self.set_nbActors(min(self.nb_actors + 1, 10)))
		self.dec_shortcut = QShortcut(QKeySequence('Ctrl+Down'), self)
		self.dec_shortcut.activated.connect(lambda: self.set_nbActors(max(self.nb_actors - 1, 3)))
		self.prev_shortcut = QShortcut(QKeySequence('Ctrl+Left'), self)
		self.prev_shortcut.activated.connect(self.prev_solution)
		self.next_shortcut = QShortcut(QKeySequence('Ctrl+Right'), self)
		self.next_shortcut.activated.connect(self.next_solution)

		# init app variables
		self.set_nbActors(4)
		self.generate_new_instance()

		self.show()

	def createFirstPane(self):
		widget = QWidget()
		layout = QVBoxLayout()

		self.instanceView = InstanceView()
		layout.addWidget(self.instanceView)

		widget.setLayout(layout)
		return widget

	def createSecondPane(self):
		widget = QWidget()
		layout = QGridLayout()
		layout.setColumnStretch(0, 1)
		layout.setColumnStretch(1, 2)

		left_layout = QVBoxLayout()
		left_widget = QWidget()
		left_widget.setLayout(left_layout)

		layout.addWidget(left_widget, 0, 0)

		pager_layout = QHBoxLayout()
		self.prev_button = QPushButton('<', self)
		self.prev_button.clicked.connect(self.prev_solution)
		self.next_button = QPushButton('>', self)
		self.next_button.clicked.connect(self.next_solution)
		pager_layout.addWidget(self.prev_button)
		pager_layout.addWidget(self.next_button)
		pager_widget = QWidget()
		pager_widget.setLayout(pager_layout)
		left_layout.addWidget(pager_widget)

		self.nbActorsLabel = QLabel()
		self.nbActorsLabel.setAlignment(Qt.AlignCenter)
		left_layout.addWidget(self.nbActorsLabel)

		self.slider = QSlider(Qt.Horizontal, self)
		self.slider.setMinimum(3)
		self.slider.setMaximum(10)
		self.slider.setTickInterval(1)
		self.slider.valueChanged.connect(lambda: self.set_nbActors(self.slider.value()))
		left_layout.addWidget(self.slider)

		gbutton = QPushButton('Generate new instance', self)
		gbutton.clicked.connect(self.generate_new_instance)
		left_layout.addWidget(gbutton)

		graph_button = QPushButton('Show graph', self)
		graph_button.clicked.connect(lambda: show_optima_graph(self.metadata['optima_graph']))
		left_layout.addWidget(graph_button)

		# solbutton = QPushButton('Show all solutions', self)
		# solbutton.clicked.connect(self.show_all_solutions)
		# layout.addWidget(solbutton, 3, 0)

		# exportLATEXbutton = QPushButton('Export to LATEX', self)
		# exportLATEXbutton.clicked.connect(self.export_to_clipboard_LATEX)
		# layout.addWidget(exportLATEXbutton, 3, 0)

		exportXMLbtn = QPushButton('Export to XML', self)
		exportXMLbtn.clicked.connect(self.export_to_clipboard_XML)
		left_layout.addWidget(exportXMLbtn)

		exportMetaBtn = QPushButton('Export metadata', self)
		exportMetaBtn.clicked.connect(self.export_to_clipboard_metadata)
		left_layout.addWidget(exportMetaBtn)


		self.metaLabel = QLabel()
		layout.addWidget(self.metaLabel, 0, 1)
		self.metaLabel.setAlignment(Qt.AlignTop)

		# self.heuristicValueLabel1 = QLabel()
		# self.heuristicValueLabel1.setText("")
		# layout.addWidget(self.heuristicValueLabel1, 2, 1)

		# hotbutton = QPushButton('Show heatmap', self)
		# hotbutton.clicked.connect(self.show_heatmap)
		# layout.addWidget(hotbutton, 3, 1)

		widget.setLayout(layout)
		return widget

	# Slots
	@pyqtSlot()
	def generate_new_instance(self):
		self.instance = generate_solvable(self.nb_actors)
		self.solutions = asp_solve(instance=self.instance)
		wpos, stats = compute_optimal_solutions(self.instance)
		
		self.metadata = compute_metadata(self.instance, 
			self.solutions, wpos, stats)
		self.metaLabel.setText(pprint_metadata(self.metadata))
		# show_optima_graph(self.solutions)
		# show first solutions in mainwindow
		self.set_solution_view(0)

	def set_solution_view(self, sol_idx):
		self.sol_idx = sol_idx
		self.instanceView.setInstance(self.instance, allocation=self.solutions[sol_idx])
		self.next_button.setEnabled(sol_idx < len(self.solutions)-1)
		self.prev_button.setEnabled(sol_idx > 0)

	@pyqtSlot()
	def prev_solution(self):
		self.sol_idx = max(self.sol_idx-1, 0)
		self.set_solution_view(self.sol_idx)

	@pyqtSlot()
	def next_solution(self):
		self.sol_idx = min(self.sol_idx+1, len(self.solutions)-1)
		self.set_solution_view(self.sol_idx)

	def set_nbActors(self, value):
		self.nb_actors = value
		self.slider.setValue(value)
		self.nbActorsLabel.setText("Number of actors: {}".format(value))

	# @pyqtSlot()
	# def export_to_clipboard_LATEX(self):
	# 	latexstr = export_toLATEX(instance=self.instance, 
	# 		allocation=self.solutions[0], niter=self.metadata[0]['niter'], h=self.h, env='subfigure')
	# 	self.clipboard.setText(latexstr)
	# 	self.statusBar().showMessage("Exported to clipboard")

	@pyqtSlot()
	def export_to_clipboard_XML(self):
		xmlstr = export_toXML(instance=self.instance)
		self.clipboard.setText(xmlstr)
		self.statusBar().showMessage("Exported to clipboard")

	@pyqtSlot()
	def export_to_clipboard_metadata(self):
		ordered_values = [self.metadata[k] for k in ('number_of_wpos', 
			'number_of_solutions', 'average_niter', 'number_of_frozen_variables',
			'attraction_basin_size', 'mean_regret', 'ext_regret', 'mean_regret_wpos',
			'average_number_of_possible_position')]
		string = ",".join(map(str, ordered_values))
		self.clipboard.setText(string)
		self.statusBar().showMessage("Metadata exported")
