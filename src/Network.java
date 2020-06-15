import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class Network {

	//100 Users
	HashMap<PublicKey,User> users;
	//Scrooge	
	Scrooge scrooge;
	
	
	public Network() throws FileNotFoundException{
		FileOutputStream fout= new FileOutputStream("output.txt");
		MultiOutputStream multiOut= new MultiOutputStream(System.out, fout);
		PrintStream stdout= new PrintStream(multiOut);
		System.setOut(stdout);
		scrooge= new Scrooge();
		users= new HashMap<PublicKey, User>();
		initializeUsers();
		scrooge.setUsers(users);
		try {
			initializeCoins();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//Generate transaction every 2 seconds
	     timedTransactions();
	
	}
	
	public void timedTransactions(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
		    @Override
		    public void run() {
		       try {
				genRandomTransactions();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		}, 0, 2000);
	}
	
	public void initializeUsers(){
		for(int i=0;i<100;i++){
			User u=new User();
			users.put(u.getPk(),u);
		}
	}
	
	public void initializeCoins() throws Exception{
		for(PublicKey pk : users.keySet()){
			ArrayList<Coin> coins= new ArrayList<Coin>();
			for(int j=0;j<10;j++){ //create 2 coins per user
				Coin c= scrooge.CreateCoin();
				coins.add(c);
			}
			scrooge.transferInitialCoins(pk, 10, coins);			
		}
	}
	
	public void genRandomTransactions() throws Exception{
		ArrayList<PublicKey> us= new ArrayList<PublicKey>();
		ArrayList<Integer> ui= new ArrayList<Integer>();
		int user;
		for(int i=0;i<2;i++){
		do{ user=(int)(Math.random()*users.size());}
		while(ui.contains(user));
		ui.add(user);
		us.add((PublicKey)users.keySet().toArray()[user]);
		}
		User sender= users.get(us.get(0));
		User receiver= users.get(us.get(1));
		System.out.println("SENDER "+ sender.ID);
		System.out.println("RECEIVER "+ receiver.ID);
		
		scrooge.verifyTransaction(sender.genTransaction(receiver.getPk()));
		
	}
	
	
}
