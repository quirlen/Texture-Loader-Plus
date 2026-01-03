# Texture Loader Plus
A Minecraft Fabric mod for **resource pack creators** that enables loading alternative texture formats (PSD, TIFF, PNM) directly in Minecraft.
[![Download on Modrinth](https://img.shields.io/modrinth/dt/texture-loader-plus?logo=modrinth&label=Modrinth&color=00AF5C)](https://modrinth.com/project/texture-loader-plus)
## Features
- **PSD Support**: Load Photoshop files directly as textures
- **TIFF Support**: Load TIFF/TIF image files
- **PNM Support**: Load PNM/PPM image files
- **Smart Caching**: Parsed images are cached for faster reloading
- **Configurable Priority**: Choose which format takes precedence
- **Per-Format Toggles**: Enable/disable individual format support
## Download
**[Download on Modrinth](https://modrinth.com/project/texture-loader-plus)**
## Requirements
- Minecraft 1.21 - 1.21.11
- Fabric Loader 0.15.0+
- Fabric API
- **Cloth Config** (required)
- Mod Menu (optional, for easy config access)
## Installation
1. Install Fabric Loader and Fabric API
2. Install Cloth Config
3. Download from [Modrinth](https://modrinth.com/project/texture-loader-plus)
4. Place `texture-loader-plus-x.x.x.jar` in your `mods` folder
5. Launch Minecraft
## Usage
Simply place your PSD, TIFF, or PNM textures in a resource pack using the same path structure as PNG textures. The mod will automatically detect and load them.
**Example:**
```
resourcepacks/MyPack/assets/minecraft/textures/block/stone.psd
```
This will override `stone.png` when the resource pack is active.
## Configuration
Access the config through Mod Menu or edit the config file directly.
- **Primary Priority**: Which format to check first (PNG, PSD, TIFF, PNM)
- **Enable PSD/TIFF/PNM Support**: Toggle individual format support
- **Enable Smart Caching**: Cache converted images for performance
- **Debug Logging**: Enable detailed log output
## Building from Source
```bash
./gradlew build
```
The compiled JAR will be in `build/libs/`.
## License
This project is licensed under the [MIT License](LICENSE).
## Credits
- Uses [TwelveMonkeys ImageIO](https://github.com/haraldk/TwelveMonkeys) for image format support
