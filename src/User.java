import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
public class User {
	//User ID
	String ID;
	static int counter;
	//PK
	PublicKey pk;
	//SK
    private PrivateKey sk;
	//Coins owned
	ArrayList<Coin> coins;
	public User(){
		try {
			ID= "User("+counter+")";
			counter++;
			KeyPair kp= generateKeyPair();
			pk= kp.getPublic();
			sk= kp.getPrivate();
			coins= new ArrayList<Coin>();						
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public PublicKey getPk(){
		return pk;
	}	
	//Method to generate Transaction
	public Transaction genTransaction(PublicKey receiver) throws Exception{
		int amount=(int)((Math.random()*coins.size())+1); 
		ArrayList<Coin> c= new ArrayList<Coin>();
		ArrayList<Integer> indicies= new ArrayList<Integer>();
		for(int i=0;i<amount;i++){
			int index;
			do{ index=(int)(Math.random()*coins.size());}
			while(indicies.contains(index));
			indicies.add(index);
			c.add(coins.get(index));
		}
		
		Transaction t= new Transaction(pk,receiver,amount,c);
		t.sign(sk);	
		return t;
	}
	public void addCoins(ArrayList<Coin> c){
		for(int i=0;i<c.size();i++){
			coins.add(c.get(i));
		}
	}
	public static KeyPair generateKeyPair() throws Exception {
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(2048, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();
	    return pair;
	}
}
