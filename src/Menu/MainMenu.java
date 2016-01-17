package Menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Client.Client;
import Client.ClientFrame;
import Client.ClientInventory;
import Imports.Images;
import Server.Server;
import WorldCreator.CreatorFrame;
import WorldCreator.CreatorItems;
import WorldCreator.CreatorWorld;

public class MainMenu {

	private static ClientFrame mainFrame;
	private static MainPanel mainMenu;
	private static CreatorPanel creatorPanel;
	private static GamePanel gamePanel;
	
	public MainMenu()
	{
		Images.importImages();
		mainFrame = new ClientFrame();
		mainFrame.setLayout(null);
		mainMenu = new MainPanel();
		mainFrame.add(mainMenu);
		mainMenu.revalidate();
		mainFrame.setVisible(true);
		mainMenu.repaint();
	}

	private static class MainPanel extends JPanel
	{
		int middle = (Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH)/2;
		Image titleImage = Images.getImage("WorldQuestOnline.png");
		JButton playGame;
		JButton createServer;
		JButton createMap;
		JButton instructions;

		public MainPanel()
		{
			setDoubleBuffered(true);
			//setBackground(Color.white);

			setFocusable(true);
			setLayout(null);
			setLocation(0,0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,Client.SCREEN_HEIGHT);


			Image playGameImage = Images.getImage("FindAGame.png");
			playGame = new JButton(new ImageIcon(playGameImage));
			playGame.setSize(playGameImage.getWidth(null),playGameImage.getHeight(null));
			playGame.setLocation(middle - playGameImage.getWidth(null)/2,200);
			playGame.setBorder(BorderFactory.createEmptyBorder());
			playGame.setContentAreaFilled(false);
			playGame.setOpaque(false);
			playGame.addActionListener(new StartGame());
			add(playGame);	

			Image createServerImage = Images.getImage("CreateAServer.png");
			createServer = new JButton(new ImageIcon(createServerImage));
			createServer.setSize(createServerImage.getWidth(null),createServerImage.getHeight(null));
			createServer.setLocation(middle - createServerImage.getWidth(null)/2,350);
			createServer.setBorder(BorderFactory.createEmptyBorder());
			createServer.setContentAreaFilled(false);
			createServer.setOpaque(false);
			createServer.addActionListener(new StartServer());
			add(createServer);	

			Image createMapImage = Images.getImage("CreateAMap.png");
			createMap = new JButton(new ImageIcon(createMapImage));
			createMap.setSize(createMapImage.getWidth(null),createMapImage.getHeight(null));
			createMap.setLocation(middle - createMapImage.getWidth(null)/2,500);
			createMap.setBorder(BorderFactory.createEmptyBorder());
			createMap.setContentAreaFilled(false);
			createMap.setOpaque(false);
			createMap.addActionListener(new StartCreator());
			add(createMap);	

			Image instructionsImage = Images.getImage("Instructions.png");
			instructions = new JButton(new ImageIcon(instructionsImage));
			instructions.setSize(instructionsImage.getWidth(null),instructionsImage.getHeight(null));
			instructions.setLocation(middle - instructionsImage.getWidth(null)/2,650);
			instructions.setBorder(BorderFactory.createEmptyBorder());
			instructions.setContentAreaFilled(false);
			instructions.setOpaque(false);
			instructions.addActionListener(new OpenInstructions());
			add(instructions);	

			setVisible(true);
			repaint();
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.fillRect(0, 0, Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,Client.SCREEN_HEIGHT);
			graphics.drawImage(titleImage,middle - titleImage.getWidth(null)/2,50,null);
		}


	}

