# Fraternize
Grade 12 ICS Summative project. [Website](https://zharry.ca/projects/grade12/fraternize/)

## How to Play
- Use WASD to Move
- Space to Jump
- F3 Opens Debug
- Esc Opens Settings
- F11 Toggles Fullscreen (Affected by Issue #20)

## Recomended System Requirements
- OpenGL 3.2, Java 8
- Dual Core 2GHz CPU
- 2GB Of System Memory
- Dedicated GPU with 512MB of VRAM

## Minimum System Requirements
- OpenGL 1.1, Java 8
- 1.4GHz CPU
- 1GB Of System Memory
- GPU with 256MB of VRAM

## Server
Public Server IP:
> fraternizegame.ml
- By default the server uses port 9001 (TCP), but it can be changed.
### Server Commands
| Command  | Description                     |
|----------|---------------------------------|
| tps      | Check server's tickrate         |
| pingall  | Send a ping test to all clients |
| tp       | Teleport all players to map     |
| reload   | Dynamically reload map          |
| list     | List all player ID's and IP's   |
| spectate | Set player to spectator mode    |
| stop     | Stop the server                 |

## Map Modeling Specs
### Collidable, Rendered Objects
1. Collider.ID (Collider ID must correspond with Activator ID)
2. Floor.ID
3. Wall.ID
### Non-collidable, Rendered Objects
1. Activator.ID
2. Activator.Exit.STR (Where STR is the next Level's name)
### Non-collidable, Non-rendered Objects
1. Light.TYPE.ID (TYPE is 0 for Shadow Casting, 1 for Non-shadow Casting)
2. Boundary.ID
3. Marker.ID (Use Marker.Spawn for Spawn marker

## How to build Jar
1. In Eclipse, Export while playing dependencies in subfolder
2. Open JarSplice
3. Add Exported Jars
4. Add Dependencies from Windows, Mac OS X and Linux
5. Use Main Class 'Fraternize'
6. Create Jar
