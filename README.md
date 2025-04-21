# ravenAddons

ravenAddons is a mod designed for Hypixel SkyBlock, focusing on features often overlooked by other popular mods.
Additionally, features for the Hypixel Gamemode, The Pit, are being made.

# Features

You can see a list of features below, as well as a list of upcoming features and a to-do list.

<details>
<summary>Pit</summary>

## Pit

- Care Package Highlighter

![Care Package Highlighter - Mystic Sword](https://cdn.modrinth.com/data/cached_images/a9e81c7528013f61bdd4829f40ddf3e67775f4c9.png)

</details>

<details>
<summary>SkyBlock</summary>

## SkyBlock

+ Dodge List
    - Checks if players that join through party finder are on your dodge list.
    - It allows the user to supply a reason for why you have dodged the user.
    - Users can choose the duration for how long players should be dodged for by
      using `/ra dodge tempadd <player> <duration> [reason]`
    - Has the additional option to automatically kick (and the option to announce why).

![Dodge List](https://github.com/user-attachments/assets/a6c970f7-d82b-47dd-aab8-8b31555349bb)

+ DROP Alerts
    - Select a username to annoy with your rare drops.

+ DROP Titles
    - Create a title notification for your rare drops.

+ Fire Freeze Timer
    - Places a timer above an entity's head when they are frozen with a Fire Freeze Staff.
    - Additional options include announcing to party chat when a mob is frozen and a notification for when you should
      re-activate the ability of Fire Freeze Staff to freeze the mob again.

+ Lost Time Calculator
    - Calculates the time lost due to lag at the end of a Dungeons or Kuudra run.

</details>

<details>
<summary>Dungeons</summary>

## Dungeons

+ Fire Freeze Timer (Floor 3)
    - You can choose if the timer should start from five to three seconds.
    - Sound customizability for when you should freeze the professor while the default sound being `random.anvil_land`.

+ Better Device Notifications
    - Replaces Hypixel's device complete titles for your username with a custom title or subtitle that you have chosen.

+ Energy Crystal Notification
    - Shows "Place Crystal" when you have an energy crystal in your inventory.

+ Leap Announce
    - Customizable leap announce.

+ Leap Sound
    - Plays note.pling when you leap to someone.

</details>

<details>
<summary>Mining</summary>

## Mining

+ Mining Ability Notifications

+ Gemstone Powder Notifications
    - Option to choose the threshold of powder.

</details>

<details>
<summary>Party Commands</summary>

## Party Commands

+ !since
    - Announces to the party how many mobs you have spawned before spawning an Inquisitor.

</details>

<details>
<summary>Commands</summary>

## Commands

+ Refill Commands related to Dungeons
    - Choose between a lot of refill commands. You can view the full list with `/ra help`.
    - `/ra ep` - Ender Pearl Refill.
    - `/ra ij` - Inflatable Jerry Refill.
    - `/ra sb` - Superboom TNT Refill.
    - `/ra sl` - Spirit Leap Refill.
    - `/ra de` - Decoy Refill.


+ Refill Commands related to Mining
    - `/ra cs` - Cobblestone Refill (Mining Routes).
    - `/ra bo` - Bob-omb Refill.

</details>

# Upcoming

???

# To-do list

+ Change pre-existing timers to be based around ticks.

+ Create global dodge list using a repo system.

+ Expand !since to accept arguments and add `!since inq`, `!since chim` and `!since relic` while falling back
  to `!since inq` if no argument was provided.