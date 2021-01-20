----------------------------------------------------------------------
Mail protocols 6. (15p) An extra feature on the above applications,
for example your own choice...

"When adding several protocols to the server, you can use socket
timeouts to loop throuh the different ports to listen and handle one
protocol/port at a time and then move to check next protocol. So there
is no need to make the server listen to the ports at the same
time. This could be done with concurrent programming, but would be too
complicated (in correspondance to the amount of points you get from
the task). Problems would rise when different threads try to access
the 'inbox' at the same time. The solutions to that are complicated,
but if you make the server threaded, you can get extra points."

"As solution to this assigment: Describe what feature(s) you have
implemented. The maximum for this assignment is 15 points. For
example, if you have graphical interface on all (or just one)
applications, that is 5 points, if you have SSL/TLS on all (or just
one) clients that is 5 points, etc."

Ohjelma suunniteltiin heti alussa toimivan rinnakkaisesti säikeiden
avulla. Hakemisto `src/thread` sisältää protokollista riippumattomat
kantaluokat, jotka tarjoavat rajapinnat UDP- tai TCP-sokettien
käsittelyyn. Kantaluokat voivat muuttaa tilaansa dynaamisesti
ajoaikana, esimerkiksi sitä kuinka vastaanotetut paketit käsitellään.

Pääohjelma ainoastaan käynnistää palvelimet, asiakkaat ja
käyttöliittymän. Tekstikäyttöliittymä suoritetaan erillisessä
säikeessä, jotta käyttäjän syöttämät komennot eivät estäisi asiakasta
kuuntelemasta sokettiaan. Niinpä ohjelma ei pääse jumittumaan edes
virhetilanteissa. Sovelluksen arkkitehtuurin suunnitteluun käytettiin
runsaasti aikaa, minkä ansiosta protokollien lisääminen tapahtui
myöhemmin melko vaivattomasti.
----------------------------------------------------------------------
Your own choice of protocol 5. OR work on a single protocol for about
30 hours (30 points). Keep track of time you use on studying and
implementing the protocol and report the time used in the comments of
your software.

HTTP/2 protokollan toteuttaminen keskeytettiin, sillä ei löytynyt 6h
aikana sopivaa testipalvelinta, joka olisi tukenut HTTP/2-pyyntöä
selväkielisenä eli h2c. Esimerkiksi testityökalun
`tools.keycdn.com/http2-test` mukaan `www.example.com` tukisi HTTP/2
protokollaa, mutta pakettikaappauksen`http2.pcap` perusteella vastaus
on virheellinen. 101 sijaan saatiin 200. Ongelman voi toistaa
linux-komennolla `curl --http2 www.example.com`. RFC-dokumentin
mukaan:

"A server that does not support HTTP/2 can respond to the request as
though the Upgrade header field were absent:"

     HTTP/1.1 200 OK
     Content-Length: 243
     Content-Type: text/html

"A server that supports HTTP/2 accepts the upgrade with a 101
(Switching Protocols) response.  After the empty line that terminates
the 101 response, the server can begin sending HTTP/2 frames.  These
frames MUST include a response to the request that initiated the
upgrade."

     HTTP/1.1 101 Switching Protocols
     Connection: Upgrade
     Upgrade: h2c
----------------------------------------------------------------------
