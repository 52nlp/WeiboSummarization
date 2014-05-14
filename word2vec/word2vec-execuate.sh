#########################################################################
# File Name: word2vec-execuate.sh
# Author: liuchen
# mail: liuchen112013@gmail.com
# Created Time: 2014年04月27日 星期日 13时04分06秒
#########################################################################
#!/bin/bash
time ./word2vec -train ../doc/diaoyudao/text.splited -output ../doc/diaoyudao/word.vec -cbow 0 -size 200 -window 5 -negative 0 -hs 1 -sample 1e-3 -threads 12 -binary 0
./distance ../doc/diaoyudao/word.vec
