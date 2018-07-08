cd groovy
for t in $(find . -name '*Test.groovy')
do
	 echo ">>>>>>>>>>>>>>>>>>TESTING<<<<<<<<<<<<<<<<<<<<<<<"
     echo "$t"
     groovy $t
done
