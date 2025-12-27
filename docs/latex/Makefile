# Makefile per compilare la relazione tecnica TicketBlock

# Nome del file principale (senza estensione)
MAIN = relazione-tecnica

# Compilatore LaTeX
LATEX = pdflatex

# Opzioni del compilatore
LATEX_FLAGS = -interaction=nonstopmode -halt-on-error

# Target di default
all: $(MAIN).pdf

# Compilazione del documento PDF
$(MAIN).pdf: $(MAIN).tex
	@echo "Compilazione documento LaTeX..."
	$(LATEX) $(LATEX_FLAGS) $(MAIN).tex
	@echo "Seconda passata per indice e riferimenti..."
	$(LATEX) $(LATEX_FLAGS) $(MAIN).tex
	@echo "Documento compilato con successo: $(MAIN).pdf"

# Pulizia dei file temporanei
clean:
	@echo "Pulizia file temporanei..."
	rm -f *.aux *.log *.toc *.out *.nav *.snm *.vrb *.bbl *.blg *.lot *.lof
	@echo "File temporanei rimossi."

# Pulizia completa (include PDF)
cleanall: clean
	@echo "Rimozione PDF..."
	rm -f $(MAIN).pdf
	@echo "Pulizia completa."

# Visualizza il PDF (su sistemi con evince, xdg-open, o open)
view: $(MAIN).pdf
	@if command -v evince > /dev/null 2>&1; then \
		evince $(MAIN).pdf & \
	elif command -v xdg-open > /dev/null 2>&1; then \
		xdg-open $(MAIN).pdf & \
	elif command -v open > /dev/null 2>&1; then \
		open $(MAIN).pdf & \
	else \
		echo "Nessun visualizzatore PDF trovato."; \
	fi

# Help
help:
	@echo "Target disponibili:"
	@echo "  all       - Compila il documento (default)"
	@echo "  clean     - Rimuove file temporanei"
	@echo "  cleanall  - Rimuove tutti i file generati (include PDF)"
	@echo "  view      - Apre il PDF con il visualizzatore di sistema"
	@echo "  help      - Mostra questo messaggio"

.PHONY: all clean cleanall view help
