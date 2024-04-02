# Builder's Hammer
*Builder's Hammer* is a Minecraft mod that brings the functionality of the creative debug stick into survival Minecraft. It also aims to be more inuitive to use than the debug stick.

The mod adds a Copper Hammer (currently uncraftable) that gives the player the ability to change the value of a block state's properties. Only some blocks and their properties are changeable, however. This is to make it more balanced in survival in contrast to the debug stick and to make it more believeable as an item. [Here](https://github.com/Goby56/builders-hammer/blob/main/src/main/java/com/goby56/buildershammer/ChangeableProperties.java) are some of the [supported blocks](https://github.com/Goby56/builders-hammer/blob/main/src/main/java/com/goby56/buildershammer/ChangeableProperties.java) and their properties. Please provide suggestions on blocks to add in the future over on the [issues page](https://github.com/Goby56/builders-hammer/issues)

To use the hammer you can either left or right click while holding it. 
- Left click will change a property dependent on where you are looking.
- Sneaking + left click will change a predefined property not dependent on direction.
- Right click will save the block state as a preset for that block.
- While having a saved block state you can apply it with left click.
- Sneaking + right click will remove the preset for that block and allow you to continue modifying the block state's properties.

Currently the mod shows a small text above the hotbar to indicate what you have changed and when you save or remove a preset. It will also highlight the block in green when saving and in red when removing the preset.

![Showcase](https://github.com/Goby56/builders-hammer/assets/60710855/644d698e-11d2-4de9-82bc-4581f4e589e6)

