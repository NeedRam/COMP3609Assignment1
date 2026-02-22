# Space Invaders - Game Documentation

---

## Original Game

This game is based on the classic **Space Invaders** arcade game by **Toshihiro Nishikado** (1978, Taito). The player controls a spaceship defending Earth from waves of descending alien invaders.

---

## Gameplay Description

The player controls a spaceship at the bottom of the screen, defending Earth from waves of alien invaders. Move left and right using arrow keys to dodge enemy fire and position for shots. Press **SPACE** to fire bullets upward at the descending alien formation.

Aliens move horizontally in formation and slowly descend toward the player. The game features three tiers of aliens with different appearances and point values. Higher tier aliens (top rows) shoot more frequently than lower tier aliens. The game ends when all lives are lost or aliens reach the bottom of the screen.

---

## How to Score Points

| Alien Type | Location | Points |
|------------|----------|--------|
| Tier 1 Aliens | Top rows (white/green) | **10 points** each |
| Tier 2 Aliens | Middle rows (gold/white/green/black) | **20 points** each |
| Tier 3 Aliens | Bottom rows (gold/white/green/black) | **30 points** each |

- Surviving waves increases the **level**
- Higher levels feature **faster-moving aliens** for increased challenge

---

## Controls

| Key | Action |
|-----|--------|
| ← Left Arrow | Move ship left |
| → Right Arrow | Move ship right |
| Space | Fire bullet |
| Start Button | Begin new game |
| Pause Button | Pause/resume game |
| Exit Button | Close game |

---

## Asset Sources

**Images (All user-created):**
- Player ship: `playerShip.png`
- Enemy ships: `enemyTier1-3*.png` (multiple color variants)
- Background: `spaceBackground.jpg`

**Sound Effects (All user-created):**
- Background music: `backgroundMusic.wav`
- Shoot sound: `shootSound.wav`
- Explosion sound: `explosionSound.wav`
- Player hit sounds: `playerHit1.wav`, `playerHit2.wav`, `PlayerHit3.wav`

---

*COMP 3609 Assignment 1 - Space Invaders Game*
