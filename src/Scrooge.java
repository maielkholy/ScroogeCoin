import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class Scrooge {
	//Public key
	PublicKey pk;
	//Private key
	private PrivateKey sk;
	//Ledger (linked List of Blocks)
	HashMap<byte[],Block> ledger=new HashMap<byte[],Block>();
	ArrayList<byte[]>ledgerHashes;
	//current temp block
	ArrayList<Transaction> blockConstruction=new ArrayList<Transaction>();
	
	//latest hash block
	byte[] blockLhash="Start".getBytes();
	//Network Users
	HashMap<PublicKey, User> users;
	
	byte[] SignLastHash;
	
	public Scrooge(){	
		try {
			KeyPair kp=generateKeyPair();
			pk= kp.getPublic();
			sk= kp.getPrivate();
			users= new HashMap<PublicKey, User>();
			ledger= new HashMap<byte[],Block>();
			ledgerHashes= new ArrayList<byte[]>();
			SignLastHash= signHash(blockLhash);
			//users= new ArrayList<User>(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setUsers(HashMap<PublicKey,User>u){//ArrayList<User>u){
		users=u;
	}	
	public Coin CreateCoin() throws Exception{
		//create coin and sign
		Coin c= new Coin();
		c.sign(sk);
		//create transaction of creating coin ,update hash,sign
		Transaction t= new Transaction(c);
		t.sign(sk);
		//add transaction to list
		blockConstruction.add(t);
		printTempBlock();
		//reach 10- form a block
		if(blockConstruction.size()==10){
			blockComplete();
		}
		return c;
	}
	public void transferInitialCoins(PublicKey receiver, int amount, ArrayList<Coin> coins) throws Exception{
		Transaction t= new Transaction(pk,receiver,amount,coins);
		t.sign(sk);
		blockConstruction.add(t);
		t.printTransaction();
		printTempBlock();
		if(blockConstruction.size()==10){
			blockComplete();
		}
		for(int i=0;i<coins.size();i++){
			coins.get(i).addTransaction(t);
		}
		users.get(receiver).addCoins(coins);
		
	}
	public boolean verifyTransaction(Transaction t) throws Exception{
		t.printTransaction(); 
		String pt= t.getPlainText();
		byte[] sig= t.getSign();
		PublicKey sender= t.getSender();
		ArrayList<Coin> c= t.getCoins();
		boolean signatureVerified= verifySign(pt, sig, sender); 
		boolean coinOwnership= true;
		boolean doubleSpend=false;
		
		for(int i=0; i<t.prevPointers.size();i++){
			Coin coin= c.get(i);
			//verify coin signature
			boolean cs= verifySign(coin.getCoinID(), coin.getSig(), pk);
			//verify user is receiver in last coin transaction
			Transaction prev= coin.history.get(t.prevPointers.get(i));
			PublicKey rec= prev.getReceiver();
			if(!(rec==sender&&cs) ){
				coinOwnership=false;
				break;
			}			
		}
		for(int i=0;i<blockConstruction.size();i++){
			Transaction temp=blockConstruction.get(i);
			if(temp.getSender()==sender){
			ArrayList<Coin> tempC=temp.getCoins();
			
			for(int j =0; j<tempC.size();j++){
				for(int k=0;k<c.size();k++){
					if(c.get(k).getCoinID()==tempC.get(j).getCoinID()){
						doubleSpend= true;
						break;
					}
				}				
			}
			
			}
			
		}
		if(signatureVerified &&coinOwnership &&(!doubleSpend)){
			blockConstruction.add(t);
			printTempBlock();
			if(blockConstruction.size()==10){
				blockComplete();
			}
			return true;
		}
		System.out.println("signatureVerified "+ signatureVerified);
		System.out.println("coinOwnership "+coinOwnership );
		System.out.println("doubleSpend "+doubleSpend);
		return false;
		
		
	}
	public void blockComplete() throws Exception{
		Block b= new Block(blockConstruction);
		b.setPointer(blockLhash);
		blockLhash=b.getHash();
		transferTransactionCoins();
		ledger.put(b.hash, b);
		ledgerHashes.add(b.hash);
		//print blockChain
		System.out.println("*********************************BLOCK CHAIN*********************************");
		for(int i=0; i<ledgerHashes.size();i++){
			
			ledger.get(ledgerHashes.get(i)).printBlock();;
			System.out.println("**************************");
		}
		SignLastHash= signHash(b.hash);
		System.out.println("Last Signed Hash: "+ Base64.getEncoder().encodeToString(SignLastHash));
		
		
	}
	public void transferTransactionCoins(){
		 for(int i=0; i<blockConstruction.size();i++){
			 Transaction t= blockConstruction.get(i);
			 if(!t.type.equals("CC")){
				 if(t.sender!= pk){
			 User sender= users.get(t.sender);
			 
			 User receiver=users.get(t.receiver);
			 ArrayList<Coin> coins= t.getCoins();
			 for(int j=0;j<coins.size();j++){
				 sender.coins.remove(coins.get(j));
				 receiver.coins.add(coins.get(j));
			 }	}		 
		 }
			 }
		 blockConstruction= new ArrayList<Transaction>();
	}

	public static boolean verifySign(String plainText, byte[] signature, PublicKey publicKey) throws Exception {
	    Signature publicSignature = Signature.getInstance("SHA256withRSA");
	    publicSignature.initVerify(publicKey);
	    publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
	    return publicSignature.verify(signature);
	}
	
	public void printTempBlock(){
		System.out.println("-------------------------Temp Block Updated-------------------------");
		for (int i=0; i<blockConstruction.size();i++){
			Transaction t= blockConstruction.get(i);
			System.out.println("Transaction: "+t.ID);
			//t.printTransaction();
		 
		}
	}
	
	public static KeyPair generateKeyPair() throws Exception {
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(2048, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();
	    return pair;
	}
	public byte[] signHash(byte[] hash) throws Exception{
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		 privateSignature.initSign(sk);
		 privateSignature.update(hash); 
		 return privateSignature.sign();
	}
}
