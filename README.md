# FastCrystals

## Updated to 1.21 by Infnox
* Optimizations and dealing with some packets is still pending, will be done soon.


Makes placing crystals really fast

Based on the work by [Jyguy](https://github.com/Jyguy) in [mcpvp-club/FasterCrystals](https://github.com/mcpvp-club/FasterCrystals), with some improvements

# Key advantages:

* Performance
  * Runs way more code asynchronously, most packet logic can be done off the main thread
  * Simplified logic; FasterCrystals does a lot of checks it really shouldn't, such as [here](https://github.com/mcpvp-club/FasterCrystals/blob/34cae257e354daa53520a6ba29cf1e4f70a685cf/core/src/main/java/xyz/reknown/fastercrystals/listeners/packet/CrystalPacketListener.java#L130-L167) as compared to [here](https://github.com/MCAuroraNetwork/FastCrystals/blob/01d14cedbbab3c096faf3686aca8d90e488140cb/src/main/java/club/aurorapvp/fastcrystals/listeners/packet/AnimationPacketListener.java#L25-L48), these do effectively same thing, but FasterCrystals does more logic in a much less readable way than it has to
  * Other minor things, like caching the plugin instance rather than getting it from the class and not constantly cloning the location for no reason

* Speed
  * Since FastCrystals runs almost entirely asynchronously, it doesn't need to schedule onto the main thread as often, which means it doesn't need to wait until the next game tick to perform logic
  * FastCrystals also sends the client some packets directly, without having to wait for the server to process anything, improving the speed the client can place at. This can theoretically reduce times between crystal placements up to between 50-99ms, and will likely be improved upon in future versions of this plugin

* Maintainability
  * FastCrystals doesn't use any Minecraft server internals, instead 100% leveraging only the modern Paper API, making it much more likely to retain compatibilty going into the future, needing much less matenience with future versions, and reducing complexity
  * FastCrystals makes various code readability improvements by utilizing much less nesting, more early returns, and adhering to the [Google Java Format](https://github.com/google/google-java-format)

* Some fixes, for example FasterCrystals doesn't work with bedrock, only obsidian
