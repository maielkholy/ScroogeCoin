import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class Transaction {
	//Type
	String type;
	//counter
	static int countercc=0;
	static int counterpay=0;
	//Transaction ID
	String ID;
	//Sender
	PublicKey sender;
	//Receiver
	PublicKey receiver;
	//Amount
	int amount;	
	//coins
	ArrayList<Coin> coins;	
	//Digital Sign
	byte[] DS;	
	//Hash pointer
	ArrayList<byte[]> prevPointers;
	ArrayList<HashMap<byte[],Transaction>>history;
    //ArrayList<Transaction> history;
	//Hash
	byte[] hash;
	//coinID
	String coinID;
	//coin object created
	Coin coin;
	public Transaction(Coin coin) throws NoSuchAlgorithmException{//create coin
		history= new ArrayList<HashMap<byte[],Transaction>>();
		prevPointers= new ArrayList<byte[]>();
		ID= "CC("+countercc+")";
		countercc++;
		this.coinID= coin.getCoinID();
		type="CC";
		hash();
	
		HashMap<byte[],Transaction>h= new HashMap<byte[],Transaction>();
		for(int i=0;i<coin.getHistory().size();i++){
		h.put(coin.getHistory().get(i).getHash(),coin.getHistory().get(i));
		}
		history.add(h);
		coin.addTransaction(this);
		setPrevHash();
	}
	public Transaction(PublicKey sender,PublicKey receiver, int amount, ArrayList<Coin> coins) throws NoSuchAlgorithmException{// transfer coins transaction
		this.sender=sender;
		prevPointers= new ArrayList<byte[]>();
		ID="Pay("+counterpay+")";
		counterpay++;
		type="pay";
		this.receiver= receiver;
		this.amount= amount;
		this.coins= coins;
		hash();
		history= new ArrayList<HashMap<byte[],Transaction>>();
		
		for(int j=0; j<coins.size();j++){
			Coin c= coins.get(j);
			HashMap<byte[],Transaction> h= new HashMap<byte[],Transaction>();
			for (byte[] i : c.getHistory().keySet()) {
				  h.put(i,c.getHistory().get(i));
				}
			history.add(h);
		}
		setPrevHash();
		for (int i=0; i<coins.size();i++){
			//coins.get(i).addTransaction(this);
		}
	}
	
	public String getPlainText(){
		String transaction="";
		if(this.type.equals("CC")){
			transaction=this.ID+";"+this.coinID;
		}
		else{
			transaction=this.ID+";"+this.receiver+";"+this.amount+";"+this.coins;
		}
		return transaction;
	}
	
	public void setPrevHash(){
		if(type.equals("CC")){
			prevPointers.add("0".getBytes());

		}
		else{
			for(int i=0;i<coins.size();i++){
				
				prevPointers.add(coins.get(i).getLatest());
			}
			
		}		
	}
	public void hash() throws NoSuchAlgorithmException{
		String transaction="";
		transaction= getPlainText();
		byte[] input = transaction.getBytes();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		this.hash = digest.digest(input);
		
	}
	public void sign(PrivateKey privateKey) throws Exception {
		String transaction= getPlainText();
	    Signature privateSignature = Signature.getInstance("SHA256withRSA");
	    privateSignature.initSign(privateKey);
	    privateSignature.update(transaction.getBytes(StandardCharsets.UTF_8)); 
	    DS = privateSignature.sign();
	}
	public byte[] getHash(){
		return this.hash;
	}
	public byte[] getSign(){
		return this.DS;
	}
	public PublicKey getSender(){
		return sender;
	}
	public PublicKey getReceiver(){
		return receiver;
	}
	public ArrayList<Coin> getCoins(){
		return coins;
	}
	public void printTransaction(){
		System.out.println("********************");
		System.out.println("Transaction ID "+this.ID);
		System.out.println("Hash: "+Base64.getEncoder().encodeToString(hash));
		for(int i=0;i<prevPointers.size();i++){
		System.out.println("Prev Hash Pointer: " +Base64.getEncoder().encodeToString(prevPointers.get(i)));
		}
		if(type.equals("CC")){
			System.out.println("Coin ID created: "+this.coinID);
		}
		else{
			System.out.println("Sender HashCode: " +sender.hashCode());
			System.out.println("Receiver Hash Code: "+receiver.hashCode());
			System.out.println("Amount: " +amount+" Coins");
			
			for(int i=0;i<coins.size();i++){
				System.out.println("Coin transferred: "+coins.get(i).getCoinID());
				System.out.println("Coin History: " );
				printHashMap(history.get(i));
				
				}
		}
		System.out.println("Signature: " + Base64.getEncoder().encodeToString(DS));
	}
	public void printHashMap(HashMap<byte[],Transaction> hm){
		for (byte[] i : hm.keySet()) {
			 System.out.println(hm.get(i).ID);
			}
	}
	
	
	
}
