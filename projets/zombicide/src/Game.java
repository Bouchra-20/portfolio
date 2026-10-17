import Board.Board;
import java.util.*;
import Cells.*;
import Equipments.*;
import Actions.*;
import Players.*;

public class Game {
	/**The game board**/
	protected Board board;
	public static final int MAX_EXP = 30;
	private static List<Player> players;
    private static List<String> roles;
    private static List<Zombie> zombies;
	/**Constructor of class Game
	 * @param board the game board
	 */
	public Game(Board board) {
		this.board = board;
		this.players = new ArrayList<>();
		this.zombies = new ArrayList<>();
	}
	/**
	 * getter method for zombies
	 * @return List of zombies
	 */
	public List<Zombie>	getZombies(){
		return this.zombies;
	}
	/**
	 * getter method for players
	 * @return List of players
	 */
	public List<Player>	getPlayers(){
		return this.players;
	}
	
	public void addPlayer(Player player){
		this.players.add(player);
	}
	/**
	 * Method that returns the total experience for all players in the board
	 * @return int 
	 */
	public int getTotalExp(){
		int sum = 0;
		for(Player player : players){
			sum+=player.getExpertiseLevel();
		}
		return sum;

	}
	
	public void setZombieInSewer(int nbrZmbPasse){
		Random random = new Random();
    	
        ZombieType[] zombieTypes = ZombieType.values();
        for(int i=0;i<nbrZmbPasse;i++) {
        	int randomIndex = random.nextInt(board.getSewers().size());
        	Sewer s = board.getSewers().get(randomIndex);
        	int randomId = random.nextInt(ZombieType.values().length);
        	ZombieType randomZombieType = zombieTypes[randomId];
        	Zombie zombie = new Zombie(randomZombieType);
            s.addZombie(zombie); 
			this.zombies.add(zombie); 
			zombie.setCellPosition(s.getCell());    
        }
    }
	
