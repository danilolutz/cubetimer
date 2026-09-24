# Contributing to CubeTimer

Thanks for helping make CubeTimer more reliable, understandable, and useful for speedcubers.

## Before you start

- Search existing [issues](https://github.com/danilolutz/cubetimer/issues) before opening a duplicate.
- Use [issues](https://github.com/danilolutz/cubetimer/issues) for reproducible bugs and concrete proposals.
- Keep security vulnerabilities out of public issues. Follow [SECURITY.md](SECURITY.md).
- For large, behavior-changing, or architectural work, [open an issue](https://github.com/danilolutz/cubetimer/issues/new/choose) before implementing it.

## Development setup

CubeTimer targets Android and requires:

- Android Studio or JDK 17+
- Android SDK Platform 37
- A connected device or emulator for instrumentation tests

Run the relevant checks before opening a pull request:

```sh
./gradlew testDebugUnitTest
./gradlew lint
./gradlew assembleDebug
```

Run `./gradlew connectedDebugAndroidTest` when the change affects Android UI or platform behavior. Use `./gradlew assembleRelease` to validate a release build from source.

On Windows, replace `./gradlew` with `./gradlew.bat`.

## Project shape

The project keeps dependencies directed toward the domain:

```text
domain/   cube model, solves, timer session/state, pure statistics
data/     SharedPreferences history and Android PNG sharing
feature/  timer state holder, history, and solve sharing flows
ui/       Compose screens, cube map, formatting, and theme
```

Keep `cs/min2phase` isolated and unchanged. `TnoodleThreeByThreeScrambler` is the adapter between the vendored TNoodle/min2phase source and the application's `ScrambleSource` boundary.

## Code and pull requests

- Keep changes focused and explain the user problem being solved.
- Add or update tests when behavior changes.
- Preserve the architecture and dependency direction unless the pull request explains why a change is necessary.
- Do not commit credentials, keystores, private keys, solve history, screenshots containing personal data, or generated build artifacts.
- Update user-facing documentation when behavior or limitations change.
- Include screenshots for visible UI changes and describe migration or privacy implications when relevant.

## License

By contributing to CubeTimer, you agree that your contribution is provided under the [GNU General Public License v3.0 or later](LICENSE).
