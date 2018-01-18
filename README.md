# Fraternize
Grade 12 ICS Summative project

## How to Play
- Use WASD to Move
- Space to Jump
- F3 Opens Debug
- F11 Toggles Fullscreen
- Esc Opens Settings

## System Requirements
- OpenGL 1.1, Java 8
- Dual Core 2GHz CPU
- 1GB Of System Memory
- (Recommended) Dedicated GPU with 512MB of VRAM

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
3. Marker.ID (Use Marker.Spawn for Spawn marker)