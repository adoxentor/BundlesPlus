# BundlesPlus

Fabric and Forge are split into modules, and we merge it afterwards

# Running
Fabric: `gradlew :fabric:runClient`<br>
Forge: `gradlew :forge:runClient`

Or use the run configs if you are using IntelliJ IDEA.

# Compiling
Run `gradlew buildMerged`, and the merged jar will be in `build/libs/`.