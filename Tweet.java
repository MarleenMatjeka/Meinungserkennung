
public class Tweet {
	private String content;
	private String target;
	private String stance;
	private String opinion;
	private String sentiment;
	
	
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
	
	
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public String toString() {
		return "Content: "+content+"\ntarget: "+target+"\nstance: "+stance+"\nopinionTowards: "+opinion+"\nsentiment: "+sentiment;
	}
}
