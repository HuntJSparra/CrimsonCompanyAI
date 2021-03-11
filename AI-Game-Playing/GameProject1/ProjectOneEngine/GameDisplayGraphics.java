package ProjectOneEngine;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.shape.ArcType;

class GameDisplayGraphics{
    public static int SIZE_TALL = 700;
    public static int SIZE_WIDE = 700;
    public static int MARGIN = 20;
    public static double LINE_WIDE = 4;
    static int BOX_WIDE;
    static int BOX_TALL;
    static int FONT_SIZE = 30;
    static GraphicsContext gc;


    static void displayState(Canvas can, GameState state){
	int x, y, num_stones;
	
	gc = can.getGraphicsContext2D();

	BOX_WIDE = (SIZE_WIDE-2*MARGIN)/6;
	BOX_TALL = (SIZE_TALL-2*MARGIN)/8;
	gc.setStroke(Color.BLACK);
	gc.setLineWidth(LINE_WIDE);
	
	gc.clearRect(0,0,SIZE_WIDE,SIZE_TALL);

	// Print the Players' Names
	gc.setStroke(Color.BLUE);
	gc.setFont(Font.font("Helvetica", FONT_SIZE));
	gc.setLineWidth(1.5);
	gc.strokeText(state.getTopName(), MARGIN + BOX_WIDE, MARGIN + 0.5*BOX_TALL);
	gc.strokeText(state.getBotName(), MARGIN + BOX_WIDE, MARGIN + 7.75*BOX_TALL);
	gc.setStroke(Color.BLACK);
	// End Players Names
	
	for(int i=0; i<=5; i++){
	    gc.strokeRect(MARGIN + i*BOX_WIDE, MARGIN + 2*BOX_TALL, BOX_WIDE, 2*BOX_TALL);
	    gc.strokeRect(MARGIN + i*BOX_WIDE, MARGIN + 4*BOX_TALL, BOX_WIDE, 2*BOX_TALL);
	}

	gc.strokeRect(MARGIN,MARGIN + BOX_TALL, BOX_WIDE*6, BOX_TALL);
	gc.strokeRect(MARGIN,MARGIN + 6*BOX_TALL, BOX_WIDE*6, BOX_TALL);
       

	// NOTE: Top Player's bins are 5, 4, 3, 2, 1, 0
	// Bot Player's bins are 0, 1, 2, 3, 4, 5
	
	for(int i=0; i<6; i++){
	    num_stones = state.getStones(PlayerID.TOP, 5-i);
	    x = MARGIN + i*BOX_WIDE + BOX_WIDE/2;
	    y = MARGIN + 3*BOX_TALL;
	    displayBinContents(x, y, num_stones);
	}

	for(int i=0; i<6; i++){
	    num_stones = state.getStones(PlayerID.BOT, i);
	    x = MARGIN + i*BOX_WIDE + BOX_WIDE/2;
	    y = MARGIN + 5*BOX_TALL;
	    displayBinContents(x, y, num_stones);
	}

	num_stones = state.getHome(PlayerID.TOP);
	x = MARGIN + 3*BOX_WIDE;
        y = MARGIN + BOX_TALL + BOX_TALL/2;	
	displayHome(x, y, num_stones);

	num_stones = state.getHome(PlayerID.BOT);
	x = MARGIN + 3*BOX_WIDE;
	y = MARGIN + 6*BOX_TALL + BOX_TALL/2;	
	displayHome(x, y, num_stones);

    }

    static void displayBinContents(int x, int y, int num_stones){

	gc.setFill(Color.DARKBLUE);
	int INC = Math.min(BOX_WIDE, BOX_TALL) / 4;
	int RAD = (3 * INC) / 4;
	x = x - RAD/2;
	y = y - RAD/2;
	
	int count = 1;

	//Parameters for a small number of stones
	int half_row = 1;

	//Parameters for larger numbers of stones
	if (num_stones > 9){
	    half_row = 2;
	}
	
	int row_size = half_row * 2 + 1;
	int dx = -1 * half_row * INC;
	int dy = -1 * half_row * INC;
	int temp;

	// Up to 25 Stones
	while (num_stones <= 25 && count <= num_stones){
	    count = count + 1;
	    gc.fillOval(x + dx, y + dy, RAD, RAD);
	    dx = dx + INC;
	    temp = count % row_size;
	    if( temp == 1){
		dy = dy + INC;
		dx = -1 * half_row * INC;
	    }
	}
	
	if( num_stones > 25 ){
	    y = y + FONT_SIZE/2;
	    gc.strokeText(String.valueOf(num_stones), x, y);
	}
    }

    static void displayHome(int x, int y, int num_stones){

	gc.setFill(Color.DARKBLUE);
	int INC = Math.min(BOX_WIDE, BOX_TALL) / 4;
	int RAD = (3 * INC) / 4;
	x = x - RAD/2;
	y = y - RAD/2;
	
	int count = 1;
	int row_size;
	if (num_stones % 2 == 0){
	    row_size = num_stones / 2;
	}
	else{
	    row_size = num_stones / 2 + 1;
	}
	    
	int half_row =  row_size / 2;
	
	int dx = -1 * half_row * INC;
	int dy = -1 * INC;
	int temp;

	while (count <= num_stones){
	    count = count + 1;
	    gc.fillOval(x + dx, y + dy, RAD, RAD);
	    dx = dx + INC;
	    temp = count % row_size;
	    if( temp == 1){
		dy = dy + INC;
		dx = -1 * half_row * INC;
	    }
	}

    }
}
