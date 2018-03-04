NAME ?= main

all: $(NAME).pdf

$(NAME).pdf: $(NAME).tex
	@echo $@
	latexmk -pdflatex=xelatex -pdf --shell-escape $<

clean:
	latexmk -CA
	@rm -vf *.{aux,log,nav,out,pdf,snm,toc,sta}

.PHONY: clean
