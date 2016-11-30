# puzzle-09-2016

<a href="https://frontend-sample.herokuapp.com/"><s>REST API</s></a>

И при успехе, и при обработанной ошибке возвращается 200 и тело, примерно такое: {"code":0,"content":{"login":"q"}}.
Если будет ошибка, то в content записывается сообщение.
Енум с кодами и сообщениями <a href="https://github.com/djkah11/puzzle-09-2016/blob/master/src/main/java/ru/mail/park/main/ResponseCode.java">тут</a>.

Игрок подключается к сокету ws://rainbow-square-backend.herokuapp.com/game (сначала надо залогиниться). По сокету посылаются сообщения вида {type, content}. type совпадает с именем класса, в content должен быть json с объектом этого класса. Возможные варианты:

Присоединение к игре:<br>
type: "GameJoin"<br>
content пустой<br>

Действие игрока:<br>
type: "PlayerAction"<br>
content:<br>
&nbsp;&nbsp;&nbsp;&nbsp;int row;<br>
&nbsp;&nbsp;&nbsp;&nbsp;int col;<br>
&nbsp;&nbsp;&nbsp;&nbsp;boolean positive;<br>

Снимок сервера:<br>
type: "ServerSnap"<br>
content:<br>
&nbsp;&nbsp;&nbsp;&nbsp;String player;<br>
&nbsp;&nbsp;&nbsp;&nbsp;String opponent;<br>
&nbsp;&nbsp;&nbsp;&nbsp;int[][] playerMatrix;<br>
&nbsp;&nbsp;&nbsp;&nbsp;int[][] opponentMatrix;<br>
&nbsp;&nbsp;&nbsp;&nbsp;int[][] target;<br>
&nbsp;&nbsp;&nbsp;&nbsp;boolean gameOver;<br>
&nbsp;&nbsp;&nbsp;&nbsp;boolean win;<br>
