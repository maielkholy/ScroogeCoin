import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
public class Coin {
	//counter 
	static int counter=0;
	//coin ID
	private String ID;
	//Signature
	byte[] DS;
	//Transaction acum
	HashMap <byte[],Transaction> history;
	//ArrayList
	ArrayList<byte[]> hashes;
	public Coin(){
		history= new HashMap<byte[],Transaction>();
		hashes=new ArrayList<byte[]>();
		ID= "Coin("+counter+")";
		counter++;		
	}
	 public String getCoinID(){
		 return ID;
	 }
	 public HashMap <byte[],Transaction> getHistory(){
		 return history;
	 }
	 public Transaction getLastTransaction(){
		 if(history.keySet().size()>0){
				Object[] ks=history.keySet().toArray();
				return history.get((byte[])ks[ks.length-1]);
			 }
		 return null;
	 }
	 public byte [] getSig(){
		 return DS;
	 }
	 public byte [] getLatest(){
		 if(hashes.size()>0)
		 return hashes.get(hashes.size()-1);
		
		 return "0".getBytes();
	 }
	 public void addTransaction(Transaction t){//Add transaction
		 history.put(t.getHash(),t);
		 hashes.add(t.getHash());
	 }
	 public void sign(PrivateKey privateKey) throws Exception {
		 String coin= ID;
		 Signature privateSignature = Signature.getInstance("SHA256withRSA");
		 privateSignature.initSign(privateKey);
		 privateSignature.update(coin.getBytes(StandardCharsets.UTF_8)); 
		 DS = privateSignature.sign();
	}
}
