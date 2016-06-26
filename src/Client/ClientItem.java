package Client;

import java.awt.Color; 
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;
import Server.Items.ServerArmour;
import Server.Items.ServerPotion;
import Server.Items.ServerWeapon;

@SuppressWarnings("serial")
/**
 * An item in the inventory. It is also a button to identify clicking
 * @author Alex Raita & William Xu
 *
 */
public class ClientItem extends JButton implements MouseListener{

	private Image image;
	private String imageName;
	private boolean selected = false;
	private int equipSlot = ServerPlayer.DEFAULT_WEAPON_SLOT;
	private int row;
	private int col;
	private ClientInventory inventory;
	private String type;
	private int amount = 1;
	private int cost;

	/**
	 * Constructor
	 */
	public ClientItem(String imageName, String type,int amount,int cost,int row, int col, ClientInventory inventory)
	{
		super(new ImageIcon(Images.getImage(imageName)));
		this.amount = amount;
		this.row = row;
		this.col = col;
		this.type = type;
		this.cost = cost;
		this.inventory = inventory;
		this.imageName = imageName;
		image = Images.getImage(imageName);

		if(type.charAt(2) == ServerWorld.ARMOUR_TYPE.charAt(2))
			equipSlot = ServerPlayer.DEFAULT_ARMOUR_SLOT;

		setSize(Images.INVENTORY_IMAGE_SIDELENGTH+10,Images.INVENTORY_IMAGE_SIDELENGTH+10);
		setLocation(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*29 + 5,row*Images.INVENTORY_IMAGE_SIDELENGTH+row*20+375);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(this);
		
		//Add tooltips
		switch(type)
		{
		case ServerWorld.HP_POTION_TYPE:
			setToolTipText(String.format("+%d HP",ServerPotion.HEAL_AMOUNT));
			break;
		case ServerWorld.MAX_HP_TYPE:
			setToolTipText(String.format("Max HP +%d",ServerPotion.MAX_HP_INCREASE));
			break;
		case ServerWorld.MANA_POTION_TYPE:
			setToolTipText(String.format("+%d Mana",ServerPotion.MANA_AMOUNT));
			break;
		case ServerWorld.MAX_MANA_TYPE:
			setToolTipText(String.format("Max Mana +%d",ServerPotion.MAX_MANA_INCREASE));
			break;
		case ServerWorld.DMG_POTION_TYPE:
			setToolTipText(String.format("+%d Base Damage",ServerPotion.DMG_AMOUNT));
			break;
		case ServerWorld.SPEED_POTION_TYPE:
			setToolTipText(String.format("+%d Speed",ServerPotion.SPEED_AMOUNT));
			break;
		case ServerWorld.JUMP_POTION_TYPE:
			setToolTipText(String.format("+%d Jump",ServerPotion.JUMP_AMOUNT));
			break;
		case ServerWorld.MONEY_TYPE:
			setToolTipText("Money");
			break;
		case ServerWorld.STEEL_ARMOUR:
			setToolTipText(String.format("Steel Armour (%.0f%% Damage Reduction)",ServerArmour.STEEL_DEF*100));
			break;
		case ServerWorld.BLUE_NINJA_ARMOUR:
			setToolTipText(String.format("Blue Ninja Armour (%.0f%% Damage Reduction)",ServerArmour.BLUE_DEF*100));
			break;
		case ServerWorld.RED_NINJA_ARMOUR:
			setToolTipText(String.format("Red Ninja Armour (%.0f%% Damage Reduction)",ServerArmour.RED_DEF*100));
			break;
		case ServerWorld.GREY_NINJA_ARMOUR:
			setToolTipText(String.format("Grey Ninja Armour (%.0f%% Damage Reduction)",ServerArmour.GREY_DEF*100));
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Dagger (+%d Damage)",ServerWeapon.DADIAMOND_DMG));
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Dagger (+%d Damage)",ServerWeapon.DAGOLD_DMG));
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Dagger (+%d Damage)",ServerWeapon.DAIRON_DMG));
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Dagger (+%d Damage)",ServerWeapon.DASTONE_DMG));
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Dagger (+%d Damage)",ServerWeapon.DAWOOD_DMG));
			break;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Ax (+%d Damage)",ServerWeapon.AXDIAMOND_DMG));
			break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Ax (+%d Damage)",ServerWeapon.AXGOLD_DMG));
			break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Ax (+%d Damage)",ServerWeapon.AXIRON_DMG));
			break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Ax (+%d Damage)",ServerWeapon.AXSTONE_DMG));
			break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Ax (+%d Damage)",ServerWeapon.AXWOOD_DMG));
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Sword (+%d Damage)",ServerWeapon.SWDIAMOND_DMG));
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Sword (+%d Damage)",ServerWeapon.SWGOLD_DMG));
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Sword (+%d Damage)",ServerWeapon.SWIRON_DMG));
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Sword (+%d Damage)",ServerWeapon.SWSTONE_DMG));
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Sword (+%d Damage)",ServerWeapon.SWWOOD_DMG));
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Halberd (+%d Damage)",ServerWeapon.HADIAMOND_DMG));
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Halberd (+%d Damage)",ServerWeapon.HAGOLD_DMG));
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Halberd (+%d Damage)",ServerWeapon.HAIRON_DMG));
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Halberd (+%d Damage)",ServerWeapon.HASTONE_DMG));
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Halberd (+%d Damage)",ServerWeapon.HAWOOD_DMG));
			break;
		case ServerWorld.SLINGSHOT_TYPE:
			setToolTipText(String.format("Slingshot (+%d Damage)",ServerWeapon.SLING_DMG));
			break;
		case ServerWorld.WOODBOW_TYPE:
			setToolTipText(String.format("Wood Bow (+%d Damage)",ServerWeapon.WOODBOW_DMG));
			break;
		case ServerWorld.STEELBOW_TYPE:
			setToolTipText(String.format("Steel Bow (+%d Damage)",ServerWeapon.STEELBOW_DMG));
			break;
		case ServerWorld.MEGABOW_TYPE:
			setToolTipText(String.format("Mega Bow (+%d Damage)",ServerWeapon.MEGABOW_DMG));
			break;
		case ServerWorld.FIREWAND_TYPE:
			setToolTipText(String.format("<html>Fire Wand (+%d Damage)<p>ManaCost: %d",ServerWeapon.FIREWAND_DMG,ServerWeapon.FIREWAND_MANA));
			break;
		case ServerWorld.ICEWAND_TYPE:
			setToolTipText(String.format("<html>Ice Wand (+%d Damage)<p>Mana Cost: %d",ServerWeapon.ICEWAND_DMG,ServerWeapon.ICEWAND_MANA));
			break;
		case ServerWorld.DARKWAND_TYPE:
			setToolTipText(String.format("<html>Dark Wand (+%d Damage)<p>Mana Cost: %d",ServerWeapon.DARKWAND_DMG,ServerWeapon.DARKWAND_MANA));
			break;
		}
	}

