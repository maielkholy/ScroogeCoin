

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class SystemTest extends JFrame{
	
	/**
	 * 
	 */
	Network n;
	private static final long serialVersionUID = 1L;

	public SystemTest() throws IOException 
		{
			JPanelWithBackground jb = new JPanelWithBackground("hiclipart.com.png");
			jb.setPreferredSize(new Dimension(getWidth(),getHeight()));
			jb.setOpaque(false);
		    getContentPane().add(jb);
			setTitle("Welcome to Scrooge");
			setBounds(0,0,500,1000);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			KeyListener listener = new KeyListener() {
			 
			@Override
			 
			public void keyPressed(KeyEvent event) {
				System.exit(0);
			    printEventInfo("Key Pressed", event);
			    
			 
			}
			 
			@Override
			public void keyReleased(KeyEvent arg0) {
		
			}
			@Override
			 
			public void keyTyped(KeyEvent event) {
			 
			}
			 
			private void printEventInfo(String str, KeyEvent e) {
			 
			    System.out.println(str);
			 
			    int code = e.getKeyCode();
			 
			    System.out.println("   Code: " + KeyEvent.getKeyText(code));
			 
			}
			  };
			  
			JLabel label= new JLabel("Press SpaceBar to Terminate");
			
			label.setSize(250, 250);
			label.setFont(new Font("Serif",Font.ITALIC,36));
			  
			  addKeyListener(listener);
			 
			 this.add(label, BorderLayout.NORTH);
			  setVisible(true);
			new Network(); 
		
	} 

	public static void main(String[] args) throws IOException {
		 new SystemTest();
		
	}
}
