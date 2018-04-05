from PyQt5.QtWidgets import QWidget, QGridLayout, QLabel
from PyQt5.QtCore import Qt

class InstanceView(QWidget):

	def __init__(self):
		super().__init__()
		self.layout = QGridLayout()

	def setInstance(self, instance, allocation=None):
		if not allocation: allocation = dict()
		# delete layout
		QWidget().setLayout(self.layout)
		# populate new layout
		self.layout = QGridLayout()
		n = len(instance)

		for i in range(n):
			for j in range(n):
				label = QLabel()
				obj = instance[j][i]
				label.setText(str(obj))
				if obj == allocation.get(j):
					label.setStyleSheet('background-color: #FEE84F;')
				label.setAlignment(Qt.AlignCenter)
				self.layout.addWidget(label, i, j)

		self.setLayout(self.layout)


class AllSolutionsView(QWidget):

	def __init__(self, instances):
		super().__init__()
		self.layout = QGridLayout()
		self.resize(500, 500)

		MAX_PER_ROW = 4

		for i in range(len(instances)):
			instance = instances[i]['instance']
			alloc = instances[i]['alloc']
			instanceview = InstanceView()
			instanceview.setInstance(instance, alloc)
			y = i // MAX_PER_ROW
			x = i % MAX_PER_ROW
			self.layout.addWidget(instanceview, y, x)

		self.setLayout(self.layout)
		self.show()