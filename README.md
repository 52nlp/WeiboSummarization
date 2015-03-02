##Requirements##
Java 1.7+ on Windows

##Structure##
```
|-bin                                           java byte code
|-doc
　　|-diaoyudao
　　  |-weibo.htm                                weibo source
　　  |-input.txt								   input file of preprocess
　　  |-stopwords.dat					           table of stopwords
　　  |-splited.txt							   doc after word segmentation
　　  |-selected.txt						       doc after filtering words of low frequency
　　  |-offstopword.txt					       doc after filtering stopwords
　　  |-offspeechtag.txt					       doc after taking off speech tag
　　  |-speechchosen.txt				           doc of words of a particular speech tag
　　  |-speechchosenoffspeechtag.txt	
　　  |-naive
　　      |-single.tree						   one-way semantic tree
　　      |-single.summary				       summary from one-way semantic tree
　　      |-double.summary			           summary from two-way semantic tree
　　  |-classbased
　　      |-phrase.txt						   phrases mined from weibo
　　      |-vec-based-syn.list			       synonyms mined from weibo
　　|-yaan									   same structure as ./doc/diaoyudao
|-lib											library
|-src											java source code
　　|-ICTCLAS/I3S/AC					           word segmentation software interface
　　|-db										   links to data base
　　|-demo						   		       demo
　　|-LDAVariety								   LDA base
　　|-DFLDA|-InfiniteLDA|-newHLDA			       three LDA variances
　　|-DirichletForest						       Dirichlet Forest
　　|-util									   some base class
　　|-Fpgrowth|-NodeClass|-org|-sentiment|-thu|-TM	other frameworks
　　|-preprocess							       preprocess
　　  |-GenDoc.java							   taking off html tag from source file
　　  |-PreProcess.java						   preprocess functions
　　  |-Config.java							   configure
　　|-tf_idf									   Hybrid TFIDF algor
　　|-naiveSummary							   summary from none topic-model
　　  |-Node.java								   node from semantic tree
　　  |-Summary.java							   summarization from one-way semantic tree
　　  |-BothWaySummary.java					   summarization from two-way semantic tree
　　  |-Config.java							   configure
　　  |-Lib.java								   assistant library
　　|-classbBasedSummary						   summary from topic model
　　  |-Node.java								   node from semantic tree
　　  |-VectorLoader.java						   load word vector
　　  |-SynPair.java							   mining synonyms
　　  |-LinkBasedOnVec.java|-PhraseFinder.java   mining phrases
　　  |-Config.java							   configure
　　  |-Lib.java								   assistant library
|-TopicModelData								data
|-word2vec										word2vec libaray from google
```