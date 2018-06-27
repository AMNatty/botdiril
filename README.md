# botdiril
Community Discord Bot running on JDA specifically crafted for Vandiland.

**This is a Eclipse Maven project. JDK 10+**

Botdiril uses several dependencies (see ``pom.xml``), maily **JDA** and **lavaplayer**.

**Botdiril pretty much works out of the box with one condition:**
***Setting up ``settings.json``***

# Basic configuration:
For Botdiril to work, you need to get at least a Discord bot secret in the developer center and save it in ``settings.json`` in the **project's root folder**.
This is pretty much what you are looking for 90% of the time:
```js
{
    "key" : "<your Discord bot secret key>"
}
```
You can see the full JSON structure below.
*The ``s3config`` object is not required and only extends some of the features*
```js
{
    "key" : "<your Discord bot secret key>",
    "s3config" : {
        "endpoint" : "<AWS endpoint>",
        "site" : "<publicly accessible AWS S3 site>",
        "key" : "<AWS key>",
        "pass" : "<AWS password>",
        "bucket" : "<S3 bucket>",
        "css" : "<stylesheet URL>",
        "js" : "<javascript URL>",
        "logo" : "<logo URL>"
    }
}
```
For more information, see the source code for the command ``FullInventory``.
