#for dir in WEB-INF/tlds WEB-INF/jsp WEB-INF/tags rendering includes public instructor
#do
#  rsync -rvz --exclude=".*.sw?" tw/$dir/ src/main/webapp/$dir
#done
rsync -rvz --exclude=WEB-INF/classes --exclude=META-INF \
  --exclude=WEB-INF/lib --exclude=WEB-INF/web.xml \
  --exclude=".*.sw?" tw/ src/main/webapp/
