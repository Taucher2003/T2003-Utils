#!/usr/bin/env bash

IFS=$'\n'

# Prepare build
rm -rf javadoc
rm -rf clone
mkdir javadoc || true
cat .gitlab/template/javadoc-header.html > javadoc/index.html

# Clone repo
git clone $1 clone
cd clone
git tag current

# Execute build
for tag in `git tag`; do
  git checkout $tag
  mvn -Dmaven.repo.local=../.m2 clean compile javadoc:aggregate
  mkdir ../javadoc/$tag || true
  mv target/site/apidocs/* ../javadoc/$tag
  echo "<li><a href=\"$tag\">$tag</a></li>" >> ../javadoc/index.html
done


# Cleanup
git tag -d current
cd ..
rm -rf clone

# Finish index page
cat .gitlab/template/javadoc-footer.html >> javadoc/index.html
