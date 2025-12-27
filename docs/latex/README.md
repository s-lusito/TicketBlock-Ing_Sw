# Documentazione LaTeX - TicketBlock

Questa directory contiene la relazione tecnica completa del progetto TicketBlock in formato LaTeX.

## File Principali

- `relazione-tecnica.tex` - Documento principale della relazione tecnica
- `Makefile` - Script per compilare il documento
- `README.md` - Questo file

## Compilazione

### Requisiti

Per compilare il documento è necessario avere installato:
- LaTeX (TeX Live, MiKTeX, o MacTeX)
- pdflatex
- Pacchetti LaTeX richiesti: babel, graphicx, hyperref, listings, xcolor, geometry, float, tikz

### Comando di Compilazione

```bash
# Compilazione semplice
pdflatex relazione-tecnica.tex

# Compilazione completa (con bibliografia e indice)
pdflatex relazione-tecnica.tex
pdflatex relazione-tecnica.tex

# Oppure usando make
make
```

### Pulizia File Temporanei

```bash
make clean
```

## Contenuto della Relazione

La relazione tecnica è organizzata nelle seguenti sezioni:

1. **Introduzione**
   - Ambito applicativo
   - Obiettivo del progetto
   - Problema affrontato
   - Soluzione proposta
   - Confronto con soluzioni esistenti

2. **Stato dell'Arte**
   - Blockchain nel ticketing
   - Lavori correlati
   - Contesto specifico del progetto

3. **Modello di Processo e Valutazioni Iniziali**
   - Modello Agile Scrum adottato
   - Diagramma di Gantt
   - Analisi dei rischi
   - Stima dei costi

4. **Metodo Proposto e Requisiti**
   - Requisiti funzionali
   - Requisiti non funzionali

5. **Architettura e Tech Stack**
   - Architettura a tre livelli
   - Stack tecnologico completo
   - Design patterns utilizzati

6. **Prototipo**
   - Diagramma delle classi UML
   - Casi d'uso principali
   - Descrizione frontend

7. **Validazione e Verifica**
   - Test unitari
   - Test di integrazione
   - Test smart contracts
   - Metriche di qualità

8. **Discussione**
   - Sfide tecniche affrontate
   - Decisioni architetturali
   - Limitazioni attuali

9. **Conclusioni e Sviluppi Futuri**
   - Risultati raggiunti
   - Contributi principali
   - Roadmap futura

10. **Bibliografia**
    - Riferimenti scientifici e tecnici

11. **Allegati**
    - Diagrammi UML
    - Struttura del codice
    - Test documentati

## Note

- La relazione è scritta in **italiano** come richiesto
- Utilizza placeholder `[FRONTEND]` per le parti relative al frontend Vue.js non ancora completato
- Include diagrammi TikZ per visualizzazioni grafiche
- Riferimenti ai diagrammi Mermaid presenti nella directory `docs/`

## Output

Il comando di compilazione genera:
- `relazione-tecnica.pdf` - Documento PDF finale
- File temporanei (.aux, .log, .toc, etc.)

## Personalizzazione

Per aggiornare la relazione:
1. Modificare il file `relazione-tecnica.tex`
2. Ricompilare con `pdflatex` o `make`
3. Verificare il PDF generato

## Integrazione con Presentazione

Questa relazione tecnica può essere utilizzata come base per la presentazione richiesta. I contenuti possono essere estratti e adattati per slide.
