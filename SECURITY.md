# Security policy

CubeTimer is an offline Android application. It has not received an external security audit.

## Reporting a vulnerability

Please report suspected vulnerabilities privately through [GitHub Private Vulnerability Reporting](https://github.com/danilolutz/cubetimer/security/advisories/new).

Do not open a public issue, discussion, or pull request for an unpatched vulnerability.

Include:

- the affected CubeTimer version and build number;
- device model and Android version;
- a clear description of the impact;
- reproducible steps or a proof of concept, when safe;
- relevant logs with solve history, shared images, personal data, tokens, and secrets removed.

Reports will be reviewed as soon as practical. Public disclosure will be coordinated after a fix or mitigation is available.

## Scope

Security reports are especially valuable for:

- local solve persistence, migration, backup, and data exposure;
- `FileProvider`, PNG generation, and Android share flows;
- third-party dependencies and vendored source updates;
- APK signing, release artifacts, and distribution configuration.

For ordinary bugs and feature requests, use the [CubeTimer issue tracker](https://github.com/danilolutz/cubetimer/issues).
