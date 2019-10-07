# Humanities

A multiplayer text-protocol based game of Cards Against Humanities as defined by [this](https://www.wikihow.com/Play-Cards-Against-Humanity) article. Once the game server and player clients are running (instructions below):
- If a minimum of four players have joined, a new round will start.
- Players may join at any time, and will be an active player at the start of the next round.
- Players may leave at any time, possibly causing the current round to terminate.
- The game ends when there are no more black cards or white cards left, whichever comes first.
- At any time during an active round, a player may be a subject or the czar.

## Running the game server
One game server must be running to facilitate interaction between players. In the `Humanities/` directory run the game server:
```java -ea -cp bin humanities.GameServer PORT WHITE_CARDS BLACK_CARDS```
Where:
- `PORT` is an integer that specifies the server's listening port number
- `WHITE_CARDS` is the path to a text file containing the set of white cards used to play the game. Each line in the input file is intepreted as text corresponding to a unique white card.
- `BLACK_CARDS` is the path to a file containing the set of black cards used to play the game. Each line in the input file is intepreted as text corresponding to a unique black card. Black cards must one fill in space, i.e. they must match `(~_)*_+(~_)*`.

## Running the player client

Run a player client for every each participant. In the `Humanities/` directory run the player client: 
```java -cp bin humanities.PlayerClient HOST PORT```
Where:
- `HOST` is the hostname
- `PORT` is the game server's port number

### Player Commands
- `"state"` prints the current state of the player, indicating their role (subject or czar) in the current round, or if they are inactive. The will also print the current black card if relevant. 
- `"hand"` prints the players current hand of white cards.
- `"quit"` exits the game.
- When prompted, a subject may play a white card by giving the number of the corresponding white card in their hand.
- When prompted, the czar may choose a white card by giving the number of the corresponding white card of subjects' proposals.

