Comandi per collegare il progetto a git


cd C:\percorso\dove\vuoi\mettere\il\progetto
git init
git clone https://github.com/ORG/NOME-REPO.git
cd NOME-REPO

per creare un brench:
git status
git branch
git checkout -b feature/NOME_DI_CIò_CHE_VUOI_FARE //(esempio UC2)

QUANDO FINISCI DI FARE CIò CHE VUOI FARE
git add .
git commit -m "Descrizione breve e chiara"
git push -u origin feature/nome-cosa-stai-facendo


QUANDO DEVI RICOMINCIARE
git checkout main
git pull
git checkout feature/nome-cosa-stai-facendo
git merge main