	public void appearZombie() {
		Random random = new Random();
		
		int Index = random.nextInt(3);
		if(Index == 1) {
			int IndexZombie = random.nextInt(2);
			ZombieType[] zombieType1 = {ZombieType.STRONG,ZombieType.ABOMINATION};
			ZombieType randomType1 = zombieType1[IndexZombie];
			Zombie zombie1 = new Zombie(randomType1);
			int randomIndex = random.nextInt(board.getSewers().size());
        	Sewer s = board.getSewers().get(randomIndex);
        	s.addZombie(zombie1); 
        	this.zombies.add(zombie1);
        	zombie1.setCellPosition(s.getCell());
		}else if(Index == 2) {
			int IndexZombie = random.nextInt(2);
			ZombieType[] zombieType1 = {ZombieType.STRONG,ZombieType.ABOMINATION};
			ZombieType randomType1 = zombieType1[IndexZombie];
			Zombie zombie1 = new Zombie(randomType1);
			
			int randomIndex = random.nextInt(board.getSewers().size());
        	Sewer s = board.getSewers().get(randomIndex);
        	s.addZombie(zombie1); 
        	this.zombies.add(zombie1);
        	zombie1.setCellPosition(s.getCell());
        	
        	IndexZombie = random.nextInt(2);
			ZombieType[] zombieType2 = {ZombieType.WALKER,ZombieType.RUNNER};
			ZombieType randomType2 = zombieType2[IndexZombie];
			Zombie zombie2 = new Zombie(randomType2);
			randomIndex = random.nextInt(board.getSewers().size());
			s = board.getSewers().get(randomIndex);
			s.addZombie(zombie2);
			this.zombies.add(zombie2);
			zombie2.setCellPosition(s.getCell());
		}else if(Index == 3) {
			int IndexZombie = random.nextInt(2);
			ZombieType[] zombieType1 = {ZombieType.STRONG,ZombieType.ABOMINATION};
			ZombieType randomType1 = zombieType1[IndexZombie];
			Zombie zombie1 = new Zombie(randomType1);
			
			int randomIndex = random.nextInt(board.getSewers().size());
        	Sewer s = board.getSewers().get(randomIndex);
        	s.addZombie(zombie1); 
        	this.zombies.add(zombie1);
        	zombie1.setCellPosition(s.getCell());
        	
        	IndexZombie = random.nextInt(2);
			ZombieType[] zombieType2 = {ZombieType.WALKER,ZombieType.RUNNER};
			ZombieType randomType2 = zombieType2[IndexZombie];
			Zombie zombie2 = new Zombie(randomType2);
			randomIndex = random.nextInt(board.getSewers().size());
			s = board.getSewers().get(randomIndex);
			s.addZombie(zombie2);
			this.zombies.add(zombie2);
			zombie2.setCellPosition(s.getCell());
			
			IndexZombie = random.nextInt(2);
			ZombieType[] zombieType3 = {ZombieType.WALKER,ZombieType.RUNNER};
			ZombieType randomType3 = zombieType3[IndexZombie];
			Zombie zombie3 = new Zombie(randomType3);
			randomIndex = random.nextInt(board.getSewers().size());
			s = board.getSewers().get(randomIndex);
			s.addZombie(zombie3);
			this.zombies.add(zombie3);
			zombie3.setCellPosition(s.getCell());
			
		}
	}
	/**
	 * Create the game board
	 */
	public void zombiePhase(Zombie zombie) {
		if(zombie.getCellPosition().players().size() <= 1){
			Player p = zombie.getCellPosition().players().get(0);
			zombie.attack(p);
			if(p.getLifeLevel()<=0) {
				if(p.getPlayerCell() instanceof Street) {
					this.players.remove(p);
					zombie.getCellPosition().removePlayer(p);
				}else {
					if(!p.getEquipments().isEmpty()) {
						zombie.getCellPosition().equipments().addAll(p.getEquipments());
					}
					if(p.getLeftHandItem() != null) {
						zombie.getCellPosition().equipments().add(p.getLeftHandItem());
					}
					if(p.getRightHandItem() != null) {
						zombie.getCellPosition().equipments().add(p.getRightHandItem());
					}
					this.players.remove(p);
					zombie.getCellPosition().removePlayer(p);
					
				}
				System.out.println("\u001B[31m"+zombie.getType()+" attacked "+ p.getRole()+ " , "+ p.getRole()+" has dead "+ "\u001B[0m ");
			}else {
				System.out.println("\u001B[31m"+zombie.getType()+" attacked "+ zombie.getCellPosition().players().get(0).getRole()+ "\u001B[0m ");
			}
		}else{
			Random random = new Random();
			int randomIndex = random.nextInt(zombie.getCellPosition().players().size());
			Player p = zombie.getCellPosition().players().get(randomIndex);
			zombie.attack(p);
			if(p.getLifeLevel()<=0) {
				if(p.getPlayerCell() instanceof Street) {
					this.players.remove(p);
					zombie.getCellPosition().removePlayer(p);
				}else {
					if(!p.getEquipments().isEmpty()) {
						zombie.getCellPosition().equipments().addAll(p.getEquipments());
					}
					if(p.getLeftHandItem() != null) {
						zombie.getCellPosition().equipments().add(p.getLeftHandItem());
					}
					if(p.getRightHandItem() != null) {
						zombie.getCellPosition().equipments().add(p.getRightHandItem());
					}
					this.players.remove(p);
					zombie.getCellPosition().removePlayer(p);
					
				}
				System.out.println("\u001B[31m"+zombie.getType()+" attacked "+ p.getRole()+ " , "+ p.getRole()+" has dead "+ "\u001B[0m ");
			}else {
				System.out.println("\u001B[31m"+zombie.getType()+" attacked "+ p.getRole()+ "\u001B[0m ");
			}
		}
	}
	
	public boolean gameOver(){
		if (this.board.totalZombie()==0 || this.getTotalExp()==MAX_EXP){
			System.out.println("\u001B[33m"+"The game is won"+ "\u001B[0m ");
			return true;
		}else if(this.players.isEmpty()){
			System.out.println("\u001B[33m"+"Game lost, you are dead"+ "\u001B[0m ");
			return true;
		}
		return false;
	}
	
