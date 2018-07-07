cd groovy
for t in *Test.groovy
do
	 echo ">>>>>>>>>>>>>>>>>>TESTING<<<<<<<<<<<<<<<<<<<<<<<"
     echo "$t"
     groovy $t
done

for t in domain/*Test.groovy
do
	 echo ">>>>>>>>>>>>>>>>>>TESTING<<<<<<<<<<<<<<<<<<<<<<<"
     echo "$t"
     groovy $t
done
