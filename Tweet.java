
public class Tweet {
	private String content;
	private String target;
	private String stance;
	private String opinion;
	private String sentiment;
	
	private int[] N1GramList = new int[30];
	private int[][] N3GramList = new int[30][3];
	
	public Tweet(String content, String target, String stance, String opinion, String sentiment) {
		this.content= content;
		this.target = target;
		this.stance = stance;
		this.opinion = opinion;
		this.sentiment = sentiment;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getStance() {
		return stance;
	}
	
	public String getOpinion() {
		return opinion;
	}
	
	public String getSentiment() {
		return sentiment;
	}
	
	public String getContent() {
		return content;
	}
	
	public int[] getN1GramList() {
		return N1GramList;
	}
	
	public int[][] getN3GramList(){
		return N3GramList;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setN1GramList(int[] N1GramList) {
		this.N1GramList = N1GramList;
	}
	
	public void setN3GramList(int[][] N3GramList) {
		this.N3GramList = N3GramList;
	}
	
	
	public String toString() {
		return "Content: "+content+"\ntarget: "+target+"\nstance: "+stance+"\nopinionTowards: "+opinion+"\nsentiment: "+sentiment;
	}
}
