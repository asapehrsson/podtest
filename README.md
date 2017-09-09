# Hemuppgift Apputveckling

Följande hemuppgift är till för att vara en del av bedömningsunderlaget vid rekrytering av utvecklare till Sveriges radio. Tanken är att den skall gå att genomföra på några kvällar och vi förväntar oss därför inte perfektion. 

## Bygg din egen dokumentärspelare
Vi vill att du bygger en enkel poddradio app enligt nedanstående specifikation. Poddradiospelaren ska använda sig av minst två flöden med Sveriges Radios dokumentärer. Du får gärna använda vårt api som är beskrivet på http://sverigesradio.se/api/ 

### Tekniska krav
- Appen skall vara byggd med nativekod.
- Appen med komplett källkod och resursfiler skall kunna packas ihop till en zip fil och köras på en annan dator.

### Användarfall
- Som en användare vill jag gå in på en startsida där jag har överblick över vilka avsnitt som finns. 
- Som en användare skall jag kunna få mer information om ett valt program.
- Som en användare vill jag kunna lyssna på ett enskilt avsnitt.
- Som en utvecklare vill jag kunna vidareutveckla koden med testdriven utveckling

## Resultat

Har utgått från hypotesen en PoC med “få klick till uppspelning” och minsta mängd funktioner för att kunna utvärdera. 
- Ett flöde med pod’ar
- Metadata: namn och beskrivning
- Starta uppspelning, pausa uppspelning och stoppa uppspelning

Timeboxat till max 8 timmar. Prioriterade uppspelning framför två flöden.
### Skisser på UI

#### Visa info
[Från start, visa info](images/to_info.jpg)

När appen startar laddas episoder allteftersom man skrollar

Mer info om episoden visas vid klick på (i) 

#### Starta uppspelning
[Från start, starta uppspelning](images/to_miniplayer.jpg)

Uppspelningen startar vid klick varsomhelst på list-elementet (utom (i))

Metadata om det som spelas upp samt spelarkontroller play/pause visas i en minispelare 

Uppspelningen avbryts genom att swipa bort minispelaren
 
Att göra senare
- Visa progress vid uppspelning
- Komma ihåg vad som setts
- Återuppta uppspelning
- Spelare i helskärmsläge
- Kunna skrubba
- Spela i bakgrunden 

### Utforskar /api.sr.se
http://sverigesradio.se/api/documentation/v2/index.html
 
Utforskade API mha Advanced REST client och curl.
 
Slutsats: kommer att använda den här URL’en i appen:

```
curl -v http://api.sr.se/api/v2/episodes/index?programid=2519&fromdate=2017-01-01&todate=2017-06-01&urltemplateid=3&audioquality=hi&format=json
```

Genererar klasser mha http://www.jsonschema2pojo.org/

_Styra format mha Accept: application/json" verkar inte funka_

### Bygger appen
Skapar projekt, väljer min version utifrån Google Play services (4.0) / Exoplayer (4.1) (trots att inte Exoplayer används i PoC) 
 
Att göra senare
- Ta bort hårdkodning av episod URL
- Invalidera och ladda om data på ett ansvarsfullt sätt
- Ladda pod i egen tråd, visa minispelare direkt med spinner
- Felhantering, t.ex vid offline. 
- Tillgänglighet
- Extrahera styles, bla för layout
