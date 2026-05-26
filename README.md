# Theatrical Extra Lights


## Contributors

* Dumaan089
* Rushmead
* J8-Diablo

## TODO

### Fixture Configuration UI Refactor

The current fixture configuration UI still needs a complete refactor.
At the moment, the system can be unstable and some parts of the interface are buggy or difficult to use.

### Automatic Universe Switching

Improve the configuration card behavior so universes switch automatically when the next DMX address exceeds the 512 channel limit.

Example:

* If a fixture would overflow the current universe,
* The configuration should automatically continue on the next universe instead of creating invalid channel mappings.

### Simpler Configuration Workflow

The configuration card should become:

* easier to understand,
* faster to use,
* cleaner visually,
* and more intuitive for large lighting setups.

The goal is to make fixture patching closer to a real lighting console workflow while keeping it beginner-friendly.