	/**
	 * Play the game
	 */
	public void play() {
        Scanner scanner = new Scanner(System.in);
        boolean fin = false;
        int nosyAction = 0;
		while (!fin) {
			Player player ;
            for(int i=0;i<this.players.size();i++){
            	player = players.get(i);
				player.resetActionPoints();
            	board.CurrentCell(player.getPlayerCell().getCoordinateX(), player.getPlayerCell().getCoordinateY());
                
            	
            	player.PlayerStats();
            	while(player.getActionPoints()>0){
            		
            		 //System.out.println(player.getRole()+" action points : "+player.getActionPoints());          
                     System.out.print("Choose an action : see(see around), search(search a room), take(take on hand an equipment), use(use an equipment), open(open a door), noise(make noise), move(move), attack(attack a zombie)  " );
                     
                     if(player.getRole().equals("Healer")){
 						System.out.print(", heal(heal yourself or someone) ");
 					 }
 					 System.out.println("");
 					 
                     String action = scanner.nextLine().toLowerCase();
                     
                     if(player.getRole().equals("Healer")){
 						if(action.equals("heal")){
 							if(player.heal(player)) {
 								player.decreaseActionPoints();
 							}
 							continue;
 						}
 					 }
                     
                     if (!(action.equals("see") || action.equals("search") || action.equals("take") || action.equals("use") || action.equals("open") || action.equals("noise") || action.equals("move") || action.equals("attack")) ){
                         System.out.println("Invalid input ");
                         continue;
                     }
                     if (action.equals("see")){
                         board.printCell(player.getPlayerCell().getCoordinateX(), player.getPlayerCell().getCoordinateY());
                         continue;
                     }else if(action.equals("search")){
                         if(player.getPlayerCell() instanceof Street){
                             System.out.println("\u001B[36m"+"You can't search on a street."+ "\u001B[0m ");
                             continue;
                         }else{
								if(player.getPlayerCell().equipments().isEmpty()){
									System.out.println("\u001B[36m"+"Nothing to take"+ "\u001B[0m ");
									continue;
								}else{
									SearchRoom searchRoom = new SearchRoom(player);
									if(!searchRoom.execute()){
                                        continue;
                                    }else{
                                    	if(player.getRole().equals("Nosy")){
                                    		if(nosyAction == 0) {
                                    			System.out.println("\u001B[32m"+"You have a free search action each turn "+ "\u001B[0m ");
    											nosyAction +=1;
                                    		}else {
                                    			nosyAction = 0;
                                    			player.decreaseActionPoints();
                                    		}
                                    		
										}else{
											player.decreaseActionPoints();
										}
                                    }
								}
                         }
                         
                     }else if(action.equals("take")){
                         TakeOnHand takeOnHand = new TakeOnHand(player);
                         if(!takeOnHand.execute()) {
                        	 continue;
                         }else {
                        	 player.decreaseActionPoints();
                         }
                     }else if (action.equals("use")){
                         if(player.getRightHandItem() == null){
                             System.out.println("\u001B[36m"+"You don't have any equipment in your hand "+ "\u001B[0m ");
                             continue;
                         }else{
                        	 boolean effect;
                             if(player.getRightHandItem() instanceof FirstAidKit){
                            	 effect = player.getRightHandItem().Effect(player);
                                 if(!effect) {
                                	 continue;
                                 }else {
                                	 player.setRightHandItem(null);
                                	 player.decreaseActionPoints();
                                 }
                             }else if(player.getRightHandItem() instanceof Glasses){
                                 effect = player.getRightHandItem().Effect(player.getPlayerCell());
                                 if(!effect) {
                                	 continue;
                                 }else {
                                	 player.setRightHandItem(null);
                                	 player.decreaseActionPoints();
                                 }
                             }else if(player.getRightHandItem() instanceof HealVial){
                                 effect = player.getRightHandItem().Effect(player);
                                 if(!effect) {
                                	 continue;
                                 }else {
                                	 player.setRightHandItem(null);
                                	 player.decreaseActionPoints();
                                 }
                             }else if(player.getRightHandItem() instanceof Equipments.Map){
                                 effect = player.getRightHandItem().Effect(board);
                                 if(!effect) {
                                	 continue;
                                 }else {
                                	 player.setRightHandItem(null);
                                	 player.decreaseActionPoints();
                                 }
                             }else if(player.getRightHandItem() instanceof Key) {
                            	 System.out.println("\u001B[36m"+"You need to choose \"open\" action"+ "\u001B[0m ");
                            	 continue;
                             }else {
                            	 System.out.println("\u001B[36m"+"You don't have any equipment to use"+ "\u001B[0m ");
                            	 continue;
                             }
                         }
                     }else if (action.equals("open")){
                         if(board.isCarrefour(player.getPlayerCell().getCoordinateX(),player.getPlayerCell().getCoordinateY())){
                             System.out.println("\u001B[36m"+"No door"+ "\u001B[0m ");
                             continue;
                         }else if(!(player.getRightHandItem() instanceof Key || player.getLeftHandItem().type() == WeaponType.AXE || player.getLeftHandItem().type() == WeaponType.CROWBAR)){
                             System.out.println("\u001B[36m"+"You need an equipment to open the door"+ "\u001B[0m ");
                             continue;
                         }else{
                             System.out.println("Choose the direction of the door to open : up,down,left,right");
                             String direction = scanner.nextLine().toLowerCase();
                             if (!(direction.equals("up") || direction.equals("down") || direction.equals("left") || direction.equals("right")) ){
                                 System.out.println("Invalid direction . Please enter up /down / left /right  or 'exit' to quit. ");
                                 continue;
                             }
                             Door d = board.getDoor(player.getPlayerCell().getCoordinateX(), player.getPlayerCell().getCoordinateY(), direction);
                             if(player.getRightHandItem() instanceof Key){
                                 player.getRightHandItem().Effect(d);
                                 player.setRightHandItem(null);
                                 this.appearZombie();
                                 player.decreaseActionPoints();
                             }else{
                            	 if(d.isOpen()) {
                            		 System.out.println("\u001B[36m"+"The door is already opened"+ "\u001B[0m ");
                            		 continue;
                            	 }else {
                            		 d.toOpen();
                                     System.out.println("\u001B[31m"+"You opened the door"+ "\u001B[0m ");
                                     this.appearZombie();
                                     player.decreaseActionPoints();
                            	 }
                             }
                         }
                         
                     }else if (action.equals("noise")){
                         NoiseGenerator.addNoise(player.getPlayerCell());
                         player.decreaseActionPoints();
                     }else if (action.equals("move")){
                         System.out.println("Enter a direction ( up (North), down (South), left (West), right (East) ) or exit to quit:" );
                         String direction = scanner.nextLine().toLowerCase();
                     
                         if ( direction.equals("exit")){
                             continue;
                         }else if (!(direction.equals("up") || direction.equals("down") || direction.equals("left") || direction.equals("right")) ){
                             System.out.println("Invalid direction . Please enter up /down / left /right  or 'exit' to quit. ");
                             continue;
                         }
                         if(player.getPlayerCell() instanceof Street) {
                        	 if(board.move(direction,player)) {
                        		 player.decreaseActionPoints();
                        	 }else {
                        		 continue;
                        	 }
                         }else {
                        	 if(board.getDoor(player.getPlayerCell().getCoordinateX(), player.getPlayerCell().getCoordinateY(), direction) != null){
                                 Door d = board.getDoor(player.getPlayerCell().getCoordinateX(), player.getPlayerCell().getCoordinateY(), direction);
                                 if(d.isOpen()){
                                     board.move(direction,player);
                                     player.decreaseActionPoints();
                                 }else{
                                     System.out.println("\u001B[36m"+"The door is closed."+ "\u001B[0m ");
                                     continue;
                                 }
                        	 }else {
                        		System.out.println("\u001B[36m"+"The player cannot move to this direction"+ "\u001B[0m ");
                         		continue;
                        	 }
                         
                         }
                     }else if (action.equals("attack")){
                    	 if(player.getLeftHandItem().getMaxRange()>0){
 							AttackAction attack = new AttackAction(player,null, player.getLeftHandItem());
 							if(attack.distantAttack(this.board)){
 								if(attack.getTarget() != null && attack.getTarget().getLifePts()<=0){
 									this.zombies.remove(attack.getTarget());
 									//player.getPlayerCell().removeZombie(attack.getTarget());
 								}
 								player.decreaseActionPoints();
 							}else{
 								continue;
 							}
 							
 						}else {
 							if(player.getPlayerCell().getZombies().isEmpty()){
 	                             System.out.println("\u001B[36m"+"No zombie arround"+ "\u001B[0m ");
 	                             continue;
 	                         }else {
 	                        	 List<Zombie> zombiesOfCell = board.getBoard()[player.getPlayerCell().getCoordinateX()][player.getPlayerCell().getCoordinateY()].getZombies();
 	                        	 if(zombiesOfCell.size()==1){
 									Zombie target = zombiesOfCell.get(0);
 									AttackAction attack = new AttackAction(player,target, player.getLeftHandItem());
 	                                attack.execute();
 	                                if(target.getLifePts()<=0){
 	                                	this.zombies.remove(target);
 	                                	//player.getPlayerCell().removeZombie(target);
 	                                }
 	                                player.decreaseActionPoints();
 								}else {
 									System.out.println("Choose a zombie with his lifePoints");
 	 	                             for (int z=0 ;z<zombiesOfCell.size();z++){
 	 	                                 System.out.print(zombiesOfCell.get(z).symbole());
 	 	                             }
 	 	                             System.out.println("");
 	 	                             if(scanner.hasNextInt()){
 	 	                            	 int points = scanner.nextInt();
 	 	                            	 Zombie target ;
 	 	                                 for(Zombie z : zombiesOfCell){
 	 	                                     if(z.getLifePts() == points){
 	 	                                         target = z;
 	 	                                         AttackAction attack = new AttackAction(player,target, player.getLeftHandItem());
 	 	                                         if(attack.execute()) {
 	 	                                        	 if(target.getLifePts()<=0){
 	 	                                                 this.zombies.remove(target);
 	 	                                             }
 	 	                                        	 player.decreaseActionPoints();
 	 	                                        	 break;
 	 	                                         }else {
 	 	                                        	 continue;
 	 	                                         }
 	 	                                         
 	 	                                     }
 	 	                                 }
 	 	                            	 
 	 	                             }else {
 	 	                            	 System.out.println("Invalid input");
 	 	                            	 continue;
 	 	                             }
 								}
 	                         } 
 						} 
                     }
                 }
                     
            	}
            System.out.println("The zombies phase");
            
            for(Zombie zombie : this.zombies){
            	if(zombie.getType()==ZombieType.RUNNER) {
            		for(int j=0;j<2;j++) {
            			if(!(zombie.getCellPosition() instanceof Hotel) && zombie.getCellPosition().hasPlayer()){
    						this.zombiePhase(zombie);
    					
            			}else{
            				board.moveZombietoLoudestCell(zombie);
            			}
            		}
            	}else {
            		if(!(zombie.getCellPosition() instanceof Hotel) && zombie.getCellPosition().hasPlayer()){
						this.zombiePhase(zombie);
					
        			}else{
        				board.moveZombietoLoudestCell(zombie);
        			}
            	}
        }
            fin = this.gameOver();
            NoiseGenerator.resetNoise();
            if(!fin) {
            	NoiseGenerator.resetNoise();
                if(!this.zombies.isEmpty()) {
                	int nbrZmbOut = board.sewerZombie(this.players);
                    this.setZombieInSewer(nbrZmbOut);
                }
            }else {
            	break;
            }
   }
	}
}
