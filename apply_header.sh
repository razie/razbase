
for ff in `find . -name "*.scala"`
do
   mv $ff ${ff}.orig
   awk 'BEGIN {inn=0} /\/\*/ {inn=inn+1} /\*\// {inn=inn+1} //{if (inn==0 || inn >= 2) {inn=inn+1; if (inn ==0||inn>3) print$0}}' ${ff}.orig > ${ff}.new
   cat ~/w/header.txt ${ff}.new > ${ff}
   rm ${ff}.orig ${ff}.new
done

