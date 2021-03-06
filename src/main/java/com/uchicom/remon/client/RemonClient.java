// (c) 2016 uchicom
package com.uchicom.remon.client;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.uchicom.remon.Constants;
import com.uchicom.remon.client.action.CloseAction;
import com.uchicom.remon.client.action.ConnectAction;
import com.uchicom.remon.runnable.ImageReceiver;
import com.uchicom.remon.runnable.Receiver;
import com.uchicom.remon.util.ImagePanel;
import com.uchicom.remon.util.ImageUtil;

/**
 * ローカルクライアント.<br/>
 * (リモートサーバ)-(ローカルクライアント)構成で使用する.<br/>
 * (リモートクライアント)-(経由サーバ)-(ローカルクライアント)構成で使用する.
 * クライアントからカラー白黒、キャプチャ間隔、差分ＯＮＯＦＦ、を制御したい
 * 
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RemonClient extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane = new JTabbedPane();

	private JCheckBoxMenuItem checkbox;
	private JCheckBoxMenuItem checkbox2;

	private Map<JScrollPane, ImageReceiver> receiverMap = new HashMap<>();
	private boolean mono;
	private int delay = 100;
	private String aes;
	private String iv;
	
	public RemonClient(GraphicsConfiguration gc) {
		super(gc);
	}

	public RemonClient(boolean mono, String aes, String iv) {
		super("Remon");
		this.mono = mono;
		this.aes = aes;
		this.iv = iv;
		initComponents();
	}

	public void initComponents() {
		setIconImage(ImageUtil.getImageIcon("images/icon.png").getImage());
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
		menu = new JMenu("画像");
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem radio = new JRadioButtonMenuItem(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				putValue(NAME, "モノクロ");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				write(Constants.COMMAND_IMAGE_KIND, 0);
			}

		});
		group.add(radio);
		menu.add(radio);

		radio = new JRadioButtonMenuItem(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
	
			{
				putValue(NAME, "グレースケール");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				write(Constants.COMMAND_IMAGE_KIND, 1);
			}

		});
		group.add(radio);
		menu.add(radio);

		radio = new JRadioButtonMenuItem(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
	
			{
				putValue(NAME, "カラー");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				write(Constants.COMMAND_IMAGE_KIND, 2);
			}

		});
		radio.setSelected(true);
		group.add(radio);
		menu.add(radio);
		menuBar.add(menu);
		menu = new JMenu("操作");
		checkbox = new JCheckBoxMenuItem(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
	
			{
				putValue(NAME, "マウス");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkbox.isSelected()) {
					write(Constants.COMMAND_MOUSE_FLAG, true);
				} else {
					write(Constants.COMMAND_MOUSE_FLAG, false);

				}
			}

		});
		menu.add(checkbox);

		checkbox2 = new JCheckBoxMenuItem(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
	
			{
				putValue(NAME, "キーボード");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkbox.isSelected()) {
					write(Constants.COMMAND_KEY_FLAG, true);
				} else {
					write(Constants.COMMAND_KEY_FLAG, false);
				}
			}

		});
		menu.add(checkbox2);
		menu.add(new JMenuItem(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
	
			{
				putValue(NAME, "アンダースコアをクリップボードにコピー");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				write(Constants.COMMAND_KEY_PRESS, KeyEvent.VK_UNDERSCORE);
			}

		}));
		menuBar.add(menu);

		menu = new JMenu("送信");
		menu.add(new JMenuItem(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
	
			{
				putValue(NAME, "遅延");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String value = JOptionPane.showInputDialog(RemonClient.this, "遅延時間", delay);
				if (value != null) {
					RemonClient.this.delay = Integer.parseInt(value);
					write(Constants.COMMAND_DELAY, RemonClient.this.delay);
				}
			}

		}));
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

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
	}

	Socket socket = null;
	OutputStream os = null;
	byte[] bytes = new byte[3];

	public void write(int command, int... attributes) {
		try {
			if (!socket.isClosed() && !socket.isOutputShutdown()) {
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
	public void write(int command, boolean... attributes) {
		try {
			if (!socket.isClosed() && !socket.isOutputShutdown()) {
				os.write(command);
				for (boolean attribute : attributes) {
					os.write((byte) (attribute ? 0x01 : 0x00));
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
			socket = new Socket(hostName, port);

			ImagePanel panel = new ImagePanel();
			JScrollPane scrollPane = new JScrollPane(panel);

			panel.addMouseListener(new MouseAdapter() {
				private int modifiersEx;
				@Override
				public void mouseReleased(MouseEvent e) {
					write(Constants.COMMAND_MOUSE_RELEASE, modifiersEx & (~e.getModifiersEx())
							& (InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK));
					modifiersEx = e.getModifiersEx();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					modifiersEx = e.getModifiersEx();
					write(Constants.COMMAND_MOUSE_PRESS, e.getClickCount(), modifiersEx
							& (InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK));
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

			tabbedPane.addTab(hostName + ":" + port + "", scrollPane);
			ImageReceiver receiver = mono ? new Receiver(socket, panel, aes, iv) : new ImageReceiver(socket, panel);

			receiverMap.put(scrollPane, receiver);
			Thread thread = new Thread(receiver);
			thread.setDaemon(true);
			thread.start();
			os = socket.getOutputStream();

			// 初期値送信
			initParameters();
		} catch (Exception e2) {
			JOptionPane.showMessageDialog(this, e2.getMessage());
			e2.printStackTrace();
		}
	}

	/**
	 * 初期パラメータ設定.
	 */
	private void initParameters() {
		write(Constants.COMMAND_DELAY, 100);
		write(Constants.COMMAND_IMAGE_KIND, 2); // 送信開始
	}

	public void close() {
		Component component = tabbedPane.getSelectedComponent();
		if (component == null) {
			return;
		}
		ImageReceiver receiver = receiverMap.get(component);
		tabbedPane.remove(component);
		receiverMap.remove(component);
		receiver.setStoped(true);
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
