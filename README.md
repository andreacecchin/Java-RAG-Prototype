<h1 align="center">Java Retrieval-Augmented Generation Prototype</h1>

<p align="center">
    <img src="asset/logo.png" style="width:200px;height:auto">
</p>

<p align="center">
  Progetto di stage presso Sync Lab S.r.l.
    <br>
    <br>
  Retrieval-Augmented Generation con LangChain4J
    <br>
  Large Language Model Phi-3 mini, GPT 3.5 Turbo, Gemini 1.5 Pro e PaLM 2 Bison
    <br>
  Sviluppato con Java Spring
</p>
  
<p align="center">
    <img src="asset/technology.png" style="width:400px;height:auto">
</p>

<h3 align="center">Come testare</h3>

<p>
    Dopo aver avviato l'applicazione, utilizzare Postman o simili per accedere agli endpoint per:
    <br><br>
    Visualizzare i documenti in database, su cui poter fare domande:
    <br>
    GET localhost:8080/api/documents
    <br>
    Nessun parametro richiesto.
    <br><br>
    Porgere una domanda:
    <br>
    GET localhost:8080/api/ask
    <br>
    Parametro testuale: domanda.
    <br><br>
    Rimuovere un documento:
    <br>
    DELETE localhost:8080/api/documents
    <br>
    Parametro testuale: nome documento da rimuovre (nome.pdf)
    <br><br>
    Aggiungere un documento (già presente in /resources):
    <br>
    POST localhost:8080/api/documents
    <br>
    Parametro testuale: nome documento da aggiungere (nome.pdf)
</p>
