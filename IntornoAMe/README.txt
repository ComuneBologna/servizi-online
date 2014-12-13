====================EmptyApp====================

COMANDI MAVEN SUPPORTATI

mvn compile -P <nome profilo>
produce il compilato java server side

mvn package -P <nome profilo>
produce il compilato gwt ed il war di uscita

mvn integration-test -P <nome profilo> -DskipTests=true
installa l'artefatto war nella cartella di hot deploy del server locale di sviluppo

mvn clean
elimina tutte le risorse compilate. da eseguire prima di ogni commit

Esecuzione in debug di un test da eclipse:
test -Dtest=<nome classe del test> -DforkCount=0

GESTIONE DEI PROFILI
Ogni sviluppatore deve:
-creare una cartella svilXY (X iniziale nome, Y iniziale cognome) in src/main/resources
in cui inserire le risorse di sviluppo (Es log4j.xml).
-aggiornare il pom.xml per aggiungere un profilo svilXY
-creare un file src/main/filters/svilXY.properties in cui inserire le proprie configurazioni di ambiente (es liferay deploy dir) 



NOTE
richiede dependency ojdbc6, da installare in repo locale con
mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.3 -Dpackaging=jar -DgeneratePom=true


ERRORI JAVASCRIPT
I file jquery-min danno errore di validazione in eclipse, a causa di un bug. Al fine di non visualizzarlo:
-aprire properties del progetto
-javascript include path source tab
-selezionare exlude e add
-selezionare i file min.js