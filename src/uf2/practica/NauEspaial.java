package uf2.practica;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class NauEspaial extends javax.swing.JFrame {
	public NauEspaial() {
		initComponents();
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(255, 255, 255));
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
		pack();
	}

	public static void main(String args[]) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(NauEspaial.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
		NauEspaial f = new NauEspaial();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Naus Espaials");
		f.setContentPane(new PanelNau());
		f.setSize(480, 560);
		f.setVisible(true);
	}
}

class PanelNau extends JPanel implements Runnable, KeyListener {
	private int numNaus = 3;
	ArrayList<Nau> nau;
	Nau nauPropia;
	ArrayList<Dispar> dispar = new ArrayList<Dispar>();

	public PanelNau() {
		//nau = new Nau[numNaus];
		nau = new ArrayList<Nau>();
		for (int i = 0; i < numNaus; i++) {
			Random rand = new Random();
			int velocitat = (rand.nextInt(3) + 5) * 10;
			int posX = rand.nextInt(100) + 30;
			int posY = rand.nextInt(100) + 30;
			int dX = rand.nextInt(3) + 1;
			int dY = rand.nextInt(3) + 1;
			String nomNau = Integer.toString(i);
			nau.add(new Nau(nomNau, posX, posY, dX, dY, velocitat));
		}

		// Creo la nau propia
		nauPropia = new Nau("NauNostra", 200, 400, 10, 0, 100);

		// Creo fil per anar pintant cada 0,1 segons el joc per pantalla
		Thread n = new Thread(this);
		n.start();

		// Creo listeners per a que el fil principal del programa gestioni
		// esdeveniments del teclat
		addKeyListener(this);
		setFocusable(true);

	}

	public void run() {
		boolean execucio = true;
		System.out.println("Inici fil repintar");
		while (execucio) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			} // espero 0,1 segons
			// System.out.println("Repintant");
			if(nau.isEmpty()) {
				execucio = false;
				System.exit(0);
			}else {
				repaint();
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < nau.size(); ++i) {
			nau.get(i).pinta(g);
		}
		nauPropia.pinta(g);
		for (int i = 0; i < nau.size(); i++) {
			double referencia = Math.sqrt(Math.pow((nau.get(i).getX()-nauPropia.getX()), 2) + 
					Math.pow((nau.get(i).getY()-nauPropia.getY()), 2));
			if(referencia < 100.0) {
				System.out.println("GAME OVER!");
				System.exit(0);
			}
		}
		for(int i = 0; i < dispar.size(); i++) {
			// si arriva als marges ...
			
			if (dispar.get(i).getY() >= 500 - dispar.get(i).getTy() || dispar.get(i).getY() <= dispar.get(i).getTy()) {
				dispar.remove(i);
				i--;
			} else {
				dispar.get(i).pinta(g);
				for(int j = 0; j < nau.size(); j++) {
					double referencia = Math.sqrt(Math.pow((dispar.get(i).getX()-nau.get(j).getX()), 2) + 
							Math.pow((dispar.get(i).getY()-nau.get(j).getY()), 2));
					if (referencia < 100.0) {
						nau.remove(j);
						//System.out.println("ha tocat");
						j--;
					}
				}
			}
			
		}
	}

	// Metodes necesaris per gestionar esdeveniments del teclat
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" +
		// e.getKeyChar());
		if (e.getKeyCode() == 37) {
			nauPropia.esquerra();
		} // System.out.println("a l'esquerra"); }
		if (e.getKeyCode() == 39) {
			nauPropia.dreta();
		} // System.out.println("a la dreta"); }
		if (e.getKeyCode() == 32) {
			Dispar disp = new Dispar(nauPropia.getX(), nauPropia.getY(), -10, 100);
			dispar.add(disp);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}

class Nau extends Thread {
	private String nomNau;
	private int x, y;
	private int dsx, dsy, v;
	private int tx = 10;
	private int ty = 10;

	private String img = "/images/nau.jpg";
	private Image image;

	public Nau(String nomNau, int x, int y, int dsx, int dsy, int v) {
		this.nomNau = nomNau;
		this.x = x;
		this.y = y;
		this.dsx = dsx;
		this.dsy = dsy;
		this.v = v;
		image = new ImageIcon(Nau.class.getResource("nau.png")).getImage();
		Thread t = new Thread(this);
		t.start();
	}

	public int velocitat() {
		return v;
	}

	public void moure() {
		x = x + dsx;
		y = y + dsy;
		// si arriva als marges ...
		if (x >= 450 - tx || x <= tx)
			dsx = -dsx;
		if (y >= 500 - ty || y <= ty)
			dsy = -dsy;
	}

	public void pinta(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(this.image, x, y, null);
	}

	public void run() {
		while (true) {
			// System.out.println("Movent nau numero " + this.nomNau);
			try {
				Thread.sleep(this.v);
			} catch (Exception e) {
			}
			moure();
		}
	}

	public void esquerra() {
		this.dsx = -10;
	}

	public void dreta() {
		this.dsx = 10;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

class Dispar extends Thread {
	private int x;
	private int y;
	private int dsy, v;
	private int tx = 10;
	private int ty = 10;

	private String img = "/images/bala.jpg";
	private Image image;

	public Dispar(int x, int y, int dsy, int v) {
		this.x = x;
		this.y = y;
		this.dsy = dsy;
		this.v = v;
		image = new ImageIcon(Dispar.class.getResource("bala.png")).getImage();
		Thread t = new Thread(this);
		t.start();
	}

	public int velocitat() {
		return v;
	}

	public void moure() {
		y = y + dsy;
	}

	public void pinta(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(this.image, x, y, null);
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(this.v);
			} catch (Exception e) {
			}
			moure();
		}
	}

	public void moviment() {
		this.dsy = 10;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTy() {
		return ty;
	}
}
