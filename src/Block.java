import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;


public class Block {
	//Counter
	static int counter=0;
	// Block ID
	String ID;
	//Transactions (10)
	ArrayList<Transaction> transactions;
	// Block Hash
	byte[] hash;
	//Prev Hash pointer
	byte[] prevPointer;
	public Block(ArrayList<Transaction>t){
		transactions=new ArrayList<Transaction>();
		ID= "Block("+counter+")";
		counter++;
		for(int i=0;i<t.size();i++){
		this.transactions.add(t.get(i));
		}
		
	}
	
	public void hash() throws NoSuchAlgorithmException{
		String block=ID+transactions;
		byte[] input = block.getBytes();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		this.hash = digest.digest(input);
		
	}
	public byte[] getHash() throws NoSuchAlgorithmException{
		hash();
		return hash;
	}
	public void setPointer(byte[] pointer){
		prevPointer=pointer;
	}
	public void printBlock(){
		System.out.println("Block ID: "+ ID);
		System.out.println("Block Hash: "+ Base64.getEncoder().encodeToString(hash));
		System.out.println("Previous Hash: "+ Base64.getEncoder().encodeToString(prevPointer));
		}
	
	
}
