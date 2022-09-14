![Description](ic_logo.png)
# Rules
## Introduction
You are a Lord seeking new lands to expand your Kingdom. You must explore all the lands, wheat fields, lakes, mountains in order to spot the best plots. But be careful, as some other lords are also coveting theses lands...

## Object of the Game
Connect your dominos in order to build your kingdom in a 5x5 grid in a way to score the most prestige points.

## Setup
### Setting up a new game
Choose how many players will play. This game supports 1 to 4 players.  
Next, for each player, enter a name which will be displayed. Names can be chosen randomly.  
Finally, for each player, select the player type: human or computer. If a computer is included in the game, choose the AI level. Default level is Normal.  
When you are done choosing the game setup, click Start.

### Starting situation
At the start of the game, each player is given a field, placed at the center of the screen. The field represents your kingdom. At the center of the field, your castle is placed: it is the starting point of your expansion.  

At the start of the game, game dominos are drawn. Each game domino has two elements: an domino number, ranging from 1 to 48, and two tiles, each composed of a tile type, and a crown amount. For each player in the game, 12 game domino are added to the initial drawing pile: thus, it holds 24 dominos if two players are in the game, 36 if it is three players, and 48 for four players. The drawing pile is then shuffled.  

Player will play consecutively until the drawing pile is exhausted. The first turn order will be decided randomly. Consecutive play order will depend on players actions, and is detailed in the Playing the Game section.  

When two players play against each other, they play twice each turn.  
The first turn order is still random. For the following turns, each domino determines their position for the next turn.

## Playing the Game
### Every turn
At the start of every turn, game dominos are drawn. The amount of drawn dominos depend on the number of player in the game: 
- For 2 players, 4 dominos are drawn
- For 3 players, 3 dominos are drawn
- For 4 players, 4 dominos are drawn  
<!-- end of the list -->

The drawn dominos are placed at the bottom of the screen, ordered by their number (ascending). Then, consecutively, each player is to choose a game domino to place on the castle. When every player have played a domino, the turn is finished. If the drawing pile holds enough dominos to start another turn, new game dominos are drawn, and another turn starts.  
When the drawing pile is exhausted, the game ends.

### Player actions
#### Placing a domino
When a domino is chosen by a player, it must be placed on their field. Feel free to look at other players' fields, as it might be more strategic to impede an opponent; however, dominos can only be played on your field, not on your opponents'.

When placing a domino on your field, the following rules apply:
- Game dominos can be played horizontally or vertically alike
- The two tiles forming the game domino must be placed next to each other. You cannot place tiles diagonally
- You can only place the domino on locations that are free: the squares should not contain any tile, including a castle
- One of the two tiles should be connected to a tile of the same type. The castle tile is considered to be of any type, so you will always be able to play a domino next to your castle
- The maximum size of your field is 5x5 tiles (except if you apply additional rules to the game: see section Additional Rules, The Mighty Duel)  

<!-- end of the list -->
![Domino placement rules](rules_placement.png)  
<br/>

If no domino can be placed without violating any of the rules, you may discard any of the available dominos: however, doing so does not grant points. Discarding a domino counts as playing it.  
You cannot choose and play a domino that has been played by another player on the same turn. Dominos that have already been played should be darkened, as to show they are not available for picking.

#### Players order
When all players have chosen and played (or discarded) a domino, then turn ends, and another one starts, with a determined player order.

This player order is determined by last turn's picks: the player who picked the lowest indexed domino will start first, followed by the second lowest, etc.  
For example, on a 4 players game:
- The first turn player order is the following: [P1, P2, P3, P4]
- The available dominos indexes are the following: [4, 16, 17, 42]
- The players choose the following dominos: [{P1:16}, {P2:42}, {P3:4}, {P4:17}]
- All dominos have been chosen and played; thus a new turn starts. A new set of 4 dominos is drawn from the drawing pile
- Players play according to the following order: [P3, P1, P4, P2] (4 < 16 < 17 < 42)  
<!-- end of the list -->

Likewise, on a 2 players game:
- The first turn player order is the following: [P1, P2, P2, P1]
- The available dominos indexes are the following: [4, 16, 17, 42]
- The players choose the following dominos: [{P1:16}, {P2:42}, {P2:4}, {P1:17}]
- All dominos have been chosen and played; thus a new turn starts. A new set of 4 dominos is drawn from the drawing pile
- Players play according to the following order: [P2, P1, P1, P2] (4 < 16 < 17 < 42)  


## End of the Game
When the drawing pile is exhausted, the game ends. The players are then evaluated on the wealth of their kingdom, according to the following rules:
- Kingdoms are composed of different properties. A property is a set of connected tiles of the same type. A tile can only belong to one property
- Kingdoms can have multiple properties of the same type, as long as these properties are not connected
- Castles are not part of any property
- Each property has a size, determined by how many tiles are included in the property, and a number of crowns, sum of any crown carried by a tile of the property
- Each property gives an amount of prestige: prestige is calculated by multiplying the size of the property by the number of crowns of the property
- The wealth of the kingdom is determined by summing all the properties prestige

<!-- end of the list -->
![Calculating points](rules_score.png)  
<br/>

The monarch whose kingdom has the highest wealth value wins the game.  
If several kingdoms share their wealth value, the kingdom most extended (with the most tiles on the field) breaks the tie.  
If the tie is not broken, the kingdom with the most crowns on its properties breaks the tie.  
If the tie is still not broken, the players share the victory.

## Additional Rules
For more variety, additional rules can be added to the game. You can mix these different variants if you want according to what you prefer.

### Dynasty
Play 3 rounds in a row. At the end of the 3 rounds, the player with the highest number of points wins the game.
### The middle Kingdom
Score 10 additional points if your castle is in the center of your kingdom.
### Harmony
Score 5 additional points if you territory is complete (no discarded dominos).
### The Mighty Duel
With 2 players. After one or two practice games, the real champions battle to build the largest kingdoms: use ALL the dominos to build a 7x7 grid.


## Available tiles
![Available tiles](rules_tiles.jpg)

[Game rules (en, PDF)](https://www.gamesweb.dk/spilleregler/kingdomino_eng.pdf)  

<!-- Additional sources:
https://zayenz.se/blog/post/kingdomino-cig2018-paper/
	-->