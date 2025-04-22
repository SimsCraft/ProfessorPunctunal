# Images Directory

This folder contains all the images used in the project, including character sprites, animation strips, and other graphical assets.

## Folder Structure

The images are organized into the following subdirectories:

### 1. /sprites/

Contains individual sprite images. These images represent the different poses of characters or objects in the game. They are used to create animations through code (either manually or by combining them into animation strips).

Example contents:

* `ali_front_0.png` — Idle pose of the player when facing down (frame 2).
* `ali_left_0.png` — First frame of the walking animation (when facing left).

### 2. /animations/

Contains animation strip files. These are packed images that hold multiple frames of an animation in a single image, typically used for efficient rendering in the game. The code reads from these strips to display the animation.

Example contents:

* `ali_walking_down.png` — Downward-facing walking animation strip for the player character (left, neutral, right, neutral legs).

#### How to Add or Modify Images

For individual sprites:

* Add each sprite as a separate image in the `/sprites/` folder.

* Ensure filenames are clear and descriptive (e.g., `player_idle_0.png`, `enemy_walk_1.png`).

For animation strips:

* Combine the individual sprites into a single image using a sprite editor (e.g., Aseprite, PixieEditor).
  * **Ensure there are *NO SPACES* between the bounding boxes of individual sprites and *NO MARGINS* along the borders of the image.**
  
  * Note: Some sprites in an animation (e.g., walking animations) may have varying dimensions, which can result in gaps. These gaps are acceptable, but it's important to align the sprites consistently across the strip.

* Save the resulting strip image in the `/animations/` folder.

* Ensure the strip layout is clear and well-organized, whether arranged horizontally, vertically, or in a grid.

  * Note: If using a grid layout, be mindful that ***missing cells may cause empty frames during playback.***

#### Notes

* **Image formats**: PNG is the preferred format for all sprite and animation images due to its transparency support and lossless compression.

* **Frame Order**: When creating animation strips, the frames should be ordered from left to right in the direction they will play during the animation. For example, for a walking animation: left leg forward → neutral → right leg forward → neutral.

### 3. /backgrounds/

Contains all background images used in the game. These images represent static or dynamic backdrops that provide the setting for the game’s various scenes or levels.

Example contents:

* `car_park.png` — A car park background.
* `school_hallway.png` — A school hallway background.

#### How to Add or Modify Backgrounds

* Add new background images by placing them in the `/backgrounds/` folder.

* **File Naming**: Use descriptive names that reflect the content or level the background is associated with (e.g., `level_2_background.png`, `night_forest.png`).

* **Image Format**: Use PNG or JPG depending on the level of detail and transparency needs. PNG is preferred if transparency is required.

---

## 🧑‍🎨 Editing Images

* If you need to edit a sprite, use a sprite editing tool like Aseprite, PixieEditor, or any other tool that supports transparent PNGs.
* When adding or modifying animation strips, ensure that the frames align correctly for smooth animation playback.

---

## 📚 Resources

* [Aseprite (for creating/editing sprites)](https://www.aseprite.org/)

* [PixieEditor (free online sprite editor)](https://pixieeditor.com/)

---

## 🤝 Contributors

If you're contributing to this project, please make sure to add new sprites or animation strips following the naming conventions and structure outlined above.

---

## 🧠 TL;DR

This folder holds all game image assets:

* `/sprites/`: individual frames for characters/objects

* `/animations/`: combined sprite strips (horizontal, vertical, or grid-based)

* `/backgrounds/`: static or dynamic scene backdrops

🖼 Use `.png` format for all sprites and animations — it supports transparency and maintains image quality.

📸 `.jpg` is allowed for backgrounds if transparency isn’t needed and smaller file size is preferred.

File names should be kept clear and descriptive. Use underscores to separate words. Multiple sprites using the same name should be zero-indexed (i.e., `file_0.png`, `file_1.png`, `file_2.png`).

💡 For animation strips:

* Keep frames aligned consistently.

* No margins or spacing between sprites. Gaps resulting from varying image dimensions are fine though.

* Grids *must* be full — missing cells will show as empty frames in-game.

---

*Last Updated: [2025-04-21 20:16 UTC-04:00]*
