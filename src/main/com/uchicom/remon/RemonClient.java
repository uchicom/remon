/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.uchicom.remon.action.CloseAction;
import com.uchicom.remon.action.ConnectAction;
import com.uchicom.remon.runnable.ImageReceiver;
import com.uchicom.remon.util.ImagePanel;

/**
 * ローカルクライアント.<br/>
 * (リモートサーバ)-(ローカルクライアント)構成で使用する.<br/>
 * (リモートクライアント)-(経由サーバ)-(ローカルクライアント)構成で使用する.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RemonClient extends JFrame {

	private JTabbedPane tabbedPane = new JTabbedPane();

	private boolean ssl;
	private GraphicsConfiguration gc ;
	public RemonClient(GraphicsConfiguration gc){
		super(gc);
	}
	public RemonClient(boolean ssl) {
		super("Remon");
		this.ssl = ssl;
		initComponents();
	}

	public void initComponents() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("接続");
		JMenuItem menuItem = new JMenuItem(new ConnectAction(this));
		menu.add(menuItem);
		menuItem = new JMenuItem(new CloseAction(this));
		menu.add(menuItem);
		menuBar.add(menu);
//		menu = new JMenu("表示");
//		menuItem = new JMenuItem(new FullScreenAction(this));
//		menu.add(menuItem);
		menuBar.add(menu);
		setJMenuBar(menuBar);

		tabbedPane.enableInputMethods(false);

		getContentPane().add(tabbedPane);

		tabbedPane.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void keyReleased(KeyEvent e) {
				write(Constants.COMMAND_KEY_RELEASE, e.getKeyCode());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				write(Constants.COMMAND_KEY_PRESS, e.getKeyCode());
			}
		});

	}

	public void setImage(BufferedImage bufferedImage, int x, int y, long transfer) {
		if (bufferedImage == null)
			return;
		JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
		((ImagePanel) scrollPane.getViewport().getView()).setImage(bufferedImage, x, y, transfer);

	}

	Socket socket = null;
	OutputStream os = null;
	byte[] bytes = new byte[3];

	public void write(int command, int... attributes) {
		try {
			if (!socket.isOutputShutdown()) {
				os.write(command);
				for (int attribute : attributes) {

					bytes[0] = (byte) (attribute & 0xFF);
					bytes[1] = (byte) ((attribute >> 8) & 0xFF);
					bytes[2] = (byte) ((attribute >> 16) & 0xFF);
					os.write(bytes);
				}
				os.flush();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
	}

	public void connect(String hostName, int port) {

		try {
			if (ssl){
				SSLContext sslContext = SSLContext.getDefault();
		        SocketFactory sf = sslContext.getSocketFactory();
				socket = sf.createSocket(hostName, port);
			} else {
				socket = new Socket(hostName, port);
			}
			JPanel panel = new ImagePanel();
			JScrollPane scrollPane = new JScrollPane(panel);

			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					write(Constants.COMMAND_MOUSE_RELEASE,
							e.getModifiers() &
									(InputEvent.BUTTON1_MASK |
											InputEvent.BUTTON2_MASK |
									InputEvent.BUTTON3_MASK));
				}

				@Override
				public void mousePressed(MouseEvent e) {
					write(Constants.COMMAND_MOUSE_PRESS, e.getClickCount(),
							e.getModifiers() &
									(InputEvent.BUTTON1_MASK |
											InputEvent.BUTTON2_MASK |
									InputEvent.BUTTON3_MASK));
				}
			});
			panel.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseMoved(MouseEvent e) {
					write(Constants.COMMAND_MOUSE_MOVE, e.getX(), e.getY());

				}

				@Override
				public void mouseDragged(MouseEvent e) {
					mouseMoved(e);

				}
			});
			scrollPane.addMouseWheelListener(new MouseWheelListener() {

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					write(Constants.COMMAND_MOUSE_WHEEL, e.getWheelRotation());

				}
			});

			tabbedPane.addTab(hostName + "(" + port + ")", scrollPane);

			Thread receiver = new Thread(new ImageReceiver(socket, this));
			receiver.setDaemon(true);
			receiver.start();
			os = socket.getOutputStream();
		} catch (Exception e2) {
			JOptionPane.showMessageDialog(this, e2.getMessage());
			e2.printStackTrace();
		}
	}

	public void close() {
		tabbedPane.remove(tabbedPane.getSelectedIndex());
	}

	public void fullScreen() {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = ge.getDefaultScreenDevice();
		RemonClient client = new RemonClient(device.getDefaultConfiguration());
		try {
			client.setVisible(false);
			client.setUndecorated(true);

			device.setFullScreenWindow(client);
			client.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			device.setFullScreenWindow(null);
		}
	}
}
