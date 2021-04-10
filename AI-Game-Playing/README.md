# AI-Game-Playing
Project code related to the course Artificial Intelligence Through Machine Game-Playing

# Our AI (Ranked?)
Behold our TeamiumPremium AI for Crimson Company!
These are in a particular order, that may be accurate in performance.

## Min Max Monte Carlo
Using Minimax algorithm we look ahead with a random deck / dragon placement like in our Monte Carlo AI. 
This AI ( if you wanna call it that ) performs the worst comparatively to all our AI and the provided AI. ( It barely wins a majority of the time against Random Player )

It struggles to search deep enough due to the branching factor.

## Monte Carlo
In this AI through the use of card counting we generate a random deck and randomly assume the opponents hidden dragon location to determine the best move with these parameters while assuming all subsequent moves are random. This is run several times with multiple combinations to determine the best possible move.

The performance of this AI is only slightly better than Min Max Monte Carlo. 

## Random Rush
In this AI we start by picking a random castle that does not have hidden dragon. 
The point of this AI is to finish the game as quick as possible. To do this it buys the cheapest monster and places it on the chosen castle. 
After this buy the most expensive / valuable monster and place it randomly.  

This AI has been observed to win against Random Player frequently. 
This AI typically has more coins than random near the end of the game ( giving it an advantage ).

## Premium Marketium
In this AI we focus on the economy aspect of the game. This strategy held fairly well against other AI's. We aren't completely sure as to why the economy focus was beneficial but it proved that there were some benefits.
In this AI we also use Min-Max, but since our focus is on the economy we can prune much of the tree by using separate algorithms to determine moves based on the economy ( such as BuyMonster and GetRespond ). Here we only use Min-Max for monster placement.

