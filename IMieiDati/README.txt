====================IMieiDati====================

COMANDI MAVEN SUPPORTATI

mvn compile -P<nome profilo>
produce il compilato java server side

mvn package -P<nome profilo>
produce il compilato gwt ed il war di uscita

mvn integration-test -P<nome profilo> -DskipTests=true
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




====Deploy in ambiente liferay====


- Collegarsi da /fascicolo/web/fascicoloadmin...

-- Control Panel -- (Portal) Site Templates
  Scegliere FascicoloPrivateSite e cliccare  Actions - View Pages
   Apparirà la pagina privata iniziale (con la vecchia portlet)
  - togliere la vecchia portlet

- da qui, fare Manage Pages - Site Pages - Add Page
  nome:  i-miei-dati  e Template (lasciare vuoto)
- Scegliere il look-and-feel  FascicoloPrivate  
- Fare Save

 Per visualizzare la pagina:
- dall'indirizzo scritto nel browser  (es: localhost:8080/fascicolo/group/template-11868/home )
   togliere /home e mettere /i-miei-dati
- Dopo cliccare  Manage - Page Layout -- scegliere FascicoloPrivate-1colxs12
- Fare Save

Per aggiungere la portlet:
 da qui, cliccare  Add - More.. - fascicolo - I_Miei_Dati_Portlet
- Su Options (della portlet) - Look And Feel -
  - scegliere Show Border: NO   
  - fare Save

Per visualizzare la pagina:
- collegarsi da .../fascicolo/c/portal/login e, una volta loggati,
  dall'url che apparirà sul browser, togliere  /home.. e sostituire /i-miei-dati
