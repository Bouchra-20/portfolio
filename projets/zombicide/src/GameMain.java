import Board.Board;

import Cells.*;
import Equipments.*;
import Players.*;
import java.util.*;
import Actions.*;

public class GameMain{
    //private static Cell[][] cells;
    private static int width;
    private static int height;
    private static int plrnbr;
    public static void main(String[] args) {
        if(args.length < 3 ){
            System.out.println("Please enter a width and a height and the number of players");
        }else if((Integer.parseInt(args[0])<5) || (Integer.parseInt(args[1])<5)){
            System.out.println("Please enter a width or a height  over 5 ");
        }else if((Integer.parseInt(args[2])<=0)){
            System.out.println("Please enter a positive number of players >0");
        }else{
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
            plrnbr= Integer.parseInt(args[2]);
            
            //List<Player> players = new ArrayList<>();
            //List<Zombie> zombies = new ArrayList<>();
            
            Board board = new Board(width,height);
            Game game = new Game(board);
            
            for(int i=0;i<plrnbr;i++){
                game.addPlayer(new Player(0, 5, 3));
            }
            int j=1;
            int k=0;
            for(int i=0;i<plrnbr;i++){
            	if(i>3){
            		if(k>3) {
            			j=1;
            		}
            		k = i-(i-j);
                    game.getPlayers().get(i).assignedRole(k);
                    j++;
                }else{
                    game.getPlayers().get(i).assignedRole(i+1);
                }
            }
            

            board.generate();// Generate the layout with streets and rooms


            board.fillWithBuildings(0,0);

            board.placeSpecialRooms();
            board.addDoor();
            
            
            
            Cell crossroadsCell = board.findCarrefour();
            for(int i=0;i<plrnbr;i++){
                crossroadsCell.setPlayer(game.getPlayers().get(i));
                game.getPlayers().get(i).setCell(crossroadsCell); 
            }

            board.sitEquipments();
            
            /*Zombie zombi1 = new Zombie(ZombieType.STRONG);
            Zombie zombi2 = new Zombie(ZombieType.ABOMINATION);
            Zombie zombi3 = new Zombie(ZombieType.STRONG);
            Zombie zombi4 = new Zombie(ZombieType.ABOMINATION);
            board.setZombie(zombi1,0,0);
            zombi1.setCellPosition(board.getBoard()[0][0]);
            board.setZombie(zombi2,2,4);
            zombi2.setCellPosition(board.getBoard()[2][4]);
            board.setZombie(zombi3,2,3);
            zombi3.setCellPosition(board.getBoard()[2][3]);
            board.setZombie(zombi4,4,4);
            zombi4.setCellPosition(board.getBoard()[4][4]);
            game.getZombies().add(zombi4);
            game.getZombies().add(zombi3);
            game.getZombies().add(zombi2);
            game.getZombies().add(zombi1);*/
            
            int nbrZmbOut = width + height;
            game.setZombieInSewer(nbrZmbOut);
            
            /*HealVial fiole = new HealVial();
            Weapon Axe = new Weapon(WeaponType.CHAINSAW);
            Weapon w2 = new Weapon(WeaponType.SHOTGUN);
            Weapon w3 = new Weapon(WeaponType.CROWBAR);
            Weapon w4 = new Weapon(WeaponType.AXE);*/
            //System.out.println("---------------------------------------------");
            //board.printPlayerPositions();
            //System.out.println("---------------------------------------------");
            //board.printZombiePositions();
            System.out.println("---------------------------------------------");
            board.printBoard();
            System.out.println("---------------------------------------------");
            /*for(int i=0;i<plrnbr;i++){
            	game.getPlayers().get(i).PlayerStats();
            }
            System.out.println("---------------------------------------------");
            */
            /*game.getPlayers().get(0).PickUp(Axe);
            game.getPlayers().get(2).PickUp(w2);
            game.getPlayers().get(3).PickUp(w3);
            game.getPlayers().get(1).PickUp(w4);*/
            //board.printBoardMap();
            //System.out.println("---------------------------------------------");
            
            /*List<String> roles = new ArrayList<>();
            for(Player p : players){
                roles.add(p.getRole());
            }*/
            
            /*Player player = players.get(0);
            
            Scanner scanner = new Scanner(System.in);
            
            ZombieType[] zombieTypes = ZombieType.values();

    		Random random = new Random();*/
            
            game.play();

            
                /*System.out.println("Enter a direction ( up (North), down (South), left (West), right (East) ) or exit to quit:" );
                String direction = scanner.nextLine().toLowerCase();
                
                if ( direction.equals("exit")){
                    break;
                }

                if (!(direction.equals("up") || direction.equals("down") || direction.equals("left") || direction.equals("right")) ){
                    System.out.println("Invalid direction . Please enter up /down / left /right  or 'exit' to quit. ");
                    continue;
                }
                for(int i=0;i<plrnbr;i++){
                	board.move(direction,players.get(i));
                }
                board.printCell(players.get(0).getPlayerCell().getCoordinateX(), players.get(0).getPlayerCell().getCoordinateY());
                for(int i=0;i<plrnbr;i++){
                    players.get(i).PlayerStats();
                    
                /*board.printBoard();
                for(int i=0;i<plrnbr;i++){
                    System.out.println("nosy "+players.get(i).toString());
                }*/
                //board.printBoardMap(); 
        }
        }
}

