# 1.0.2
- Rewrite Village Shrine Generation logic to move away from buggy Mixin placement
  - Now we add to the Village's House Structure Pool instead of trying to place the Shrine ourselves, which should fix a lot of the issues with Shrines not generating properly
  - However, this does mean that we no longer are attempting to spawn a single shrine in every village, it is now possible for some villages to spawn with 0 shrines and some to spawn with multiple shrines, but overall this should lead to more consistent shrine generation across all villages

# 1.0.1
- Teleport to Owner during Journey if they get too far away
- Automatically Sit when Owner is Offline, Sleeping or Dying

# 1.0.0
- First non-beta release
- Complete Rewrite of Wayfinder logic to use minecraft's Brain and AI systems
- Lots of other fixes and adjustments

# 1.0.2-Beta
- Make some advancements visible
- Decrease amount of journeys for each adventure advancement
- Exclude biomes in #c:hidden_from_locator_selection tag from Wayfinder Locator Selection GUI

# 1.0.1-Beta
- Add Boundless Exploration Advancement
- Add Sweet Dreams Music Disc as Reward for completing Ultimate Adventurer
- Add Ambient Sound Subtitle
- Add Support for Oh The Biomes We've Gone Villages
- Update Wayfinder Heart Block and Wayfinder textures
- Add German Translations (de_de)

# 1.0.0-Beta
- Initial release
- Expect Bugs, Please report them on our Github page  