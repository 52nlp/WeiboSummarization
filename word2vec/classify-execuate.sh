#########################################################################
# File Name: classify-execuate.sh
# Author: liuchen
# mail: liuchen112013@gmail.com
# Created Time: 2014年04月25日 星期五 15时48分50秒
#########################################################################
#!/bin/bash

#time ./word2vec -train ../doc/diaoyudao/text.splited -output ../doc/diaoyudao/wordclass.unsorted -cbow 0 -size 200 -window 5 -negative 0 -hs 1 -sample 1e-3 -threads 12 -classes 20
#sort ../doc/diaoyudao/wordclass.unsorted -k 2 -n > ../doc/diaoyudao/wordclass.sorted
#echo process file ../doc/diaoyudao/text.splited end

time ./word2vec -train ../doc/yaan/text.splited -output ../doc/yaan/wordclass.unsorted -cbow 0 -size 200 -window 5 -negative 0 -hs 1 -sample 1e-3 -threads 12 -classes 20 -binary 0
sort ../doc/yaan/wordclass.unsorted -k 2 -n > ../doc/yaan/wordclass.sorted
echo process file ../doc/yaan/text.splited end
