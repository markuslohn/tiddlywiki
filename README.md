# bimalo-tiddlywiki

An utility to generate a [Tiddlywiki](http://tiddlywiki.com) file based on folders and files on the file system. For every file a new Tiddler will be generated. The name of the folders are used as tags for the corresponding Tiddlers.

It uses the following frameworks:

* [Apache Commons Virtual File System](https://commons.apache.org/proper/commons-vfs/): Used to walk trough the file system.
* [Apache Freemarker](http://freemarker.org): A template engine used to generate the TiddlyWiki file.
* [Apache Tika](http://tika.apache.org): A library to parse and retrieve document contents and meta data. The framework was extended with a YAML Front Matter Parser.

The project contains the following modules

Module  |  Description |  
--|---
tiddlywiki-common  | cross-cutting functionality
tiddlywiki-core | TiddlyWiki business objects, file system analysis and wiki generation process.

## Usage

The final distribution contains the following structure and files:

bimalo-tiddlywiki  
|--bin    
|---- tw.sh  
|---- default-template.html  
|--config  
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

**Note:**
As alternative you can put all parameters in a config file and provide the path to the config file when invoking the generator. The config file needs `.properties` as extension. See the following example:

```
./tw.sh -configFile=tw.properties
```

## Configuration

### Front Matter

Any text file that contains a YAML front matter block will be processed by this utility as a special file. The front matter must be the first thing in the file and must take the form of valid YAML set between triple-dashed lines. Here is a basic example:

```
---
title: How to install Elasticsearch as single node on CentOS 7
keywords: [Monitoring, elastic]
---
```

Between these triple-dashed lines, you can set predefined variables (see below for a reference) or even create custom ones of your own. These variables will used to configure the Tiddlers.

#### Predefined Global Variables

There are a number of predefined global variables that you can set in the front matter of a page or post.

Variable  | Description  
--|--
title | The title for the Tiddler.
description  |  A detailed human readable explanation for the Tiddler.  
creator  | The author/creator of the Tiddler.  
keywords  | A list of keywords or tags used to classify the Tiddler.  
default | yes or no to indicate this is a default Tiddler.

### Freemarker Template

Freemarker Template engine is used to generate the TiddlyWiki file. So you can create your own template file. Download an empty TiddlyWiki file from the homepage and use Freemarker syntax to customize the template. The following variables can be used in the template:

Variable  | Description   
--|--
title  |  The title for the TiddlyWiki.
subTitle  | The sub-title for the TiddlyWiki.  
defaultTiddlers  | All Tiddlers that should be displayed, when opening the TiddlyWiki.
rootTiddlers  | A hierachial list of Tiddlers.  

See the following example to display all Tiddlers:

```
<#macro tiddlersgenerator tiddlers>
    <#if tiddlers??>
            <#local currentTime = .now>
            <#list tiddlers as tiddler>
               <#local tags="">
               <#list tiddler.tags as tag>
                 <#if tag?is_last>
                   <#local tags=tags + "[[" + tag + "]]">
                 <#else>
                   <#local tags=tags + "[[" + tag + "]]" + " ">
                 </#if>
               </#list>

            <#if tiddler.contentType?? && (tiddler.contentType?string?contains("pdf") || tiddler.contentType?string?contains("jpg") || tiddler.contentType?string?contains("jpeg") || tiddler.contentType?string?contains("png") || tiddler.contentType?string?contains("gif"))>
               <div _canonical_uri="${tiddler.path}" created="${tiddler.createDate?string["yyyyMMddHHmmssS"]}" modified="${tiddler.lastModifyDate?string["yyyyMMddHHmmssS"]}" tags="${tags}" title="${tiddler.title}" type="<#if tiddler.contentType??>${tiddler.contentType}</#if>">
                   <pre>
                   </pre>
            <#else>
               <div created="${tiddler.createDate?string["yyyyMMddHHmmssS"]}" modified="${tiddler.lastModifyDate?string["yyyyMMddHHmmssS"]}" tags="${tags}" title="${tiddler.uniqueTitle}" type="<#if tiddler.contentType??>${tiddler.contentType}</#if>">
                   <pre><#if tiddler.text??>${tiddler.text}</#if></pre>
            </#if>
              </div>
              <#if tiddler.tiddlers??>
                 <@tiddlersgenerator tiddlers=tiddler.tiddlers/>
              </#if>
            </#list>
    </#if>
</#macro>

```

The possible attributes of a Tiddler can be obtained by the javadoc for de.bimalo.tiddlywiki.Tiddler. A complete example is delivered by the distribution in the file default-template.html.

## Build

### Normal Build
    mvn install

### Normal Build with Checkstyle
    mvn install -Psourcecheck

### Distribution
    mvn package

**Note:** The distribution will be generated automatically with the assembly plugin and is available in the /target folder.

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
