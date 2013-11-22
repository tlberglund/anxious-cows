# Anxious Cows: An Agent Model

This is the code supporting my talk, _Anxiouw Cows in ClojureScript_.

![norwegian-red](https://f.cloud.github.com/assets/63223/1602849/3d9f9c86-539f-11e3-9f34-264e1a094acb.png)

## Running this Code

1. Have [Leiningen installed](https://github.com/technomancy/leiningen#installation)
2. Clone this repo
3. Run `lein clsjbuild once` from the root of the repo
4. Open `index.html`
4. Profit

## Branches

The presentation has four and a half phases to it, each supported by a branch (the final branch is optional).

* `phase-one`: A trivial model of static cows with random positions inside a pen.
* `phase-two`: Adds motion to the cows, plus elastic collisions with the sides of the pen. Cows cannot collide with each other.
* `phase-tree`: Electrifies the fence, so cows experience anxiety when they hit it. Anxiety decays slowly with time.
* `phase-four`: Introduces cow fusion and differentiation, such cows inherit anxiety from a sufficiently anxious and nearby neighbor.
* `phase-moo`: Adds amazingly annoying mooing noises when cows get shocked on the fence.

Other branches may exist, and are likely to contain partially implemented new features. The position of `master` is not particularly important in this repository.
