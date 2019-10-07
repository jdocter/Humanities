# Humanities

A multiplayer text-protocol based game of Cards Against Humanities as defined by [this](https://www.wikihow.com/Play-Cards-Against-Humanity) article. A minimum of four players are required 

## Running the game server
One game server must be running to facilitate interaction between players. In the `Humanities/` directory run the game server:
```java -ea -cp bin humanities.GameServer PORT WHITE_CARDS BLACK_CARDS```
Where:
- `PORT` is an integer that specifies the server's listening port number
- `WHITE_CARDS` is the path to a text file containing the set of white cards used to play the game. Each line in the input file is intepreted as text corresponding to a unique white card.
- `BLACK_CARDS` is the path to a file containing the set of black cards used to play the game. Each line in the input file is intepreted as text corresponding to a unique black card. Black cards must one fill in space, i.e. they must match `(~_)*_+(~_)*`.

## Running the player client

