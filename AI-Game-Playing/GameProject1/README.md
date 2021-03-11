**This folder has the files that you will need for Project One.**

You should create your AI player in a new (separate package). 
Do NOT add your files to ProjectOneEngine. 
Instead, you should import the files you need from ProjectOneEngine (e.g. the Player Interface)

Your AI players should implement the ProjectOneEngine.Player interface. 

Note: To start a game, I recommend that you compile all the files.
Then once you have compiled the files, use Java to run ProjectOneEngine.AIGameApp or ProjectOneEngine.HumanGameApp

I have my Javafx files in C:\javafx
Therefore, I use the following command to start a game (from the command line):
 
java --module-path  C:\javafx\lib --add-modules javafx.controls ProjectOneEngine.HumanGameApp

**Note:** This tells Java to find the Package ProjectOneEngine and then run the class HumanGameApp belonging to the package ProjectOneEngine.
This requires Java to be able to find the package ProjectOneEngine. 
The first place that Java looks for packages is in sub-directories of the current working directory. 
Therefore, I find it easiest to just open a terminal window, change the directory to the folder that contains ProjectOneEngine and then ask java to run ProjectOneEngine.HumanGameApp  (An alternative approach is to put ProjectOneEngine into a folder in your system's java CLASSPATH)

------------

**THE RULES OF THE GAME:**

The game is played between a Top player and a Bottom player.
The Bottom player goes first

There are 12 bins, 6 on the Top and 6 on the Bottom. 
Each bin starts with four stones.

On a turn, the active player selects a bin on their side.
The stones are taken out of that bin and distributed counter-clockwise
... One stone is dropped in each bin (going counter-clockwise around the circle of bins)

If the last stone ends up in a bin on the opponent's side
... and that bin has 2 or 3 stones (after the stone lands in the bin)
... Then a CAPTURE takes place and the (2 or 3) stones are removed
... These captured stones are permenantly placed in the active player's HOME (off the board)

If a capture takes place, the active player then looks backwards one bin (clockwise)
... If the previous bin is also on the opponent's side and has 2 or 3 stones
... Then the active player captures all the stones from both bins
... Repeat until you hit a bit that doesn't have 2 or 3 stones
... Also, you can only capture on your opponents' side 
... so if looking back clockwise gets you back to your own row of bins, you stop this process

If possible, you MUST leave your opponent with a stone to move on their next turn. 
... That is, after your turn, your opponent must have a stone in one of their bins

If you are unable to leave your opponent with a stone to play on their turn
... Then your opponent (who has no stones in any bins) captures ALL stones in your bins 
... (which is all stones left anywhere) ... and the game ends!

When the game ends, whoever has the most stones in their HOME wins!