	private static class CreatorPanel extends JPanel
	{
		public CreatorPanel()
		{
			setDoubleBuffered(true);
			setBackground(Color.red);
			setFocusable(true);
			setLayout(null);
			setLocation(0,0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,Client.SCREEN_HEIGHT);
			
			String fileName = "";
			while(true)
			{
				fileName = (String)JOptionPane.showInputDialog("Please enter the name of the file you want to edit/create (No blank names)");
				if(fileName != null && !fileName.trim().isEmpty())
					break;
				if(fileName == null)
					System.exit(0);
			}

			CreatorWorld world = null;
			try {
				world = new CreatorWorld(fileName);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new CreatorMenuButton());
			CreatorItems items = new CreatorItems(world,menu);
			world.setLocation(0,0);
			items.setLocation(Client.SCREEN_WIDTH,0);

			add(world);
			add(items);
			world.revalidate();
			items.invalidate();
			items.revalidate();
			setVisible(true);
			items.repaint();
		}
	}

	private static class GamePanel extends JPanel
	{
		Client client;
		
		public GamePanel()
		{
			boolean connected = false;
			int port;

			String serverIP = JOptionPane
					.showInputDialog("Please enter the IP address of the server");
			if (serverIP.equals(""))
			{
				serverIP = "192.168.0.16";
				port = 5000;
			}
			else
			{
				port = Integer.parseInt(JOptionPane
						.showInputDialog("Please enter the port of the server"));
			}

			Socket mySocket = null;

			while (!connected)
			{
				try
				{
					mySocket = new Socket(serverIP, port);
					connected = true;
				}
				catch (IOException e)
				{
					serverIP = JOptionPane
							.showInputDialog("Connection Failed. Please a new IP");
					port = Integer.parseInt(JOptionPane
							.showInputDialog("Please enter the port of the server"));
				}
			}
			JLayeredPane pane = new JLayeredPane();
			pane.setLocation(0,0);
			pane.setLayout(null);
			pane.setSize(Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT);
			pane.setDoubleBuffered(true);
			mainFrame.add(pane);
			pane.setVisible(true);
			
			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new GameMenuButton());
			ClientInventory inventory = new ClientInventory(menu);
			client = new Client(mySocket,inventory,pane);
			inventory.setClient(client);
			
			client.setLocation(0,0);
			inventory.setLocation(Client.SCREEN_WIDTH,0);
			
			pane.add(client);
			mainFrame.add(inventory);
			client.initialize();
			client.revalidate();
			inventory.revalidate();
			pane.revalidate();
			pane.setVisible(true);
			mainFrame.setVisible(true);
			inventory.repaint();
		}
	}
	
	private static class CreatorMenuButton implements ActionListener
	{	
		public void actionPerformed(ActionEvent e)
		{
			creatorPanel.setVisible(false);
			mainFrame.remove(creatorPanel);
			mainFrame.invalidate();
			mainFrame.validate();
			creatorPanel= null;
			
			mainMenu = new MainPanel();
			mainFrame.add(mainMenu);
			mainFrame.setVisible(true);
			mainMenu.revalidate();
		}
	}
	
	private static class GameMenuButton implements ActionListener
	{	
		public void actionPerformed(ActionEvent e)
		{
			gamePanel.setLocation(-10000,-10000);
			gamePanel.setVisible(false);
			mainFrame.remove(gamePanel);
			mainFrame.invalidate();
			mainFrame.validate();
			gamePanel= null;
			
			mainMenu = new MainPanel();
			mainFrame.add(mainMenu);
			mainFrame.setVisible(true);
			mainMenu.revalidate();
		}
	}
	
	private static class StartGame implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			gamePanel = new GamePanel();
			mainFrame.add(gamePanel);
			mainFrame.setVisible(true);
			gamePanel.revalidate();


		}

	}

	private static class StartServer implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			Server server = new Server();

			Thread serverThread = new Thread(server);

			serverThread.start();

			JOptionPane.showMessageDialog(mainMenu, "A Server was opened!");

		}

	}

	private static class StartCreator implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			creatorPanel = new CreatorPanel();
			mainFrame.add(creatorPanel);
			mainFrame.setVisible(true);
			creatorPanel.revalidate();
		}

	}

	private static class OpenInstructions implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}

	}

}