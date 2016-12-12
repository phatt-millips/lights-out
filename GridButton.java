import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class GridButton extends JButton {
	
	private int row;
	private int col;
	private boolean lit;
	private final Color DARK = new Color(165,137,193);
	private final Color LIGHT = new Color(255,250,129);
	private int r = 165;
	private int g = 137;
	private int b = 193;
	private Timer fadeTimer= new Timer(0, new fadeButtonListener());


	GridButton(int x, int y){
		super("");
		row = x;
		col = y;
		lit = false;
		setBackground(new Color(r, g, b));
		setBorder(new BevelBorder(0));
	}
	
	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}
	public boolean isLit(){
		return lit;
	}
	public void toggleLit(){
		lit = !lit;
		fadeTimer.start();
	}
	//Simply changes the boarder of the button that is pressed
	public void buttonPressed(){
		setBorder(new BevelBorder(10));
	}
	public void reset(){
		lit = false;
	}
	private class fadeButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if (lit && !new Color(r,g,b).equals(LIGHT)){			
				setBackground(new Color(r, g, b));
				if (r<LIGHT.getRed()){
					r++;
				}
				if (g<LIGHT.getGreen()){
					g++;
				}
				if (b>LIGHT.getBlue()){
					b--;
				}
			}
			else if (!lit && !new Color(r,g,b).equals(DARK)){		
				setBackground(new Color(r, g, b));
				if (r > DARK.getRed()){
					r--;
				}
				if (g > DARK.getGreen()){
					g--;
				}
				if (b < DARK.getBlue()){
					b++;
				}
			}
			else{
				fadeTimer.stop();
				setBorder(new BevelBorder(0));
			}
		}
	}

}

