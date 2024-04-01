# American Dream

A 2D game similar to Super Smash Bros but with the addition of guns and the American Dream

### Running the game

Running the game is as simple as running the client jar file in jars folder. The server is already running on a remote
server.

```bash
java -jar client-$VERSION_NUMBER.jar
```

replace $VERSION_NUMBER with the version number to run.

### Game keys

* 'Left' - Shoot bullets to the left
* 'Right' - Shoot bullets to the right
* 'A' - Move left
* 'D' - Move right
* 'W' - Jump
* 'S' (hold) - Drop through a platform

Sidenote: the game is also playable with a controller

### Server setup for development

Open project module named 'server' in IntelliJ.

Navigate to file 'GameServer.java' located in src directory.

Run GameServer.java if it is runnable.

If GameServer.java isn't runnable:

* Open Gradle tab (Elephant logo on the right side of the screen)
* Click 'build' located in server\Tasks\build\
* Gameserver.java should now be runnable

### Client(s) setup for development

Open project module named 'client' in IntelliJ.

Open Gradle tab (Elephant logo on the right side of the screen)

Click 'run' located in client\Tasks\other\

Click 'run' again to create an additional client for the same server

## Contributing

Rihard Rivis, Elias Markus Priinits, Rasmus Sander

## License

[MIT](https://choosealicense.com/licenses/mit/)