# CrimsonCompanyAI
A collection of A.I. implementations, created by TeamiumPremium, for a variant of Crimson Company.
For the course: Artificial Intelligence Through Machine Game Playing, Spring 2021.

Yes, we know the name "TeamiumPremium" is ~~terrible~~ great. It keeps things light.

## Structure
**TeamiumPremium**: Directory containing the AI implementations and associated code (e.g. test Oware environment runner).

**AI-Game-Playing**: Directory containing the Crimson Company environment, provided by https://github.com/mlepinski/AI-Game-Playing (*main* branch).

## AI-Game-Playing Subtree
The Crimson Company environment is handled using *git subtree* (as opposed to *submodule*). *Subtree* was chosen over *submodule* for usability when the Oware environment is updated. *Subtree* simply requires the repository to be updated. *Submodule* requires all contributors to update, making user-error likely.

*Subtree* also allows the repository to be cloned without extra commands.

**The subtree can be manually updated with**:
`git subtree pull --prefix AI-Game-Playing https://github.com/mlepinski/AI-Game-Playing.git main --squash`

*Time permitting, I hope to replace this manual update with a GitHub Action.*
