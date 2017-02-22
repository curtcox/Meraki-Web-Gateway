cd groovy
for t in *Test.groovy
do
	 echo ">>>>>>>>>>>>>>>>>>TESTING<<<<<<<<<<<<<<<<<<<<<<<"
     echo "$t"
     groovy $t
done
