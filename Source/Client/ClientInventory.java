package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
/**
 * Stores and displays the inventory for the client
 * @author Alex Raita & William Xu
 *
 */
public class ClientInventory extends JPanel implements ActionListener {

	// Pixel width, and inventory dimensions for capacity
	public final static int INVENTORY_WIDTH = 300;
	public final static int WIDTH = 4;
	public final static int HEIGHT = 8;
	public final static Color RED = new Color(253, 83, 83);

	private ClientItem[][] inventory = new ClientItem[HEIGHT][WIDTH];
	private ClientItem[] equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];
	private ClientItem equippedArmour = null;

	private Client client;

	private JButton mainMenu;
	private JButton switchTeams;

	private ArrayList<ClientItem> removeList = new ArrayList<ClientItem>();

	/**
	 * Constructor
	 * 
	 * @param menu  the button that will take the player back to the main menu
	 */
	public ClientInventory(JButton menu) {
		setDoubleBuffered(true);
		setBackground(Color.BLACK);
		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(INVENTORY_WIDTH, Client.SCREEN_HEIGHT);

		if (menu != null) {
			mainMenu = menu;
			mainMenu.setSize(100, 50);
			mainMenu.setLocation(20, Client.SCREEN_HEIGHT - 100);
			add(mainMenu);
		}

		switchTeams = new JButton("Switch Teams");
		switchTeams.setSize(150, 50);
		switchTeams.setLocation(135, Client.SCREEN_HEIGHT - 100);
		switchTeams.addActionListener(this);
		add(switchTeams);
	}

	/**
	 * Adds an item to the array given its image
	 * 
	 * @param image
	 */
	public void addItem(String image, String type, int amount, int cost) {
		// If the item is a potion, check if it already exists and if it does,
		// increase the amount
		if (type.charAt(1) == ServerWorld.STACK_TYPE.charAt(1))
			for (int row = 0; row < inventory.length; row++)
				for (int col = 0; col < inventory[row].length; col++)
					if (inventory[row][col] != null
							&& inventory[row][col].getType().equals(type)) {
						inventory[row][col].increaseAmount(amount);
						return;
					}

		// Find first empty space and add item
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[row].length; col++)
				if (inventory[row][col] == null) {
					inventory[row][col] = new ClientItem(image, type, amount,
							cost, row, col, this);
					add(inventory[row][col]);
					repaint();
					return;
				}

		// Inventory full if the method does not exit
		// This shouldn't happen
		System.out.println("Full Inventory");
	}

	/**
	 * Gets the amount of money in the inventory
	 */
	public int getMoney() {
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory.length; col++)
				if (inventory[row][col] != null
						&& inventory[row][col].getType().equals(
								ServerWorld.MONEY_TYPE))
					return inventory[row][col].getAmount();
		return 0;
	}

	/**
	 * Decrease the amount of money in the inventory
	 * 
	 * @param amount
	 *            the amount to decrease money by
	 */
	public void decreaseMoney(int amount) {
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[0].length; col++)
				if (inventory[row][col] != null
						&& inventory[row][col].getType().equals(
								ServerWorld.MONEY_TYPE)) {
					inventory[row][col].decreaseAmount(amount);
					// If we have no money, remove the button
					if (inventory[row][col].getAmount() <= 0) {
						inventory[row][col].setVisible(false);
						remove(inventory[row][col]);
						invalidate();
						inventory[row][col] = null;
					} else
						inventory[row][col].repaint();
				}
	}

	/**
	 * Sell a given item
	 * 
	 * @param item
	 *            the item to be sold
	 * @param pos
	 *            where the item is located
	 */
	public void sellItem(ClientItem item, int pos) {
		// Tell the server you are selling the item
		if (pos == ServerPlayer.DEFAULT_WEAPON_SLOT || !item.isSelected()) {
			client.printToServer("S " + item.getType());
			if (!removeList.contains(item))
				removeList.add(item);
		}
	}

	/**
	 * When an item is confirmed to be sold, get rid of it
	 */
	public void removeThis(String type) {
		ClientItem toRemove = null;
		for (ClientItem item : removeList) {
			if (item.getType().equals(type)) {
				toRemove = item;
				if (item.getAmount() > 1) {
					item.decreaseAmount();
				} else {
					item.setVisible(false);
					remove(item);
					invalidate();
					for (int row = 0; row < inventory.length; row++)
						for (int col = 0; col < inventory[row].length; col++) {
							if (inventory[row][col] == item) {
								inventory[row][col] = null;
								repaint();
								return;
							}
						}
				}
			}
		}
		removeList.remove(toRemove);
	}

	/**
	 * Removes an item from the inventory
	 * 
	 * @param item
	 *            the item to be removed
	 * @param pos
	 *            where the item is located
	 */
	public void removeItem(ClientItem item, int pos) {
		// Drop 1 item at a time and tell the server
		if (item.getAmount() > 1) {
			item.decreaseAmount();
			client.printToServer("DrI " + item.getType());
		} else {
			// Remove the item completely
			item.setVisible(false);
			remove(item);
			invalidate();
			// If it is in the inventory
			if (pos == ServerPlayer.DEFAULT_WEAPON_SLOT || !item.isSelected())
				for (int row = 0; row < inventory.length; row++)
					for (int col = 0; col < inventory[row].length; col++) {
						if (inventory[row][col] == item) {
							inventory[row][col] = null;
							client.printToServer("DrI " + item.getType());
							repaint();
							return;
						}
					}
			// If it's armour
			else if (pos == ServerPlayer.DEFAULT_ARMOUR_SLOT) {
				equippedArmour = null;
				client.printToServer("DrW " + item.getEquipSlot());

			}
			// If it's a weapon
			else {
				equippedWeapons[pos] = null;
				client.printToServer("DrW " + item.getEquipSlot());

				// If we dropped the weapon we selected, select a new weapon
				if (client.getWeaponSelected() == pos) {
					for (int spot = 0; spot < equippedWeapons.length; spot++)
						if (equippedWeapons[spot] != null) {
							equippedWeapons[spot].setBorder(BorderFactory
									.createLineBorder(Color.white));
							client.setWeaponSelected(spot);
							repaint();
							return;
						}
					client.setWeaponSelected(ServerPlayer.DEFAULT_WEAPON_SLOT);
				}

			}
		}

	}

	/**
	 * Use an item (potion)
	 * 
	 * @param item
	 *            the item to be used
	 */
	public void use(ClientItem item) {
		// If we are trying to use a potion that we cannot use, return
		if ((item.getType().equals(ServerWorld.HP_POTION_TYPE) && client
				.getHP() == client.getMaxHP())
				|| (item.getType().equals(ServerWorld.MANA_POTION_TYPE) && client
						.getMana() == client.getMaxMana())
				|| (item.getType().equals(ServerWorld.SPEED_POTION_TYPE) && client
						.getSpeed() == ServerPlayer.MAX_HSPEED)
				|| (item.getType().equals(ServerWorld.JUMP_POTION_TYPE) && client
						.getJump() == ServerPlayer.MAX_VSPEED)
				|| (item.getType().equals(ServerWorld.DMG_POTION_TYPE) && client
						.getBaseDamage() == ServerPlayer.MAX_DMGADD)
				|| (item.getType().equals(ServerWorld.MAX_HP_TYPE) && client
						.getMaxHP() == ServerPlayer.PLAYER_MAX_HP)
				|| (item.getType().equals(ServerWorld.MAX_MANA_TYPE) && client
						.getMaxMana() == ServerPlayer.PLAYER_MAX_MANA))
			return;

		// Decrease the amonut of the potion
		if (item.getAmount() > 1) {
			item.decreaseAmount();
			client.printToServer("DrU " + item.getType());
		} else {
			// Remove it from the inventory
			item.setVisible(false);
			remove(item);
			invalidate();

			for (int row = 0; row < inventory.length; row++)
				for (int col = 0; col < inventory[row].length; col++) {
					if (inventory[row][col] == item) {
						inventory[row][col] = null;
						client.printToServer("DrU " + item.getType());
						repaint();
						return;
					}
				}
		}
	}

	/**
	 * Clear the inventory except for the money
	 */
	public void clear() {
		ClientItem money = null;
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[row].length; col++)
				if (inventory[row][col] != null) {
					if (inventory[row][col].getType().equals(
							ServerWorld.MONEY_TYPE)) {
						money = inventory[row][col];
					} else
						remove(inventory[row][col]);
				}

		inventory = new ClientItem[HEIGHT][WIDTH];
		// Reset the money
		if (money != null) {
			inventory[0][0] = money;
			inventory[0][0].setLocation(29, 375);
			inventory[0][0].setRow(0);
			inventory[0][0].setCol(0);
		}

		// Remove all weapons
		for (int weapon = 0; weapon < equippedWeapons.length; weapon++)
			if (equippedWeapons[weapon] != null)
				remove(equippedWeapons[weapon]);
		equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];

		if (equippedArmour != null)
			remove(equippedArmour);
		equippedArmour = null;

		invalidate();
		repaint();
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * Paint the inventory
	 */
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		// Background
		graphics.drawImage(Images.getImage("Inventory"), 0, 0, null);

		// Draw stats
		graphics.setFont(ClientWorld.STATS_FONT);
		if (client.getHP() > 0) {
			graphics.setColor(Color.red);
			graphics.fillRect(100, 95,
					(int) (client.getHP() * 180.0 / client.getMaxHP()), 20);
			graphics.setColor(Color.white);
			if (client.getMaxHP() == ServerPlayer.PLAYER_MAX_HP)
				graphics.setColor(Color.green);
			graphics.drawString(
					String.format("%d/%d", client.getHP(), client.getMaxHP()),
					153, 110);
		}

		graphics.setColor(Color.blue);
		graphics.fillRect(100, 135,
				(int) (client.getMana() * 180.0 / client.getMaxMana()), 20);
		graphics.setColor(Color.white);
		if (client.getMaxMana() == ServerPlayer.PLAYER_MAX_MANA)
			graphics.setColor(Color.green);
		graphics.drawString(
				String.format("%d/%d", client.getMana(), client.getMaxMana()),
				153, 150);

		graphics.setColor(RED);
		graphics.drawString(String.format("%.0f%%", client.getArmour() * 100),
				115, 255);

		if (client.getBaseDamage() == ServerPlayer.MAX_DMGADD)
			graphics.setColor(Color.green);
		if (client.getBaseDamage() > 9)
			graphics.drawString(
					String.format("%d(+%d%%)",
							(int)Math.ceil(client.getDamage()*(1+client.getBaseDamage()/100.0)),
							client.getBaseDamage()), 105, 215);
		else
			graphics.drawString(
					String.format("%d(+%d%%)",
							(int)Math.ceil(client.getDamage()*(1+client.getBaseDamage()/100.0)),
							client.getBaseDamage()), 109, 215);

		graphics.setColor(RED);
		if (client.getSpeed() == ServerPlayer.MAX_HSPEED)
			graphics.setColor(Color.green);
		graphics.drawString(
				String.format("%d", client.getSpeed() - ServerPlayer.MOVE_SPEED
						+ 1), 260, 215);

		graphics.setColor(RED);
		if (client.getJump() == ServerPlayer.MAX_VSPEED)
			graphics.setColor(Color.green);
		graphics.drawString(
				String.format("%d", client.getJump() - ServerPlayer.JUMP_SPEED
						+ 1), 260, 255);
	}

	public ClientItem[] getEquippedWeapons() {
		return equippedWeapons;
	}

	public void setEquippedWeapons(ClientItem[] equippedWeapons) {
		this.equippedWeapons = equippedWeapons;

		// Make the border show up around the first weapon
		if (equippedWeapons[0] != null) {
			equippedWeapons[0].setBorder(BorderFactory
					.createLineBorder(Color.white));
			client.setWeaponSelected(0);
		}
	}

	public ClientItem[][] getInventory() {
		return inventory;
	}

	public void setInventory(ClientItem[][] inventory) {
		this.inventory = inventory;
	}

	public ClientItem getEquippedArmour() {
		return equippedArmour;
	}

	public void setEquippedArmour(ClientItem equippedArmour) {
		this.equippedArmour = equippedArmour;
	}

	public JButton getMenuButton() {
		return mainMenu;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		client.printToServer("X");

	}

}