	/**
	 * Paint the amount of that this item occurs
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		if(amount <= 1)
			return;
		graphics.setColor(Color.white);
		if(amount <= 9)
			graphics.drawString(amount+"", getWidth()-8, 10);
		else if(amount <= 99)
			graphics.drawString(amount+"", getWidth()-16, 10);
		else
			graphics.drawString(amount+"", getWidth()-24, 10);
	}
	public int getEquipSlot()
	{
		return equipSlot;
	}
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getCost()
	{
		return cost;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setRow(int row)
	{
		this.row = row;
	}

	public int getRow()
	{
		return row;
	}

	public int getCol()
	{
		return col;
	}
	public void setCol(int col)
	{
		this.col = col;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void increaseAmount()
	{
		amount++;
	}

	public void increaseAmount(int amount)
	{
		this.amount += amount;
	}
	public int getAmount()
	{
		return amount;
	}

	public void decreaseAmount()
	{
		amount--;
	}

	public void decreaseAmount(int amount)
	{
		this.amount -= amount;		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * React to user clicks
	 */
	public void mousePressed(MouseEvent e) {
		if(e.getButton()== MouseEvent.BUTTON1)
		{
			//If it can be equipped
			if(type.charAt(1) == ServerWorld.EQUIP_TYPE.charAt(1))
			{
				//If this is already equipped remove it
				if(selected)
				{
					
					ClientItem[][] invGrid = inventory.getInventory();
					for(int row = 0; row < invGrid.length;row++)
						for(int col = 0;col < invGrid[row].length;col++)
							if(invGrid[row][col] == null)
							{
								//Move back to inventory
								inventory.getClient().printToServer("MI "+equipSlot);
								setBorder(BorderFactory.createEmptyBorder());
								selected = false;
								
								invGrid[row][col] = this;
								this.row = row;
								this.col = col;
								if(type.charAt(2) == ServerWorld.WEAPON_TYPE.charAt(2))
								{
									inventory.getEquippedWeapons()[equipSlot] = null;
									equipSlot = ServerPlayer.DEFAULT_WEAPON_SLOT;
									for(int weapon = 0; weapon < inventory.getEquippedWeapons().length;weapon++)
									{
										if(inventory.getEquippedWeapons()[weapon] != null)
										{
											inventory.getClient().setWeaponSelected(weapon);
											inventory.getEquippedWeapons()[weapon].setBorder(BorderFactory.createLineBorder(Color.white));
										}
									}
								}
								else if(type.charAt(2) == ServerWorld.ARMOUR_TYPE.charAt(2))
								{
									inventory.setEquippedArmour(null);
								}
								setLocation(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*29,row*Images.INVENTORY_IMAGE_SIDELENGTH+row*20+375);

								return;
							}
				}
				//If it is not equipped, equip it
				else if(type.charAt(2) == ServerWorld.WEAPON_TYPE.charAt(2))
				{
					//Only move to weapons if there is room
					int pos = 0;
					for(;pos < ServerPlayer.MAX_WEAPONS;pos++)
					{
						if(inventory.getEquippedWeapons()[pos] == null)
							break;
					}

					if(pos == ServerPlayer.MAX_WEAPONS)
						return;

					inventory.getClient().printToServer("MW "+type);
					selected = true;
					inventory.getInventory()[row][col] = null;
					inventory.getEquippedWeapons()[pos] = this;
					equipSlot = pos;
					row = -1;
					col = -1;
					setLocation(equipSlot*Images.INVENTORY_IMAGE_SIDELENGTH+equipSlot*23+95,865);

					System.out.println("Selected "+inventory.getClient().getWeaponSelected());
					//If this is the first item to be equipped, auto select it
					if(inventory.getClient().getWeaponSelected() == ServerPlayer.DEFAULT_WEAPON_SLOT ||inventory.getEquippedWeapons()[inventory.getClient().getWeaponSelected()] == null )
					{
						inventory.getClient().setWeaponSelected(equipSlot);
						setBorder(BorderFactory.createLineBorder(Color.white));
					}

					repaint();
				}
				//If it is not equipped, equip it
				else if(type.charAt(2) == ServerWorld.ARMOUR_TYPE.charAt(2))
				{
					inventory.getInventory()[row][col] = null;
					if(inventory.getEquippedArmour() != null)
					{
						inventory.getEquippedArmour().setBorder(BorderFactory.createEmptyBorder());
						inventory.getEquippedArmour().setSelected(false);

						ClientItem[][] invGrid = inventory.getInventory();
						boolean shouldBreak = false;
						for(int row = 0; row < invGrid.length;row++)
						{
							for(int col = 0;col < invGrid[row].length;col++)
								if(invGrid[row][col] == null)
								{
									invGrid[row][col] = inventory.getEquippedArmour();
									inventory.getEquippedArmour().setRow(row);
									inventory.getEquippedArmour().setCol(col);
									inventory.getEquippedArmour().setLocation(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*29,row*Images.INVENTORY_IMAGE_SIDELENGTH+row*20+375);
									shouldBreak = true;
									break;

								}
							if(shouldBreak)
								break;
						}
					}		
					inventory.getClient().printToServer("MA "+type);
					selected = true;
					inventory.setEquippedArmour(this);
					setBorder(BorderFactory.createLineBorder(Color.white));
					setLocation(95,923);
					repaint();


				}
			}
			//If it's a potion use it
			else if(type.charAt(2) == ServerWorld.POTION_TYPE.charAt(2))
			{
				inventory.use(this);
			}

		}
		//Right clicks either sell of drop an item
		else if(e.getButton() == MouseEvent.BUTTON3)
		{
			//Sell item
			if(inventory.getClient().isShopOpen() && !type.equals(ServerWorld.MONEY_TYPE) && !inventory.getClient().getShop().isFull(type) && !selected)
			{
				inventory.sellItem(this,equipSlot);
			}
			//Drop item
			else if(!inventory.getClient().isShopOpen())
				inventory.removeItem(this,equipSlot);
			repaint();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
