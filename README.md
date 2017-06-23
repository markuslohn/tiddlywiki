# bimalo-tiddlywiki

A small utility to generate a [Tiddlywiki](http://tiddlywiki.com) file based on the analysed file system. It
generates a Tiddler for every found file. The folder names are used to generate tags for the files.

It uses the following frameworks:

* [Apache Commons Virtual File System](https://commons.apache.org/proper/commons-vfs/): Used to walk trough the file system.
* [Apache Freemarker](http://freemarker.org): A template engine used to generate the TiddlyWiki file.

The project contains the following modules

Module  |  Description |  
--|---
tiddlywiki-common  | cross-cutting functionality
tiddlywiki-core | TiddlyWiki business objects and file system

## Usage

The final distribution contains the following structure and files:

bimalo-tiddlywiki  
|--bin    
|---- tw.sh  
|---- default-template.html  
|---- log4j.properties  
|--lib  
|----- bimalo-tiddlywiki-XXX.jar  
|--log  


./tw -rootFolder=<value> -templateFile=<value> -resultFile=<value>

|Parameter  |  Description
|-----------|-------------
|rootFolder |  The absolute path to the folder containing the content for the TiddlyWiki.
|templateFile| The absolute path to an empty TiddlyWiki file.
|resultFile| The absolute path to the result TiddlyWiki file.


## Build

### Normal Build
    mvn install

### Normal Build with Checkstyle
    mvn install -Psourcecheck

### Distribution
    mvn assembly:single

**Note:** The distribution is available in the /target folder.

## Release

1. Clone git repository

        https://bitbucket.org/bimalo/tiddlywiki.git

2. Checkout branch `master`.

        git fetch origin
        git checkout develop
        git pull
        Set version number for the release

3. Set version number

        mvn versions:set -DnewVersion=<release-version-number> -DgenerateBackupPoms=false -o
        git commit -m "Bumped version number to <release-version-number>"
        git tag -a <release-version-number>

4. Set next version number

        mvn versions:set -DnewVersion=<next-snapshot-version-number> -DgenerateBackupPoms=false -o         
        git commit -m "Bumped version number to <next-snapshot-version-number>"

5. Push all commits and tags

        git push origin master
        git push origin --tags <release-version-number>