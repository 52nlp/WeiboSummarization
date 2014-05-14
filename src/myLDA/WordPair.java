package myLDA;

public class WordPair {
	int word1;
	int word2;
	int frequency;
	double similarity;
	int type;
	public WordPair(int word1,int word2,int frequency,double similarity){
		this.word1=word1;
		this.word2=word2;
		this.frequency=frequency;
		this.similarity=similarity;
		this.type=0;
	}
	
	public void add(int frequency){
		this.frequency+=frequency;
	}
	
	public void setType(int type){this.type=type;}
	
	public boolean equals(WordPair tocompare){
		if(word1==tocompare.word1&&word2==tocompare.word2)
			return true;
		if(word1==tocompare.word2&&word2==tocompare.word1)
			return true;
		return false;
	}
}
