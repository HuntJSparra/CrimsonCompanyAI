import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.shape.ArcType;

public class TestDisplayGraphics{
    public static int SIZE_TALL = 300;
    public static int SIZE_WIDE = 800;
    public static int MARGIN = 20;
    public static double LINE_WIDE = 4;
    static int FONT_SIZE;
    static GraphicsContext gc;

    static void display(Canvas can){
	gc = can.getGraphicsContext2D();

	int BOX_WIDE = (SIZE_WIDE-2*MARGIN)/8;
	int BOX_TALL = (SIZE_TALL-2*MARGIN)/2;
	gc.setLineWidth(LINE_WIDE);
	
	gc.clearRect(0,0,SIZE_WIDE,SIZE_TALL);

	gc.setStroke(Color.BLACK);
	gc.strokeRect(MARGIN, MARGIN, BOX_WIDE, 2*BOX_TALL);

	for(int i=1; i<=6; i++){
	    gc.strokeRect(MARGIN + i*BOX_WIDE, MARGIN, BOX_WIDE, BOX_TALL);
	    gc.strokeRect(MARGIN + i*BOX_WIDE, MARGIN + BOX_TALL, BOX_WIDE, BOX_TALL);
	}
	
	gc.strokeRect(MARGIN + 7*BOX_WIDE, MARGIN, BOX_WIDE, 2*BOX_TALL);
		
    }


}
